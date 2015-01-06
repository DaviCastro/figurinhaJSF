package br.com.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

import br.com.dao.Identifiable;
import br.com.exception.DbLibException;
import br.com.service.Service;

/**
 * 
 * Classe responsavel por implementar o LazyDataTable de forma generica
 * 
 * 
 * @param <T>
 */
public class GenericLazyList<T extends Identifiable> extends LazyDataModel<T> {

	private T entity;
	private List<T> listEntity = new ArrayList<T>();
	private Service<T> dataService;

	public GenericLazyList(Service<T> dataService, T entity) {
		this.dataService = dataService;
		this.entity = entity;
		
	}

	private static final long serialVersionUID = 1098360609550814873L;

	/**
	 * Metodo obrigatorio que é chamado quando o dataTable é carregado
	 * Construtor sem multiSort
	 */
	@Override
	public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, String> filters) {

		try {
			/**
			 * Objeto util de ordem para trafegar entre as camadas sem
			 * depedencia de tecnologia
			 */
			List<OrderUtil> listaOrdem = null;
			/**
			 * Se for selecionado sort na tela ele adiciona na lista de sorte
			 */
			if (sortField != null) {
				listaOrdem = new ArrayList<OrderUtil>();
				listaOrdem.add(new OrderUtil(sortField, getSortOrder(sortOrder)));
			}

			/**
			 * Executa a pesquisa
			 */
			listEntity = dataService.search(entity, first, pageSize, listaOrdem);
			/**
			 * Seta a quantidade total de linhas que a pesquisa pode possuir
			 */
			setRowCount(dataService.rowCount(entity));
			setPageSize(pageSize);
			
			if (listEntity.size() == 0) {
				 Message message = new Message("msg.nenhum.registro",
				 FacesMessage.SEVERITY_ERROR,
				 null, true);
				 FacesUtil.addMessage(message);
			}
			
			return listEntity;

		} catch (DbLibException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	/**
	 * Metodo obrigatorio que é chamado quando o dataTable é carregado
	 * Construtor com multiSort
	 */
	public List<T> load(int first, int pageSize, List<SortMeta> multiSortMeta,
			Map<String, String> filters) {

		try {
			List<OrderUtil> listaOrdem = null;
			if (multiSortMeta != null && multiSortMeta.size() != 0) {
				listaOrdem = new ArrayList<OrderUtil>();
				for (SortMeta sort : multiSortMeta) {
					listaOrdem.add(new OrderUtil(sort.getSortField(), getSortOrder(sort
							.getSortOrder())));
				}
			}
			listEntity = dataService.search(entity, first, pageSize, listaOrdem);
			setRowCount(dataService.rowCount(entity));
			setPageSize(pageSize);
			return listEntity;

		} catch (DbLibException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * Metodo utilitario para definir como sera preenchido a ordem do objeto
	 * OrdemUtil
	 * 
	 * @param sort
	 * @return
	 */
	public boolean getSortOrder(SortOrder sort) {
		return sort.equals(SortOrder.ASCENDING);
	}

	/**
	 * Metodo responsavel por recuperar objeto da linha
	 */
	public T getRowData(String id) {
		Integer identificador = Integer.valueOf(id);

		for (T entity : listEntity) {
			if (identificador.equals(entity.getId())) {
				return entity;
			}
		}

		return null;
	}

	@Override
	public Object getRowKey(T entity) {
		return entity.getId();
	}

	public Service<T> getDataService() {
		return dataService;
	}

	public void setDataService(Service<T> dataService) {
		this.dataService = dataService;
	}

}
