package br.com.scheduler;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.Trigger;
import org.quartz.TriggerKey;

/**
 * 
 * @author davidson
 * @version 1.0 Class responsible to log some events in the Scheduler
 */
public class StandardSchedulerListener implements SchedulerListener {

	private static Logger logger = Logger
			.getLogger(StandardSchedulerListener.class);

	/**
	 * The Scheduler calls these methods when a new JobDetail is scheduled
	 */
	public void jobScheduled(Trigger trigger) {

		logger.info(trigger.getJobKey() + " has been scheduled");

	}

	/**
	 * The Scheduler calls these methods when job is unscheduled.
	 */
	public void jobUnscheduled(TriggerKey triggerKey) {
		logger.info(triggerKey + " is being unscheduled");

	}

	/**
	 * The Scheduler calls this method when a trigger has reached the state that
	 * it will never fire again.
	 */
	public void triggerFinalized(Trigger trigger) {
		logger.info("Trigger is finished for " + trigger.getJobKey());

	}

	/**
	 * The Scheduler calls this method when a trigger has been paused
	 */
	public void triggerPaused(TriggerKey triggerKey) {
		logger.info(triggerKey + " is being paused");

	}

	/**
	 * The Scheduler calls this method when a group of triggers has been paused
	 */
	public void triggersPaused(String triggerGroup) {
		logger.info(triggerGroup + " is being paused");

	}

	/**
	 * The Scheduler calls this method when a group of triggers has been
	 * unpaused (or resumed)
	 */
	public void triggerResumed(TriggerKey triggerKey) {
		logger.info(triggerKey + " is now resuming");

	}

	/**
	 * The Scheduler calls this method when a trigger has been unpaused (or
	 * resumed).
	 */
	public void triggersResumed(String triggerGroup) {
		logger.info(triggerGroup + " is now resuming");

	}

	public void jobAdded(JobDetail jobDetail) {
		// TODO Auto-generated method stub

	}

	/**
	 * The Scheduler calls these methods when a new JobDetail is deleted.
	 * Similar to unscheduled
	 */
	public void jobDeleted(JobKey jobKey) {
		logger.info(jobKey + " is deleted");

	}

	/**
	 * The Scheduler calls this method when a job has been paused.
	 */
	public void jobPaused(JobKey jobKey) {
		logger.info(jobKey + " is pausing");

	}

	/**
	 * The Scheduler calls this method when a jobGroup has been paused.
	 */
	public void jobsPaused(String jobGroup) {
		logger.info(jobGroup + " is pausing");

	}

	/**
	 * The Scheduler calls this method when a job has been unpaused (or
	 * resumed);
	 */
	public void jobResumed(JobKey jobKey) {
		logger.info(jobKey + " is now resuming");

	}

	/**
	 * The Scheduler calls this method when a group of jobs has been unpaused
	 * (or resumed)
	 */
	public void jobsResumed(String jobGroup) {
		logger.info(jobGroup + " is now resuming");

	}

	/**
	 * The Scheduler calls this method when a serious error has occurred during
	 * the normal runtime of the Scheduler.
	 */
	public void schedulerError(String msg, SchedulerException cause) {
		logger.error(msg, cause.getUnderlyingException());

	}

	/**
	 * The Scheduler calls this method when scheduler is started
	 */
	public void schedulerStarted() {
		logger.info("Scheduler is being started");

	}

	/**
	 * The Scheduler calls this method when scheduler is shutdown
	 */
	public void schedulerShutdown() {
		logger.info("Scheduler shutdown");

	}

	/**
	 * The Scheduler calls this method when scheduler is beings shutdown
	 */
	public void schedulerShuttingdown() {
		logger.info("Scheduler is being shutdown");

	}

	public void schedulingDataCleared() {
		// TODO Auto-generated method stub

	}

	public void schedulerInStandbyMode() {
		// TODO Auto-generated method stub

	}

}
