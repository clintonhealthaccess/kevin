package org.chai.kevin.survey;

import java.util.Comparator;
import org.chai.kevin.Sorter;

public class SurveyQuestionSorter implements Comparator<SurveyQuestion> {
	@Override
	public int compare(SurveyQuestion questionOne, SurveyQuestion questionTwo) {

		if (questionOne.getOrder() != null && questionTwo.getOrder() != null) {
			if (questionOne.getOrder() == questionTwo.getOrder()) {
				return Sorter.compareOrder(questionOne.getId(),
						questionTwo.getId());
			} else {
				return Sorter.compareOrder(questionOne.getOrder(),
						questionTwo.getOrder());
			}
		} else if (questionOne.getOrder() == null
				&& questionTwo.getOrder() == null) {
			return Sorter.compareOrder(questionOne.getId(), questionTwo.getId());
		} else {
			return Sorter.compareOrder(questionOne.getOrder(),
					questionTwo.getOrder());
		}
	}

}
