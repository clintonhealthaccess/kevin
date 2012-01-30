package org.chai.kevin.planning

import java.util.Map;

import org.chai.kevin.AbstractController;
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.value.Value;
import org.hisp.dhis.period.Period;

class PlanningController extends AbstractController {
	
	def activityService
	
	def editActivity = {		
		def activityType = ActivityType.get(params.int('activityType'))
		def location = DataLocationEntity.get(params.int('location'))
		def period = Period.get(params.int('period'))
		def lineNumber = params.int('lineNumber')
		
		def newActivity = activityService.getActivity(activityType, location, period, lineNumber)
		
		render (view: '/planning/newActivity', model: [
			activityType: activityType, 
			activity: newActivity,
			location: location,
			period: period
		])
	}

	def saveValue = {
		def activityType = ActivityType.get(params.int('activityType'))
		def location = DataLocationEntity.get(params.int('location'))
		def period = Period.get(params.int('period'))
		def lineNumber = params.int('lineNumber')
		
		activityService.modify(location, period, activityType, lineNumber, params)
		
		def activity = activityService.getActivity(activityType, location, period, lineNumber)
		def validatable = activity.validatable
		
		render(contentType:"text/json") {
			status = 'success'
			
			elements = array {
				elem (
					id: activityType.id,
					skipped: array {
						validatable.skippedPrefixes.each { prefix -> element prefix }
					},
					invalid: array {
						validatable.invalidPrefixes.each { invalidPrefix ->
							pre (
								prefix: invalidPrefix,
								valid: validatable.isValid(invalidPrefix),
								errors: g.renderUserErrors(element: activity, validatable: validatable, suffix: invalidPrefix, location: location)
							)
						}
					},
					nullPrefixes: array {
						validatable.nullPrefixes.each { prefix -> element prefix }
					}
				)
			}
		}
	}
		
}
