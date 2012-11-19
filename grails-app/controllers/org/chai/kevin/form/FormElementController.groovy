package org.chai.kevin.form

import org.chai.location.DataLocation;
import org.chai.kevin.planning.PlanningType;
import org.chai.kevin.survey.Survey;
import org.chai.kevin.survey.SurveyElement;

class FormElementController {
	
	def formElementService
	
	def view = {
		// TODO make this method generic, it shouldn't contain any reference to SurveyElement
		
		def element = formElementService.getFormElement(params.int('id'))
		def location = DataLocation.get(params.int('location'))

		// TODO make this better the survey element should generate it's own link?
		// then we can make a PlanningElement that links to a planning form		
		def link = null
		if (element instanceof SurveyElement) {
			redirect (controller: "editSurvey", action: "sectionPage", params: [section: element.surveyQuestion.section.id, location: location.id], fragment: 'element-'+element.id)
		}
	}
	
	def getAjaxData = {
		def formElements = formElementService.searchFormElements(params['term'], params.list('include'), params);
		
		render(contentType:"text/json") {
			elements = array {
				formElements.each { formElement ->
					elem (
						key: formElement.id,
						value: formElement.label+'['+formElement.id+']'
					)
				}
			}
		}
	}
	
	def getHtmlData = {
		def formElements = formElementService.searchFormElements(params['searchText'], params.list('include'), params);
		
		render(contentType:"text/json") {
			result = 'success'
			html = g.render(template:'/entity/form/formElements', model:[formElements: formElements])
		}
	}
	
	def getDescription = {
		def formElement = formElementService.getFormElement(params.int('id'))

		if (formElement == null) {
			render(contentType:"text/json") { result = 'error' }
		}
		else {
			render(contentType:"text/json") {
				result = 'success'
				html = g.render (template: formElement.descriptionTemplate, model: [formElement: formElement])
			}
		}
	}
}
