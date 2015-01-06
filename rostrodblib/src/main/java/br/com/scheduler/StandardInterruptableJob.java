package br.com.scheduler;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.UnableToInterruptJobException;

import br.com.scheduler.ProcessSchedule.ActionOnError;

/**
 * Marks a Job class as one that must not have multiple instances executed
 * concurrently
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
/**
 * 
 * @author davidson
 * @version 1.0
 * 
 * Class responsible for actually execute the methods contained in the class of business(runnableProcess)
 * 
 */
public class StandardInterruptableJob implements InterruptableJob {

	/**
	 * Logger
	 */
	private static Logger logger = Logger
			.getLogger(StandardInterruptableJob.class);

	/**
	 * Class of business
	 */
	private RunnableProcess runnableProcess;

	/**
	 * DTO
	 */
	private ProcessSchedule processSchedule;

	/**
	 * Method called when job can execute. Just Call the methods in
	 * runnableProcess
	 */
	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		/**
		 * Necessary to inject the necessary objects
		 * (runnableProcess,processSchedule)
		 */
		init(context);

		try {

			runnableProcess.init(processSchedule);

			runnableProcess.run();

		} catch (Exception e) {

			logger.error("Error executing runableProcess "
					+ processSchedule.getName() + " cause " + e.getMessage());

			JobExecutionException jobExecutionException = new JobExecutionException(
					e);

			/**
			 * According to the rule of business when a error happens , job can
			 * be RESCHEDULE or removed from the schedule.
			 */
			if (ActionOnError.RESCHEDULE.equals(processSchedule
					.getActionOnError())) {
				jobExecutionException.refireImmediately();
			} else {
				jobExecutionException.setUnscheduleFiringTrigger(true);
			}

			throw jobExecutionException;

		}
	}

	/**
	 * Retrieves runnableProcess,processSchedule
	 * 
	 * @param context
	 */
	public void init(JobExecutionContext context) {
		this.runnableProcess = (RunnableProcess) context.getJobDetail()
				.getJobDataMap().get(RunnableProcess.class.getName());
		this.processSchedule = (ProcessSchedule) context.getJobDetail()
				.getJobDataMap().get(ProcessSchedule.class.getName());
	}

	/**
	 * Method called when scheduler send a signal to interrupt the job. Just
	 * call the runnableProcess interrupt method
	 */
	public void interrupt() throws UnableToInterruptJobException {
		try {
			runnableProcess.stop();
		} catch (Exception e) {
			logger.error("Error executing the runabbleProcess stop "
					+ e.getMessage());

		}
	}

}
