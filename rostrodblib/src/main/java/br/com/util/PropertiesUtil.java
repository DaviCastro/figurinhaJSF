package br.com.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import br.com.exception.DbLibException;


public class PropertiesUtil {
	/**
	 * Devolve instancia de arquivo de propiedades
	 * 
	 * @return
	 * @throws AlgoritmoException
	 */
	public static Properties recuperaProperties() throws DbLibException {
		Properties props = new Properties();
		try {
			props.load(PropertiesUtil.class.getClassLoader().getResourceAsStream(
					"br/com/util/propiedades.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new DbLibException("erro", e);
		}

		return props;

	}

	public static Properties recuperaPropertiesPorCaminho(String caminho)
			throws DbLibException {
		Properties prop = new Properties();

		try {
			prop.load(new FileInputStream(caminho));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new DbLibException("erro", e);
		}

		return prop;
	}
}
