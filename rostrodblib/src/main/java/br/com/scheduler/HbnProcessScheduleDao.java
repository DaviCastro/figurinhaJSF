package br.com.scheduler;

import br.com.dao.HbnDao;
import br.com.exception.DbLibException;

/**
 * Classe de implementaçao do DAO<POJO do hibernate que deve implementar a sua
 * interface que pode possuir metodos adicionais ou herdados de dao<T> e a
 * implementaçao deses metodos deve ficar aqui.
 * 
 * Deve ser tipado com a o pojo em questao para que o generics funcione.
 * 
 * 
 * 
 */

public class HbnProcessScheduleDao extends HbnDao<ProcessSchedule> implements
		ProcessScheduleDao {

	public HbnProcessScheduleDao(String banco) throws DbLibException {
		super(banco);
		// TODO Auto-generated constructor stub
	}

}
