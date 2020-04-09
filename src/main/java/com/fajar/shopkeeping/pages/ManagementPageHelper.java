package com.fajar.shopkeeping.pages;

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
import javax.swing.SwingConstants;
import javax.swing.text.JTextComponent;

import com.fajar.annotation.FormField;
import com.fajar.entity.setting.EntityElement;
import com.fajar.shopkeeping.callbacks.MyCallback;
import com.fajar.shopkeeping.component.ComponentBuilder;
import com.fajar.shopkeeping.constant.UrlConstants;
import com.fajar.shopkeeping.handler.ManagementHandler;
import com.fajar.shopkeeping.util.ComponentUtil;
import com.fajar.shopkeeping.util.Log;
import com.fajar.shopkeeping.util.StringUtil;
import com.fajar.shopkeeping.util.ThreadUtil;
import com.toedter.calendar.JDateChooser;

public class ManagementPageHelper {
	
	private final ManagementPage page;
	
	public ManagementPageHelper(ManagementPage managementPage) {
		this.page = managementPage;
	}
	
	public  Component[] generateDataTableNavigationButtonsV2() {
		if(page.getSelectedLimit() == 0) {
			return new Component[] {};
		}
		
		/* DISPLAYED BUTTONS */
		 
		List<Component> navButtons = new ArrayList<Component>();
		List<Integer> displayed_buttons = new ArrayList<Integer> ();
		int buttonCount =  page.calculateTotalPage();
		int currentPage = page.getSelectedPage();
		int min = currentPage - 2;
		int max = currentPage + 2;
		for (int i = min; i <= max; i++) {
			displayed_buttons.add(i);
		}
		boolean firstSeparated = false;
		boolean lastSeparated = false;

		for (int i = 0; i < buttonCount; i++) {
			int buttonValue = i * 1 + 1;
			boolean included = false;
			for (int j = 0; j < displayed_buttons.size(); j++) {
				if ( displayed_buttons.get(j) == i && !included) {
					included = true;
				}
			}
			if (!lastSeparated && currentPage < i - 2
					&& (i * 1 + 1) == (buttonCount - 1)) {
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
			JButton button =  createNavButton(i+1, i, active);
			 
			navButtons.add(button);
		}

//		let nextPage = currentPage == buttonCount - 1 ? currentPage : currentPage + 1;
		
		return ComponentUtil.toArrayOfComponent(navButtons);
	}
	
	
	
	/**
	 * generate array of navigation buttons for data table
	 * @return
	 */
	public Component[] generateDataTableNavigationButtons() {
		
		if(page.getSelectedLimit() == 0) {
			return new Component[] {};
		}
		page.getLabelTotalData().setText("Total: "+page.getTotalData());
		
		int totalPage =  page.calculateTotalPage();
		
		Component[] navigationButtons = new Component[totalPage ];
		
		for (int i = 0; i < totalPage; i++) {
			
			boolean active = i == page.getSelectedPage();
			JButton button = createNavButton(i+1, i, active);
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
					JScrollPane scrollPane = (JScrollPane)((JPanel) page.getFieldComponent(key)).getComponent(0); 
					page.removeAllImageSelectionField(scrollPane);
				}catch (Exception e) {
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
	 * @param entity
	 */
	public void populateFormInputs(final Map entity) { 
		ThreadUtil.run(new Runnable() {
			
			@Override
			public void run() {
				doPopulateFormInputs(entity);
			}
		});
	}
	
	/**
	 * perform setting component values and setEditMode(true)
	 * @param entity
	 */
	public void doPopulateFormInputs(Map entity) {
		
		doClearForm();
		page.setManagedObject(entity);
		Set<String> objectKeys = page.managedObjectKeySet();
		
		for (String key : objectKeys) {
			Object value = page.getManagedObjectValue(key);
			EntityElement entityElement = page.getEntityElement(key);
			
			if(null == entityElement) {
				continue;
			}
			
			String elementType = entityElement.getType();
			
			if(value == null) {
				value = "";
			}
			boolean isImage = FormField.FIELD_TYPE_IMAGE.equals(elementType);
			Component formField = page.getFieldComponent(key);
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
						EntityElement element = page.getEntityElement(key);
						String optionItemName = element.getOptionItemName();
						((JComboBox) formField).setSelectedItem(valueMap.get(optionItemName));
					} catch (Exception e) { }
				
			} else if(isImage && entityElement.isMultiple() == false) {
				formField = page.getSingleImagePreviewLabel(key);
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
					JScrollPane scrollPane = (JScrollPane)((JPanel) page.getFieldComponent(key)).getComponent(0); 
					page.addNewImageSelectionField(entityElement, scrollPane);
					
					Icon icon = ComponentBuilder.imageIcon(imageUrl, 160, 160);

					page.setIconOnMultipleImagePreviewLabel(key, index ,icon);
					newValues[index] = "{ORIGINAL>>"+string+"}";
					
					index++;
				}
				
				page.updateManagedObject(key, String.join("~", newValues)); 
				
			} else {
				Log.log("key not managed: ",key);
			}
		}
		
		page.setEditMode(true);
		Log.log("::singleObjectPreviews:",page.getSingleImagePreviews());
		
		
	}
	
