package br.com.dao;

import java.io.Serializable;

/**
 * 
 * Classes pojo deverao implementar esta interface e implementar o metodo getId
 * com a finalidade de se ter a chave primaria no momento em que estiver lidando
 * com o objeto generico exemploT entity entity.getId(tornar possivel este tipo
 * de operacao)
 * 
 * 
 */
public interface Identifiable {
	public Serializable getId();
}
