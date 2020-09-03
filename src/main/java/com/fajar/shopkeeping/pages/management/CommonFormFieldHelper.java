package com.fajar.shopkeeping.pages.management;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import com.fajar.shopkeeping.callbacks.ApplicationException;
import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.component.builder.ComponentBuilder;
import com.fajar.shopkeeping.constant.UrlConstants;
import com.fajar.shopkeeping.handler.ManagementHandler;
import com.fajar.shopkeeping.pages.BasePage;
import com.fajar.shopkeeping.pages.ManagementPage;
import com.fajar.shopkeeping.util.ComponentUtil;
import com.fajar.shopkeeping.util.Log;
import com.fajar.shopkeeping.util.StringUtil;
import com.fajar.shopkeeping.util.ThreadUtil;
import com.fajar.shoppingmart.dto.FieldType;
import com.fajar.shoppingmart.entity.setting.EntityElement;
import com.toedter.calendar.JDateChooser;

public class CommonFormFieldHelper {

	private final ManagementPage page;

	public CommonFormFieldHelper(ManagementPage managementPage) {
		this.page = managementPage;
	}

	public Component[] generateDataTableNavigationButtonsV2() {
		if (page.getSelectedLimit() == 0) {
			return new Component[] {};
		}

		/* DISPLAYED BUTTONS */

		List<Component> navButtons = new ArrayList<Component>();
		List<Integer> displayed_buttons = new ArrayList<Integer>();
		int buttonCount = page.calculateTotalPage();
		int currentPage = page.getSelectedPage();
		int min = currentPage - 2;
		int max = currentPage + 2;
		for (int i = min; i <= max; i++) {
			displayed_buttons.add(i);
		}
		boolean firstSeparated = false;
		boolean lastSeparated = false;

		int prevPage = page.getSelectedPage() - 1;
		if (prevPage < 0) {
			prevPage = buttonCount;
		}

		JButton prevButton = createNavButton("prev", prevPage, false);
		navButtons.add(prevButton);

		for (int i = 0; i < buttonCount; i++) {
//			int buttonValue = i * 1 + 1;
			boolean included = false;
			for (int j = 0; j < displayed_buttons.size(); j++) {
				if (displayed_buttons.get(j) == i && !included) {
					included = true;
				}
			}
			if (!lastSeparated && currentPage < i - 2 && (i * 1 + 1) == (buttonCount - 1)) {
				// console.log("btn id",btn.id,"MAX",max,"LAST",(jumlahTombol-1));
				lastSeparated = true;

			}
			if (!included && i != 0 && !firstSeparated) {
				firstSeparated = true;

			}
			if (!included && i != 0 && i != (buttonCount - 1)) {
				continue;
			}

			boolean active = i == page.getSelectedPage();
			JButton button = createNavButton(i + 1, i, active);

			navButtons.add(button);
		}

		int nextPage = page.getSelectedPage() + 1;
		if (nextPage > buttonCount) {
			nextPage = 0;
		}

		JButton nextButton = createNavButton("next", nextPage, false);
		navButtons.add(nextButton);

		return ComponentUtil.toArrayOfComponent(navButtons);
	}

	/**
	 * generate array of navigation buttons for data table
	 * 
	 * @return
	 */
	public Component[] generateDataTableNavigationButtons() {

		if (page.getSelectedLimit() == 0) {
			return new Component[] {};
		}
		page.getLabelTotalData().setText("Total: " + page.getTotalData());

		int totalPage = page.calculateTotalPage();

		Component[] navigationButtons = new Component[totalPage];

		for (int i = 0; i < totalPage; i++) {

			boolean active = i == page.getSelectedPage();
			JButton button = createNavButton(i + 1, i, active);
//			ComponentBuilder.button(i+1, 50, 20); 
//			button.addActionListener(dataTableNavigationListener(i));
//			button.setBackground(i == page.getSelectedPage() ? Color.orange : Color.yellow);
//			
			navigationButtons[i] = button;
		}
		return navigationButtons;
	}

	private JButton createNavButton(Object text, int page, boolean active) {
		JButton button = ComponentBuilder.button(text, 70, 20);
		button.addActionListener(dataTableNavigationListener(page));
		button.setBackground(active ? Color.orange : Color.yellow);

		return button;
	}

