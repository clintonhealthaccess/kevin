package org.chai.kevin.task

import org.chai.kevin.task.Task.TaskStatus;
import org.chai.kevin.data.Calculation;
import org.chai.kevin.data.Data;
import org.chai.kevin.data.NormalizedDataElement;
import org.springframework.context.i18n.LocaleContextHolder;

class CalculateTask extends Task {

	def dataService
	def refreshValueService
	def messageSource
	
	Integer dataId
	
	public CalculateTask() {
		super();
	}
	
	def executeTask() {
		def data = dataService.getData(dataId, Data.class)
		if (data != null) {
			if (data instanceof NormalizedDataElement) refreshValueService.refreshNormalizedDataElement(data, this)
			else if (data instanceof Calculation<CalculationPartialValue>) refreshValueService.refreshCalculation(data, this)
			refreshValueService.flushCaches()
		}
	}
	
	String getInformation() {
		def data = dataService.getData(dataId, Data.class)
		if (data == null) return messageSource.getMessage('task.calculate.data.notfound', new Object[0], LocaleContextHolder.getLocale())
		else return data.class.simpleName+': '+data.code
	}
	
	boolean isUnique() {
		def task = CalculateTask.findByDataId(dataId)
		return task == null || task.status == TaskStatus.COMPLETED || task.status == TaskStatus.ABORTED
	}
	
	def cleanTask() {
		// nothing to do here
	}
	
	String getOutputFilename() {
		return null
	}
	
	String getFormView() {
		return null
	}
	
	Map getFormModel() {
		return null
	}
	
	static constraints = {
		dataId(nullable: false)	
	}
	
}
