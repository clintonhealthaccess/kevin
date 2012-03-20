package org.chai.kevin.form

import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.planning.PlanningType;
import org.chai.kevin.survey.SurveyElement;

class FormElementController {

	def formElementService
	
	def view = {
		def element = formElementService.getFormElement(params.int('id'))
		def location = DataLocationEntity.get(params.int('location'))

		// TODO make this better the survey element should generate it's own link?
		// then we can make a PlanningElement that links to a planning form		
		def link = null
		if (element instanceof SurveyElement) {
			redirect (controller: "editSurvey", action: "sectionPage", params: [section: element.surveyQuestion.section.id, location: location.id], fragment: 'element-'+element.id)
		}
	}
	
}
