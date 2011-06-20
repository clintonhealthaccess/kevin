package org.chai.kevin;

public class Sorter {
	public static int compareOrder(Integer orderOne, Integer orderTwo) {
		if (orderOne != null && orderTwo != null)
			return orderOne - orderTwo;
		if (orderOne != null && orderTwo == null)
			return -1;
		if (orderOne == null && orderTwo != null)
			return 1;
		if (orderOne == null && orderTwo == null)
			return 0;

		return 0;

	}
}
