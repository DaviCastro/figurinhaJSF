package br.com.controller;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ValueChangeEvent;

import br.com.util.FacesUtil;

@ManagedBean(name = "localeController")
@SessionScoped
/**
 * Classe responsavel pela mudanca de idioma no camada web
 *
 */
public class LocaleController implements Serializable {

	private static final long serialVersionUID = 1L;

	private String currentLocale;

	public String getCurrentLocale() {
		return currentLocale;
	}

	public void setCurrentLocale(String currentLocale) {
		this.currentLocale = currentLocale;
	}
	/**
	 * Map que contera os nome dos messages que o sistema possui
	 */
	private static Map<String, Object> countries;
	static {
		countries = new LinkedHashMap<String, Object>();
		countries.put("Português", "pt_BR");
		countries.put("English", "en_US");
	}

	public Map<String, Object> getCountriesInMap() {
		return countries;
	}

	/**
	 * Metodo que de fato troca o idioma
	 * Seta o locale do ViewRoot com o nome recebido da camada web desde que o mesmo esteja no hash
	 * @param e
	 */
	public void countryLocaleCodeChanged(ValueChangeEvent e) {
		for (Map.Entry<String, Object> entry : countries.entrySet())
			if (entry.getValue().toString().equals(e.getNewValue().toString()))
				FacesUtil.getViewRoot().setLocale(
						new Locale(entry.getKey(), String.valueOf(entry.getValue())));
	}

}