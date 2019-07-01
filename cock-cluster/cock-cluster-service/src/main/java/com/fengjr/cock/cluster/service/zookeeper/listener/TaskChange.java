package com.fengjr.cock.cluster.service.zookeeper.listener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fengjr.cock.cluster.service.IPAddressUtil;
import com.fengjr.cock.cluster.service.cache.ServerTaskMapCache;
import com.fengjr.cock.cluster.service.quartz.SchedulerManager;
import com.fengjr.cock.cluster.service.zookeeper.ZKLeaderHandler;
import com.fengjr.cock.common.domain.schedule.ScheduleConfig;
import com.fengjr.cock.common.json.GsonUtil;
import com.fengjr.cock.common.zookeeper.CuratorZKClient;
import com.fengjr.cock.common.zookeeper.ZKDictionary;


public class TaskChange implements Change {

	
	private static final Logger log = LoggerFactory.getLogger(TaskChange.class);
	
	@Override
	public void change(CuratorFramework client, PathChildrenCacheEvent event) {

		ChildData data = null;
		String json = null;
		ScheduleConfig config = null;
		CuratorZKClient zkClient = null;
		String server = null;
		String ip = null;
		try {
			
			zkClient = CuratorZKClient.getCuratorZKClient();
			
			switch (event.getType()) {
			case CHILD_ADDED:
				
				log.debug("TaskChange change CHILD_ADDED");
				
				break;
			case CHILD_UPDATED:
				
				data = event.getData();
				log.debug("TaskChange change CHILD_UPDATED path=" +data.getPath());
				
				json = zkClient.getData(data.getPath());
				log.debug("TaskChange change CHILD_UPDATED json=" + json);
				if(StringUtils.isNotBlank(json)){
					
					config = GsonUtil.fromJson(ScheduleConfig.class, json);
					
					ip = IPAddressUtil.getIpAndPortString();
					
					if( null != config 
							&& StringUtils.isNotBlank(config.getLoadId()) 
							&& ip.equalsIgnoreCase(config.getLoadId())){
						
						log.debug("TaskChange change CHILD_UPDATED resetTrigger");
						
						SchedulerManager.getResetTrigger(config);
					}
					else if( null != config && StringUtils.isBlank(config.getLoadId()) ){
						
						if(ZKLeaderHandler.isLeader()){
							server = ServerTaskMapCache.getMinTaskServer();
							config.setLoadId(server);
							config.setLoadTime(Calendar.getInstance().getTime());
							config.setLastUpdateDate(Calendar.getInstance().getTime());
							zkClient.setData(data.getPath(), GsonUtil.toJsonString(config));
							ServerTaskMapCache.increment(ip);
						}
					}
				}
				
				break;
			case CHILD_REMOVED:
				
				data = event.getData();
				log.debug("TaskChange change CHILD_REMOVED path=" +data.getPath());
				
				// 是否是leader
				if(ZKLeaderHandler.isLeader()){
					
					ServerTaskMapCache.decrement(ip);
					// 是否严重不平均
					if(isNotAverage()){
						ZKLeaderHandler.doAllot(client);
					}
				}
				
				break;
			default:
				log.debug("TaskChange change defalut = "+event.getType().name());
				break;
			}
			
		} catch (Exception e) {
			
			log.error("TaskChange change e="+e.getMessage(), e);
		}
	}

	
	public boolean isNotAverage() throws Exception{
		CuratorZKClient zkClient = null;
		List<String> childList = null;
		String json = null;
		ScheduleConfig config = null;
		
		int max = 0;
		int min = 0;
		Map<String, Integer> map = new HashMap<String, Integer>();
		Integer count = 0;
		try{
			
			if(ServerTaskMapCache.isNotEmpty()){
				
				max = ServerTaskMapCache.getMaxTaskCount();
				min = ServerTaskMapCache.getMinTaskCount();
				
				if( (min+1)*2 <= max ){
					return true;
				}
				return false;
			}
			
			zkClient = CuratorZKClient.getCuratorZKClient();
			
			childList = zkClient.getChildren(ZKDictionary.INSTANCE.getTaskPath());
			
			for(String item:childList){
				
				json = zkClient.getData(ZKDictionary.INSTANCE.getTaskPath()+ "/" +item);
				
				config = GsonUtil.fromJson(ScheduleConfig.class, json);
				
				count = map.get(config.getLoadId());
				if(count == null){
					map.put(config.getLoadId(), 1);
				}
				else{
					map.put(config.getLoadId(), count + 1);
				}
			}
			
			int i = 0;
			for(Map.Entry<String, Integer> entry:map.entrySet()){
				
				if(i == 0){
					max = entry.getValue();
					min = entry.getValue();
				}
				else{
					if(entry.getValue() > max){
						max = entry.getValue();
					}
					
					if(entry.getValue() < min){
						min = entry.getValue();
					}
				}
				i ++;
			}
			
			if( (min+1)*2 <= max ){
				return true;
			}
		}
		finally{
			zkClient = null;
			childList = null;
			json = null;
			config = null;
			map = null;
		}
		return false;
	}
}
