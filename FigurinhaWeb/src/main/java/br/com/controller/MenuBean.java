package br.com.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;
import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.component.submenu.Submenu;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DefaultMenuModel;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.MenuModel;
import org.primefaces.model.TreeNode;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.ContextLoaderListener;

import br.com.exception.DbLibException;
import br.com.pojo.Menu;
import br.com.service.MenuService;
import br.com.util.DataState;
import br.com.util.FacesUtil;
import br.com.util.Message;

/**
 * 
 * Classe responsavel pela construcao do menu dinamico baseado em permissoes
 * 
 */
@ManagedBean(name = "menuBean")
@ViewScoped
public class MenuBean extends GenericBean<Menu, MenuService> {

	/**
	 * Atributo que faz o bind com o componente na tela.
	 * */
	private MenuModel model;

	/**
	 * Atributo que faz o bind com a arvore da tela de adi��o
	 * */
	private TreeNode root;

	private TreeNode[] nosSelecionados;

	private List<Menu> listaMenuGeral;

	private SecurityExpressionRoot securityRoot;

	private RoleHierarchyImpl roleHierarchy;

	private String caminhoMenu;

	private DualListModel<String> pickArquivos = new DualListModel<String>();
	private DualListModel<String> pickRoles = new DualListModel<String>();

