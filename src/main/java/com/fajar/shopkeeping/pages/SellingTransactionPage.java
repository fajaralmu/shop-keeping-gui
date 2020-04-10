package com.fajar.shopkeeping.pages;

import static com.fajar.shopkeeping.model.PanelRequest.autoPanelScrollWidthHeightSpecified;
import static com.fajar.shopkeeping.pages.BaseTransactionPage.DropDownType.CUSTOMER;
import static com.fajar.shopkeeping.pages.BaseTransactionPage.DropDownType.PRODUCT;
import static com.fajar.shopkeeping.pages.BaseTransactionPage.DropDownType.SUPPLIER;
import static com.fajar.shopkeeping.util.StringUtil.beautifyNominal;
import static javax.swing.SwingConstants.LEFT;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.fajar.dto.ShopApiResponse;
import com.fajar.entity.BaseEntity;
import com.fajar.entity.Customer;
import com.fajar.entity.Product;
import com.fajar.entity.ProductFlow;
import com.fajar.entity.Supplier;
import com.fajar.entity.Transaction;
import com.fajar.shopkeeping.component.ComponentBuilder;
import com.fajar.shopkeeping.component.Dialogs;
import com.fajar.shopkeeping.model.PanelRequest;
import com.fajar.shopkeeping.util.DateUtil;
import com.fajar.shopkeeping.util.Log;
import com.fajar.shopkeeping.util.StringUtil;

import lombok.Data;

@Data
public class SellingTransactionPage  extends BaseTransactionPage{

	
	private long customerId;  
	
	private Customer selectedCustomer;
	private JComboBox customerComboBox;
	private JLabel labelRemainingQty;
	private JLabel labelProductPrice;
	
	public SellingTransactionPage() { 
		super("Transaction", BASE_WIDTH, BASE_HEIGHT, "Selling");
		
	}  
	
	@Override
	protected void initEvent() {
		 addActionListener(buttonClearCart, buttonClearListener());
		 addActionListener(buttonSubmitTransaction, submitTransactionListener());
		 addActionListener(buttonSubmitCart, buttonSubmitToCartListener());
		
		//fields  
		addKeyListener(inputQtyField, textFieldKeyListener(inputQtyField, "quantity"), false);	 
		super.initEvent();
	} 
	
