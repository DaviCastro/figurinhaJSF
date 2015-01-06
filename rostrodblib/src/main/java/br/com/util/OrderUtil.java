package br.com.util;
/**
 * 
 * Classe com objetivo de trazer informaçoes de sort
 * da camada de visualizaçao para ser utilizada na camada de persistencia
 *
 */
public class OrderUtil {

	/**
	 * Nome da propiedade a ser utilizada no criteria
	 */
	private String property;

	/**
	 * boolean para decidir a ordem que os objetos serao buscados
	 */
	private boolean ascending;

	public OrderUtil() {
		super();

	}

	public OrderUtil(String property, boolean ascending) {
		super();
		this.property = property;
		this.ascending = ascending;
	}

	public String getProperty() {
		return property;
	}

	public boolean isAscending() {
		return ascending;
	}

	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	public void setProperty(String property) {
		this.property = property;
	}

}
