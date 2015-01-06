package br.com.util;

import java.lang.reflect.Field;

import javax.persistence.Embedded;
import javax.persistence.Id;

public class Teste {

	public static <T> T clone(T object, boolean copiarId) {
		T clone = null;

		try {
			clone = (T) object.getClass().newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		for (Class obj = object.getClass(); !obj.equals(Object.class); obj = obj
				.getSuperclass()) {
			for (Field field : obj.getDeclaredFields()) {
				field.setAccessible(true);

				if (field.getAnnotation(Id.class) != null
						|| field.getAnnotation(Embedded.class) != null) {
					try {
						field.set(clone, field.get(object));
					} catch (Throwable t) {
					}
				}

			}
			return clone;
		}
		return clone;

	}
}