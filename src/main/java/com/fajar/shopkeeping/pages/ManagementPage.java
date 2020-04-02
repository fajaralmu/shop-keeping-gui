package com.fajar.shopkeeping.pages;

import java.awt.Color;
import java.awt.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.fajar.annotation.FormField;
import com.fajar.entity.Unit;
import com.fajar.entity.User;
import com.fajar.entity.setting.EntityElement;
import com.fajar.entity.setting.EntityProperty;
import com.fajar.shopkeeping.component.ComponentBuilder;
import com.fajar.shopkeeping.model.PanelRequest;
import com.fajar.shopkeeping.util.EntityUtil;
import com.fajar.shopkeeping.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;

@Data
public class ManagementPage extends BasePage { 
 
	private EntityProperty entityProperty ;

	private JPanel formPanel;
	
	private JButton buttonSubmit;
	private JButton buttonClear;
	
	private Map<String, List<Map>> fixedListContainer = new HashMap<>();
	
	public ManagementPage() {
		super("Management", BASE_WIDTH, BASE_HEIGHT);
	} 

	@Override
	public void initComponent() {
		 
		
		PanelRequest panelRequest = new PanelRequest(1, 670, 20, 15, Color.WHITE, 30, 30, 0, 0, false, true); 
		
		formPanel = generateEntityForm();
		buttonSubmit = button("Submit");
		buttonClear = button("Clear");
		
		mainPanel = ComponentBuilder.buildPanelV2(panelRequest,

				title("Management Page", 50) ,
				formPanel, ComponentBuilder.buildInlineComponent(100, buttonSubmit, buttonClear)
				 ); 

		parentPanel.add(mainPanel);
		exitOnClose();

	}

	@Override
	protected void initEvent() {
		super.initEvent(); 
		 
	}
	
