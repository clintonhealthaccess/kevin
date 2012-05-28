package org.chai.kevin.planning

import java.util.Map;

import org.chai.kevin.AbstractController;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.Location;
import org.chai.kevin.Period;
import org.chai.kevin.value.Value;

class EditPlanningController extends AbstractController {
	
	def planningService
	def languageService
	
	def index = {
		redirect (action: 'planning', params: params)	
	}
	
	def view = {
		// this action redirects to the current survey if a DataUser logs in
		// or to a survey summary page if an admin logs in
		if (log.isDebugEnabled()) log.debug("planning.view, params:"+params)
		def user = getUser()

		if (user.hasProperty('dataLocation') && user.dataLocation != null) {
			Planning planning = Planning.get(params.int('planning'))

			if (planning == null) {
				planning = planningService.getDefaultPlanning()
			}
			if (planning == null) {
				log.info("no planning found - redirecting to 404")
				response.sendError(404)
			}
			else {
				redirect (action: 'overview', params: [planning: planning?.id, location: user.dataLocation.id])
			}
		}
		else {
			redirect (action: 'summaryPage')
		}
	}
	
	def summaryPage = {
		def location = Location.get(params.int('location'))
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
		def location = DataLocation.get(params.int('location'))
		
		if (validate(planningType.planning, location)) {
			def lineNumber = params.int('lineNumber')
			def planningEntry = planningService.getOrCreatePlanningEntry(planningType, location, lineNumber)
			
			render (view: '/planning/editPlanningEntry', model: [
				planningType: planningType, 
				planningEntry: planningEntry,
				location: location,
				targetURI: targetURI
			])
		}
	}
	
	def deletePlanningEntry = {
		def planningType = PlanningType.get(params.int('planningType'))
		def location = DataLocation.get(params.int('location'))
		
		if (validate(planningType.planning, location)) {
			def lineNumber = params.int('lineNumber')
			
			planningService.deletePlanningEntry(planningType, location, lineNumber)
			
			redirect (uri: getTargetURI())
		}
	}
	
	def save = {
		def planningType = PlanningType.get(params.int('planningType'))
		def location = DataLocation.get(params.int('location'))
		def lineNumberParam = params.int('lineNumber')
		
		def planningEntry = planningService.modify(planningType, location, lineNumberParam, params)
		def validatable = planningEntry.validatable
		
		if (planningEntry.invalidSections.empty) {
			planningService.submitIfNeeded(planningType.planning, location)
			
			redirect(uri: targetURI)
		}
		else {
			flash.message = message(code: 'planning.new.save.invalid')
			render (view: '/planning/editPlanningEntry', model: [
				planningType: planningType,
				planningEntry: planningEntry,
				location: location,
				targetURI: targetURI
			])
		}
	}

	def saveValue = {
		def planningType = PlanningType.get(params.int('planningType'))
		def location = DataLocation.get(params.int('location'))
		def lineNumberParam = params.int('lineNumber')
		
		def planningEntry = planningService.modify(planningType, location, lineNumberParam, params)
		def validatable = planningEntry.validatable
		
		render(contentType:"text/json") {
			status = 'success'
			id = planningType.id
			lineNumber = lineNumberParam
			complete = validatable.complete
			valid = !validatable.invalid
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
								errors: g.renderUserErrors(element: planningType.formElement, validatable: validatable, suffix: invalidPrefix, location: location)

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
		def planning = Planning.get(params.int('planning'))
		def location = DataLocation.get(params.int('location'))

		if (validate(planning, location)) {
			planningService.submitIfNeeded(planning, location)
			planningService.refreshBudgetIfNeeded(planning, location)
			
			def planningLists = planning.planningTypes.collect {
				planningService.getPlanningList(it, location)
			}
	
			render (view: '/planning/budget/budget', model: [
				planning: planning,
				location: location,
				budgetNeedsUpdate: false,
				planningLists: planningLists,
				incoming: planningLists.sum {it.incoming},
				outgoing: planningLists.sum {it.outgoing},
				difference: planningLists.sum {it.difference}
			])
		}
	}
	
	def planningList = {
		def planningType = PlanningType.get(params.int('planningType'))
		def location = DataLocation.get(params.int('location'))
		
		if (validate(planningType.planning, location)) {
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
	}
	
	def overview = {
		def planning = Planning.get(params.int('planning'))
		def location = DataLocation.get(params.int('location'))

		if (validate(planning, location)) {		
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
	
	def validate(def planning, def location) {
		if (location == null || planning == null || !planning.typeCodes.contains(location.type.code)) {
			response.sendError(404)
			return false
		}
		return true
	}
}
