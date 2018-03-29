package com.aimsio.view;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.aimsio.repository.SignalService;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.AxisType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

@SpringComponent
@UIScope
public class ChartView extends VerticalLayout {

	private static final long serialVersionUID = -2291265884223056530L;
	
	
	
    @SuppressWarnings("serial")
	public ChartView() {}
    
	private void handleAssetUNComboBox(String selectedValue) {
		
	}
	
}
