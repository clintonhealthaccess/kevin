package org.chai.kevin.task

import net.sf.json.JSONNull;

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.task.Task.TaskStatus;
import org.chai.kevin.util.JSONUtils;

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
		Task.list()[0].max == 0
		Task.list()[0].current == null
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
	
	def "purge tasks"() {
		setup:
		def user = newUser('user', 'uuid')
		def task1 = new CalculateTask(dataId: 1, user: user, status: TaskStatus.NEW).save(failOnError: true)
		def task2 = new CalculateTask(dataId: 2, user: user, status: TaskStatus.COMPLETED).save(failOnError: true)
		def task3 = new CalculateTask(dataId: 3, user: user, status: TaskStatus.IN_PROGRESS).save(failOnError: true)
		def task4 = new CalculateTask(dataId: 4, user: user, status: TaskStatus.COMPLETED).save(failOnError: true)
		taskController = new TaskController()
		
		when:
		taskController.purge()
		
		then:
		Task.count() == 2
		Task.list()[0].status == TaskStatus.NEW
		Task.list()[1].status == TaskStatus.IN_PROGRESS
	}
	
	def "progress when new"() {
		setup:
		def user = newUser('user', 'uuid')
		def task1 = new CalculateTask(dataId: 1, user: user, status: TaskStatus.NEW).save(failOnError: true)
		taskController = new TaskController()
		
		when:
		taskController.params['ids'] = [task1.id]
		taskController.progress()
		def content = taskController.response.contentAsString
		def jsonResult = JSONUtils.getMapFromJSON(content)
		
		then:
		jsonResult.tasks.size() == 1
		jsonResult.tasks[0].status == "NEW"
		jsonResult.tasks[0].progress == JSONNull.instance
		jsonResult.tasks[0].id == task1.id
	}
	
	def "progress when in progress"() {
		setup:
		def user = newUser('user', 'uuid')
		def task1 = new CalculateTask(dataId: 1, user: user, status: TaskStatus.IN_PROGRESS, max: 100, current: 50).save(failOnError: true)
		taskController = new TaskController()
		
		when:
		taskController.params['ids'] = [task1.id]
		taskController.progress()
		def jsonResult = JSONUtils.getMapFromJSON(taskController.response.contentAsString)
		
		then:
		jsonResult.tasks.size() == 1
		jsonResult.tasks[0].status == "IN_PROGRESS"
		jsonResult.tasks[0].progress == 0.5
		jsonResult.tasks[0].id == task1.id
	}
	
	def "progress when task does not exist"() {
		setup:
		taskController = new TaskController()
		
		when:
		taskController.params['ids'] = ['1']
		taskController.progress()
		def jsonResult = JSONUtils.getMapFromJSON(taskController.response.contentAsString)
		
		then:
		jsonResult.tasks.size() == 0
	}
	
}

