package br.com.scheduler;

import static org.quartz.CalendarIntervalScheduleBuilder.calendarIntervalSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SchedulerListener;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.TriggerListener;
import org.quartz.UnableToInterruptJobException;
import org.quartz.impl.StdSchedulerFactory;

import br.com.exception.DbLibException;
import br.com.scheduler.ProcessSchedule.Periodicity;
import br.com.scheduler.ProcessSchedule.Status;
import br.com.util.ReflectionUtil;

/**
 * 
 * @author davidson
 * @version 1.0
 */
public class QuartzScheduler implements StandardScheduler {

	private static Logger log = Logger.getLogger(QuartzScheduler.class);
	/**
	 * 
	 */
	private Scheduler scheduler;

	/**
	 * @throws DbLibException
	 *             Method responsible for initializing the scheduler.
	 */
	public void start() throws DbLibException {

		try {

			init();
			scheduler.start();
		} catch (SchedulerException e) {
			throw new DbLibException("Failed to start the scheduler", e, log);
		}

	}

	/**
	 * Method responsible for stopping the scheduler,can receive a parameter to
	 * wait for all processes finish
	 * 
	 * @param waitForJobsToComplete
	 * @throws DbLibException
	 */
	public void shutdown(boolean waitForJobsToComplete) throws DbLibException {
		try {
			scheduler.shutdown(waitForJobsToComplete);
		} catch (SchedulerException e) {
			throw new DbLibException("Failed to shutdown the scheduler", e, log);
		}

	}

	/**
	 * Take action according to the status of the process. Status is equal to
	 * DISABLED, calls the method to DISABLED the process Status is equal to
	 * REACTIVE, calls the method to REACTIVE the process Status is equal to
	 * RESCHEDULE, calls the method to RESCHEDULE the process
	 */
	public void rescheduleProcess(ProcessSchedule processSchedule)
			throws DbLibException {
		if (processSchedule.getStatus().equals(Status.DISABLED)) {
			disableJob(processSchedule);
		} else if (processSchedule.getStatus().equals(Status.REACTIVE)) {
			reactiveJob(processSchedule);
		} else {
			rescheduleJob(processSchedule);
		}

	}

	/**
	 * Method responsible to remove the processSchedule from schedule
	 */
	public void unscheduleProcess(ProcessSchedule processSchedule)
			throws DbLibException {
		unscheduleJob(processSchedule, false);

	}

	/**
	 * Start a list of triggers
	 */
	public void scheduleAllProcess(List<ProcessSchedule> listProcessSchedule)
			throws DbLibException {
		startTriggers(listProcessSchedule);
	}

	/**
	 * The method initializes the scheduler and all listeners used to control
	 * scheduling
	 * 
	 * @throws SchedulerException
	 */
	public void init() throws SchedulerException {
		SchedulerFactory schedulerFactory = new StdSchedulerFactory();

		scheduler = schedulerFactory.getScheduler();
		TriggerListener triggerListener = new StandardTriggerListener();
		JobListener jobListener = new StandardJobListener();
		SchedulerListener schedulerListener = new StandardSchedulerListener();
		scheduler.getListenerManager().addJobListener(jobListener);
		scheduler.getListenerManager().addTriggerListener(triggerListener);
		scheduler.getListenerManager().addSchedulerListener(schedulerListener);

	}

	public void scheduleProcess(ProcessSchedule processSchedule)
			throws DbLibException {
		startTrigger(processSchedule);
	}

	/**
	 * Method only makes the call to the method startTrigger
	 * 
	 * @param listProcessSchedule
	 * @throws DbLibException
	 */
	private void startTriggers(List<ProcessSchedule> listProcessSchedule)
			throws DbLibException {

		for (ProcessSchedule processScheduleInstance : listProcessSchedule) {
			if (!(processScheduleInstance.getPeriodicity().equals(
					Periodicity.SINGLE) && processScheduleInstance.getStatus()
					.equals(Status.COMPLETE))) {
				startTrigger(processScheduleInstance);
			}

		}

	}

	/**
	 * Checks if the job already exists and call the method processTrigger
	 * 
	 * @param processSchedule
	 * @throws DbLibException
	 */
	private void startTrigger(ProcessSchedule processSchedule)
			throws DbLibException {
		if (!checkJobExists(processSchedule)) {
			processTrigger(processSchedule);
		}
	}

