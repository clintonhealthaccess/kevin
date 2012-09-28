package org.chai.kevin

import org.chai.kevin.IntegrationTests;
import org.chai.task.Task;
import org.chai.task.Task.TaskStatus;

class RefreshJobSpec extends IntegrationTests {

	def refreshJob
	def taskService
	
	def "refresh job creates refreshall task"() {
		setup:
		taskService.metaClass.rabbitSend = { Object[] args -> return; }
		refreshJob = new RefreshJob()
		refreshJob.taskService = taskService
		
		when:
		refreshJob.execute()
		
		then:
		Task.count()
	}
	
}
