package org.chai.kevin.survey;

import java.util.Comparator;

import org.chai.kevin.Sorter;

public class SurveySectionSorter implements Comparator<SurveySection> {
	@Override
	public int compare(SurveySection sectionOne, SurveySection sectionTwo) {

		if (sectionOne.getOrder() != null && sectionTwo.getOrder() != null) {
			if (sectionOne.getOrder() == sectionTwo.getOrder()) {
				return Sorter.compareOrder(sectionOne.getId(), sectionTwo.getId());
			} else {
				return Sorter.compareOrder(sectionOne.getOrder(),
						sectionTwo.getOrder());
			}
		} else if (sectionOne.getOrder() == null
				&& sectionTwo.getOrder() == null) {
			return Sorter.compareOrder(sectionOne.getId(), sectionTwo.getId());
		} else {
			return Sorter.compareOrder(sectionOne.getOrder(),
					sectionTwo.getOrder());
		}
	}

}
