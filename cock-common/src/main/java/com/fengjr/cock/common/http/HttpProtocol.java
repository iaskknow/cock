package com.fengjr.cock.common.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.CodingErrorAction;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

public class HttpProtocol {

	private static final int connTimeout = 1000*6;
	private static final int soTimeout = 1000*50;
	private static final String defaultCharset = "UTF-8";
	
	private static final String defaultContentType = "application/x-www-form-urlencoded;charset="+defaultCharset;
	
	
	public static String http(String url ,String text) throws Exception
	{
		return http(url,defaultCharset ,defaultContentType ,null ,text ,false ,null);
	}
	
	public static String http(String url ,Map<String ,?> pair) throws Exception
	{
		return http(url ,defaultCharset ,defaultContentType ,pair ,null ,false ,null);
	}
	
	public static String http(String url ,String text, String contentType)throws Exception{
		return http(url ,defaultCharset ,contentType ,null ,text ,false ,null);
	}
	
	public static String http(String url ,String text, String contentType, Map<String, String> headers)throws Exception{
		return http(url ,defaultCharset ,contentType ,null ,text ,false ,headers);
	}
	
	public static String http(String url ,String charset ,String contentType ,Map<String ,?> pair ,String sbXml ,boolean blank,Map<String,String> headers) throws Exception
	{
		if( null == url || url.trim().equals("") ) return null;
		if( null == contentType || contentType.trim().equals("") ) contentType = defaultContentType;
		if( null == charset || charset.trim().equals("") ) charset = defaultCharset;
		url = url.trim();
		
		StringBuilder resultString = new StringBuilder();
		
		HttpClientBuilder builder = null;
		ConnectionConfig connectionConfig = null;
		RequestConfig requestConfig = null;
		CloseableHttpClient client = null;
		CloseableHttpResponse response = null;
		HttpPost post = null;
		HttpEntity rsEntity = null;
		List <NameValuePair> nvps = null;
		
		InputStream inputStream = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		String line = null;
		String dot = "";
		
		try{
			builder = HttpClients.custom();
			
			ssl(builder ,url);
			builder.setDefaultCookieStore(new BasicCookieStore());
	        connectionConfig = ConnectionConfig.custom()
	            .setMalformedInputAction(CodingErrorAction.IGNORE)
	            .setUnmappableInputAction(CodingErrorAction.IGNORE)
	            .setCharset(Consts.UTF_8)
	            .build();
	        builder.setDefaultConnectionConfig(connectionConfig);
	        
	        client = builder.build();
	        
			post = new HttpPost(url);
			post.setHeader("Cache-Control", "no-cache");
			post.setHeader("Connection", "Keep-Alive");
			post.setHeader("User-Agent", "NetFox");
			post.setHeader("Content-Type",contentType);
			if(headers != null){
				for(String key:headers.keySet()){
					post.setHeader(key, headers.get(key));
				}
			}
			requestConfig = RequestConfig.custom()
	                .setSocketTimeout(soTimeout)
	                .setConnectTimeout(connTimeout)
	                .setMaxRedirects(0)
	                .build();
			
			post.setConfig(requestConfig);
			
			if( null != pair && !pair.isEmpty() )
			{
				nvps = new ArrayList <NameValuePair>();
				for(Map.Entry<String, ?> entry : pair.entrySet())
				{
					nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()+""));
				}
				post.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
			}
			else if( null != sbXml )
			{
				post.setEntity(new StringEntity(sbXml.toString() ,charset));
			}
			
			response = client.execute(post);
			if( null != response )
			{
				if(response.getStatusLine() != null){
					if(response.getStatusLine().getStatusCode() == 200){
						rsEntity = response.getEntity();
						if( null != rsEntity )
						{
							inputStream = rsEntity.getContent();
							isr = new InputStreamReader(inputStream ,charset);
							br = new BufferedReader(isr);  
							while( (line = br.readLine()) != null )
							{  
								resultString.append(dot + line);
								if( blank ) dot = "\n";
							}
						}
					}
					else{
						throw new org.apache.http.client.HttpResponseException(response.getStatusLine().getStatusCode(),
								"Client receive code="+response.getStatusLine().getStatusCode()+" respone");
					}
				}
			}
			
		}
		catch(Exception e)
		{
			throw e;
		}
		finally
		{
			if( null != inputStream ) inputStream.close();
			inputStream = null;
			
			if( null != isr ) isr.close();
			isr = null;
			
			if( null != br ) br.close();
			br = null;

			if( null != response ) response.close();
			response = null;
			
			if( null != client ) client.close();
			client = null;
			
			pair = null;
			builder = null;
			connectionConfig = null;
			requestConfig = null;
			post = null;
			rsEntity = null;
			nvps = null;
			dot = null;
		}
		
		return resultString.toString();
	}

	private static void ssl(HttpClientBuilder builder ,String url) throws Exception
	{
		URL urlObj = null;
		SSLContext sslcontext = null;
		javax.net.ssl.TrustManager[] trustAllCerts = null;
		X509TrustManager tm = null;
		SSLConnectionSocketFactory sslsf = null;
		
		try{
			urlObj = new URL(url);
			String scheme = urlObj.getProtocol();
			
			if( scheme.equalsIgnoreCase("https") )
			{
				sslcontext = SSLContexts.createDefault();
		        trustAllCerts = new javax.net.ssl.TrustManager[1];
		        tm = getX509TrustManager();
		        trustAllCerts[0] = tm;
		        sslcontext.init(null, trustAllCerts, null);
		        
		        sslsf = new SSLConnectionSocketFactory(sslcontext ,SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		        builder.setSSLSocketFactory(sslsf);
			}
		}
		finally
		{
			urlObj = null;
			sslcontext = null;
			trustAllCerts = null;
			tm = null;
			sslsf = null;
		}
	}
	

	public static X509TrustManager getX509TrustManager()
	{
		return new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] arg0, String arg1)
					throws CertificateException {
			}
			public void checkServerTrusted(X509Certificate[] arg0, String arg1)
					throws CertificateException {
			}
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
	}
	
}
