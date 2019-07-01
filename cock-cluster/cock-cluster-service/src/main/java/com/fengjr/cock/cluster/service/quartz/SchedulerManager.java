package com.fengjr.cock.cluster.service.quartz;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fengjr.cock.common.domain.dubbo.DubboConfig;
import com.fengjr.cock.common.domain.enums.ProtocolTypeEnum;
import com.fengjr.cock.common.domain.enums.ScheduleJobStatusEnum;
import com.fengjr.cock.common.domain.enums.ScheduleTypeEnum;
import com.fengjr.cock.common.domain.http.HttpConfig;
import com.fengjr.cock.common.domain.schedule.ScheduleConfig;
import com.fengjr.cock.common.dubbo.DubboProtocol;
import com.fengjr.cock.common.json.GsonUtil;
import com.fengjr.cock.common.zookeeper.CuratorZKClient;
import com.fengjr.cock.common.zookeeper.ZKDictionary;


public class SchedulerManager {
	
	private static final Logger log = LoggerFactory.getLogger(SchedulerManager.class);

	private static final Map<String, ScheduleConfig> scheduleConfigMap = new ConcurrentHashMap<String, ScheduleConfig>();
	
	private static final Class<SchedulerExecute> EXECUTOR = SchedulerExecute.class;
	
