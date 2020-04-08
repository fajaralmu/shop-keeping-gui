package com.fajar.shopkeeping.pages;

import static com.fajar.shopkeeping.component.ComponentBuilder.buildInlineComponent;
import static com.fajar.shopkeeping.component.ComponentBuilder.buildVerticallyInlineComponent;
import static com.fajar.shopkeeping.model.PanelRequest.autoPanelNonScroll;
import static com.fajar.shopkeeping.model.PanelRequest.autoPanelScrollWidthHeightSpecified;
import static com.fajar.shopkeeping.service.AppContext.getContext;
import static com.fajar.shopkeeping.util.EntityUtil.getDeclaredField;
import static com.fajar.shopkeeping.util.MapUtil.objectEquals;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.fajar.annotation.FormField;
import com.fajar.dto.ShopApiResponse;
import com.fajar.entity.BaseEntity;
import com.fajar.entity.setting.EntityElement;
import com.fajar.entity.setting.EntityProperty;
import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.component.ComponentBuilder;
import com.fajar.shopkeeping.component.ComponentModifier;
import com.fajar.shopkeeping.component.Dialogs;
import com.fajar.shopkeeping.component.Loadings;
import com.fajar.shopkeeping.component.MyInfoLabel;
import com.fajar.shopkeeping.constant.ContextConstants;
import com.fajar.shopkeeping.constant.PageConstants;
import com.fajar.shopkeeping.constant.UrlConstants;
import com.fajar.shopkeeping.handler.MainHandler;
import com.fajar.shopkeeping.handler.ManagementHandler;
import com.fajar.shopkeeping.model.PanelRequest;
import com.fajar.shopkeeping.model.SharedContext;
import com.fajar.shopkeeping.util.DateUtil;
import com.fajar.shopkeeping.util.EntityUtil;
import com.fajar.shopkeeping.util.Log;
import com.fajar.shopkeeping.util.StringUtil;
import com.fajar.shopkeeping.util.ThreadUtil;
import com.toedter.calendar.JDateChooser;

import lombok.Data;

@Data
public class ManagementPage extends BasePage {

	private static final String DATE_PATTERN = "EEE, d MMM yyyy HH:mm:ss";
	public static final String ORDER_ASC = "asc";
	public static final String ORDER_DESC = "desc";
	private static final String NULL_IMAGE = "NULL"; 

	private EntityProperty entityProperty;

	private JPanel formPanel;
	private JPanel listPanel;
	private JPanel navigationPanel;

	private final JButton buttonSubmit = button("Submit");
	private final JButton buttonClear = button("Clear");
	private final JButton buttonFilterEntity = button("Go");
	private final JButton buttonRefresh = button("Refresh");

	private Class<? extends BaseEntity> entityClass;
	private List<BaseEntity> entityList; 
	
	private final Map<String, JTextField> columnFilterTextFields = new HashMap<>(); //list of data table column filter inputs
	private final Map<String, Component> formInputFields = new HashMap<>(); 
 
	private final Map<String, List<Map>> comboBoxListContainer = new HashMap<>(); 
	private final Map<String, Object> fieldsFilter = new HashMap<>();
	private final Map<String, JLabel> singleImagePreviews = new HashMap<>(); 
	private final Map<String, List<JLabel>> multipleImagePreviews = new HashMap<>(); 

	private Map<String, Object> managedObject = new HashMap<>();
	private String currentElementIdFocus = "";
	
	private boolean refreshing;
	private boolean editMode;
	
	private String idFieldName;
	private String orderType;
	private String orderBy;
	private Object idValue;
	
	private final JTextField inputPage = numberField("0");
	private final JTextField inputLimit = numberField("10");
	
	private final JLabel labelTotalData = label("total data");

	private int selectedPage = 0;
	private int selectedLimit = 10;
	private int totalData = 0; 
	
	private final ManagementPageHelper helper;
	
	Component actionButtons = buildInlineComponent(90, buttonSubmit, buttonClear, buttonRefresh); 
	
	
	public ManagementPage() {
		super("Management", BASE_WIDTH + 400, BASE_HEIGHT);
		helper = new ManagementPageHelper(this);
	}

