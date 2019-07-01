package com.fengjr.cock.manage.service.zookeeper;

import com.fengjr.cock.common.zookeeper.ConnectHandler;
import com.fengjr.cock.common.zookeeper.CuratorZKClient;
import org.apache.curator.framework.state.ConnectionState;

public class ZKConnectedHandler implements ConnectHandler {

    @Override
    public void connected(CuratorZKClient client, ConnectionState state) {

    }
}
