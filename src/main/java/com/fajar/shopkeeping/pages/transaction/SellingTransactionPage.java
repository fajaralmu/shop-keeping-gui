package com.fajar.shopkeeping.pages.transaction;

import static com.fajar.shopkeeping.component.builder.ComponentActionListeners.addActionListener;
import static com.fajar.shopkeeping.component.builder.ComponentActionListeners.addKeyListener;
import static com.fajar.shopkeeping.component.builder.ComponentBuilder.buildInlineComponent;
import static com.fajar.shopkeeping.component.builder.ComponentBuilder.buildInlineComponentv2;
import static com.fajar.shopkeeping.component.builder.ComponentBuilder.label;
import static com.fajar.shopkeeping.component.builder.InputComponentBuilder.clearComboBox;
import static com.fajar.shopkeeping.component.builder.InputComponentBuilder.clearLabel;
import static com.fajar.shopkeeping.component.builder.InputComponentBuilder.clearTextField;
import static com.fajar.shopkeeping.component.builder.InputComponentBuilder.setText;
import static com.fajar.shopkeeping.pages.transaction.BaseTransactionPage.DropDownType.CUSTOMER;
import static com.fajar.shopkeeping.pages.transaction.BaseTransactionPage.DropDownType.PRODUCT;
import static com.fajar.shopkeeping.util.StringUtil.beautifyNominal;
import static javax.swing.SwingConstants.LEFT;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.fajar.shopkeeping.callbacks.Listeners;
import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.component.Dialogs;
import com.fajar.shopkeeping.component.builder.ComponentBuilder;
import com.fajar.shopkeeping.component.builder.InputComponentBuilder;
import com.fajar.shopkeeping.model.PanelRequest;
import com.fajar.shopkeeping.util.Log;
import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.entity.BaseEntity;
import com.fajar.shoppingmart.entity.Customer;
import com.fajar.shoppingmart.entity.Product;
import com.fajar.shoppingmart.entity.ProductFlow;
import com.fajar.shoppingmart.entity.Transaction;

import lombok.Data;

@Data
public class SellingTransactionPage  extends BaseTransactionPage{

	private String customerCode, productCode;
	
	private Customer selectedCustomer;
	private JComboBox customerComboBox;
	private JLabel labelRemainingQty;
	private JLabel labelProductPrice;
	private JTextField inputCustomerCode;
	private JTextField inputProductCode;
	private JTextField inputCustomerPayment;
	private JLabel labelTotalChange;
	
	//buttons
	private JButton buttonSearchCustomerByCode;
	private JButton buttonSearchProductByCode;
	
	public SellingTransactionPage() { 
		super("Transaction", BASE_WIDTH, BASE_HEIGHT+20, "Selling");
		
	}  
	
	@Override
	protected void initEvent() {
		 addActionListener(buttonClearCart, buttonClearListener());
		 addActionListener(buttonSubmitTransaction, submitTransactionListener());
		 addActionListener(buttonSubmitCart, buttonSubmitToCartListener());
		 addActionListener(buttonSearchCustomerByCode, this::searchCustomerByCode);
		 addActionListener(buttonSearchProductByCode, getProductByCodeListener());
		
		//fields  
		addKeyListener(inputQtyField, textFieldKeyListener(inputQtyField, "quantity"), false);	
		addKeyListener(inputProductCode, textFieldKeyListener(inputProductCode, "productCode"), false);	
		addKeyListener(inputCustomerCode, textFieldKeyListener(inputCustomerCode, "customerCode"), false);	
		addKeyListener(inputCustomerPayment, inputCustomerPaymentKeyListener(), false);
		super.initEvent();
	} 
	
	private ActionListener getProductByCodeListener() {
		return (e) -> {
			getHandler().getProductDetail(productCode);
		};
	}

	private void searchCustomerByCode(ActionEvent e) {
		MyCallback<WebResponse> callback = (response)->{
			if(response.getEntities().size()>0) {
				setSelectedCustomer((Customer) response.getEntities().get(0));
			}
		};
		getHandler().getExactEntity(Customer.class, "id", customerCode, callback );
	}
	
	private KeyListener inputCustomerPaymentKeyListener() { 
		return Listeners.keyReleasedOnlyListener((e) -> { 
				JTextField textfield = (JTextField) e.getSource();
				try {
					long value = Long.valueOf(textfield.getText());
					long change = value - grandTotalPrice;
					
					if(change > 0l) {					
						setText(labelTotalChange, beautifyNominal(change));
					}else {
						setText(labelTotalChange, "( - )" + beautifyNominal(Math.abs(change)));
					}
					
				} catch (Exception e2) { }
			});
	}

