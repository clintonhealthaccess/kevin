package org.chai.kevin.planning

import org.chai.kevin.AbstractController;

class PlanningController extends AbstractController {
	
	
	def newActivity = {
		
		def activityType = ActivityType.get(params.int('activityType'))
		
		
		render (view: '/planning/newActivity', model: [activityType: activityType])
	}
	
}
