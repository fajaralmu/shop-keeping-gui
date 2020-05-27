package com.fajar.shopkeeping.pages;

import static com.fajar.shopkeeping.component.ComponentBuilder.label;
import static com.fajar.shopkeeping.model.PanelRequest.autoPanelScrollWidthHeightSpecified;
import static com.fajar.shopkeeping.pages.BaseTransactionPage.DropDownType.PRODUCT;
import static com.fajar.shopkeeping.pages.BaseTransactionPage.DropDownType.SUPPLIER;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.fajar.dto.WebResponse;
import com.fajar.entity.BaseEntity;
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
import com.toedter.calendar.JDateChooser;

import lombok.Data;

@Data
public class SupplyTransactionPage extends BaseTransactionPage {   
	
	private long supplierId;
	private long unitPrice;
	private Date expiryDate = new Date();
	
	private Supplier selectedSupplier;
	
	//fields
	private JComboBox supplierComboBox;  
	private JTextField inputUnitPriceField;
	private JDateChooser inputExpiredDateField; 

	public SupplyTransactionPage() { 
		super("Transaction", BASE_WIDTH, BASE_HEIGHT, "Supply");
		
	}  
	
	/**
	 * build table
	 * @return
	 */
	protected JPanel buildProductListPanel() {
		 
		if(null == productFlows) {
			productFlows = new ArrayList<ProductFlow>();
		} 
		
		long grandTotalPrice = 0l;
		int colSize = 8;
		int columnWidth = 100;
		
		Object[] tableHeader = new Object[] {"No", "Flow Id", "Product", "Quantity", "Price @unit", "Total Price", "Exp Date", "Option"};
		Component[] rowComponents = new Component[1 + productFlows.size()];
		
		rowComponents[0] = rowPanelHeader(colSize, columnWidth, 
				tableHeader);
		
		for (int i = 0; i < productFlows.size(); i++) {
			ProductFlow productFlow = productFlows.get(i);
			
			Date expDate 			= productFlow.getExpiryDate() == null ? new Date() : productFlow.getExpiryDate();  
			JButton buttonEdit 		= button("edit", 100, editProductFlow(productFlow));
			JButton buttonRemove 	= button("remove", 100, buttonRemoveListener(productFlow)); 
			JPanel buttonField 		= ComponentBuilder.buildVerticallyInlineComponent(100, buttonEdit, buttonRemove); 
			String dateString 		= DateUtil.formatDate(expDate, "dd-MM-yyyy");
			
			long totalPrice = productFlow.getCount() * productFlow.getPrice();
			
			rowComponents[i + 1] 	= rowPanel(colSize, columnWidth, 
					(i+1),
					0,
					productFlow.getProduct().getName(),
					StringUtil.beautifyNominal(productFlow.getCount()),
					StringUtil.beautifyNominal(productFlow.getPrice()), 
					StringUtil.beautifyNominal(totalPrice),
					dateString,
					buttonField);
			
			grandTotalPrice+=totalPrice;
		}
		 
		PanelRequest panelRequest = getProductListPanelRequest(columnWidth, colSize);
		
		setText(labelTotalPrice, StringUtil.beautifyNominal(grandTotalPrice));
		
		JPanel panel = buildPanelV2(panelRequest, (rowComponents));
		return panel;
	}
	  
	 
	/**
	 * transaction fields
	 * @return
	 */
	protected JPanel buildFormPanel() {
		//supplier
		ActionListener actionListener = dynamicDropdownActionListener(SUPPLIER);
		KeyListener keyListener = dynamicDropdownFieldKeyListener(SUPPLIER); 
		supplierComboBox = ComponentBuilder.buildEditableComboBox("", keyListener, actionListener, "type supplier name..");
		
		//product
		ActionListener actionListenerProduct = dynamicDropdownActionListener(PRODUCT);
		KeyListener keyListenerProduct = dynamicDropdownFieldKeyListener(PRODUCT); 
		productComboBox = ComponentBuilder.buildEditableComboBox("", keyListenerProduct, actionListenerProduct, "type product name.."); 
		
		inputQtyField = numberField("0");
		inputUnitPriceField = numberField("0");
 		inputExpiredDateField = dateChooser(new Date());
 		
 		labelProductUnit = label("unit", LEFT);
 		labelProductUnit.setSize(200, 20); 
 		labelTotalPrice = label("total price", LEFT);
 		labelTotalPrice.setSize(300, 20);
 		
		PanelRequest panelRequest = getFormFieldPanelRequest();
		JPanel panel = ComponentBuilder.buildPanelV2(panelRequest , 
				label("Supplier", LEFT), supplierComboBox,
				label("Product", LEFT), productComboBox,
				label("Quantity", LEFT), inputQtyField,
				label("Unit", LEFT), labelProductUnit,
				label("Unit Price", LEFT), inputUnitPriceField,
				label("Expired Date", LEFT), inputExpiredDateField,
				buttonSubmitCart, buttonClearCart,
				buttonSubmitTransaction, null,
				label("Total Price"), labelTotalPrice
				);
		return panel ;
	}
	
