package org.chai.kevin.survey

class SurveyElementService {

	SurveyElement getSurveyElement(Long id) {
		return SurveyElement.get(id)
	}
	
}
