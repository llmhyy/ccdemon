package ccdemon.model.rule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import mcidiff.util.DiffUtil;
import mcidiff.util.StringComparator;
import ccdemon.model.Candidate;
import ccdemon.model.ConfigurationPoint;
import ccdemon.util.CCDemonUtil;
import ccdemon.util.Settings;

public class NamingRule {
	
	private ArrayList<RuleItem> itemList = new ArrayList<>();
	private EquivalentComponentGroupList equivalentComponentGroupList = new EquivalentComponentGroupList();
	
	public void applyMethodReturnTypeRule(String candidateString){
		EquivalentComponentGroup group = new EquivalentComponentGroup();
		RuleItem ruleItem = findMethodReturnTypeItem();
		matchingCandidateStringToNamingPattern(candidateString, false, group, ruleItem);
		
		if(ruleItem != null){
			updateCandidatesByNewValue();			
		}
	}
	
	private RuleItem findMethodReturnTypeItem() {
		for(RuleItem item: itemList){
			if(item.isMethodReturnTypeItem()){
				return item;
			}
		}
		return null;
	}

	public void applyMethodNameRule(String candidateString){
		EquivalentComponentGroup group = new EquivalentComponentGroup();
		RuleItem ruleItem = findMethodRuleItem();
		matchingCandidateStringToNamingPattern(candidateString, false, group, ruleItem);
		
		if(ruleItem != null){
			updateCandidatesByNewValue();			
		}
	}
	
	private RuleItem findMethodRuleItem() {
		for(RuleItem item: itemList){
			if(item.isMethodNameItem()){
				return item;
			}
		}
		return null;
	}

	public void applyTypeNameRule(String candidateString){
		EquivalentComponentGroup group = new EquivalentComponentGroup();
		RuleItem ruleItem = findTypeRuleItem();
		matchingCandidateStringToNamingPattern(candidateString, false, group, ruleItem);
		
		if(ruleItem != null){
			updateCandidatesByNewValue();			
		}
	}
	
	
	private RuleItem findTypeRuleItem() {
		for(RuleItem item: itemList){
			if(item.isTypeItem()){
				return item;
			}
		}
		return null;
	}

	/**
	 * if a (new) candidate is determined in a configuration point, it will has an impact
	 * on the number of candidates in other configuration points. It may generate or remove 
	 * a new candidate on other configuration points.
	 * 
	 * @param candidateString
	 * @param configurationPoint
	 */
	public void applyRule(String candidateString, ConfigurationPoint currentPoint){
		EquivalentComponentGroup group = this.equivalentComponentGroupList.findEquivalentGroup(currentPoint);
		RuleItem ruleItem = this.equivalentComponentGroupList.findMatchingRuleItem(currentPoint);
		
		matchingCandidateStringToNamingPattern(candidateString, false, group, ruleItem);
		
		/*if(!currentPoint.getCopiedTokenSeq().isSingleToken()){
			return;
		}*/
		
		updateCandidatesByNewValue();
		
	}

