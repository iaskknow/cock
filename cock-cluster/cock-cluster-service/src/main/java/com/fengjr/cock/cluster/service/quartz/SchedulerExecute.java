package com.fengjr.cock.cluster.service.quartz;

import java.util.Map;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fengjr.cock.common.domain.dubbo.DubboConfig;
import com.fengjr.cock.common.domain.enums.HttpMethodEnum;
import com.fengjr.cock.common.domain.enums.ProtocolTypeEnum;
import com.fengjr.cock.common.domain.http.HttpConfig;
import com.fengjr.cock.common.domain.schedule.ScheduleConfig;
import com.fengjr.cock.common.dubbo.DubboProtocol;
import com.fengjr.cock.common.http.HttpAsyncProtocol;
import com.fengjr.cock.common.json.GsonUtil;
import com.google.gson.reflect.TypeToken;



public class SchedulerExecute implements Job {

	
	private static final Logger logger = LoggerFactory.getLogger(SchedulerExecute.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException 
	{
		logger.info(getClass().getName() + " - execute()");
		
		JobDataMap jobDataMap = null;
		ScheduleConfig config = null;
		try{
			jobDataMap = context.getTrigger().getJobDataMap();
			config = (ScheduleConfig)jobDataMap.get("CONFIG");
			
			if( ProtocolTypeEnum.DUBBO.name().equals(config.getProtocolType()) ){
				
				invokeDubbo(config);
			}
			else if( ProtocolTypeEnum.HTTP.name().equals(config.getProtocolType()) ){
				
				invokeHttp(config);
			}
		}
		catch(Exception e) {
			logger.error("SchedulerExecute execute e="+e.getMessage(), e);
		}
		finally {
			jobDataMap = null;
			config = null;
		}
	}
	
	
	private void invokeDubbo(ScheduleConfig config){
		String params = null;
		StringBuilder targetInfo = null;
		String address = null;
		String interfaze = null;
		String method = null;
		String version = null;
		String group = null;
		
		DubboConfig dubboConfig = null;
		try{
			
			dubboConfig = config.getDubboConfig();
			
			address = dubboConfig.getInterfaceAddress();
			interfaze = dubboConfig.getInterfaceName();
			method = dubboConfig.getInterfaceMethod();
			params = dubboConfig.getInterfaceParams();
			version = dubboConfig.getInterfaceVersion();
			group = dubboConfig.getInterfaceGroup();
			
			targetInfo = new StringBuilder();
			targetInfo.append(" invoke  address = ").append(address).
			append(" Interface = ").append(interfaze).
			append(" method = ").append(method);
			logger.info(targetInfo.toString());
			
			String result = DubboProtocol.get(address, interfaze, dubboConfig.getTimeOut(), 
					group,version, null, method, params);
			
			logger.info("调用成功 result="+result);
		}
		catch(Exception e){
			logger.error("SchedulerExecute invokeDubbo e="+e.getMessage(), e);
		}
		finally{
			params = null;
			targetInfo = null;
			address = null;
			interfaze = null;
			method = null;
			version = null;
			group = null;
			dubboConfig = null;
		}
	}
	
	
	private void invokeHttp(ScheduleConfig config){
		
		Map<String,String> requestParams = null;
		String httpMethod = null;
		String httpUrl = null;
		String contentType = null;
		String httpParams = null;
		String httpTimeout = null;
		HttpConfig httpConfig = null;
		try{
			
			httpConfig = config.getHttpConfig();
			
			httpMethod = httpConfig.getHttpMethod();
			httpUrl = httpConfig.getHttpUrl();
//			contentType = httpConfig.getHttpContentType();
			httpParams = httpConfig.getHttpParams();
			httpTimeout = httpConfig.getTimeOut();
			
			if( HttpMethodEnum.POST.name().equalsIgnoreCase(httpMethod) ){
				
				requestParams = GsonUtil.getDomain(httpParams, new TypeToken<Map<String,String>>(){});
				
				HttpAsyncProtocol.httpPost(httpUrl, Integer.valueOf(httpTimeout), "UTF-8", requestParams);
			}
			
			else if( HttpMethodEnum.GET.name().equalsIgnoreCase(httpMethod) ){
				
				HttpAsyncProtocol.httpGet(httpUrl, Integer.valueOf(httpTimeout), "UTF-8");
			}
		}
		catch(Exception e){
			logger.error("SchedulerExecute invokeHttp e="+e.getMessage(), e);
		}
		finally{
			requestParams = null;
			httpMethod = null;
			httpUrl = null;
			contentType = null;
			httpParams = null;
			httpTimeout = null;
			httpConfig = null;
		}
	}

}