	@Override
	public void show() {
		super.show();
		loadForm();
	}

	@Override
	public void initComponent() {

		PanelRequest panelRequest = autoPanelNonScroll(2, 550, 5, Color.WHITE);
		panelRequest.setCenterAligment(true);

		if (formPanel == null) {
			formPanel = buildPanelV2(panelRequest, label("Please wait.."));
		}  
		if(listPanel == null) {
			listPanel = buildPanelV2(panelRequest, label("Please wait.."));
		} 
		if(navigationPanel == null) { //will be populated with pagination buttons when get filtered entities
			navigationPanel = ComponentBuilder.blankPanel(520, 90);
		} 
		
		mainPanel = buildPanelV2(panelRequest,

				title("Management::"+getEntityClassName(), 30), null,
				formPanel, 
				buildVerticallyInlineComponent(520, navigationPanel,  listPanel));

		parentPanel.add(mainPanel);
		
		exitOnClose();

	}
	
	/**
	 * rebuild the navigation buttons for data table
	 */
	private void validateNavigationPanel() { 

		ThreadUtil.run(new Runnable() {
			
			@Override
			public void run() {
				JPanel contentNavigation = getNavigationPanel();
				contentNavigation.setBounds(0, 0, 515, 81);
				contentNavigation.setPreferredSize(new Dimension(515, 81));
				navigationPanel.removeAll();
				navigationPanel.add(contentNavigation); 
				navigationPanel.revalidate();
				navigationPanel.repaint();
			}
		});
		
	}

	/**
	 * generate data table navigations
	 * @return
	 */
	private JPanel getNavigationPanel() {
		
		if(selectedLimit == 0) {
			return new JPanel();
		} 
		
		Component[] navigationButtons = helper.generateDataTableNavigationButtons();
		PanelRequest panelRequest = autoPanelScrollWidthHeightSpecified(navigationButtons .length, 50, 1, Color.gray, 500, 40);
		JPanel panelNavigation = buildPanelV2(panelRequest, navigationButtons);
		
		JPanel panelPageLimit = buildInlineComponent(60, label("page"), inputPage, label("limit"), inputLimit, buttonFilterEntity, labelTotalData);
		return buildVerticallyInlineComponent(500, panelNavigation, panelPageLimit);
	}

	
	

	private String getEntityClassName() {
		if(entityClass == null) {
			return "";
		}
		return entityClass.getSimpleName();
	}

	@Override
	protected void initEvent() {
		super.initEvent();
		addActionListener(buttonSubmit, getHandler().submit()); 
		addKeyListener(inputPage, textFieldKeyListener(inputPage, "selectedPage"));
		addKeyListener(inputLimit, textFieldKeyListener(inputLimit, "selectedLimit")); 
		addActionListener(buttonFilterEntity, getHandler().filterEntity());  
		addActionListener(buttonRefresh, buttonRefreshListener()); 
		addActionListener(buttonClear, clearListener());
		
		addActionListener(menuBack, pageNavigation(PageConstants.PAGE_DASHBOARD));
		
	} 
	
	@Override
	public void setAppHandler(MainHandler mainHandler) {
		SharedContext context = getContext(ContextConstants.CTX_MANAGEMENT_PAGE);
		setEntityClass(context.getEntityClass());
		super.setAppHandler(mainHandler);
		
	}
	