	/**
	 * Retrieves the list of all running processes
	 * 
	 * @throws DbLibException
	 * @return
	 */
	public List<String> getRunningJobs() throws DbLibException {

		try {
			List<JobExecutionContext> listJobExecutionContext;
			listJobExecutionContext = scheduler.getCurrentlyExecutingJobs();

			List<String> runningJobs = new ArrayList<String>();

			for (JobExecutionContext jobExecutionContext : listJobExecutionContext) {
				runningJobs.add(jobExecutionContext.getJobDetail().getKey()
						.getName());
			}

			return runningJobs;
		} catch (SchedulerException e) {

			throw new DbLibException("Failed to get the current running jobs",
					e, log);
		}

	}

	/**
	 * Create a trigger based on days and the information received from the
	 * processSchedule Every trigger has a unique identifier, in this case is
	 * the name of the processSchedule
	 * 
	 * @param processSchedule
	 * @return
	 * @throws DbLibException
	 */
	private Trigger createDailyTrigger(ProcessSchedule processSchedule)
			throws DbLibException {

		if (!canProcess(processSchedule)) {
			Trigger trigger = newTrigger()
					.withDescription(processSchedule.getName())
					.withIdentity(processSchedule.getName())
					.withSchedule(
							calendarIntervalSchedule()
									.withIntervalInDays(
											processSchedule.getInterval())
									.withMisfireHandlingInstructionDoNothing()
									.preserveHourOfDayAcrossDaylightSavings(
											true))
					.startAt(processSchedule.getNextExecution()).build();

			return trigger;
		}

		Trigger trigger = newTrigger()
				.withDescription(processSchedule.getName())
				.withIdentity(processSchedule.getName())
				.withSchedule(
						calendarIntervalSchedule()
								.withIntervalInDays(
										processSchedule.getInterval())
								.withMisfireHandlingInstructionFireAndProceed()
								.preserveHourOfDayAcrossDaylightSavings(true))
				.startAt(processSchedule.getNextExecution()).build();

		return trigger;
	}

	/**
	 * Create a trigger based on weeks and the information received from the
	 * processSchedule Every trigger has a unique identifier, in this case is
	 * the name of the processSchedule
	 * 
	 * @param processSchedule
	 * @return
	 * @throws DbLibException
	 */
	private Trigger createWeeklyTrigger(ProcessSchedule processSchedule)
			throws DbLibException {

		if (!canProcess(processSchedule)) {
			Trigger trigger = newTrigger()
					.withDescription(processSchedule.getName())
					.withIdentity(processSchedule.getName())
					.withSchedule(
							calendarIntervalSchedule()
									.withIntervalInWeeks(
											processSchedule.getInterval())
									.withMisfireHandlingInstructionDoNothing()
									.preserveHourOfDayAcrossDaylightSavings(
											true))
					.startAt(processSchedule.getNextExecution()).build();
			return trigger;
		}

		Trigger trigger = newTrigger()
				.withDescription(processSchedule.getName())
				.withIdentity(processSchedule.getName())
				.withSchedule(
						calendarIntervalSchedule()
								.withIntervalInWeeks(
										processSchedule.getInterval())
								.withMisfireHandlingInstructionFireAndProceed()
								.preserveHourOfDayAcrossDaylightSavings(true))
				.startAt(processSchedule.getNextExecution()).build();
		return trigger;

	}

	/**
	 * Create a trigger based on mounths and the information received from the
	 * processSchedule Every trigger has a unique identifier, in this case is
	 * the name of the processSchedule
	 * 
	 * @param processSchedule
	 * @return
	 * @throws DbLibException
	 */
	private Trigger createMonthlyTrigger(ProcessSchedule processSchedule)
			throws DbLibException {
		if (!canProcess(processSchedule)) {
			Trigger trigger = newTrigger()
					.withDescription(processSchedule.getName())
					.withIdentity(processSchedule.getName())
					.withSchedule(
							calendarIntervalSchedule()
									.withIntervalInMonths(
											processSchedule.getInterval())
									.withMisfireHandlingInstructionDoNothing()
									.preserveHourOfDayAcrossDaylightSavings(
											true))
					.startAt(processSchedule.getNextExecution()).build();
			return trigger;
		}
		Trigger trigger = newTrigger()
				.withDescription(processSchedule.getName())
				.withIdentity(processSchedule.getName())
				.withSchedule(
						calendarIntervalSchedule()
								.withIntervalInMonths(
										processSchedule.getInterval())
								.withMisfireHandlingInstructionFireAndProceed()
								.preserveHourOfDayAcrossDaylightSavings(true))
				.startAt(processSchedule.getNextExecution()).build();
		return trigger;

	}

