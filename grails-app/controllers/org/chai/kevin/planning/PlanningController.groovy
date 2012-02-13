package org.chai.kevin.planning

import java.util.Map;

import org.chai.kevin.AbstractController;
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.value.Value;
import org.hisp.dhis.period.Period;

class PlanningController extends AbstractController {
	
	def planningService
	
	def index = {
		redirect (action: 'planning', params: params)	
	}
	
	def view = {
		
		
	}
	
	def editPlanningEntry = {	
		def planningType = PlanningType.get(params.int('planningType'))
		def location = DataLocationEntity.get(params.int('location'))
		def lineNumber = params.int('lineNumber')
		
		def newPlanningLine = planningService.getPlanningEntry(planningType, location, lineNumber)
		
		render (view: '/planning/editPlanningEntry', model: [
			planningType: planningType, 
			planningLine: newPlanningLine,
			location: location,
			period: period
		])
	}
	
	def deletePlanningEntry = {
		def planningType = PlanningType.get(params.int('planningType'))
		def location = DataLocationEntity.get(params.int('location'))
		def lineNumber = params.int('lineNumber')
		
		planningService.deletePlanningEntry(planningType, location, lineNumber)
		
		redirect (uri: getTargetURI())
	}

	def saveValue = {
		def planningType = PlanningType.get(params.int('planningType'))
		def location = DataLocationEntity.get(params.int('location'))
		def lineNumber = params.int('lineNumber')
		
		planningService.modify(planningType, location, lineNumber, params)
		
		def planningLine = planningService.getPlanningEntry(planningType, location, lineNumber)
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
	
	def budgetUpdated = {
		// TODO returns a json 'true' if the budget is updated
		def planning = Planning.get(params.int('planning'))
		def location = DataLocationEntity.get(params.int('location'))

		for (def planningType: planning.planningTypes) {
			planningService.getPlanningList(planningType, location)
		}
	}
	
	def updatingBudget = {
		// TODO waiting page that polls 'budgetUpdated' to see if the budget is updated	
	}
	
	def budget = {
		def planning = Planning.get(params.int('planning'))
		def location = DataLocationEntity.get(params.int('location'))

		// waiting logic if some budget costs must be calculated
		// redirect to waiting page 'updatingBudget' if they must
		for (def planningType: planning.planningTypes) {
			if (!planningService.getPlanningList(planningType, location).isBudgetUpdated()) {
				redirect (action: 'updatingBudget', params:[planning: planning, location: location])
				return;
			} 
		}

		def budgetPlanningTypes = planning.planningTypes.collect {
			planningService.getPlanningTypeBudget(it, location, period)
		}
		
		render (view: '/planning/budget', model: [
			budgetPlanningTypes: budgetPlanningTypes
		])
	}
		
	def planningList = {
		def planningType = PlanningType.get(params.int('planningType'))
		def location = DataLocationEntity.get(params.int('location'))
		def sectionNumber = params.int('section')
		if (sectionNumber == null) sectionNumber = 0
		
		def planningList = planningService.getPlanningList(planningType, location)
		
		
		render (view: '/planning/planningList', model: [
			location: location,
			planning: planningList.planningType.planning,
			planningType: planningList.planningType,
			planningList: planningList,
			section: planningType.sections[sectionNumber]
		])
	}
	
	def overview = {
		def planning = Planning.get(params.int('planning'))
		def location = DataLocationEntity.get(params.int('location'))
		
		def planningLists = planning.planningTypes.collect {
			planningService.getPlanningList(it, location)
		}
		
		render (view: '/planning/overview', model: [
			location: location,
			planning: planning,
			planningLists: planningLists
		])
	}
	
}
