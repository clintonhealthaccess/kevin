package org.chai.kevin.task

import grails.validation.ValidationException;

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.Period;
import org.chai.kevin.data.NormalizedDataElement;
import org.chai.kevin.data.Type;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.location.Location;
import org.chai.kevin.location.LocationLevel;
import org.chai.kevin.security.User;
import org.chai.task.Task;
import org.chai.task.Task.TaskStatus;
import org.chai.kevin.value.NormalizedDataElementValue;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.task.CalculateTask;

class CalculateTaskSpec extends IntegrationTests {

	static transactional = false
	
	def cleanup() {
		NormalizedDataElementValue.executeUpdate("delete NormalizedDataElementValue")
		NormalizedDataElement.executeUpdate("delete NormalizedDataElement")
		DataLocation.executeUpdate("delete DataLocation")
		Location.executeUpdate("delete Location")
		LocationLevel.executeUpdate("delete LocationLevel")
		DataLocationType.executeUpdate("delete DataLocationType")
		Period.executeUpdate("delete Period")
		CalculateTask.executeUpdate("delete CalculateTask")
		User.executeUpdate("delete User")
		sessionFactory.currentSession.flush()
	}
	
	def "null constraints"() {
		setup:
		def user = newUser('user', 'uuid')
		
		when:
		new CalculateTask(user: user, status: TaskStatus.NEW, dataId: 1).save(failOnError: true, flush: true)
		
		then:
		Task.count() == 1
		
		when:
		new CalculateTask(user: user, status: TaskStatus.NEW).save(failOnError: true, flush: true)
		
		then:
		thrown ValidationException
	}
		
	def "task is unique"() {
		setup:
		def user = newUser('user', 'uuid')
		def task = new CalculateTask(user: user, status: TaskStatus.NEW, dataId: 1).save(failOnError: true, flush: true)
		
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
		task.save(failOnError: true, flush: true)
		
		then:
		sameTask.isUnique()
		
		when:
		task.status = TaskStatus.ABORTED
		task.save(failOnError: true, flush: true)
		
		then:
		sameTask.isUnique()
	}
	
	def "execute task - CalculateTask"() {
		setup:
		def user = newUser('user', 'uuid')
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP): '1']]))
		def task = new CalculateTask(user: user, status: TaskStatus.NEW, dataId: normalizedDataElement.id).save(failOnError: true, flush: true)
		
		when:
		task.executeTask()
		
		then:
		NormalizedDataElementValue.count() == 2
	}
	
	
	def "get information when data found"() {
		setup:
		def user = newUser('user', 'uuid')
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP): '1']]))
		def task = new CalculateTask(user: user, status: TaskStatus.NEW, dataId: normalizedDataElement.id).save(failOnError: true, flush: true)
		
		expect:
		task.getInformation() == 'NormalizedDataElement: CODE1'
	}
	
	def "get information when data not found"() {
		setup:
		def user = newUser('user', 'uuid')
		def task = new CalculateTask(user: user, status: TaskStatus.NEW, dataId: 1).save(failOnError: true, flush: true)
		
		expect:
		task.getInformation() == 'Data element not found'
	}
	
	
}
