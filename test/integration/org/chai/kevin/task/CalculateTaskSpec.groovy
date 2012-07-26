package org.chai.kevin.task

import grails.validation.ValidationException;

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.NormalizedDataElement;
import org.chai.kevin.data.Type;
import org.chai.kevin.task.Task.TaskStatus;
import org.chai.kevin.value.NormalizedDataElementValue;

class CalculateTaskSpec extends IntegrationTests {

	def "null constraints"() {
		setup:
		def user = newUser('user', 'uuid')
		
		when:
		new CalculateTask(user: user, status: TaskStatus.NEW, dataId: 1).save(failOnError: true)
		
		then:
		Task.count() == 1
		
		when:
		new CalculateTask(user: user, status: TaskStatus.NEW).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
		
	def "task is unique"() {
		setup:
		def user = newUser('user', 'uuid')
		def task = new CalculateTask(user: user, status: TaskStatus.NEW, dataId: 1).save(failOnError: true)
		
		when:
		def sameTask = new CalculateTask(user: user, status: TaskStatus.NEW, dataId: 1)
		
		then:
		!sameTask.isUnique()
		
		when:
		def otherTask = new CalculateTask(user: user, status: TaskStatus.NEW, dataId: 2)
		
		then:
		otherTask.isUnique()
		
		when:
		task.status = TaskStatus.COMPLETED
		task.save(failOnError: true)
		
		then:
		sameTask.isUnique()
	}
	
	def "execute task - CalculateTask"() {
		setup:
		def user = newUser('user', 'uuid')
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP): '1']]))
		def task = new CalculateTask(user: user, status: TaskStatus.NEW, dataId: normalizedDataElement.id).save(failOnError: true)
		
		when:
		task.executeTask()
		
		then:
		NormalizedDataElementValue.count() == 2
	}
	
	
}
