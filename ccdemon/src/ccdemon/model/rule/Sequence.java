package ccdemon.model.rule;

import java.util.ArrayList;

public class Sequence {
	private ArrayList<String> compList = new ArrayList<>();
	private int startIndex = 0;
	private int endIndex = 0;
	
	/**
	 * @param compList
	 */
	public Sequence(ArrayList<String> compList) {
		super();
		this.compList = compList;
	}
	
	public String getStartString(){
		return this.compList.get(startIndex);
	}
	
	public String getEndString(){
		return this.compList.get(endIndex);
	}
	
	public String toString(){
		return this.compList.toString();
	}
	
	/**
	 * @return the compList
	 */
	public ArrayList<String> getCompList() {
		return compList;
	}
	/**
	 * @param compList the compList to set
	 */
	public void setCompList(ArrayList<String> compList) {
		this.compList = compList;
	}
	/**
	 * @return the startIndex
	 */
	public int getStartIndex() {
		return startIndex;
	}
	/**
	 * @param startIndex the startIndex to set
	 */
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}
	/**
	 * @return the endIndex
	 */
	public int getEndIndex() {
		return endIndex;
	}
	/**
	 * @param endIndex the endIndex to set
	 */
	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}
	
	public void moveStartIndex(String startString) {
		for(int i=startIndex; i<compList.size(); i++ ){
			String str = compList.get(i);
			if(str.toLowerCase().equals(startString.toLowerCase())){
				startIndex = i;
				break;
			}
		}
	}
	
	public void moveEndIndex(String endString) {
		for(int i=startIndex+1; i<compList.size(); i++){
			String str = compList.get(i);
			if(str.toLowerCase().equals(endString.toLowerCase())){
				endIndex = i;
				break;
			}
		}
	}
	
	public String retrieveComponent() {
		StringBuffer buffer = new StringBuffer();
		for(int i=startIndex+1; i<endIndex; i++){
			buffer.append(compList.get(i));
		}
		
		String compString = buffer.toString();
		if(compString.length() == 0){
			return null;
		}
		else{
			return compString;
		}
	}
	
	
}
