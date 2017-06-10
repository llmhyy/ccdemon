package ccdemon.model.rule;

import java.util.ArrayList;

import ccdemon.model.ConfigurationPoint;

public class RuleItem {
	/**
	 * the length of {@code instanceList} should be same as the candidate number of
	 * {@code configurationPoint}
	 */
	private ConfigurationPoint configurationPoint;
	
	/**
	 * this list is used to represent the pattern such as <parse, *, Element>
	 */
	private ArrayList<Component> componentList;
	private ArrayList<NameInstance> nameInstanceList = new ArrayList<>();
	
	private boolean isMethodNameItem = false;
	private boolean isTypeItem = false;
	private boolean isMethodReturnTypeItem = false;
	
	public RuleItem(ConfigurationPoint configurationPoint){
		this.configurationPoint = configurationPoint;
	}
	
	public String toString(){
		return this.componentList.toString();
	}
	
	/**
	 * @return the nameInstanceList
	 */
	public ArrayList<NameInstance> getNameInstanceList() {
		return nameInstanceList;
	}



	public void addNameInstance(NameInstance name){
		this.nameInstanceList.add(name);
	}

	public boolean isChangeable(){
		return this.configurationPoint != null;
	}
	
	/**
	 * @return the configurationPoint
	 */
	public ConfigurationPoint getConfigurationPoint() {
		return configurationPoint;
	}

	/**
	 * @param configurationPoint the configurationPoint to set
	 */
	public void setConfigurationPoint(ConfigurationPoint configurationPoint) {
		this.configurationPoint = configurationPoint;
	}

	/**
	 * @return the componentList
	 */
	public ArrayList<Component> getComponentList() {
		return componentList;
	}

	/**
	 * @param componentList the componentList to set
	 */
	public void setComponentList(ArrayList<Component> componentList) {
		this.componentList = componentList;
	}

	

	/**
	 * @return the isMethodNameItem
	 */
	public boolean isMethodNameItem() {
		return isMethodNameItem;
	}

	/**
	 * @param isMethodNameItem the isMethodNameItem to set
	 */
	public void setMethodNameItem(boolean isMethodNameItem) {
		this.isMethodNameItem = isMethodNameItem;
	}
	
	/**
	 * @return the isMethodReturnTypeItem
	 */
	public boolean isMethodReturnTypeItem() {
		return isMethodReturnTypeItem;
	}

	/**
	 * @param isMethodReturnTypeItem the isMethodReturnTypeItem to set
	 */
	public void setMethodReturnTypeItem(boolean isMethodReturnTypeItem) {
		this.isMethodReturnTypeItem = isMethodReturnTypeItem;
	}

	/**
	 * @return the isTypeItem
	 */
	public boolean isTypeItem() {
		return isTypeItem;
	}

	/**
	 * @param isTypeItem the isTypeItem to set
	 */
	public void setTypeItem(boolean isTypeItem) {
		this.isTypeItem = isTypeItem;
	}
	
	
}
