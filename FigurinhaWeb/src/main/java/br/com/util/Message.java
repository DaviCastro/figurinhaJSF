package br.com.util;

import javax.faces.application.FacesMessage.Severity;

/**
 * 
 * Classe responsanvel por agrupar as informaçoes necessarias para adiconar
 * mensagem no faceContext
 * 
 */
public class Message  {

	private String key;
	private Severity severity;
	private String bundle = "messages";
	private String[] args;
	private String component;
	private boolean argI18N = false;
	private boolean msgBundle = false;

	public Message(String key, Severity severity) {
		super();
		this.key = key;
		this.severity = severity;
	}

	public Message(String key, Severity severity, String component) {
		super();
		this.key = key;
		this.severity = severity;
		this.component = component;
	}
	
	public Message(String key, Severity severity, String component,boolean msgBundle) {
		super();
		this.key = key;
		this.severity = severity;
		this.component = component;
		this.msgBundle = msgBundle;
	}

	public Message(String key, Severity severity, String[] args) {
		super();
		this.key = key;
		this.severity = severity;
		this.args = args;
	}

	public Message(String key, Severity severity, String bundle, String[] args, String component,
			boolean argI18N, boolean msgBundle) {
		super();
		this.key = key;
		this.severity = severity;
		this.bundle = bundle;
		this.args = args;
		this.component = component;
		this.argI18N = argI18N;
		this.msgBundle = msgBundle;
	}

	public Message(String key, Severity severity, String[] args, String component, boolean msgBundle) {
		super();
		this.key = key;
		this.severity = severity;
		this.args = args;
		this.component = component;
		this.msgBundle = msgBundle;
	}

	/**
	 * Apenas para informar se a mensagem ira no bundle ou nao
	 * 
	 * @return
	 */
	public boolean isMsgBundle() {
		return msgBundle;
	}

	public void setMsgBundle(boolean msgBundle) {
		this.msgBundle = msgBundle;
	}

	/**
	 * Apenas para informar onde a mensagem será renderizada
	 * 
	 * @return
	 */
	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	/**
	 * Informaçoes de gravidade da mensagem Info,warn etc
	 * 
	 * @return
	 */
	public Severity getSeverity() {
		return severity;
	}

	public void setSeverity(Severity severity) {
		this.severity = severity;
	}

	/**
	 * Possui a chave para pesquisa no bundle ou a mensagem a ser adicionada
	 * 
	 * @return
	 */
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Possui o bundle a ser utilizado para pesquisa da key
	 * 
	 * @return
	 */
	public String getBundle() {
		return bundle;
	}

	public void setBundle(String bundle) {
		this.bundle = bundle;
	}

	/**
	 * Boolean para informar se o o argumento será buscado do bundle ou nao
	 * 
	 * @return
	 */
	public boolean isArgI18N() {
		return argI18N;
	}

	public void setArgI18N(boolean argI18N) {
		this.argI18N = argI18N;
	}

	/**
	 * Argumentos a a ser adicionado a mensagem
	 * 
	 * @return
	 */
	public String[] getArgs() {
		return args;
	}

	public void setArgs(String[] args) {
		this.args = args;
	}

}
