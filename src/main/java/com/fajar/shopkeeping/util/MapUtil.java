package com.fajar.shopkeeping.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapUtil {
	
	public static <T> List convertMapList(List mapList, Class objectClass) {
		
		List result = new ArrayList<>();
		
		for (Object object : mapList) {
			result.add(mapToObject( (Map) object, objectClass));
		} 
		
		return result;
	}

	public static Object mapToObject(Map map, Class objectClass) {
		Set mapKeys = map.keySet();

		try {
			Object result = objectClass.newInstance();

			for (Object key : mapKeys) {

				try {
					Object value = map.get(key);

					Field field = EntityUtil.getDeclaredField(objectClass, key.toString());

					if (value != null && field != null) {

						Class<?> fieldType = field.getType();
						
						/**
						 * mapValue is map
						 */
						if (value.getClass().equals(Map.class)) {

							value = mapToObject((Map) value, fieldType);
						} else 
							/**
							 * long
							 */
							if (fieldType.equals(long.class) || fieldType.equals(Long.class)) {
							value = Long.valueOf(value.toString());
						} else 
							/**
							 * double
							 */
							if (fieldType.equals(double.class) || fieldType.equals(Double.class)) {
							value = Double.valueOf(value.toString());
						}

						field.setAccessible(true);
						field.set(result, value);
					}
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
			}

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

}
