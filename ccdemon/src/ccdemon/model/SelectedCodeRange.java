package ccdemon.model;

import mcidiff.util.ASTUtil;

import org.eclipse.jdt.core.dom.CompilationUnit;

public class SelectedCodeRange {
	private String fileName;
	private int startPosition;
	private int endPosition;
	
	/**
	 * @param fileName
	 * @param startPosition
	 * @param endPosition
	 */
	public SelectedCodeRange(String fileName, int startPosition, int endPosition) {
		super();
		this.fileName = fileName;
		this.startPosition = startPosition;
		this.endPosition = endPosition;
	}
	
	public int getStartLine(){
		CompilationUnit cu = ASTUtil.generateCompilationUnit(getFileName(), null);
		return cu.getLineNumber(getStartPosition());
		
		//return 166;
	}
	
	public int getEndLine(){
		CompilationUnit cu = ASTUtil.generateCompilationUnit(getFileName(), null);
		return cu.getLineNumber(getEndPosition());
				
		//return 171;
	}
	
	public int getPositionLength(){
		return getEndPosition()-getStartPosition();
	}
	
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	/**
	 * @return the startPosition
	 */
	public int getStartPosition() {
		return startPosition;
	}
	/**
	 * @param startPosition the startPosition to set
	 */
	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}
	/**
	 * @return the endPosition
	 */
	public int getEndPosition() {
		return endPosition;
	}
	/**
	 * @param endPosition the endPosition to set
	 */
	public void setEndPosition(int endPosition) {
		this.endPosition = endPosition;
	}
	
	
}
