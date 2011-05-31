package org.chai.kevin.dsr;

import java.util.Comparator;

public class DsrTargetSorter implements Comparator<DsrTarget> {
	@Override
	public int compare(DsrTarget targetOne, DsrTarget targetTwo) {

		if (targetOne.getCategory() != null && targetTwo.getCategory() != null)
			return this.compareOrder(targetOne.getCategory().getOrder(),
					targetTwo.getCategory().getOrder());
		if (targetOne.getCategory() != null && targetTwo.getCategory() == null)
			return this.compareOrder(targetOne.getCategory().getOrder(),
					targetTwo.getOrder());
		if (targetOne.getCategory() == null && targetTwo.getCategory() != null)
			return compareOrder(targetOne.getOrder(), targetTwo.getCategory()
					.getOrder());
		if (targetOne.getCategory() == null && targetTwo.getCategory() == null)
			return compareOrder(targetOne.getOrder(), targetTwo.getOrder());
		return 0;
	}

	private int compareOrder(Integer orderOne, Integer orderTwo) {
		if (orderOne != null && orderTwo != null)
			return orderOne - orderTwo;
		if (orderOne != null && orderTwo == null)
			return 1;
		if (orderOne == null && orderTwo != null)
			return -1;
		if (orderOne == null && orderTwo == null)
			return 0;

		return 0;

	}

}
