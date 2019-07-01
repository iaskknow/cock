package com.fengjr.cock.common.zookeeper;

import org.apache.curator.framework.state.ConnectionState;

public interface ConnectHandler {

	
	public void connected(CuratorZKClient client, ConnectionState state);
}
