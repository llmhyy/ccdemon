package ccdemon.model.rule;

import java.util.ArrayList;

import ccdemon.model.ConfigurationPoint;

public class EquivalentComponentGroupList {
	private ArrayList<EquivalentComponentGroup> groupList = new ArrayList<>();

	public void addGroup(EquivalentComponentGroup group){
		this.groupList.add(group);
	}
	
	public EquivalentComponentGroup findEquivalentGroup(ConfigurationPoint point){
		for(EquivalentComponentGroup group: this.groupList){
			for(Component comp: group.getComponents()){
				if(point.equals(comp.getRuleItem().getConfigurationPoint())){
					return group;
				}
			}
		}
		
		return null;
	}
	
	public RuleItem findMatchingRuleItem(ConfigurationPoint point){
		for(EquivalentComponentGroup group: this.groupList){
			for(Component comp: group.getComponents()){
				if(point.equals(comp.getRuleItem().getConfigurationPoint())){
					return comp.getRuleItem();
				}
			}
		}
		
		return null;
	}
	
	/**
	 * @return the groupList
	 */
	public ArrayList<EquivalentComponentGroup> getGroupList() {
		return groupList;
	}

	/**
	 * @param groupList the groupList to set
	 */
	public void setGroupList(ArrayList<EquivalentComponentGroup> groupList) {
		this.groupList = groupList;
	}	
	
}
