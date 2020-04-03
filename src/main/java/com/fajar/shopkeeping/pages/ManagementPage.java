package com.fajar.shopkeeping.pages;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import com.fajar.annotation.FormField;
import com.fajar.dto.ShopApiResponse;
import com.fajar.entity.BaseEntity;
import com.fajar.entity.CostFlow;
import com.fajar.entity.setting.EntityElement;
import com.fajar.entity.setting.EntityProperty;
import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.component.ComponentBuilder;
import com.fajar.shopkeeping.component.Dialogs;
import com.fajar.shopkeeping.component.Loadings;
import com.fajar.shopkeeping.handler.ManagementHandler;
import com.fajar.shopkeeping.model.PanelRequest;
import com.fajar.shopkeeping.util.EntityUtil;
import com.fajar.shopkeeping.util.Log;
import com.toedter.calendar.JDateChooser;

import lombok.Data;

@Data
public class ManagementPage extends BasePage {

	private EntityProperty entityProperty;

	private JPanel formPanel;

	private final JButton buttonSubmit = button("Submit");
	private final JButton buttonClear = button("Clear");
	private final JButton buttonFilterEntity = button("Filter");

	private Class<? extends BaseEntity> entityClass = CostFlow.class;
 
	private Map<String, List<Map>> comboBoxListContainer = new HashMap<>();
	private Map<String, Object> managedObject = new HashMap<>();
	private Map<String, Object> fieldsFiler = new HashMap<>();
	
	private String idFieldName;
	
	private final JTextField inputPage = numberField("0");
	private final JTextField inputLimit = numberField("10");

	private String selectedPage;
	private String selectedLimit;
	
	
	public ManagementPage() {
		super("Management", BASE_WIDTH, BASE_HEIGHT);
	}

	@Override
	public void show() {
		super.show();
		loadForm();
	}

	@Override
	public void initComponent() {

		PanelRequest panelRequest = new PanelRequest(1, 670, 20, 15, Color.WHITE, 30, 30, 0, 0, false, true);

		if (formPanel == null) {
			formPanel = buildPanelV2(panelRequest, label("Please wait.."));
		} 

		mainPanel = ComponentBuilder.buildPanelV2(panelRequest,

				title("Management Page", 50), formPanel,
				ComponentBuilder.buildInlineComponent(100, buttonSubmit, buttonClear),
				ComponentBuilder.buildInlineComponent(100, label("page"), inputPage, label("limit"), inputLimit, buttonFilterEntity));

		parentPanel.add(mainPanel);
		exitOnClose();

	}

	@Override
	protected void initEvent() {
		super.initEvent();
		buttonSubmit.addActionListener(getHandler().submit());
		inputPage.addKeyListener(textFieldActionListener(inputPage, "selectedPage"));
		inputLimit.addKeyListener(textFieldActionListener(inputLimit, "selectedLimit"));
		buttonFilterEntity.addActionListener(getHandler().filterEntity()); 
		
	}
	
