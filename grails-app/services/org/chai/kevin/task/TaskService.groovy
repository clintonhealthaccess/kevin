package org.chai.kevin.task

import org.chai.kevin.task.Task;
import org.chai.kevin.task.Task.TaskStatus;

class TaskService {
	
	static rabbitQueue = 'adminQueue'
	static transactional = true
	
	def handleMessage(def taskId) {
		if (log.isDebugEnabled()) log.debug('handleMessage(taskId='+taskId+')')
		
		// handle Long message…
		def task = Task.get(taskId)
		
		if (task != null && task.status != TaskStatus.COMPLETED) {
			task.status = TaskStatus.IN_PROGRESS
			task.numberOfTries++
			task.save(failOnError: true)
			task.executeTask()
			task.status = TaskStatus.COMPLETED
			task.save(failOnError: true)
		}
	}
	
	def sendToQueue(def task) {
		if (log.isDebugEnabled()) log.debug("sendToQueue(task="+task+")")
				
		task.sentToQueue = false
		try {
			// we add the class to the queue for processing
			rabbitSend 'adminQueue', task.id
			// we set the flag to true, the queue now is 
			// responsible for making sure the task gets 
			// processed (even if the queue fails, it should persist the job
			task.sentToQueue = true
		} catch (Exception e) {
			if (log.isWarnEnabled()) log.warn("exception trying to send the task to the queue for processing", e);
		}
		task.save(failOnError: true)
	}

}
