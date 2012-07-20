package org.chai.kevin.task

import org.chai.kevin.security.User


abstract class Task {

	enum TaskStatus{NEW, COMPLETED, IN_PROGRESS}
	
	User user
	TaskStatus status
	Date added
	Integer numberOfTries = 0
	Progress progress
	Boolean sentToQueue = false
	
	def abstract executeTask()
	
	abstract boolean isUnique()
	
	static mapping = {
		version false
	}
	
	static constraints = {
		user(nullable: false)
		status(nullable: false)
		progress(nullable: true)
	}
	
	static embedded = ['progress']
}

class Progress {
	
	Integer max;
	Integer current;

	private boolean aborted = false;
	
	Double retrievePercentage() {
		return current.doubleValue()/max.doubleValue();
	}
	
	void abort() {
		this.aborted = true;
	}
	
	boolean aborted() {
		return aborted;
	}
	
	void save() {
		this.save()
	}
}
