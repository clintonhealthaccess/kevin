package org.chai.kevin.task

import org.chai.kevin.task.Task.TaskStatus;
import org.chai.kevin.data.Calculation;
import org.chai.kevin.data.Data;
import org.chai.kevin.data.NormalizedDataElement;

class CalculateTask extends Task {

	def dataService
	def refreshValueService
	
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
	
	boolean isUnique() {
		def task = CalculateTask.findByDataId(dataId)
		return task == null || task.status == TaskStatus.COMPLETED
	}
	
	static constraints = {
		dataId(nullable: false)	
	}
	
}
