package com.fajar.shopkeeping.util;

import com.fajar.shoppingmart.dto.FieldType;

public class ObjectUtil {
	
	public static <T> T castTo(Object object ) {
		
		return ( T) object;
	}
	
	public static FieldType getFieldTypeEnum(String value) {
		FieldType[] values = FieldType.values();
		for (FieldType fieldType : values) {
			if(fieldType.value.equals(value)) {
				return fieldType;
			}
		}
		return null;
	}
	
	public static void main(String[] sdd) {
		System.out.println(getFieldTypeEnum("textarea"));
	}

}
