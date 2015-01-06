package br.com.controller;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang.StringUtils;

import br.com.pojo.Exemplo;
import br.com.service.ExemploService;
import br.com.util.FacesUtil;
import br.com.util.Message;

@ManagedBean(name = "exemploBean")
@ViewScoped
/**
 * Classe bean que devera extender genericBean para conseguir 
 * executar os metodos padroes como save,update
 * Na tipagem de GenericBean dever� ser informado o pojo Deste Bean e o Service que trata as regras de negocio do mesmo.
 */
public class ExemploBean extends GenericBean<Exemplo, ExemploService> {

	public ExemploBean() {
		/**
		 * O contrutor do super deve conter imprescindivelmente o nome da
		 * implementa�ao do Service referente a classe pojo. O mesmo dever�
		 * estar na ServiceFactory
		 */
		super();

	}

	@Override
	public void messageSuccesSave() {
		String msgBundle = null;
		switch (state) {
		case ADD_STATE:
			msgBundle= "mensagem.exemplo.registrado";
			break;
		case EDIT_STATE:
			msgBundle = "mensagem.exemplo.atualizado";
			break;

		}
		if(StringUtils.isNotBlank(msgBundle))
			FacesUtil.addMessage(new Message(msgBundle, FacesMessage.SEVERITY_INFO, new String[]{dataEdit.getNome()}, null, true));
		
	}

	@Override
	public void messageSuccessDelete() {
		FacesUtil.addMessage(new Message("mensagem.exemplo.excluido", FacesMessage.SEVERITY_INFO, new String[]{dataEdit.getNome()}, null, true));
		
	}


}
