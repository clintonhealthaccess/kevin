package org.chai.kevin.dsr;

import java.util.Comparator;

public class DsrObjectiveSorter implements Comparator<DsrObjective> {

	@Override
	public int compare(DsrObjective objOne, DsrObjective objTwo) {

		if (objOne.getOrder() != null && objTwo.getOrder() != null)
			return this.compareOrder(objOne.getOrder(),
					objTwo.getOrder());		
		if (objOne.getOrder() == null && objTwo.getOrder() == null)
			return this.compareOrder(objOne.getOrder(),
					objTwo.getOrder());
		if (objOne.getOrder() != null && objTwo.getOrder() == null)
			return this.compareOrder(objOne.getOrder(),
					objTwo.getOrder());
		if (objOne.getOrder() == null && objTwo.getOrder() != null)
			return this.compareOrder(objOne.getOrder(),
					objTwo.getOrder());
		return 0;
	}
	
	private int compareOrder(Integer orderOne, Integer orderTwo) {
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
