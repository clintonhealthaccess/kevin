package org.chai.kevin.task

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.task.Task.TaskStatus;

class TaskControllerSpec extends IntegrationTests {

	def taskController
	
	def "create task"() {
		setup:
		def user = newUser('user', 'uuid')
		setupSecurityManager(user)
		taskController = new TaskController()
		
		when:
		taskController.params['class'] = 'CalculateTask'
		taskController.params['dataId'] = 1
		taskController.create()
		
		then:
		Task.count() == 1
		Task.list()[0].dataId == 1
		taskController.response.redirectedUrl == '/'
	}
	
	def "create non-unique task"() {
		setup:
		def user = newUser('user', 'uuid')
		setupSecurityManager(user)
		new CalculateTask(dataId: 1, user: user, status: TaskStatus.NEW).save(failOnError: true)
		taskController = new TaskController()
		
		when:
		taskController.params['class'] = 'CalculateTask'
		taskController.params['dataId'] = 1
		taskController.create()
		
		then:
		Task.count() == 1
		taskController.response.redirectedUrl == '/'
	}
	
	def "create unique task that is a new task of an already completed one"() {
		setup:
		def user = newUser('user', 'uuid')
		setupSecurityManager(user)
		new CalculateTask(dataId: 1, user: user, status: TaskStatus.COMPLETED).save(failOnError: true)
		taskController = new TaskController()
		
		when:
		taskController.params['class'] = 'CalculateTask'
		taskController.params['dataId'] = 1
		taskController.create()
		
		then:
		Task.count() == 2
		taskController.response.redirectedUrl == '/'
	}
	
	def "create task validation"() {
		setup:
		def user = newUser('user', 'uuid')
		setupSecurityManager(user)
		taskController = new TaskController()
		
		when:
		taskController.params['class'] = 'CalculateTask'
		taskController.create()
		
		then:
		Task.count() == 0
		taskController.response.redirectedUrl == '/'
	}
	
	def "create task with wrong class"() {
		setup:
		def user = newUser('user', 'uuid')
		setupSecurityManager(user)
		taskController = new TaskController()
		
		when:
		taskController.params['class'] = 'Inexistant'
		taskController.create()
		
		then:
		taskController.modelAndView == null
	}
	
	def "task list"() {
		setup:
		def user = newUser('user', 'uuid')
		setupSecurityManager(user)
		def task = new CalculateTask(dataId: 1, user: user, status: TaskStatus.NEW).save(failOnError: true)
		taskController = new TaskController()
		
		when:
		taskController.list()
		
		then:
		taskController.modelAndView.model.entities == [task] 
	}
	
	def "delete task"() {
		setup:
		def user = newUser('user', 'uuid')
		setupSecurityManager(user)
		def task = new CalculateTask(dataId: 1, user: user, status: TaskStatus.NEW).save(failOnError: true)
		taskController = new TaskController()
		
		when:
		taskController.params.id = task.id
		taskController.delete()
		
		then:
		Task.count() == 0
		taskController.response.redirectedUrl == '/'
	}
	
	def "delete inexistant task"() {
		setup:
		taskController = new TaskController()
		
		when:
		taskController.params.id = '1'
		taskController.delete()
		
		then:
		Task.count() == 0
		taskController.response.redirectedUrl == '/'
	}
	
}
