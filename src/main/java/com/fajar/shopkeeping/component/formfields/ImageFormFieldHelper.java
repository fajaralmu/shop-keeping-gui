package com.fajar.shopkeeping.component.formfields;

import static com.fajar.shopkeeping.component.ComponentBuilder.buildInlineComponent;
import static com.fajar.shopkeeping.component.ComponentBuilder.buildVerticallyInlineComponent;
import static com.fajar.shopkeeping.component.ComponentBuilder.button;
import static com.fajar.shopkeeping.component.ComponentModifier.updateScrollPane;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import com.fajar.entity.setting.EntityElement;
import com.fajar.shopkeeping.component.ComponentBuilder;
import com.fajar.shopkeeping.component.ComponentModifier;
import com.fajar.shopkeeping.component.MyInfoLabel;
import com.fajar.shopkeeping.pages.ManagementPage;
import com.fajar.shopkeeping.util.Log;

import lombok.Data;

@Data
public class ImageFormFieldHelper {
	
	final ManagementPage page;
	public ImageFormFieldHelper(ManagementPage page) {
		this.page = page;
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
			
			buttonAddImage.addActionListener(formFieldHelper().buttonAddImageFieldListener( element, imageSelectionWrapperPanel));
			
			JPanel inputPanel = ComponentBuilder.buildVerticallyInlineComponent(200, imageSelectionWrapperPanel, buttonAddImage); 
			
			page.setFormInputComponent(element.getId(), imageSelectionWrapperPanel);
			
			return inputPanel;
			
		}else {
			JLabel imagePreview = createImagePreview();
			
			page.setSingleImageContainer(element.getId(), imagePreview);
			
			JButton buttonChoose = ComponentBuilder.button("choose file", 160, formFieldHelper().onChooseSingleImageFileClick(new JFileChooser(), element.getId())); 
			
			JButton buttonClear = ComponentBuilder.button("clear", 160, formFieldHelper().buttonClearSingleImageClick(element.getId()));  
			JPanel inputPanel = ComponentBuilder.buildVerticallyInlineComponent(205, buttonChoose, buttonClear, imagePreview) ; 
			
			return inputPanel;
		}
	}
	
	private CommonFormFieldHelper formFieldHelper() {
		return page.getHelper();
	}
	
	/**
	 * add image selection field in scrollable panel
	 * @param element
	 * @param imageSelectionScrollPane
	 */
	public void addNewImageSelectionField(EntityElement element, JScrollPane imageSelectionScrollPane ) {
		Log.log("addNewImageSelectionField");
		
		JPanel panel = (JPanel)  imageSelectionScrollPane.getViewport().getView(); 
		JPanel imageSelectionPanel = (JPanel) panel.getComponent(0); 
		
		if(imageSelectionPanel.getComponentCount() > 0 && imageSelectionPanel.getComponent(0) instanceof MyInfoLabel) {
			imageSelectionPanel.removeAll();
			Log.log("REMOVE ALL");
		}
		
		int index = imageSelectionPanel.getComponentCount();
		
		JLabel imagePreview = createImagePreview(); 
		
		JButton buttonChoose = button("choose file ("+index+")", 160, formFieldHelper().onChooseMultipleImageFileClick(new JFileChooser(), element.getId(), index));  
		JButton buttonClear = button("clear", 160, formFieldHelper().buttonClearMultipleImageClick(element.getId(), index)); 
		JButton buttonRemove = button("remove" , 160, formFieldHelper().removeImageSelectionListener(element, index, imageSelectionScrollPane));
		
		int componentCount = imageSelectionPanel.getComponentCount();
		
		JPanel newImageSelection = buildVerticallyInlineComponent(200, buttonChoose, buttonClear, buttonRemove, imagePreview) ;  
		newImageSelection.setBounds(newImageSelection.getX(), componentCount * newImageSelection.getHeight(), newImageSelection.getWidth(), newImageSelection.getHeight());
		
		Dimension newDimension = new Dimension(imageSelectionPanel.getWidth(),  imageSelectionPanel.getHeight() + newImageSelection.getHeight() );
		imageSelectionPanel.setSize(newDimension);
		imageSelectionPanel.add(newImageSelection);  
		JPanel wrapperPanel  = buildInlineComponent(imageSelectionPanel.getWidth() + 5, imageSelectionPanel);  
		
		updateScrollPane(imageSelectionScrollPane, wrapperPanel, newDimension);
		
		getPage().addMultipleImageContainer(element.getId(), imagePreview);
		
	} 
	
	
	/**
	 * remove value (in specified field & index) from managed object
	 * @param elementId
	 * @param index
	 */
	public void removeMultipleImageContainerItem(String elementId, int index) {
		// TODO multipleImagePreviews.get(id).remove(index);
		Log.log("removeMultipleImageContainerItem[",elementId,"] at", index);
		try {
			 Object currentObject = getPage().getManagedObjectValue(elementId);
			 String[] rawString = currentObject.toString().split("~");
			 Log.log( rawString);
			 
			 if(rawString.length >= index + 1) {
				 rawString[index] = "NULL";
			 }
			 
			 String newValue = String.join("~", rawString);
			 Log.log("new value: ",newValue);
			 getPage().updateManagedObject(elementId, newValue);
		}catch (Exception e) { 
		}
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
		
		JLabel removedInfo = ComponentBuilder.label("removed at:"+index);
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
	 * create label for image preview
	 * @return
	 */
	public static JLabel createImagePreview() {
		JLabel imagePreview = ComponentBuilder.label("No Preview."); 
		imagePreview.setSize(160, 160);
		imagePreview.setBorder(BorderFactory.createLineBorder(Color.BLACK)); 
		return imagePreview;
	}
}