	/**
	 * 
	 */
	private void updateCandidatesByNewValue() {
		for(RuleItem item: itemList){
			if(item.isChangeable() && item.getComponentList().size() > 0){
				boolean isValidForAdding = false;
				StringBuffer buffer = new StringBuffer();
				for(int i=0; i<item.getComponentList().size(); i++){
					Component comp = item.getComponentList().get(i);
					String currentValue = comp.getGroup().getCurrentValue();
					if(comp.isAbstract() && currentValue != null){
						//The first non-type component is usually in lower case.
						currentValue = parseStringToCamel(i, item, currentValue, null);
						buffer.append(currentValue);
						isValidForAdding = true;
					}
					else if(!comp.isAbstract()){
						if(currentValue != null && !comp.getAbstractName().toLowerCase().equals(currentValue.toLowerCase())){
							isValidForAdding = true;
						}
						currentValue = (currentValue != null) ? currentValue : comp.getAbstractName();
						currentValue = parseStringToCamel(i, item, currentValue, comp.getAbstractName());
						comp.getGroup().setCurrentValue(currentValue);
						buffer.append(currentValue);
					}
					else{
						//break;
						//isValidForAdding = false;
						buffer.append(comp.getOriginName());						
					}
				}
				
				if(isValidForAdding){
					String newValue = buffer.toString();
					newValue = parseStringToCamel(0, item, newValue, null);
					ConfigurationPoint point = item.getConfigurationPoint();
					//point.clearRuleGeneratedCandidates();
					/*if(!point.containsByIgnoringCase(newValue)){
					point.getCandidates().add(new Candidate(newValue, 0, Candidate.RULE, point));							
					}*/
					Candidate candidate = new Candidate(newValue, 0, Candidate.RULE, point);
					point.addCandidate(candidate);
				}
			}
		}
	}
	
	
	private String parseStringToCamel(int position, RuleItem item, String value, String referenceValue){
		String currentValue = value;
		
		if(currentValue.length() == 0){
			return currentValue;
		}
		if(referenceValue == null){
			if(position == 0 && 
					!(item.getConfigurationPoint().isType() || 
							item.getConfigurationPoint().isConstructor() || item.getConfigurationPoint().containsExpression())){
				char[] chars = currentValue.toCharArray();
				chars[0] = String.valueOf(chars[0]).toLowerCase().charAt(0);
				currentValue = String.valueOf(chars);
			}
			else if(position != 0 ){
				char[] chars = currentValue.toCharArray();
				chars[0] = String.valueOf(chars[0]).toUpperCase().charAt(0);
				currentValue = String.valueOf(chars);
			}
		}
		else{
			if(Character.isUpperCase(referenceValue.charAt(0))){
				char[] chars = currentValue.toCharArray();
				chars[0] = String.valueOf(chars[0]).toUpperCase().charAt(0);
				currentValue = String.valueOf(chars);
			}
			else{
				char[] chars = currentValue.toCharArray();
				chars[0] = String.valueOf(chars[0]).toLowerCase().charAt(0);
				currentValue = String.valueOf(chars);
			}
		}
		return currentValue;
	}
	
	/**
	 * match the candidate string to the naming pattern of its configuration
	 * @param candidateString
	 * @param currentPoint
	 */
	private void matchingCandidateStringToNamingPattern(String candidateString, boolean isSetOriginName,
			EquivalentComponentGroup group, RuleItem ruleItem){
		
		if(group != null && ruleItem != null){
			ArrayList<Component> components = ruleItem.getComponentList();
			String[] comps0 = DiffUtil.splitExpressionOrTokenWRTIdentifier(candidateString);
			String[] instanceArray = splitCamelStrings(comps0);
			//String[] instanceArray = DiffUtil.splitCamelString(candidateString);
			
			String[] patternArray = new String[components.size()];
			for(int i=0; i<components.size(); i++){
				patternArray[i] = components.get(i).getAbstractName();
			}
			
			TemplateMatch templateMatch = CCDemonUtil.matchPattern(patternArray, components, instanceArray);
			if(templateMatch.isMatchable()){
				/**
				 * find which component in the new candidate string are corresponded to the abstract component
				 */
				//matchEquivalence(isSetOriginName, components, instanceArray, templateMatch);
				//HashSet<Integer> matchedInstaceCursorSet = getMatchableInstanceCursor(templateMatch);
				String newComponentName = null;
				String preString = "";
				for(int i=0; i<components.size(); i++){
					Component preComp = components.get(i); 
					
					Integer instanceStartCursor = templateMatch.getValue(i);
					if(instanceStartCursor == -1){
						newComponentName = "";
					}
					else{
						int matchableIndex = findNextMatchableComponentIndex(templateMatch, i);
						if(matchableIndex != -1){
							Component postComp = components.get(matchableIndex);
							
							int instanceEndCursor = templateMatch.getValue(matchableIndex);
							
							StringBuffer buffer = new StringBuffer();
							for(int k=instanceStartCursor+1; k<=instanceEndCursor-1; k++){
								buffer.append(instanceArray[k]);
							}
							String extraString = buffer.toString();
							
							
							if(isMoreSim(extraString, preComp, postComp)){
								newComponentName = preString + instanceArray[instanceStartCursor] + extraString;
								preString = "";
							}
							else{
								newComponentName = preString + instanceArray[instanceStartCursor];
								preString = extraString;
							}
						}
						else{
							StringBuffer buffer = new StringBuffer();
							for(int k=instanceStartCursor; k<=instanceArray.length-1; k++){
								buffer.append(instanceArray[k]);
							}
							newComponentName = preString + buffer.toString();
						}
					}
					
					if(isSetOriginName){
						preComp.setOriginName(newComponentName);
					}
					else{
						preComp.getGroup().setCurrentValue(newComponentName);					
					}
				}
			}
		}
	}
	
