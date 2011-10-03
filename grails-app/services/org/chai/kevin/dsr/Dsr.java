package org.chai.kevin.dsr;


public class Dsr {

	private String stringValue;
	private boolean applies;
	
	public Dsr(String stringValue, boolean applies) {
		this.stringValue = stringValue;
		this.applies = applies;
	}

	public String getStringValue() {
		return stringValue;
	}
	
	public boolean isApplies() {
		return applies;
	}
	
	
}
