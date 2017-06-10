package ccdemon.model.rule;

import mcidiff.util.DiffUtil;
import ccdemon.util.CCDemonUtil;

public class PatternMatchingComponent {
	private String content;
	private double position;
	/**
	 * @param content
	 * @param position
	 */
	public PatternMatchingComponent(String content, double position) {
		super();
		this.content = content;
		this.position = position;
	}
	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}
	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}
	/**
	 * @return the position
	 */
	public double getPosition() {
		return position;
	}
	/**
	 * @param position the position to set
	 */
	public void setPosition(double position) {
		this.position = position;
	}
	
	public double computeSimilarity(PatternMatchingComponent patternComponent, Component instance){
		double contentWeight = 0.4;
		double positionWeight = 0.3;
		/**
		 * consider the similarity between pattern instances and current instance.
		 */
		double instanceWeight = 0.3;
		
		double contentValue = (patternComponent.getContent().toLowerCase().equals(getContent().toLowerCase())) ? 1 : 0;
		if(getContent().equals(Component.ABS_LITERAL) || patternComponent.equals(Component.ABS_LITERAL)){
			contentValue = 0.5;
		}
		
		double positionValue = 1 - Math.abs(patternComponent.getPosition()-getPosition());
		
		double instanceValue = 0;
		boolean f2 = DiffUtil.isJavaIdentifier(patternComponent.getContent());
		for(String ins: instance.getSupportingNames()){
			boolean f1 = DiffUtil.isJavaIdentifier(ins);
			if(f1 && f2){
				instanceValue++;
			}
		}
		instanceValue /= instance.getSupportingNames().length;
		
		return contentWeight*contentValue + positionWeight*positionValue + instanceWeight*instanceValue;
	}
}