	/**
	 * clear input form
	 * @param clearSupplier
	 */
	protected void clearForm(boolean clearSupplier) {
		
		selectedProduct = null;
		managedProductFlow = null;
		quantity = 0;
		unitPrice = 0;
		expiryDate = new Date(); 

		clearComboBox(productComboBox);
		clearTextField(inputQtyField);
		clearTextField(inputUnitPriceField);
		clearDateChooser(inputExpiredDateField);
		clearLabel(labelProductUnit);
		
		if(clearSupplier) {
			selectedSupplier = null;
			clearComboBox(supplierComboBox);
			labelTotalPrice.setText("0");
		}
		
		setEditMode(false);
		
	}
	
	protected void populateForm(ProductFlow productFlow, BaseEntity supplierOrCustomer) {
		
		managedProductFlow = productFlow;
		selectedProduct = productFlow.getProduct();
		quantity = productFlow.getCount();
		expiryDate = productFlow.getExpiryDate() == null ?new Date(): productFlow.getExpiryDate();
		unitPrice = productFlow.getPrice();
		
		productComboBox.setSelectedItem(productFlow.getProduct().getName());
		inputExpiredDateField.setDate(expiryDate);
		inputQtyField.setText(String.valueOf(quantity));
		inputUnitPriceField.setText(String.valueOf(unitPrice));
		
		labelProductUnit.setText(selectedProduct.getUnit().getName()); 
		

		if(supplierOrCustomer != null) {
			supplierComboBox.setSelectedItem(((Supplier)supplierOrCustomer).getName());
		}
		
	} 
	
	
	private void setSelectetSupplierByName(String string) {
		 
		for (Supplier supplier : supplierDropdownValues) {
			if(supplier.getName().equals(string)) {
				setSelectedSupplier(supplier);
			}
		}
	}
	
	/**
	 * set selected supplier 
	 * @param supplier
	 */
	private void setSelectedSupplier(Supplier supplier) { 
		selectedSupplier = supplier;
		Log.log("selectedSupplier: ",selectedSupplier);
	}

	/**
	 * set selected product by name
	 * @param name
	 */
	private void setSelectedProductByName(String name) { 
		for (Product product : productDropdownValues) {
			if(product.getName().equals(name)) {
				setSelectedProduct (product);
			}
		}
	}
	 
	@Override
	public void setSelectedProduct (Product product) { 
		this.selectedProduct = product;
		labelProductUnit.setText(product.getUnit().getName());
	}
	
	@Override
	protected void handleDynamicDropdownChange(final DropDownType dropDownType, String selectedValue) {
		Log.log("handleDynamicDropdownChange: ",selectedValue);
		if(selectedValue != null) {
			 
			 switch (dropDownType) {
				case SUPPLIER:
					setSelectetSupplierByName(selectedValue );
					break;
				case PRODUCT:
					setSelectedProductByName(selectedValue );
					break;
				default:
					break;
			} 
		 }
	} 

	@Override
	protected void initEvent() {
		super.initEvent();
		
		 addActionListener(buttonClearCart, buttonClearListener());
		 addActionListener(buttonSubmitTransaction, submitTransactionListener());
		 addActionListener(buttonSubmitCart, buttonSubmitToCartListener());
		 
		 //fields  
		 addKeyListener(inputQtyField, textFieldKeyListener(inputQtyField, "quantity"), false);	 
		 addKeyListener(inputUnitPriceField, textFieldKeyListener(inputUnitPriceField, "unitPrice"), false);
		 addActionListener(inputExpiredDateField, dateChooserListener(inputExpiredDateField, "expiryDate")); 
		 
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
				getHandler().transactionSupply(productFlows, selectedSupplier);
				
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
		
		ProductFlow productFlow = new ProductFlow();
		productFlow.setProduct(selectedProduct);
		productFlow.setCount(quantity);
		productFlow.setPrice(unitPrice);
		productFlow.setExpiryDate(expiryDate); 
			
		if (editMode) {
			removeProductFlow(selectedProduct.getId()); 
		}
		
		addProductFlow(productFlow);
		
		clearForm(false);
		Log.log("product flow: ", productFlow);
		refresh();
		
	}  

	/**
	 * handle response when supply transaction has been performed
	 * @param response
	 */
	public void callbackTransactionSupply(WebResponse response) {
		Transaction transaction = response.getTransaction();
		String tranCode = transaction.getCode();
		
		Dialogs.info("Success: "+tranCode);
		
		clearProductFlows();
		clearForm(true);
		refresh();
	}
 
	@Override
	public void show() {
		titleLabel.setText("Pembelian");
		super.show();
	}

}
