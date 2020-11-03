package com.fajar.shopkeeping.component;

import javax.swing.JMenuItem;

import com.fajar.shoppingmart.entity.BaseEntity;

import lombok.Data;

@Data
public class ManagementMenuItem extends JMenuItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7471146137438422592L;

	private final Class<? extends BaseEntity> entityClass;
	
	public ManagementMenuItem(String menuName, Class<? extends BaseEntity> _class) {
		super(menuName);
		this.entityClass = _class;
	}
	
}
