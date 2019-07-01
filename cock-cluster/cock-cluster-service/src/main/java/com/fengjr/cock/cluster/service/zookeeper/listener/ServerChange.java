package com.fengjr.cock.cluster.service.zookeeper.listener;

import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fengjr.cock.cluster.service.IPAddressUtil;
import com.fengjr.cock.cluster.service.zookeeper.ZKLeaderHandler;
import com.fengjr.cock.cluster.service.zookeeper.ZKRegister;
import com.fengjr.cock.common.zookeeper.CuratorZKClient;


public class ServerChange implements Change {

	
	private static final Logger log = LoggerFactory.getLogger(ServerChange.class);
	
	@Override
	public void change(CuratorFramework client, PathChildrenCacheEvent event) {

		String ip = null;
		String path = null;
		try {
			switch (event.getType()) {
			case CHILD_ADDED:
				log.debug("ServerChange change = "+event.getType().name());
				if(ZKLeaderHandler.isLeader()){
					
					ZKLeaderHandler.doAllot(client);
				}
				break;
			case CHILD_REMOVED:
				path = event.getData().getPath();
				ip = IPAddressUtil.getIpAndPortString();
				log.debug("ServerChange change = "+event.getType().name()+" path="+path);
				
				if(StringUtils.isNotBlank(path) && path.contains(ip) && client.getZookeeperClient().isConnected()){
					log.debug("ServerChange change ZKRegister.register= "+event.getType().name());
					ZKRegister.register(CuratorZKClient.getCuratorZKClient());
				}
				else{
					
					if(ZKLeaderHandler.isLeader()){
						
						ZKLeaderHandler.doAllot(client);
					}
				}
				break;
			default:
				log.debug("ServerChange change defalut = "+event.getType().name());
				break;
			}
		} catch (Exception e) {
			
			log.error("ServerChange change e="+e.getMessage(), e);
		}
		
	}

}
