package com.fajar.shopkeeping.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fajar.shoppingmart.util.EntityUtil;

public class MapUtil {
	
	public static <T> List convertMapList(List mapList, Class objectClass) {
		
		List result = new ArrayList<>();
		
		for (Object object : mapList) {
			result.add(mapToObject( (Map) object, objectClass));
		} 
		
		return result;
	}
	
	public static void printMap(Map map) {
		Log.log("printing map");
		for(Object key:map.keySet()) {
			Log.log("key:", key,":",map.get(key));
		}
	}

	public static Object mapToObject(Map map, Class objectClass) {
		Set mapKeys = map.keySet();

		try {
			Object result = objectClass.newInstance();

			for (Object key : mapKeys) {
				Object value = map.get(key);
				Field field =  EntityUtil.getDeclaredField(objectClass, key.toString()); 
				 
				try {  
					
					if (value != null && field != null) {

						Class  fieldType = field.getType();
						boolean isEnum = fieldType.isEnum();
						
						/**
						 * mapValue is map
						 */
						if (value.getClass().equals(Map.class) || value.getClass().equals(LinkedHashMap.class)) {

							value = mapToObject((Map) value, fieldType);
						} else 
							/**
							 * long
							 */
							if (objectEquals(fieldType,long.class,Long.class)) {
							value = Long.valueOf(value.toString());
						} else 
							/**
							 * int
							 */
							if (objectEquals(fieldType,int.class,Integer.class)) {
							value = Integer.parseInt(value.toString());
						} else 
							/**
							 * double
							 */
							if (objectEquals(fieldType, double.class  ,Double.class)) {
							value = Double.valueOf(value.toString());
						} else
							/**
							 * date from Long
							 */
							if(fieldType.equals(Date.class) && (objectEquals(value.getClass(),Long.class,long.class))) {
								value = new Date((Long) value);
						}else
							/**
							 * long from date
							 */
							if(objectEquals(fieldType, long.class ,Long.class) && value.getClass().equals(Date.class)) {
								value = ((Date) value).getTime();
						}else
							/**
							 * ENUM
							 */
							if(isEnum) {
								value = Enum.valueOf(fieldType, value.toString());
							}
							

						field.setAccessible(true);
						field.set(result, value);
					}
				} catch (Exception e) {
					String valueType = value == null ? "NULL": value.getClass().toString();
					Log.log("fieldType: ",(field == null? "NULL":field.getType())," value: ",value, "type:",valueType);
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
	
	public static boolean objectEquals(Object object, Object ...objects) {
		
		
		for (Object object2 : objects) {
			if(object.equals(object2)) {
				return true;
			}
		}
		
		return false;
	}
	
	
	public static Map getMapFromList(String key, Object selectedValue, List<Map> list) {
		if(null == list) {
			Log.log("list is null");
			return null;
		}
		for (Map map : list) {
			if(map.get(key).equals(selectedValue)) {
				return map;
			}
		}
		
		return null;
	}

}
