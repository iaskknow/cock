package com.fengjr.cock.cluster.service.zookeeper;

import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.framework.state.ConnectionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fengjr.cock.cluster.service.zookeeper.listener.AllotListener;
import com.fengjr.cock.cluster.service.zookeeper.listener.ChildrenListener;
import com.fengjr.cock.cluster.service.zookeeper.listener.ServerChange;
import com.fengjr.cock.cluster.service.zookeeper.listener.TaskChange;
import com.fengjr.cock.common.zookeeper.ConnectHandler;
import com.fengjr.cock.common.zookeeper.CuratorZKClient;
import com.fengjr.cock.common.zookeeper.ZKDictionary;


public class ZKConnectedHandler implements ConnectHandler {
	
	
	private static final Logger log = LoggerFactory.getLogger(ZKConnectedHandler.class);
	
	private PathChildrenCache taskCache;
	private PathChildrenCache serverCache;
	private NodeCache nodeCache;

	public void connected(CuratorZKClient client, ConnectionState state){
		
		
		if(state == ConnectionState.CONNECTED || state == ConnectionState.RECONNECTED){
			
			// 创建目录
			ZKDictionary.INSTANCE.createCockPath(client);
			
			// 注册
			try {
				ZKRegister.register(client);
			} catch (RuntimeException e) {
				log.error("ZKConnectedHandler ZKRegister.register e="+e.getMessage(), e);
			} 
			
			// 添加监听
			try{
				taskCache = new PathChildrenCache(client.getClient(), 
						ZKDictionary.INSTANCE.getTaskPath(), false);
				taskCache.start(StartMode.NORMAL);
				taskCache.getListenable().addListener(new ChildrenListener(new TaskChange()));
				
				
				serverCache = new PathChildrenCache(client.getClient(), 
						ZKDictionary.INSTANCE.getServerPath(), false);
				serverCache.start(StartMode.NORMAL);
				serverCache.getListenable().addListener(new ChildrenListener(new ServerChange()));
				
				
				nodeCache = new NodeCache(client.getClient(), 
						ZKDictionary.INSTANCE.getAllottingLockPath(), false);
				nodeCache.start();
				nodeCache.getListenable().addListener(new AllotListener());
				
			}catch(Exception e){
				log.error("ZKConnectedHandler addListener e="+e.getMessage(), e);
			}
			
			// leader Select
			ZKLeaderSelector.INSTANCE.getLeader(client, new ZKLeaderHandler());
			
		}
	}
	
}
