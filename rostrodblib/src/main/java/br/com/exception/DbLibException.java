package br.com.exception;

import org.apache.log4j.Logger;

public class DbLibException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2547295831746162558L;
	
	
	
	public DbLibException(String causa) {
		super(causa);
	}
	public DbLibException(String causa,Throwable e) {
		super(causa, e);
	}
	
	public DbLibException(String causa,Throwable e,Logger log){
		super(causa,e);
		log.error(causa);
	}
	
	

}