	private boolean isMoreSim(String extraString, Component preComp,
			Component postComp) {
		if(!preComp.isAbstract() && postComp.isAbstract()){
			return false;
		}
		else{
			return true;
		}
	}

	private int findNextMatchableComponentIndex(TemplateMatch templateMatch,
			int startIndex) {
		for(int i=startIndex+1; i<templateMatch.getSize(); i++){
			int value = templateMatch.getValue(i);
			if(value != -1){
				return i;
			}
		}
		
		return -1;
	}

	/**
	 * find the cursor after instanceStartCursor and not in matchedInstanceCursorSet
	 * @param instanceStartCursor
	 * @param matchedInstaceCursorSet
	 * @return
	 */
	public int findEndCursor(Integer instanceStartCursor, int maxCursor,
			HashSet<Integer> matchedInstaceCursorSet) {
		int afterCursor = instanceStartCursor + 1;
		while(!matchedInstaceCursorSet.contains(afterCursor) && afterCursor <= maxCursor){
			afterCursor++;
		}
		return afterCursor - 1;
		
		
	}

	public HashSet<Integer> getMatchableInstanceCursor(TemplateMatch templateMatch){
		HashSet<Integer> set = new HashSet<>();
		for(int i=0; i<templateMatch.getSize(); i++){
			Integer index = templateMatch.getValue(i);
			if(index != -1){
				set.add(index);
			}
		}
		
		return set;
	}

	/**
	 * find which component in the new candidate string are corresponded to the abstract component
	 * 
	 * @param isSetOriginName
	 * @param components
	 * @param instanceArray
	 * @param templateMatch
	 */
	public void matchEquivalence(boolean isSetOriginName,
			ArrayList<Component> components, String[] instanceArray,
			TemplateMatch templateMatch) {
		for(int i=0; i<components.size(); i++){
			Component comp = components.get(i);
			if(comp.isAbstract()){
				int startTemplateCursor = i-1;
				int endTempalteCursor = i+1;
				
				int startInstanceCursor = 0;
				int endInstanceCursor = 0;
				
				if(startTemplateCursor < 0){
					startInstanceCursor = -1;
				}
				else{
					startInstanceCursor = templateMatch.getValue(startTemplateCursor);
					if(startInstanceCursor == -1){
						int index = templateMatch.getValue(i);
						if(index != -1){
							startInstanceCursor = index - 1;
						}
						else{
							startInstanceCursor = instanceArray.length;
						}
					} 
				}
				
				if(endTempalteCursor > components.size()-1){
					endInstanceCursor = instanceArray.length;
				}
				else{
					endInstanceCursor = templateMatch.getValue(endTempalteCursor);
					if(endInstanceCursor == -1){
						int index = templateMatch.getValue(i);
						if(index != -1){
							endInstanceCursor = index + 1;
						}
						else {
							endInstanceCursor = -1;
						}
					}
					//endInstanceCursor = (endInstanceCursor == -1)? instanceArray.length : endInstanceCursor;
				}
				
				StringBuffer buffer = new StringBuffer();
				for(int j=startInstanceCursor+1; j<=endInstanceCursor-1; j++){
					if(j != -1){
						buffer.append(instanceArray[j]);							
					}
				}
				String newComponentName = buffer.toString();
				if(isSetOriginName){
					comp.setOriginName(newComponentName);
				}
				else{
					comp.getGroup().setCurrentValue(newComponentName);					
				}
			}
			else{
				int index = templateMatch.getValue(i);
				if(index != -1){
					String newComponentName = instanceArray[index];
					if(isSetOriginName){
						comp.setOriginName(newComponentName);
					}
					else{
						comp.getGroup().setCurrentValue(newComponentName);					
					}			
				}
				else{
					if(isSetOriginName){
						comp.setOriginName("");
					}
					else{
						comp.getGroup().setCurrentValue("");					
					}
				}
			}
		}
	}
	
