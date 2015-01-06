package br.com.service;

import br.com.dao.Dao;
import br.com.pojo.Exemplo;

/**
 * 
 * Implementa�ao da interface Exemplo Service onde os metodos padroes da
 * Interface Service estarao implementados na Classe extendida GenericService
 * EXTENDE A INTERFACE DE EXEMPLO SERVICE E A SUA IMPLEMENTACAO ESTA EM
 * GENERICSERVICE
 */

public class ExemploServiceImpl extends GenericService<Exemplo, Dao<Exemplo>> implements
		ExemploService {



	@Override
	/**
	 * Metodo utilizado para valida�oes de regra de Negocio
	 */
	public boolean consiste() {
		// TODO Auto-generated method stub
		return false;
	}

}
