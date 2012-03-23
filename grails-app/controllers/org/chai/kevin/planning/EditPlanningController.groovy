package org.chai.kevin.planning

import java.util.Map;

import org.chai.kevin.AbstractController;
import org.chai.kevin.Period;
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.location.LocationEntity;
import org.chai.kevin.value.Value;

class EditPlanningController extends AbstractController {
	
	def planningService
	def languageService
	
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
				redirect (action: 'overview', params: [planning: dataEntry?.id, location: user.dataLocation.id])
			}
		}
		else {
			redirect (action: 'summaryPage')
		}
	}
	
	def summaryPage = {
		def location = LocationEntity.get(params.int('location'))
		def planning = Planning.get(params.int('planning'))
		
		def summaryPage = null
		if (location != null && planning != null) { 
			summaryPage = planningService.getSummaryPage(planning, location)
			summaryPage.sort(params.sort, params.order, languageService.currentLanguage)
		}
		
		render (view: '/planning/summary/summaryPage', model: [
			summaryPage: summaryPage,
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
		def newPlanningEntry = planningList.getOrCreatePlanningEntry(lineNumber)
		
		render (view: '/planning/editPlanningEntry', model: [
			planningType: planningType, 
			planningEntry: newPlanningEntry,
			location: location,
			targetURI: targetURI
		])
	}
	
	def editPlanningSection = {
		
		def planningType = PlanningType.get(params.int('planningType'))
		def location = DataLocationEntity.get(params.int('location'))
		def lineNumber = params.int('lineNumber')
		def section = params.section
		
		def planningList = planningService.getPlanningList(planningType, location)
		def planningEntry = planningList.planningEntries[lineNumber]
		
		render(contentType:"text/json") {
			status = 'success'
			html = g.render (template: '/planning/budget/planningSection', model:[
				planningType: planningType,
				planningEntry: planningEntry,
				location: location,
				section: section
			])
		}
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
		def lineNumberParam = params.int('lineNumber')
		
		def planningEntry = planningService.modify(planningType, location, lineNumberParam, params)
		def validatable = planningEntry.validatable
		
		render(contentType:"text/json") {
			status = 'success'
			id = planningType.id
			lineNumber = lineNumberParam
			complete = validatable.complete
			valid = !validatable.invalid
			budgetUpdated = planningEntry.budgetUpdated
			sections = array {
				planningType.sections.each { section ->
					sect (
						section: section,
						prefix: planningEntry.getPrefix(section),
						invalid: planningEntry.invalidSections.contains(section),
						complete: !planningEntry.incompleteSections.contains(section)
					)
				}
			}
			elements = array {
				elem (
					id: planningType.formElement.id,
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

	def submit = {
		def planningType = PlanningType.get(params.int('planningType'))
		def location = DataLocationEntity.get(params.int('location'))
		def lineNumber = params.int('lineNumber')
		
		planningService.submit(planningType, location, lineNumber)
		
		redirect (uri: targetURI)
	}
	
	def unsubmit = {
		def planningType = PlanningType.get(params.int('planningType'))
		def location = DataLocationEntity.get(params.int('location'))
		def lineNumber = params.int('lineNumber')
		
		planningService.unsubmit(planningType, location, lineNumber)
		
		redirect (uri: targetURI)
	}
	
	def updateBudget = {
		def planning = Planning.get(params.int('planning'))
		def planningType = PlanningType.get(params.int('planningType'))
		def location = DataLocationEntity.get(params.int('location'))
		
		if (planning != null) planning.planningTypes.each {
			planningService.refreshBudget(it, location)
		}
		else {
			planning = planningType.planning
			planningService.refreshBudget(planningType, location)
		}

		redirect (action: 'budget', params:[planning: planning.id, location: location.id] )

	}
	
	def budget = {
		def planning = Planning.get(params.int('planning'))
		def location = DataLocationEntity.get(params.int('location'))
		def planningLists = planning.planningTypes.collect {
			planningService.getPlanningList(it, location)
		}

		render (view: '/planning/budget/budget', model: [
			planning: planning,
			location: location,
//			updatedBudget: isBudgetUpdated(planning, location),
			planningLists: planningLists
		])
	}
	
//	def isBudgetUpdated(def planning, def location) {
//		for (def planningType : planning.planningTypes) {
//			if (!planningService.getPlanningList(planningType, location).isBudgetUpdated()) return false
//		}
//		return true
//	}
		
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