	/**
	 * =========================================
	 *             Input Fields
	 * =========================================            
	 */
	
	/**
	 * build dynamic combo box for CRUD form
	 * @param element
	 * @param elementId
	 * @param fieldType
	 * @return
	 */
	public JComboBox buildDynamicComboBox(EntityElement element, Class<?> fieldType) {
		
		String elementId = element.getId();
		String optionItemName = element.getOptionItemName();
		
		KeyListener comboBoxKeyListener = dynamicComboBoxListener(optionItemName, fieldType, elementId);
		ActionListener comboBoxActionListener = comboBoxOnSelectListener(optionItemName, fieldType, elementId);
		
		JComboBox inputComponent = ComponentBuilder.buildEditableComboBox("",comboBoxKeyListener, comboBoxActionListener, "type something.." );
		inputComponent.setSize(150, 20); 
		
		return inputComponent;
	}
	
	/**
	 * build fixed combo box for CRUD form
	 * @param element
	 * @param elementId
	 * @param fieldType
	 * @return
	 */
	public JComboBox buildFixedComboBox(EntityElement element,  Class fieldType) {
		
		String optionItemName = element.getOptionItemName(); 
		String elementId = element.getId();
		/**
		 * call API
		 */
		List<Map> objectList = page.getHandler().getAllEntity(fieldType);
		page.setComboBoxValuesContainer(elementId, objectList);

		Object[] comboBoxValues = ManagementPage.extractListOfSpecifiedField(objectList, optionItemName);
		Object defaultValue = objectList.get(0).get(optionItemName);
		
		ActionListener comboBoxActionListener  = comboBoxOnSelectListener(optionItemName, fieldType, elementId);
		
		JComboBox inputComponent = ComponentBuilder.buildComboBox(defaultValue , comboBoxActionListener, comboBoxValues);
		 
		return inputComponent;
	}
	
	
	/**
	 * build single image input form field
	 * @param element
	 * @param fieldType
	 * @param multiple
	 * @return
	 */
	public JPanel buildImageField(EntityElement element,  Class<?> fieldType, boolean multiple) {

		if(multiple) {
			JPanel imageSelectionField = ComponentBuilder.buildVerticallyInlineComponent(200, ComponentBuilder.infoLabel("click add..", SwingConstants.CENTER)); 
			JButton buttonAddImage = ComponentBuilder.button("add"); 
			JPanel imageSelectionWrapperPanel = ComponentBuilder.buildVerticallyInlineComponentScroll(190, 300, imageSelectionField, buttonAddImage) ; 
			
			buttonAddImage.addActionListener(buttonAddImageFieldListener( element, imageSelectionWrapperPanel));
			
			JPanel inputPanel = ComponentBuilder.buildVerticallyInlineComponent(200, imageSelectionWrapperPanel, buttonAddImage); 
			
			page.setFormInputComponent(element.getId(), imageSelectionWrapperPanel);
			
			return inputPanel;
			
		}else {
			JLabel imagePreview = page.createImagePreview();
			
			page.setSingleImageContainer(element.getId(), imagePreview);
			
			JButton buttonChoose = ComponentBuilder.button("choose file", 160, onChooseSingleImageFileClick(new JFileChooser(), element.getId())); 
			
			JButton buttonClear = ComponentBuilder.button("clear", 160, buttonClearSingleImageClick(element.getId()));  
			JPanel inputPanel = ComponentBuilder.buildVerticallyInlineComponent(205, buttonChoose, buttonClear, imagePreview) ; 
			
			return inputPanel;
		}
	}
	
