package org.chai.kevin.planning

import java.util.Map;

import org.chai.kevin.AbstractController;
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.value.Value;
import org.hisp.dhis.period.Period;

class PlanningController extends AbstractController {
	
	def planningService
	
	def editPlanningLine = {		
		def planningType = PlanningType.get(params.int('planningType'))
		def location = DataLocationEntity.get(params.int('location'))
		def period = Period.get(params.int('period'))
		def lineNumber = params.int('lineNumber')
		
		def newPlanningLine = planningService.getPlanningLine(planningType, location, period, lineNumber)
		
		render (view: '/planning/editPlanningLine', model: [
			planningType: planningType, 
			planningLine: newPlanningLine,
			location: location,
			period: period
		])
	}

	def saveValue = {
		def planningType = PlanningType.get(params.int('planningType'))
		def location = DataLocationEntity.get(params.int('location'))
		def period = Period.get(params.int('period'))
		def lineNumber = params.int('lineNumber')
		
		planningService.modify(location, period, planningType, lineNumber, params)
		
		def planningLine = planningService.getPlanningLine(planningType, location, period, lineNumber)
		def validatable = planningLine.validatable
		
		render(contentType:"text/json") {
			status = 'success'
			
			elements = array {
				elem (
					id: planningType.id,
					skipped: array {
						validatable.skippedPrefixes.each { prefix -> element prefix }
					},
					invalid: array {
						validatable.invalidPrefixes.each { invalidPrefix ->
							pre (
								prefix: invalidPrefix,
								valid: validatable.isValid(invalidPrefix),
								errors: g.renderUserErrors(element: planningLine, validatable: validatable, suffix: invalidPrefix, location: location)
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
	
	def budget = {
		def location = DataLocationEntity.get(params.int('location'))
		def period = Period.get(params.int('period'))
		
		def planningTypes = PlanningType.list()
		def budgetPlanningTypes = planningTypes.collect {
			planningService.getPlanningTypeBudget(it, location, period)
		}
		
		render (view: '/planning/budget', model: [
			budgetPlanningTypes: budgetPlanningTypes
		])
	}
		
}
