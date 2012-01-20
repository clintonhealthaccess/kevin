package org.chai.kevin.survey

import org.chai.kevin.survey.wizard.Wizard;

class PlanningController {

	
	def workflow = {
		def workflow = Wizard.get(params.int('workflow'))
		
		render(view: '/survey/workflow/workflow', model:[workflow: workflow])	
	}
	
	
}
