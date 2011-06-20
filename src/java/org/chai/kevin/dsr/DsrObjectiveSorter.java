package org.chai.kevin.dsr;

import java.util.Comparator;

import org.chai.kevin.Sorter;

public class DsrObjectiveSorter implements Comparator<DsrObjective> {
	@Override
	public int compare(DsrObjective objOne, DsrObjective objTwo) {
		if (objOne.getOrder() != null && objTwo.getOrder() != null) {
			if (objOne.getOrder() == objTwo.getOrder()) {
				return Sorter.compareOrder(objOne.getId(), objTwo.getId());
			} else {
				return Sorter.compareOrder(objOne.getOrder(), objTwo.getOrder());
			}
		} else if (objOne.getOrder() == null && objTwo.getOrder() == null) {
			return Sorter.compareOrder(objOne.getId(), objTwo.getId());
		} else {
			return Sorter.compareOrder(objOne.getOrder(), objTwo.getOrder());
		}
	}
}
