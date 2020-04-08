package com.fajar.shopkeeping.pages;

import static javax.swing.SwingConstants.LEFT;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.fajar.entity.Product;
import com.fajar.entity.ProductFlow;
import com.fajar.shopkeeping.component.ComponentBuilder;
import com.fajar.shopkeeping.constant.PageConstants;
import com.fajar.shopkeeping.handler.TransactionHandler;
import com.fajar.shopkeeping.model.PanelRequest;
import com.fajar.shopkeeping.util.Log;
import com.toedter.calendar.JDateChooser;

import lombok.Data;

@Data
public class SupplyTransactionPage extends BasePage { 
	
	private JPanel formPanel;
	private JPanel productListPanel;
	
	
	private long supplierId;
	private int quantity;
	private int unitPrice;
	private Date expiryDate;
	private Product selectedProduct;
	
	//fields
	private JComboBox productComboBox; 
	private JTextField inputQtyField;
	private JTextField inputUnitPriceField;
	private JDateChooser inputExpiredDateField;
	
	private final List<ProductFlow> incomingProducts = new ArrayList<ProductFlow>(); 
	private ProductFlow managedProductFlow;

	public SupplyTransactionPage() {
		super("Launcher", BASE_WIDTH, BASE_HEIGHT);
	} 

	@Override
	public void initComponent() { 
		
		PanelRequest panelRequest = new PanelRequest(1, 670, 20, 15, Color.WHITE, 30, 30, 0, 0, false, true);   
		
		
		if(formPanel == null) {
			formPanel = buildFormPanel();
		}
		
		
		if(productListPanel == null) {
			productListPanel = buildProductListPanel();
		}
		
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

	private JPanel buildProductListPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * transaction fields
	 * @return
	 */
	private JPanel buildFormPanel() {
		ActionListener actionListener = productFieldSelectedListener();
		KeyListener keyListener = productFieldKeyListener(); 
		productComboBox = ComponentBuilder.buildEditableComboBox("", keyListener, actionListener, "type product name.."); 
		inputQtyField = numberField("0");
		inputUnitPriceField = numberField("0");
 		inputExpiredDateField = dateChooser(new Date());
 		
 		
		PanelRequest panelRequest = PanelRequest.autoPanelNonScroll(2, 200, 5, Color.LIGHT_GRAY);
		JPanel panel = ComponentBuilder.buildPanelV2(panelRequest , 
				label("Product", LEFT), productComboBox,
				label("Quantity", LEFT), inputQtyField,
				label("Unit Price", LEFT), inputUnitPriceField,
				label("Expired Date", LEFT), inputExpiredDateField
				);
		return panel ;
	}

	private KeyListener productFieldKeyListener() { 
		return new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent event) {
				
				final JComboBox dynamicComboBox = getComboBox(event);
				final String componentText = getComboBoxText(dynamicComboBox);
				Log.log("typed: ",componentText);
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
		};
	}

	private ActionListener productFieldSelectedListener() {
		 
		return  new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		};
	}

	@Override
	protected void initEvent() {
		super.initEvent();
		
		 addActionListener(menuBack, getHandler().navigationListener(PageConstants.PAGE_DASHBOARD));
		 
		 //fields  
		 addKeyListener(inputQtyField, textFieldKeyListener(inputQtyField, "quantity"), false);	 
		 addKeyListener(inputUnitPriceField, textFieldKeyListener(inputUnitPriceField, "unitPrice"), false);
		 addActionListener(inputExpiredDateField, dateChooserListener(inputExpiredDateField, "expiryDate")); 
		 
	}
	
	private TransactionHandler getHandler() {
		return (TransactionHandler) appHandler;
	}

}
