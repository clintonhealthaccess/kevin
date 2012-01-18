package org.chai.kevin.survey

import org.chai.kevin.survey.workflow.Workflow;

class PlanningController {

	
	def workflow = {
		def workflow = Workflow.get(params.int('workflow'))
		
		render(view: '/survey/workflow/workflow', model:[workflow: workflow])	
	}
	
	
}
