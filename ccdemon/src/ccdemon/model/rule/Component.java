package ccdemon.model.rule;

import mcidiff.util.DiffUtil;

public class Component {
	public final static String ABS_LITERAL = "*";
	
	private RuleItem ruleItem;
	
	private String abstractName;
	private String[] supportingNames;
	
	/**
	 * it is the original text of this component in the pasted code
	 */
	private String originName;
	
	private boolean isAbstract;
	
	private EquivalentComponentGroup group;
	
	public Component(RuleItem item, String[] supportingNames, boolean isAbstract) {
		this.ruleItem = item;
		this.supportingNames = supportingNames;
		this.isAbstract = isAbstract;
		
		if(isAbstract){
			this.abstractName = Component.ABS_LITERAL;
		}
		else{
			for(int i=0; i<supportingNames.length; i++){
				if(supportingNames[i] != null){
					this.abstractName = supportingNames[i];
					break;
				}
			}
		}
	}
	
	@Override
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.abstractName);
		buffer.append("[");
		for(String str: this.supportingNames){
			buffer.append(str+" ");
		}
		buffer.append("]");
		return buffer.toString();
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof Component){
			Component thatComp = (Component)obj;
			if(thatComp.getSupportingNames().length != this.getSupportingNames().length){
				return false;
			}
			else{
				for(int i=0; i<this.getSupportingNames().length; i++){
					String thatString = thatComp.getSupportingNames()[i];
					String thisString = this.getSupportingNames()[i];
					if(thatString != null && thisString != null){
						if(!thatString.toLowerCase().equals(thisString.toLowerCase())){
							return false;
						}
					}
					else if((thatString != null && thisString == null) || 
							(thatString == null && thisString != null)){
						return false;
					}
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isIdentifierComponent(){
		for(String str: this.supportingNames){
			if(!DiffUtil.isJavaIdentifier(str)){
				return false;
			}
		}
		
		return true;
	}
	
	public double compareSupportingNamesWith(Component thatComp){
		if(thatComp.getSupportingNames().length != this.getSupportingNames().length){
			return 0;
		}
		else{
			double count = 0;
			for(int i=0; i<this.getSupportingNames().length; i++){
				String thatString = thatComp.getSupportingNames()[i];
				String thisString = this.getSupportingNames()[i];
				if(thatString != null && thisString != null){
					if(thatString.toLowerCase().equals(thisString.toLowerCase())){
						count++;
					}
				}
				else if(thatString == null && thisString == null){
					count++;
				}
			}
			
			return count/getSupportingNames().length;
		}
	}
	
	/**
	 * @return the ruleItem
	 */
	public RuleItem getRuleItem() {
		return ruleItem;
	}
	/**
	 * @param ruleItem the ruleItem to set
	 */
	public void setRuleItem(RuleItem ruleItem) {
		this.ruleItem = ruleItem;
	}
	/**
	 * @return the abstractName
	 */
	public String getAbstractName() {
		return abstractName;
	}
	/**
	 * @param abstractName the abstractName to set
	 */
	public void setAbstractName(String abstractName) {
		this.abstractName = abstractName;
	}
	/**
	 * @return the supportingNames
	 */
	public String[] getSupportingNames() {
		return supportingNames;
	}
	/**
	 * @param supportingNames the supportingNames to set
	 */
	public void setSupportingNames(String[] supportingNames) {
		this.supportingNames = supportingNames;
	}
	/**
	 * @return the isAbstract
	 */
	public boolean isAbstract() {
		return isAbstract;
	}
	/**
	 * @param isAbstract the isAbstract to set
	 */
	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	/**
	 * @return the group
	 */
	public EquivalentComponentGroup getGroup() {
		return group;
	}

	/**
	 * @param group the group to set
	 */
	public void setGroup(EquivalentComponentGroup group) {
		this.group = group;
	}

	/**
	 * @return the originName
	 */
	public String getOriginName() {
		return originName;
	}

	/**
	 * @param originName the originName to set
	 */
	public void setOriginName(String originName) {
		this.originName = originName;
	}
	
}
