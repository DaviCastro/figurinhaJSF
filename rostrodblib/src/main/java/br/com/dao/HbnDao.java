package br.com.dao;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;

import br.com.exception.DbLibException;
import br.com.factory.HbnConexaoFactory;
import br.com.util.OrderUtil;

/**
 * 
 * Classe que possui a implementaçao dos metodos necessarios para trabalhar com
 * o dao do Hibernate como save,delete,findAll
 * 
 * @param <T>
 */
public class HbnDao<T> implements Dao<T> {

	protected EntityManager conexao;
	private Class<T> persistentClass;

	protected static final Logger logger = Logger.getLogger(HbnDao.class);

	/**
	 * A conexao é construida no momento em que a classe é carregada As classes
	 * filhas deverao informar o persistent unit utilizado, basicamente sera
	 * informado em qual banco essas informaçoes deverao ser persitidas deixando
	 * assim flexivel no caso de trabalhar com bancos diferentes.
	 * 
	 * @param banco
	 * @throws DbLibException
	 */
	public HbnDao(String banco) throws DbLibException {

		conexao = HbnConexaoFactory.adquirirConexao(banco);

	}

	/**
	 * Metodo responsavel por persistir(Insert) as informaçoes no banco com base
	 * em uma entidade generica é necessario abrir e fechar transacao para que o
	 * objeto seja de fato persistido. e lançado a excecao RostroDbLibException,
	 * onde a mesma deverá ser tratada na parte visual
	 */
	public void save(T entity) throws DbLibException {
		try {

			conexao.getTransaction().begin();
			conexao.persist(entity);
			conexao.flush();
			conexao.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DbLibException("Erro ao executar metodo save", e);
		}
	}

	@SuppressWarnings("unchecked")
	/**
	 * Metodo que faz a busca por um objeto de  exemplo onde o é utilizado a propiedade like ou seja será buscado
	 * valores que correspondem do comeco da String para o final. Serao buscados todos os objetos que estirem dentro do padrao
	 */
	public List<T> findByExample(T entity) throws DbLibException {
		try {
			Session hibernateSession = (Session) conexao.getDelegate();
			/**
			 * Pulo do gato
			 * para hibernate nao manter cache da entidade pesquisada
			 */
			conexao.detach(entity);
			Criteria criteria = hibernateSession.createCriteria(getTypeClass());
			criteria.add(Example.create(entity).ignoreCase().enableLike(MatchMode.START));
			return (List<T>) criteria.list();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DbLibException("Erro ao executar metodo findByExample", e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<T> findByExample(T entity, int startingAt, int maxPerPage) throws DbLibException {
		try {
			Session hibernateSession = (Session) conexao.getDelegate();
			/**
			 * Pulo do gato
			 * para hibernate nao manter cache da entidade pesquisada
			 */
			conexao.detach(entity);
			Criteria criteria = hibernateSession.createCriteria(getTypeClass());
			criteria.add(Example.create(entity).ignoreCase().enableLike(MatchMode.START));
			criteria.setFirstResult(startingAt);
			criteria.setMaxResults(maxPerPage);
			return (List<T>) criteria.list();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DbLibException("Erro ao executar metodo findByExample", e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<T> findByExample(T entity, int startingAt, int maxPerPage, List<OrderUtil> order)
			throws DbLibException {
		try {
			Session hibernateSession = (Session) conexao.getDelegate();
			/**
			 * Pulo do gato
			 * para hibernate nao manter cache da entidade pesquisada
			 */
			conexao.detach(entity);
			Criteria criteria = hibernateSession.createCriteria(getTypeClass());
			criteria.add(Example.create(entity).ignoreCase().enableLike(MatchMode.START));
			criteria.setFirstResult(startingAt);
			criteria.setMaxResults(maxPerPage);
			if (order != null && order.size() != 0)
				for (OrderUtil orderUtil : order) {
					criteria.addOrder(orderUtil.isAscending() ? Order.asc(orderUtil.getProperty())
							: Order.desc(orderUtil.getProperty()));
				}

			return (List<T>) criteria.list();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DbLibException("Erro ao executar metodo findByExample", e);
		}
	}

	@SuppressWarnings("unchecked")
	/**
	 * Metodo que faz a busca de todo os registros do objeto.
	 */
	public List<T> findAll() throws DbLibException {
		try {
			Session hibernateSession = (Session) conexao.getDelegate();
			Criteria criteria = hibernateSession.createCriteria(getTypeClass());
			return (List<T>) criteria.list();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DbLibException("Erro ao executar metodo findByExample", e);
		}
	}

	/**
	 * Metodo que faz a deleçao(Delete) de registros é necessario que a entidade
	 * esteja managed para que o mesmo seja deletado, o meio mais comum para
	 * fazer isso é chamar o merge antes do delete
	 */
	public void remove(T entity) throws DbLibException {
		try {
			conexao.getTransaction().begin();
			entity = conexao.merge(entity);
			conexao.remove(entity);
			conexao.flush();
			conexao.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DbLibException("Erro ao executar metodo remove", e);
		}

	}

	@SuppressWarnings({ "unchecked" })
	/**
	 * Metodo responsavel por recuperar a classe do tipo <T>
	 * 
	 * @return
	 */
	private Class<T> getTypeClass() {
		if (this.persistentClass == null) {
			this.persistentClass = (Class<T>) ((ParameterizedType) getClass()
					.getGenericSuperclass()).getActualTypeArguments()[0];
		}
		return this.persistentClass;
	}

	@SuppressWarnings("unchecked")
	/**
	 * Metodo que faz a busca por um objeto de  exemplo onde o é utilizado a propiedade like ou seja será buscado
	 * valores que correspondem do comeco da String para o final. Será buscado apenas o primeiro objeto que estiver dentro do padrao
	 */
	public T findOneByExample(T entity) throws DbLibException {
		try {
			Session hibernateSession = (Session) conexao.getDelegate();
			Criteria criteria = hibernateSession.createCriteria(getTypeClass());
			criteria.add(Example.create(entity));
			return (T) criteria.uniqueResult();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DbLibException("Erro ao executar metodo findOneByExample", e);
		}
	}

	/**
	 * Metodo responsavel por favor o merge das informaçoes do objeto com as
	 * informaçoes contidas no banco(update)
	 */
	public void merge(T entity) throws DbLibException {
		try {
			conexao.getTransaction().begin();
			conexao.merge(entity);
			conexao.flush();
			conexao.getTransaction().commit();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DbLibException("Erro ao executar metodo merge", e);
		}

	}

	/**
	 * Metodo que retorna a quantidadee de linhas de acordo com o example
	 * fornecido
	 */
	public int rowCount(T entity) throws DbLibException {
		Session hibernateSession = (Session) conexao.getDelegate();
		Criteria criteria = hibernateSession.createCriteria(getTypeClass());
		criteria.add(Example.create(entity).ignoreCase().enableLike(MatchMode.START));
		return ((Number) criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}
}
