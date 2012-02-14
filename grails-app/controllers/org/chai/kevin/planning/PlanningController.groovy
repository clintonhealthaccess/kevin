package org.chai.kevin.planning

import java.util.Map;

import org.chai.kevin.AbstractController;
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.location.LocationEntity;
import org.chai.kevin.value.Value;
import org.hisp.dhis.period.Period;

class PlanningController extends AbstractController {
	
	def planningService
	
	def index = {
		redirect (action: 'planning', params: params)	
	}
	
	def view = {
		// this action redirects to the current survey if a SurveyUser logs in
		// or to a survey summary page if an admin logs in
		if (log.isDebugEnabled()) log.debug("planning.view, params:"+params)
		def user = getUser()

		if (user.hasProperty('dataLocation') && user.dataLocation != null) {
			Planning dataEntry = Planning.get(params.int('planning'))

			if (dataEntry == null) {
				dataEntry = planningService.getDefaultPlanning()
			}
			if (dataEntry == null) {
				log.info("no planning found - redirecting to 404")
				response.sendError(404)
			}
			else {
				redirect (controller:'planning', action: 'overview', params: [planning: planning?.id, location: user.dataLocation.id])
			}
		}
		else {
			redirect (controller: 'planning', action: 'summaryPage')
		}
	}
	
	def summaryPage = {
		
		def location = LocationEntity.get(params.int('location'))
		def planning = Planning.get(params.int('planning'))
		
		render (view: '/planning/summary/summaryPage', model: [
			plannings: Planning.list(),
			currentPlanning: planning,
			currentLocation: location
		])	
	}
	
	def editPlanningEntry = {	
		def planningType = PlanningType.get(params.int('planningType'))
		def location = DataLocationEntity.get(params.int('location'))
		def lineNumber = params.int('lineNumber')
		
		def planningList = planningService.getPlanningList(planningType, location)
		def newPlanningLine = planningList.getOrCreatePlanningEntry(lineNumber)
		
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
		
		def planningEntry = planningService.modify(planningType, location, lineNumber, params)
		def validatable = planningEntry.validatable
		
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
								errors: g.renderUserErrors(element: planningEntry, validatable: validatable, suffix: invalidPrefix, location: location)
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
	
//	def budgetUpdated = {
//		// TODO returns a json 'true' if the budget is updated
//		def planning = Planning.get(params.int('planning'))
//		def location = DataLocationEntity.get(params.int('location'))
//
//		// TODO
//		for (def planningType: planning.planningTypes) {
//			planningService.getPlanningList(planningType, location)
//		}
//	}
	
	def updateBudget = {
		// TODO waiting page that polls 'budgetUpdated' to see if the budget is updated
		def planning = Planning.get(params.int('planning'))
		def location = DataLocationEntity.get(params.int('location'))
		
		// TODO do this in the background
		for (def planningType: planning.planningTypes) {
			planningService.refreshBudget(planningType, location)
		}
		redirect (action: 'budget', params:[planning: planning.id, location: location.id] )
	}
	
	def budget = {
		def planning = Planning.get(params.int('planning'))
		def location = DataLocationEntity.get(params.int('location'))

		// waiting logic if some budget costs must be calculated
		// redirect to waiting page 'updatingBudget' if they must
		for (def planningType: planning.planningTypes) {
			if (!planningService.getPlanningList(planningType, location).isBudgetUpdated()) {
				redirect (action: 'updateBudget', params:[planning: planning.id, location: location.id])
				return;
			} 
		}

		def planningTypeBudgets = planning.planningTypes.collect {
			planningService.getPlanningTypeBudget(it, location)
		}
		
		render (view: '/planning/budget/budget', model: [
			planning: planning,
			location: location,
			planningTypeBudgets: planningTypeBudgets
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
