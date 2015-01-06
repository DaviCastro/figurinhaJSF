package br.com.dao;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;

import br.com.annotation.Db4oDao;
import br.com.annotation.HbnDao;

/**
 * 
 * AbstractFactory para obter uma f�brica especifica de uma implementa��o DAO.
 * Baseado em arquivo properties ser� retornado a implementa�ao da Factory de
 * dao utilizada no projeto.
 * 
 * Pode ser utilizada tanto para utilizar tecnologias diferentes exemplo: Se a
 * persistencia utilizada for feita pelo hibernate recupera o DAOFactory do
 * hibernate ou qualquer outra implementa�ao.
 * 
 * Os metodos para recuperar os DAOs do pojo em questao deverao ser declarados
 * como abstracts nesta classe para que as Factorys Filhas implementem.
 */
public class DaoFactory {

	private static DaoFactory singleton;
	protected static final Logger logger = Logger
			.getLogger(DaoFactory.class);

	// TODO PROPERTIES
	// private static String parametro = "DB4O";
	private static String parametro = "HIBERNATE";
	private static Class<? extends Annotation> annotation;

	public static DaoFactory getInstance() throws InstantiationException,
			IllegalAccessException {
		if (singleton == null) {
			singleton = new DaoFactory();
			if (parametro.equals("HIBERNATE")) {
				annotation = HbnDao.class;

			} else if (parametro.equals("DB4O")) {
				annotation = Db4oDao.class;
			}
		}

		return singleton;
	}

	@SuppressWarnings({ "unchecked" })
	public <T> T getDao(T entity) {

		try {

			return (T) (annotation.getName().equals(HbnDao.class.getName()) ? entity
					.getClass()
					.getAnnotation(HbnDao.class)
					.hbnDao()
					.getDeclaredConstructor(String.class)
					.newInstance(
							entity.getClass().getAnnotation(HbnDao.class)
									.banco())
					: entity.getClass()
							.getAnnotation(Db4oDao.class)
							.db4oDao()
							.getDeclaredConstructor(String.class)
							.newInstance(
									entity.getClass()
											.getAnnotation(Db4oDao.class)
											.banco()));

		} catch (InstantiationException e) {
			logger.error(e);
		} catch (IllegalAccessException e) {
			logger.error(e);
		} catch (IllegalArgumentException e) {
			logger.error(e);
		} catch (InvocationTargetException e) {
			logger.error(e);
		} catch (NoSuchMethodException e) {
			logger.error(e);
		} catch (SecurityException e) {
			logger.error(e);
		}
		return null;
	}
}
