package com.fengjr.cock.cluster.service.zookeeper;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fengjr.cock.cluster.domain.ServerNode;
import com.fengjr.cock.cluster.service.IPAddressUtil;
import com.fengjr.cock.cluster.service.cache.ServerTaskMapCache;
import com.fengjr.cock.common.domain.schedule.AllotDomain;
import com.fengjr.cock.common.domain.schedule.ScheduleConfig;
import com.fengjr.cock.common.json.GsonUtil;
import com.fengjr.cock.common.zookeeper.CuratorZKClient;
import com.fengjr.cock.common.zookeeper.ZKDictionary;

public class ZKLeaderHandler {

	private static final Logger log = LoggerFactory.getLogger(ZKLeaderHandler.class);
	
	private static boolean isLeader = false;
	
	private static AtomicInteger count = new AtomicInteger(0);
	
	public void leaderNotify(CuratorFramework client){
		
		isLeader = true;
		
		// 分配task
		try {
			doAllot(client);
		} catch (Exception e) {
			log.error("ZKLeaderHandler doAllot e="+e.getMessage(), e);
		}
	}
	
	public static boolean isLeader(){
		
		return isLeader;
	}
	
	
	public static void doAllot(CuratorFramework client) throws Exception{
		
		log.debug("ZKLeaderHandler doAllot ="+count.incrementAndGet());
		
		CuratorZKClient curatorClient = null;
		String allotLockPath = null;
		AllotDomain domain = null;
		String taskPath = null;
		String serverPath = null;
		List<String> taskList = null;
		List<String> serverList = null;
		Map<String,Integer> map = null;
		String data = null;
		ServerNode node = null;
		ScheduleConfig config = null;
		String server = null;
		
		try{
			curatorClient = CuratorZKClient.getCuratorZKClient();
			allotLockPath = ZKDictionary.INSTANCE.getAllottingLockPath();
			boolean isExists = curatorClient.checkExists(allotLockPath);
			
			if(!isExists){
				
				domain = new AllotDomain();
				domain.setLeader(IPAddressUtil.getIpAndPortString());
				domain.setStatus("0");
				curatorClient.createEphemeral(allotLockPath, GsonUtil.toJsonString(domain));
			}
			else{
				domain = new AllotDomain();
				domain.setLeader(IPAddressUtil.getIpAndPortString());
				domain.setStatus("0");
				curatorClient.setData(allotLockPath, GsonUtil.toJsonString(domain));
			}
			
			taskPath = ZKDictionary.INSTANCE.getTaskPath();
			serverPath = ZKDictionary.INSTANCE.getServerPath();
			
			taskList = getChildren(client, taskPath);
			serverList = getChildren(client, serverPath);
			
			int taskItemNum = taskList.size();
			int serverNum = serverList.size();
			
			int numOfSingle = taskItemNum / serverNum;
			int otherNum = taskItemNum % serverNum;
			
			map = new HashMap<String,Integer>();
			ServerTaskMapCache.clear();
			node = new ServerNode();
			node.setLoad("0");
			
			for (int i = 0; i < serverNum; i++) {
				
				server = serverList.get(i);
				if (i < otherNum) {
					ServerTaskMapCache.put(server, numOfSingle + 1);
					map.put(server, numOfSingle + 1);
				} else {
					ServerTaskMapCache.put(server, numOfSingle);
					map.put(server, numOfSingle);
				}
				
				setData(client, serverPath + "/" +server, GsonUtil.toJsonString(node));
			}
			
			
			
			for(Entry<String, Integer> entry:map.entrySet()){
				
				for(int i = 0 ; i< entry.getValue() ; i++){
					
					data = getData(client, taskPath + "/" + taskList.get(i));
					
					config = GsonUtil.fromJson(ScheduleConfig.class, data);
					
					config.setLoadId(entry.getKey());
					
					config.setLoadTime(Calendar.getInstance().getTime());
					
					config.setLastUpdateDate(Calendar.getInstance().getTime());
					
					setData(client, taskPath + "/" + taskList.get(i), GsonUtil.toJsonString(config));
				}
			}
			
			data = getData(client, allotLockPath);
			domain = GsonUtil.fromJson(AllotDomain.class, data);
			domain.setStatus("1");
			setData(client, allotLockPath, GsonUtil.toJsonString(domain));
		}
		finally{
			curatorClient = null;
			allotLockPath = null;
			domain = null;
			taskPath = null;
			serverPath = null;
			taskList = null;
			serverList = null;
			map = null;
			data = null;
			
			config = null;
		}
		
	}
	
	public static List<String> getChildren(CuratorFramework client, String path) {
        try {
            return client.getChildren().forPath(path);
        } catch (NoNodeException e) {
            return null;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
	
	
	public static String getData(CuratorFramework client, String path) throws Exception{
    	
    	byte[] bytes = null;
    	String result = null;
    	try{
    		bytes = client.getData().forPath(path);
    		
    		result = new String(bytes, "UTF-8");
    	}
    	finally{
    		bytes = null;
    	}
    	return result;
    }
    
    
    public static int setData(CuratorFramework client, String path, String data) throws Exception {
    	
    	int version = 0;
    	Stat stat = new Stat();
    	try{
    		client.getData().storingStatIn(stat).forPath(path);
    		version = client.setData()
    				.withVersion(stat.getVersion())
    				.forPath(path, data.getBytes()).getVersion();
    	}
    	finally{
    		stat = null;
    	}
    	
    	return version;
    }
	
	public static int[] assignTaskNumber(int serverNum,int taskItemNum){
		int[] taskNums = new int[serverNum];
		int numOfSingle = taskItemNum / serverNum;
		int otherNum = taskItemNum % serverNum;

		for (int i = 0; i < taskNums.length; i++) {
			if (i < otherNum) {
				taskNums[i] = numOfSingle + 1;
			} else {
				taskNums[i] = numOfSingle;
			}
		}
		return taskNums;
	}
}
