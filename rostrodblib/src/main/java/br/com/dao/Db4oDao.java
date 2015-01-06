package br.com.dao;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.apache.log4j.Logger;

import br.com.conexao.Db4oConexao;
import br.com.exception.DbLibException;
import br.com.factory.Db4oConexaoFactory;
import br.com.util.OrderUtil;

import com.db4o.ObjectContainer;

public class Db4oDao<T> implements Dao<T> {

	private Db4oConexao conexao;

	public Db4oConexao getConexao() {
		return conexao;
	}

	protected static final Logger logger = Logger.getLogger(Db4oDao.class);

	public Db4oDao(String banco) {
		try {
			conexao = Db4oConexaoFactory.adquirirConexao(banco);
		} catch (DbLibException e) {
			// TODO Colocar logs
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}

	}

	/**
	 * Metodo que faz insert e update no banco. Se a referencia do objeto
	 * existir no banco realiza update senn�o insert
	 * 
	 * @param entity
	 */
	public void save(T entity) {
		ObjectContainer sessao = conexao.getSessao();
		sessao.store(entity);
		sessao.close();
	}

	/**
	 * Metodo que retorna o resultado do metodo get, apenas com um nome mais
	 * legivel
	 * 
	 * @param entity
	 * @return
	 * */
	public List<T> findByExample(T entity) {

		ObjectContainer sessao = conexao.getSessao();
		List<T> retorno = sessao.queryByExample(entity);
		sessao.close();
		return retorno;
	}

	/**
	 * Metodo que retorna apenas UM resultado.
	 * 
	 * @param entity
	 * @return
	 * */
	public T findOneByExample(T entity) {
		if (findByExample(entity) != null)
			return findByExample(entity).get(0);
		return null;
	}

	/**
	 * Recupera a lista completa de objetos de acordo com o tipo atual do T
	 * 
	 * @return
	 */
	public List<T> findAll() {
		ObjectContainer sessao = conexao.getSessao();
		List<T> retorno = sessao.queryByExample(getTypeClass());
		sessao.close();
		return retorno;

	}

	/**
	 * Remocao do objeto do banco.
	 * 
	 * @param entity
	 */
	public void remove(T entity) {
		ObjectContainer sessao = conexao.getSessao();
		sessao.delete(entity);
		sessao.close();

	}

	@SuppressWarnings("unchecked")
	/**
	 * Recupera o tipo da classe 
	 * @return
	 */
	private Class<T> getTypeClass() {
		Class<T> clazz = (Class<T>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
		return clazz;
	}

	public void merge(T entity) {
		save(entity);

	}

	public List<T> findByExample(T entity, int startingAt, int maxPerPage) throws DbLibException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<T> findByExample(T entity, int startingAt, int maxPerPage, List<OrderUtil> order)
			throws DbLibException {
		// TODO Auto-generated method stub
		return null;
	}

	public int rowCount(T entity) throws DbLibException {
		// TODO Auto-generated method stub
		return 0;
	}

	


}
