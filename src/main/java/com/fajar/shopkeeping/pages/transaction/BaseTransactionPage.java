package com.fajar.shopkeeping.pages.transaction;

import static com.fajar.shopkeeping.component.builder.ComponentActionListeners.addActionListener;
import static com.fajar.shopkeeping.model.PanelRequest.autoPanelScrollWidthHeightSpecified;
import static com.fajar.shopkeeping.pages.transaction.BaseTransactionPage.DropDownType.CUSTOMER;
import static com.fajar.shopkeeping.pages.transaction.BaseTransactionPage.DropDownType.PRODUCT;
import static com.fajar.shopkeeping.pages.transaction.BaseTransactionPage.DropDownType.SUPPLIER;

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

import com.fajar.shopkeeping.callbacks.ApplicationException;
import com.fajar.shopkeeping.callbacks.Listeners;
import com.fajar.shopkeeping.callbacks.WebResponseCallback;
import com.fajar.shopkeeping.component.Dialogs;
import com.fajar.shopkeeping.component.builder.ComponentBuilder;
import com.fajar.shopkeeping.component.builder.InputComponentBuilder;
import com.fajar.shopkeeping.constant.PageConstants;
import com.fajar.shopkeeping.handler.TransactionHandler;
import com.fajar.shopkeeping.model.PanelRequest;
import com.fajar.shopkeeping.pages.BasePage;
import com.fajar.shopkeeping.service.AppSession;
import com.fajar.shopkeeping.util.Log;
import com.fajar.shopkeeping.util.ThreadUtil;
import com.fajar.shoppingmart.dto.TransactionType;
import com.fajar.shoppingmart.dto.WebResponse;
import com.fajar.shoppingmart.entity.BaseEntity;
import com.fajar.shoppingmart.entity.Customer;
import com.fajar.shoppingmart.entity.Product;
import com.fajar.shoppingmart.entity.ProductFlow;
import com.fajar.shoppingmart.entity.Supplier;
import com.fajar.shoppingmart.util.EntityUtil;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
@Data
public abstract class BaseTransactionPage<T> extends BasePage<TransactionHandler>{ 
	
	protected JButton buttonSubmitCart;
	protected JButton buttonClearCart;
	protected JButton buttonSubmitTransaction;
	
	@Setter(value = AccessLevel.NONE )
	protected T transactionStakeHolder; 
	
	protected JComboBox<String> productComboBox; 
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
	protected final TransactionType type;

	public BaseTransactionPage(String title, int w, int h, TransactionType type) {
		super(title, w, h); 
		this.type = type;
	}

	public abstract void setSelectedProduct(Product product) ;
	
	@Override
	public void initComponent() { 
		
		buttonSubmitCart = ComponentBuilder.editButton("Submit To Cart");
 		buttonClearCart =  ComponentBuilder.button("Clear");
 		buttonSubmitTransaction = InputComponentBuilder.submitButton("SUBMIT TRANSACTION");
		
		if(formPanel == null) {
			formPanel = buildFormPanel();
		}
		
		if(titleLabel == null) {
			titleLabel = ComponentBuilder.title("Transaction", 50);
		}
		
		productListPanel = buildProductListPanel(); 
		
		mainPanel = ComponentBuilder.buildVerticallyInlineComponent(670,

				titleLabel,
				formPanel,
				productListPanel,
				ComponentBuilder.label(AppSession.getApplicationProfile().getName())
				); 

		parentPanel.add(mainPanel);
		exitOnClose();

	} 
	
	protected ActionListener submitTransactionListener() {
		 
		return (ActionEvent e)->{
			int confirm = Dialogs.confirm("Continue submit the Transaction?"); 
			if(confirm != 0) { 
				return;
			}
			if(type.equals(TransactionType.SELLING)) {
				getHandler().transactionSell(getProductFlows(), (Customer) getTransactionStakeHolder());
			}else if(type.equals(TransactionType.PURCHASING)) {
				getHandler().transactionPurchasing(getProductFlows(), (Supplier) getTransactionStakeHolder());
			}
		};
		
	}
	
	protected abstract void submitToCart();
	
	protected abstract JPanel buildFormPanel();
	
