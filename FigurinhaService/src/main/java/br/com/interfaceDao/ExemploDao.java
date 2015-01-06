package br.com.interfaceDao;

import br.com.dao.Dao;
import br.com.pojo.Exemplo;

/**
 * Interface que extende os metodos padroes defindos para todos os tipos de dao
 * onde qualquer metodo especifico para este DAO devera ser declarado aqui.
 * Programação orientado a interface para que a mesma possa receber diferentes
 * implementaçoes na camada de cima. Deve ser tipada com o Pojo em questao para
 * que generics funcione.
 */
public interface ExemploDao extends Dao<Exemplo> {

}
