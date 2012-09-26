package org.chai.kevin.data

import org.chai.kevin.AbstractController;
import org.chai.kevin.Period;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.task.CalculateTask;
import org.chai.kevin.task.Task.TaskStatus;
import org.chai.kevin.value.DataValue;
import org.chai.kevin.value.Status;
import org.chai.kevin.value.Value;
import org.codehaus.groovy.grails.commons.ApplicationHolder;

class DataController extends AbstractController {
	
	def taskService
	def dataService
	def valueService
	def refreshValueService
	
	def reportService
	def surveyService
	
	def getDescription = {
		def data = dataService.getData(params.int('id'), Data.class)

		if (data == null) {
			render(contentType:"text/json") { result = 'error' }
		}
		else {
			def periods = Period.list()
			
			def periodValues = [:]
			def valuesWithError = [:]
			
			periods.each { periodValues.put(it, valueService.getNumberOfValues(data, it)) }
			if (data instanceof NormalizedDataElement) periods.each { valuesWithError.put(it, valueService.getNumberOfValues(data, Status.ERROR, it)) }

			render(contentType:"text/json") {
				result = 'success'
				html = g.render (template: '/entity/data/dataDescription', model: [data: data, periodValues: periodValues, valuesWithError: valuesWithError])
			}
		}
	}
	
	def getExplainer = {
		def data = dataService.getData(params.int('id'), Data.class)

		if (data == null) {
			response.sendError(404)
		}
		else {
			def periods = Period.list([cache: true])
			def referencingData = dataService.getReferencingData(data)
			
			def surveyElementMap = [:]
			if (data instanceof RawDataElement) {
				def surveyElements = surveyService.getSurveyElements(data, null);
			
				surveyElements.each { surveyElement ->
					surveyElementMap.put(surveyElement, surveyService.getNumberOfApplicableDataLocationTypes(surveyElement));
				}
			}
			
			def reportTargets = reportService.getReportTargets(data)
			
			render (view: '/entity/data/explanation/explainData',  model: [
				dataElement: data,
				surveyElements: surveyElementMap,
				referencingData: referencingData,
				reportTargets: reportTargets
			])
		}
	}
	
	def getData = {
		def clazz = Class.forName('org.chai.kevin.data.'+params['class'], true, Thread.currentThread().contextClassLoader)
		def includeTypes = params.list('include')
		def dataList = dataService.searchData(clazz, params['searchText'], includeTypes, [:]);
		
		render(contentType:"text/json") {
			result = 'success'
			html = g.render(template:'/entity/data/dataList', model:[data: dataList])
		}
	}
	
	def getAjaxData = {
		def clazzes = []
		if (params['class'] != null) clazzes.add Class.forName('org.chai.kevin.data.'+params['class'], true, Thread.currentThread().contextClassLoader)
		if (params['classes'] != null) clazzes.addAll params.list('classes').collect {Class.forName('org.chai.kevin.data.'+it, true, Thread.currentThread().contextClassLoader)}
		def includeTypes = params.list('include')
		
		def dataList = []
		clazzes.each {dataList.addAll dataService.searchData(it, params['term'], includeTypes, [:])};
		
		render(contentType:"text/json") {
			elements = array {
				dataList.each { item ->
					elem (
						key: item.id,
						value: i18n(field:item.names)+' ['+item.code+'] ['+item.class.simpleName+']'
					)
				}
			}
		}
	}

	def deleteValues = {
		def data = dataService.getData(params.int('data'), Data.class)
		if (data == null) {
			response.sendError(404)
		}
		else {
			valueService.deleteValues(data, null, null)
			
			data.setLastValueChanged(new Date())
			dataService.save(data)
			
			refreshValueService.flushCaches()
			
			flash.message = message(code: 'data.values.deleted')
			redirect(uri: getTargetURI())
		}
	}
	
	def addReferencingDataTasks = {
		def data = dataService.getData(params.int('data'), Data.class)
		if (data == null) {
			response.sendError(404)
		}
		else {
			List<Data> referencesFor = [data]
			Set<Data> referencingData = new HashSet<Data>()
			
			while (!referencesFor.empty) {
				referencingData.addAll(referencesFor)
				
				def oldReferences = new ArrayList(referencesFor)
				referencesFor.clear()
				oldReferences.each { reference ->
					referencesFor.addAll(dataService.getReferencingData(reference))
				}
			}
			
			referencingData.each { reference ->
				if (!(reference instanceof RawDataElement)) {
					if (log.isDebugEnabled()) log.debug('adding task for data: '+reference)
					
					def task = new CalculateTask()
					task.dataId = reference.id
					
					task.status = TaskStatus.NEW
					task.user = currentUser
					task.added = new Date()
					
					// we check if the task is unique
					if (task.isUnique()) {
						// we save it
						task.save(failOnError: true)
						
						// we send it for processing
						taskService.sendToQueue(task)
					}
				}
			}
			
			flash.message = message(code: 'task.creation.success', args: [createLink(controller: 'task', action: 'list')])
			redirect(uri: getTargetURI())
		}
	}
	
	// TODO move to DataElementController
	def search = {
		adaptParamsForList()
		
		def data = dataService.getData(params.int('data'), DataElement.class)
		if (data == null) {
			response.sendError(404)
		}
		else {
			List<Period> periods = Period.list([cache: true])
			def period = Period.get(params.int('period'))
			if (period == null) period = Period.list()[Period.count() - 1]
			
			def values = valueService.searchDataValues(
				params.q,
				data,
				null,
				period,
				params
			)
			def valueCount = valueService.countDataValues(params.q, data, null, period)
			
			render (view: '/entity/list', model:[
				data: data,
				periods: periods,
				selectedPeriod: period,
				entities: values,
				entityCount: valueCount,
				template: "value/data"+data.class.simpleName+"List",
				code: 'datavalue.label',
				search: true
			])
		}
	}
	
	def dataValueList = {
		adaptParamsForList()
		
		def data = dataService.getData(params.int('data'), Data.class)
		if (data == null) {
			response.sendError(404)
		}
		else {
			List<Period> periods = Period.list([cache: true])
			def period = Period.get(params.int('period'))
			if (period == null) period = Period.list()[Period.count() - 1]
			
			def values = valueService.listDataValues(
				data,
				null,
				period,
				params
			)
			def valueCount = valueService.countDataValues(null, data, null, period)
			
			render (view: '/entity/list', model:[
				data: data,
				periods: periods,
				selectedPeriod: period,
				entities: values,
				entityCount: valueCount,
				template: "value/data"+data.class.simpleName+"List",
				code: 'datavalue.label',
				search: true
			])
		}
	}
	
}