	/**
	 * when button clear pressed
	 * @return
	 */
	private ActionListener clearListener() { 
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				ThreadUtil.run(new Runnable() {
					
					@Override
					public void run() {
						fieldsFilter.clear();
						helper.doClearForm();
					}
				}); 
			}
		};
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
	protected void setDefaultValues() {  
		setSelectedPage(Integer.valueOf(inputPage.getText()));
		setSelectedLimit(Integer.valueOf(inputLimit.getText()));
		super.setDefaultValues();
	}

	/**
	 * constructs CRUD form
	 */
	public void loadForm() {
		Loadings.start();
		ThreadUtil.run(new Runnable() {

			@Override
			public void run() {
				setFormPanel(generateEntityForm());
				helper.doClearForm();
				getHandler().getEntities();
				
				preInitComponent();
				initEvent();
				Loadings.end();
			}
		}); 

	}
	
	
	
	/**
	 * keys of single image iconed JLabels
	 * @return
	 */
	public Set<String> singleImagePreviewsKeySet(){
		return singleImagePreviews.keySet();
	}
	
	/**
	 * keys of multiple JPanel with iconed JLabels
	 * @return
	 */
	public Set<String> multipleImagePreviewsKeySet(){
		return multipleImagePreviews.keySet();
	}
	
	public Component getFieldComponent(String elementId) {
		
		return formInputFields.get(elementId);
	}
	
	/**
	 * set icon to JLabel in the singleImagePreviews HashMap
	 * @param elementId
	 * @param icon
	 */
	public void setIconOnSingleImagePreview(String elementId, Icon icon) {
		singleImagePreviews.get(elementId).setIcon(icon);
	}
	
	/**
	 * clear list of component in specified elementId in multipleImagePreviews HashMap 
	 * @param key
	 */
	public void clearMultipleImagePreviews(String elementId) {
		multipleImagePreviews.get(elementId).clear();
	}

	/**
	 * CRUD Form Generation
	 * 
	 * @return
	 */
	private JPanel generateEntityForm() {

		PanelRequest panelRequest = autoPanelNonScroll(1, 420, 2, Color.WHITE);

		List<Component> formComponents = new ArrayList<Component>();
 
		comboBoxListContainer.clear();
		formInputFields.clear();
		EntityProperty newEntityProperty = EntityUtil.createEntityProperty(entityClass, null);
		
		setEntityProperty(newEntityProperty);

		List<EntityElement> entityElements = newEntityProperty.getElements();

		for (EntityElement element : entityElements) {
			final String elementId = element.getId();

			Field entityField = EntityUtil.getDeclaredField(entityClass, elementId);
			Class<?> fieldType = entityField.getType();
			JLabel lableName = ComponentBuilder.label(element.getLableName(), SwingConstants.LEFT); 
			String elementType = element.getType();
			boolean skipFormField = false;
			
			if (elementType == null) {
				continue;
			}

			Component inputComponent = textField("Not Configured");
			inputComponent.setFocusable(true);
			inputComponent.requestFocus();

			if (elementType.equals(FormField.FIELD_TYPE_FIXED_LIST)) {
 
				inputComponent = helper.buildFixedComboBox(element, fieldType);
			} else if (elementType.equals(FormField.FIELD_TYPE_DYNAMIC_LIST)) {
				 
				inputComponent = helper.buildDynamicComboBox(element, fieldType);
			} else if (element.isIdentity()) {
				
				inputComponent = textFieldDisabled("ID"); 
				setIdFieldName(elementId);
				
			} else if (elementType.equals(FormField.FIELD_TYPE_TEXTAREA)) {

				inputComponent = textArea(elementId);
				((JTextArea) inputComponent).addKeyListener(helper.textAreaActionListener((JTextArea) inputComponent, elementId));
				
			} else if (elementType.equals("color")) {
				skipFormField = true;
				continue;
			} else if (elementType.equals(FormField.FIELD_TYPE_NUMBER)) {

				inputComponent = numberField(elementId);
				((JTextField) inputComponent).addKeyListener(helper.crudTextFieldActionListener((JTextField) inputComponent, elementId));
				
			} else if (elementType.equals(FormField.FIELD_TYPE_DATE)) {

				inputComponent = dateChooser();
				((JDateChooser) inputComponent).addPropertyChangeListener(helper.dateChooserPropertyChangeListener(
						(JDateChooser) inputComponent, elementId ));
				
			} else if (elementType.equals(FormField.FIELD_TYPE_IMAGE)) {
				skipFormField = true;
				inputComponent = helper.buildImageField(element, fieldType, element.isMultiple());
				
			} else {
				inputComponent = textField(elementId);
				((JTextField) inputComponent).addKeyListener(helper.crudTextFieldActionListener((JTextField) inputComponent, elementId) );
			}
			inputComponent.setSize(200, inputComponent.getHeight());
			
			if(!skipFormField) {
				setFormInputComponent(elementId, inputComponent);
			}
			
			formComponents.add(buildInlineComponent(200, lableName, inputComponent));
			
		}
		
		formComponents.add(actionButtons );

		JPanel formPanel = buildPanelV2(panelRequest, toArrayOfComponent(formComponents));
		return formPanel;
	}
	
	public void setFormInputComponent(String elementId, Component inputComponent) { 
		formInputFields.put(elementId, inputComponent);
	} 
	
	public void setSingleImageContainer(String elementId, JLabel imagePreview) {
		singleImagePreviews.put(elementId, imagePreview);
		
	}
 
	
	/**
	 * remove all file chooser buttons in multiple image selection fields
	 * @param imageSelectionScrollPane
	 */
	public void removeAllImageSelectionField(JScrollPane imageSelectionScrollPane) {
		
		try {
			JPanel panel = (JPanel)  imageSelectionScrollPane.getViewport().getView(); 
			JPanel imageSelectionPanel = (JPanel) panel.getComponent(0);  
			imageSelectionPanel.removeAll();
			
			Dimension newDimension = new Dimension(imageSelectionPanel.getWidth(), 200);// - componentToRemove.getHeight() );
			imageSelectionPanel.setSize(newDimension); 
			JPanel wrapperPanel  = buildInlineComponent(imageSelectionPanel.getWidth() + 5, imageSelectionPanel);  
			
			updateScrollPane(imageSelectionScrollPane, wrapperPanel, newDimension);
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * add image selection field in scrollable panel
	 * @param element
	 * @param imageSelectionScrollPane
	 */
	public void addNewImageSelectionField(EntityElement element, JScrollPane imageSelectionScrollPane) {
		Log.log("addNewImageSelectionField");
		
		JPanel panel = (JPanel)  imageSelectionScrollPane.getViewport().getView(); 
		JPanel imageSelectionPanel = (JPanel) panel.getComponent(0); 
		
		if(imageSelectionPanel.getComponentCount() > 0 && imageSelectionPanel.getComponent(0) instanceof MyInfoLabel) {
			imageSelectionPanel.removeAll();
			Log.log("REMOVE ALL");
		}
		
		int index = imageSelectionPanel.getComponentCount();
		
		JLabel imagePreview = createImagePreview(); 
		
		JButton buttonChoose = button("choose file ("+index+")", 160, helper.onChooseMultipleImageFileClick(new JFileChooser(), element.getId(), index));  
		JButton buttonClear = button("clear", 160, helper.buttonClearMultipleImageClick(element.getId(), index)); 
		JButton buttonRemove = button("remove" , 160, helper.removeImageSelectionListener(element, index, imageSelectionScrollPane));
		
		int componentCount = imageSelectionPanel.getComponentCount();
		
		JPanel newImageSelection = buildVerticallyInlineComponent(200, buttonChoose, buttonClear, buttonRemove, imagePreview) ;  
		newImageSelection.setBounds(newImageSelection.getX(), componentCount * newImageSelection.getHeight(), newImageSelection.getWidth(), newImageSelection.getHeight());
		
		Dimension newDimension = new Dimension(imageSelectionPanel.getWidth(),  imageSelectionPanel.getHeight() + newImageSelection.getHeight() );
		imageSelectionPanel.setSize(newDimension);
		imageSelectionPanel.add(newImageSelection);  
		JPanel wrapperPanel  = buildInlineComponent(imageSelectionPanel.getWidth() + 5, imageSelectionPanel);  
		
		updateScrollPane(imageSelectionScrollPane, wrapperPanel, newDimension);
		
		addMultipleImageContainer(element.getId(), imagePreview);
		
	} 
	 
	/**
	 * do remove input fields for upload image
	 * @param index
	 * @param imageSelectionScrollPane
	 */
	public void removeImageSelectionItem(EntityElement element, int index, JScrollPane imageSelectionScrollPane) {
		Log.log("removeImageSelectionItem");
		
		JPanel panel = (JPanel)  imageSelectionScrollPane.getViewport().getView(); 
		JPanel imageSelectionPanel = (JPanel) panel.getComponent(0); 
		
		if(imageSelectionPanel.getComponentCount() > 0 && imageSelectionPanel.getComponent(0) instanceof MyInfoLabel) {
			imageSelectionPanel.removeAll();
			Log.log("REMOVE ALL");
			return;
		}
		
		JPanel componentToRemove = (JPanel) imageSelectionPanel.getComponent(index);
		
		JLabel removedInfo = label("removed at:"+index);
		removedInfo.setBackground(Color.gray);
		ComponentModifier.updatePosition(removedInfo, componentToRemove);
		imageSelectionPanel.remove(index);
		imageSelectionPanel.add(removedInfo, index); 
		 	
		Dimension newDimension = new Dimension(imageSelectionPanel.getWidth(),  imageSelectionPanel.getHeight());// - componentToRemove.getHeight() );
		imageSelectionPanel.setSize(newDimension); 
		JPanel wrapperPanel  = buildInlineComponent(imageSelectionPanel.getWidth() + 5, imageSelectionPanel);  
		
		updateScrollPane(imageSelectionScrollPane, wrapperPanel, newDimension);
		
		removeMultipleImageContainerItem(element.getId(), index);
		
	}
	
	/**
	 * remove value (in specified field & index) from managed object
	 * @param elementId
	 * @param index
	 */
	private void removeMultipleImageContainerItem(String elementId, int index) {
		// TODO multipleImagePreviews.get(id).remove(index);
		Log.log("removeMultipleImageContainerItem[",elementId,"] at", index);
		try {
			 Object currentObject = managedObject.get(elementId);
			 String[] rawString = currentObject.toString().split("~");
			 Log.log( rawString);
			 
			 if(rawString.length >= index + 1) {
				 rawString[index] = NULL_IMAGE;
			 }
			 
			 String newValue = String.join("~", rawString);
			 Log.log("new value: ",newValue);
			 updateManagedObject(elementId, newValue);
		}catch (Exception e) { 
		}
	}

	/**
	 * update scrollable content
	 * @param scrollPane
	 * @param component
	 * @param newDimension
	 */
	private static void updateScrollPane(JScrollPane scrollPane, Component component, Dimension newDimension) {
		component.setPreferredSize(newDimension);
		component.setSize(newDimension);
		scrollPane.setViewportView(component);
		scrollPane.validate(); 
	}
	
	/**
	 * add new multiple image previews label(image icon)
	 * @param id
	 * @param imagePreview
	 */
	private void addMultipleImageContainer(String id, JLabel imagePreview) {
		if(multipleImagePreviews.get(id) == null) {
			multipleImagePreviews.put(id, new ArrayList<JLabel>());
		}
		multipleImagePreviews.get(id).add(imagePreview );
		
	}
	
	/**
	 * create label for image preview
	 * @return
	 */
	public JLabel createImagePreview() {
		JLabel imagePreview = label("No Preview."); 
		imagePreview.setSize(160, 160);
		imagePreview.setBorder(BorderFactory.createLineBorder(Color.BLACK)); 
		return imagePreview;
	}
	
	 
	public JLabel getImagePreviewLabelForMultipleImages(String elementId, int index) {
		return multipleImagePreviews.get(elementId).get(index); 
	}
	 
	
	/**
	 * CRUID DATA TABLE
	 * @return
	 */
	private JPanel buildListPanel() {
		
		List<BaseEntity> entities = entityList;
		List<Component> listComponents = new ArrayList<>();
		List<EntityElement> entityElements = entityProperty.getElements();
		
		final int colSize = entityElements.size() + 2;   
		final int columnWidth = 160;
		int sequenceNumber = Integer.valueOf(selectedPage) *  Integer.valueOf(selectedLimit);
		Component headerPanel = createDataTableHeader();
		
		listComponents.add(headerPanel );
		
		
		for (BaseEntity entity : entities) {
			Component[] components = new Component[colSize ];
			components[0] = label(sequenceNumber + 1); 
			
			String idFieldName = "";
			Object idValue = "";
			boolean idExist = false;
			/**
			 * checking the value type
			 */
			elementLoop: for(int i = 0; i< entityElements.size();i++) {
				
				final EntityElement element = entityElements.get(i); 
				final Field field = getDeclaredField(entity.getClass(), element.getId());
				final String fieldType = element.getType();
				Object value;
				
				try {
					value = field.get(entity);
					
					if(null != value) {
						
						if( objectEquals(fieldType, FormField.FIELD_TYPE_DYNAMIC_LIST, FormField.FIELD_TYPE_FIXED_LIST)){
							
							String optionItemName = element.getOptionItemName();
							Field converterField = EntityUtil.getDeclaredField(field.getType(), optionItemName);
							Object converterValue = converterField.get(value);
							value = converterValue;
							
						}else if(objectEquals(fieldType, FormField.FIELD_TYPE_IMAGE)) {
						
							value = value.toString().split("~")[0];
							components[i + 1] = ComponentBuilder.imageLabel(UrlConstants.URL_IMAGE+value, 100, 100);
							continue elementLoop;
							
						}else if(objectEquals(fieldType, FormField.FIELD_TYPE_DATE)) {
							
							value = DateUtil.parseDate((Date)value, DATE_PATTERN);
							
						}else if(objectEquals(fieldType, FormField.FIELD_TYPE_NUMBER)) {
							
							value = StringUtil.beautifyNominal(Long.valueOf(value.toString()));
							
						} 
						
						if(value.toString().length() > 30) {
							value = value.toString().substring(0, 30)+"...";
						}
						
						if(element.isIdentity()) {
							idExist  = true;
							idFieldName = element.getId();
							idValue = value;
						}
					}  
					
					components[i + 1] = label(value);
				} catch (IllegalArgumentException | IllegalAccessException e) { 
					e.printStackTrace();
				}
				 
			}
			
			//end field elements
			components[colSize - 1] = idExist ? helper.editButton(idFieldName, idValue) : label("-");
			
			sequenceNumber++;
			JPanel rowPanel = rowPanel(colSize , columnWidth, components);
			listComponents.add(rowPanel);
		}
		
		PanelRequest panelRequest = autoPanelScrollWidthHeightSpecified(1, columnWidth * colSize, 5, Color.LIGHT_GRAY, 500, 450);
		
		JPanel panel = buildPanelV2(panelRequest, toArrayOfComponent(listComponents));
//		
//		PanelRequest panelRequest2 = PanelRequest.autoPanelNonScroll(1, 510, 5, Color.white);
		return panel;
	}
	
	
	/**
	 * table header for data table
	 * @return
	 */
	private Component createDataTableHeader() { 
		columnFilterTextFields.clear();
		
		List<EntityElement> entityElements = entityProperty.getElements();
		List<Component> headerComponents = new ArrayList<>();
		
		headerComponents.add(label("No"));
		
		for(EntityElement element:entityElements) {
			
			final String elementId = element.getId();
			
			JButton buttonAsc = orderButton(elementId, ORDER_ASC);
			JButton buttonDesc = orderButton(elementId, ORDER_DESC);
			JPanel orderButtons = buildInlineComponent(45, buttonAsc, buttonDesc);
			
			JLabel columnLabel = label(elementId);
			
			if(element.getType().equals(FormField.FIELD_TYPE_DATE)) {
				
				//DD
				JTextField dateFilterDay = buildDateFilter(elementId, "day");
				columnFilterTextFields.put(elementId.concat("-day"), dateFilterDay);  
				//MM
				JTextField dateFilterMonth = buildDateFilter(elementId, "momth");
				columnFilterTextFields.put(elementId.concat("-month"), dateFilterMonth); 
				//yyyy
				JTextField dateFilterYear = buildDateFilter(elementId, "year");
				columnFilterTextFields.put(elementId.concat("-year"), dateFilterYear); 
				 
				Component columnHeader = buildVerticallyInlineComponent(100, columnLabel, dateFilterDay, dateFilterMonth, dateFilterYear, orderButtons );
				headerComponents.add(columnHeader);
//			} else if(element.getType().equals(FormField.FIELD_TYPE_IMAGE)) {
//				
//				Component columnHeader = ComponentBuilder.buildVerticallyInlineComponent(100, columnLabel, orderButtons);
//				headerComponents.add(columnHeader);
			} else {
			
				JTextField filterField = textField("");
				filterField.addKeyListener(helper.filterFieldKeyListener(elementId));
				
				if(fieldsFilter.get(elementId) != null) {
					filterField.setText(fieldsFilter.get(elementId).toString());
				}
				columnFilterTextFields.put(elementId, filterField);  
				
				Component columnHeader = buildVerticallyInlineComponent(100, columnLabel, filterField, orderButtons);
				headerComponents.add(columnHeader);
			}
			
		}
		
		headerComponents.add(label("Option"));
		
		Component header = rowPanelHeader(entityElements.size() + 2, 160, toArrayOfComponent(headerComponents));
		 
		return header;
	}
	
	/**
	 * create date filter column field
	 * @param elementId
	 * @param mode
	 * @return
	 */
	private JTextField buildDateFilter(String elementId, String mode) {
		//DD
		JTextField dateFilter = textField("");
		dateFilter.addKeyListener(helper.filterFieldKeyListener(elementId.concat("-"+mode)));
		
		if(fieldsFilter.get(elementId.concat("-"+mode)) != null) {
			dateFilter.setText(fieldsFilter.get(elementId.concat("-"+mode)).toString());
		}
		return dateFilter;
	}

	/**
	 * create order button
	 * @param elementId
	 * @param theOrderType
	 * @return
	 */
	private JButton orderButton(final String elementId, final String theOrderType) {
		JButton button = button(theOrderType.equals(ORDER_ASC)?'˄':'˅');
		button.addActionListener(new ActionListener() { 
			@Override
			public void actionPerformed(ActionEvent e) {
				setOrderBy(elementId);
				setOrderType(theOrderType);
				getHandler().getEntities();
			}
		});
		
		if(elementId.equals(orderBy) && theOrderType.equals(orderType)) {
			button.setBackground(Color.WHITE);
		}
		
		button.setSize(50, 20);
		return button ;
	}

	

	public void setComboBoxValuesContainer(String elementId, List<Map> objectList) { 
		comboBoxListContainer.put(elementId, objectList);
	}

	
	
	/**
	 * update managed entity
	 * @param elementId
	 * @param selectedObjectFromList
	 */
	public void updateManagedObject(String elementId, Object value) {
		if(null == managedObject) {
			setManagedObject(new HashMap<String, Object>());
		}
		
		Log.log("Update managed object(",elementId,"):",value);
		
		managedObject.put(elementId, value);
	}
	
	public List<Map> getComboBoxValues(String elementId){
		return comboBoxListContainer.get(elementId); 
	}
	
	
	
	public static Object[] extractListOfSpecifiedField(List<Map> objectList, String optionItemName) {
		Object[] result = new Object[objectList.size()];
		for (int i = 0; i < objectList.size(); i++) {
			Map item = objectList.get(i);
			result[i] = item.get(optionItemName);
		}
		return result;
	} 

	public ManagementHandler getHandler() {
		return (ManagementHandler) appHandler;
	}

	/**
	 * 
	 * ======================================================
	 *                  CALLBACKS
	 * ======================================================           
	 *  
	 */
	
	/**
	 * handle response when add/update entity
	 * @param response
	 */
	public void callbackUpdateEntity(HashMap response) {
		Object code = response.get("code");
		
		if(code.equals("00")) {
			Dialogs.showInfoDialog("Update success!");
		}else {
			Dialogs.showErrorDialog("Update failed!");
		}
		Log.log("Callback update entity: ", response);
		
		ThreadUtil.run(new Runnable() {
			
			@Override
			public void run() {
				getHandler().getEntities();
				helper.doClearForm(); 
			}
		}); 

		setEditMode(false);
		
	}

	/**
	 * handle response when get filtered entities
	 * @param response
	 */
	public void callbackGetFilteredEntities(final ShopApiResponse response) { 
		
		ThreadUtil.run(new Runnable() {
			
			@Override
			public void run() {
				Log.log("Filtered Entities: ", response.getEntities());
				int totalData = response.getTotalData();
				int totalPage = totalData / getSelectedLimit();
				
				if(totalPage < selectedPage) {
					setSelectedPage(totalPage);
				}
				
				setEntityList(response.getEntities());
				setTotalData(response.getTotalData());  
				setListPanel(buildListPanel()); 
				refresh();
				updateColumnFilterFieldFocus();
				validateNavigationPanel();
			}
		});
		
	}
	
	
	
	/**
	 * reset cursor to the last focus component
	 */
	private void updateColumnFilterFieldFocus() {
		ThreadUtil.run(new Runnable() {
			
			@Override
			public void run() {
				try {
					String textValue = columnFilterTextFields.get(currentElementIdFocus).getText();
					columnFilterTextFields.get(currentElementIdFocus).requestFocus();
					columnFilterTextFields.get(currentElementIdFocus).setSelectionStart(textValue.length());
					columnFilterTextFields.get(currentElementIdFocus).setSelectionEnd(textValue.length());
				}catch (Exception e) {
					 
				}
				
			}
		});
		
	}

	/**
	 * callback when edit button pressed
	 * @return
	 */
	public MyCallback callbackGetSingleEntity() { 
		return new MyCallback() {
			
			@Override
			public void handle(Object... params) throws Exception {
				 Map entity = (Map) params[0];
				 helper.populateFormInputs(entity);
				 setEditMode(true);
				
			} 
			
		};
	}
	
	/**
	 * get value of managed object with field: key
	 * @param key
	 * @return
	 */
	public Object getManagedObjectValue(String key) {
		return managedObject.get(key);
	}
	
	/**
	 * get managed object fields
	 * @return
	 */
	public Set<String> managedObjectKeySet(){
		return managedObject.keySet();
	} 
	
	public void setIconOnMultipleImagePreviewLabel(String key, int index, Icon icon) { 
		multipleImagePreviews.get(key).get(index).setIcon(icon );
	}

	public Component getSingleImagePreviewLabel(String key) { 
		return singleImagePreviews.get(key); 
	}

	/**
	 * get EntityElement by elementId
	 * @param elementId
	 * @return
	 */
	public EntityElement getEntityElement(String elementId) {
		 
		for (EntityElement entityElement : entityProperty.getElements()) {
			if(entityElement.getId().equals(elementId)) {
				return entityElement;
			}
		}
		return null;
	}

	/**
	 * validate managedObject values
	 */
	public void validateEntity() {
		if(null == managedObject) {
			return;
		}
		
		Set<String> keys = managedObject.keySet();
		
		for(String key: keys) {
			
			EntityElement element = getEntityElement(key);
			Object value = managedObject.get(key);
			
			if(null == element || null == value) {
				continue;
			} 
			
			if(FormField.FIELD_TYPE_IMAGE.equals(element.getType()) && element.isMultiple()) {
				String[] rawValue = value.toString().split("~"); 
				Log.log("rawValue length:",rawValue.length);
				List<String> validValues = new ArrayList<>();
				for (int i = 0; i < rawValue.length; i++) {
				
					if(rawValue[i] != null && rawValue[i].equals(NULL_IMAGE) == false) { 
						validValues.add(rawValue[i]); 
					}
					
				}
				
				String[] rawOfValidValue = StringUtil.toArrayOfString(validValues);
				Log.log("rawOfValidValue.length:",rawOfValidValue.length);
				String finalValidValue = String.join("~", rawOfValidValue);
				updateManagedObject(key, finalValidValue);
				
			}
		}
		
		Log.log("entity validated");
		Log.log("entity: ",managedObject);
	}

	public void putFilterValue(String key, String value) {
		fieldsFilter.put(key, value);
	}

}
