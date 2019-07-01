package com.fengjr.cock.cluster.service.zookeeper.listener;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;

public interface Change {

	
	public void change(CuratorFramework client, PathChildrenCacheEvent event);
}
