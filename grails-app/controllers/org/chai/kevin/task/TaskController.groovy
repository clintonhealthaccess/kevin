package org.chai.kevin.task

import org.chai.kevin.AbstractController;
import org.chai.kevin.task.Task.TaskStatus;

class TaskController extends AbstractController {
	
	def taskService
	
	/**
	 * Lists all tasks currently saved in the database
	 */
	def list = {
		adaptParamsForList()
		
		def tasks = Task.list(params)
		
		render (view: '/entity/list', model:[
			entities: tasks,
			template: "task/taskList",
			code: 'task.label',
			entityCount: Task.count(),
			entityClass: Task.class,
			search: true
		])
	}
	
	/**
	 * Ajax call that gets the progress of an individual task
	 */
	def progress = {
		
	}
	
	/**
	 * Creates a task and sends it for processing
	 */
	def create = {
		if (log.isDebugEnabled()) log.debug("task.create, params:"+params) 
		
		Class taskClass
		try {
			if (params.get('class') != null) taskClass = Class.forName('org.chai.kevin.task.'+params['class'], true, Thread.currentThread().contextClassLoader)
		} catch (ClassNotFoundException e) {}
		if (taskClass != null) {
			// we create the class
			def task = taskClass.newInstance()
			task.properties = params
			
			// we set the status
			task.status = TaskStatus.NEW
			task.user = currentUser
			task.added = new Date()
			
			if (task.validate()) {
				// we check that it doesn't already exist
				if (!task.isUnique()) {
					flash.message = message(code: 'task.creation.notunique.error', args: [createLink(controller: 'task', action: 'list')])
					redirect(uri: targetURI)
				}
				else {
					// we save it
					task.save(failOnError: true)
					
					// we send it for processing
					taskService.sendToQueue(task)
					
					// we redirect to the list
					flash.message = message(code: 'task.creation.success', args: [createLink(controller: 'task', action: 'list')])
					redirect(uri: targetURI)
				}
			}
			else {
				if (log.isInfoEnabled()) log.info ("validation error in ${task}: ${task.errors}}")
				flash.message = message(code: 'task.creation.validation.error')
				redirect(uri: targetURI)
			}

		}
		else {
			response.sendError(404)
		}
	}
	
	def delete = {
		if (log.isDebugEnabled()) log.debug("task.delete, params:"+params)
		
		def entity = Task.get(params.int('id'))
		if (entity != null) {
			try {
				entity.delete()
				
				if (!flash.message) flash.message = message(code: 'default.deleted.message', args: [message(code: 'task.label', default: 'entity'), params.id])
				redirect(uri: targetURI)
			}
			catch (org.springframework.dao.DataIntegrityViolationException e) {
				flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'task.label', default: 'entity'), params.id])
				redirect(uri: targetURI)
			}
		}
		else {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'task.label', default: 'entity'), params.id])
			redirect(uri: targetURI)
		}
	}
	
}
