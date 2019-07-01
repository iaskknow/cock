package com.fengjr.cock.cluster.service.zookeeper;

import java.util.concurrent.CountDownLatch;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fengjr.cock.common.zookeeper.CuratorZKClient;
import com.fengjr.cock.common.zookeeper.ZKDictionary;


public enum ZKLeaderSelector {
	
	
	INSTANCE;
	
	private static final Logger log = LoggerFactory.getLogger(ZKLeaderSelector.class);
	
	private LeaderSelector leaderSelector;
	private CountDownLatch countDownLatch;

	public void getLeader(CuratorZKClient client, final ZKLeaderHandler handler){

		countDownLatch = new CountDownLatch(1);
		leaderSelector = new LeaderSelector(client.getClient(), 
				ZKDictionary.INSTANCE.getLeaderLockPath(), 
				new LeaderSelectorListener() {

			@Override
			public void stateChanged(CuratorFramework client, ConnectionState newState) {
				log.debug("LeaderSelectorListener stateChanged "+newState.name());
			}

			@Override
			public void takeLeadership(CuratorFramework client) throws Exception {
				
				log.debug("i am leader");
				
				handler.leaderNotify(client);
				
				countDownLatch.await();
				
				log.debug("release leader");
			}
		});
		leaderSelector.autoRequeue();
		leaderSelector.start();
	}

}
