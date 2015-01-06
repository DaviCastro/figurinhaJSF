package br.com.scheduler;

import java.io.Serializable;
import java.util.Date;

import br.com.dao.Identifiable;

public class ProcessSchedule implements Serializable, Identifiable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	private String name;
	private String processClassName;
	private String data;
	private String description;
	private int interval;
	private Date startDate;
	private Date endDate;
	private Periodicity periodicity;
	private ActionOnError actionOnError;
	private Status status;
	private int maxTimeToProcess;
	private Date nextExecution;
	private String feedBack;

	public enum Status {
		SCHEDULED("S"), RUNNING("R"), ERROR("E"), FINISHED("F"), DISABLED("D"), REACTIVE(
				"R"), RESCHEDULE("L"),COMPLETE("C");
		private String value;

		private Status(String value) {
			this.setValue(value);
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

	public enum Periodicity {
		DAILY("D"), WEEKLY("W"), MONTHLY("M"), INTRADAY("I"), SINGLE("S");
		private String value;

		private Periodicity(String value) {
			this.setValue(value);
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

	}

	public enum ActionOnError {
		STOP("S"), RESCHEDULE("R");

		private String value;

		private ActionOnError(String value) {
			this.setValue(value);
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProcessClassName() {
		return processClassName;
	}

	public void setProcessClassName(String processClassName) {
		this.processClassName = processClassName;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getMaxTimeToProcess() {
		return maxTimeToProcess;
	}

	public void setMaxTimeToProcess(int maxTimeToProcess) {
		this.maxTimeToProcess = maxTimeToProcess;
	}

	public String getFeedBack() {
		return feedBack;
	}

	public void setFeedBack(String feedBack) {
		this.feedBack = feedBack;
	}

	public Date getNextExecution() {
		return nextExecution;
	}

	public void setNextExecution(Date nextExecution) {
		this.nextExecution = nextExecution;
	}

	public Periodicity getPeriodicity() {
		return periodicity;
	}

	public void setPeriodicity(Periodicity periodicity) {
		this.periodicity = periodicity;
	}

	public ActionOnError getActionOnError() {
		return actionOnError;
	}

	public void setActionOnError(ActionOnError actionOnError) {
		this.actionOnError = actionOnError;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

}