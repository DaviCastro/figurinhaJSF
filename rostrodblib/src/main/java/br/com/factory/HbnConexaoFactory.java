package br.com.factory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;

import br.com.exception.DbLibException;

public class HbnConexaoFactory  {
	
	private static final Logger logger = Logger.getLogger(HbnConexaoFactory.class); 
    public static EntityManager adquirirConexao(String banco) throws DbLibException  {
    	try{
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(banco);
        EntityManager em = emf.createEntityManager();
        return em;
    	}catch (Exception e) {
    		logger.error(e.getMessage(), e);
			throw new DbLibException(e.getMessage(), e);
		}
    }

}
