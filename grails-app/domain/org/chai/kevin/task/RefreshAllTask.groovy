package org.chai.kevin.task

import org.chai.kevin.task.Task.TaskStatus;
import org.chai.kevin.data.Calculation;
import org.chai.kevin.data.Data;
import org.chai.kevin.data.NormalizedDataElement;

class RefreshAllTask extends Task {

	def refreshValueService
	
	public RefreshAllTask() {
		super();
	}
	
	def executeTask() {
		refreshValueService.refreshAll(this)
		refreshValueService.flushCaches()
	}
	
	String getInformation() {
		return ''
	}
	
	boolean isUnique() {
		def tasks = RefreshAllTask.list()
		return tasks.empty || tasks[0].status == TaskStatus.COMPLETED || tasks[0].status == TaskStatus.ABORTED
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
	
}
