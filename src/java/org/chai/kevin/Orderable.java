package org.chai.kevin;

public abstract class Orderable implements Comparable<Orderable> {

	public abstract Integer getOrder();
	
	@Override
	public int compareTo(Orderable o) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
