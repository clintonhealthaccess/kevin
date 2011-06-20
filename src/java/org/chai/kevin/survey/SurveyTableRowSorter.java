package org.chai.kevin.survey;

import java.util.Comparator;
import org.chai.kevin.Sorter;

public class SurveyTableRowSorter implements Comparator<SurveyTableRow> {
	@Override
	public int compare(SurveyTableRow rowOne, SurveyTableRow rowTwo) {
		if (rowOne.getOrder() != null && rowTwo.getOrder() != null) {
			if (rowOne.getOrder() == rowTwo.getOrder()) {
				return Sorter.compareOrder(rowOne.getId(), rowTwo.getId());
			} else {
				return Sorter
						.compareOrder(rowOne.getOrder(), rowTwo.getOrder());
			}
		} else if (rowOne.getOrder() == null && rowTwo.getOrder() == null) {
			return Sorter.compareOrder(rowOne.getId(), rowTwo.getId());
		} else {
			return Sorter.compareOrder(rowOne.getOrder(), rowTwo.getOrder());
		}
	}

}