	/**
	 * Create a trigger based on "intraDay" information(minutes interval) and
	 * the information received from the processSchedule Every trigger has a
	 * unique identifier, in this case is the name of the processSchedule
	 * 
	 * @param processSchedule
	 * @return
	 * @throws DbLibException
	 */
	private Trigger createIntraDayTrigger(ProcessSchedule processSchedule)
			throws DbLibException {

		Trigger trigger = newTrigger()
				.withDescription(processSchedule.getName())
				.withIdentity(processSchedule.getName())
				.withSchedule(
						SimpleScheduleBuilder
								.simpleSchedule()
								.withIntervalInMinutes(
										processSchedule.getInterval())
								.repeatForever())
				.startAt(processSchedule.getNextExecution()).build();
		return trigger;

	}

	/**
	 * Call the method createJob based on information dto,trigger and
	 * RunnableProcess
	 * 
	 * @param processSchedule
	 * @throws DbLibException
	 */
	private void processTrigger(ProcessSchedule processSchedule)
			throws DbLibException {

		createJob(getRunnableProcess(processSchedule),
				getTrigger(processSchedule), processSchedule);

	}

	/**
	 * Create a trigger based on the information received from the
	 * processSchedule Every trigger has a unique identifier, in this case is
	 * the name of the processSchedule Single trigger just execute once.
	 * 
	 * @param processSchedule
	 * @return
	 */
	private Trigger createSingleTrigger(ProcessSchedule processSchedule) {
		Trigger trigger = newTrigger()
				.withDescription(processSchedule.getName())
				.withIdentity(processSchedule.getName())
				.withSchedule(SimpleScheduleBuilder.simpleSchedule())
				.startAt(processSchedule.getNextExecution()).build();
		return trigger;
	}

	/**
	 * Injects into the job the runnableProcess,processSchedule objects and
	 * creates the job that will be associated with a trigger. Call the method
	 * scheduleJob.
	 * 
	 * @param runnableProcess
	 * @param trigger
	 * @param processSchedule
	 * @throws DbLibException
	 */
	private void createJob(RunnableProcess runnableProcess, Trigger trigger,
			ProcessSchedule processSchedule) throws DbLibException {
		JobDetail job = newJob(StandardInterruptableJob.class).withIdentity(

		processSchedule.getName()).build();
		job.getJobDataMap().put(RunnableProcess.class.getName(),
				runnableProcess);
		job.getJobDataMap().put(ProcessSchedule.class.getName(),
				processSchedule);

		scheduleJob(job, trigger);
	}

	/**
	 * Create the schedule based on job and trigger
	 * 
	 * @param jobDetail
	 * @param trigger
	 * @throws DbLibException
	 */
	private void scheduleJob(JobDetail jobDetail, Trigger trigger)
			throws DbLibException {
		try {
			scheduler.scheduleJob(jobDetail, trigger);

		} catch (SchedulerException e) {

			throw new DbLibException("Failed to schedule the process", e, log);
		}

	}

	/**
	 * Method responsible to remove the job from schedule
	 * 
	 * @param processSchedule
	 * @param interrupt
	 * @throws DbLibException
	 */
	private void unscheduleJob(ProcessSchedule processSchedule,
			boolean interrupt) throws DbLibException {
		JobKey key = getJobKeyByName(processSchedule);
		try {
			if (interrupt) {
				scheduler.interrupt(key);
			}
			scheduler.deleteJob(key);
		} catch (SchedulerException e) {

			throw new DbLibException("Failed to unschedule the process", e, log);
		}
	}

	/**
	 * Method responsible to send a signal to interrupt the job
	 * 
	 * @param processSchedule
	 * @throws DbLibException
	 */
	public void interrupt(ProcessSchedule processSchedule)
			throws DbLibException {
		JobKey key = getJobKeyByName(processSchedule);
		try {
			scheduler.interrupt(key);
		} catch (UnableToInterruptJobException e) {

			throw new DbLibException("Failed to interrupt the process", e, log);
		}
	}

	/**
	 * Retrieves the class RunnableProcess according to informed bean in spring
	 * 
	 * @param processSchedule
	 * @return RunnableProcess
	 * @throws DbLibException
	 */
	private RunnableProcess getRunnableProcess(ProcessSchedule processSchedule)
			throws DbLibException {
		return (RunnableProcess) ReflectionUtil.getClassByName(processSchedule
				.getProcessClassName());
	}

