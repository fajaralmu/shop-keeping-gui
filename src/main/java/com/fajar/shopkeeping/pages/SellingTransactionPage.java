package com.fajar.shopkeeping.pages;

import static com.fajar.shopkeeping.pages.BaseTransactionPage.DropDownType.CUSTOMER;
import static com.fajar.shopkeeping.pages.BaseTransactionPage.DropDownType.PRODUCT;
import static com.fajar.shopkeeping.pages.BaseTransactionPage.DropDownType.SUPPLIER;
import static javax.swing.SwingConstants.LEFT;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.Date;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.fajar.entity.Customer;
import com.fajar.entity.Product;
import com.fajar.shopkeeping.component.ComponentBuilder;
import com.fajar.shopkeeping.model.PanelRequest;
import com.fajar.shopkeeping.util.Log;

import lombok.Data;

@Data
public class SellingTransactionPage  extends BaseTransactionPage{

	
	private long customerId;  
	
	private Customer selectedCustomer;
	private JComboBox customerComboBox;
	
	public SellingTransactionPage() { 
		super("Transaction", BASE_WIDTH, BASE_HEIGHT, "Selling");
		
	}  

	@Override
	public void setSelectedProduct(Product product) {
		this.selectedProduct = product;
		
	}

	@Override
	protected JPanel buildFormPanel() {
		//CUSTOMER
		ActionListener actionListener = dynamicDropdownActionListener(CUSTOMER);
		KeyListener keyListener = dynamicDropdownFieldKeyListener(CUSTOMER); 
		customerComboBox = ComponentBuilder.buildEditableComboBox("", keyListener, actionListener, "type customer name..");
				
		//product
		ActionListener actionListenerProduct = dynamicDropdownActionListener(PRODUCT);
		KeyListener keyListenerProduct = dynamicDropdownFieldKeyListener(PRODUCT); 
		productComboBox = ComponentBuilder.buildEditableComboBox("", keyListenerProduct, actionListenerProduct, "type product name.."); 
		
		inputQtyField = numberField("0"); 
		 		
 		labelProductUnit = label("unit", LEFT);
 		labelProductUnit.setSize(200, 20);
 		
 		buttonSubmitCart = button("Submit To Cart");
 		buttonClearCart = button("Clear");
 		buttonSubmitTransaction = button("SUBMIT TRANSACTION");
		 		
 		PanelRequest panelRequest = PanelRequest.autoPanelNonScroll(2, 200, 5, Color.LIGHT_GRAY);
		JPanel panel = ComponentBuilder.buildPanelV2(panelRequest , 
				label("Customer", LEFT), customerComboBox,
				label("Product", LEFT), productComboBox,
				label("Quantity", LEFT), inputQtyField,
				label("Unit", LEFT), labelProductUnit, 
				buttonSubmitCart, buttonClearCart,
				buttonSubmitTransaction, null
				);
		return panel ;
	}

	@Override
	protected JPanel buildProductListPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void handleDynamicDropdownChange(DropDownType dropDownType, String selectedValue) {
		Log.log("handleDynamicDropdownChange: ",selectedValue);
		if(selectedValue != null) {
			 
			 switch (dropDownType) {
				case CUSTOMER:
					setSelectetCustomerByName(selectedValue );
					break;
				case PRODUCT:
					setSelectedProductByName(selectedValue );
					break;
				default:
					break;
			} 
		 }
		
	}

	private void setSelectedProductByName(String selectedValue) {
		for (Product product : productDropdownValues) {
			if(product.getName().equals(selectedValue)) {
				Log.log("product: ",product);
				setSelectedProduct(product);
			}
		}
		
	}

	private void setSelectetCustomerByName(String selectedValue) {
		for (Customer customer : customerDopdownValues) {
			if(customer.getName().equals(selectedValue)) {
				Log.log("select customer: ",customer);
				setSelectedCustomer(customer);
			}
		}
		
	} 

}
