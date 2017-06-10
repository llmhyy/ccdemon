package ccdemon.model.rule;

public class NameInstance {
	private String name;
	private boolean isSingleToken;
	
	/**
	 * @param name
	 * @param b
	 */
	public NameInstance(String name, boolean isSingleToken) {
		super();
		this.name = name;
		this.isSingleToken = isSingleToken;
	}
	
	@Override
	public String toString(){
		return this.name;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the isSingleToken
	 */
	public boolean isSingleToken() {
		return isSingleToken;
	}
	/**
	 * @param isSingleToken the isSingleToken to set
	 */
	public void setSingleToken(boolean isSingleToken) {
		this.isSingleToken = isSingleToken;
	}
	
	
}
