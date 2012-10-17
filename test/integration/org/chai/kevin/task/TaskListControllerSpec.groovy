package org.chai.kevin.task

import org.chai.task.CalculateTask;
import org.chai.kevin.task.TaskListController;

class TaskListControllerSpec {

	def "task list"() {
		setup:
		setupSecurityManager('uuid')
		def task = new CalculateTask(dataId: 1, principal: 'uuid', status: TaskStatus.NEW).save(failOnError: true)
		taskController = new TaskListController()
		
		when:
		taskController.list()
		
		then:
		taskController.modelAndView.model.entities == [task]
	}
	
}
