package org.chai.kevin.dsr;

import java.util.Comparator;

import org.chai.kevin.Sorter;

public class DsrTargetSorter implements Comparator<DsrTarget> {
	@Override
	public int compare(DsrTarget targetOne, DsrTarget targetTwo) {

		if (targetOne.getCategory() != null && targetTwo.getCategory() != null)
			if (targetOne.getCategory() == targetTwo.getCategory()) {
				return Sorter.compareOrder(targetOne.getOrder(),
						targetTwo.getOrder());
			} else {
				return Sorter.compareOrder(targetOne.getCategory().getOrder(),
						targetTwo.getCategory().getOrder());
			}
		if (targetOne.getCategory() != null && targetTwo.getCategory() == null)
			return Sorter.compareOrder(targetOne.getCategory().getOrder(),
					targetTwo.getOrder());
		if (targetOne.getCategory() == null && targetTwo.getCategory() != null)
			return Sorter.compareOrder(targetOne.getOrder(), targetTwo
					.getCategory().getOrder());
		if (targetOne.getCategory() == null && targetTwo.getCategory() == null)
			return Sorter
					.compareOrder(targetOne.getOrder(), targetTwo.getOrder());
		return 0;
	}
}
