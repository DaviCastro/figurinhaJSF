package br.com.scheduler;

import br.com.exception.DbLibException;

public interface RunnableProcess {

	public void init(ProcessSchedule processSchedule) throws DbLibException;

	public void run() throws DbLibException;

	public void stop() throws DbLibException;

	public String getName() throws DbLibException;

	public String getFeedBack() throws DbLibException;

}
