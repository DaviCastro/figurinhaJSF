package br.com.converter;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.persistence.Embedded;
import javax.persistence.Id;

import org.apache.commons.lang.StringUtils;

import br.com.annotation.AutoComplete;

@FacesConverter(value = "genericConverter")
/**
 * Classe responsavel por fazer o converter entre objeto e string de forma generica
 * funciona basicamente sobre anota��es feitas na classe pojo
 *
 */
public class GenericConverter implements Converter {

	/**
	 * Constante criada para identifica�ao de objeto em caso de o mesmo estiver vazio
	 */
	private final String CONSTANTE_AUTO_COMPLETE = "%v@l0r@ut0C0mpl&t&$#";

	/**
	 * Metodo que retornara a referencia do objeto com base no valor recebido da tela.
	 * Este valor ser� buscado no hash do UiComponent 
	 * Existem 2 possibilidades para este metodo se o valor informado na tela existir neste Hash apenas ser� retornado diretamente a referencia do objeto do mesmo
	 * Em caso contrario ser� buscado a referencia do objeto pela constante utilizada no getAsString apareces para conseguir retornar o tipo do objeto
	 * com o tipo do objeto em maos ser� buscado o campo que possui a anota��o @AutoComplete e apos possuir a referencia do campo e invokado o seu metodo set
	 * com o valor informado da tela, desta forma teremos um objeto preenchido com os valores preenchidos na tela.
	 */
	public Object getAsObject(FacesContext ctx, UIComponent component, String value) {
		if (value != null && !"".equals(value)) {
			Object retorno;
			/**
			 * se o objeto existir no hash para a "key" valor apenas retorna a referencia
			 */
			if ((retorno = this.getAttributesFrom(component).get(value)) != null)
				return retorno;
			
			/**
			 * Em caso contrario ser� buscado no hash pela constante o objeto
			 * apenas para conseguir recuperar o objeto vazio e conseguir setar
			 * o valor recebido no mesmo para pesquisa 
			 */
			else {
				try {
					/**
					 * Recebe referencia do objeto
					 */
					retorno = this.getAttributesFrom(component).get(CONSTANTE_AUTO_COMPLETE);

					String prefixo = "set";
					/**
					 * Busca no objeto o campo que possui a anotacao autoComplete
					 * e executa o set do Mesmo
					 */
					for (Field field : retorno.getClass().getDeclaredFields()) {

						if (field.getAnnotation(AutoComplete.class) != null) {

							Method method = retorno.getClass().getMethod(
									prefixo + StringUtils.capitalize(field.getName()),
									field.getType());
							
							method.invoke(retorno, value);
						}

					}
					return retorno;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
		return null;
	}

	/**
	 * Metodo que identificara o objeto como String no UI component No caso
	 * deste metodo ser� gravado o valor do id do mesmo no Ui component
	 * juntamente com a referencia com o seu objeto para que seja possivel
	 * recuperar a referencia do mesmo atraves desta id no getAsObject Metodo
	 * utiliza de reflexao para percorrar os campos do objeto recebido ate que
	 * encontre a anota��o @id ou @Embedded
	 * 
	 * Metodo possui um detalhe se o usuario pesquisar antes de o outcomplete
	 * seja acionado � necessario gravar a referencia do objeto juntamente com
	 * um valor padrao para que o mesmo possa ser recuperado no getAsObject
	 */
	public String getAsString(FacesContext ctx, UIComponent component, Object value) {
		if (value != null && !"".equals(value)) {

			/**
			 * Percorre o os campos da classe do objeto
			 */
			for (Field field : value.getClass().getDeclaredFields()) {

				/**
				 * verifica se objeto possui anotacoes "chaves"
				 */
				if (field.getAnnotation(Id.class) != null
						|| field.getAnnotation(Embedded.class) != null) {
					Method a;
					try {
						/**
						 * executa a chamada do metodo get deste campo
						 */
						a = value.getClass().getMethod(
								"get" + StringUtils.capitalize(field.getName()));
						String valor = String.valueOf(a.invoke(value));
						/**
						 * se o valor for diferente de null quer dizer que o
						 * objeto ja possui os valores preenchidos e o metodo
						 * get foi bem sucedido apenas ira gravar a referencia
						 * do objeto e sua id no uicomponent
						 */
						if (valor != null && !"null".equalsIgnoreCase(valor))
							this.addAttribute(component, value, valor);
						else
							/**
							 * em caso contrario objeto est� vazio e ser�
							 * gravado um valor padrao para a referencia deste
							 * objeto
							 */
							this.addAttribute(component, value, CONSTANTE_AUTO_COMPLETE);

						return valor.equals("null") ? null : valor;
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return "";
	}

	/**
	 * Metodo responsavel por inserir no hash do Ui component que guarda a referencia dos atributos do mesmo 
	 * @param component
	 * @param o
	 * @param id
	 */
	private void addAttribute(UIComponent component, Object o, Serializable id) {
		this.getAttributesFrom(component).put(String.valueOf(id), o);
	}

	/**
	 * Metodo que retorna um hash todos os atributos do UI component
	 * @param component
	 * @return
	 */
	private Map<String, Object> getAttributesFrom(UIComponent component) {
		return component.getAttributes();
	}

}