	/**
	 * button edit on datatable row
	 * @param idFieldName2
	 * @param idValue2
	 * @return
	 */
	public JButton editButton(final String idFieldName2, final Object idValue2) {
		JButton button = ComponentBuilder.button("Edit");
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				page.getHandler().getSingleEntity(idFieldName2, idValue2);
				
			}
		});
		return button;
	}

	
	/**
	 * =========================================
	 *             Listeners
	 * =========================================            
	 */
	
	
	/**
	 * action when navigation button is clicked
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
	 * @param elementId
	 * @return
	 */
	public ActionListener buttonClearSingleImageClick(final String elementId) { 
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					//it means does not modify current value if in edit mode
					page.setIconOnSingleImagePreview(elementId, new ImageIcon());
					page.updateManagedObject(elementId, null);
				} catch (Exception e2) { }
				
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
				int returnVal = fileChooser.showOpenDialog(page.getParentPanel());

				if (returnVal == JFileChooser.APPROVE_OPTION) { 
//						Dialogs.showInfoDialog("FILE PATH:", file.getCanonicalPath()); 
					ThreadUtil.run(new Runnable() {
						
						@Override
						public void run() {
							
							try {
								final File file = fileChooser.getSelectedFile();
								JLabel imagePreview = (JLabel) page.getSingleImagePreviewLabel(elementId);
								
								Icon icon = ComponentBuilder.imageIconFromFile(file.getCanonicalPath(), imagePreview.getWidth(), imagePreview.getHeight());
								page.setIconOnSingleImagePreview(elementId, icon );
								
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
	 * @param inputComponent
	 * @param elementId
	 * @return
	 */
	public KeyListener crudTextFieldActionListener(final JTextField inputComponent, final String elementId) {
		 
		return new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) { }

			@Override
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
	 * @param inputComponent
	 * @param elementId
	 * @return
	 */
	public KeyListener textAreaActionListener(final JTextArea inputComponent, final String elementId) {
		 
		return new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) { }

			@Override
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
	 * @param inputComponent
	 * @param elementId
	 * @return
	 */
	public PropertyChangeListener dateChooserPropertyChangeListener(final JDateChooser inputComponent, final String elementId) {
		 
		return new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				Date selectedDate = inputComponent.getDate(); 
				String convertedDate = ManagementPage.SIMPLE_DATE_FORMAT.format(selectedDate);
				Log.log("selected date converted: ", convertedDate);
				
				page.updateManagedObject(elementId, convertedDate);
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
	public ActionListener comboBoxOnSelectListener(final String optionItemName, final Class<?> fieldType,
			final String elementId) {
		 
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				 final JComboBox inputComponent = (JComboBox) e.getSource();
				 Object selectedValue = inputComponent.getSelectedItem();
				 List<Map> rawList = page.getComboBoxValues(elementId);
				 Map selectedObjectFromList = ManagementHandler.getMapFromList(optionItemName, selectedValue, rawList);
				 
				 if(null == selectedObjectFromList) {
					 return ;
				 }
				 
				 page.updateManagedObject(elementId, selectedObjectFromList);
				 Log.log("managedObject: ",page.getManagedObject());
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
	public ActionListener removeImageSelectionListener(final EntityElement element,final int index, final JScrollPane imageSelectionScrollableWrapper) {
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) { 
				page.removeImageSelectionItem(element ,index, imageSelectionScrollableWrapper);
				
			}
		};
	}
	
	/**
	 * clear image in multiple image selection
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
	public ActionListener onChooseMultipleImageFileClick(final JFileChooser fileChooser,final  String elementId,final  int index) {
		  
			
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
								JLabel imagePreview =  page.getImagePreviewLabelForMultipleImages(elementId, index);
								Icon icon = ComponentBuilder.imageIconFromFile(file.getCanonicalPath(), imagePreview.getWidth(), imagePreview.getHeight());
								page.setIconOnMultipleImagePreviewLabel(elementId, index, icon );
								
								String base64 = StringUtil.getBase64Image(file); 
								Object currentValue =  page.getManagedObjectValue(elementId);
								Log.log("currentValue: ",currentValue);
								
								if(null == currentValue) {
									page.updateManagedObject(elementId, base64);
								}else {
									String[] rawValues = currentValue.toString().split("~");
									String finalValue =  currentValue.toString();
									if(rawValues.length >= index + 1) {
										rawValues[index] = base64;
										finalValue = String.join("~", rawValues);
									}else {
										finalValue +=( "~"+base64);
									}
									
									page.updateManagedObject(elementId, finalValue);
								}
								
								Log.log(elementId,":", page.getManagedObjectValue(elementId));
								 
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
	 * button add when clicked will perform adding new file chooser button
	 * @param element
	 * @param imageSelectionScrollableWrapper
	 * @return
	 */
	public ActionListener buttonAddImageFieldListener( final EntityElement element, final JPanel imageSelectionScrollableWrapper) {
		 
		return new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JScrollPane scrollableWrapper = (JScrollPane)imageSelectionScrollableWrapper.getComponent(0);  
				page.addNewImageSelectionField(element, scrollableWrapper);
			}
		};
	}
	
	/**
	 * when CRUD dynamic comboBox has changed
	 * @param dynamicComboBox
	 * @param optionItemName
	 * @param fieldType
	 * @return
	 */
	public KeyListener dynamicComboBoxListener(final String optionItemName, final Class<?> fieldType, final String elementId) {

		return new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent event) {
				
				final JComboBox dynamicComboBox = page.getComboBox(event);
				final String componentText = page.getComboBoxText(dynamicComboBox);
				page.getHandler().getEnitiesFormDynamicDropdown(fieldType, optionItemName, componentText, new MyCallback() {
					
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
						page.setComboBoxValuesContainer(elementId, entities);
						
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
	
	
	
	/**
	 * when filter field typed
	 * @param key
	 * @return
	 */
	public KeyListener filterFieldKeyListener(final String key) { 
		return new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) { }
			
			@Override
			public void keyReleased(KeyEvent e) {
				String value = ((JTextField) e.getComponent()).getText();
				
				page.putFilterValue(key, value);
				page.setCurrentElementIdFocus(key); 
				page.getHandler().getEntities();
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) { }
		};
	} 
}
