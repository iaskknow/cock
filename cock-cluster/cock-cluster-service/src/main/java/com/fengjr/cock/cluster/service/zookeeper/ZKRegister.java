package com.fengjr.cock.cluster.service.zookeeper;

import com.fengjr.cock.cluster.domain.ServerNode;
import com.fengjr.cock.cluster.service.IPAddressUtil;
import com.fengjr.cock.common.json.GsonUtil;
import com.fengjr.cock.common.zookeeper.CuratorZKClient;
import com.fengjr.cock.common.zookeeper.ZKDictionary;

public class ZKRegister {

	
	public static void register(CuratorZKClient client) throws RuntimeException{
		
		
		try{
			String ID = IPAddressUtil.getIpAndPortString();
			
			String path= ZKDictionary.INSTANCE.getServerPath() + "/" + ID;
			
			boolean exists = client.checkExists(path);
			
			if(!exists){
				
				ServerNode node = new ServerNode();
				node.setLoad("0");
				client.createEphemeral(path, GsonUtil.toJsonString(node));
			}
		}
		catch(Exception e){
			throw new RuntimeException("register fail", e);
		}
	}
}
