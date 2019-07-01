package com.fengjr.cock.common.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.nio.client.util.HttpAsyncClientUtils;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fengjr.cock.common.domain.enums.HttpMethodEnum;

public class HttpAsyncProtocol {

	protected static final Logger log = LoggerFactory.getLogger(HttpAsyncProtocol.class);
	
	private static final String defaultCharset = "UTF-8";
	private static final String defaultContentType = "application/x-www-form-urlencoded;charset="+defaultCharset;
	
	public static void httpPost(String url, int timout, String charset,  Map<String ,?> pair)throws Exception{
		
		http(url, charset, defaultContentType, HttpMethodEnum.POST, pair, null, null);
	}
	
	public static void httpGet(String url, int timout, String charset) throws Exception{
		
		http(url, charset, defaultContentType, HttpMethodEnum.GET, null, null, null);
	}
	
	public static void http(String url, String charset, String contentType, HttpMethodEnum method, 
			Map<String ,?> pair, String sbXml, Map<String,String> headers) throws Exception {
		
		if( null == url || url.trim().equals("") ) return;
		if( null == contentType || contentType.trim().equals("") ) contentType = defaultContentType;
		if( null == charset || charset.trim().equals("") ) charset = defaultCharset;
		url = url.trim();
		
		HttpAsyncClientBuilder builder = null;
		RequestConfig requestConfig = null;
		URL uri = null;
		String scheme = null;
		CloseableHttpAsyncClient client = null;
		List <NameValuePair> nvps = null;
		try{
			requestConfig = RequestConfig.custom()
		            .setSocketTimeout(6000)
		            .setConnectTimeout(20000)
		            .setMaxRedirects(0)
		            .build();
			
			ConnectionConfig connectionConfig = ConnectionConfig.custom()
					.setCharset(Charset.forName("UTF-8"))
					.setMalformedInputAction(CodingErrorAction.IGNORE)
					.setUnmappableInputAction(CodingErrorAction.IGNORE).build();
			
			builder = HttpAsyncClients.custom();
			builder.setDefaultRequestConfig(requestConfig);
			builder.setDefaultConnectionConfig(connectionConfig);
			
			uri = new URL(url);
			scheme = uri.getProtocol();
			if("https".equalsIgnoreCase(scheme)){
				ssl(builder);
			}
			
			client = builder.build();
			
			client.start();
			
			
			if(HttpMethodEnum.POST == method){
				HttpPost post = new HttpPost(url);
				post.setHeader("Cache-Control", "no-cache");
				post.setHeader("Connection", "Keep-Alive");
				post.setHeader("User-Agent", "NetFox");
				post.setHeader("Content-Type",contentType);
				if(headers != null){
					for(String key:headers.keySet()){
						post.setHeader(key, headers.get(key));
					}
				}
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
					post.setEntity(new StringEntity(sbXml ,charset));
				}
				client.execute(post, new Notify(post, client));
			}
			else if(HttpMethodEnum.GET == method){
				
				HttpGet get = new HttpGet(url);
				get.setHeader("Cache-Control", "no-cache");
				get.setHeader("Connection", "Keep-Alive");
				get.setHeader("User-Agent", "NetFox");
				get.setHeader("Content-Type",contentType);
				if(headers != null){
					for(String key:headers.keySet()){
						get.setHeader(key, headers.get(key));
					}
				}
				client.execute(get, new Notify(get, client));
			}
		}
		finally{
			
			builder = null;
			requestConfig = null;
			uri = null;
			scheme = null;
			client = null;
			nvps = null;
		}
	}
	
	public static void ssl(HttpAsyncClientBuilder builder) throws Exception{
		
		
        SSLContext sslcontext = SSLContexts.custom()
                .loadTrustMaterial(new TrustSelfSignedStrategy())
                .build();
        
        SSLIOSessionStrategy sslSessionStrategy = new SSLIOSessionStrategy(
                sslcontext,
                new String[] { "TLSv1" },
                null,
                SSLIOSessionStrategy.getDefaultHostnameVerifier());
        
        builder.setSSLStrategy(sslSessionStrategy);
	}
	
	public static class Notify implements FutureCallback<HttpResponse>{

		private HttpUriRequest request;
		private CloseableHttpAsyncClient client;
		public Notify(HttpUriRequest request, CloseableHttpAsyncClient client) {
			this.request = request;
			this.client = client;
		}

		@Override
		public void completed(HttpResponse response) {
			
			String charset = "UTF-8";
			HttpEntity rsEntity = null;
			InputStream inputStream = null;
			InputStreamReader isr = null;
			BufferedReader br = null;
			String line = null;
			String dot = "";
			StringBuilder resultString = null;
			
			try{
				if( null != response ){
					
					if(response.getStatusLine() != null){
						if(response.getStatusLine().getStatusCode() == 200){
							
							resultString = new StringBuilder();
							
							rsEntity = response.getEntity();
							if( null != rsEntity )
							{
								inputStream = rsEntity.getContent();
								isr = new InputStreamReader(inputStream ,charset);
								br = new BufferedReader(isr);  
								while( (line = br.readLine()) != null )
								{  
									resultString.append(dot + line);
									if( true ) dot = "\n";
								}
								
								log.debug(request.getRequestLine() + "->" + 
								response.getStatusLine()+" result="+
								resultString.toString());
							}
						}
						else{
							// 非正常返回
							log.debug("HttpAsyncProtocol responseCode = "+response.getStatusLine().getStatusCode());
						}
						log.debug("HttpAsyncProtocol "+request.getRequestLine() + "->" + response.getStatusLine());
					}
				}
			}catch(Exception e){
				log.error("HttpAsyncProtocol completed="+request.getRequestLine()+" ex="+e.getMessage(), e);
			}
			finally{
				if( null != inputStream ) {
					try {
						inputStream.close();
					} catch (IOException e) {
					}
				}
				inputStream = null;
				
				if( null != isr ){
					try {
						isr.close();
					} catch (IOException e) {
					}
				}
				isr = null;
				
				if( null != br ) {
					try {
						br.close();
					} catch (IOException e) {
					}
				}
				br = null;
				
				charset = null;
				rsEntity = null;
				line = null;
				dot = null;
				resultString = null;
				
				HttpAsyncClientUtils.closeQuietly(client);
			}
		}

		@Override
		public void failed(Exception ex) {
			HttpAsyncClientUtils.closeQuietly(client);
			log.debug("HttpAsyncProtocol failed() "+request.getRequestLine() + "->" + ex);
		}

		@Override
		public void cancelled() {
			HttpAsyncClientUtils.closeQuietly(client);
			log.debug("HttpAsyncProtocol cancelled() "+request.getRequestLine() + " cancelled");
		}
		
	}
	
	
}
