package org.chai.kevin.task

import net.sf.json.JSONNull;

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Type;
import org.chai.kevin.task.Task.TaskStatus;
import org.chai.kevin.util.JSONUtils;
import org.codehaus.groovy.grails.plugins.testing.GrailsMockMultipartFile;

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
		def task = new CalculateTask(dataId: 1, user: user, status: TaskStatus.NEW, sentToQueue: false).save(failOnError: true)
		taskController = new TaskController()
		
		when:
		taskController.params.id = task.id
		taskController.delete()
		
		then:
		Task.count() == 0
		taskController.response.redirectedUrl == '/'
	}
	
	def "delete complete task"() {
		setup:
		def user = newUser('user', 'uuid')
		setupSecurityManager(user)
		def task = new CalculateTask(dataId: 1, user: user, status: TaskStatus.COMPLETED, sentToQueue: true).save(failOnError: true)
		taskController = new TaskController()
		
		when:
		taskController.params.id = task.id
		taskController.delete()
		
		then:
		Task.count() == 0
		taskController.response.redirectedUrl == '/'
	}
	
	// cannot test because of withNewTransaction call, and abort()
	// seems to not be overridable using metaClass (task.metaClass.abort = {aborted = true})
//	def "delete in progress task already sent aborts the task"() {
//		setup:
//		def user = newUser('user', 'uuid')
//		setupSecurityManager(user)
//		def task = new TestCalculateTask(dataId: 1, user: user, status: TaskStatus.IN_PROGRESS, sentToQueue: true).save(failOnError: true)
//		taskController = new TaskController()
//		
//		when:
//		taskController.params.id = task.id
//		taskController.delete()
//		
//		then:
//		Task.count() == 1
//		Task.list()[].aborted == true
//		taskController.response.redirectedUrl == '/'
//	}
	
	
	def "delete new task already sent aborts the task"() {
		setup:
		def user = newUser('user', 'uuid')
		setupSecurityManager(user)
		def task = new CalculateTask(dataId: 1, user: user, status: TaskStatus.NEW, sentToQueue: true).save(failOnError: true)
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
		def task5 = new CalculateTask(dataId: 5, user: user, status: TaskStatus.ABORTED).save(failOnError: true)
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
	
	
	def "task form with inexisting class"() {
		setup:
		taskController = new TaskController()
		
		when:
		taskController.params['class'] = 'not_existant'
		taskController.taskForm()
		
		then:
		taskController.response.redirectedUrl == null
	}
	
	def "task form"() {
		setup:
		taskController = new TaskController()
		
		when:
		taskController.params['class'] = 'NominativeImportTask'
		taskController.taskForm()
		
		then:
		taskController.modelAndView.model.task != null
		taskController.modelAndView.viewName == '/task/nominativeImport'		
	}
	
	def "create task with file - normal behaviour"() {
		setup:
		def user = newUser('user', 'uuid')
		setupSecurityManager(user) 
		def period = newPeriod()
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		File tempFileZip = new File("test/integration/org/chai/kevin/imports/nominativeTestFile.csv.zip")
		GrailsMockMultipartFile grailsMockMultipartFileZip = new GrailsMockMultipartFile(
			"nominativeTestFile", "nominativeTestFile.csv.zip", "", tempFileZip.getBytes())
		taskController = new TaskController()
		
		when:
		taskController.params['class'] = 'NominativeImportTask'
		taskController.params.rawDataElementId = dataElement.id
		taskController.params.periodId = period.id
		taskController.params.encoding = 'UTF-8'
		taskController.params.delimiter = ','
		taskController.params.file = grailsMockMultipartFileZip
		taskController.createTaskWithFile()
		
		then:
		taskController.response.redirectedUrl == '/'
		Task.count() == 1
		Task.list()[0].rawDataElementId == dataElement.id
		Task.list()[0].periodId == period.id
		Task.list()[0].encoding == 'UTF-8'
		Task.list()[0].delimiter == ','
		Task.list()[0].inputFilename == 'nominativeTestFile.csv.zip'
		
	}
	
	def "create task with file validation - no fields"() {
		setup:
		def user = newUser('user', 'uuid')
		setupSecurityManager(user)
		taskController = new TaskController()
		
		when:
		taskController.params['class'] = 'NominativeImportTask'
		taskController.createTaskWithFile()
		
		then:
		taskController.modelAndView.model.task != null
		taskController.modelAndView.model.taskWithFile != null
		taskController.modelAndView.viewName == '/task/nominativeImport'
		taskController.modelAndView.model.taskWithFile.errors.getFieldErrors('file').size() == 1
		taskController.modelAndView.model.taskWithFile.errors.getFieldErrors('inputFilename').size() == 1
		taskController.modelAndView.model.taskWithFile.errors.getFieldErrors('encoding').size() == 1
		taskController.modelAndView.model.taskWithFile.errors.getFieldErrors('delimiter').size() == 1
		taskController.modelAndView.model.task.errors.getFieldErrors('rawDataElementId').size() == 1
		taskController.modelAndView.model.task.errors.getFieldErrors('periodId').size() == 1
		Task.count() == 0
	}
	
	def "create task with file validation - file name not correct"() {
		setup:
		def user = newUser('user', 'uuid')
		setupSecurityManager(user)
		File tempFileZip = new File("test/integration/org/chai/kevin/imports/nominativeTestFile.csv.zip")
		GrailsMockMultipartFile grailsMockMultipartFileZip = new GrailsMockMultipartFile(
			"nominativeTestFile", "nominativeTestFile.wrong", "", tempFileZip.getBytes())
		taskController = new TaskController()
		
		when:
		taskController.params['class'] = 'NominativeImportTask'
		taskController.params.encoding = 'UTF-8'
		taskController.params.delimiter = ','
		taskController.params.file = grailsMockMultipartFileZip
		taskController.createTaskWithFile()
		
		then:
		taskController.modelAndView.model.task != null
		taskController.modelAndView.model.taskWithFile != null
		taskController.modelAndView.viewName == '/task/nominativeImport'
		taskController.modelAndView.model.taskWithFile.errors.getFieldErrors('file').size() == 0
		taskController.modelAndView.model.taskWithFile.errors.getFieldErrors('inputFilename').size() == 1
		taskController.modelAndView.model.taskWithFile.errors.getFieldErrors('encoding').size() == 0
		taskController.modelAndView.model.taskWithFile.errors.getFieldErrors('delimiter').size() == 0
		taskController.modelAndView.model.task.errors.getFieldErrors('rawDataElementId').size() == 1
		taskController.modelAndView.model.task.errors.getFieldErrors('periodId').size() == 1
		Task.count() == 0
	}
	
	def "download output when task class does not exist"() {
		setup:
		taskController = new TaskController()
		
		when:
		taskController.params['id'] = 1
		taskController.downloadOutput()
		
		then:
		taskController.response.redirectedUrl == null
	}
	
	def "download output when task is not completed"() {
		setup:
		def user = newUser('user', 'uuid')
		def task = new CalculateTask(dataId: 1, user: user, status: TaskStatus.NEW).save(failOnError: true)
		taskController = new TaskController()
		
		when:
		taskController.params['id'] = task.id
		taskController.downloadOutput()
		
		then:
		taskController.response.redirectedUrl == null
	}
	
	def "download output when task has no output"() {
		setup:
		def user = newUser('user', 'uuid')
		def task = new CalculateTask(dataId: 1, user: user, status: TaskStatus.COMPLETED).save(failOnError: true)
		taskController = new TaskController()
		
		when:
		taskController.params['id'] = task.id
		taskController.downloadOutput()
		
		then:
		taskController.response.redirectedUrl == null
	}
	
	def "download output when output file not found"() {
		setup:
		def user = newUser('user', 'uuid')
		def task = new NominativeImportTask(rawDataElementId: 1, periodId: 1, user: user, status: TaskStatus.COMPLETED, inputFilename: 'test.csv', delimiter: ',', encoding: 'UTF-8').save(failOnError: true)
		taskController = new TaskController()
		
		when:
		taskController.params['id'] = task.id
		taskController.downloadOutput()
		
		then:
		taskController.flash.message != null
		taskController.response.contentType != "application/zip";
	}
	
	def "download output when output file found"() {
		setup:
		def user = newUser('user', 'uuid')
		def task = new NominativeImportTask(rawDataElementId: 1, periodId: 1, user: user, status: TaskStatus.COMPLETED, inputFilename: 'nominativeTestFile.csv.zip', delimiter: ',', encoding: 'UTF-8').save(failOnError: true)
		task.metaClass.getFolder = { return new File('test/integration/org/chai/kevin/imports/') }
		task.metaClass.getOutputFilename = { return "nominativeTestFile.csv.zip"}
		taskController = new TaskController()
		
		when:
		taskController.params['id'] = task.id
		taskController.downloadOutput()
		
		then:
		taskController.flash.message == null
		taskController.response.outputStream != null
		taskController.response.contentType == "application/zip";
	}
	
}
