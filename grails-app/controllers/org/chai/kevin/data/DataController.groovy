package org.chai.kevin.data

import org.chai.kevin.AbstractController;
import org.codehaus.groovy.grails.commons.ApplicationHolder;

class DataController extends AbstractController {
	
	def dataService
	
	def getDescription = {
		def data = dataService.getData(params.int('id'), Data.class)

		if (data == null) {
			render(contentType:"text/json") { result = 'error' }
		}
		else {
			render(contentType:"text/json") {
				result = 'success'
				html = g.render (template: '/entity/data/dataDescription', model: [data: data])
			}
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

}