	static Scheduler scheduler = null;
	static{
		
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
		} catch (SchedulerException e) {
			log.error("getDefaultScheduler ex="+e.getMessage(),e);
		}
	}
	
	public static Map<String, ScheduleConfig> getMap(){
		return scheduleConfigMap;
	}
	
	public static Map<String, String> getRunningTask() throws Exception{
		
		
		Map<String, String> map = new HashMap<String, String>();
		String[] groups = null;
		String[] triggers = null;
		try{
			groups = scheduler.getTriggerGroupNames();
			triggers = null;
			for(String group:groups){
				triggers = scheduler.getTriggerNames(group);
				for(String e:triggers){
					map.put(group + "_" + e, String.valueOf(scheduler.getTriggerState(e, group)));
				}
			}
		}
		finally{
			groups = null;
			triggers = null;
		}
		return map;
	}
	
	
	public static synchronized void load(List<ScheduleConfig> list) throws Exception{
		
		clear();
		scheduleConfigMap.clear();
		for(ScheduleConfig config: list){
			getAddTrigger(config);
		}
		start();
	}
	
	public static void clear() throws Exception{
		
		String[] groups = null;
		String[] triggers = null;
		try{
			groups = scheduler.getTriggerGroupNames();
			
			log.debug("group ="+groups.length);
			
			for(String group:groups){
				
				triggers = scheduler.getTriggerNames(group);
				for(String e:triggers){
					boolean del = scheduler.deleteJob(e, group);
					log.debug("jobName="+e+": group="+group+" del="+del);
				}
			}
		}
		finally{
			groups = null;
			triggers = null;
		}
	}
	
	
	public static void start(){
		
		try {
			scheduler.start();
		} catch (SchedulerException e) {
			log.error("start ex="+e.getMessage(),e);
		}
	}
	
	public static synchronized void getAddTrigger(ScheduleConfig config) throws Exception
	{
		Trigger trigger = null;
		if( null != config )
		{
			
			trigger = scheduler.getTrigger(config.getJobName(), config.getGroupName());
			
			if(trigger == null){
				
				if( ScheduleTypeEnum.CRON.name().equals(config.getScheduleType()) )
				{
					addCornTrigger(config);
				}
				else if( ScheduleTypeEnum.SIMPLE.name().equals(config.getScheduleType()) )
				{
					addSimpleTrigger(config);
				}
				
				if(ScheduleJobStatusEnum.STATE_PAUSED.name().equals(config.getJobStatus())){
					scheduler.pauseTrigger(config.getJobName(), config.getGroupName());
				}
			}
		}
	}
	
	private static void addCornTrigger(ScheduleConfig config) throws Exception{
		
		Trigger trigger = null;
		JobDetail jobDetail = null;
		JobDataMap jobDataMap = null;
		try{
			jobDetail = new JobDetail(config.getJobName(), config.getGroupName(), EXECUTOR);
			
			trigger = new CronTrigger(config.getJobName(), config.getGroupName(), 
					config.getJobName(), config.getGroupName(), 
					config.getStartTime(), config.getStopTime(), config.getJobCron());
			trigger.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
			
			jobDataMap = trigger.getJobDataMap();
			jobDataMap.put("CONFIG", config);
			
			scheduler.scheduleJob(jobDetail ,trigger);
			
			scheduleConfigMap.put(config.getJobName() + config.getGroupName(), config);
		}
		finally{
			trigger = null;
			jobDetail = null;
			jobDataMap = null;
		}
		
	}
	
	private static void addSimpleTrigger(ScheduleConfig config) throws Exception {
		
		Trigger trigger = null;
		JobDetail jobDetail = null;
		JobDataMap jobDataMap = null;
		try{
			jobDetail = new JobDetail(config.getJobName(), config.getGroupName(), EXECUTOR);
			
			trigger = new SimpleTrigger(config.getJobName(), config.getGroupName(), 
					config.getStartTime(), config.getStopTime(), 
					config.getJobCount(), config.getJobInterval());
			
			jobDataMap = trigger.getJobDataMap();
			jobDataMap.put("CONFIG", config);
			
			scheduler.scheduleJob(jobDetail ,trigger);
			
			scheduleConfigMap.put(config.getJobName() + config.getGroupName(), config);
		}
		finally{
			trigger = null;
			jobDetail = null;
			jobDataMap = null;
		}
	}
	
	
	public static synchronized void getDeleteTrigger(String jobName, String groupName) throws Exception {
		
		CuratorZKClient client = CuratorZKClient.getCuratorZKClient();
		
		if( null != client ){
			
			ScheduleConfig config = scheduleConfigMap.get(jobName + groupName);
			
			if( null != config){
				
				client.delete(ZKDictionary.INSTANCE.getTaskPath()+"/"+config.getId());
				
				// 如果是dubbo client 需要释放链接
				releaseDubboClient(config.getJobName(), config.getGroupName());
				
				boolean del = scheduler.deleteJob(jobName, groupName);
				
				if(del){
					scheduleConfigMap.remove(jobName + groupName);
				}
			}
		}
	}
	
	public static void pause(String jobName, String groupName) throws Exception{
		
		ScheduleConfig config = null;
		CuratorZKClient client = CuratorZKClient.getCuratorZKClient();
		try {
			
			config = scheduleConfigMap.get(jobName + groupName);
			
			if( null != config ){
				config.setJobStatus(ScheduleJobStatusEnum.STATE_PAUSED.name());
				
				client.setData(ZKDictionary.INSTANCE.getTaskPath()+"/"+config.getId(), GsonUtil.toJsonString(config));
				
				scheduler.pauseTrigger(jobName, groupName);
			}
			else{
				throw new RuntimeException("任务不存在");
			}
			
		}
		finally{
			config = null;
		}
	}
	
	public static void resume(String jobName, String groupName) throws Exception{
		
		ScheduleConfig config = null;
		CuratorZKClient client = CuratorZKClient.getCuratorZKClient();
		try {
			
			config = scheduleConfigMap.get(jobName + groupName);
			
			if( null != config ){
				config.setJobStatus(ScheduleJobStatusEnum.STATE_NORMAL.name());
				client.setData(ZKDictionary.INSTANCE.getTaskPath()+"/"+config.getId(), GsonUtil.toJsonString(config));
				scheduler.resumeTrigger(jobName, groupName);
			}
			else{
				throw new RuntimeException("任务不存在");
			}
		} 
		finally{
			config = null;
		}
	}
	
	
	public static void getResetTrigger(ScheduleConfig config) throws Exception {
		
		Trigger trigger = null;
		try{
			// 如果配置数据有变化 重新加载
			if(isChanged(config)){
				
				log.debug("getResetTrigger isChanged");
				
				trigger = scheduler.getTrigger(config.getJobName(), config.getGroupName());
				
				log.debug("getResetTrigger trigger = "+trigger);
				
				if( null != trigger){
					
					releaseDubboClient(config.getJobName(), config.getGroupName());
					
					if(trigger instanceof SimpleTrigger){
						
						resetSimpleTrigger(trigger, config.getGroupName(), config);
						
						scheduleConfigMap.put(config.getJobName() + config.getGroupName(), config);
					}
					else if(trigger instanceof CronTrigger){
						
						resetCronTrigger(trigger, config.getGroupName(), config);
						
						scheduleConfigMap.put(config.getJobName() + config.getGroupName(), config);
					}
				}
				else{
					getAddTrigger(config);
				}
			}
		}
		finally{
			trigger = null;
		}
	}
	
	
	public static void resetSimpleTrigger(Trigger trigger, String groupName, ScheduleConfig config) throws Exception{
		
		JobDataMap jobDataMap = null;
		SimpleTrigger simpleTrigger = null;
		try{
			simpleTrigger = (SimpleTrigger)trigger;
			simpleTrigger.setStartTime(config.getStartTime());
			simpleTrigger.setEndTime(config.getStopTime());
			simpleTrigger.setRepeatCount(config.getJobCount());
			simpleTrigger.setRepeatInterval(config.getJobInterval());
			
			jobDataMap = new JobDataMap();
			jobDataMap.put("CONFIG", config);
			
			simpleTrigger.setJobDataMap(jobDataMap);
			
			scheduler.rescheduleJob(config.getJobName(), groupName, simpleTrigger);
		}
		finally{
			jobDataMap = null;
			simpleTrigger = null;
		}
	}
	
	
	public static void resetCronTrigger(Trigger trigger, String groupName, ScheduleConfig config) throws Exception{
		
		JobDataMap jobDataMap = null;
		CronTrigger cronTrigger = null;
		try{
			cronTrigger = (CronTrigger)trigger;
			cronTrigger.setStartTime(config.getStartTime());
			cronTrigger.setEndTime(config.getStopTime());
			cronTrigger.setCronExpression(config.getJobCron());
			
			jobDataMap = new JobDataMap();
			jobDataMap.put("CONFIG", config);
			
			cronTrigger.setJobDataMap(jobDataMap);
			
			scheduler.rescheduleJob(config.getJobName(), groupName, cronTrigger);
		}
		finally{
			jobDataMap = null;
			cronTrigger = null;
		}
	}
	
	private static void releaseDubboClient( String jobName, String groupName ) throws Exception{
		
		ScheduleConfig config = null;
		try{
			config = scheduleConfigMap.get(jobName + groupName);
			
			if( null != config ){
				if(ProtocolTypeEnum.DUBBO.name().equals(config.getProtocolType())){
					
					if( null != config.getDubboConfig() ){
						DubboProtocol.destroy(config.getDubboConfig().getInterfaceAddress() + 
								config.getDubboConfig().getInterfaceName());
					}
				}
			}
		}
		finally{
			config = null;
		}
	}
	
	
	public static boolean isChanged( ScheduleConfig config ) {
		
		ScheduleConfig oldConfig = null;
		DubboConfig dubboConfig = null;
		HttpConfig httpConfig = null;
		String json = null;
		SimpleDateFormat format = null;
		try{
			
			oldConfig = scheduleConfigMap.get( config.getJobName() + config.getGroupName() );
			
			if(oldConfig == null){
				
				return true;
			}
			else{
				if(oldConfig.getProtocolType().equals(config.getProtocolType())){
					
					if(oldConfig.getJobName().equals(config.getJobName()) 
							&& oldConfig.getGroupName().equals(config.getGroupName())
							&& oldConfig.getJobCount() == config.getJobCount()
							&& oldConfig.getJobInterval() == config.getJobInterval()
							&& oldConfig.getJobCron().equals(config.getJobCron())){
						format = new SimpleDateFormat("yyyy-MM-dd");
						
						if(format.format(oldConfig.getStartTime()).equals(format.format(config.getStartTime())) && 
								(
										(oldConfig.getStopTime() == null && config.getStopTime() == null) || 
										(
												oldConfig.getStopTime() != null && config.getStopTime() != null && 
												format.format(oldConfig.getStopTime()).equals(config.getStopTime())
										)
								)
						  )
						{
							
							if(ProtocolTypeEnum.DUBBO.name().equals(config.getProtocolType())){
								dubboConfig = config.getDubboConfig();
								
								json = GsonUtil.toJsonString(oldConfig.getDubboConfig());
								
								if(json.equals(GsonUtil.toJsonString(dubboConfig))){
									return false;
								}
								
							}
							else if(ProtocolTypeEnum.HTTP.name().equals(config.getProtocolType())){
								
								httpConfig = config.getHttpConfig();
								
								json = GsonUtil.toJsonString(oldConfig.getHttpConfig());
								
								if(json.equals(GsonUtil.toJsonString(httpConfig))){
									return false;
								}
							}
						}
						else{
							
							return true;
						}
					}
					else{
						return true;
					}
				}
				else{
					
					return true;
				}
			}
		}
		finally{
			oldConfig = null;
			dubboConfig = null;
			httpConfig = null;
			json = null;
		}
		return false;
	}
	
}
