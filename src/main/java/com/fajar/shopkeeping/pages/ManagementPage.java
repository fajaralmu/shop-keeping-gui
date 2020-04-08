package com.fajar.shopkeeping.pages;

import static com.fajar.shopkeeping.util.MapUtil.objectEquals;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.JTextComponent;

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
import com.fajar.shopkeeping.service.AppContext;
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
	
	private JMenuItem menuBack;
	
	private final ManagementPageHelper helper;
	
	Component actionButtons = ComponentBuilder.buildInlineComponent(90, buttonSubmit, buttonClear, buttonRefresh); 
	
	
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

		PanelRequest panelRequest = PanelRequest.autoPanelNonScroll(2, 550, 5, Color.WHITE);
		panelRequest.setCenterAligment(true);

		if (formPanel == null) {
			formPanel = buildPanelV2(panelRequest, label("Please wait.."));
		}  
		if(listPanel == null) {
			listPanel = buildPanelV2(panelRequest, label("Please wait.."));
		}
		
		mainPanel = ComponentBuilder.buildPanelV2(panelRequest,

				title("Management::"+getEntityClassName(), 30), null,
				formPanel, 
				ComponentBuilder.buildVerticallyInlineComponent(500, getNavigationPanel(),  listPanel));

		parentPanel.add(mainPanel);
		exitOnClose();

	}
	
	/**
	 * generate data table navigations
	 * @return
	 */
	private JPanel getNavigationPanel() {
		
		if(selectedLimit == 0) {
			return new JPanel();
		} 
		
		Component[] navigationButtons = generationNavigationButtons();
		PanelRequest panelRequest = PanelRequest.autoPanelScrollWidthHeightSpecified(navigationButtons .length, 50, 1, Color.gray, 500, 40);
		JPanel panelNavigation = ComponentBuilder.buildPanelV2(panelRequest, navigationButtons);
		
		JPanel panelPageLimit = ComponentBuilder.buildInlineComponent(60, label("page"), inputPage, label("limit"), inputLimit, buttonFilterEntity, labelTotalData);
		return ComponentBuilder.buildVerticallyInlineComponent(500, panelNavigation, panelPageLimit);
	}

	
	/**
	 * generate array of navigation buttons
	 * @return
	 */
	private Component[] generationNavigationButtons() {
		
		if(selectedLimit == 0) {
			return new Component[] {};
		}
		labelTotalData.setText("Total: "+totalData);
		int totalPage =  (totalData) / ( selectedLimit);
		if(totalData % selectedLimit != 0) {
			totalPage ++;
		}
		Component[] navigationButtons = new Component[totalPage ];
		
		for (int i = 0; i < totalPage; i++) {
			JButton button = button(i+1, 50, 20); 
			button.addActionListener(navigationListener(i));
			button.setBackground(i == selectedPage? Color.orange : Color.yellow);
			
			navigationButtons[i] = button; 
		}
		return navigationButtons;
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
		SharedContext context = AppContext.getContext(ContextConstants.CTX_MANAGEMENT_PAGE);
		this.entityClass = context.getEntityClass();
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
				fieldsFilter.clear();
				clearForm();
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
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				formPanel = generateEntityForm();
				clearForm();
				getHandler().getEntities();
				
				preInitComponent();
				initEvent();
				Loadings.end();
			}
		});

		thread.start();

	}
	
	/**
	 * clear input fields or set it to default values
	 */
	private void clearForm() {
		
		ThreadUtil.run(new Runnable() {
			public void run() {
				Map<String, Component> inputs = formInputFields;
				Set<String> inputKeys = inputs.keySet();
				for (String key : inputKeys) {

					Component formField = formInputFields.get(key);
					
					if(formField instanceof JTextField)
						try {
							((JTextField ) formField).setText("");
						}catch (Exception e) { }
					
					if(formField instanceof JTextArea)
						try {
							((JTextArea ) formField).setText("");
						}catch (Exception e) { }
					
					if(formField instanceof JComboBox)
					{
						//leave as it
					}
					
					//multiple image
					if(formField instanceof JPanel) {
						try {
							JScrollPane scrollPane = (JScrollPane)((JPanel)formInputFields.get(key)).getComponent(0); 
							removeAllImageSelectionField(scrollPane);
						}catch (Exception e) {
							// TODO: handle exception
						}
					}
					
				}
				
				Set<String> singleImageKeys = singleImagePreviews.keySet();
				for (String key : singleImageKeys) {
					singleImagePreviews.get(key).setIcon(new ImageIcon());
				}
				Set<String> multipleImagePreviewsKeys = multipleImagePreviews.keySet();
				for (String key : multipleImagePreviewsKeys) {
					multipleImagePreviews.get(key).clear();
				}
				
				
				setEditMode(false);
			}
		});
		
		
		
	}

	/**
	 * CRUD Form Generation
	 * 
	 * @return
	 */
	private JPanel generateEntityForm() {

		PanelRequest panelRequest = PanelRequest.autoPanelNonScroll(1, 420, 2, Color.WHITE);

		List<Component> formComponents = new ArrayList<Component>();
 
		comboBoxListContainer.clear();
		formInputFields.clear();
		entityProperty = EntityUtil.createEntityProperty(entityClass, null);

		List<EntityElement> entityElements = entityProperty.getElements();

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
 
				inputComponent = buildFixedComboBox(element, fieldType);
			} else if (elementType.equals(FormField.FIELD_TYPE_DYNAMIC_LIST)) {
				 
				inputComponent = buildDynamicComboBox(element, fieldType);
			} else if (element.isIdentity()) {
				
				inputComponent = textFieldDisabled("ID"); 
				this.idFieldName = elementId;  
			} else if (elementType.equals(FormField.FIELD_TYPE_TEXTAREA)) {

				inputComponent = textArea(elementId);
				((JTextArea) inputComponent).addKeyListener(textAreaActionListener((JTextArea) inputComponent, elementId));
				
			} else if (elementType.equals("color")) {
				skipFormField = true;
				continue;
			} else if (elementType.equals(FormField.FIELD_TYPE_NUMBER)) {

				inputComponent = numberField(elementId);
				((JTextField) inputComponent).addKeyListener(crudTextFieldActionListener((JTextField) inputComponent, elementId));
				
			} else if (elementType.equals(FormField.FIELD_TYPE_DATE)) {

				inputComponent = dateChooser();
				((JDateChooser) inputComponent).addPropertyChangeListener(dateChooserPropertyChangeListener(
						(JDateChooser) inputComponent, elementId ));
				
			} else if (elementType.equals(FormField.FIELD_TYPE_IMAGE)) {
				skipFormField = true;
				inputComponent = buildImageField(element, fieldType, element.isMultiple());
				
			} else {
				inputComponent = textField(elementId);
				((JTextField) inputComponent).addKeyListener(crudTextFieldActionListener((JTextField) inputComponent, elementId) );
			}
			inputComponent.setSize(200, inputComponent.getHeight());
			
			if(!skipFormField) {
				formInputFields.put(elementId, inputComponent);
			}
			
			formComponents.add(ComponentBuilder.buildInlineComponent(200, lableName, inputComponent));
			
		}
		
		formComponents.add(actionButtons );

		JPanel formPanel = buildPanelV2(panelRequest, toArrayOfComponent(formComponents));
		return formPanel;
	}
	
	/**
	 * build single image input form field
	 * @param element
	 * @param fieldType
	 * @param multiple
	 * @return
	 */
	private JPanel buildImageField(EntityElement element,  Class<?> fieldType, boolean multiple) {

		if(multiple) {
			JPanel imageSelectionField = ComponentBuilder.buildVerticallyInlineComponent(200, ComponentBuilder.infoLabel("click add..", SwingConstants.CENTER)); 
			JButton buttonAddImage = button("add"); 
			JPanel imageSelectionWrapperPanel = ComponentBuilder.buildVerticallyInlineComponentScroll(190, 300, imageSelectionField, buttonAddImage) ; 
			
			buttonAddImage.addActionListener(buttonAddImageFieldListener( element, imageSelectionWrapperPanel));
			
			JPanel inputPanel = ComponentBuilder.buildVerticallyInlineComponent(200, imageSelectionWrapperPanel, buttonAddImage); 
			
			formInputFields.put(element.getId(), imageSelectionWrapperPanel);
			
			return inputPanel;
			
		}else {
			JLabel imagePreview = createImagePreview();
			
			addSingleImageContainer(element.getId(), imagePreview);
			
			JButton buttonChoose = button("choose file", 160, onChooseSingleImageFileClick(new JFileChooser(), element.getId())); 
			buttonChoose.addActionListener(onChooseSingleImageFileClick(new JFileChooser(), element.getId()));
			
			JButton buttonClear = button("clear", 160, buttonClearSingleImageClick(element.getId()));  
			JPanel inputPanel = ComponentBuilder.buildVerticallyInlineComponent(205, buttonChoose, buttonClear, imagePreview) ; 
			
			return inputPanel;
		}
	}
	
	private void addSingleImageContainer(String elementId, JLabel imagePreview) {
		singleImagePreviews.put(elementId, imagePreview);
		
	}

	private ActionListener buttonAddImageFieldListener( final EntityElement element, final JPanel imageSelectionScrollableWrapper) {
		 
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JScrollPane scrollableWrapper = (JScrollPane)imageSelectionScrollableWrapper.getComponent(0);  
				addNewImageSelectionField(element, scrollableWrapper);
			}
		};
	}
	
	/**
	 * remove all file chooser buttons in multiple image selection fields
	 * @param imageSelectionScrollPane
	 */
	private void removeAllImageSelectionField(JScrollPane imageSelectionScrollPane) {
		
		try {
			JPanel panel = (JPanel)  imageSelectionScrollPane.getViewport().getView(); 
			JPanel imageSelectionPanel = (JPanel) panel.getComponent(0);  
			imageSelectionPanel.removeAll();
			
			Dimension newDimension = new Dimension(imageSelectionPanel.getWidth(), 200);// - componentToRemove.getHeight() );
			imageSelectionPanel.setSize(newDimension); 
			JPanel wrapperPanel  = ComponentBuilder.buildInlineComponent(imageSelectionPanel.getWidth() + 5, imageSelectionPanel);  
			
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
	private void addNewImageSelectionField(EntityElement element, JScrollPane imageSelectionScrollPane) {
		Log.log("addNewImageSelectionField");
		
		JPanel panel = (JPanel)  imageSelectionScrollPane.getViewport().getView(); 
		JPanel imageSelectionPanel = (JPanel) panel.getComponent(0); 
		
		if(imageSelectionPanel.getComponentCount() > 0 && imageSelectionPanel.getComponent(0) instanceof MyInfoLabel) {
			imageSelectionPanel.removeAll();
			Log.log("REMOVE ALL");
		}
		
		int index = imageSelectionPanel.getComponentCount();
		
		JLabel imagePreview = createImagePreview(); 
		
		JButton buttonChoose = button("choose file ("+index+")", 160, onChooseMultipleImageFileClick(new JFileChooser(), element.getId(), index));  
		JButton buttonClear = button("clear", 160, buttonClearMultipleImageClick(element.getId(), index)); 
		JButton buttonRemove = button("remove" , 160, removeImageSelectionListener(element, index, imageSelectionScrollPane));
		
		int componentCount = imageSelectionPanel.getComponentCount();
		
		JPanel newImageSelection = ComponentBuilder.buildVerticallyInlineComponent(200, buttonChoose, buttonClear, buttonRemove, imagePreview) ;  
		newImageSelection.setBounds(newImageSelection.getX(), componentCount * newImageSelection.getHeight(), newImageSelection.getWidth(), newImageSelection.getHeight());
		
		Dimension newDimension = new Dimension(imageSelectionPanel.getWidth(),  imageSelectionPanel.getHeight() + newImageSelection.getHeight() );
		imageSelectionPanel.setSize(newDimension);
		imageSelectionPanel.add(newImageSelection);  
		JPanel wrapperPanel  = ComponentBuilder.buildInlineComponent(imageSelectionPanel.getWidth() + 5, imageSelectionPanel);  
		
		updateScrollPane(imageSelectionScrollPane, wrapperPanel, newDimension);
		
		addMultipleImageContainer(element.getId(), imagePreview);
		
	}
	
	
	private ActionListener removeImageSelectionListener(final EntityElement element,final int index, final JScrollPane imageSelectionScrollableWrapper) {
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) { 
				removeImageSelectionItem(element ,index, imageSelectionScrollableWrapper);
				
			}
		};
	}
	 
	/**
	 * do remove input fields for upload image
	 * @param index
	 * @param imageSelectionScrollPane
	 */
	private void removeImageSelectionItem(EntityElement element, int index, JScrollPane imageSelectionScrollPane) {
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
		JPanel wrapperPanel  = ComponentBuilder.buildInlineComponent(imageSelectionPanel.getWidth() + 5, imageSelectionPanel);  
		
		updateScrollPane(imageSelectionScrollPane, wrapperPanel, newDimension);
		
		removeMultipleImageContainerItem(element.getId(), index);
		
	}
	
	private void removeMultipleImageContainerItem(String id, int index) {
		// TODO multipleImagePreviews.get(id).remove(index);
		Log.log("removeMultipleImageContainerItem[",id,"] at", index);
		try {
			 Object currentObject = managedObject.get(id);
			 String[] rawString = currentObject.toString().split("~");
			 Log.log( rawString);
			 if(rawString.length >= index + 1) {
				 rawString[index] = NULL_IMAGE;
			 }
			 String newValue = String.join("~", rawString);
			 Log.log("new value: ",newValue);
			 updateManagedObject(id, newValue);
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
		scrollPane .setViewportView(component);
		scrollPane .validate(); 
	}
	
	/**
	 * add new multiple image selection label(image icon)
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
	private JLabel createImagePreview() {
		JLabel imagePreview = label("No Preview."); 
		imagePreview.setSize(160, 160);
		imagePreview.setBorder(BorderFactory.createLineBorder(Color.BLACK)); 
		return imagePreview;
	}
	
	/**
	 * clear image in multiple image selection
	 * @param id
	 * @param index
	 * @return
	 */
	private ActionListener buttonClearMultipleImageClick(final String elementId, final int index) {
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try { 
					multipleImagePreviews.get(elementId).get(index).setIcon(new ImageIcon());
//					updateManagedObject(elementId, null);
				} catch (Exception e2) { }
				
			}
		};
	}

	/**
	 * clear image in single image selection
	 * @param elementId
	 * @return
	 */
	private ActionListener buttonClearSingleImageClick(final String elementId) { 
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					//it means does not modify current value if in edit mode
					singleImagePreviews.get(elementId).setIcon(new ImageIcon());
					updateManagedObject(elementId, null);
				} catch (Exception e2) { }
				
			}
		};
	}
	
	/**
	 * when filechooser for multiple image clicked
	 * @param jFileChooser
	 * @param id
	 * @param index
	 * @return
	 */
	private ActionListener onChooseMultipleImageFileClick(final JFileChooser fileChooser,final  String elementId,final  int index) {
		  
			
			return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int returnVal = fileChooser.showOpenDialog(parentPanel);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();

					try { 
						JLabel imagePreview = multipleImagePreviews.get(elementId).get(index); 
						multipleImagePreviews.get(elementId).get(index).setIcon(ComponentBuilder.imageIconFromFile(file.getCanonicalPath(), imagePreview.getWidth(), imagePreview.getHeight()));
						String base64 = StringUtil.getBase64Image(file); 
						Object currentValue =  managedObject.get(elementId);
						Log.log("currentValue: ",currentValue);
						
						if(null == currentValue) {
							updateManagedObject(elementId, base64);
						}else {
							String[] rawValues = currentValue.toString().split("~");
							String finalValue =  currentValue.toString();
							if(rawValues.length >= index + 1) {
								rawValues[index] = base64;
								finalValue = String.join("~", rawValues);
							}else {
								finalValue +=( "~"+base64);
							}
							
							updateManagedObject(elementId, finalValue);
						}
						
						Log.log(elementId,":",managedObject.get(elementId));
						
//						updateManagedObject(elementId, base64);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} else {
					System.out.println("Open command cancelled by user.");
				}
			}
			 
		};
	}

	/**
	 * when fileChooser for image clicked
	 * @param fileChooser
	 * @param imagePreview
	 * @param elementId
	 * @return
	 */
	private ActionListener onChooseSingleImageFileClick(final JFileChooser fileChooser, final String elementId) {
		
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int returnVal = fileChooser.showOpenDialog(parentPanel);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();

					try {
//						Dialogs.showInfoDialog("FILE PATH:", file.getCanonicalPath()); 
						JLabel imagePreview = singleImagePreviews.get(elementId);
						singleImagePreviews.get(elementId).setIcon(ComponentBuilder.imageIconFromFile(file.getCanonicalPath(), imagePreview.getWidth(), imagePreview.getHeight()));
						String base64 = StringUtil.getBase64Image(file); 
						updateManagedObject(elementId, base64);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} else {
					System.out.println("Open command cancelled by user.");
				}

			}
		};
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
				final Field field = EntityUtil.getDeclaredField(entity.getClass(), element.getId());
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
			components[colSize - 1] = idExist ? editButton(idFieldName, idValue) : label("-");
			
			sequenceNumber++;
			JPanel rowPanel = rowPanel(colSize , columnWidth, components);
			listComponents.add(rowPanel);
		}
		
		PanelRequest panelRequest = PanelRequest.autoPanelScrollWidthHeightSpecified(1, columnWidth * colSize, 5, Color.LIGHT_GRAY, 500, 450);
		
		JPanel panel = buildPanelV2(panelRequest, toArrayOfComponent(listComponents));
