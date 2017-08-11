package com.adp.hr;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "adpscorecard")
@Component
public class HRScoreCardConfiguration {

	private List<TabMetaDataVO> tabs = new ArrayList<>();

	public List<TabMetaDataVO> getTabs() {
		return tabs;
	}

	public void setTabs(List<TabMetaDataVO> tabs) {
		this.tabs = tabs;
	}

	public static class TabMetaDataVO {

		private String name;
		private List<CellMetaDataVO> cells = new ArrayList<>();

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<CellMetaDataVO> getCells() {
			return cells;
		}

		public void setCells(List<CellMetaDataVO> cells) {
			this.cells = cells;
		}

	}

	public static class CellMetaDataVO {

		private String name;
		private String dataType;
		private List<String> values = new ArrayList<>();

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDataType() {
			return dataType;
		}

		public void setDataType(String dataType) {
			this.dataType = dataType;
		}

		public List<String> getValues() {
			return values;
		}

		public void setValues(List<String> values) {
			this.values = values;
		}

		public boolean isValidation() {
			return validation;
		}

		public void setValidation(boolean validation) {
			this.validation = validation;
		}

		private boolean validation;

	}

}
