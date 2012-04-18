package org.chai.kevin.survey

import org.chai.kevin.data.Type;
import org.chai.kevin.form.FormEnteredValue;
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.Location;
import org.chai.kevin.survey.export.SurveyExportData
import org.chai.kevin.survey.export.SurveyExportDataPoint
import org.chai.kevin.util.Utils;

class SurveyExportServiceSpec extends SurveyIntegrationTests {

	def surveyExportService
//	def surveyValueService
		
	def "test for export section"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(j(["en":"survey"]), period)
		def program = newSurveyProgram(j(["en":"program"]), survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(j(["en":"section"]), program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = newSimpleQuestion(j(["en":"question"]), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
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

	def "test for export program"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(j(["en":"survey"]), period)
		def program = newSurveyProgram(j(["en":"program"]), survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(j(["en":"section"]), program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = newSimpleQuestion(j(["en":"question"]), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
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
		def survey = newSurvey(j(["en":"survey"]), period)
		def program = newSurveyProgram(j(["en":"program"]), survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(j(["en":"section"]), program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = newSimpleQuestion(j(["en":"question"]), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
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
		def dataLocation = newDataLocation(j(["en":"Test"]), "TEST", Location.findByCode(NORTH), DataLocationType.findByCode(HEALTH_CENTER_GROUP))
		def period = newPeriod()
		def survey = newSurvey(j(["en":"survey"]), period)
		def program = newSurveyProgram(j(["en":"program"]), survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(j(["en":"section"]), program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = newSimpleQuestion(j(["en":"question"]), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
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
		def survey = newSurvey(j(["en":"survey"]), period)
		def program = newSurveyProgram(j(["en":"program"]), survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(j(["en":"section"]), program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = newSimpleQuestion(j(["en":"question"]), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
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
		def survey = newSurvey(j(["en":"survey"]), period)
		def program = newSurveyProgram(j(["en":"program"]), survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(j(["en":"section"]), program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = newSimpleQuestion(j(["en":"question"]), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def type = Type.TYPE_LIST(Type.TYPE_MAP(["key1":Type.TYPE_NUMBER()]))
		def element = newSurveyElement(question, newRawDataElement(CODE(1), type), ['[_].key1':j(['en':'header1'])])
		FormEnteredValue formEnteredValue = newFormEnteredValue(element, period, DataLocation.findByCode(BUTARO), type.getValue([['key1':10]]))
		Map<SurveyElement, FormEnteredValue> surveyElementValueMap = new HashMap<SurveyElement, FormEnteredValue>()
		surveyElementValueMap.put(formEnteredValue.getFormElement(), formEnteredValue)
		
		when:
		List<SurveyExportDataPoint> dataPoints = surveyExportService.getSurveyExportDataPoints(DataLocation.findByCode(BUTARO), survey, program, section, question, surveyElementValueMap)
		
		then:
		dataPoints.size() == 1
		dataPoints.get(0).equals(["survey",NORTH,BURERA,BUTARO,DISTRICT_HOSPITAL_GROUP,"program","section","SIMPLE","LIST","question",
			"10.0", "Line 1", "header1"])
	}
	
	def "test for get zip file"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(j(["en":"survey"]), period)
		def program = newSurveyProgram(j(["en":"program"]), survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(j(["en":"section"]), program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = newSimpleQuestion(j(["en":"question"]), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
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
		def survey = newSurvey(j(["en":"survey"]), period)
		def program = newSurveyProgram(j(["en":"program"]), survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(j(["en":"section"]), program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = newSimpleQuestion(j(["en":"question"]), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def type = Type.TYPE_NUMBER()
		def element = newSurveyElement(question, newRawDataElement(CODE(1), type))
		FormEnteredValue formEnteredValue = newFormEnteredValue(element, period, DataLocation.findByCode(BUTARO), v("10"))		
		Map<SurveyElement, FormEnteredValue> surveyElementValueMap = new HashMap<SurveyElement, FormEnteredValue>()
		surveyElementValueMap.put(formEnteredValue.getFormElement(), formEnteredValue)
		
		when:
		def file = surveyExportService.getExportFilename(DataLocation.findByCode(BUTARO), section, program, survey)
		
		then:
		file.startsWith("section_ButaroDH_")
	}
		
}
