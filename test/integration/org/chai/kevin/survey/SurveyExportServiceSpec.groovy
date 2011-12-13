package org.chai.kevin.survey

import org.chai.kevin.Organisation;
import org.chai.kevin.data.Type;
import org.chai.kevin.survey.export.SurveyExportData
import org.chai.kevin.survey.export.SurveyExportDataPoint
import org.chai.kevin.survey.validation.SurveyEnteredValue
import org.chai.kevin.util.Utils;
import org.hisp.dhis.organisationunit.OrganisationUnit;

class SurveyExportServiceSpec extends SurveyIntegrationTests {

	def surveyExportService
//	def surveyValueService
		
	def "test for export section"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(j(["en":"survey"]), period)
		def objective = newSurveyObjective(j(["en":"objective"]), survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(j(["en":"section"]), objective, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = newSimpleQuestion(j(["en":"question"]), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def type = Type.TYPE_NUMBER()
		def element = newSurveyElement(question, newRawDataElement(CODE(1), type))
		SurveyEnteredValue surveyEnteredValue = newSurveyEnteredValue(element, period, OrganisationUnit.findByName(BUTARO), v("10"))		
		Map<SurveyElement, SurveyEnteredValue> surveyElementValueMap = new HashMap<SurveyElement, SurveyEnteredValue>()
		surveyElementValueMap.put(surveyEnteredValue.getSurveyElement(), surveyEnteredValue)
		
		when:
		List<SurveyExportDataPoint> dataPoints = surveyExportService.getSurveyExportDataPoints(getOrganisation(BUTARO), survey, objective, section, question, surveyElementValueMap)
	
		then:
		dataPoints.size() == 1
		dataPoints.get(0).equals(["survey",NORTH,BURERA,BUTARO,DISTRICT_HOSPITAL_GROUP,"objective","section","SIMPLE","NUMBER","question","10.0"])
	}

	def "test for export objective"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(j(["en":"survey"]), period)
		def objective = newSurveyObjective(j(["en":"objective"]), survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(j(["en":"section"]), objective, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = newSimpleQuestion(j(["en":"question"]), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def type = Type.TYPE_NUMBER()
		def element = newSurveyElement(question, newRawDataElement(CODE(1), type))
		SurveyEnteredValue surveyEnteredValue = newSurveyEnteredValue(element, period, OrganisationUnit.findByName(BUTARO), v("10"))		
		Map<SurveyElement, SurveyEnteredValue> surveyElementValueMap = new HashMap<SurveyElement, SurveyEnteredValue>()
		surveyElementValueMap.put(surveyEnteredValue.getSurveyElement(), surveyEnteredValue)
		
		when:
		List<SurveyExportDataPoint> dataPoints = surveyExportService.getSurveyExportDataPoints(getOrganisation(BUTARO), survey, objective, section, question, surveyElementValueMap)
	
		then:
		dataPoints.size() == 1
		dataPoints.get(0).equals(["survey",NORTH,BURERA,BUTARO,DISTRICT_HOSPITAL_GROUP,"objective","section","SIMPLE","NUMBER","question","10.0"])
	}

	def "test for export survey"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(j(["en":"survey"]), period)
		def objective = newSurveyObjective(j(["en":"objective"]), survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(j(["en":"section"]), objective, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = newSimpleQuestion(j(["en":"question"]), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def type = Type.TYPE_NUMBER()
		def element = newSurveyElement(question, newRawDataElement(CODE(1), type))
		SurveyEnteredValue surveyEnteredValue = newSurveyEnteredValue(element, period, OrganisationUnit.findByName(BUTARO), v("10"))		
		Map<SurveyElement, SurveyEnteredValue> surveyElementValueMap = new HashMap<SurveyElement, SurveyEnteredValue>()
		surveyElementValueMap.put(surveyEnteredValue.getSurveyElement(), surveyEnteredValue)
		
		when:
		List<SurveyExportDataPoint> dataPoints = surveyExportService.getSurveyExportDataPoints(getOrganisation(BUTARO), survey, objective, section, question, surveyElementValueMap)
	
		then:
		dataPoints.size() == 1
		dataPoints.get(0).equals(["survey",NORTH,BURERA,BUTARO,DISTRICT_HOSPITAL_GROUP,"objective","section","SIMPLE","NUMBER","question","10.0"])
	}
	
	def "test for skip levels"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(j(["en":"survey"]), period)
		def objective = newSurveyObjective(j(["en":"objective"]), survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(j(["en":"section"]), objective, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = newSimpleQuestion(j(["en":"question"]), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def type = Type.TYPE_NUMBER()
		def element = newSurveyElement(question, newRawDataElement(CODE(1), type))
		SurveyEnteredValue surveyEnteredValue = newSurveyEnteredValue(element, period, OrganisationUnit.findByName(BUTARO), v("10"))		
		Map<SurveyElement, SurveyEnteredValue> surveyElementValueMap = new HashMap<SurveyElement, SurveyEnteredValue>()
		surveyElementValueMap.put(surveyEnteredValue.getSurveyElement(), surveyEnteredValue)
		
		when:
		List<SurveyExportDataPoint> dataPoints = surveyExportService.getSurveyExportDataPoints(getOrganisation(BUTARO), survey, objective, section, question, surveyElementValueMap)
	
		then:
		dataPoints.size() == 1		
		!dataPoints.get(0).equals(["survey",COUNTRY,NORTH,BURERA,SECTOR,BUTARO,DISTRICT_HOSPITAL_GROUP,"objective","section","SIMPLE","NUMBER","question","10.0"])
		dataPoints.get(0).equals(["survey",NORTH,BURERA,BUTARO,DISTRICT_HOSPITAL_GROUP,"objective","section","SIMPLE","NUMBER","question","10.0"])
	}
	
	def "test for simple question with multiple list headers"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(j(["en":"survey"]), period)
		def objective = newSurveyObjective(j(["en":"objective"]), survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(j(["en":"section"]), objective, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = newSimpleQuestion(j(["en":"question"]), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def type = Type.TYPE_LIST(Type.TYPE_MAP(["key1":Type.TYPE_NUMBER()]))
		def element = newSurveyElement(question, newRawDataElement(CODE(1), type), ['[_].key1':j(['en':'header1'])])
		SurveyEnteredValue surveyEnteredValue = newSurveyEnteredValue(element, period, OrganisationUnit.findByName(BUTARO), type.getValue([['key1':10]]))
		Map<SurveyElement, SurveyEnteredValue> surveyElementValueMap = new HashMap<SurveyElement, SurveyEnteredValue>()
		surveyElementValueMap.put(surveyEnteredValue.getSurveyElement(), surveyEnteredValue)
		
		when:
		List<SurveyExportDataPoint> dataPoints = surveyExportService.getSurveyExportDataPoints(getOrganisation(BUTARO), survey, objective, section, question, surveyElementValueMap)
		
		then:
		dataPoints.size() == 1
		dataPoints.get(0).equals(["survey",NORTH,BURERA,BUTARO,DISTRICT_HOSPITAL_GROUP,"objective","section","SIMPLE","LIST","question",
			"10.0", "header1"])
	}
	
	def "test for get zip file"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = newSurvey(j(["en":"survey"]), period)
		def objective = newSurveyObjective(j(["en":"objective"]), survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(j(["en":"section"]), objective, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = newSimpleQuestion(j(["en":"question"]), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def type = Type.TYPE_NUMBER()
		def element = newSurveyElement(question, newRawDataElement(CODE(1), type))
		SurveyEnteredValue surveyEnteredValue = newSurveyEnteredValue(element, period, OrganisationUnit.findByName(BUTARO), v("10"))		
		Map<SurveyElement, SurveyEnteredValue> surveyElementValueMap = new HashMap<SurveyElement, SurveyEnteredValue>()
		surveyElementValueMap.put(surveyEnteredValue.getSurveyElement(), surveyEnteredValue)
		
		when:
		def file = surveyExportService.getSurveyExportFile("file", getOrganisation(BUTARO), section, objective, survey)
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
		def objective = newSurveyObjective(j(["en":"objective"]), survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = newSurveySection(j(["en":"section"]), objective, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = newSimpleQuestion(j(["en":"question"]), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def type = Type.TYPE_NUMBER()
		def element = newSurveyElement(question, newRawDataElement(CODE(1), type))
		SurveyEnteredValue surveyEnteredValue = newSurveyEnteredValue(element, period, OrganisationUnit.findByName(BUTARO), v("10"))		
		Map<SurveyElement, SurveyEnteredValue> surveyElementValueMap = new HashMap<SurveyElement, SurveyEnteredValue>()
		surveyElementValueMap.put(surveyEnteredValue.getSurveyElement(), surveyEnteredValue)
		
		when:
		def file = surveyExportService.getExportFilename(getOrganisation(BUTARO), section, objective, survey)
		
		then:
		file.startsWith("section_ButaroDH_")
	}
		
}
