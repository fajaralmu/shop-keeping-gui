package com.fajar.shopkeeping.pages.transaction;

import static com.fajar.shopkeeping.component.builder.ComponentActionListeners.addActionListener;
import static com.fajar.shopkeeping.component.builder.ComponentActionListeners.addKeyListener;
import static com.fajar.shopkeeping.component.builder.ComponentBuilder.buildInlineComponentv2;
import static com.fajar.shopkeeping.component.builder.ComponentBuilder.label;
import static com.fajar.shopkeeping.component.builder.InputComponentBuilder.clearComboBox;
import static com.fajar.shopkeeping.component.builder.InputComponentBuilder.clearDateChooser;
import static com.fajar.shopkeeping.component.builder.InputComponentBuilder.clearLabel;
import static com.fajar.shopkeeping.component.builder.InputComponentBuilder.clearTextField;
import static com.fajar.shopkeeping.component.builder.InputComponentBuilder.setText;
import static com.fajar.shopkeeping.pages.transaction.BaseTransactionPage.DropDownType.PRODUCT;
import static com.fajar.shopkeeping.pages.transaction.BaseTransactionPage.DropDownType.SUPPLIER;
import static javax.swing.SwingConstants.LEFT;

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
import javax.swing.JTextField;

import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.component.Dialogs;
import com.fajar.shopkeeping.component.builder.ComponentBuilder;
import com.fajar.shopkeeping.component.builder.ComponentModifier;
import com.fajar.shopkeeping.component.builder.InputComponentBuilder;
import com.fajar.shopkeeping.model.PanelRequest;
import com.fajar.shopkeeping.util.DateUtil;
import com.fajar.shopkeeping.util.Log;
import com.fajar.shopkeeping.util.StringUtil;
import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.entity.BaseEntity;
import com.fajar.shoppingmart.entity.Product;
import com.fajar.shoppingmart.entity.ProductFlow;
import com.fajar.shoppingmart.entity.Supplier;
import com.fajar.shoppingmart.entity.Transaction;
import com.toedter.calendar.JDateChooser;

import lombok.Data;

@Data
public class PurchasingTransactionPage extends BaseTransactionPage {   
	
	private long unitPrice;
	private String productCode, supplierCode;
	private Date expiryDate = new Date();
	
	private Supplier selectedSupplier;
	
	//fields
	private JComboBox supplierComboBox;  
	private JTextField inputUnitPriceField;
	private JTextField inputSupplierCode;
	private JTextField InputProductCode;
	private JLabel labelCurrentPrice;
	private JDateChooser inputExpiredDateField; 
	
	//buttons
	private JButton buttonSearchSupplierByCode;
	private JButton buttonSearchProductByCode;

	public PurchasingTransactionPage() { 
		super("Transaction", BASE_WIDTH, BASE_HEIGHT+20, "Supply");
		
	}  
	
