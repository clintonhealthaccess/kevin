package org.chai.kevin.survey;

import java.util.Comparator;

import org.chai.kevin.Sorter;

public class SurveyTableColumnSorter implements Comparator<SurveyTableColumn> {

	@Override
	public int compare(SurveyTableColumn columnOne, SurveyTableColumn columnTwo) {
		if (columnOne.getOrder() != null && columnTwo.getOrder() != null) {
			if (columnOne.getOrder() == columnTwo.getOrder()) {
				return Sorter
						.compareOrder(columnOne.getId(), columnTwo.getId());
			} else {
				return Sorter.compareOrder(columnOne.getOrder(),
						columnTwo.getOrder());
			}

		} else if (columnOne.getOrder() == null && columnTwo.getOrder() == null) {
			return Sorter.compareOrder(columnOne.getId(), columnTwo.getId());
		} else {
			return Sorter.compareOrder(columnOne.getOrder(),
					columnTwo.getOrder());
		}
	}

}
