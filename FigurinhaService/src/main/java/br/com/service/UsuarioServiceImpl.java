package br.com.service;

import br.com.dao.Dao;
import br.com.pojo.Usuario;

public class UsuarioServiceImpl extends GenericService<Usuario, Dao<Usuario>>
		implements UsuarioService {

	@Override
	public boolean consiste() {
		// TODO Auto-generated method stub
		return false;
	}

}