	public MenuBean() {
		super();
		pickArquivos.getSource().addAll(getArquivosJsf());
		pickRoles.getSource().addAll(super.getRoles());
		model = new DefaultMenuModel();
		root = new DefaultTreeNode("Root", null);
		try {

			/**
			 * Lista recebe todos os objetos menu do banco
			 */
			listaMenuGeral = dataService.findAll();

			preparaPermissaoHierarquica();

			constroiMenu(listaMenuGeral);
		} catch (DbLibException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Metodo gambiarra que faz bind com um inputHidden do Modal de ADD/EDIT
	 * para ser chamado antes da renderiza��o dos componentes. Assim pode-se
	 * manipular o conteudo dos componentes da tela em caso de edi��o. e a
	 * limpeza dos mesmos no caso de adicao;
	 * */
	public String getInitModalMenu() {
		if (state == DataState.EDIT_STATE) {
			/**
			 * Setar url + menuPai + permissoes caso existam.
			 * */

			// Url
			if (StringUtils.isNotBlank(dataEdit.getUrl())) {
				pickArquivos.getSource().remove(dataEdit.getUrl());
				pickArquivos.getTarget().add(dataEdit.getUrl());
			}
			// Permissoes
			for (String permissao : dataEdit.getPermissao()) {
				pickRoles.getSource().remove(permissao);
				pickRoles.getTarget().add(permissao);
			}
			// Menu Pai
			if (dataEdit.getMenuPai() != null) {
				marcaMenu(dataEdit.getMenuPai(), root.getChildren());
			}

		} else if (state == DataState.ADD_STATE) {
			/**
			 * Deve limpar todas as sele��es
			 * */
		}

		return "";

	}

	private void marcaMenu(Menu menuASerPesquisado, List<TreeNode> menusRaiz) {
		for (TreeNode menuRaiz : menusRaiz) {

			if (((Menu) menuRaiz.getData()).equals(menuASerPesquisado))
				menuRaiz.setSelected(true);
			else
				for (TreeNode no : menuRaiz.getChildren()) {
					if (((Menu) no.getData()).equals(menuASerPesquisado))
						no.setSelected(true);
					else
						marcaMenu(menuASerPesquisado, no.getChildren());
				}
		}
	}

	private void constroiMenu(List<Menu> listaMenuGeral) {

		/**
		 * Pega a lista de todos os menu Pai, ou seja quem tem idMenuPai == 0 ou
		 * null
		 */
		List<Menu> listaMenuPai = getRootMenu(listaMenuGeral);

		/**
		 * Para cada menu Pai deve ser feita a constru��o de todos os seus
		 * filhos
		 * */
		for (Menu menuPai : listaMenuPai) {
			List<Menu> filhos = new ArrayList<Menu>();
			/**
			 * Lista contendo os filhos do menu pai.
			 * */
			filhos.addAll(getFilhos(menuPai, listaMenuGeral));
			/**
			 * Controi o menu a partir do root e seus filhos no primefaces.
			 * */
			constroiMenu(menuPai, filhos);

			/**
			 * Controi a �rvore utilizada na adi��o de menu a partir do root e
			 * seus filhos no primefaces.
			 * */
			constroiArvore(menuPai, filhos);

		}
	}

	private void constroiArvore(Menu menuPai, List<Menu> filhos) {

		TreeNode noPai = new DefaultTreeNode(menuPai, root);

		/**
		 * Cache com o id do Menu e sua instancia no primefaces, utilizado para
		 * os filhos terem onde ser inseridos.
		 * */
		Map<Integer, TreeNode> cacheSubmenu = new HashMap<Integer, TreeNode>();
		cacheSubmenu.put(menuPai.getId(), noPai);
		/**
		 * Percorre menus filhos e podem ser adicionados como submenus em caso
		 * de nao forem links ou como itens de menu com actions em caso de
		 * possuirem urls f
		 */
		for (Menu filho : filhos) {
			if (!filho.possuiFilho()) {
				TreeNode subFilho;
				/**
				 * Adiciona subMenu filho no menuPai
				 */

				if (!filho.getMenuPai().getId().equals(menuPai.getId())) {
					subFilho = new DefaultTreeNode(filho, cacheSubmenu.get(filho.getMenuPai()
							.getId()));
				} else
					subFilho = new DefaultTreeNode(filho, noPai);
				/**
				 * A instancia do menu que nao � filho ser� adicionada no hash.
				 * */
				cacheSubmenu.put(filho.getId(), subFilho);
			}
		}

	}

	/**
	 * Metodo que constroi o menu a partir de um root com seus filhos no
	 * primefaces.
	 * */
	private void constroiMenu(Menu menuPai, List<Menu> filhos) {

		/**
		 * Menu pai criado no primefaces.
		 * */
		Submenu menuRoot = new Submenu();
		menuRoot.setLabel(menuPai.getNome());
		/**
		 * Cache com o id do Menu e sua instancia no primefaces, utilizado para
		 * os filhos terem onde ser inseridos.
		 * */
		Map<Integer, Submenu> cacheSubmenu = new HashMap<Integer, Submenu>();
		cacheSubmenu.put(menuPai.getId(), menuRoot);
		/**
		 * Percorre menus filhos e podem ser adicionados como submenus em caso
		 * de nao forem links ou como itens de menu com actions em caso de
		 * possuirem urls f
		 */
		for (Menu filho : filhos) {
			if (!filho.possuiFilho()) {
				Submenu subFilho = new Submenu();
				subFilho.setLabel(filho.getNome());
				/**
				 * Adiciona subMenu filho no menuPai
				 */

				if (!filho.getMenuPai().getId().equals(menuPai.getId())) {
					cacheSubmenu.get(filho.getMenuPai().getId()).getChildren().add(subFilho);
				} else

					menuRoot.getChildren().add(subFilho);
				/**
				 * A instancia do menu que nao � filho ser� adicionada no hash.
				 * */
				cacheSubmenu.put(filho.getId(), subFilho);
			} else {
				MenuItem item = new MenuItem();
				item.setValue(filho.getNome());
				item.setUrl(filho.getUrl());
				/**
				 * Recupera a referencia do menu pai para adicionar o seu item
				 * de menu tudo feito atraves do HashMap
				 * */
				cacheSubmenu.get(filho.getMenuPai().getId()).getChildren().add(item);
			}
		}
		/**
		 * Adiciona o root no modelo
		 * */
		model.addSubmenu(menuRoot);
	}

	/**
	 * Metodo que busca todos os filhos de um menu. Fun��o recursiva em que caso
	 * o registro n�o seja "folha"(idMenuPai != null && idMenuPai != 0 && url !=
	 * null;) faz a chamada no metodo para percorrer os diferentes niveis.
	 * */
	private List<Menu> getFilhos(Menu menuPai, List<Menu> lista) {
		List<Menu> listaRetornoFilhos = new ArrayList<Menu>();

		/**
		 * Pega a lista lista com todos os menus e recupera todos os objetos que
		 * possuem idmenuPai igual ao do menuPai passado
		 */
		for (Menu filho : getListMenuPorId(menuPai, lista)) {
			listaRetornoFilhos.add(filho);
			/**
			 * Caso n�o seja folha faz a chamada recursiva com o filho para
			 * buscar seus "proximos"
			 * */
			if (!filho.possuiFilho())
				listaRetornoFilhos.addAll(getFilhos(filho, lista));
		}

		return listaRetornoFilhos;
	}

	/**
	 * Pega todos os menu pai, a busca dos filhos ser� atrav�s deles. A condi��o
	 * para ser menuPai � ter idMenuPai ==0 ou null dependendo da abordagem para
	 * trazer esse campo no Hibernate.
	 * 
	 * */
	private List<Menu> getRootMenu(List<Menu> lista) {
		List<Menu> listaRetorno = new ArrayList<Menu>();
		for (Menu menu : lista)

			if (menu.isPai() && temPermissao(menu.getPermissao()))
				listaRetorno.add(menu);
		return listaRetorno;
	}

	/**
	 * Busca lista de Menus que possuem idMenuPai == ao id passado. Metodo
	 * utilizado na fun��o getFilhos.
	 * */
	private List<Menu> getListMenuPorId(Menu busca, List<Menu> lista) {
		List<Menu> lRetorno = new ArrayList<Menu>();
		for (Menu menu : lista) {
			if (menu.getMenuPai() != null && menu.getMenuPai().getId() == busca.getId()
					&& temPermissao(menu.getPermissao()))
				lRetorno.add(menu);
		}
		return lRetorno;
	}

	private boolean temPermissao(Set<String> permissoes) {
		for (String permissao : permissoes)
			if (securityRoot.hasRole(permissao))
				return true;
		return false;
	}

	/**
	 * Metodo responsavel por recuperar a configura�ao de permissao hierarquica
	 * e setar um objeto RoleHierarchyImpl e adicionar o mesmo no
	 * SecurityExpressionRoot para que o metodo HasRole funciona de forma
	 * hierarquica e possa ser utilizado para verificar se o usuario possui
	 * permissao no Menu
	 */
	private void preparaPermissaoHierarquica() {
		BeanFactory factory = ContextLoaderListener.getCurrentWebApplicationContext();

		roleHierarchy = (RoleHierarchyImpl) factory.getBean("roleHierarchy");
		securityRoot = new SecurityExpressionRoot(SecurityContextHolder.getContext()
				.getAuthentication()) {

		};

		securityRoot.setRoleHierarchy(roleHierarchy);
	}

	/**
	 * M�todos para ADD
	 * */

	@Override
	public void preEdit(ActionEvent event) {
		super.preEdit(event);
	}

	/**
	 * Metodo save foi sobrescrito para o dataEdit ser preenchido com os dados
	 * da tela
	 * */
	public void save() {

		/**
		 * Nome, Permiss�o, menuPai se houver, url se houver
		 * 
		 * */
		/**
		 * Menu pai se houver
		 * */
		List<TreeNode> listaNos = Arrays.asList(nosSelecionados);
		if (listaNos != null && !listaNos.isEmpty())
			dataEdit.setMenuPai((Menu) listaNos.get(0).getData());

		/**
		 * url se houver
		 * */
		if (pickArquivos.getTarget() != null && !pickArquivos.getTarget().isEmpty())
			dataEdit.setUrl(pickArquivos.getTarget().get(0));

		/**
		 * Permissoes
		 * */
		dataEdit.setPermissao(new HashSet<String>(pickRoles.getTarget()));

		super.save();
	}

	/**
	 * Metodo validateSave sobrescrito para valida��es da tela
	 * 
	 * */
	@Override
	protected List<Message> validateSave(Menu dataSearch) {
		List<Message> lRetorno = new ArrayList<Message>();
		if (pickRoles.getTarget() == null && pickRoles.getTarget().isEmpty()) {
			lRetorno.add(new Message("mensagem.menu.maiorQueUm", FacesMessage.SEVERITY_ERROR, null,
					true));
		}

		return lRetorno;
	}

	public void onNodeSelect(NodeSelectEvent event) {
		/**
		 * Limpa a sele��o de todos os nos que j� haviam sido selecionados;
		 * */
		for (TreeNode d : nosSelecionados) {
			d.setSelected(false);
			d.setExpanded(true);
		}
		nosSelecionados = new DefaultTreeNode[1];
		/**
		 * Adiciona apenas o n� selecionado e seta sua sele��o para true;
		 * */
		nosSelecionados[0] = event.getTreeNode();
		event.getTreeNode().setSelected(true);
		event.getTreeNode().setExpanded(true);
	}

	public void onNodeUnselect(NodeUnselectEvent event) {
	}

	public void onTransfer(TransferEvent e) {
		if (e.isAdd() && pickArquivos.getTarget().size() == 1) {
			FacesContext fc = FacesContext.getCurrentInstance();
			FacesUtil.addMessage(new Message("mensagem.menu.maiorQueUm",
					FacesMessage.SEVERITY_ERROR, null, true));
			fc.renderResponse();

			RequestContext rc = RequestContext.getCurrentInstance();
			rc.update(FacesUtil.getViewRoot().findComponent("formAddMenu:pickList").getClientId(fc));
		}
	}

	private List<String> getArquivos() {

		List<String> lista = new ArrayList<String>();
		ServletContext servletContext = (ServletContext) FacesUtil.getExternalContext()
				.getContext();
		String absoluteDiskPath = servletContext.getRealPath("");

		try {
			percorreDiretorio(absoluteDiskPath, lista);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return lista;
	}

	public List<String> getArquivosJsf() {
		List<String> lRetorno = new ArrayList<String>();
		for (String arquivo : getArquivos())
			lRetorno.add(arquivo
					.substring(arquivo.lastIndexOf("MedicalWeb") + 10, arquivo.length())
					.replace("xhtml", "jsf").replaceAll("\\\\", "/"));
		return lRetorno;
	}

	private boolean isArquivoValido(String arquivo) {
		String[] extensoes = { ".xhtml", ".html" };
		for (String extensao : extensoes)
			if (arquivo.endsWith(extensao))
				return true;
		return false;

	}

	private boolean isDiretorioValido(String name) {
		if (name.contains("template"))
			return false;
		return true;
	}

	private void percorreDiretorio(String caminho, List<String> lista) throws Exception {
		File localFile1 = new File(caminho);
		File[] arrayOfFile = localFile1.listFiles();
		File localFile2 = null;

		if (localFile1.isDirectory()) {
			for (int i = 0; i < arrayOfFile.length; i++) {
				localFile2 = arrayOfFile[i];
				// if (localFile2.getName().equalsIgnoreCase(".svn"))
				if (localFile2.getName().equalsIgnoreCase(".svn")
						|| !isDiretorioValido(localFile2.getName()))
					continue;
				if (!localFile2.isDirectory() && isArquivoValido(localFile2.getAbsolutePath())) {
					System.out.println(localFile2.getAbsolutePath());
					lista.add(localFile2.getAbsolutePath());
					// processaArquivo(localFile2.getAbsolutePath());
				} else {
					final String pathDiretorio = localFile2.getAbsolutePath();
					percorreDiretorio(pathDiretorio, lista);
				}
			}

		}
	}

	/*
	 * String relativeWebPath = "resources/img/janela.png"; ServletContext
	 * servletContext = (ServletContext)
	 * FacesUtil.getExternalContext().getContext(); String absoluteDiskPath =
	 * servletContext.getRealPath(relativeWebPath);
	 */

	/**
	 * Getters & Setters
	 * */

	public List<Menu> getListaMenuGeral() {
		return listaMenuGeral;
	}

	public void setListaMenuGeral(List<Menu> listaMenuGeral) {
		this.listaMenuGeral = listaMenuGeral;
	}

	public SecurityExpressionRoot getSecurityRoot() {
		return securityRoot;
	}

	public void setSecurityRoot(SecurityExpressionRoot securityRoot) {
		this.securityRoot = securityRoot;
	}

	public RoleHierarchyImpl getRoleHierarchy() {
		return roleHierarchy;
	}

	public void setRoleHierarchy(RoleHierarchyImpl roleHierarchy) {
		this.roleHierarchy = roleHierarchy;
	}

	public MenuModel getModel() {
		return model;
	}

	public void setModel(MenuModel model) {
		this.model = model;
	}

	@Override
	public void messageSuccesSave() {
		// TODO Auto-generated method stub

	}

	@Override
	public void messageSuccessDelete() {
		// TODO Auto-generated method stub

	}

	public String getCaminhoMenu() {
		return caminhoMenu;
	}

	public void setCaminhoMenu(String caminhoMenu) {
		this.caminhoMenu = caminhoMenu;
	}

	public DualListModel<String> getPickArquivos() {
		return pickArquivos;
	}

	public void setPickArquivos(DualListModel<String> pickArquivos) {
		this.pickArquivos = pickArquivos;
	}

	public TreeNode getRoot() {
		return root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}

	public TreeNode[] getNosSelecionados() {
		return nosSelecionados;
	}

	public void setNosSelecionados(TreeNode[] nosSelecionados) {
		this.nosSelecionados = nosSelecionados;
	}

	public DualListModel<String> getPickRoles() {
		return pickRoles;
	}

	public void setPickRoles(DualListModel<String> pickRoles) {
		this.pickRoles = pickRoles;
	}

}
