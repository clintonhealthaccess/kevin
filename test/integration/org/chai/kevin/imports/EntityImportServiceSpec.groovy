package org.chai.kevin.imports

import java.lang.reflect.Field
import org.chai.kevin.IntegrationTests
import org.chai.kevin.dashboard.DashboardTarget
import org.chai.kevin.data.Type;
import org.chai.kevin.exports.EntityHeaderSorter;
import org.chai.kevin.form.FormEnteredValue;
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.Location;
import org.chai.kevin.survey.SurveyQuestion
import org.chai.kevin.survey.SurveyElement
import org.chai.kevin.survey.SurveyIntegrationTests
import org.chai.kevin.util.Utils;

class EntityImportServiceSpec extends IntegrationTests {

	def entityExportService
	
	//TODO if import field is a list
	//TODO if import field is not a list, but contains an entity id
	//TODO if import field is not a list, and does not contain an entity id	
	
	def "test for export entity"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = SurveyIntegrationTests.newSurvey(j(["en":"survey"]), period)
		def program = SurveyIntegrationTests.newSurveyProgram(j(["en":"program"]), survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = SurveyIntegrationTests.newSurveySection(j(["en":"section"]), program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = SurveyIntegrationTests.newSimpleQuestion(j(["en":"question"]), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def type = Type.TYPE_NUMBER()
		def element = SurveyIntegrationTests.newSurveyElement(question, newRawDataElement(CODE(1), type))
		FormEnteredValue formEnteredValue = newFormEnteredValue(element, period, DataLocation.findByCode(BUTARO), v("10"))		
		Map<SurveyElement, FormEnteredValue> surveyElementValueMap = new HashMap<SurveyElement, FormEnteredValue>()
		surveyElementValueMap.put(formEnteredValue.getFormElement(), formEnteredValue)
		
		when:
		def file = entityExportService.getExportFile("file", SurveyQuestion.class)
		def zipFile = Utils.getZipFile(file, "file")
		
		then:
		zipFile.exists() == true
		zipFile.length() > 0
	}
	
	def "test for valid export filename"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = SurveyIntegrationTests.newSurvey(j(["en":"survey"]), period)
		def program = SurveyIntegrationTests.newSurveyProgram(j(["en":"program"]), survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = SurveyIntegrationTests.newSurveySection(j(["en":"section"]), program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = SurveyIntegrationTests.newSimpleQuestion(j(["en":"question"]), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def type = Type.TYPE_NUMBER()
		def element = SurveyIntegrationTests.newSurveyElement(question, newRawDataElement(CODE(1), type))
		FormEnteredValue formEnteredValue = newFormEnteredValue(element, period, DataLocation.findByCode(BUTARO), v("10"))		
		Map<SurveyElement, FormEnteredValue> surveyElementValueMap = new HashMap<SurveyElement, FormEnteredValue>()
		surveyElementValueMap.put(formEnteredValue.getFormElement(), formEnteredValue)
		
		when:
		def file = entityExportService.getExportFilename(SurveyQuestion.class)
		
		then:
		file.startsWith("SurveyQuestion_")
	}
	
	def "test for entity header sort"(){
		setup:
		def entitySurveyQuestionFieldHeaders = []			
		def headerClass = SurveyQuestion.class;
		while(headerClass != null && headerClass != Object.class){				
			Field[] classFields = headerClass.getDeclaredFields();
			for(Field field : classFields){
				if(field.getName().equalsIgnoreCase("id")) continue;
				entitySurveyQuestionFieldHeaders.add(field);
			}
			headerClass = headerClass.getSuperclass();
		}
		
		def entityDashboardTargetFieldHeaders = []
		headerClass = DashboardTarget.class;
		while(headerClass != null && headerClass != Object.class){
			Field[] classFields = headerClass.getDeclaredFields();
			for(Field field : classFields){
				if(field.getName().equalsIgnoreCase("id")) continue;
				entityDashboardTargetFieldHeaders.add(field);
			}
			headerClass = headerClass.getSuperclass();
		}
		
		when:
		Collections.sort(entitySurveyQuestionFieldHeaders, EntityHeaderSorter.BY_FIELD())
		def surveyQuestionHeaders = entitySurveyQuestionFieldHeaders.collect { it.getName() }
		Collections.sort(entityDashboardTargetFieldHeaders, EntityHeaderSorter.BY_FIELD())
		def dashboardTargetHeaders = entityDashboardTargetFieldHeaders.collect { it.getName() }
		
		then:
		surveyQuestionHeaders.equals(["code", "names", "order", "section", "typeCodeString", "descriptions"])
		dashboardTargetHeaders.equals(["code", "names", "order", "calculation", "program", "weight", "descriptions"])
	}
		
}
