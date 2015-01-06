package br.com.scheduler;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerListener;

/**
 * 
 * @author davidson
 * @version 1.0
 */
public class StandardTriggerListener implements TriggerListener {

	/**
	 * Logger.
	 */
	private static Logger logger = Logger
			.getLogger(StandardTriggerListener.class);

	
	public String getName() {
		return "triggerListener";
	}

	
	public void triggerFired(Trigger trigger, JobExecutionContext context) {
		logger.info("Trigger fired " + trigger.getKey());
	}

	
	/**
	 * return true when you need veto job execution
	 */
	public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public void triggerMisfired(Trigger trigger) {
		logger.info("The trigger can not be started because your date and processing window are in disagreement with the current date,"
				+ "the process will start on next valid execution according to their frequency, process: "
				+ trigger.getKey());
	}

	
	public void triggerComplete(Trigger trigger, JobExecutionContext context,
			CompletedExecutionInstruction triggerInstructionCode) {
		logger.info("Trigger complete" + trigger.getKey());
	}

}
