package br.com.scheduler;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.UnableToInterruptJobException;

/**
 * Class responsible for interrupt a process that reached maximum processing
 * time
 * 
 * @author davidson
 * @version 1.0
 */
public class StandardJob implements Job {

	/**
	 * JobKey
	 */
	private JobKey key;

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(StandardJob.class);

	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		/**
		 * Retrieves the key injected by the StandardJobListener
		 */
		key = (JobKey) context.getJobDetail().getJobDataMap()
				.get(JobKey.class.getName());
		try {
			context.getScheduler().interrupt(key);
		} catch (UnableToInterruptJobException e) {
			logger.error("Unable to interrupt job " + key);

		}

	}

}
