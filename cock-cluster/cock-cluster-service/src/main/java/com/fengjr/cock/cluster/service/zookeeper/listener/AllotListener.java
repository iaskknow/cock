package com.fengjr.cock.cluster.service.zookeeper.listener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fengjr.cock.cluster.domain.ServerNode;
import com.fengjr.cock.cluster.service.IPAddressUtil;
import com.fengjr.cock.cluster.service.quartz.SchedulerManager;
import com.fengjr.cock.common.domain.schedule.AllotDomain;
import com.fengjr.cock.common.domain.schedule.ScheduleConfig;
import com.fengjr.cock.common.json.GsonUtil;
import com.fengjr.cock.common.zookeeper.CuratorZKClient;
import com.fengjr.cock.common.zookeeper.ZKDictionary;

public class AllotListener implements NodeCacheListener {

	
	protected static final Logger log = LoggerFactory.getLogger(AllotListener.class);
	
	@Override
	public void nodeChanged() throws Exception {

		CuratorZKClient client = null;
		
		String data = null;
		AllotDomain allot = null;
		List<String> tasks = null;
		ScheduleConfig config = null;
		List<ScheduleConfig> list = null;
		String IP = null;
		ServerNode node = null;
		try{
			
			client = CuratorZKClient.getCuratorZKClient();
			
			boolean isExists = client.checkExists(ZKDictionary.INSTANCE.getAllottingLockPath());
			
			if(isExists){
				
				data = client.getData(ZKDictionary.INSTANCE.getAllottingLockPath());
				
				log.debug("AllotListener nodeChanged data="+data);
				
				allot = GsonUtil.fromJson(AllotDomain.class, data);
				
				if("1".equals(allot.getStatus())){
					
					IP = IPAddressUtil.getIpAndPortString();
					data = client.getData(ZKDictionary.INSTANCE.getServerPath() +"/"+IP);
					
					log.debug("AllotListener nodeChanged serverPath data="+data);
					
					node = GsonUtil.fromJson(ServerNode.class, data);
					
					if("0".equals(node.getLoad())){
						
						list = new ArrayList<ScheduleConfig>();
						// 分配完成 开始加载
						tasks = client.getChildren(ZKDictionary.INSTANCE.getTaskPath());
						
						for(String item:tasks){
							
							data = client.getData(ZKDictionary.INSTANCE.getTaskPath() + "/" +item);
							
							config = GsonUtil.fromJson(ScheduleConfig.class, data);
							
							if(IP.equals(config.getLoadId())){
								
								list.add(config);
							}
						}
						if( list != null && !list.isEmpty()){
							
							SchedulerManager.load(list);
							
							for(ScheduleConfig e:list){
								
								e.setLoadTime(Calendar.getInstance().getTime());
								data = GsonUtil.toJsonString(e);
								client.setData(ZKDictionary.INSTANCE.getTaskPath() + "/" + e.getId(), data);
							}
						}
						
						node = new ServerNode();
						node.setLoad("1");
						client.setData(ZKDictionary.INSTANCE.getServerPath() +"/"+IP, GsonUtil.toJsonString(node));
					}
				}
			}
		}
		catch(Exception e){
			
			log.error("AllotListener e="+e.getMessage(), e);
		}
		finally{
			client = null;
			
			data = null;
			allot = null;
			tasks = null;
			config = null;
			list = null;
			IP = null;
		}
	}

}
