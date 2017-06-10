package ccdemon.model.rule;

/**
 * The index of {@code templateMatch} is the index of pattern-array, e.g., [add, *, Value], 
 * the value of {@code templateMatch} is the index of instance-array, e.g., [add, Model, Value]
 * 
 * @author linyun
 *
 */
public class TemplateMatch {
	private int[] templateMatch;
	private boolean matchable;
	
	public TemplateMatch(int size){
		this.templateMatch = new int[size];
		for(int i=0; i<templateMatch.length; i++){
			templateMatch[i] = -1;
		}
	}
	
	public void setValue(int index, int value){
		templateMatch[index] = value;
	}

	public int getValue(int index) {
		return templateMatch[index];
	}

	/**
	 * @return the matchable
	 */
	public boolean isMatchable() {
		return matchable;
	}

	/**
	 * @param matchable the matchable to set
	 */
	public void setMatchable(boolean matchable) {
		this.matchable = matchable;
	}
	
	public int getSize(){
		return this.templateMatch.length;
	}
}
