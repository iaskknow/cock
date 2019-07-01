package com.fengjr.cock.cluster.service.quartz.listener;

import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fengjr.cock.cluster.service.quartz.SchedulerManager;

public class GlobalSchedulerListener implements SchedulerListener {

	protected static final Logger log = LoggerFactory.getLogger(GlobalSchedulerListener.class);
	
	@Override
	public void jobScheduled(Trigger trigger) {

	}

	@Override
	public void jobUnscheduled(String triggerName, String triggerGroup) {

	}

	@Override
	public void triggerFinalized(Trigger trigger) {

		try {
			SchedulerManager.getDeleteTrigger(trigger.getJobName(), trigger.getJobGroup());
		} catch (Exception e) {
			log.error("triggerFinalized trigger.getJobName()="+trigger.getJobName()+" e="+e.getMessage(), e);
		}
	}

	@Override
	public void triggersPaused(String triggerName, String triggerGroup) {
		
	}

	@Override
	public void triggersResumed(String triggerName, String triggerGroup) {

	}

	@Override
	public void jobAdded(JobDetail jobDetail) {

	}

	@Override
	public void jobDeleted(String jobName, String groupName) {

	}

	@Override
	public void jobsPaused(String jobName, String jobGroup) {

	}

	@Override
	public void jobsResumed(String jobName, String jobGroup) {

	}

	@Override
	public void schedulerError(String msg, SchedulerException cause) {

	}

	@Override
	public void schedulerInStandbyMode() {

	}

	@Override
	public void schedulerStarted() {

	}

	@Override
	public void schedulerShutdown() {

	}

	@Override
	public void schedulerShuttingdown() {

	}

}
