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
import org.chai.task.RefreshSurveyTask;
import org.chai.task.Task;
import org.chai.task.Task.TaskStatus;
import org.chai.kevin.value.NormalizedDataElementValue;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.task.CalculateTask;

class RefreshSurveyTaskSpec extends IntegrationTests {

	static transactional = false
	
	def cleanup() {
		NormalizedDataElementValue.executeUpdate("delete NormalizedDataElementValue")
		NormalizedDataElement.executeUpdate("delete NormalizedDataElement")
		DataLocation.executeUpdate("delete DataLocation")
		Location.executeUpdate("delete Location")
		LocationLevel.executeUpdate("delete LocationLevel")
		DataLocationType.executeUpdate("delete DataLocationType")
		Period.executeUpdate("delete Period")
		RefreshSurveyTask.executeUpdate("delete RefreshSurveyTask")
		User.executeUpdate("delete User")
		sessionFactory.currentSession.flush()
	}
	
	def "null constraints"() {
		when:
		new RefreshSurveyTask(principal: 'uuid', status: TaskStatus.NEW, locationId: 1, surveyId: 1).save(failOnError: true, flush: true)
		
		then:
		Task.count() == 1
		
		when:
		new RefreshSurveyTask(principal: 'uuid', status: TaskStatus.NEW, locationId: 1).save(failOnError: true, flush: true)
		
		then:
		thrown ValidationException
		
		when:
		new RefreshSurveyTask(principal: 'uuid', status: TaskStatus.NEW, surveyId: 1).save(failOnError: true, flush: true)
		
		then:
		thrown ValidationException
	}
		
}
