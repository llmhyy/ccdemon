package ccdemon.model.rule;

import java.util.ArrayList;

public class EquivalentComponentGroup {
	private ArrayList<Component> components = new ArrayList<>();
	
	private String currentValue;
	
	@Override
	public String toString(){
		return this.components.toString();
	}
	
	/**
	 * @return the components
	 */
	public ArrayList<Component> getComponents() {
		return components;
	}

	/**
	 * @param components the components to set
	 */
	public void setComponents(ArrayList<Component> components) {
		this.components = components;
	} 
	
	public void addComponent(Component component){
		this.components.add(component);
	}

	/**
	 * @return the currentValue
	 */
	public String getCurrentValue() {
		return currentValue;
	}

	/**
	 * @param currentValue the currentValue to set
	 */
	public void setCurrentValue(String currentValue) {
		this.currentValue = currentValue;
	}
	
	
}
