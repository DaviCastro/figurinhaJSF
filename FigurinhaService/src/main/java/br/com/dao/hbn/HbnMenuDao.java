package br.com.dao.hbn;

import br.com.dao.HbnDao;
import br.com.exception.DbLibException;
import br.com.interfaceDao.MenuDao;
import br.com.pojo.Menu;

public class HbnMenuDao extends HbnDao<Menu> implements MenuDao {

	public HbnMenuDao(String banco) throws DbLibException {
		super(banco);
		// TODO Auto-generated constructor stub
	}

}