	@Override
	protected ActionListener submitTransactionListener() { 
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int confirm = Dialogs.confirm("Continue submit the Transaction?"); 
				if(confirm != 0) { 
					return;
				}
				getHandler().transactionSell(getProductFlows(), getSelectedCustomer());
			}
		};
	}

	/**
	 * submit current data to cart
	 */
	protected void submitToCart() {  
		
		boolean productValid = validateSelectedProduct();
		
		if(productValid == false) {
			return;
		}
		
		if(quantity > selectedProduct.getCount()) {
			Dialogs.error("Product insufficient!");
			return;
		}
		
		ProductFlow productFlow = new ProductFlow();
		productFlow.setProduct(selectedProduct);
		productFlow.setCount(quantity);
		productFlow.setPrice(selectedProduct.getPrice());
//		productFlow.setExpiryDate(expiryDate); 
			
		if (editMode) {
			removeProductFlow(selectedProduct.getId()); 
		}
		
		addProductFlow(productFlow);
		
		clearForm(false);
		Log.log("product flow: ", productFlow);
		refresh();
		
	}  
	
	
	@Override
	protected void populateForm(ProductFlow productFlow, BaseEntity supplierOrCustomer) {
		
		managedProductFlow = productFlow;
		selectedProduct = productFlow.getProduct();
		quantity = productFlow.getCount(); 
		
		productComboBox.setSelectedItem(productFlow.getProduct().getName());
		inputQtyField.setText(String.valueOf(quantity));
		
		setText(labelProductUnit, selectedProduct.getUnit().getName());
		setText(labelProductPrice, beautifyNominal(selectedProduct.getPrice())); 
		setText(labelRemainingQty, beautifyNominal(selectedProduct.getCount()));
		

		if( supplierOrCustomer != null) {
			customerComboBox.setSelectedItem(((Customer)supplierOrCustomer).getName());
		}
		
	}
	
	/**
	 * clear input form
	 * @param clearCustomer
	 */
	protected void clearForm(boolean clearCustomer) {
		
		selectedProduct = null;
		managedProductFlow = null;
		quantity = 0; 

		clearComboBox(productComboBox);
		clearTextField(inputQtyField);
		clearLabel(labelProductPrice); 
		clearLabel(labelProductUnit);
		clearLabel(labelRemainingQty);
		
		if(clearCustomer) {
			selectedCustomer = null;
			clearComboBox(customerComboBox);
			labelTotalPrice.setText("0");
		}
		
		setEditMode(false);
		
	}

	@Override
	public void setSelectedProduct(Product product) {
		
		if(null == product || null == product.getUnit()) {
			Log.log("product is null");
			return;
		}
		
		this.selectedProduct = product;
		setText(labelProductUnit, product.getUnit().getName());
		setText(labelRemainingQty, beautifyNominal(product.getCount()));
		setText(labelProductPrice, beautifyNominal(product.getPrice()));
		Log.log("Selected product: ", product);
		
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
 		
 		labelRemainingQty = label("Remaining Quantity", LEFT);
 		labelProductPrice = label("Price @ Unit", LEFT);
 		labelTotalPrice = label("Total Price", LEFT);
 		labelTotalPrice.setSize(300, 20);
		 		
 		PanelRequest panelRequest = PanelRequest.autoPanelNonScroll(2, 200, 5, Color.LIGHT_GRAY);
		JPanel panel = ComponentBuilder.buildPanelV2(panelRequest , 
				label("Customer", LEFT), customerComboBox,
				label("Product", LEFT), productComboBox,
				label("Unit Price", LEFT), labelProductPrice,
				label("Stock", LEFT), labelRemainingQty,
				label("Quantity", LEFT), inputQtyField,
				label("Unit", LEFT), labelProductUnit, 
				buttonSubmitCart, buttonClearCart,
				buttonSubmitTransaction, null,
				label("Total Price"), labelTotalPrice
				);
		return panel ;
	}

	@Override
	protected JPanel buildProductListPanel() {
		if(null == productFlows) {
			productFlows = new ArrayList<ProductFlow>();
		} 
		
		long grandTotalPrice = 0l;
		int colSize = 7;
		int columnWidth = 100;
		Component[] rowComponents = new Component[1 + productFlows.size()];
		rowComponents[0] = rowPanelHeader(colSize, columnWidth, "No", "Flow Id", "Product", "Quantity", "Price @unit", "Total Price",  "Option");
		
		for (int i = 0; i < productFlows.size(); i++) {
			ProductFlow productFlow = productFlows.get(i); 
			
			JButton buttonEdit = button("edit", 100, editProductFlow(productFlow));
			JButton buttonRemove = button("remove", 100, buttonRemoveListener(productFlow)); 
			JPanel buttonField = ComponentBuilder.buildVerticallyInlineComponent(100, buttonEdit, buttonRemove);  
			long totalPrice = productFlow.getCount() * productFlow.getPrice();
			rowComponents[i + 1] = rowPanel(colSize, columnWidth, 
					(i+1),
					0,
					productFlow.getProduct().getName(),
					beautifyNominal(productFlow.getCount()),
					beautifyNominal(productFlow.getPrice()), 
					beautifyNominal(totalPrice),
					buttonField);
			
			grandTotalPrice+=totalPrice;
		}
		 
		PanelRequest panelRequest = autoPanelScrollWidthHeightSpecified(1, columnWidth * colSize, 5, Color.LIGHT_GRAY, 600, 250);
		
		setText(labelTotalPrice, beautifyNominal(grandTotalPrice));
		
		JPanel panel = buildPanelV2(panelRequest, (rowComponents));
		return panel;
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
				Log.log("product: ",product.getCode());
				getHandler().getProductDetail(product.getCode());
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

	public void callbackGetProductDetail(ShopApiResponse response) {
		Log.log("callbackGetProductDetail: ",response);
		try {
			 Product product = (Product) response.getEntities().get(0);
			 setSelectedProduct(product);
		}catch (Exception e) {
			e.printStackTrace();
			Log.log("Error set selected product:", e.getMessage());
		}
	}

	 
	/**
	 * handle response when selling transaction has been performed
	 * @param response
	 */
	public void callbackTransactionSell(ShopApiResponse response) {
		Transaction transaction = response.getTransaction();
		String tranCode = transaction.getCode();
		
		Dialogs.info("Success: "+tranCode);
		
		clearProductFlows();
		clearForm(true);
		refresh();
	}

}
