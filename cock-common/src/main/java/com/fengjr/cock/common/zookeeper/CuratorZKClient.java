package com.fengjr.cock.common.zookeeper;

import java.util.List;

import javax.servlet.ServletContext;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CuratorZKClient {

	protected static final Logger log = LoggerFactory.getLogger(CuratorZKClient.class);

	private final CuratorFramework client;
	
	public static ServletContext servletContext = null;
	
	public static CuratorZKClient getCuratorZKClient(){
		
		CuratorZKClient client = null;
		if( null != servletContext ){
			
			client = (CuratorZKClient)servletContext.getAttribute("client");
		}
		return client;
	}

    public CuratorZKClient(String connectString, final ConnectHandler handler) {
        try {
            CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder()
                    .connectString(connectString)
                    .retryPolicy(new RetryNTimes(1, 1000))
                    .connectionTimeoutMs(5000)
                    .sessionTimeoutMs(20000)
                    .authorization("digest", "fengjr:fengjr.com".getBytes());
            client = builder.build();
            client.getConnectionStateListenable().addListener(new ConnectionStateListener() {
				
				@Override
				public void stateChanged(CuratorFramework client, ConnectionState state) {
					
					if(state == ConnectionState.LOST){
						log.debug("CuratorZKClient=================================lost");
					}
					else if(state == ConnectionState.CONNECTED){
						log.debug("CuratorZKClient==================================connected");
						handler.connected(CuratorZKClient.this, ConnectionState.CONNECTED);
					}
					else if(state == ConnectionState.RECONNECTED){
						log.debug("CuratorZKClient==================================reconnected");
						handler.connected(CuratorZKClient.this, ConnectionState.RECONNECTED);
					}
					else if(state == ConnectionState.SUSPENDED){
						
						log.debug("CuratorZKClient===================================SUSPENDED");
					}
					
				}
			});
            
            client.start();
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
    
    public CuratorFramework getClient(){
    	
    	return client;
    }
    
    public void createPersistent(String path) {
        try {
            client.create().forPath(path);
        } catch (NodeExistsException e) {
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public String createPersistentSequential(String path) {
        try {
            return client.create().withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath(path);
        } catch (NodeExistsException e) {
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        return null;
    }
    
    public void createEphemeral(String path) {
        try {
            client.create().withMode(CreateMode.EPHEMERAL).forPath(path);
        } catch (NodeExistsException e) {
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
    
    public void createEphemeral(String path, String data) {
    	
    	 try {
             client.create().withMode(CreateMode.EPHEMERAL).forPath(path, data.getBytes("UTF-8"));
         } catch (NodeExistsException e) {
         } catch (Exception e) {
             throw new IllegalStateException(e.getMessage(), e);
         }
    }
    
    public String createEphemeralSequential(String path) {
        try {
            return client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(path);
        } catch (NodeExistsException e) {
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        return null;
    }

    public void delete(String path) {
        try {
            client.delete().forPath(path);
        } catch (NoNodeException e) {
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public List<String> getChildren(String path) {
        try {
            return client.getChildren().forPath(path);
        } catch (NoNodeException e) {
            return null;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public boolean checkExists(String path) {
        try {
            if (client.checkExists().forPath(path) != null) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }
    
    public boolean isConnected() {
        return client.getZookeeperClient().isConnected();
    }

    public void doClose() {
        client.close();
    }
    
    
    public String getData(String path) throws Exception{
    	
    	byte[] bytes = null;
    	String result = null;
    	try{
    		bytes = client.getData().forPath(path);
    		
    		result = new String(bytes, "UTF-8");
    	}
    	finally{
    		bytes = null;
    	}
    	return result;
    }
    
    
    public int setData(String path, String data) throws Exception {
    	
    	int version = 0;
    	Stat stat = new Stat();
    	try{
    		client.getData().storingStatIn(stat).forPath(path);
    		version = client.setData()
    				.withVersion(stat.getVersion())
    				.forPath(path, data.getBytes("UTF-8")).getVersion();
    	}
    	finally{
    		stat = null;
    	}
    	
    	return version;
    }

}