	/**
	 * clear input fields or set it to default values
	 */
	public void doClearForm() {

		Map<String, Component> inputs = page.getFormInputFields();
		Set<String> inputKeys = inputs.keySet();
		for (String key : inputKeys) {

			Component formField = page.getFieldComponent(key); 
			
			try {
				//JTextField AND JTextArea
				if (formField instanceof JTextComponent) {
					((JTextComponent) formField).setText("");
				}
			} catch (Exception e) { } 

			if (formField instanceof JComboBox) {
				// leave as it
			}

			// multiple image
			if (formField instanceof JPanel) {
				try {
					JScrollPane scrollPane = (JScrollPane) ((JPanel) page.getFieldComponent(key)).getComponent(0);
					imageFormFieldHelper().removeAllImageSelectionField(scrollPane);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

		}

		Set<String> singleImageKeys = page.singleImagePreviewsKeySet();
		for (String elementId : singleImageKeys) {
			page.setIconOnSingleImagePreview(elementId, new ImageIcon());
		}
		Set<String> multipleImagePreviewsKeys = page.multipleImagePreviewsKeySet();
		for (String elementId : multipleImagePreviewsKeys) {
			page.clearMultipleImagePreviews(elementId);
		}

		page.setEditMode(false);

	}

	/**
	 * populate form fields by given entity
	 * 
	 * @param entity
	 */
	public void populateFormInputs(final Map<String, Object> entity) {
		ThreadUtil.run(new Runnable() {

			@Override
			public void run() {
				doPopulateFormInputs(entity);
			}
		});
	}

	private ImageFormFieldHelper imageFormFieldHelper() {
		return page.getImageHelper();
	}

	/**
	 * perform setting component values and setEditMode(true)
	 * 
	 * @param entity
	 */
	public void doPopulateFormInputs(Map<String, Object> entity) {

		doClearForm();
		page.setManagedObject(entity);
		Set<String> objectKeys = page.managedObjectKeySet();

		for (String key : objectKeys) {
			Object value = page.getManagedObjectValue(key);
			EntityElement entityElement = page.getEntityElement(key);

			if (null == entityElement) {
				continue;
			}

			String elementType = entityElement.getType();

			if (value == null) {
				value = "";
			}
			boolean isImage = FieldType.FIELD_TYPE_IMAGE.value.equals(elementType);
			Component formField = page.getFieldComponent(key);
			if (formField != null && !isImage) {

				try {
					//JTextField AND JTextArea
					if (formField instanceof JTextComponent) {
						((JTextComponent) formField).setText(value.toString());
					} 
									
					if (formField instanceof JComboBox) {
						Map valueMap = (Map) value;
						EntityElement element = page.getEntityElement(key);
						String optionItemName = element.getOptionItemName();
						((JComboBox) formField).setSelectedItem(valueMap.get(optionItemName));
					}
				} catch (Exception e) { }

			} else if (isImage && entityElement.isMultiple() == false) {
				formField = page.getSingleImagePreviewLabel(key);
				final String imageName = value.toString();
				final JLabel iconLabel = (JLabel) formField;

				ThreadUtil.run(new Runnable() {

					@Override
					public void run() {
						Icon imageIcon = ComponentBuilder.imageIcon(UrlConstants.URL_IMAGE + imageName, 160, 160);
						(iconLabel).setIcon(imageIcon);

					}
				});

			} else if (isImage && entityElement.isMultiple() == true) {
				String[] rawValues = value.toString().split("~");
				Log.log("rawValues.length:", entity);
				String[] newValues = new String[rawValues.length];
				int index = 0;

				for (String string : rawValues) {
					String imageUrl = UrlConstants.URL_IMAGE + string;
					JScrollPane scrollPane = (JScrollPane) ((JPanel) page.getFieldComponent(key)).getComponent(0);
					imageFormFieldHelper().addNewImageSelectionField(entityElement, scrollPane);

					Icon icon = ComponentBuilder.imageIcon(imageUrl, 160, 160);

					page.setIconOnMultipleImagePreviewLabel(key, index, icon);
					newValues[index] = "{ORIGINAL>>" + string + "}";

					index++;
				}

				page.updateManagedObject(key, String.join("~", newValues));

			} else {
				Log.log("key not managed: ", key);
			}
		}

		page.setEditMode(true);
		Log.log("::singleObjectPreviews:", page.getSingleImagePreviews());

	}

	/**
	 * ========================================= Input Fields
	 * =========================================
	 */

	/**
	 * build dynamic combo box for CRUD form
	 * 
	 * @param element
	 * @param elementId
	 * @param fieldType
	 * @return
	 */
	public JComboBox<?> buildDynamicComboBox(EntityElement element, Class<?> fieldType) {

		String elementId = element.getId();
		String optionItemName = element.getOptionItemName();

		KeyListener comboBoxKeyListener = dynamicComboBoxListener(optionItemName, fieldType, elementId);
		ActionListener comboBoxActionListener = comboBoxOnSelectListener(optionItemName, fieldType, elementId);

		JComboBox<?> inputComponent = ComponentBuilder.buildEditableComboBox("", comboBoxKeyListener,
				comboBoxActionListener, "type something..");
		inputComponent.setSize(150, 20);

		return inputComponent;
	}

	/**
	 * build fixed combo box for CRUD form
	 * 
	 * @param element
	 * @param elementId
	 * @param fieldType
	 * @return
	 */
	public JComboBox<?> buildFixedComboBox(EntityElement element, Class<?> fieldType) {

		String optionItemName = element.getOptionItemName();
		String elementId = element.getId();
		Object defaultComboBoxValue;
		Object[] comboBoxValues;
		
		/**
		 * call API
		 */
		List<Map<Object, Object>> objectList;
		Log.log("element.getDefaultValues(): ", element.getDefaultValues());
		
		checkIfElementIsEnum(element);
		
		boolean notHavingDefaultValue = element.getDefaultValues() == null || element.getDefaultValues().length == 0;
		
		if (notHavingDefaultValue ) {
			objectList = page.getHandler().getAllEntity(fieldType);
			comboBoxValues = ManagementPage.extractListOfSpecifiedField(objectList, optionItemName);
			defaultComboBoxValue = objectList.get(0).get(optionItemName);
			
		} else { 
			objectList = new ArrayList<>();
			String[] defaultComboBoxValues = element.getDefaultValues();
			for (final String defaultValue : defaultComboBoxValues) {
				
				objectList.add(new HashMap<Object, Object>() { 
					private static final long serialVersionUID = -7134182338709991777L;

					{
						put("key", defaultValue);
						put("value", defaultValue);
					}
				});
			}
			
			comboBoxValues = stringArrayToObject(element.getDefaultValues());
			defaultComboBoxValue = comboBoxValues[0]; 
		}

		page.setComboBoxValuesContainer(elementId, objectList);   

		ActionListener comboBoxActionListener = comboBoxOnSelectListener(optionItemName, fieldType, elementId);
		JComboBox<?> inputComponent = ComponentBuilder.buildComboBox(defaultComboBoxValue, comboBoxActionListener, comboBoxValues);

		return inputComponent;
	}

	private void checkIfElementIsEnum(EntityElement element) {
		 
		if(element.getField().getType().isEnum()) {
			Object[] enumConstants = element.getField().getType().getEnumConstants();
			element.setDefaultValues(StringUtil.toArrayOfString(Arrays.asList(enumConstants)));
		}
	}

	static Object[] stringArrayToObject(String... strings) {

		Object[] array = new Object[strings.length];
		for (int i = 0; i < strings.length; i++) {
			array[i] = strings[i];
		}
		return array;
	}

	/**
	 * button edit on data table row
	 * 
	 * @param idFieldName2
	 * @param idValue2
	 * @return
	 */
	public JButton editButton(final String idFieldName2, final Object idValue2) {
		JButton button = ComponentBuilder.editButton("Edit");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				page.getHandler().getSingleEntity(idFieldName2, idValue2);

			}
		});
		return button;
	}

	/**
	 * ========================================= Listeners
	 * =========================================
	 */

	/**
	 * action when navigation button is clicked
	 * 
	 * @param i
	 * @return
	 */
	public ActionListener dataTableNavigationListener(final int i) {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				page.setSelectedPage(i);
				page.getHandler().getEntities();
			}
		};
	}

	/**
	 * clear image in single image selection
	 * 
	 * @param elementId
	 * @return
	 */
	public ActionListener buttonClearSingleImageClick(final String elementId) {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					// it means does not modify current value if in edit mode
					page.setIconOnSingleImagePreview(elementId, new ImageIcon());
					page.updateManagedObject(elementId, null);
				} catch (Exception e2) {
				}

			}
		};
	}

	/**
	 * when fileChooser for image clicked
	 * 
	 * @param fileChooser
	 * @param imagePreview
	 * @param elementId
	 * @return
	 */
	public ActionListener onChooseSingleImageFileClick(final JFileChooser fileChooser, final String elementId) {

		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int returnVal = fileChooser.showOpenDialog(page.getParentPanel());

				if (returnVal == JFileChooser.APPROVE_OPTION) {
//						Dialogs.showInfoDialog("FILE PATH:", file.getCanonicalPath()); 
					ThreadUtil.run(new Runnable() {

						@Override
						public void run() {

							try {
								final File file = fileChooser.getSelectedFile();
								JLabel imagePreview = (JLabel) page.getSingleImagePreviewLabel(elementId);

								Icon icon = ComponentBuilder.imageIconFromFile(file.getCanonicalPath(),
										imagePreview.getWidth(), imagePreview.getHeight());
								page.setIconOnSingleImagePreview(elementId, icon);

								String base64 = StringUtil.getBase64Image(file);
								page.updateManagedObject(elementId, base64);
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
					});

				} else {
					System.out.println("Open command cancelled by user.");
				}

			}
		};
	}

	/**
	 * when CRUD jTextfield has changed
	 * 
	 * @param inputComponent
	 * @param elementId
	 * @return
	 */
	public KeyListener crudTextFieldActionListener(final JTextField inputComponent, final String elementId) {

		return new KeyListener() {
 
			public void keyTyped(KeyEvent e) { } 
			public void keyPressed(KeyEvent e) { }

			@Override
			public void keyReleased(KeyEvent e) {
				String value = inputComponent.getText();
				page.updateManagedObject(elementId, value);

			}
		};
	}

	/**
	 * when CRUD textArea has changed
	 * 
	 * @param inputComponent
	 * @param elementId
	 * @return
	 */
	public KeyListener textAreaActionListener(final JTextArea inputComponent, final String elementId) {

		return new KeyListener() {
 
			public void keyTyped(KeyEvent e) { } 
			public void keyPressed(KeyEvent e) { }

			@Override
			public void keyReleased(KeyEvent e) {
				String value = inputComponent.getText();
				page.updateManagedObject(elementId, value);

			}
		};
	}

	/**
	 * when JDateChooser changed
	 * 
	 * @param inputComponent
	 * @param elementId
	 * @return
	 */
	public PropertyChangeListener dateChooserPropertyChangeListener(final JDateChooser inputComponent,
			final String elementId) {

		return new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				Date selectedDate = inputComponent.getDate();
				String convertedDate = page.dateFormat.format(selectedDate);
				Log.log("selected date converted: ", convertedDate);

				page.updateManagedObject(elementId, convertedDate);
			}
		};
	}

	/**
	 * when CRUD combo box selected
	 * 
	 * @param inputComponent
	 * @param optionItemName
	 * @param fieldType
	 * @param elementId
	 * @return
	 */
	public ActionListener comboBoxOnSelectListener(final String optionItemName, final Class<?> fieldType,
			final String elementId) {

		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				final JComboBox inputComponent = (JComboBox) e.getSource();

				Object selectedValue = inputComponent.getSelectedItem();
				List<Map<Object, Object>> rawList = page.getComboBoxValues(elementId);
				Object selectedObjectFromList = null;

				boolean emptyOptionItemName = false;
				String mapKey = optionItemName;

				if ("".equals(optionItemName) || null == optionItemName) {
					mapKey = "key";
					emptyOptionItemName = true;
				}
				final Object rawObjectFromList = ManagementHandler.getMapFromList(mapKey, selectedValue, rawList);

				if (null == rawObjectFromList) {
					return;
				}

				if (emptyOptionItemName) {
					selectedObjectFromList = ((Map) rawObjectFromList).get("value");
				}else {
					selectedObjectFromList = rawObjectFromList;
				}

				page.updateManagedObject(elementId, selectedObjectFromList);
				Log.log("managedObject: ", page.getManagedObject());
			}

		};
	}

	/**
	 * 
	 * @param element
	 * @param index
	 * @param imageSelectionScrollableWrapper
	 * @return
	 */
	public ActionListener removeImageSelectionListener(final EntityElement element, final int index,
			final JScrollPane imageSelectionScrollableWrapper) {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				imageFormFieldHelper().removeImageSelectionItem(element, index, imageSelectionScrollableWrapper);

			}
		};
	}

	/**
	 * clear image in multiple image selection
	 * 
	 * @param id
	 * @param index
	 * @return
	 */
	public ActionListener buttonClearMultipleImageClick(final String elementId, final int index) {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					page.setIconOnMultipleImagePreviewLabel(elementId, index, new ImageIcon());
//					multipleImagePreviews.get(elementId).get(index).setIcon(new ImageIcon());
//					updateManagedObject(elementId, null);
				} catch (Exception e2) {
				}

			}
		};
	}

	/**
	 * action fired when file chooser for multiple image is clicked
	 * 
	 * @param jFileChooser
	 * @param id
	 * @param index
	 * @return
	 */
	public ActionListener onChooseMultipleImageFileClick(final JFileChooser fileChooser, final String elementId,
			final int index) {

		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int returnVal = fileChooser.showOpenDialog(page.getParentPanel());

				if (returnVal == JFileChooser.APPROVE_OPTION) {

					ThreadUtil.run(new Runnable() {

						@Override
						public void run() {
							File file = fileChooser.getSelectedFile();

							try {
								JLabel imagePreview = page.getImagePreviewLabelForMultipleImages(elementId, index);
								Icon icon = ComponentBuilder.imageIconFromFile(file.getCanonicalPath(),
										imagePreview.getWidth(), imagePreview.getHeight());
								
								page.setIconOnMultipleImagePreviewLabel(elementId, index, icon);

								String base64 = StringUtil.getBase64Image(file); 
								updateImageForObject(elementId, base64); 

							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}

						private void updateImageForObject(String elementId, String base64) {
							 
							Object currentValue = page.getManagedObjectValue(elementId); 
							Log.log("currentValue: ", currentValue);

							if (null == currentValue) {
								page.updateManagedObject(elementId, base64);
							} else {
								
								String finalValue = getBase64ImageFinalValue(currentValue, base64); 
								page.updateManagedObject(elementId, finalValue);
							}
							
						}

						private String getBase64ImageFinalValue(Object currentValue, String base64) {
							String[] rawValues = currentValue.toString().split("~");
							String finalValue = currentValue.toString();
							if (rawValues.length >= index + 1) {
								rawValues[index] = base64;
								finalValue = String.join("~", rawValues);
							} else {
								finalValue += ("~" + base64);
							}
							return finalValue;
						}
					});
				} else {
					System.out.println("Open command cancelled by user.");
				}
			}

		};
	}

	/**
	 * button add when clicked will perform adding new file chooser button
	 * 
	 * @param element
	 * @param imageSelectionScrollableWrapper
	 * @return
	 */
	public ActionListener buttonAddImageFieldListener(final EntityElement element,
			final JPanel imageSelectionScrollableWrapper) {

		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JScrollPane scrollableWrapper = (JScrollPane) imageSelectionScrollableWrapper.getComponent(0);
				imageFormFieldHelper().addNewImageSelectionField(element, scrollableWrapper);
			}
		};
	}

	/**
	 * when CRUD dynamic comboBox has changed
	 * 
	 * @param dynamicComboBox
	 * @param optionItemName
	 * @param fieldType
	 * @return
	 */
	public KeyListener dynamicComboBoxListener(final String optionItemName, final Class<?> fieldType,
			final String elementId) {

		return new KeyListener() { 
			public void keyPressed(KeyEvent e) { }
			public void keyTyped(KeyEvent e) { }

			@Override
			public void keyReleased(KeyEvent event) {

				final JComboBox dynamicComboBox = BasePage.getComboBox(event);
				final String comboBoxText = BasePage.getComboBoxText(dynamicComboBox);
				page.getHandler().getEnitiesFormDynamicDropdown(fieldType, optionItemName, comboBoxText,
						new MyCallback< Map<Object, Object>>() {

							@Override
							public void handle(Map<Object, Object> params) throws ApplicationException {
 
								populateDynamicDropdown(params);
							}

							/**
							 * populate items on comboBox
							 * 
							 * @param WebResponse
							 */
							private void populateDynamicDropdown( Map<Object, Object> WebResponse) {
								List entities = (List) WebResponse.get("entities");
								page.setComboBoxValuesContainer(elementId, entities);

								dynamicComboBox.removeAllItems();

								for (Object object : entities) {
									Map mapItem = (Map) object;
									dynamicComboBox.addItem(mapItem.get(optionItemName));
								}
								dynamicComboBox.setSelectedItem(comboBoxText);
								((JTextComponent) dynamicComboBox.getEditor().getEditorComponent())
										.setSelectionStart(comboBoxText.length());
								((JTextComponent) dynamicComboBox.getEditor().getEditorComponent())
										.setSelectionEnd(comboBoxText.length());
								dynamicComboBox.showPopup();
							}
						});

			}
 
		};
	}

	/**
	 * when filter field typed
	 * 
	 * @param key
	 * @return
	 */
	public KeyListener filterFieldKeyListener(final String key) {
		return new KeyListener() {
 
			public void keyTyped(KeyEvent e) { } 
			public void keyPressed(KeyEvent e) { } 
			
			public void keyReleased(KeyEvent e) {
				String value = ((JTextField) e.getComponent()).getText();

				page.putFilterValue(key, value);
				page.setCurrentElementIdFocus(key);
//				page.getHandler().getEntities();

			} 
		};
	}
}
