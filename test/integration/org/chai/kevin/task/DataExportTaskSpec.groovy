package org.chai.kevin.task

import grails.validation.ValidationException

import org.apache.commons.io.FileUtils;
import org.chai.kevin.IntegrationTests
import org.chai.kevin.Period
import org.chai.kevin.data.NormalizedDataElement
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Sum;
import org.chai.kevin.data.Type;
import org.chai.kevin.exports.CalculationExport
import org.chai.kevin.exports.DataElementExport
import org.chai.kevin.location.DataLocation
import org.chai.kevin.location.DataLocationType
import org.chai.kevin.location.Location
import org.chai.kevin.location.LocationLevel
import org.chai.kevin.security.User
import org.chai.task.Task;
import org.chai.task.Task.TaskStatus
import org.chai.kevin.value.NormalizedDataElementValue
import org.chai.task.DataExportTask;

class DataExportTaskSpec extends IntegrationTests {

	static transactional = false
	
	def cleanup() {
		DataElementExport.list().each {it.locations.clear(); it.periods.clear(); it.dataElements.clear(); it.delete();}
		CalculationExport.list().each {it.locations.clear(); it.periods.clear(); it.calculations.clear(); it.delete();}
		sessionFactory.currentSession.flush()
		NormalizedDataElement.executeUpdate("delete NormalizedDataElement")
		Sum.executeUpdate("delete Summ")
		RawDataElement.executeUpdate("delete RawDataElement")
		DataLocation.executeUpdate("delete DataLocation")
		Location.executeUpdate("delete Location")
		LocationLevel.executeUpdate("delete LocationLevel")
		DataLocationType.executeUpdate("delete DataLocationType")
		Period.executeUpdate("delete Period")
		DataExportTask.executeUpdate("delete DataExportTask")
		User.executeUpdate("delete User")
		sessionFactory.currentSession.flush()
	}
	
	def "null constraints"() {
		setup:
		def user = newUser('user', 'uuid')
		
		when:
		new DataExportTask(user: user, status: TaskStatus.NEW, exportId: 1).save(failOnError: true, flush: true)
		
		then:
		Task.count() == 1
		
		when:
		new DataExportTask(user: user, status: TaskStatus.NEW).save(failOnError: true, flush: true)
		
		then:
		thrown ValidationException
	}
		
	def "task is unique"() {
		setup:
		def user = newUser('user', 'uuid')
		def task = new DataExportTask(user: user, status: TaskStatus.NEW, exportId: 1).save(failOnError: true, flush: true)
		
		when:
		def sameTask = new DataExportTask(user: user, status: TaskStatus.NEW, exportId: 1)
		
		then:
		!sameTask.isUnique()
		
		when:
		def otherTask = new DataExportTask(user: user, status: TaskStatus.NEW, exportId: 2)
		
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
	
	def "execute task - DataExportTask with calculation"() {
		setup:
		def user = newUser('user', 'uuid')
		setupLocationTree();
		def period = newPeriod()
		def sum = newSum("1",CODE(1));
		
		def dataExport = newCalculationExport(j("en":"Testing Seach One"), 
			new HashSet([period]), 
			DISTRICT_HOSPITAL_GROUP+','+HEALTH_CENTER_GROUP, 
			new HashSet([Location.findByCode(BURERA), DataLocation.findByCode(KIVUYE)]), 
			new HashSet([sum]))
		
		def task = new DataExportTask(principal: user.uuid, status: TaskStatus.NEW, exportId: dataExport.id).save(failOnError: true, flush: true)
		FileUtils.deleteDirectory(task.folder)
		
		when:
		task.executeTask()
		
		then:
		new File(task.folder, 'exportOutput.csv').exists()
		
		// tearDown
		FileUtils.deleteDirectory(task.folder)
	}
	
	def "execute task - DataExportTask with data element"() {
		setup:
		def user = newUser('user', 'uuid')
		setupLocationTree();
		def period = newPeriod()
		
		def dataExport = newDataElementExport(j("en":"Testing Seach One"),
			new HashSet([period]),
			DISTRICT_HOSPITAL_GROUP+','+HEALTH_CENTER_GROUP,
			new HashSet([Location.findByCode(BURERA), DataLocation.findByCode(KIVUYE)]),
			new HashSet([newRawDataElement(CODE(1), Type.TYPE_NUMBER())]))
		
		def task = new DataExportTask(principal: user.uuid, status: TaskStatus.NEW, exportId: dataExport.id).save(failOnError: true, flush: true)
		FileUtils.deleteDirectory(task.folder)
		
		when:
		task.executeTask()
		
		then:
		new File(task.folder, 'exportOutput.csv').exists()
		
		// tearDown
		FileUtils.deleteDirectory(task.folder)
	}
	
	def "test clean task"() {
		setup:
		def user = newUser('user', 'uuid')
		setupLocationTree();
		def period = newPeriod()
		
		def dataExport = newDataElementExport(j("en":"Testing Seach One"),
			new HashSet([newPeriod()]),
			DISTRICT_HOSPITAL_GROUP+HEALTH_CENTER_GROUP,
			new HashSet([Location.findByCode(BURERA), DataLocation.findByCode(KIVUYE)]),
			new HashSet([newRawDataElement(CODE(1), Type.TYPE_NUMBER())]))
		
		when:
		def task = new DataExportTask(user: user, status: TaskStatus.NEW, exportId: dataExport.id).save(failOnError: true, flush: true)
		task.getFolder()
		
		then:
		new File('files/'+task.id).exists()
		
		when:
		task.cleanTask()
		
		then:
		!new File('files/'+task.id).exists()  
	}
	
}