	@Override
	protected void setDefaultValues() {  
		selectedPage = inputPage.getText();
		selectedLimit = inputLimit.getText();
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
				preInitComponent();
				initEvent();
				Loadings.end();
			}
		});

		thread.start();

	}

	/**
	 * CRUD Form Generation
	 * 
	 * @return
	 */
	private JPanel generateEntityForm() {

		PanelRequest panelRequest = PanelRequest.autoPanelNonScroll(1, 300, 10, Color.yellow);

		List<Component> formComponents = new ArrayList<Component>();
 
		comboBoxListContainer.clear();
		entityProperty = EntityUtil.createEntityProperty(entityClass, null);

		List<EntityElement> entityElements = entityProperty.getElements();

		for (EntityElement element : entityElements) {
			final String elementId = element.getId();

			Field entityField = EntityUtil.getDeclaredField(entityClass, elementId);
			Class<?> fieldType = entityField.getType();
			JLabel lableName = label(element.getLableName());

			String elementType = element.getType();
			if (elementType == null) {
				continue;
			}

			Component inputComponent = textField("Not Configured");
			inputComponent.setFocusable(true);
			inputComponent.requestFocus();

			if (elementType.equals(FormField.FIELD_TYPE_FIXED_LIST)) {
 
				inputComponent = buildFixedComboBox(element, elementId, fieldType);
			} else if (elementType.equals(FormField.FIELD_TYPE_DYNAMIC_LIST)) {
				 
				inputComponent = buildDynamicComboBox(element, elementId, fieldType);
			} else if (element.isIdentity()) {
				
				inputComponent = textFieldDisabled("ID"); 
				this.idFieldName = elementId;  
			} else if (elementType.equals(FormField.FIELD_TYPE_TEXTAREA)) {

				inputComponent = textArea(elementId);
				((JTextArea) inputComponent).addKeyListener(textAreaActionListener((JTextArea) inputComponent, elementId));
				
			} else if (elementType.equals("color")) {
				
				continue;
			} else if (elementType.equals(FormField.FIELD_TYPE_NUMBER)) {

				inputComponent = numberField(elementId);
				((JTextField) inputComponent).addKeyListener(crudTextFieldActionListener((JTextField) inputComponent, elementId));
				
			} else if (elementType.equals(FormField.FIELD_TYPE_DATE)) {

				inputComponent = dateChooser();
				((JDateChooser) inputComponent).addPropertyChangeListener(dateChooserPropertyChangeListener(
						(JDateChooser) inputComponent, elementId ));
				
			} else {
				inputComponent = textField(elementId);
				((JTextField) inputComponent).addKeyListener(crudTextFieldActionListener((JTextField) inputComponent, elementId) );
			}

			formComponents.add(ComponentBuilder.buildInlineComponent(150, lableName, inputComponent));

		}
		/*
		 * <c:forEach var="element" items="${entityProperty.elements}"> <tr
		 * valign="top"> <td><label>${element.lableName }</label></td> <td><c:choose>
		 * <c:when test="${  element.type == 'fixedlist'}"> <select
		 * class="input-field form-control" id="${element.id }"
		 * required="${element.required }" identity="${element.identity }"
		 * itemValueField="${element.optionValueName}"
		 * itemNameField="${element.optionItemName}">
		 * 
		 * </select> <script> window["valueField_${element.id}"] =
		 * "${element.optionValueName}"; window["itemField_${element.id}"] =
		 * "${element.optionItemName}"; let options = ${ element.jsonList }; for (let i
		 * = 0; i < options.length; i++) { let option = document
		 * .createElement("option"); let optionItem = options[i]; option.value =
		 * optionItem["${element.optionValueName}"]; option.innerHTML =
		 * optionItem["${element.optionItemName}"]; document.getElementById(
		 * "${element.id }") .append(option); } </script> </c:when> <c:when
		 * test="${  element.type == 'dynamiclist'}"> <input onkeyup="loadList(this)"
		 * name="${element.id }" id="input-${element.id }" class="form-control"
		 * type="text" /> <br /> <select style="width: 200px"
		 * class="input-field form-control" id="${element.id }"
		 * required="${element.required }" multiple="multiple"
		 * identity="${element.identity }" itemValueField="${element.optionValueName}"
		 * itemNameField="${element.optionItemName}"
		 * name=${element.entityReferenceClass} >
		 * 
		 * </select> <script> window["valueField_${element.id}"] =
		 * "${element.optionValueName}"; window["itemField_${element.id}"] =
		 * "${element.optionItemName}"; </script> </c:when> <c:when
		 * test="${  element.type == 'textarea'}"> <textarea
		 * class="input-field form-control" id="${element.id }" type="${element.type }"
		 * ${element.required?'required':'' } identity="${element.identity }">
		 * </textarea> </c:when> <c:when test="${  element.showDetail}"> <input
		 * detailfields="${element.detailFields}" showdetail="true" class="input-field"
		 * id="${element.id }" type="hidden" name="${element.optionItemName}"
		 * disabled="disabled" /> <button id="btn-detail-${element.id }"
		 * class="btn btn-info"
		 * onclick="showDetail('${element.id }','${element.optionItemName}' )">Detail</
		 * button> </c:when> <c:when
		 * test="${ element.type=='img' && element.multiple == false}"> <input
		 * class="input-field form-control" id="${element.id }" type="file"
		 * ${element.required?'required':'' } identity="${element.identity }" /> <button
		 * id="${element.id }-file-ok-btn" class="btn btn-primary btn-sm"
		 * onclick="addImagesData('${element.id}')">ok</button> <button
		 * id="${element.id }-file-cancel-btn" class="btn btn-warning btn-sm"
		 * onclick="cancelImagesData('${element.id}')">cancel</button> <div> <img
		 * id="${element.id }-display" width="50" height="50" /> </div> </c:when>
		 * <c:when test="${ element.type=='img' && element.multiple == true}"> <div
		 * id="${element.id }" name="input-list" class="input-field"> <div
		 * id="${element.id }-0-input-item" class="${element.id }-input-item"> <input
		 * class="input-file" id="${element.id }-0" type="file"
		 * ${element.required?'required':'' } identity="${element.identity }" /> <button
		 * id="${element.id }-0-file-ok-btn " class="btn btn-primary btn-sm"
		 * onclick="addImagesData('${element.id}-0')">ok</button> <button
		 * id="${element.id }-0-file-cancel-btn" class="btn btn-warning btn-sm"
		 * onclick="cancelImagesData('${element.id}-0')">cancel</button> <button
		 * id="${element.id }-0-remove-list" class="btn btn-danger btn-sm"
		 * onclick="removeImageList('${element.id }-0')">Remove</button> <div> <img
		 * id="${element.id }-0-display" width="50" height="50" /> </div> </div> </div>
		 * <button id="${element.id }-add-list"
		 * onclick="addImageList('${element.id }')">Add</button> </c:when> <c:when
		 * test="${ element.identity}"> <input class="input-field form-control"
		 * disabled="disabled" id="${element.id }" type="text"
		 * ${element.required?'required':'' } identity="${element.identity }" />
		 * </c:when> <c:otherwise> <input class="input-field form-control"
		 * id="${element.id }" type="${element.type }" ${element.required?'required':''
		 * } identity="${element.identity }" /> </c:otherwise> </c:choose></td> </tr>
		 * </c:forEach>
		 */

		JPanel formPanel = buildPanelV2(panelRequest, toArrayOfComponent(formComponents));
		return formPanel;
	}
	
	/**
	 * build dynamic combo box for CRUD form
	 * @param element
	 * @param elementId
	 * @param fieldType
	 * @return
	 */
	private JComboBox buildDynamicComboBox(EntityElement element, String elementId, Class<?> fieldType) {
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
	private JComboBox  buildFixedComboBox(EntityElement element, String elementId, Class fieldType) {
		String optionItemName = element.getOptionItemName(); 
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
	private void updateManagedObject(String elementId, Object selectedObjectFromList) {
		if(null == managedObject) {
			managedObject = new HashMap<>();
		}
		managedObject.put(elementId, selectedObjectFromList);
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

	private Component[] toArrayOfComponent(List<Component> formComponents) {

		Component[] components = new Component[formComponents.size()];
		for (int i = 0; i < formComponents.size(); i++) {
			components[i] = formComponents.get(i);
		}
		return components;
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
		
	}

	/**
	 * handle response when get filtered entities
	 * @param response
	 */
	public void handleGetFilteredEntities(ShopApiResponse response) {
		Log.log("Filtered Entities: ",response.getEntities());
		
	}

}
