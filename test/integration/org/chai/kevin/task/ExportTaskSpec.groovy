package org.chai.kevin.task

import grails.validation.ValidationException

import org.chai.kevin.IntegrationTests
import org.chai.kevin.Period
import org.chai.kevin.data.NormalizedDataElement
import org.chai.kevin.exports.CalculationExport
import org.chai.kevin.exports.DataElementExport
import org.chai.kevin.location.DataLocation
import org.chai.kevin.location.DataLocationType
import org.chai.kevin.location.Location
import org.chai.kevin.location.LocationLevel
import org.chai.kevin.security.User
import org.chai.kevin.task.Task.TaskStatus
import org.chai.kevin.value.NormalizedDataElementValue

class ExportTaskSpec extends IntegrationTests {

	static transactional = false
	
	def cleanup() {
		DataElementExport.executeUpdate("delete DataElementExport")
		CalculationExport.executeUpdate("delete CalculationExport")
		NormalizedDataElementValue.executeUpdate("delete NormalizedDataElementValue")
		NormalizedDataElement.executeUpdate("delete NormalizedDataElement")
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
	}
	
//	def "execute task - DataExportTask"() {
//		setup:
//		def user = newUser('user', 'uuid')
//		setupLocationTree()
//		def period = newPeriod()
//		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP): '1']]))
//		def task = new CalculateTask(user: user, status: TaskStatus.NEW, dataId: normalizedDataElement.id).save(failOnError: true, flush: true)
//		
//		when:
//		task.executeTask()
//		
//		then:
//		NormalizedDataElementValue.count() == 2
//	}
	
	
//	def "get dataExport"(){
//		setup:
//		setupLocationTree();
//		def periods=new HashSet([newPeriod()]);
//		def locationType="Health Center,District Hospital";
//		
//		def sum = newSum("",CODE(1));
//		def aggregation = newAggregation("1",CODE(2));
//		
//		def locations=new HashSet();
//		locations.addAll(getLocations([BURERA]));
//		locations.addAll(getDataLocations([KIVUYE]));
//		
//		def calculations=new HashSet([sum,aggregation]);
//		def dataExport = newCalculationExport(j("en":"Testing Seach One"), periods, locationType, locations, calculations);
//		calculationExportController = new CalculationExportController();
//		
//		when:
//		calculationExportController.params.('export.id')=dataExport.id
//		calculationExportController.export()
//		then:
//		calculationExportController.response.getContentType() == "application/zip"
//		
//	}
//	
	
//	def "get dataExport"(){
//		setup:
//		setupLocationTree();
//		def periods=new HashSet([newPeriod()]);
//		def locationType="Health Center,District Hospital";
//		def typeOne = Type.TYPE_NUMBER();
//		def typeTwo = Type.TYPE_BOOL();
//		def dataElementOne = newRawDataElement(CODE(1), typeOne);
//		def dataElementTwo = newRawDataElement(CODE(2), typeTwo);
//		def locations=new HashSet();
//		locations.addAll(getLocations([BURERA]));
//		locations.addAll(getDataLocations([KIVUYE]));
//		def dataElements=new HashSet([dataElementOne,dataElementTwo]);
//		def dataExport = newDataElementExport(j("en":"Testing Seach One"), periods, locationType, locations, dataElements);
//		dataElementExportController = new DataElementExportController();
//		
//		when:
//		dataElementExportController.params.('export.id')=dataExport.id
//		dataElementExportController.export()
//		then:
//		dataElementExportController.response.getContentType() == "application/zip"
//		
//	}
	
}
