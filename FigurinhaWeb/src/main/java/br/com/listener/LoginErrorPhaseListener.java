package br.com.listener;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.WebAttributes;

import br.com.util.FacesUtil;
import br.com.util.Message;

/**
 * 
 * Listener com principal objetivo de buscar erros de autenticação e gerar
 * mensagens de validação para o usuario
 * 
 */
public class LoginErrorPhaseListener implements PhaseListener {

	protected final Log logger = LogFactory.getLog(getClass());
	/**
	 * 
	 */
	private static final long serialVersionUID = -7661473606020523L;

	@Override
	public void afterPhase(PhaseEvent event) {

	}

	/**
	 * Recupera se ocorreu algum erro de autenticação e gera mensagem na tela.
	 */
	@Override
	public void beforePhase(PhaseEvent event) {

		Exception e = (Exception) FacesContext.getCurrentInstance().getExternalContext()
				.getSessionMap().get(WebAttributes.AUTHENTICATION_EXCEPTION);

		if (e instanceof BadCredentialsException) {

			FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
					.put(WebAttributes.AUTHENTICATION_EXCEPTION, null);
			FacesUtil.addMessage(new Message("mensagem.autenticacao", FacesMessage.SEVERITY_ERROR,
					null, true));
		}

	}

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.RENDER_RESPONSE;
	}

}
