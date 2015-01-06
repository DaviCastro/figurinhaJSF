package br.com.service;

import org.apache.log4j.Logger;

import br.com.scheduler.ProcessScheduleServiceImpl;

public class ServiceFactory {

	protected static final Logger logger = Logger
			.getLogger(ServiceFactory.class);

	private static ProcessScheduleServiceImpl processScheduleServiceImpl;

	public static <T> Service<?> getService(Class<?> entityClazz) {
		try {
			if (entityClazz
					.getAnnotation(br.com.annotation.Service.class).service()
					.equals(ProcessScheduleServiceImpl.class)) {
				if (processScheduleServiceImpl == null) {
					processScheduleServiceImpl = new ProcessScheduleServiceImpl();
				}
				return processScheduleServiceImpl;
			}

			return (Service<?>) entityClazz
					.getAnnotation(br.com.annotation.Service.class).service()
					.newInstance();

		} catch (InstantiationException e) {
			logger.error(e);
		} catch (IllegalAccessException e) {
			logger.error(e);
		}
		return null;

	}
}
