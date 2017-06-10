package ccdemon.evaluation.model;

import mcidiff.model.TokenSeq;
import ccdemon.model.ConfigurationPoint;

public class CPWrapper {
	private ConfigurationPoint point;
	private TokenSeq correntValue;
	
	/**
	 * @param point
	 * @param correntValue
	 */
	public CPWrapper(ConfigurationPoint point, TokenSeq correntValue) {
		super();
		this.point = point;
		this.correntValue = correntValue;
	}
	/**
	 * @return the point
	 */
	public ConfigurationPoint getPoint() {
		return point;
	}
	/**
	 * @param point the point to set
	 */
	public void setPoint(ConfigurationPoint point) {
		this.point = point;
	}
	/**
	 * @return the correntValue
	 */
	public TokenSeq getCorrentValue() {
		return correntValue;
	}
	/**
	 * @param correntValue the correntValue to set
	 */
	public void setCorrentValue(TokenSeq correntValue) {
		this.correntValue = correntValue;
	}
	
	
}
