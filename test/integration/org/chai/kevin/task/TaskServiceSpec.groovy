package org.chai.kevin.task

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.task.Task.TaskStatus;

class TaskServiceSpec extends IntegrationTests {

	def taskService
	
	def "handle message sets status to completed"() {
		setup:
		def user = newUser('user', 'uuid')
		def task = new CalculateTask(user: user, status: TaskStatus.NEW, dataId: '1').save(failOnError:true)
		
		when:
		taskService.handleMessage(task.id)
		
		then:
		Task.list()[0].status == TaskStatus.COMPLETED
	}
	
	def "handle message increments number of tries"() {
		setup:
		def user = newUser('user', 'uuid')
		def task = new CalculateTask(user: user, status: TaskStatus.NEW, dataId: '1').save(failOnError:true)
		
		when:
		taskService.handleMessage(task.id)
		
		then:
		Task.list()[0].numberOfTries == 1
	}
	
	def "send to queue sets senttoqueue to false when rabbit not running"() {
		setup:
		def user = newUser('user', 'uuid')
		def task = new CalculateTask(user: user, status: TaskStatus.NEW, dataId: '1').save(failOnError:true)
		
		when:
		taskService.sendToQueue(task)
		
		then:
		Task.list()[0].sentToQueue == false
	}
}
