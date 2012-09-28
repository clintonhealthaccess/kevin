package org.chai.task

import org.chai.task.CalculateTask;
import org.chai.task.TaskController;

class TaskControllerSpec {

	
	def "task list"() {
		setup:
		setupSecurityManager('uuid')
		def task = new CalculateTask(dataId: 1, principal: 'uuid', status: TaskStatus.NEW).save(failOnError: true)
		taskController = new TaskController()
		
		when:
		taskController.list()
		
		then:
		taskController.modelAndView.model.entities == [task]
	}
	
}
