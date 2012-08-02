package org.chai.kevin.task

import java.io.File

import org.apache.commons.io.FileUtils
import org.chai.kevin.security.User


abstract class Task implements Progress {

	enum TaskStatus{NEW, COMPLETED, IN_PROGRESS, ABORTED}
	
	def grailsApplication
	
	User user
	TaskStatus status
	Date added = new Date()
	
	Date started
	Date finished
	
	Integer numberOfTries = 0
	Boolean sentToQueue = false
	
	// progress
	Long max = 0;
	Long current = null;
	Boolean aborted = false;
	
	abstract def executeTask()
	
	abstract boolean isUnique()
	abstract String getFormView()
	abstract Map getFormModel()
	abstract String getOutputFilename()
	abstract String getInformation()
	
	File getFolder() {
		def folder = new File(grailsApplication.config.task.temp.folder + File.separator + this.getId())
		if (!folder.exists()) folder.mkdirs()
		return folder
	}
	
	def cleanTask() {
		File folder = getFolder()
		if (folder != null && folder.exists()) FileUtils.deleteDirectory(folder)
	}
	
	void incrementProgress(Long increment = null) {
		if (log.isDebugEnabled()) log.debug('incrementProgress, max: '+ max +', current: '+current)
		if (aborted) throw new TaskAbortedException()
		
		if (current != null) {
			Task.withNewTransaction {
				if (increment == null) current++
				else current += increment
				this.save(flush: true)
			}
		}
	}
	
	void setMaximum(Long max) {
		Task.withNewTransaction {
			this.max = max;
			this.current = 0;
			this.save(flush: true)
		}
	}
	
	void abort() {
		Task.withNewTransaction {
			aborted = true
			this.save(flush: true)
		}
	}
	
	boolean isAborted() {
		return aborted
	}
	
	Double retrievePercentage() {
		if (current == null || max == 0) return null
		return current.doubleValue()/max.doubleValue()
	}
	
	static mapping = {
		version false
	}
	
	static constraints = {
		user(nullable: true)
		
		status(nullable: false)
		max(nullable: false)
		current(nullable: true)
		
		started(nullable: true)
		finished(nullable: true)
	}
	
}

