package br.com.dao;

import java.util.List;

import br.com.exception.DbLibException;
import br.com.util.OrderUtil;

/**
 * 
 * Inteface que determina quais os metodos padroes que todas as implementacoes
 * de dao deverao possuir indepedente de tecnologia , no caso é utilizado
 * generics logo a implementaçao deve ser metodos fica em uma classe pai e todos
 * os Daos filhos deverao implementar esta interface e extender esta classe pai
 * que possui de fato a implementaçao dos metos
 * 
 * @param <T>
 */
public interface Dao<T> {

	public void merge(T entity) throws DbLibException;

	public void save(T entity) throws DbLibException;

	/**
	 * Metodo para retornar uma lista por example se a entidade nao tiver
	 * valores setados ira trazer todos os registros
	 * 
	 * @param entity
	 * @return
	 * @throws DbLibException
	 */

	public List<T> findByExample(T entity) throws DbLibException;

	/**
	 * Metodo que busca por um exemplo de acordo com um range minimo e maximo
	 * 
	 * @param entity
	 * @param startingAt
	 * @param maxPerPage
	 * @return
	 * @throws DbLibException
	 */
	public List<T> findByExample(T entity, int startingAt, int maxPerPage) throws DbLibException;

	/**
	 * Metodo que busca por um exemplo de acordo com um range minimo,maximo e
	 * ordem
	 * 
	 * @param entity
	 * @param startingAt
	 * @param maxPerPage
	 * @param order
	 * @return
	 * @throws DbLibException
	 */
	public List<T> findByExample(T entity, int startingAt, int maxPerPage, List<OrderUtil> order)
			throws DbLibException;

	/**
	 * Metodo que retorna apenas UM resultado.
	 * 
	 * @param entity
	 * @return
	 * */
	public T findOneByExample(T entity) throws DbLibException;

	/**
	 * Recupera a lista completa de objetos de acordo com o tipo atual do T
	 * 
	 * @return
	 */
	public List<T> findAll() throws DbLibException;

	/**
	 * Recupera o tipo da classe
	 * 
	 * @return
	 */
	/**
	 * Remocao do objeto do banco.
	 * 
	 * @param entity
	 */
	public void remove(T entity) throws DbLibException;

	public int rowCount(T entity) throws DbLibException;
}
