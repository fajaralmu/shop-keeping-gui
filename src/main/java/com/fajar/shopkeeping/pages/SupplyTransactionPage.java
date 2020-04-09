package com.fajar.shopkeeping.pages;

import static com.fajar.shopkeeping.model.PanelRequest.autoPanelScrollWidthHeightSpecified;
import static com.fajar.shopkeeping.pages.SupplyTransactionPage.DropDownType.PRODUCT;
import static com.fajar.shopkeeping.pages.SupplyTransactionPage.DropDownType.SUPPLIER;
import static javax.swing.SwingConstants.LEFT;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import com.fajar.dto.ShopApiResponse;
import com.fajar.entity.BaseEntity;
import com.fajar.entity.Product;
import com.fajar.entity.ProductFlow;
import com.fajar.entity.Supplier;
import com.fajar.entity.Transaction;
import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.component.ComponentBuilder;
import com.fajar.shopkeeping.component.Dialogs;
import com.fajar.shopkeeping.constant.PageConstants;
import com.fajar.shopkeeping.handler.TransactionHandler;
import com.fajar.shopkeeping.model.PanelRequest;
import com.fajar.shopkeeping.util.DateUtil;
import com.fajar.shopkeeping.util.EntityUtil;
import com.fajar.shopkeeping.util.Log;
import com.fajar.shopkeeping.util.ThreadUtil;
import com.toedter.calendar.JDateChooser;

import lombok.Data;

@Data
public class SupplyTransactionPage extends BasePage { 
	
	private JPanel formPanel;
	private JPanel productListPanel;
	
	
	private long supplierId;
	private int quantity;
	private long unitPrice;
	private Date expiryDate = new Date();
	private Product selectedProduct;
	private Supplier selectedSupplier;
	
	//fields
	private JComboBox supplierComboBox; 
	private JComboBox productComboBox; 
	private JTextField inputQtyField;
	private JTextField inputUnitPriceField;
	private JDateChooser inputExpiredDateField;
	private JLabel labelProductUnit; 
	
	private JButton buttonSubmitCart;
	private JButton buttonClearCart;
	private JButton buttonSubmitTransaction;
	
	private List<ProductFlow> productFlows = new ArrayList<ProductFlow>(); 
	private final List<Product> productDropdownValues = new ArrayList<Product>();
	private final List<Supplier> supplierDropdownValues = new ArrayList<Supplier>();
	private ProductFlow managedProductFlow;
	
	private boolean editMode = false;
	 

	public SupplyTransactionPage() {
		super("Launcher", BASE_WIDTH, BASE_HEIGHT);
	} 

	@Override
	public void initComponent() { 
		
		PanelRequest panelRequest = new PanelRequest(1, 670, 20, 15, Color.WHITE, 30, 30, 0, 0, false, true);   
		
		
		if(formPanel == null) {
			formPanel = buildFormPanel();
		}
		
		productListPanel = buildProductListPanel(); 
		
		mainPanel = ComponentBuilder.buildPanelV2(panelRequest,

				title("Transaction Supply", 50),
				formPanel,
				productListPanel 
				); 

		parentPanel.add(mainPanel);
		exitOnClose();

	}
	
	
	@Override
	protected void constructMenu() {
		if(menuBar.getMenuCount()>0) {
			return;
		}
		
		setMenuBack(new JMenuItem("Back"));
		
		JMenu menu = new JMenu("Menu");
		menu.add(menuBack);
		menuBar.add(menu ); 
	}

	/**
	 * build table
	 * @return
	 */
	private JPanel buildProductListPanel() {
		 
		if(null == productFlows) {
			productFlows = new ArrayList<ProductFlow>();
		}
		
		
		int colSize = 7;
		int columnWidth = 100;
		Component[] rowComponents = new Component[1 + productFlows.size()];
		rowComponents[0] = rowPanelHeader(colSize, columnWidth, "No", "Flow Id", "Product", "Quantity", "Price @unit", "Exp Date", "Option");
		
		for (int i = 0; i < productFlows.size(); i++) {
			ProductFlow productFlow = productFlows.get(i);
			
			Date expDate = productFlow.getExpiryDate() == null ? new Date() : productFlow.getExpiryDate(); 
			
			JButton buttonEdit = button("edit", 100, editProductFlow(productFlow));
			JButton buttonRemove = button("remove", 100, buttonRemoveListener(productFlow));
			
			JPanel buttonField = ComponentBuilder.buildVerticallyInlineComponent(100, buttonEdit, buttonRemove);
			
			String dateString = DateUtil.parseDate(expDate, "dd-MM-yyyyy");
			
			rowComponents[i + 1] = rowPanel(colSize, columnWidth, 
					(i+1),
					0,
					productFlow.getProduct().getName(),
					productFlow.getCount(),
					productFlow.getPrice(),
					dateString,
					buttonField);
		}
		 
		PanelRequest panelRequest = autoPanelScrollWidthHeightSpecified(1, columnWidth * colSize, 5, Color.LIGHT_GRAY, 600, 300);
		
		JPanel panel = buildPanelV2(panelRequest, (rowComponents));
		return panel;
	}
	 

