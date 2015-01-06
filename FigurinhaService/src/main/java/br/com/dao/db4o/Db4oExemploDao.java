package br.com.dao.db4o;

import br.com.dao.Db4oDao;
import br.com.interfaceDao.ExemploDao;
import br.com.pojo.Exemplo;


public class Db4oExemploDao extends Db4oDao<Exemplo> implements ExemploDao {

	public Db4oExemploDao(String banco) {
		super(banco);
	}

}
