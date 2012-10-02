package org.chai.kevin.task

import org.chai.kevin.AbstractController;
import org.chai.task.Task
import org.chai.task.TaskController;

class TaskListController extends AbstractController {

	/**
	* Lists all tasks currently saved in the database
	*/
   def list = {
	   adaptParamsForList()
	   	   
	   def tasks = Task.list(params)
	   
	   render (view: '/entity/task/list', model:[
		   entities: tasks,
		   template: "task/taskList",
		   code: 'task.label',
		   entityCount: Task.count(),
		   entityClass: Task.class,
		   search: true
	   ])
   }
   
}
