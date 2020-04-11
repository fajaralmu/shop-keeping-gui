package com.fajar.shopkeeping.component;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * used when constructing dynamic JPanel
 * @author Republic Of Gamers
 *
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PanelRow {

	private int row;
	private int height;
//	private int width;
	@Builder.Default
	private List<Component> components = new ArrayList<>();
}
