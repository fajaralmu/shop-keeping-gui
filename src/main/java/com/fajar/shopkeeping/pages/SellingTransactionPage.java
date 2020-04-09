package com.fajar.shopkeeping.pages;

import javax.swing.JPanel;

import com.fajar.entity.Customer;
import com.fajar.entity.Product;

import lombok.Data;

@Data
public class SellingTransactionPage  extends BaseTransactionPage{

	
	private long customerId;  
	
	private Customer selectedCustomer;
	
	public SellingTransactionPage() { 
		super("Transaction", BASE_WIDTH, BASE_HEIGHT, "Selling");
		
	}  

	@Override
	public void setSelectedProduct(Product product) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected JPanel buildFormPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected JPanel buildProductListPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void handleDynamicDropdownChange(DropDownType dropDownType, String selectedValue) {
		// TODO Auto-generated method stub
		
	} 

}
