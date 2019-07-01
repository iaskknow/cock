package com.fengjr.cock.common.zookeeper;

import com.fengjr.cock.common.zookeeper.CuratorZKClient;

public enum ZKDictionary {
	
	INSTANCE;
	
	private final String root = "/fengjr";
	
	private final String projectPath = root + "/cock";
	
	private final String taskPath = projectPath + "/task";
	
	private final String serverPath = projectPath + "/server";
	
	private final String leaderLockPath = projectPath + "/leader_lock";
	
	private final String allottingLockPath = projectPath + "/allotting_lock";
	
	public void createCockPath(CuratorZKClient client){
		
		boolean exists = client.checkExists( root );
		
		if(exists){
			exists = client.checkExists( projectPath );
			if(exists){
				exists = client.checkExists( taskPath );
				if(exists){
					exists = client.checkExists( serverPath );
					if(exists){
						return;
					}
					else{
						client.createPersistent( serverPath );
						createCockPath(client);
					}
				}
				else{
					client.createPersistent( taskPath );
					createCockPath(client);
				}
			}
			else{
				client.createPersistent( projectPath );
				createCockPath(client);
			}
		}
		else{
			client.createPersistent( root );
			createCockPath(client);
		}
	}

	public String getRoot() {
		return root;
	}

	public String getProjectPath() {
		return projectPath;
	}

	public String getTaskPath() {
		return taskPath;
	}

	public String getServerPath() {
		return serverPath;
	}

	public String getLeaderLockPath() {
		return leaderLockPath;
	}

	public String getAllottingLockPath() {
		return allottingLockPath;
	}
	
	
}