	/**
	 * Check if the job exists in quartz
	 * 
	 * @param processSchedule
	 * @return boolean
	 * @throws DbLibException
	 */
	private boolean checkJobExists(ProcessSchedule processSchedule)
			throws DbLibException {

		try {
			boolean jobExists = scheduler
					.checkExists(getJobKeyByName(processSchedule));
			if (jobExists) {

				throw new DbLibException("The process alredy exists");

			}
			return jobExists;
		} catch (SchedulerException e) {
			log.error(e.getMessage());
		}
		return false;

	}

	/**
	 * Method responsible to rescheduleJob
	 * 
	 * @param processSchedule
	 * @throws DbLibException
	 */
	private void rescheduleJob(ProcessSchedule processSchedule)
			throws DbLibException {

		try {
			Trigger oldTrigger = scheduler
					.getTrigger(getTriggerKeyByName(processSchedule));

			Trigger newTrigger = getTrigger(processSchedule);
			/**
			 * rare problem of updating a trigger and it finishes just before
			 * the execution reach this point
			 */
			if (oldTrigger != null) {
				scheduler.rescheduleJob(oldTrigger.getKey(), newTrigger);
			} else {
				startTrigger(processSchedule);
			}

		} catch (SchedulerException e) {

			throw new DbLibException("Failed to reschedule the process", e, log);

		}

	}

	/**
	 * Creates the trigger according to the periodicity of the processSchedule
	 * 
	 * @param processSchedule
	 * @return Trigger
	 * @throws DbLibException
	 */
	private Trigger getTrigger(ProcessSchedule processSchedule)
			throws DbLibException {
		if (Periodicity.DAILY.equals(processSchedule.getPeriodicity())) {

			return createDailyTrigger(processSchedule);
		} else if (Periodicity.WEEKLY.equals(processSchedule.getPeriodicity())) {

			return createWeeklyTrigger(processSchedule);
		} else if (Periodicity.MONTHLY.equals(processSchedule.getPeriodicity())) {

			return createMonthlyTrigger(processSchedule);
		} else if (Periodicity.INTRADAY
				.equals(processSchedule.getPeriodicity())) {

			return createIntraDayTrigger(processSchedule);
		} else {

			return createSingleTrigger(processSchedule);
		}

	}

	/**
	 * Method responsible to disable Job, in fact it makes the call to
	 * unschedule the Job
	 * 
	 * @param processSchedule
	 * @throws DbLibException
	 */
	private void disableJob(ProcessSchedule processSchedule)
			throws DbLibException {
		unscheduleJob(processSchedule, true);
	}

	/**
	 * Method responsible to reactive Job, in fact it makes the call to create a
	 * new schedule
	 * 
	 * @param processSchedule
	 * @throws DbLibException
	 */
	private void reactiveJob(ProcessSchedule processSchedule)
			throws DbLibException {
		if (processSchedule.getNextExecution() == null) {
			processSchedule.setNextExecution(processSchedule.getStartDate());
		}

		startTrigger(processSchedule);
	}

	/**
	 * JobKey is the unique key for identifying a job. Get the JobKey by the
	 * name of the processSchedule.
	 * 
	 * @param processSchedule
	 * @return JobKey
	 */
	private JobKey getJobKeyByName(ProcessSchedule processSchedule) {
		return new JobKey(processSchedule.getName());
	}

	/**
	 * TriggerKey is the unique key for identifying a trigger. Get the
	 * TriggerKey by the name of the processSchedule
	 * 
	 * @return TriggerKey
	 * @param processSchedule
	 */
	private TriggerKey getTriggerKeyByName(ProcessSchedule processSchedule) {
		return new TriggerKey(processSchedule.getName());
	}

	/**
	 * verifies that the process can run according to business rule
	 * 
	 * @param processSchedule
	 * @return
	 */
	public boolean canProcess(ProcessSchedule processSchedule) {
		long tenMinutes = 10 * 60000;
		Date dateToCompare = new Date(System.currentTimeMillis() - tenMinutes);

		if (processSchedule.getNextExecution().before(dateToCompare)) {

			if (processSchedule.getMaxTimeToProcess() == 0) {
				return false;
			}
			Calendar calendar = Calendar.getInstance();

			calendar.setTime(dateToCompare);

			int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

			calendar.setTime(processSchedule.getStartDate());
			int startHour = calendar.get(Calendar.HOUR_OF_DAY);

			calendar.add(Calendar.HOUR, processSchedule.getMaxTimeToProcess());
			int endHour = calendar.get(Calendar.HOUR_OF_DAY);

			if (currentHour > startHour && currentHour >= endHour) {

				return false;

			}
		}
		return true;
	}

}
