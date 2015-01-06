package br.com.scheduler;

import static org.quartz.DateBuilder.futureDate;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.InterruptableJob;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerKey;

import br.com.exception.DbLibException;
import br.com.scheduler.ProcessSchedule.Periodicity;
import br.com.scheduler.ProcessSchedule.Status;
import br.com.service.Service;
import br.com.service.ServiceFactory;

import com.mysql.jdbc.log.Log;

/**
 * @author davidson
 * @version 1.1 Class responsible to log and take some actions according to
 *          events in job
 */
public class StandardJobListener implements JobListener {
	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(StandardJobListener.class);

	public String getName() {
		return "jobListener";
	}

	/**
	 * Run this if job is about to be executed. This method is responsible for
	 * starting another job to interrupt the currentJob when it reaches the
	 * maximum processing time. This method also set the DTO(ProcessSchedule)
	 * status to running and nextExecution.
	 */
	@SuppressWarnings("unchecked")
	public void jobToBeExecuted(JobExecutionContext inContext) {

		/**
		 * Just ArangiInterruptableJob ill start another job.
		 */
		if (inContext.getJobInstance() instanceof InterruptableJob) {
			/**
			 * Retrieves the processSchedule object from the current job
			 */
			ProcessSchedule processSchedule = (ProcessSchedule) inContext
					.getJobDetail().getJobDataMap()
					.get(ProcessSchedule.class.getName());

			/**
			 * If job dont have maxTimeToProcess ill never be interrupt
			 */
			if (processSchedule.getMaxTimeToProcess() > 0) {
				Trigger trigger = getTrigger(processSchedule);
				/**
				 * Create a new ArangiJob with a random identity
				 */
				JobDetail job = newJob(StandardJob.class).withIdentity(
						UUID.randomUUID().toString()).build();
				/**
				 * Inject the current jobKey
				 */
				job.getJobDataMap().put(JobKey.class.getName(),
						inContext.getJobDetail().getKey());

				/**
				 * Inject in the current Job a trigger who will stop him.
				 */
				inContext.getJobDetail().getJobDataMap()
						.put(TriggerKey.class.getName(), trigger.getKey());
				try {
					inContext.getScheduler().scheduleJob(job, trigger);
					logger.info("Created the job to stop "
							+ processSchedule.getName());
				} catch (SchedulerException e) {
					logger.error("Failed to create a job to interrupt the process: "
							+ processSchedule.getName());
				}

			}
			/**
			 * Update dto object
			 */
			processSchedule.setNextExecution(inContext.getNextFireTime());
			processSchedule.setStatus(Status.RUNNING);

			@SuppressWarnings("rawtypes")
			Service service = ServiceFactory.getService(ProcessSchedule.class);

			/**
			 * Call the facade to update dto.
			 */
			try {
				service.save(processSchedule);
			} catch (DbLibException e) {
				logger.error("Unable to update the dto when job to be executed. Job "
						+ processSchedule.getName());
			}
		}
	}

	/**
	 * Run this after job has been executed. This method is responsible to
	 * update the status to FINISHED in the database
	 */
	@SuppressWarnings("unchecked")
	public void jobWasExecuted(JobExecutionContext context,
			JobExecutionException jobException) {

		/**
		 * Just ArangiInterruptableJob ill update status
		 */
		if (context.getJobInstance() instanceof InterruptableJob) {
			ProcessSchedule processSchedule = (ProcessSchedule) context
					.getJobDetail().getJobDataMap()
					.get(ProcessSchedule.class.getName());

			processSchedule.setStatus(processSchedule.getPeriodicity().equals(
					Periodicity.SINGLE) ? Status.COMPLETE : Status.FINISHED);
			/**
			 * Call the facade to update object
			 */
			try {

				@SuppressWarnings("rawtypes")
				Service service = ServiceFactory
						.getService(ProcessSchedule.class);

				service.save(processSchedule);

			} catch (DbLibException e) {
				logger.error("Unable to update the dto after job Executed. Job "
						+ processSchedule.getName());

			}
		}

	}

	/**
	 * The Scheduler calls this method when the JobDetail was about to be
	 * executed but a triggerListener vetoed the execution.
	 */
	public void jobExecutionVetoed(JobExecutionContext context) {
		logger.info("Job " + context.getJobDetail().getKey() + "vetoed");

	}

	/**
	 * Method responsible to validate time to process according to business
	 * role. If currentHour greater than job starHour calculates the remaining
	 * time of the job.
	 * 
	 * @param processSchedule
	 * @return
	 */
	public int validateMaxTimeToProcess(ProcessSchedule processSchedule) {
		long tenMinutes = 10 * 60000;
		int maxTimeToProcess = processSchedule.getMaxTimeToProcess();
		Date dateToCompare = new Date(System.currentTimeMillis() - tenMinutes);

		if (processSchedule.getNextExecution().before(dateToCompare)) {

			Calendar calendar = Calendar.getInstance();

			calendar.setTime(dateToCompare);

			int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

			calendar.setTime(processSchedule.getNextExecution());
			int startHour = calendar.get(Calendar.HOUR_OF_DAY);
			calendar.add(Calendar.HOUR, maxTimeToProcess);
			int endHour = calendar.get(Calendar.HOUR_OF_DAY);

			if (currentHour > startHour) {
				if (currentHour < endHour) {
					maxTimeToProcess = endHour - currentHour;
				}
			}

		}
		return maxTimeToProcess;
	}

	/**
	 * Get a trigger according to the maxTimeToProcess
	 * 
	 * @param processSchedule
	 * @return
	 */
	public Trigger getTrigger(ProcessSchedule processSchedule) {
		return newTrigger()
				.withDescription(processSchedule.getName())
				.withIdentity(UUID.randomUUID().toString())
				.withSchedule(
						SimpleScheduleBuilder.simpleSchedule()
								.withMisfireHandlingInstructionFireNow())
				.startAt(
						futureDate(validateMaxTimeToProcess(processSchedule),
								IntervalUnit.HOUR)).build();
	}

}
