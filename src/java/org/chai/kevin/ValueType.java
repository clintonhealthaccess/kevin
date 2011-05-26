package org.chai.kevin;

public enum ValueType {
	BOOL("BOOL"), ENUM("ENUM"), VALUE("VALUE"), DATE("DATE"), STRING("STRING");
	
	final String value;

	ValueType(String value) { this.value = value; }

    public String toString() { return value; } 
    String getKey() { return name(); }
}