package org.chai.kevin.task

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.task.Task.TaskStatus;

class TaskJobSpec extends IntegrationTests {

	def taskJob
	def taskService
	
	def "job sends all non sent to queue to the queue"() {
		setup:
		def user = newUser('user', 'uuid')
		taskService.metaClass.rabbitSend = { Object[] args -> return; }
		taskJob = new TaskJob()
		taskJob.taskService = taskService
		
		when:
		def task1 = new CalculateTask(user: user, status: TaskStatus.NEW, dataId: '1', sentToQueue: false).save(failOnError:true)
		def task2 = new CalculateTask(user: user, status: TaskStatus.NEW, dataId: '2', sentToQueue: false).save(failOnError:true)
		taskJob.execute()
		
		then:
		task1.sentToQueue == true
		task2.sentToQueue == true
	}
	
}
