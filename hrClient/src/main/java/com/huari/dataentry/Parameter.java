package com.huari.dataentry;

import java.io.Serializable;

public class Parameter implements Serializable {

	public String name;//=
	public float maxValue;
	public float minValue;
	public String defaultValue;//=
	public String displayType;  // 显示范畴
	public byte isAdvanced;
	public byte isEditable;
	public String[] enumValues = null;

	public void setName(String name) {
		this.name = name;
	}

	public void setMaxValue(float maxValue) {
		this.maxValue = maxValue;
	}

	public void setMinValue(float minValue) {
		this.minValue = minValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setDisplayType(String displayType) {
		this.displayType = displayType;
	}

	public void setIsAdvanced(byte isAdvanced) {
		this.isAdvanced = isAdvanced;
	}

	public void setIsEditable(byte isEditable) {
		this.isEditable = isEditable;
	}

	public void setEnumValues(String[] enumValues) {
		this.enumValues = enumValues;
	}

	public String getName() {

		return name;
	}

	public float getMaxValue() {
		return maxValue;
	}

	public float getMinValue() {
		return minValue;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public String getDisplayType() {
		return displayType;
	}

	public byte getIsAdvanced() {
		return isAdvanced;
	}

	public byte getIsEditable() {
		return isEditable;
	}

	public String[] getEnumValues() {
		return enumValues;
	}
}
