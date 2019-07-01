package com.fengjr.cock.cluster.service.zookeeper;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.NodeExistsException;

import com.fengjr.cock.common.zookeeper.CuratorZKClient;

public class ZKClusterLock {

	
	
	public static boolean lock(CuratorZKClient client, String path){
		
		try{
			client.getClient().create().withMode(CreateMode.EPHEMERAL).forPath(path);
			return true;
		}
		catch(NodeExistsException e){
		}
		catch(Exception e){
		}
		return false;
	}
	
	public static void release(CuratorZKClient client, String path){
		
		try{
			client.delete(path);
		}
		catch(Exception e){
		}
	}
}
