package org.chai.kevin.exports

import org.chai.kevin.data.Type;
import org.chai.kevin.form.FormEnteredValue;
import org.chai.location.DataLocationType;
import org.chai.location.DataLocation;
import org.chai.location.Location;
import org.chai.kevin.survey.SurveyElement;
import org.chai.kevin.survey.SurveyIntegrationTests;
import org.chai.kevin.util.Utils;

class SurveyExportServiceSpec extends SurveyIntegrationTests {

	def surveyExportService
		
	def "test for export section"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(CODE(1), ["en":"survey"], period)
		def program = newSurveyProgram(CODE(1), ["en":"program"], survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(CODE(1), ["en":"section"], program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = newSimpleQuestion(CODE(1), ["en":"question"], section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def type = Type.TYPE_NUMBER()
		def element = newSurveyElement(question, newRawDataElement(CODE(1), type))
		FormEnteredValue formEnteredValue = newFormEnteredValue(element, period, DataLocation.findByCode(BUTARO), v("10"))		
		Map<SurveyElement, FormEnteredValue> surveyElementValueMap = new HashMap<SurveyElement, FormEnteredValue>()
		surveyElementValueMap.put(formEnteredValue.getFormElement(), formEnteredValue)
		
		when:
		List<SurveyExportDataPoint> dataPoints = surveyExportService.getSurveyExportDataPoints(DataLocation.findByCode(BUTARO), survey, program, section, question, surveyElementValueMap)
	
		then:
		dataPoints.size() == 1
		dataPoints.get(0).equals(["survey",NORTH,BURERA,BUTARO,DISTRICT_HOSPITAL_GROUP,"program","section","SIMPLE","NUMBER","question","10.0"])
	}
	
	def "test missing enum"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(CODE(1), ["en":"survey"], period)
		def program = newSurveyProgram(CODE(1), ["en":"program"], survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(CODE(1), ["en":"section"], program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = newSimpleQuestion(CODE(1), ["en":"question"], section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def type = Type.TYPE_ENUM('MISSING')
		def element = newSurveyElement(question, newRawDataElement(CODE(1), type))
		FormEnteredValue formEnteredValue = newFormEnteredValue(element, period, DataLocation.findByCode(BUTARO), v("10"))
		Map<SurveyElement, FormEnteredValue> surveyElementValueMap = new HashMap<SurveyElement, FormEnteredValue>()
		surveyElementValueMap.put(formEnteredValue.getFormElement(), formEnteredValue)
		
		when:
		List<SurveyExportDataPoint> dataPoints = surveyExportService.getSurveyExportDataPoints(DataLocation.findByCode(BUTARO), survey, program, section, question, surveyElementValueMap)
	
		then:
		dataPoints.size() == 1
		dataPoints.get(0).equals(["survey",NORTH,BURERA,BUTARO,DISTRICT_HOSPITAL_GROUP,"program","section","SIMPLE","ENUM","question","10"])
	}
	
	def "test missing enum option"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(CODE(1), ["en":"survey"], period)
		def program = newSurveyProgram(CODE(1), ["en":"program"], survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(CODE(1), ["en":"section"], program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = newSimpleQuestion(CODE(1), ["en":"question"], section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def enume = newEnume("ENUM")
		def type = Type.TYPE_ENUM('ENUM')
		def element = newSurveyElement(question, newRawDataElement(CODE(1), type))
		FormEnteredValue formEnteredValue = newFormEnteredValue(element, period, DataLocation.findByCode(BUTARO), v("\"missing_option\""))
		Map<SurveyElement, FormEnteredValue> surveyElementValueMap = new HashMap<SurveyElement, FormEnteredValue>()
		surveyElementValueMap.put(formEnteredValue.getFormElement(), formEnteredValue)
		
		when:
		List<SurveyExportDataPoint> dataPoints = surveyExportService.getSurveyExportDataPoints(DataLocation.findByCode(BUTARO), survey, program, section, question, surveyElementValueMap)
	
		then:
		dataPoints.size() == 1
		dataPoints.get(0).equals(["survey",NORTH,BURERA,BUTARO,DISTRICT_HOSPITAL_GROUP,"program","section","SIMPLE","ENUM","question","missing_option"])
	}

	def "test for export program"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(CODE(1), ["en":"survey"], period)
		def program = newSurveyProgram(CODE(1), ["en":"program"], survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(CODE(1), ["en":"section"], program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = newSimpleQuestion(CODE(1), ["en":"question"], section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def type = Type.TYPE_NUMBER()
		def element = newSurveyElement(question, newRawDataElement(CODE(1), type))
		FormEnteredValue formEnteredValue = newFormEnteredValue(element, period, DataLocation.findByCode(BUTARO), v("10"))		
		Map<SurveyElement, FormEnteredValue> surveyElementValueMap = new HashMap<SurveyElement, FormEnteredValue>()
		surveyElementValueMap.put(formEnteredValue.getFormElement(), formEnteredValue)
		
		when:
		List<SurveyExportDataPoint> dataPoints = surveyExportService.getSurveyExportDataPoints(DataLocation.findByCode(BUTARO), survey, program, section, question, surveyElementValueMap)
	
		then:
		dataPoints.size() == 1
		dataPoints.get(0).equals(["survey",NORTH,BURERA,BUTARO,DISTRICT_HOSPITAL_GROUP,"program","section","SIMPLE","NUMBER","question","10.0"])
	}

	def "test for export survey"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(CODE(1), ["en":"survey"], period)
		def program = newSurveyProgram(CODE(1), ["en":"program"], survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(CODE(1), ["en":"section"], program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = newSimpleQuestion(CODE(1), ["en":"question"], section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def type = Type.TYPE_NUMBER()
		def element = newSurveyElement(question, newRawDataElement(CODE(1), type))
		FormEnteredValue formEnteredValue = newFormEnteredValue(element, period, DataLocation.findByCode(BUTARO), v("10"))		
		Map<SurveyElement, FormEnteredValue> surveyElementValueMap = new HashMap<SurveyElement, FormEnteredValue>()
		surveyElementValueMap.put(formEnteredValue.getFormElement(), formEnteredValue)
		
		when:
		List<SurveyExportDataPoint> dataPoints = surveyExportService.getSurveyExportDataPoints(DataLocation.findByCode(BUTARO), survey, program, section, question, surveyElementValueMap)
	
		then:
		dataPoints.size() == 1
		dataPoints.get(0).equals(["survey",NORTH,BURERA,BUTARO,DISTRICT_HOSPITAL_GROUP,"program","section","SIMPLE","NUMBER","question","10.0"])
	}
	
	def "test for export survey with data location not attached to lowest level"(){
		setup:
		setupLocationTree()
		def dataLocation = newDataLocation(["en":"Test"], "TEST", Location.findByCode(NORTH), DataLocationType.findByCode(HEALTH_CENTER_GROUP))
		def period = newPeriod()
		def survey = newSurvey(CODE(1), ["en":"survey"], period)
		def program = newSurveyProgram(CODE(1), ["en":"program"], survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(CODE(1), ["en":"section"], program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = newSimpleQuestion(CODE(1), ["en":"question"], section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def type = Type.TYPE_NUMBER()
		def element = newSurveyElement(question, newRawDataElement(CODE(1), type))
		FormEnteredValue formEnteredValue = newFormEnteredValue(element, period, dataLocation, v("10"))
		Map<SurveyElement, FormEnteredValue> surveyElementValueMap = new HashMap<SurveyElement, FormEnteredValue>()
		surveyElementValueMap.put(formEnteredValue.getFormElement(), formEnteredValue)
		
		when:
		List<SurveyExportDataPoint> dataPoints = surveyExportService.getSurveyExportDataPoints(dataLocation, survey, program, section, question, surveyElementValueMap)
	
		then:
		dataPoints.size() == 1
		dataPoints.get(0).equals(["survey",NORTH,"","Test",HEALTH_CENTER_GROUP,"program","section","SIMPLE","NUMBER","question","10.0"])
	}
	
	def "test for skip levels"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(CODE(1), ["en":"survey"], period)
		def program = newSurveyProgram(CODE(1), ["en":"program"], survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(CODE(1), ["en":"section"], program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = newSimpleQuestion(CODE(1), ["en":"question"], section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def type = Type.TYPE_NUMBER()
		def element = newSurveyElement(question, newRawDataElement(CODE(1), type))
		FormEnteredValue formEnteredValue = newFormEnteredValue(element, period, DataLocation.findByCode(BUTARO), v("10"))		
		Map<SurveyElement, FormEnteredValue> surveyElementValueMap = new HashMap<SurveyElement, FormEnteredValue>()
		surveyElementValueMap.put(formEnteredValue.getFormElement(), formEnteredValue)
		
		when:
		List<SurveyExportDataPoint> dataPoints = surveyExportService.getSurveyExportDataPoints(DataLocation.findByCode(BUTARO), survey, program, section, question, surveyElementValueMap)
	
		then:
		dataPoints.size() == 1		
		!dataPoints.get(0).equals(["survey",NATIONAL,NORTH,BURERA,SECTOR,BUTARO,DISTRICT_HOSPITAL_GROUP,"program","section","SIMPLE","NUMBER","question","10.0"])
		dataPoints.get(0).equals(["survey",NORTH,BURERA,BUTARO,DISTRICT_HOSPITAL_GROUP,"program","section","SIMPLE","NUMBER","question","10.0"])
	}
	
	def "test for simple question with multiple list headers"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(CODE(1), ["en":"survey"], period)
		def program = newSurveyProgram(CODE(1), ["en":"program"], survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(CODE(1), ["en":"section"], program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = newSimpleQuestion(CODE(1), ["en":"question"], section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def type = Type.TYPE_LIST(Type.TYPE_MAP(["key1":Type.TYPE_NUMBER()]))
		def element = newSurveyElement(question, newRawDataElement(CODE(1), type), ['[_].key1':['en':'header1']])
		FormEnteredValue formEnteredValue = newFormEnteredValue(element, period, DataLocation.findByCode(BUTARO), type.getValue([['key1':10]]))
		Map<SurveyElement, FormEnteredValue> surveyElementValueMap = new HashMap<SurveyElement, FormEnteredValue>()
		surveyElementValueMap.put(formEnteredValue.getFormElement(), formEnteredValue)
		
		when:
		List<SurveyExportDataPoint> dataPoints = surveyExportService.getSurveyExportDataPoints(DataLocation.findByCode(BUTARO), survey, program, section, question, surveyElementValueMap)
		
		then:
		dataPoints.size() == 1
		dataPoints.get(0).equals(["survey",NORTH,BURERA,BUTARO,DISTRICT_HOSPITAL_GROUP,"program","section","SIMPLE","LIST","question", "10.0", "header1"])
//		dataPoints.get(0).equals(["survey",NORTH,BURERA,BUTARO,DISTRICT_HOSPITAL_GROUP,"program","section","SIMPLE","LIST","question",
//			"10.0", "Line 1", "header1"])
	}
	
	def "test for get zip file"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(CODE(1), ["en":"survey"], period)
		def program = newSurveyProgram(CODE(1), ["en":"program"], survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(CODE(1), ["en":"section"], program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = newSimpleQuestion(CODE(1), ["en":"question"], section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def type = Type.TYPE_NUMBER()
		def element = newSurveyElement(question, newRawDataElement(CODE(1), type))
		FormEnteredValue formEnteredValue = newFormEnteredValue(element, period, DataLocation.findByCode(BUTARO), v("10"))		
		Map<SurveyElement, FormEnteredValue> surveyElementValueMap = new HashMap<SurveyElement, FormEnteredValue>()
		surveyElementValueMap.put(formEnteredValue.getFormElement(), formEnteredValue)
		
		when:
		def file = surveyExportService.getSurveyExportFile("file", DataLocation.findByCode(BUTARO), section, program, survey)
		def zipFile = Utils.getZipFile(file, "file")
		
		then:
		zipFile.exists() == true
		zipFile.length() > 0
	}
	
	def "test for valid export filename"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(CODE(1), ["en":"survey"], period)
		def program = newSurveyProgram(CODE(1), ["en":"program"], survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(CODE(1), ["en":"section"], program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = newSimpleQuestion(CODE(1), ["en":"question"], section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def type = Type.TYPE_NUMBER()
		def element = newSurveyElement(question, newRawDataElement(CODE(1), type))
		FormEnteredValue formEnteredValue = newFormEnteredValue(element, period, DataLocation.findByCode(BUTARO), v("10"))		
		Map<SurveyElement, FormEnteredValue> surveyElementValueMap = new HashMap<SurveyElement, FormEnteredValue>()
		surveyElementValueMap.put(formEnteredValue.getFormElement(), formEnteredValue)
		
		when:
		def file = surveyExportService.getExportFilename(DataLocation.findByCode(BUTARO), section, program, survey)
		
		then:
		file.startsWith("CODE1_Butaro DH_")
	}
		
}