	protected abstract JPanel buildProductListPanel();
	
	protected abstract void populateForm(ProductFlow productFlow, BaseEntity supplierOrCustomer);
	
	protected abstract void setTransactionStakeHolder(T stakeholder);
	
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
		return autoPanelScrollWidthHeightSpecified(1, columnWidth * colSize, 5, Color.LIGHT_GRAY, 600, 200);
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
	
	/**
	 * when button clear form is clicked
	 * @return
	 */
	protected ActionListener buttonClearListener() {
		return  (ActionEvent e) ->{ 
				ThreadUtil.run( ()->{
					clearForm(false); 
				}); 
		};
	}
	
	/**
	 * when button edit in the product list table is clicked
	 * @param productFlow
	 * @return
	 */
	protected ActionListener editProductFlow(final ProductFlow productFlow) { 
		return (ActionEvent e)->{ 
				final ProductFlow selectedProductFlow = getProductFlow(productFlow.getProduct().getId());
				if(selectedProductFlow == null) {
					Dialogs.error("selected product does not exist");
					return;
				}
				ThreadUtil.run(()->{
					populateForm(selectedProductFlow, null);
					setEditMode(true); 
				}); 
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
	protected static void populateDropdown(String dropdownValue, List objects, String fieldName, JComboBox  dynamicComboBox) {
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
	
	@Override
	public void show() {
		titleLabel.setText(type.toString());
		super.show();
	}
	
	/**
	 * button submit to cart listener
	 * @return
	 */
	protected ActionListener buttonSubmitToCartListener() { 
		return  (ActionEvent e)-> {
				int confirm = JOptionPane.showConfirmDialog(null, "Continue submit?"); 
				if(confirm == 0) {  submitToCart(); } 
		};
	} 
	
	/**
	 * when product dropDown selected
	 * @return
	 */
	protected ActionListener dynamicDropdownActionListener(final DropDownType dropDownType) {
		 
		return  (ActionEvent e)-> { 
				
				 final JComboBox<?> inputComponent = (JComboBox<?>) e.getSource();
				 Object selectedValue = inputComponent.getSelectedItem();
				 if(null == selectedValue) { return; }
				 handleDynamicDropdownChange(dropDownType, selectedValue.toString());
			};
	}
	 
	/**
	 * when dynamic dropdown typed
	 * @param dynamicComboBox
	 * @param dropDownType
	 * @param componentText
	 */
	protected void handleDropDownKeyReleased(final JComboBox<?> dynamicComboBox, final DropDownType dropDownType,
			final String componentText) {
		
		boolean typeCustomer = dropDownType.equals(CUSTOMER) ;
		boolean typeSupplier = dropDownType.equals(SUPPLIER);
		
		Class<? extends BaseEntity> entityClass = Product.class;
		
		if(typeSupplier) {
			entityClass = Supplier.class;
		}else if(typeCustomer) {
			entityClass = Customer.class;
		}
		
		getHandler().getEntitiesFromDynamicDropdown(entityClass, "name", componentText,  
		(response) -> { 
			
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
			
		});
		
	}
	
	/**
	 * when product dropDown typed
	 * @return
	 */
	protected KeyListener dynamicDropdownFieldKeyListener(final DropDownType dropDownType) { 
		return Listeners.keyReleasedOnlyListener((e)-> { 
			final JComboBox<?> dynamicComboBox = InputComponentBuilder.getComboBoxFromEvent(e);
			final String componentText = InputComponentBuilder.getComboBoxText(dynamicComboBox);  
			handleDropDownKeyReleased(dynamicComboBox, dropDownType, componentText);
		});
	} 
	
	/**
	 * remove item from cart table
	 * @param productFlow
	 * @return
	 */
	protected ActionListener buttonRemoveListener(final ProductFlow productFlow) {
		return  (e)-> {
				int confirm = JOptionPane.showConfirmDialog(null, "Remove "+productFlow.getProduct().getName()+"?");
				
				if(confirm != 0) {  return; }
				
				ThreadUtil.run(()->{
					long productId = productFlow.getProduct().getId();
					removeProductFlow(productId);
					refresh(); 
				});
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
	
	

}