//		
//		PanelRequest panelRequest2 = PanelRequest.autoPanelNonScroll(1, 510, 5, Color.white);
		return panel;
	}
	
	/**
	 * button edit on datatable row
	 * @param idFieldName2
	 * @param idValue2
	 * @return
	 */
	private JButton editButton(final String idFieldName2, final Object idValue2) {
		JButton button = button("Edit");
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				getHandler().getSingleEntity(idFieldName2, idValue2);
				
			}
		});
		return button;
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
			JPanel orderButtons = ComponentBuilder.buildInlineComponent(45, buttonAsc, buttonDesc);
			
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
				 
				Component columnHeader = ComponentBuilder.buildVerticallyInlineComponent(100, columnLabel, dateFilterDay, dateFilterMonth, dateFilterYear, orderButtons );
				headerComponents.add(columnHeader);
//			} else if(element.getType().equals(FormField.FIELD_TYPE_IMAGE)) {
//				
//				Component columnHeader = ComponentBuilder.buildVerticallyInlineComponent(100, columnLabel, orderButtons);
//				headerComponents.add(columnHeader);
			} else {
			
				JTextField filterField = textField("");
				filterField.addKeyListener(filterFieldKeyListener(elementId));
				
				if(fieldsFilter.get(elementId) != null) {
					filterField.setText(fieldsFilter.get(elementId).toString());
				}
				columnFilterTextFields.put(elementId, filterField);  
				
				Component columnHeader = ComponentBuilder.buildVerticallyInlineComponent(100, columnLabel, filterField, orderButtons);
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
		dateFilter.addKeyListener(filterFieldKeyListener(elementId.concat("-"+mode)));
		
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
				orderBy = elementId;
				orderType = theOrderType;
				getHandler().getEntities();
			}
		});
		
		if(elementId.equals(orderBy) && theOrderType.equals(orderType)) {
			button.setBackground(Color.WHITE);
		}
		
		button.setSize(50, 20);
		return button ;
	}

	/**
	 * when filter field typed
	 * @param key
	 * @return
	 */
	private KeyListener filterFieldKeyListener(final String key) { 
		return new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) { }
			
			@Override
			public void keyReleased(KeyEvent e) {
				String value = ((JTextField) e.getComponent()).getText();
				fieldsFilter.put(key, value);
				currentElementIdFocus = key;
				
				getHandler().getEntities();
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) { }
		};
	}

	/**
	 * build dynamic combo box for CRUD form
	 * @param element
	 * @param elementId
	 * @param fieldType
	 * @return
	 */
	private JComboBox buildDynamicComboBox(EntityElement element, Class<?> fieldType) {
		String elementId = element.getId();
		String optionItemName = element.getOptionItemName();
		JComboBox inputComponent = ComponentBuilder.buildEditableComboBox("", "type something..");
		inputComponent.setSize(150, 20);
		 
		(inputComponent).getEditor().getEditorComponent()
				.addKeyListener(dynamicComboBoxListener(  inputComponent, optionItemName, fieldType, elementId));
		(inputComponent).addActionListener(comboBoxOnSelectListener(  inputComponent, optionItemName, fieldType, elementId));
		
		return inputComponent;
	}

	/**
	 * build fixed combo box for CRUD form
	 * @param element
	 * @param elementId
	 * @param fieldType
	 * @return
	 */
	private JComboBox  buildFixedComboBox(EntityElement element,  Class fieldType) {
		String optionItemName = element.getOptionItemName(); 
		String elementId = element.getId();
		/**
		 * call API
		 */
		List<Map> objectList = getHandler().getAllEntity(fieldType);
		comboBoxListContainer.put(elementId, objectList);

		Object[] comboBoxValues = extractListOfSpecifiedField(objectList, optionItemName);
		JComboBox inputComponent = ComponentBuilder.buildComboBox(objectList.get(0).get(optionItemName), comboBoxValues);
		
		inputComponent .addActionListener(comboBoxOnSelectListener( inputComponent, optionItemName, fieldType, elementId));
		return inputComponent;
	}

	/**
	 * when JDateChooser changed
	 * @param inputComponent
	 * @param elementId
	 * @return
	 */
	private PropertyChangeListener dateChooserPropertyChangeListener(final JDateChooser inputComponent, final String elementId) {
		 
		return new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				Date selectedDate = inputComponent.getDate(); 
				String convertedDate = SIMPLE_DATE_FORMAT.format(selectedDate);
				Log.log("selected date converted: ", convertedDate);
				
				updateManagedObject(elementId, convertedDate);
			}
		};
	}

	/**
	 * when CRUD textArea has changed
	 * @param inputComponent
	 * @param elementId
	 * @return
	 */
	private KeyListener textAreaActionListener(final JTextArea inputComponent, final String elementId) {
		 
		return new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) { }

			@Override
			public void keyPressed(KeyEvent e) { }

			@Override
			public void keyReleased(KeyEvent e) {
				String value = inputComponent.getText();
				updateManagedObject(elementId, value);
				
			}
			
			 
		};
	}

	/**
	 * when CRUD jTextfield has changed
	 * @param inputComponent
	 * @param elementId
	 * @return
	 */
	private KeyListener crudTextFieldActionListener(final JTextField inputComponent, final String elementId) {
		 
		return new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) { }

			@Override
			public void keyPressed(KeyEvent e) { }

			@Override
			public void keyReleased(KeyEvent e) {
				String value = inputComponent.getText();
				updateManagedObject(elementId, value);
				
			}  
		};
	}

	/**
	 * when CRUD combo box selected
	 * @param inputComponent
	 * @param optionItemName
	 * @param fieldType
	 * @param elementId
	 * @return
	 */
	private ActionListener comboBoxOnSelectListener(final JComboBox inputComponent, final String optionItemName, final Class<?> fieldType,
			final String elementId) {
		 
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				 Object selectedValue = inputComponent.getSelectedItem();
				 List<Map> rawList = comboBoxListContainer.get(elementId); 
				 Map selectedObjectFromList = ManagementHandler.getMapFromList(optionItemName, selectedValue, rawList);
				 
				 if(null == selectedObjectFromList) {
					 return ;
				 }
				 
				 updateManagedObject(elementId, selectedObjectFromList);
				 Log.log("managedObject: ",managedObject);
			}

			
		};
	}
	
	/**
	 * update managed entity
	 * @param elementId
	 * @param selectedObjectFromList
	 */
	private void updateManagedObject(String elementId, Object value) {
		if(null == managedObject) {
			managedObject = new HashMap<>();
		}
		
		Log.log("Update managed object(",elementId,"):",value);
		
		managedObject.put(elementId, value);
	}
	
	
	/**
	 * when CRUD dynamic comboBox has changed
	 * @param dynamicComboBox
	 * @param optionItemName
	 * @param fieldType
	 * @return
	 */
	private KeyListener dynamicComboBoxListener(final JComboBox dynamicComboBox, final String optionItemName, final Class<?> fieldType, final String elementId) {

		return new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent event) {
				
				final String componentText = ((JTextComponent) dynamicComboBox.getEditor().getEditorComponent()).getText(); 
				getHandler().getEnitiesFormDynamicDropdown(fieldType, optionItemName, componentText, new MyCallback() {
					
					@Override
					public void handle(Object... params) throws Exception { 
						
						HashMap shopApiResponse = (HashMap) params[0];
						populateDynamicDropdown(shopApiResponse);
					}

					/**
					 * populate items on comboBox
					 * @param shopApiResponse
					 */
					private void populateDynamicDropdown(HashMap shopApiResponse) {
						List  entities = (List) shopApiResponse.get("entities");
						comboBoxListContainer.put(elementId, entities);
						
						dynamicComboBox.removeAllItems();
						
						for (Object object : entities) {
							Map mapItem = (Map) object;
							dynamicComboBox.addItem(mapItem.get(optionItemName));
						}
						dynamicComboBox.setSelectedItem(componentText);
						((JTextComponent) dynamicComboBox.getEditor().getEditorComponent()).setSelectionStart(componentText.length());
						((JTextComponent) dynamicComboBox.getEditor().getEditorComponent()).setSelectionEnd(componentText.length());
						dynamicComboBox.showPopup();
					}
				});

			}

			@Override
			public void keyPressed(KeyEvent e) {

			}
		};
	}
 

	private Object[] extractListOfSpecifiedField(List<Map> objectList, String optionItemName) {
		Object[] result = new Object[objectList.size()];
		for (int i = 0; i < objectList.size(); i++) {
			Map item = objectList.get(i);
			result[i] = item.get(optionItemName);
		}
		return result;
	} 

	private ManagementHandler getHandler() {
		return (ManagementHandler) appHandler;
	}

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
		
		getHandler().getEntities();
		clearForm();

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
				
			}
		});
		
	}
	
	/**
	 * action when navigation button is clicked
	 * @param i
	 * @return
	 */
	private ActionListener navigationListener(final int i) { 
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setSelectedPage(i);
				getHandler().getEntities();
			}
		};
	}
	
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
				 populateFormInputs(entity);
				 setEditMode(true);
				
			} 
			
		};
	}
	
	/**
	 * populate form fields by given entity
	 * @param entity
	 */
	private void populateFormInputs(Map entity) {
		clearForm();
		setManagedObject(entity);
		Set<String> objectKeys = managedObject.keySet();
		
		for (String key : objectKeys) {
			Object value = managedObject.get(key);
			EntityElement entityElement = getEntityElement(key);
			
			if(null == entityElement) {
				continue;
			}
			
			String elementType = entityElement.getType();
			
			if(value == null) {
				value = "";
			}
			boolean isImage = FormField.FIELD_TYPE_IMAGE.equals(elementType);
			Component formField = formInputFields.get(key);
			if(formField != null && !isImage) {
			
				if(formField instanceof JTextField)
					try {
						((JTextField ) formField).setText(value.toString());
					}catch (Exception e) { }
				
				if(formField instanceof JTextArea)
					try {
						((JTextArea ) formField).setText(value.toString());
					}catch (Exception e) { }
				
				if(formField instanceof JComboBox)
					try {
						Map valueMap = (Map) value;
						EntityElement element = getEntityElement(key);
						String optionItemName = element.getOptionItemName();
						((JComboBox) formField).setSelectedItem(valueMap.get(optionItemName));
					} catch (Exception e) { }
				
			} else if(isImage && entityElement.isMultiple() == false) {
				formField = singleImagePreviews.get(key);
				final String imageName = value.toString();
				final JLabel iconLabel =(JLabel) formField;
				
				ThreadUtil.run(new Runnable() {
					
					@Override
					public void run() {
						Icon imageIcon = ComponentBuilder.imageIcon(UrlConstants.URL_IMAGE+imageName, 160, 160);
						(iconLabel).setIcon(imageIcon );
						
					}
				}); 
				
				
			} else if(isImage && entityElement.isMultiple() == true) {
				String[] rawValues = value.toString().split("~");
				Log.log("rawValues.length:",entity);
				String[] newValues = new String[rawValues.length];
				int index = 0;
				
				for (String string : rawValues) {
					String imageUrl  = UrlConstants.URL_IMAGE + string;
					JScrollPane scrollPane = (JScrollPane)((JPanel)formInputFields.get(key)).getComponent(0); 
					addNewImageSelectionField(entityElement, scrollPane);
					
					Icon icon = ComponentBuilder.imageIcon(imageUrl, 160, 160);
					multipleImagePreviews.get(key).get(index).setIcon(icon );
					
					newValues[index] = "{ORIGINAL>>"+string+"}";
					
					index++;
				}
				
				updateManagedObject(key, String.join("~", newValues)); 
				
			} else {
				Log.log("key not managed: ",key);
			}
		}
		
		Log.log("::singleObjectPreviews:",singleImagePreviews);
		
		
	}
	
	/**
	 * get EntityElement by elementId
	 * @param elementId
	 * @return
	 */
	private EntityElement getEntityElement(String elementId) {
		
		List<EntityElement> elements = entityProperty.getElements();
		for (EntityElement entityElement : elements) {
			if(entityElement.getId().equals(elementId)) {
				return entityElement;
			}
		}
		return null;
	}

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

}
