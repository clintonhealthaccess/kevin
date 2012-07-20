package org.chai.kevin.task

import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;

/**
 * This Job is here in case the queue is not available. It runs every 2 minutes
 * to check that all tasks have been sent to the queue for processing.
 */
class TaskJob implements InterruptableJob {

	static triggers = {
		cron name: 'taskTrigger', startDelay: 10000, cronExpression: "0 */2 * * * ?"
	}
	
	def sessionRequired = true
	def concurrent = false

	def taskService
	
	void execute(JobExecutionContext context) {
		if (log.isInfoEnabled()) log.info('executing TaskJob');
	
		def tasks = Task.findBySentToQueue(false)
		tasks.each { task -> taskService.sendToQueue(task) }
	}
	
	void interrupt() {}
	
}
