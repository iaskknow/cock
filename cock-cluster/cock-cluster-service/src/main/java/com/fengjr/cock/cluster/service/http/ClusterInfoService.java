package com.fengjr.cock.cluster.service.http;

import com.fengjr.cock.cluster.service.quartz.SchedulerManager;

public class ClusterInfoService {

	
	
	public static Integer getTaskCount(){
		
		return SchedulerManager.getMap().size();
	}
	
	public static Integer getRunningTaskCount(){
		
//		return SchedulerManager.
		
		return 0;
	}
}
