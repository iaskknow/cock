package com.fengjr.cock.test;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class TestJob implements Job{

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
//		context.get(key)
		
		System.out.println(context.getTrigger().getJobDataMap().get("key1") +"  "+ context.getTrigger().getName());
		
		System.out.println("i am job");
	}

}
