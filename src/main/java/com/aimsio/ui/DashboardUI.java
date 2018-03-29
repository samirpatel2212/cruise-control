package com.aimsio.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.aimsio.service.SignalService;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.AxisType;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.ListSeries;
import com.vaadin.addon.charts.model.Series;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * This UI is the application entry point. A UI may either represent a browser
 * window (or tab) or some part of an HTML page where a Vaadin application is
 * embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is
 * intended to be overridden to add component to the user interface and
 * initialize non-component functionality.
 */
@Theme("valo")
@Title("Aimsio Assignment")
@Widgetset("com.aimsio.ChartsWidgetset")
@SpringUI
public class DashboardUI extends UI {

	private static final long serialVersionUID = 4268452735153976187L;

	private ComboBox<String> assetUNComboBox;

	private ComboBox<String> statusComboBox;

	private Chart timeline;

	@Autowired
	private SignalService signalService;

	@Override
	protected void init(VaadinRequest vaadinRequest) {

		timeline = new Chart(ChartType.COLUMN);
		VerticalLayout items = new VerticalLayout();

		List<String> assetsUNList = signalService.retrieveAllAssetUNs();

		assetUNComboBox = new ComboBox<>("AssetUN", assetsUNList);
		assetUNComboBox.addValueChangeListener(e -> handleAssetUNComboBox(e.getValue()));

		statusComboBox = new ComboBox<String>("Status", assetUNComboBox.getValue() == null ? new ArrayList<>()
				: signalService.retrieveStatusByAssetUN(assetUNComboBox.getValue()));
		statusComboBox.addValueChangeListener(e -> handleStatusComboBox(e.getValue()));

		Configuration configuration = timeline.getConfiguration();
		configuration.getTitle().setText("# of Signals over Time");
		configuration.getxAxis().setType(AxisType.DATETIME);
		configuration.getyAxis().setMin(0);
		configuration.getyAxis().setTitle("# of Signals");
		configuration.getLegend().setEnabled(true);

		HorizontalLayout filters = new HorizontalLayout(assetUNComboBox, statusComboBox);
		items.addComponent(filters);
		items.addComponent(timeline);
		items.setSizeFull();

		Responsive.makeResponsive(items);
		Responsive.makeResponsive(statusComboBox);
		Responsive.makeResponsive(assetUNComboBox);
		Responsive.makeResponsive(timeline);
		Responsive.makeResponsive(filters);

		items.setHeightUndefined();
		setContent(items);
	}

	/**
	 * Update chart based on user's selection.
	 * 
	 */
	protected void updateChart() {
		Configuration configuration = timeline.getConfiguration();
		if (!StringUtils.isEmpty(assetUNComboBox.getValue())) {
			if (!StringUtils.isEmpty(statusComboBox.getValue())) {
				populateChartWithAssetUNAndStatus(configuration);
			} else {
				populateChartWithGivenAssetUN(configuration);
			}
		} else {
			// If none of the combo is selected remove everything from chart
			removeListSeries();
		}
		timeline.drawChart(configuration);
	}

	/**
	 * Retrieve signal data for selected assetUN and status, and update the chart
	 * with new data.
	 * 
	 * @param configuration
	 */
	private void populateChartWithAssetUNAndStatus(Configuration configuration) {
		// If status and assetUN is selected on UI, fetch data from DB and populate
		// chart
		Map<String, Number> signalCountMap = signalService.getSignalsForGivenAssetUNAndStatus(statusComboBox.getValue(),
				assetUNComboBox.getValue());
		removeListSeries();
		configuration.addSeries(new ListSeries(statusComboBox.getValue(), signalCountMap.values()));

		// Add entry date as
		for (String key : signalCountMap.keySet()) {
			configuration.getxAxis().addCategory(key);
		}
	}

	/**
	 * Retrieve signal data from database for selected assetUN and update the chart
	 * with new data.
	 * 
	 * @param configuration
	 */
	private void populateChartWithGivenAssetUN(Configuration configuration) {
		// Load data for selected assetUN with all possible statuses
		Collection<String> retrieveStatusByAssetUN = signalService.retrieveStatusByAssetUN(assetUNComboBox.getValue());
		statusComboBox.setItems(assetUNComboBox.getValue() == null ? new ArrayList<>() : retrieveStatusByAssetUN);
		Map<String, Map<String, Number>> signalCountMap = signalService
				.getSignalsForGivenAssetUN(assetUNComboBox.getValue());
		removeListSeries();
		for (Entry<String, Map<String, Number>> entry : signalCountMap.entrySet()) {
			Map<String, Number> countMap = entry.getValue();
			configuration.addSeries(new ListSeries(entry.getKey(), countMap.values()));
			for (String key : countMap.keySet()) {
				configuration.getxAxis().addCategory(key);
			}
		}
	}

	private void handleStatusComboBox(String value) {
		// refresh chart with new data
		updateChart();
	}

	private void handleAssetUNComboBox(String value) {
		// remove the current list series
		removeListSeries();

		// clear status combo
		statusComboBox.clear();
		statusComboBox.setItems(new ArrayList<String>());

		// refresh chart with new data
		updateChart();
	}

	private void removeListSeries() {
		/// remove all list series from chart
		timeline.getConfiguration().setSeries(new ArrayList<Series>());
	}
}