	/**
	 * build table
	 * @return
	 */
	@Override
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
			JButton buttonEdit 		= ComponentBuilder.button("edit", 100, editProductFlow(productFlow));
			JButton buttonRemove 	= ComponentBuilder.button("remove", 100, buttonRemoveListener(productFlow)); 
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
	@Override
	protected JPanel buildFormPanel() {
		//supplier
		ActionListener actionListener = dynamicDropdownActionListener(SUPPLIER);
		KeyListener keyListener = dynamicDropdownFieldKeyListener(SUPPLIER); 
		supplierComboBox = ComponentBuilder.buildEditableComboBox("", keyListener, actionListener, "type supplier name..");
		buttonSearchSupplierByCode = ComponentBuilder.button("Search");
		
		//product
		ActionListener actionListenerProduct = dynamicDropdownActionListener(PRODUCT);
		KeyListener keyListenerProduct = dynamicDropdownFieldKeyListener(PRODUCT); 
		productComboBox = ComponentBuilder.buildEditableComboBox("", keyListenerProduct, actionListenerProduct, "type product name.."); 
		buttonSearchProductByCode = ComponentBuilder.button("Search");
		
		inputQtyField = InputComponentBuilder.numberField("0");
		inputSupplierCode = InputComponentBuilder.textField("");
		InputProductCode = InputComponentBuilder.textField("");
		inputUnitPriceField = InputComponentBuilder.numberField("0");
 		inputExpiredDateField = InputComponentBuilder.dateChooser(new Date());
 		
 		labelProductUnit = label("unit", LEFT);
 		labelProductUnit.setSize(200, 20); 
 		labelTotalPrice = label("total price", LEFT);
 		labelTotalPrice.setSize(300, 20);
 		labelCurrentPrice = label("");
 		labelCurrentPrice.setSize(300, 20);
 		
		PanelRequest panelRequest = getFormFieldPanelRequest();
		JPanel panel = ComponentBuilder.buildPanelV3(panelRequest , 
				label("Supplier Name", LEFT), supplierComboBox,
				label("Or Supplier Code", LEFT), buildInlineComponentv2(inputSupplierCode, buttonSearchSupplierByCode),
				label("Product Name", LEFT), productComboBox,
				label("Or Product Code", LEFT), buildInlineComponentv2(InputProductCode, buttonSearchProductByCode),
				label("Quantity", LEFT), inputQtyField,
				label("Unit", LEFT), labelProductUnit,
				label("Curr Price", LEFT), labelCurrentPrice,
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
	@Override
	protected void clearForm(boolean clearSupplier) {
		
		selectedProduct = null;
		managedProductFlow = null;
		quantity = 0;
		unitPrice = 0;
		expiryDate = new Date(); 

		clearComboBox(productComboBox);
		clearTextField(inputQtyField);
		clearTextField(InputProductCode);
		clearTextField(inputUnitPriceField);
		clearDateChooser(inputExpiredDateField);
		clearLabel(labelProductUnit);
		clearLabel(labelCurrentPrice);
		
		if(clearSupplier) {
			selectedSupplier = null;
			clearComboBox(supplierComboBox);
			clearTextField(inputSupplierCode);
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
		InputProductCode.setText(productFlow.getProduct().getCode());
		
		inputExpiredDateField.setDate(expiryDate);
		inputQtyField.setText(String.valueOf(quantity));
		inputUnitPriceField.setText(String.valueOf(unitPrice));
		
		labelProductUnit.setText(selectedProduct.getUnit().getName()); 
		

		if(supplierOrCustomer != null) {
			supplierComboBox.setSelectedItem(((Supplier)supplierOrCustomer).getName());
			inputSupplierCode.setText(((Supplier)supplierOrCustomer).getId().toString());
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
		setText(inputSupplierCode, supplier.getId());
		supplierComboBox.setSelectedItem(supplier.getName());
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
		labelCurrentPrice.setText(StringUtil.beautifyNominal(product.getPrice()));
		setText(InputProductCode, product.getCode());
		productComboBox.setSelectedItem(product.getName());
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
		 addActionListener(buttonSearchProductByCode, this::searchProductByCode);
		 addActionListener(buttonSearchSupplierByCode, this::searchSupplierByCode);
		 
		 //fields  
		 addKeyListener(inputQtyField, textFieldKeyListener(inputQtyField, "quantity"), false);	 
		 addKeyListener(inputSupplierCode, textFieldKeyListener(inputSupplierCode, "supplierCode"), false);	 
		 addKeyListener(InputProductCode, textFieldKeyListener(InputProductCode, "productCode"), false);	 
		 addKeyListener(inputUnitPriceField, textFieldKeyListener(inputUnitPriceField, "unitPrice"), false);
		 addActionListener(inputExpiredDateField, dateChooserListener(inputExpiredDateField, "expiryDate")); 
		 
	}
	
	private void searchProductByCode(ActionEvent e) {
		MyCallback<WebResponse> callback = (WebResponse)->{
			if(WebResponse.getEntities().size()>0)
				setSelectedProduct((Product) WebResponse.getEntities().get(0));
		};
		getHandler().getExactEntity(Product.class, "code", productCode, callback);
	}
	
	private void searchSupplierByCode(ActionEvent e) {

		MyCallback<WebResponse> callback = (WebResponse)->{
			if(WebResponse.getEntities().size()>0)
				setSelectedSupplier((Supplier) WebResponse.getEntities().get(0));
		};
		getHandler().getExactEntity(Supplier.class, "id", supplierCode, callback);
	}
	
	@Override
	protected ActionListener submitTransactionListener() { 
		return  (ActionEvent e)-> {
			int confirm = Dialogs.confirm("Continue submit the Transaction?"); 
			if(confirm != 0) { 
				return;
			}
			getHandler().transactionPurchasing(productFlows, selectedSupplier); 
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
	public void callbackTransactionPurchasing(WebResponse response) {
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
