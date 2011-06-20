package org.chai.kevin.survey

import java.util.List;

class SurveySectionService {
	static transactional = true	
	List<SurveySection> getSurveySections() {
		return SurveySection.list();
	}
}
