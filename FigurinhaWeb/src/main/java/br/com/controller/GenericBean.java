package br.com.controller;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.primefaces.model.LazyDataModel;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.ContextLoaderListener;

import br.com.annotation.AutoComplete;
import br.com.dao.Identifiable;
import br.com.exception.DbLibException;
import br.com.service.Service;
import br.com.service.ServiceFactory;
import br.com.util.DataState;
import br.com.util.FacesUtil;
import br.com.util.GenericLazyList;
import br.com.util.Message;

/**
 * 
 * Classe que representa o Bean Generico possui todos os metodos necessarios
 * para o crud de um bean. Classe tipada com o tipo T que no caso ser� o objeto
 * pojo em questao e tambem o Service do tipo<Pojo> para que o mesmo pegue a
 * referencia do service. Nessa camada Dever� conter apenas valida��es de tela
 * as valida��es de regra de negocio ficar�o por encargo do Service recebido.
 * 
 * @param <T>
 * @param <DataService>
 */
public abstract class GenericBean<T extends Identifiable, DataService extends Service<T>> {

	private final static String FORMULARIOPADRAO = "principal";

	/**
	 * Objeto utilizado para pesquisa
	 */
	protected T dataSearch;
	/**
	 * Objeto manipulado tanto para novos cadastros e atualiza�oes em ja
	 * entidades ja existentes
	 */
	protected T dataEdit;
	// protected List<T> listData;
	/**
	 * Objeto contendo implementa�ao de regra de negocio
	 */
	protected DataService dataService;
	/**
	 * Objeto que � utilizado para a true pagination
	 */
	protected LazyDataModel<T> listLazyDataModel;

	/**
	 * Enum responsavel por marcar o estado de atualiza��o ou de adi��o
	 */
	protected DataState state;

	/**
	 * Objeto que recebe as mensagens de valida��o dos metodos preSave e
	 * preSearch
	 */
	private List<Message> messages = new ArrayList<Message>();

	@SuppressWarnings("unchecked")
	/**
	 * Construtor que deve receber qual o a classe de regra de negocio do bean filho
	 * para que o mesmo possa fazer o "lookup"
	 * @param service
	 */
	public GenericBean() {
		super();

		dataSearch = createData();
		dataEdit = createData();
		// listData = new ArrayList<T>();
		/**
		 * Nesse momento � buscado a classe de regra de negocio que deve ser
		 * informada no Bean
		 */
		dataService = (DataService) ServiceFactory.getService(dataEdit.getClass());
	}

	/**
	 * Metodo responsavel por limpar as informa�oes contidas no objeto dataEdit
	 * 
	 * @param event
	 */
	public void preSave(ActionEvent event) {
		dataEdit = createData();
		state = DataState.ADD_STATE;
	}

	public void preEdit(ActionEvent event) {
		state = DataState.EDIT_STATE;
	}

	/**
	 * Metodo responsavel pelo cadastro de registros Assim que o registro �
	 * salvo no banco � feito a pesquisa com base nos valores do dataSearch, se
	 * o mesmo estive vazio busca todos os registros.
	 */
	public void save() {
		try {

			messages = validateSave(dataEdit);
			if (messages != null && !messages.isEmpty()) {
				FacesUtil.processMessages(messages);
				return;
			}
			dataService.save(dataEdit);
			dataSearch = createData();
			search();
			messageSuccesSave();
		} catch (DbLibException e) {
			e.printStackTrace();
			// TODO mensagem de erro
		}

	}

	/**
	 * Metodo que deve ser implementado pela classe filha para informar qual a
	 * mensagem de sucesso de cadastro ser� renderizada
	 */
	public abstract void messageSuccesSave();

	/**
	 * Metodo que deve ser implementado pela classe filha para informar qual a
	 * mensagem de sucesso de dele��o ser� renderizada
	 */
	public abstract void messageSuccessDelete();

	/**
	 * Metodo responsavel pela dele�ao de registros
	 * 
	 * @param ev
	 */
	public void delete(ActionEvent ev) {
		try {

			dataService.delete(dataEdit);
			// listData.remove(dataEdit);
			messageSuccessDelete();
		} catch (DbLibException e) {
			e.printStackTrace();
			// TODO mensagem de erro
		}

	}

	/**
	 * Metodo responsavel por realizar a pesquisa onde no mesmo ser� chamado a o
	 * validate Search
	 */
	public void search() {

		messages = validateSearch(dataSearch);
		if (messages != null && !messages.isEmpty()) {
			FacesUtil.processMessages(messages);
			return;
		}
		// listData = dataService.search(dataSearch);

		listLazyDataModel = new GenericLazyList<T>(dataService, dataSearch);
		/**
		 * Para o caso de nao ser lazyList a verifica��o de registros deve ficar
		 * aqui neste local. Descomentar esse bloco. if (listData.size() == 0) {
		 * Message message = new Message("msg.nenhum.registro",
		 * FacesMessage.SEVERITY_WARN, null, true);
		 * FacesUtil.addMessage(message); }
		 */

	}

