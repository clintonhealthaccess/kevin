package org.chai.kevin.planning

import org.chai.kevin.AbstractController
import org.chai.kevin.Period
import org.chai.kevin.data.Type
import org.chai.location.DataLocation
import org.chai.location.Location
import org.chai.kevin.planning.PlanningCost.PlanningCostType
import org.chai.kevin.security.UserType;
import org.chai.kevin.table.AggregateLine
import org.chai.kevin.table.NumberLine
import org.chai.kevin.table.Table
import org.chai.kevin.value.Value

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
		def user = getCurrentUser()

		if (user.userType == UserType.PLANNING) {
			Planning planning = Planning.get(params.int('planning'))

			if (planning == null) {
				planning = planningService.getDefaultPlanning()
			}
			if (planning == null) {
				log.info("no planning found - redirecting to 404")
				response.sendError(404)
			}
			else {
				redirect (action: 'overview', params: [planning: planning?.id, location: user.location.id])
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
				emptyBudget: !planningLists.find {!it.empty},
				budgetTable: getBudgetTable(planningLists, location),
			])
		}
	}
	
	def getBudgetTable(def planningLists, def location) {
		def planningListLines = []
		planningLists.each { planningList ->
			def entryLines = []
			
			def i = 0
			planningList.planningEntryBudgetList.each { entry ->
				i++
				
				def costLines = getCostLines(entry, planningList)
				if (planningList.planningType.maxNumber == 1) entryLines.addAll(costLines)
				else {
					def header
					if (planningList.planningType.fixedHeader == null || planningList.planningType.fixedHeader.empty) {
						header = planningList.planningType.names + (planningList.planningType.maxNumber!=1?(' ' + i):'')
					} 
					else {
						header = languageService.getStringValue(entry.fixedHeaderValue, planningList.planningType.fixedHeaderType)
					}
					def line = new AggregateLine(header, costLines, ['budget-entry'])
					line.href = createLink(controller:'editPlanning', action:'editPlanningEntry', params:[location:location.id, planningType:planningList.planningType.id, lineNumber: entry.lineNumber])
					entryLines << line
				}	
			}
			def line = new AggregateLine(planningList.planningType.namesPlural, entryLines, ['standout'])
			line.openByDefault = true
			planningListLines << line
		}
		
		def columns = [
			message(code: 'planning.budget.table.incoming'), 
			message(code: 'planning.budget.table.outgoing'), 
			message(code: 'planning.budget.table.difference')
		]
		return new Table("", columns, planningListLines, true, ['budget'])
	}
	
	def getCostLines(def entry, def planningList) {
		def types = [Type.TYPE_NUMBER(), Type.TYPE_NUMBER(), Type.TYPE_NUMBER()]
		
		def costLines = []
		planningList.planningType.costs.each { cost ->
			def values = null
			def budgetCost = entry.getBudgetCost(cost)
			if (budgetCost != null && !budgetCost.hidden) {
				if (cost.type == PlanningCostType.INCOMING) values = [budgetCost.value, Value.VALUE_NUMBER(0), budgetCost.value]
				else values = [Value.VALUE_NUMBER(0), budgetCost.value, Value.VALUE_NUMBER(0-budgetCost.value.numberValue)]
			}
			else if (budgetCost == null) {
				if (cost.type == PlanningCostType.INCOMING) values = [null, Value.VALUE_NUMBER(0), Value.VALUE_NUMBER(0)]
				else values = [Value.VALUE_NUMBER(0), null, Value.VALUE_NUMBER(0)]
			}
			if (values != null) costLines << new NumberLine(cost.names, values, types)
		}
		return costLines
	}
	
	
	def output = {
		def planningOutput = PlanningOutput.get(params.int('planningOutput'))
		def location = DataLocation.get(params.int('location'))
		
		if (validate(planningOutput.planning, location)) {
			planningService.submitIfNeeded(planningOutput.planning, location)
			planningService.refreshOutputTableIfNeeded(planningOutput, location)
			
			def outputTable = planningService.getPlanningOutputTable(planningOutput, location)
			
			render (view: '/planning/output', model: [
				planningOutput: planningOutput,
				location: location,
				outputTable: getOutputTable(outputTable)
			])
		}
	}
	
	def getOutputTable(def outputTable) {
		def lines = []
		def rows = outputTable.getRows();
		for (int i = 0; i < rows.size(); i++) {
			def value = rows.get(i);
			
			def values = []
			def types =[]
			outputTable.planningOutput.columns.each { column ->
				values << outputTable.getValue(i, column);
				types << outputTable.getValueType(column);
			}
			lines << new NumberLine(languageService.getStringValue(value, outputTable.getHeaderType()), values, types);
		}
		def columns = outputTable.planningOutput.columns.collect {i18n(field: it.names)}
		return new Table(
			outputTable.planningOutput.captions,
			columns, lines, 
			outputTable.planningOutput.displayTotal, 
			['budget']
		);
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
