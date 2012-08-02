package org.chai.kevin.task

import grails.validation.ValidationException;

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.NormalizedDataElement;
import org.chai.kevin.data.Type;
import org.chai.kevin.task.Task.TaskStatus;
import org.chai.kevin.value.NormalizedDataElementValue;

class TaskDomainSpec extends IntegrationTests {

	def "null constraints"() {
		setup:
		def user = newUser('user', 'uuid')
		
		when:
		new CalculateTask(user: user, status: TaskStatus.NEW, dataId: 1).save(failOnError: true)
		
		then:
		Task.count() == 1
		
		when:
		new CalculateTask(status: TaskStatus.NEW, dataId: 1).save(failOnError: true)
		
		then:
		Task.count() == 2
		
		when:
		new CalculateTask(user: user, dataId: 1).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
}
