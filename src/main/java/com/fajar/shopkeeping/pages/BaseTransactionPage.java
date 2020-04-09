package com.fajar.shopkeeping.pages;

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
import com.fajar.entity.Customer;
import com.fajar.entity.Product;
import com.fajar.entity.ProductFlow;
import com.fajar.entity.Supplier;
import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.component.ComponentBuilder;
import com.fajar.shopkeeping.constant.PageConstants;
import com.fajar.shopkeeping.handler.TransactionHandler;
import com.fajar.shopkeeping.model.PanelRequest;
import com.fajar.shopkeeping.pages.BaseTransactionPage.DropDownType;
import com.fajar.shopkeeping.util.EntityUtil;
import com.fajar.shopkeeping.util.Log;
import com.fajar.shopkeeping.util.ThreadUtil;

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
	
	protected final List<Product> productDropdownValues = new ArrayList<Product>();
	protected final List<Supplier> supplierDropdownValues = new ArrayList<Supplier>(); 
	protected final List<Customer> customerDopdownValues = new ArrayList<Customer>();
	
	protected JPanel formPanel;
	protected JPanel productListPanel;
	
	@Setter(value = AccessLevel.NONE)
	protected Product selectedProduct; 
	protected int quantity; 
	
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
		
		PanelRequest panelRequest = new PanelRequest(1, 670, 20, 15, Color.WHITE, 30, 30, 0, 0, false, true);   
		
		
		if(formPanel == null) {
			formPanel = buildFormPanel();
		}
		
		productListPanel = buildProductListPanel(); 
		
		mainPanel = ComponentBuilder.buildPanelV2(panelRequest,

				title("Transaction::"+type, 50),
				formPanel,
				productListPanel 
				); 

		parentPanel.add(mainPanel);
		exitOnClose();

	} 
	
	protected abstract JPanel buildFormPanel();
	
	protected abstract JPanel buildProductListPanel();
	
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
	 
	protected void handleDropDownKeyReleased(final JComboBox dynamicComboBox, final DropDownType dropDownType,
			final String componentText) {
		
		boolean typeProduct = dropDownType.equals(PRODUCT) ;
		boolean typeCustomer = dropDownType.equals(CUSTOMER);
		
		Class entityClass = typeProduct ? ( typeCustomer ? Customer.class: Product.class ) : Supplier.class;
		
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
}