	@Override
	protected ActionListener submitTransactionListener() { 
		return (ActionEvent e)->{
			int confirm = Dialogs.confirm("Continue submit the Transaction?"); 
			if(confirm != 0) { 
				return;
			}
			getHandler().transactionSell(getProductFlows(), getSelectedCustomer()); 
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
		clearLabel(labelTotalChange); 
		clearTextField(inputProductCode);
		
		if(clearCustomer) {
			selectedCustomer = null;
			clearComboBox(customerComboBox);
			labelTotalPrice.setText("0");
			clearTextField(inputCustomerPayment);
			clearTextField(inputCustomerCode);
			grandTotalPrice = 0l;
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
		setText(inputProductCode, product.getCode());
		productComboBox.setSelectedItem(product.getName());
		Log.log("Selected product: ", product);
		
	}

	@Override
	protected JPanel buildFormPanel() {
		//CUSTOMER
		ActionListener actionListener = dynamicDropdownActionListener(CUSTOMER);
		KeyListener keyListener = dynamicDropdownFieldKeyListener(CUSTOMER); 
		customerComboBox = ComponentBuilder.buildEditableComboBox("", keyListener, actionListener, "type customer name..");
		buttonSearchCustomerByCode = ComponentBuilder.button("Search");
		inputCustomerCode = InputComponentBuilder.textField("");
				
		//product
		ActionListener actionListenerProduct = dynamicDropdownActionListener(PRODUCT);
		KeyListener keyListenerProduct = dynamicDropdownFieldKeyListener(PRODUCT); 
		productComboBox = ComponentBuilder.buildEditableComboBox("", keyListenerProduct, actionListenerProduct, "type product name.."); 
		buttonSearchProductByCode = ComponentBuilder.button("Search");
		inputProductCode = InputComponentBuilder.textField("");
		
		inputQtyField = InputComponentBuilder.numberField("0"); 
		inputCustomerPayment = InputComponentBuilder.numberField("0");
		 		
 		labelProductUnit = label("unit", LEFT);
 		labelProductUnit.setSize(200, 20);
 		
 		labelRemainingQty = label("Remaining Quantity", LEFT);
 		labelProductPrice = label("Price @ Unit", LEFT);
 		
 		labelTotalPrice = label("Total Price", LEFT);
 		labelTotalPrice.setSize(300, 20);
 		
 		labelTotalChange = label("",LEFT);
 		labelTotalChange.setSize(300, 20);
		 		
 		JPanel panelRemainingStock = buildInlineComponent(140, labelRemainingQty, labelProductUnit);
 		
 		PanelRequest panelRequest = getFormFieldPanelRequest();
		JPanel panel = ComponentBuilder.buildPanelV3(panelRequest , 
				label("Customer Name", LEFT), customerComboBox,
				label("Or Customer Code", LEFT), buildInlineComponentv2(inputCustomerCode, buttonSearchCustomerByCode),
				label("Product Name", LEFT), productComboBox,
				label("Or Product Code", LEFT), buildInlineComponentv2(inputProductCode, buttonSearchProductByCode),
				label("Unit Price", LEFT), labelProductPrice,
				label("Stock", LEFT), panelRemainingStock,
				label("Quantity", LEFT), inputQtyField,
//				label("Unit", LEFT), labelProductUnit, 
				buttonSubmitCart, buttonClearCart,
				buttonSubmitTransaction, null,
				label("Total Price", LEFT), labelTotalPrice,
				label("Payment", LEFT), inputCustomerPayment,
				label("Change", LEFT), labelTotalChange
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
		
		Object[] tableHeader = new Object[] { "No", "Flow Id", "Product", "Quantity", "Price @unit", "Total Price",  "Option"};
		Component[] rowComponents = new Component[1 + productFlows.size()];
		
		rowComponents[0] = rowPanelHeader(colSize, columnWidth, tableHeader);
		
		for (int i = 0; i < productFlows.size(); i++) {
			ProductFlow productFlow = productFlows.get(i); 
			
			JButton buttonEdit = ComponentBuilder.button("edit", 100, editProductFlow(productFlow));
			JButton buttonRemove = ComponentBuilder.button("remove", 100, buttonRemoveListener(productFlow)); 
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
		 
		PanelRequest panelRequest = getProductListPanelRequest(columnWidth, colSize);
		
		setText(labelTotalPrice, beautifyNominal(grandTotalPrice));
		this.grandTotalPrice = grandTotalPrice;
		
		JPanel panel = buildPanelV2(panelRequest, (rowComponents));
		return panel;
	}

	private void setSelectedCustomer(Customer customer) {
		this.selectedCustomer = customer;
		inputCustomerCode.setText(customer.getId().toString());
		customerComboBox.setSelectedItem(customer.getName());
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

	public void callbackGetProductDetail(WebResponse response) {
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
	public void callbackTransactionSell(WebResponse response) {
		Transaction transaction = response.getTransaction();
		String tranCode = transaction.getCode();
		
		Dialogs.info("Success: "+tranCode);
		
		clearProductFlows();
		clearForm(true);
		refresh();
	}

	@Override
	public void show() {
		titleLabel.setText("Penjualan");
		super.show();
	}
}
