package br.com.scheduler;

import java.util.List;

import br.com.exception.DbLibException;

/**
 * @author davidson
 * @version 1.0
 */
public interface StandardScheduler {
	/**
	 * Method responsible for initializing the scheduler.
	 * 
	 * @throws DbLibException
	 */
	public void start() throws DbLibException;

	/**
	 * Method responsible for stopping the scheduler,can receive a parameter to
	 * wait for all processes finish
	 * @param waitForJobsToComplete
	 * @throws BasicException
	 */
	public void shutdown(boolean waitAllProcessScheduleComplete) throws DbLibException;

	/**
	 * Method responsible to schedule the ProcessSchedule
	 * 
	 * @param processSchedule
	 * @throws BasicException
	 */
	public void scheduleProcess(ProcessSchedule processSchedule)
			throws DbLibException;

	/**
	 * Method responsible to RESCHEDULE the ProcessSchedule
	 * 
	 * @param processSchedule
	 * @throws BasicException
	 */
	public void rescheduleProcess(ProcessSchedule processSchedule)
			throws DbLibException;

	/**
	 * Method responsible to remove the processSchedule from the schedule
	 * 
	 * @param processSchedule
	 * @throws BasicException
	 */
	public void unscheduleProcess(ProcessSchedule processSchedule)
			throws DbLibException;

	/**
	 * Method responsible to schedule the all the ProcessSchedule
	 * 
	 * @param listProcessSchedule
	 * @throws BasicException
	 */
	public void scheduleAllProcess(List<ProcessSchedule> listProcessSchedule)
			throws DbLibException;

	/**
	 * Method responsible to return a list of all the current running jobs
	 * 
	 * @return List<String>
	 * @throws BasicException
	 */
	public List<String> getRunningJobs() throws DbLibException;;

}
