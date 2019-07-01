package com.fengjr.cock.test;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SchedulerListener;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerListener;
import org.quartz.impl.StdSchedulerFactory;

import com.fengjr.cock.common.json.GsonUtil;

public class QuartzTest {

	
	
	public static void main(String[] args) {
		
		JobDetail jobDetail = null;
		JobDataMap jobDataMap = null;
		Trigger trigger = null;
		try {
			
			Calendar ca = java.util.Calendar.getInstance();
			ca.add(Calendar.DATE, 2);
			
			final Scheduler sch = StdSchedulerFactory.getDefaultScheduler();
			
			sch.start();
			
			jobDetail = new JobDetail("simple1", "group1", TestJob.class);
			
//			trigger = new CronTrigger("simple1", "group1", "*/2 * * * * ?");
			
			trigger = new CronTrigger("simple1", "group1", "simple1", "group1", new Date(), null, "0 31 17 * * ?");
//			trigger = new SimpleTrigger("simple1", "group1", new Date(), ca.getTime(), -1, 1000);
			
			jobDataMap = new JobDataMap();
			
			jobDataMap.put("key1", "V1");
			trigger.setJobDataMap(jobDataMap);
			
			sch.scheduleJob(jobDetail, trigger);
			
//			sch.pauseTrigger("simple1", "group1");
			
			sch.addGlobalTriggerListener(new TriggerListener() {
				
				@Override
				public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
					return false;
				}
				
				@Override
				public void triggerMisfired(Trigger trigger) {
					System.out.println("triggerMisfired...");
				}
				
				@Override
				public void triggerFired(Trigger trigger, JobExecutionContext context) {
					System.out.println("triggerFired...");
				}
				
				@Override
				public void triggerComplete(Trigger trigger, JobExecutionContext context, int triggerInstructionCode) {
					System.out.println("triggerComplete...");
				}
				
				@Override
				public String getName() {
					return "global";
				}
			});
			
			
			sch.addSchedulerListener(new SchedulerListener() {
				
				@Override
				public void triggersResumed(String triggerName, String triggerGroup) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void triggersPaused(String triggerName, String triggerGroup) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void triggerFinalized(Trigger trigger) {
					
					// 更新数据状态 finished
					
					try {
						sch.deleteJob(trigger.getJobName(), trigger.getJobGroup());
					} catch (SchedulerException e) {
						e.printStackTrace();
					}
					
					System.out.println(trigger.getJobName()+" "+trigger.getJobGroup()+" triggerFinalized");
				}
				
				@Override
				public void schedulerStarted() {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void schedulerShuttingdown() {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void schedulerShutdown() {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void schedulerInStandbyMode() {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void schedulerError(String msg, SchedulerException cause) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void jobsResumed(String jobName, String jobGroup) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void jobsPaused(String jobName, String jobGroup) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void jobUnscheduled(String triggerName, String triggerGroup) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void jobScheduled(Trigger trigger) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void jobDeleted(String jobName, String groupName) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void jobAdded(JobDetail jobDetail) {
					// TODO Auto-generated method stub
					
				}
			});
			
			
		
			
			
			
//			Thread.sleep(10000L);
//			
//			String[] groups = sch.getTriggerGroupNames();
//			String[] triggers = null;
//			for(String group:groups){
//				
//				System.out.println("group:"+group);
//				
//				triggers = sch.getTriggerNames(group);
//				
//				for(String e:triggers){
//					
//					System.out.println("trigger:"+e);
//					
//					System.out.println("trigger state： "+sch.getTriggerState(e, group));
//				}
//			}
			
			
			
			
//			Trigger t1 = sch.getTrigger("simple1", "group1");
//			
//			if(t1 != null){
//				
//				if(t1 instanceof SimpleTrigger){
//					
//					SimpleTrigger st1 = (SimpleTrigger)t1;
//					
//					st1.setRepeatInterval(2000);
//					jobDataMap = new JobDataMap();
//					
//					jobDataMap.put("key1", "VVVVVVVVVVVVVVVVVVVVVV22222");
//					
//					st1.setJobDataMap(jobDataMap);
//					
//					sch.rescheduleJob("simple1", "group1", st1);
//					
//				}
//				else if( t1 instanceof CronTrigger){
//					
//					CronTrigger ct1 = (CronTrigger)t1;
//					
//					ct1.setCronExpression("1 * * * * ? 2019");
//					
//					jobDataMap = new JobDataMap();
//					
//					jobDataMap.put("key1", "cron VVVVVVVVVVVVVVVV3");
//					
//					ct1.setJobDataMap(jobDataMap);
//					
//					sch.rescheduleJob("simple1", "group1", ct1);
//				}
//			}
			
			
		
			
			int state = sch.getTriggerState("simple1", "group1");
			System.out.println("trigger状态" + state);
//			
//			Thread.sleep(2000L);
//			sch.pauseTrigger("simple1", "group1");
//			System.out.println("已暂停");
//			
//			state = sch.getTriggerState("simple1", "group1");
//			System.out.println("trigger状态" + state);
//			
//			Thread.sleep(2000L);
//			sch.resumeTrigger("simple1", "group1");
//			System.out.println("已重新开始");
//			
//			state = sch.getTriggerState("simple1", "group1");
//			System.out.println("trigger状态" + state);
//			
//			sch.deleteJob("jobName1业务job", "分组1");
//			
//			state = sch.getTriggerState("simple1", "group1");
//			System.out.println("trigger状态" + state);
			
			

			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