	/*private int findMatchingIndex(String[] instanceArray, int instanceStart, String compName) {
		for(int i=instanceStart; i<instanceArray.length; i++){
			if(instanceArray[i].toLowerCase().equals(compName.toLowerCase())){
				return i;
			}
		}
		return -1;
	}*/

	/**
	 * retrieve naming pattern from {@code itemList}
	 */
	public void parseNamingPattern(){
		for(RuleItem item: this.itemList){
			ArrayList<NameInstance> instanceList = item.getNameInstanceList();
			String[] names = new String[instanceList.size()];
			for(int i=0; i<instanceList.size(); i++){
				NameInstance nameInstance = instanceList.get(i);
				/*if(nameInstance.isSingleToken()){
					names[i] = nameInstance.getName();
				}
				else{
					names[i] = null;
				}*/
				names[i] = nameInstance.getName();
			}
			
			ArrayList<Component> components = parseComponents(names, item);
			item.setComponentList(components);
		}
		
		generateEquivantGroups(this.itemList);
		
		analyzeOriginNameForComponents(this.itemList);
	}
	
	private void analyzeOriginNameForComponents(ArrayList<RuleItem> itemList) {
		for(RuleItem item: itemList){
			if(item.isChangeable()){
				ConfigurationPoint point = item.getConfigurationPoint();
				EquivalentComponentGroup group = this.equivalentComponentGroupList.findEquivalentGroup(point);
				RuleItem ruleItem = this.equivalentComponentGroupList.findMatchingRuleItem(point);
				
				matchingCandidateStringToNamingPattern(point.getCurrentValue(), true, group, ruleItem);				
			}
		}
	}

	private void generateEquivantGroups(ArrayList<RuleItem> itemList) {
		ArrayList<Component> compList = new ArrayList<>();
		for(RuleItem item: itemList){
			compList.addAll(item.getComponentList());
		}
		
		while(compList.size() != 0){
			EquivalentComponentGroup group = new EquivalentComponentGroup();
			Component component = compList.get(0);
			
			group.addComponent(component);
			component.setGroup(group);
			compList.remove(0);
			//component containing non-identifier will not be equivalent to any other component.
			if(component.isIdentifierComponent()){
				Iterator<Component> iter = compList.iterator();
				while(iter.hasNext()){
					Component comp = iter.next();
										
					if(/*comp.equals(component)*/comp.compareSupportingNamesWith(component) 
							>= Settings.equivalentComponentThreshold){
						group.addComponent(comp);
						comp.setGroup(group);
						iter.remove();
					}
				}				
			}
			
			
			this.equivalentComponentGroupList.addGroup(group);
		}
		
	}

