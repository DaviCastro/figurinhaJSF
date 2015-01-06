package br.com.dao.hbn;

import br.com.dao.HbnDao;
import br.com.exception.DbLibException;
import br.com.interfaceDao.UsuarioDao;
import br.com.pojo.Usuario;

public class HbnUsuarioDao extends HbnDao<Usuario> implements UsuarioDao {

	public HbnUsuarioDao(String banco) throws DbLibException {
		super(banco);
	}


}
