package br.com.util;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * Classe que possui metodos estaticos para evitar codificaçao desnecessaria nos
 * Beans.
 * 
 */
public class FacesUtil implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Recupera instancia do FacesContext
	 * 
	 * @return
	 */
	public static FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}

	/**
	 * MÃ©todo utilizado para trocar o idioma
	 * */
	public static UIViewRoot getViewRoot() {
		FacesContext facesContext = getFacesContext();
		UIViewRoot uiViewRoot = null;

		if (facesContext != null) {
			uiViewRoot = facesContext.getViewRoot();
		}

		return uiViewRoot;
	}

	/**
	 * Metodo responsavel por receber uma lista de objetos messages e fazer a
	 * chamada ao addMessage para adicionalos no faceContext
	 * 
	 * @param messages
	 */
	public static void processMessages(List<Message> messages) {

		for (Message message : messages) {
			addMessage(message);
		}

	}

	/**
	 * Metodo para adicionar a mensagem no faceContext passando a string
	 * severity e o o messages a ser renderizado de forma direta, recomendado
	 * encapsular essas informaçoes no objeto message
	 * 
	 * @param forComponent
	 * @param message
	 * @param severity
	 */
	@Deprecated
	public static void addMessage(String forComponent, String message, Severity severity) {
		FacesUtil.getFacesContext().addMessage(forComponent,
				new FacesMessage(severity, message, ""));
	}

	/**
	 * Metodo responsavel por adicionar a mensagem no faceContext Baseado em um
	 * objeto tipo message
	 * 
	 * @param message
	 */
	public static void addMessage(Message message) {
		/**
		 * Verifica se o conteudo da mensagem deve ou nao pesquisar no bundle
		 */
		if (message.isMsgBundle()) {
			ResourceBundle bundle = ResourceBundle.getBundle(message.getBundle(), getViewRoot()
					.getLocale());

			MessageFormat format = new MessageFormat(bundle.getString(message.getKey()));

			/**
			 * Verifica se a mensagem possui parametros
			 */
			if (message.getArgs() != null && message.getArgs().length > 0) {
				/**
				 * Verifica se os parametros deverao ser buscados no bundle
				 */
				if (message.isArgI18N()) {

					String[] _args = new String[message.getArgs().length];
					for (int i = 0; i < message.getArgs().length; i++)
						_args[i] = bundle.getString(message.getArgs()[i].toString());
					message.setArgs(_args);
				}

			}
			message.setKey(format.format(message.getArgs()));

		}

		FacesMessage facesMessage = new FacesMessage(message.getSeverity(), message.getKey(), "");

		FacesContext.getCurrentInstance().addMessage(
				message.getComponent() == null ? null : message.getComponent(), facesMessage);
	}

	public static HttpServletRequest getHttpServletRequest() {
		Object request = getRequest();
		HttpServletRequest servletRequest = null;

		if (request != null) {
			servletRequest = (HttpServletRequest) request;
		}

		return servletRequest;
	}

	public static HttpServletResponse getHttpServletResponse() {
		Object response = getResponse();
		HttpServletResponse servletResponse = null;

		if (response != null) {
			servletResponse = (HttpServletResponse) response;
		}

		return servletResponse;
	}

	public static Object getRequest() {
		ExternalContext externalContext = getExternalContext();
		Object request = null;

		if (externalContext != null) {
			request = externalContext.getRequest();
		}

		return request;
	}

	public static Object getResponse() {
		ExternalContext externalContext = getExternalContext();
		Object response = null;

		if (externalContext != null) {
			response = externalContext.getResponse();
		}

		return response;
	}
	
	public static ExternalContext getExternalContext() {
		FacesContext facesContext = getFacesContext();
		ExternalContext externalContext = null;

		if (facesContext != null) {
			externalContext = facesContext.getExternalContext();
		}

		return externalContext;
	}

}