	private ActionListener buttonClearListener() {
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) { 
				ThreadUtil.run(new Runnable() {
					
					@Override
					public void run() {
						clearForm(false);
						setEditMode(false);
					}
				});
				
			}
		};
	}
	
	private ActionListener buttonRemoveListener(final ProductFlow productFlow) {
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int confirm = JOptionPane.showConfirmDialog(null, "Remove "+productFlow.getProduct().getName()+"?");
				
				if(confirm != 0) { 
					return;
				}
				
				ThreadUtil.run(new Runnable() {
					
					@Override
					public void run() {
						long productId = productFlow.getProduct().getId();
						removeProductFlow(productId);
						refresh();
						
					}
				});
				
			}
		};
	}
	
	private ActionListener editProductFlow(final ProductFlow productFlow) { 
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) { 
				final ProductFlow selectedProductFlow = getProductFlow(productFlow.getProduct().getId());
				if(selectedProductFlow == null) {
					Dialogs.showErrorDialog("selected product does not exist");
					return;
				}
				ThreadUtil.run(new Runnable() {
					
					@Override
					public void run() {
						populateForm(selectedProductFlow, null);
						setEditMode(true);
					}
				});
				
			}
		};
	}

	@Override
	public void refresh() {
		if(!refreshing) {
			setRefreshing(true);
			preInitComponent();
			initEvent();
			super.refresh();
			setRefreshing(false);
		}
	} 

	private ProductFlow getProductFlow(long productId) {
		for (ProductFlow productFlow : productFlows) {
			if(productFlow.getProduct().getId().equals(productId)) {
				return productFlow;
			}
		}
		
		return null;
	}
	
	/**
	 * transaction fields
	 * @return
	 */
	private JPanel buildFormPanel() {
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
 		
 		buttonSubmitCart = button("Submit To Cart");
 		buttonClearCart = button("Clear");
 		buttonSubmitTransaction = button("SUBMIT TRANSACTION");
 		
		PanelRequest panelRequest = PanelRequest.autoPanelNonScroll(2, 200, 5, Color.LIGHT_GRAY);
		JPanel panel = ComponentBuilder.buildPanelV2(panelRequest , 
				label("Supplier", LEFT), supplierComboBox,
				label("Product", LEFT), productComboBox,
				label("Quantity", LEFT), inputQtyField,
				label("Unit", LEFT), labelProductUnit,
				label("Unit Price", LEFT), inputUnitPriceField,
				label("Expired Date", LEFT), inputExpiredDateField,
				buttonSubmitCart, buttonClearCart,
				buttonSubmitTransaction, null
				);
		return panel ;
	}
	
	/**
	 * clear input form
	 * @param clearSupplier
	 */
	private void clearForm(boolean clearSupplier) {
		
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
		}
		
	}
	
	private void populateForm(ProductFlow productFlow, Supplier supplier) {
		
		managedProductFlow = productFlow;
		selectedProduct = productFlow.getProduct();
		quantity = productFlow.getCount();
		expiryDate = productFlow.getExpiryDate() == null ?new Date(): productFlow.getExpiryDate();
		unitPrice = productFlow.getPrice();
		
		productComboBox.setSelectedItem(productFlow.getProduct().getName());
		labelProductUnit.setText(selectedProduct.getUnit().getName());
		inputExpiredDateField.setDate(expiryDate);
		inputQtyField.setText(String.valueOf(quantity));
		inputUnitPriceField.setText(String.valueOf(unitPrice));
		

		if(supplier != null) {
			supplierComboBox.setSelectedItem(supplier.getName());
		}
		
	}
	
	private static void clearLabel(JLabel label) {
		label.setText("");
	}
	
	/**
	 * set date chooser to now
	 * @param dateChooser
	 */
	private static void clearDateChooser(JDateChooser dateChooser) {
		dateChooser.setDate(new Date());
	}
	
	/**
	 * set value to ""
	 * @param textField
	 */
	private static void clearTextField(JTextField textField) {
		textField.setText("");
	}
	
	/**
	 * remove all items and set selected value to "";
	 * @param comboBox
	 */
	private static void clearComboBox(JComboBox comboBox) {
		comboBox.removeAllItems();
		comboBox.setSelectedItem("");
	}

	/**
	 * when product dropDown typed
	 * @return
	 */
	private KeyListener dynamicDropdownFieldKeyListener(final DropDownType dropDownType) { 
		return new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent event) {
				
				final JComboBox dynamicComboBox = getComboBox(event);
				final String componentText = getComboBoxText(dynamicComboBox);
				Log.log("typed: ",componentText);
				
				Class entityClass = dropDownType.equals(PRODUCT) ? Product.class : Supplier.class;
				
				getHandler().getEntitiesFromDynamicDropdown(entityClass, "name", componentText, new MyCallback() {
					
					@Override
					public void handle(Object... params) throws Exception { 
						ShopApiResponse response = (ShopApiResponse) params[0];
						Log.log("entities: ", response.getEntities()); 
						
						if(dropDownType.equals(PRODUCT)) {	
							
							productDropdownValues.clear();
							for(BaseEntity product:response.getEntities()) {
								productDropdownValues.add((Product) product);
							}
							
							populateDropdown(componentText, productDropdownValues, "name", dynamicComboBox);
							
						}else if(dropDownType.equals(SUPPLIER)) {
							
							supplierDropdownValues.clear();
							for(BaseEntity product:response.getEntities()) {
								supplierDropdownValues.add((Supplier) product);
							}
							
							populateDropdown(componentText, supplierDropdownValues, "name", dynamicComboBox);
						}
					}
					
					
				});
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
		};
	}
	
	/**
	 * fill comboBox with values
	 * @param componentText
	 * @param objects
	 * @param fieldName
	 * @param dynamicComboBox
	 */
	private static void populateDropdown(String componentText, List objects, String fieldName, JComboBox dynamicComboBox) {
		dynamicComboBox.removeAllItems();
		
		for (Object object : objects) { 
			try {
				Field field = EntityUtil.getDeclaredField(object.getClass(), fieldName);
				field.setAccessible(true);
				dynamicComboBox.addItem(field.get(object));
			}catch (Exception e) {
				// TODO: handle exception
			}
		} 
		dynamicComboBox.setSelectedItem(componentText);
		((JTextComponent) dynamicComboBox.getEditor().getEditorComponent()).setSelectionStart(componentText.length());
		((JTextComponent) dynamicComboBox.getEditor().getEditorComponent()).setSelectionEnd(componentText.length());
		dynamicComboBox.showPopup();
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
				setSelectedProduct(product);
			}
		}
	}
	
	private void setSelectedProduct(Product product) { 
		this.selectedProduct = product;
		labelProductUnit.setText(product.getUnit().getName());
	}

	/**
	 * when product dropDown selected
	 * @return
	 */
	private ActionListener dynamicDropdownActionListener(final DropDownType dropDownType) {
		 
		return  new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) { 
				 final JComboBox inputComponent = (JComboBox) e.getSource();
				 Object selectedValue = inputComponent.getSelectedItem();
				 if(selectedValue != null) {
					 
					 switch (dropDownType) {
						case SUPPLIER:
							setSelectetSupplierByName(selectedValue.toString());
							break;
						case PRODUCT:
							setSelectedProductByName(selectedValue.toString());
							break;
						default:
							break;
					}
					 
					 
				 }
			}
 
		};
	}

	@Override
	protected void initEvent() {
		super.initEvent();
		
		 addActionListener(menuBack, getHandler().navigationListener(PageConstants.PAGE_DASHBOARD));
		 addActionListener(buttonClearCart, buttonClearListener());
		 addActionListener(buttonSubmitTransaction, submitTransactionListener());
		 
		 //fields  
		 addKeyListener(inputQtyField, textFieldKeyListener(inputQtyField, "quantity"), false);	 
		 addKeyListener(inputUnitPriceField, textFieldKeyListener(inputUnitPriceField, "unitPrice"), false);
		 addActionListener(inputExpiredDateField, dateChooserListener(inputExpiredDateField, "expiryDate")); 
		 addActionListener(buttonSubmitCart, buttonSubmitListener());
	}
	
	private ActionListener submitTransactionListener() { 
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				getHandler().transactionSupply(productFlows, selectedSupplier);
				
			}
		};
	}

	/**
	 * button submit to cart listener
	 * @return
	 */
	private ActionListener buttonSubmitListener() { 
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int confirm = JOptionPane.showConfirmDialog(null, "Continue submit?"); 
				if(confirm != 0) { 
					return;
				}
				
				submitToCart();
			} 
			
		};
	} 

	/**
	 * submit current data to cart
	 */
	private void submitToCart() { 
		
		if(null == selectedProduct) {
			Dialogs.showErrorDialog("Product must not be null!");
			return;
		}
		
		if(editMode == false && getProductFlow(selectedProduct.getId()) != null) {
			Dialogs.showErrorDialog("Product has been exist!");
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
		
		productFlows.add(productFlow);
		
		clearForm(false);
		Log.log("product flow: ", productFlow);
		refresh();
		
	}
	
	private void removeProductFlow(long productId) {
		
		for (int i = 0; i< productFlows.size(); i++) {
			ProductFlow productFlow  = productFlows.get(i);
			
			if(productFlow.getProduct().getId().equals(productId)) {
				productFlows.remove(i);
				break;
			}
		}
	}
	
	private TransactionHandler getHandler() {
		return (TransactionHandler) appHandler;
	}
	
	static enum DropDownType{
		SUPPLIER, PRODUCT
	}

	private void clearProductFlows() {
		productFlows.clear();
	}
	
	public void callbackTransactionSupply(ShopApiResponse response) {
		Transaction transaction = response.getTransaction();
		String tranCode = transaction.getCode();
		
		Dialogs.showInfoDialog("Success: "+tranCode);
		
		clearProductFlows();
		clearForm(true);
		refresh();
	}

}
