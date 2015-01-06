package br.com.service;

import java.util.List;

import br.com.exception.DbLibException;
import br.com.util.OrderUtil;

/**
 * Interface para operações de CRUD.
 * 
 * Para utilizar voce deve definir uma interface service do pojo em questao que
 * extendera essa
 * 
 * @param <T>
 */
public interface Service<T> {

	public boolean save(T entity) throws DbLibException;

	public boolean delete(T entity) throws DbLibException;

	public List<T> search(T entity) throws DbLibException;
	
	public T searchOne (T entity )throws DbLibException;

	public List<T> search(T entity, int startingAt, int maxPerPage)
			throws DbLibException;

	public List<T> search(T entity, int startingAt, int maxPerPage,
			List<OrderUtil> order) throws DbLibException;

	public List<T> findAll() throws DbLibException;

	public int rowCount(T entity) throws DbLibException;

	public void beforeInsert(T entity) throws DbLibException;

	public void afterInsert(T entity) throws DbLibException;

	public void beforeDelete(T entity) throws DbLibException;

	public void afterDelete(T entity) throws DbLibException;

	public void beforeSearch(T entity) throws DbLibException;

	public void afterSearch(T entity) throws DbLibException;

	public void beforeUpdate(T entity) throws DbLibException;

	public void afterUpdate(T entity) throws DbLibException;

}
