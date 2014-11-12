package com.strategames.engine.utils;


public class ConfigurationItem {

	public interface OnConfigurationItemChangedListener {
		public void onConfigurationItemChanged(ConfigurationItem item);
	}
	
	private String name;
	private float valueNumeric;
	private String valueText;
	private boolean valueBoolean;
	private float minValue;
	private float maxValue;
	private float stepSize;
	private Type type;
	private OnConfigurationItemChangedListener listener;
	
	public ConfigurationItem(OnConfigurationItemChangedListener listener) {
		this.listener = listener;
	}
	
	public static enum Type {
		TEXT, NUMERIC, NUMERIC_RANGE, BOOLEAN
	}
	
	public void setMaxValue(float maxValue) {
		this.maxValue = maxValue;
	}
	
	public void setMinValue(float minValue) {
		this.minValue = minValue;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setStepSize(float stepSize) {
		this.stepSize = stepSize;
	}
	
	public void setValueNumeric(float valueNumeric) {
		this.valueNumeric = valueNumeric;
		this.listener.onConfigurationItemChanged(this);
	}
	
	public void setValueText(String valueText) {
		this.valueText = valueText;
		this.listener.onConfigurationItemChanged(this);
	}
	
	public void setValueBoolean(boolean valueBoolean) {
		this.valueBoolean = valueBoolean;
		this.listener.onConfigurationItemChanged(this);
	}
	
	public float getMaxValue() {
		return maxValue;
	}
	
	public float getMinValue() {
		return minValue;
	}
	
	public String getName() {
		return name;
	}
	
	public float getStepSize() {
		return stepSize;
	}
	
	public float getValueNumeric() {
		return valueNumeric;
	}
	
	public String getValueText() {
		return valueText;
	}
	
	public boolean getValueBoolean() {
		return valueBoolean;
	}
	
	public void setType(Type type) {
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}
}