	/**
	 * Metodod e validacao de cadastro deve ser sobrescrevido na classe filha
	 * que possuira sua implementa�ao
	 * 
	 * @param dataSearch
	 * @return
	 */
	protected List<Message> validateSave(T dataSearch) {
		return new ArrayList<Message>();
	}

	/**
	 * Metodod e validacao de pesquisa deve ser sobrescrevido na classe filha
	 * que possuira sua implementa�ao
	 * 
	 * @param dataSearch
	 * @return
	 */
	protected List<Message> validateSearch(T dataEdit) {
		return new ArrayList<Message>();
	}

	/**
	 * Metodo responsavel por criar um objeto do tipo <T>
	 * 
	 * @return
	 */
	protected T createData() {
		try {
			return getObjectClass().newInstance();
		} catch (InstantiationException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;

		}
	}

	/**
	 * Metodo responsavel por realizar o autoComplete com base na anotacao
	 * AutoComplete da classe pojo
	 * 
	 * @param nome
	 * @return
	 */
	public List<T> autoComplete(String nome) {
		Class<T> clazz = getObjectClass();

		String prefixo = "set";
		try {
			/**
			 * Percorre o campo da classe ate encontrar o campo que possui a
			 * anota��o
			 * 
			 * @AutoComplete assim que encontrar este campo o seta com o valor
			 *               recebido e faz a busca no banco.
			 */
			for (Field field : clazz.getDeclaredFields()) {

				if (field.getAnnotation(AutoComplete.class) != null) {

					Method a = clazz.getMethod(
							prefixo + StringUtils.capitalize(field.getName()),
							field.getType());

					a.invoke(dataSearch, nome);
					return dataService.search(dataSearch);
				}

			}

		} catch (Exception e) {
			return null;
		}
		return null;
	}

	/**
	 * Metodo responsavel por recuperar a classe do tipo <T>
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Class<T> getObjectClass() {
		return (Class<T>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
	}

	protected List<String> getRoles() {
		BeanFactory factory = ContextLoaderListener
				.getCurrentWebApplicationContext();

		RoleHierarchyImpl roleHierarchy = (RoleHierarchyImpl) factory
				.getBean("roleHierarchy");

		Iterator<GrantedAuthority> it = roleHierarchy
				.getReachableGrantedAuthorities(
						SecurityContextHolder.getContext().getAuthentication()
								.getAuthorities()).iterator();

		List<String> roles = new ArrayList<String>();
		while (it.hasNext()) {
			roles.add(it.next().getAuthority());
		}
		return roles;

	}

	public void limparComponentesForm(String formTela) {
		// O padrao e formPesquisa entao null e passado do controller. Caso o
		// form da tela seja diferente deve ser passado o id.
		UIComponent form = FacesContext.getCurrentInstance().getViewRoot()
				.findComponent(formTela == null ? FORMULARIOPADRAO : formTela);
		if (form != null) {
			limparForm(form);
		}
	}

	/**
	 * Limpa os dados dos componentes de edição e de seus filhos,
	 * recursivamente. Checa se o componente é instância de EditableValueHolder
	 * e 'reseta' suas propriedades.
	 * 
	 */
	private void limparForm(UIComponent component) {
		if (component instanceof EditableValueHolder) {
			EditableValueHolder evh = (EditableValueHolder) component;
			evh.resetValue();
		}
		// Dependendo de como se implementa um Composite Component, ele retorna
		// ZERO
		// na busca por filhos. Nesse caso devemos iterar sobre os componentes
		// que o
		// compõe de forma diferente.
		if (UIComponent.isCompositeComponent(component)) {
			@SuppressWarnings("rawtypes")
			Iterator i = component.getFacetsAndChildren();
			while (i.hasNext()) {
				UIComponent comp = (UIComponent) i.next();

				if (comp.getChildCount() > 0) {
					for (UIComponent child : comp.getChildren()) {
						limparForm(child);
					}
				}
			}
		}
		if (component.getChildCount() > 0) {
			for (UIComponent child : component.getChildren()) {
				limparForm(child);
			}
		}
	}

	// public List<T> getListData() {
	// return listData;
	// }
	//
	// public void setListData(List<T> listData) {
	// this.listData = listData;
	// }

	public T getDataSearch() {
		return dataSearch;
	}

	public void setDataSearch(T dataSearch) {
		this.dataSearch = dataSearch;
	}

	public T getDataEdit() {
		return dataEdit;
	}

	public void setDataEdit(T dataEdit) {
		this.dataEdit = dataEdit;
	}

	public LazyDataModel<T> getListLazyDataModel() {
		return listLazyDataModel;
	}

	public void setListLazyDataModel(LazyDataModel<T> listLazyDataModel) {
		this.listLazyDataModel = listLazyDataModel;
	}

}
