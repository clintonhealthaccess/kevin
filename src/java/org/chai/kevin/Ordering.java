package org.chai.kevin;

import javax.persistence.Embeddable;

@Embeddable
public class Ordering extends JSONMap<Integer> implements Comparable<Ordering> {

	private static final long serialVersionUID = 1179476928310670136L;

	public Ordering() {
		super();
	}

	public Ordering(JSONMap jsonMap) {
		super(jsonMap);
	}

	
	@Override
	public int compareTo(Ordering o) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