	/**
	 * CRUD Form
	 * @return
	 */
	private JPanel generateEntityForm() {
		
		PanelRequest panelRequest = PanelRequest.autoPanelNonScroll(1, 300, 10, Color.yellow);
		
		List<Component> formComponents = new ArrayList<Component>();
		entityProperty = EntityUtil.createEntityProperty(Unit.class	, null);
		 
		List<EntityElement> entityElements = entityProperty.getElements();

		for (EntityElement element : entityElements) {
			final String elementId = element.getId();
			JLabel lableName = label(element.getLableName());
			
			String elementType = element.getType();
			if(elementType == null) {
				continue;
			}
			
			Component inputComponent = textField("Not Configured");

			if(elementType.equals(FormField.FIELD_TYPE_FIXED_LIST)) {
				
				String optionValueName = element.getOptionItemName();
				String optionItemName = element.getOptionItemName();
				String jsonListString = element.getJsonList();
				List<Map> objectList = convertToMapList(jsonListString);
				this.fixedListContainer.put(elementId, objectList);
				
				Object[] comboBoxValues = extractListOfSpecifiedField(objectList, optionItemName);
				inputComponent = ComponentBuilder.buildComboBox(objectList.get(0).get(optionItemName), comboBoxValues );
				
			}else if(element.isIdentity()) {
				inputComponent = textField("ID");
				inputComponent.setEnabled(false);
			}else if(elementType.equals(FormField.FIELD_TYPE_TEXTAREA)){
				inputComponent = textArea(elementId);
			}else {
				inputComponent = textField(elementId);
			}
			
			formComponents.add(ComponentBuilder.buildInlineComponent(100, lableName, inputComponent));
			
		}
		/*
		<c:forEach var="element" items="${entityProperty.elements}">
		<tr valign="top">
			<td><label>${element.lableName }</label></td>
			<td><c:choose>
					<c:when test="${  element.type == 'fixedlist'}">
						<select class="input-field form-control" id="${element.id }"
							required="${element.required }"
							identity="${element.identity }"
							itemValueField="${element.optionValueName}"
							itemNameField="${element.optionItemName}">

						</select>
						<script>
							window["valueField_${element.id}"] = "${element.optionValueName}";
							window["itemField_${element.id}"] = "${element.optionItemName}";
							let options = ${
								element.jsonList
							};
							for (let i = 0; i < options.length; i++) {
								let option = document
										.createElement("option");
								let optionItem = options[i];
								option.value = optionItem["${element.optionValueName}"];
								option.innerHTML = optionItem["${element.optionItemName}"];
								document.getElementById(
										"${element.id }")
										.append(option);
							}
						</script>
					</c:when>
					<c:when test="${  element.type == 'dynamiclist'}">
						<input onkeyup="loadList(this)" name="${element.id }"
							id="input-${element.id }" class="form-control" type="text" />
						<br />
						<select style="width: 200px" class="input-field form-control"
							id="${element.id }" required="${element.required }"
							multiple="multiple" identity="${element.identity }"
							itemValueField="${element.optionValueName}"
							itemNameField="${element.optionItemName}"
							name=${element.entityReferenceClass}
						>

						</select>
						<script>
							window["valueField_${element.id}"] = "${element.optionValueName}";
							window["itemField_${element.id}"] = "${element.optionItemName}";
						</script>
					</c:when>
					<c:when test="${  element.type == 'textarea'}">
						<textarea class="input-field form-control"
							id="${element.id }" type="${element.type }"
							${element.required?'required':'' }
							identity="${element.identity }">
				</textarea>
					</c:when>
					<c:when test="${  element.showDetail}">
						<input detailfields="${element.detailFields}"
							showdetail="true" class="input-field" id="${element.id }"
							type="hidden" name="${element.optionItemName}"
							disabled="disabled" />
						<button id="btn-detail-${element.id }" class="btn btn-info"
							onclick="showDetail('${element.id }','${element.optionItemName}' )">Detail</button>
					</c:when>
					<c:when
						test="${ element.type=='img' && element.multiple == false}">
						<input class="input-field form-control" id="${element.id }"
							type="file" ${element.required?'required':'' }
							identity="${element.identity }" />
						<button id="${element.id }-file-ok-btn"
							class="btn btn-primary btn-sm"
							onclick="addImagesData('${element.id}')">ok</button>
						<button id="${element.id }-file-cancel-btn"
							class="btn btn-warning btn-sm"
							onclick="cancelImagesData('${element.id}')">cancel</button>
						<div>
							<img id="${element.id }-display" width="50" height="50" />
						</div>
					</c:when>
					<c:when
						test="${ element.type=='img' && element.multiple == true}">
						<div id="${element.id }" name="input-list"
							class="input-field">
							<div id="${element.id }-0-input-item"
								class="${element.id }-input-item">
								<input class="input-file" id="${element.id }-0" type="file"
									${element.required?'required':'' }
									identity="${element.identity }" />
								<button id="${element.id }-0-file-ok-btn "
									class="btn btn-primary btn-sm"
									onclick="addImagesData('${element.id}-0')">ok</button>
								<button id="${element.id }-0-file-cancel-btn"
									class="btn btn-warning btn-sm"
									onclick="cancelImagesData('${element.id}-0')">cancel</button>
								<button id="${element.id }-0-remove-list"
									class="btn btn-danger btn-sm"
									onclick="removeImageList('${element.id }-0')">Remove</button>
								<div>
									<img id="${element.id }-0-display" width="50" height="50" />
								</div>
							</div>
						</div>
						<button id="${element.id }-add-list"
							onclick="addImageList('${element.id }')">Add</button>
					</c:when>
					<c:when test="${ element.identity}">
						<input class="input-field form-control" disabled="disabled"
							id="${element.id }" type="text"
							${element.required?'required':'' }
							identity="${element.identity }" />
					</c:when>
					<c:otherwise>
						<input class="input-field form-control" id="${element.id }"
							type="${element.type }" ${element.required?'required':'' }
							identity="${element.identity }" />
					</c:otherwise>
				</c:choose></td>
		</tr>
	</c:forEach>
	*/
		
		JPanel formPanel = buildPanelV2(panelRequest, toArrayOfComponent(formComponents));
		return formPanel;
	}

	private Component[] toArrayOfComponent(List<Component> formComponents) {
		
		Component[] components = new Component[formComponents.size()];
		for (int i = 0; i < formComponents.size(); i++) {
			components[i] = formComponents.get(i);
		}
		return components ;
	}

	private Object[] extractListOfSpecifiedField(List<Map> objectList, String optionItemName) {
		Object[] result = new Object[objectList.size()];
		for(int i = 0; i< objectList.size(); i++) {
			Map item = objectList.get(i);
			result[i] = item.get(optionItemName);
		}
		return result ;
	}

	private List<Map> convertToMapList(String jsonListString) {
		List<Map> result = new ArrayList<>();
		 
		try {
			List rawList = EntityUtil.OBJECT_MAPPER.readValue(jsonListString, List.class);
			for (Object object : rawList) {
				result.add((Map) object);
			}
		} catch (IOException e) {
			Log.log("ERROR parsing list");
			e.printStackTrace();
		}
		
		
		return result;
	}
	
	public static void mainX(String[] args) throws IOException {
		User.builder().displayName("FAJAR").build();
		ObjectMapper objectMapper = EntityUtil.OBJECT_MAPPER;
		List list = new ArrayList<User>() {
			{
				add(User.builder().displayName("FAJAR").build());
				add(User.builder().displayName("FAJAR 2").build());
				add(User.builder().displayName("FAJAR 1").build());
			}
		};
		String str = objectMapper.writeValueAsString(list);
		Log.log("STR:",str);
		List listr = objectMapper.readValue(str,List.class);
		Log.log("MAP:",listr);
	}

}
