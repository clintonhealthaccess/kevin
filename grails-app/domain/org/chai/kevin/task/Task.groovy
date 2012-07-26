package org.chai.kevin.task

import java.io.File;

import org.chai.kevin.security.User


abstract class Task implements Progress {

	enum TaskStatus{NEW, COMPLETED, IN_PROGRESS}
	
	def grailsApplication
	
	User user
	TaskStatus status
	Date added = new Date()
	Integer numberOfTries = 0
	Boolean sentToQueue = false
	
	// progress
	Integer max = 0;
	Integer current = null;
	
	abstract def executeTask()
	abstract def cleanTask()
	abstract boolean isUnique()
	abstract String getFormView()
	abstract Map getFormModel()
	abstract String getOutputFilename()  
	
	File getFolder() {
		def folder = new File(grailsApplication.config.task.temp.folder + File.separator + this.getId())
		if (!folder.exists()) folder.mkdirs()
		return folder
	}
	
	void incrementProgress() {
		if (current != null) {
			Task.withTransaction {
				current++
				this.save(flush: true)
			}
		}
	}
	
	void setMaximum(Integer max) {
		Task.withTransaction {
			this.max = max;
			this.current = 0;
			this.save(flush: true)
		}
	}
	
	Double retrievePercentage() {
		if (current == null || max == 0) return null
		return current.doubleValue()/max.doubleValue()
	}
	
	static mapping = {
		version false
	}
	
	static constraints = {
		user(nullable: false)
		status(nullable: false)
		max(nullable: false)
		current(nullable: true)
	}
	
}

