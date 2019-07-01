package com.fengjr.cock.cluster.service.zookeeper.listener;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChildrenListener implements PathChildrenCacheListener{

	private static final Logger log = LoggerFactory.getLogger(ChildrenListener.class);
	
	private Change change;
	
	public ChildrenListener(Change change){
		
		this.change = change;
	}
	
	public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception{
		
		switch(event.getType()){
		
		case CHILD_ADDED: 
			change.change(client, event);
			break;
		case CHILD_REMOVED: 
			change.change(client, event);
			break;
		case CHILD_UPDATED:
			change.change(client, event);
			break;
		case CONNECTION_LOST: 
			log.debug("ChildrenListener CONNECTION_LOST");
			break;
		case CONNECTION_RECONNECTED: 
			log.debug("ChildrenListener CONNECTION_RECONNECTED");
			break;
		case CONNECTION_SUSPENDED: 
			log.debug("ChildrenListener CONNECTION_SUSPENDED");
			break;
		case INITIALIZED: 
			log.debug("ChildrenListener INITIALIZED");
			break;
		}
	}
}
