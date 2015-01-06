package br.com.scheduler;

import java.util.List;

import br.com.dao.Dao;
import br.com.exception.DbLibException;
import br.com.scheduler.ProcessSchedule.Status;
import br.com.service.GenericService;

/**
 * Class responsible for handling all the scheduling processes.
 * 
 * @version 1.1
 */

public class ProcessScheduleServiceImpl extends
		GenericService<ProcessSchedule, Dao<ProcessSchedule>> implements
		ProcessScheduleService {

	private static StandardScheduler scheduler;

	/**
	 * @throws BasicException
	 *             Method responsible for start the scheduler and load all
	 *             process from database
	 */
	public void startSchedulingService() throws DbLibException {

		if (scheduler == null) {
			scheduler = new QuartzScheduler();
		}

		List<ProcessSchedule> listProcessSchedule;
		scheduler.start();
		listProcessSchedule = dao.findAll();
		scheduler.scheduleAllProcess(listProcessSchedule);

	}

	/**
	 * Method responsible for stopping the scheduler,can receive a parameter to
	 * wait for all processes finish
	 * 
	 * @param waitForJobsToComplete
	 * @throws BasicException
	 */
	public void stopSchedulingService(boolean waitAllProcessScheduleComplete)
			throws DbLibException {

		scheduler.shutdown(waitAllProcessScheduleComplete);

	}

	@Override
	/**
	 * The next execution date receive the starting date because the trigger uses as parameter
	 * The status of the process receives SCHEDULED
	 */
	public void beforeInsert(ProcessSchedule processSchedule)
			throws DbLibException {
		processSchedule.setNextExecution(processSchedule.getStartDate());
		if (!processSchedule.getStatus().equals(Status.DISABLED)) {
			processSchedule.setStatus(Status.SCHEDULED);
		}

	}

	@Override
	/**
	 * Calls method startTrigger, after insert to schedule the process
	 */
	public void afterInsert(ProcessSchedule processSchedule)
			throws DbLibException {

		scheduler.scheduleProcess(processSchedule);

	}

	@Override
	/**
	 * Calls the method to RESCHEDULE the process
	 */
	public void afterUpdate(ProcessSchedule processSchedule)
			throws DbLibException {

		scheduler.rescheduleProcess(processSchedule);

	}

	@Override
	/**
	 * when the process is deleted also unschedule the Job
	 */
	public void afterDelete(ProcessSchedule processSchedule)
			throws DbLibException {
		scheduler.unscheduleProcess(processSchedule);

	}

	public StandardScheduler getScheduler() {
		return scheduler;
	}

	public void setScheduler(StandardScheduler scheduler) {
		ProcessScheduleServiceImpl.scheduler = scheduler;
	}

	@Override
	public Dao<br.com.scheduler.ProcessSchedule> createDao() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean consiste() {
		// TODO Auto-generated method stub
		return false;
	}

}