	@SuppressWarnings("unchecked")
	private ArrayList<Component> parseComponents(String[] names, RuleItem item) {
		ArrayList<Sequence> sequenceList = new ArrayList<>();
		ArrayList<ArrayList<String>> stringLists = new ArrayList<>();
		
		int nullCount = 0;
		for(int i=0; i<names.length; i++){
			String name = names[i];
			if(name != null && name.length() > 0){
				//String[] comps = DiffUtil.splitCamelString(name);
				String[] comps0 = DiffUtil.splitExpressionOrTokenWRTIdentifier(name);
				String[] comps = splitCamelStrings(comps0);
				ArrayList<String> list = new ArrayList<>();
				list.add("$");
				for(String comp: comps){
					list.add(comp);
				}
				list.add("$");
				stringLists.add(list);
				sequenceList.add(new Sequence(list));
			}
			else{
				sequenceList.add(null);
				nullCount++;
			}
		}
		//there is only one SimpleName candidate
		if(nullCount == names.length-1){
			return new ArrayList<Component>();
		}
		
		ArrayList<String>[] stringArray = stringLists.toArray(new ArrayList[0]);
		Object[] commonList = DiffUtil.generateCommonNodeListFromMultiSequence(stringArray, new StringComparator(true));
		
		ArrayList<Component> componentList = constructComponentListInDiffRange(item, names, sequenceList, commonList);
		
		return componentList;
	}

	/**
	 * @param item
	 * @param sequenceList
	 * @param commonList
	 * @return
	 */
	private ArrayList<Component> constructComponentListInDiffRange(RuleItem item, String[] names,
			ArrayList<Sequence> sequenceList, Object[] commonList) {
		ArrayList<Component> componentList = new ArrayList<>();
		for(int i=0; i<commonList.length-1; i++){
			int startIndex = i;
			int endIndex = i+1;
			
			String startString = (String) commonList[startIndex];
			String endString = (String) commonList[endIndex];
			
			if(i != 0 && i != commonList.length-1){
				String[] supportingNames = new String[sequenceList.size()];
				for(int j=0; j<supportingNames.length; j++){
					if(names[j] != null && names[j].length() > 0){
						sequenceList.get(j).moveStartIndex(startString);
						supportingNames[j] = sequenceList.get(j).getStartString();				
					}
				}
				Component component = new Component(item, supportingNames, false);
				componentList.add(component);
			}
			
			String[] supportingNames = new String[sequenceList.size()];
			
			for(int j=0; j<sequenceList.size(); j++){
				Sequence seq = sequenceList.get(j);
				if(seq != null){
					seq.moveStartIndex(startString);
					seq.moveEndIndex(endString);	
					String compString = seq.retrieveComponent();
					supportingNames[j] = compString;
					
					seq.setStartIndex(seq.getEndIndex());
				}
				else{
					supportingNames[j] = null;
				}				
			}
			if(isNotAllNull(supportingNames)){
				Component component = new Component(item, supportingNames, true);
				componentList.add(component);
			}
		}
		return componentList;
	}
	
	private String[] splitCamelStrings(String[] list){
		ArrayList<String> strList = new ArrayList<>();
		for(String str: list){
			if(DiffUtil.isJavaIdentifier(str)){
				String[] subStrList = DiffUtil.splitCamelString(str);
				for(String subStr: subStrList){
					strList.add(subStr);
				}
			}
			else{
				strList.add(str);
			}
		}
		
		return strList.toArray(new String[0]);
	}

	private boolean isNotAllNull(String[] supportingNames) {
		for(String supportingName: supportingNames){
			if(supportingName != null){
				return true;
			}
		}
		return false;
	}

	public void addItem(RuleItem item){
		this.itemList.add(item);
	}
	
	/**
	 * @return the itemList
	 */
	public ArrayList<RuleItem> getItemList() {
		return itemList;
	}
	/**
	 * @param itemList the itemList to set
	 */
	public void setItemList(ArrayList<RuleItem> itemList) {
		this.itemList = itemList;
	}
	
	/**
	 * @return the equivalentComponentGroupList
	 */
	public EquivalentComponentGroupList getEquivalentComponentGroupList() {
		return equivalentComponentGroupList;
	}

	/**
	 * @param equivalentComponentGroupList the equivalentComponentGroupList to set
	 */
	public void setEquivalentComponentGroupList(
			EquivalentComponentGroupList equivalentComponentGroupList) {
		this.equivalentComponentGroupList = equivalentComponentGroupList;
	}
}
