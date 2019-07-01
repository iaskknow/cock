package com.fengjr.cock.test;

import java.util.concurrent.CountDownLatch;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;

import com.fengjr.cock.common.zookeeper.ConnectHandler;
import com.fengjr.cock.common.zookeeper.CuratorZKClient;

public class ZKTest {

	
	
	public static void main(String[] args) throws Exception{
		
		
		CuratorZKClient client = new CuratorZKClient("127.0.0.1:2181", new ConnectHandler(){

			@Override
			public void connected(CuratorZKClient client, ConnectionState state) {
				
				String path = "/fengjr";
				try {
					PathChildrenCache cache = new PathChildrenCache(client.getClient(), path, false);
					cache.start(StartMode.POST_INITIALIZED_EVENT);
					cache.getListenable().addListener(new PathChildrenCacheListener() {
						@Override
						public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
							switch(event.getType()){
							
							case CHILD_ADDED: 
								System.out.println("CHILD_ADDED");
								break;
							
							case CHILD_REMOVED: 
								System.out.println("CHILD_REMOVED");
								break;
							
							case CHILD_UPDATED:
								System.out.println("CHILD_UPDATED");
								break;
							
							case CONNECTION_LOST: 
								
								System.out.println("CONNECTION_LOST");
								break;
							
							case CONNECTION_RECONNECTED: 
								
								System.out.println("CONNECTION_RECONNECTED");
								
								break;
							
							case CONNECTION_SUSPENDED: 
								
								System.out.println("CONNECTION_SUSPENDED");
							
								break;
							
							case INITIALIZED: 
								
								System.out.println("INITIALIZED");
								
								break;
							}
						}
					});
					
					client.createPersistent(path);
					
					Thread.sleep(1000);
					
					client.createPersistent(path+"/c1");
					
					Thread.sleep(1000);
					client.setData(path+"/c1", "{address:postman}");
					
					Thread.sleep(1000);
					
					System.out.println(client.getChildren(path).toString());
					
					System.out.println(client.getData(path+"/c1"));
					
					
					NodeCache nodeCache = new NodeCache(client.getClient(), path, false);
					nodeCache.start();
					
					nodeCache.getListenable().addListener(new NodeCacheListener() {
						
						@Override
						public void nodeChanged() throws Exception {
							
							System.out.println("changed ...");
							
						}
					});
					
					Thread.sleep(1000);
					
					client.delete(path+"/c1");
					
					Thread.sleep(1000);
					
					client.delete(path);
					
					Thread.sleep(10000);
					
					
					final CountDownLatch countDownLatch = new CountDownLatch(1);
					
					LeaderSelector leaderSelector = new LeaderSelector(client.getClient(), "/fengjrMasterPath", new LeaderSelectorListener() {
	
						@Override
						public void stateChanged(CuratorFramework client, ConnectionState newState) {
							
							System.out.println("LeaderSelectorListener stateChanged "+newState.name());
							
						}
	
						@Override
						public void takeLeadership(CuratorFramework client) throws Exception {
							
							System.out.println("i am leader");
							
							countDownLatch.await();
							
							Thread.sleep(1000);
							
							System.out.println("release leader");
						}
						
					});
					leaderSelector.autoRequeue();
					leaderSelector.start();
					
					Thread.sleep(10000);
					
					countDownLatch.countDown();
					
					Thread.sleep(1000);
					
					CountDownLatch c1 = new CountDownLatch(1);
					
					c1.await();
					
					client.doClose();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});
		
		
		Thread.sleep(Integer.MAX_VALUE);
	}
	
	
	
	
	
}
