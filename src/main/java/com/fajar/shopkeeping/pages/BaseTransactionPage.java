package com.fajar.shopkeeping.pages;

import static com.fajar.shopkeeping.model.PanelRequest.autoPanelScrollWidthHeightSpecified;
import static com.fajar.shopkeeping.pages.BaseTransactionPage.DropDownType.CUSTOMER;
import static com.fajar.shopkeeping.pages.BaseTransactionPage.DropDownType.PRODUCT;
import static com.fajar.shopkeeping.pages.BaseTransactionPage.DropDownType.SUPPLIER;

import java.awt.Color;
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

import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.component.ComponentBuilder;
import com.fajar.shopkeeping.component.Dialogs;
import com.fajar.shopkeeping.constant.PageConstants;
import com.fajar.shopkeeping.handler.TransactionHandler;
import com.fajar.shopkeeping.model.PanelRequest;
import com.fajar.shopkeeping.util.Log;
import com.fajar.shopkeeping.util.ThreadUtil;
import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.entity.BaseEntity;
import com.fajar.shoppingmart.entity.Customer;
import com.fajar.shoppingmart.entity.Product;
import com.fajar.shoppingmart.entity.ProductFlow;
import com.fajar.shoppingmart.entity.Supplier;
import com.fajar.shoppingmart.util.EntityUtil;
import com.toedter.calendar.JDateChooser;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public abstract class BaseTransactionPage extends BasePage{ 
	
	protected JButton buttonSubmitCart;
	protected JButton buttonClearCart;
	protected JButton buttonSubmitTransaction;
	
	
	protected JComboBox productComboBox; 
	protected JTextField inputQtyField; 
	protected JLabel labelProductUnit;   
	protected JLabel labelTotalPrice;
	protected JLabel titleLabel;
	
	protected final List<Product> productDropdownValues = new ArrayList<Product>();
	protected final List<Supplier> supplierDropdownValues = new ArrayList<Supplier>(); 
	protected final List<Customer> customerDopdownValues = new ArrayList<Customer>();
	
	protected JPanel formPanel;
	protected JPanel productListPanel;
	
	@Setter(value = AccessLevel.NONE)
	protected Product selectedProduct; 
	protected int quantity; 
	protected long grandTotalPrice = 0l;
	
	protected ProductFlow managedProductFlow;
	protected List<ProductFlow> productFlows = new ArrayList<ProductFlow>(); 
	protected boolean editMode = false; 
	protected String type = "";

	public BaseTransactionPage(String title, int w, int h, String type) {
		super(title, w, h); 
		this.type = type;
	}

	public abstract void setSelectedProduct(Product product) ;
	
	@Override
	public void initComponent() { 
		
		PanelRequest panelRequest = new PanelRequest(1, 670, 20, 1, Color.WHITE, 30, 30, 0, 0, false, true);   
		buttonSubmitCart = button("Submit To Cart");
 		buttonClearCart = button("Clear");
 		buttonSubmitTransaction = button("SUBMIT TRANSACTION");
		
		if(formPanel == null) {
			formPanel = buildFormPanel();
		}
		
		if(titleLabel == null) {
			titleLabel = title("Transaction", 50);
		}
		
		productListPanel = buildProductListPanel(); 
		
		mainPanel = ComponentBuilder.buildPanelV2(panelRequest,

				titleLabel,
				formPanel,
				productListPanel 
				); 

		parentPanel.add(mainPanel);
		exitOnClose();

	} 
	
	protected abstract ActionListener submitTransactionListener();
	
	protected abstract void submitToCart();
	
	protected abstract JPanel buildFormPanel();
	
	protected abstract JPanel buildProductListPanel();
	
	protected abstract void populateForm(ProductFlow productFlow, BaseEntity supplierOrCustomer);
	
	/**
	 * clear inputs and set edit mode to FALSE
	 * @param clearSupplierOrCustomer
	 */
	protected abstract void clearForm(boolean clearSupplierOrCustomer);
	
	/**
	 * add product flow to list
	 * @param productFlow
	 */
	protected void addProductFlow(ProductFlow productFlow) {
		productFlows.add(productFlow);
		
	}
	
	protected boolean validateSelectedProduct() {
		if(null == selectedProduct) {
			Dialogs.error("Product must not be null!");
			return false;
		}
		
		ProductFlow existingProduct = getProductFlow(selectedProduct.getId());
		
		if(editMode == false && existingProduct != null) {
			Dialogs.error("Product has been exist!");
			return false;
			
		}else if(editMode && existingProduct == null) {
			Dialogs.error("Product dose not exist!");
			return false;
		} 
		
		Log.log("product is valid");
		
		return true;
	}
	
	protected PanelRequest getFormFieldPanelRequest() {
		return PanelRequest.autoPanelNonScroll(2, 250, 5, null);
	}
	
	protected PanelRequest getProductListPanelRequest(int columnWidth, int colSize) {
		return autoPanelScrollWidthHeightSpecified(1, columnWidth * colSize, 5, Color.LIGHT_GRAY, 600, 250);
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
	
	@Override
	protected void initEvent() {

		addActionListener(menuBack, getHandler().navigationListener(PageConstants.PAGE_DASHBOARD));
		super.initEvent();
	}
	
	protected TransactionHandler getHandler() {
		return (TransactionHandler) appHandler;
	}
	
	/**
	 * when button clear form is clicked
	 * @return
	 */
	protected ActionListener buttonClearListener() {
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) { 
				ThreadUtil.run(new Runnable() {
					
					@Override
					public void run() {
						clearForm(false); 
					}
				});
				
			}
		};
	}
	
	/**
	 * when button edit in the product list table is clicked
	 * @param productFlow
	 * @return
	 */
	protected ActionListener editProductFlow(final ProductFlow productFlow) { 
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) { 
				final ProductFlow selectedProductFlow = getProductFlow(productFlow.getProduct().getId());
				if(selectedProductFlow == null) {
					Dialogs.error("selected product does not exist");
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

	/**
	 * get product flow having product.id equals given productId
	 * @param productId
	 * @return
	 */
	protected ProductFlow getProductFlow(long productId) {
		for (ProductFlow productFlow : productFlows) {
			if(productFlow.getProduct().getId().equals(productId)) {
				return productFlow;
			}
		}
		
		return null;
	}
	 
	
	
	/**
	 * fill comboBox with values
	 * @param dropdownValue
	 * @param objects
	 * @param fieldName
	 * @param dynamicComboBox
	 */
	protected static void populateDropdown(String dropdownValue, List objects, String fieldName, JComboBox dynamicComboBox) {
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
		dynamicComboBox.setSelectedItem(dropdownValue);
		((JTextComponent) dynamicComboBox.getEditor().getEditorComponent()).setSelectionStart(dropdownValue.length());
		((JTextComponent) dynamicComboBox.getEditor().getEditorComponent()).setSelectionEnd(dropdownValue.length());
		dynamicComboBox.showPopup();
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
	
	/**
	 * button submit to cart listener
	 * @return
	 */
	protected ActionListener buttonSubmitToCartListener() { 
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
	 * when product dropDown selected
	 * @return
	 */
	protected ActionListener dynamicDropdownActionListener(final DropDownType dropDownType) {
		 
		return  new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) { 
				
				 final JComboBox inputComponent = (JComboBox) e.getSource();
				 Object selectedValue = inputComponent.getSelectedItem();
				 if(null == selectedValue) {
						return;
					}
				 handleDynamicDropdownChange(dropDownType, selectedValue.toString());
			}
 
		};
	}
	 
	/**
	 * when dynamic dropdown typed
	 * @param dynamicComboBox
	 * @param dropDownType
	 * @param componentText
	 */
	protected void handleDropDownKeyReleased(final JComboBox dynamicComboBox, final DropDownType dropDownType,
			final String componentText) {
		
		boolean typeCustomer = dropDownType.equals(CUSTOMER) ;
		boolean typeSupplier = dropDownType.equals(SUPPLIER);
		
		Class entityClass = Product.class;
		
		if(typeSupplier) {
			entityClass = Supplier.class;
		}else if(typeCustomer) {
			entityClass = Customer.class;
		}
		
		getHandler().getEntitiesFromDynamicDropdown(entityClass, "name", componentText, new MyCallback() {
			
			@Override
			public void handle(Object... params) throws Exception { 
				WebResponse response = (WebResponse) params[0];
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
				}else if(dropDownType.equals(CUSTOMER)) {
					
					customerDopdownValues.clear();
					for(BaseEntity customer:response.getEntities()) {
						customerDopdownValues.add((Customer) customer);
					}
					
					populateDropdown(componentText, customerDopdownValues, "name", dynamicComboBox);
				}
			} 
		});
		
	}
	
	/**
	 * when product dropDown typed
	 * @return
	 */
	protected KeyListener dynamicDropdownFieldKeyListener(final DropDownType dropDownType) { 
		return new KeyListener() { 
			@Override
			public void keyTyped(KeyEvent e) { }
			@Override
			public void keyPressed(KeyEvent e) { }
			@Override
			public void keyReleased(KeyEvent event) { 
				final JComboBox dynamicComboBox = getComboBox(event);
				final String componentText = getComboBoxText(dynamicComboBox);  
				handleDropDownKeyReleased(dynamicComboBox, dropDownType, componentText);
			}
			 
			
		};
	} 
	
	/**
	 * remove item from cart table
	 * @param productFlow
	 * @return
	 */
	protected ActionListener buttonRemoveListener(final ProductFlow productFlow) {
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
	
	/**
	 * remove product flow having product.id equals given productId
	 * @param productId
	 */
	protected void removeProductFlow(long productId) {
		
		for (int i = 0; i< productFlows.size(); i++) {
			ProductFlow productFlow  = productFlows.get(i);
			
			if(productFlow.getProduct().getId().equals(productId)) {
				productFlows.remove(i);
				break;
			}
		}
	}  
	
	protected abstract void handleDynamicDropdownChange(final DropDownType dropDownType, String selectedValue);
	
	protected void clearProductFlows() {
		productFlows.clear();
	}
	
	protected static enum DropDownType{
		SUPPLIER, PRODUCT, CUSTOMER
	}
	
	/**
	 * set label text to ""
	 * @param label
	 */
	protected static void clearLabel(JLabel label) {
		label.setText("");
	}
	
	/**
	 * set date chooser to now
	 * @param dateChooser
	 */
	protected static void clearDateChooser(JDateChooser dateChooser) {
		dateChooser.setDate(new Date());
	}
	
	/**
	 * set value to ""
	 * @param textField
	 */
	protected static void clearTextField(JTextField textField) {
		textField.setText("");
	}
	
	/**
	 * remove all items and set selected value to "";
	 * @param comboBox
	 */
	protected static void clearComboBox(JComboBox comboBox) {
		comboBox.removeAllItems();
		comboBox.setSelectedItem("");
	}

}
