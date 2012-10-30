package org.chai.kevin;


public abstract class IntegerOrderable implements Comparable<IntegerOrderable> {

	public abstract Integer getOrder();
	
	public int compareTo(IntegerOrderable arg0) {
		if (getOrder() == null && arg0.getOrder() == null) return 0;
		if (getOrder() == null) return -1;
		else return getOrder().compareTo(arg0.getOrder());
	}
	
}
