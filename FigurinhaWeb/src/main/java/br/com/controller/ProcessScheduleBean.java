package br.com.controller;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import br.com.scheduler.ProcessSchedule;
import br.com.scheduler.ProcessScheduleService;

@ManagedBean
@ApplicationScoped
public class ProcessScheduleBean extends
		GenericBean<ProcessSchedule, ProcessScheduleService> {

	public ProcessScheduleBean(String service) {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void messageSuccesSave() {
		// TODO Auto-generated method stub

	}

	@Override
	public void messageSuccessDelete() {
		// TODO Auto-generated method stub

	}

}
