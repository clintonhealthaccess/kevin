package org.chai.init

import org.chai.kevin.Period;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.form.FormValidationRule;
import org.chai.kevin.survey.Survey;
import org.chai.kevin.survey.SurveyCheckboxOption;
import org.chai.kevin.survey.SurveyCheckboxQuestion;
import org.chai.kevin.survey.SurveyElement;
import org.chai.kevin.survey.SurveyProgram;
import org.chai.kevin.survey.SurveyQuestion;
import org.chai.kevin.survey.SurveySection;
import org.chai.kevin.survey.SurveySimpleQuestion;
import org.chai.kevin.survey.SurveySkipRule;
import org.chai.kevin.survey.SurveyTableColumn;
import org.chai.kevin.survey.SurveyTableQuestion;
import org.chai.kevin.survey.SurveyTableRow;
import org.chai.kevin.util.Utils;

class SurveyInitializer {

	static def createSurveys() {
		if (!Survey.count()) {
			new Survey(code: 'survey_period1', names_en: 'Survey 2005',  period: Period.findByCode('period2'), active: false).save(failOnError: true)
			new Survey(code: 'survey_period2', names_en: 'Survey 2006',  period: Period.findByCode('period2'), lastPeriod: Period.findByCode('period1'), active: true).save(failOnError: true)
		}
	}
	
	static def createSurveyPrograms() {
		if (!SurveyProgram.count()) {
			// survey_period2
			[	new SurveyProgram(code: 'human_resources', names_en: 'Human Resources', order: 1, typeCodes: ['district_hospital', 'health_center']),
				new SurveyProgram(code: 'geographical_access', names_en: 'Geographical Access', order: 2, typeCodes: ['district_hospital', 'health_center']),
				new SurveyProgram(code: 'service_delivery', names_en: 'Service Delivery', order: 3, typeCodes: ['district_hospital', 'health_center']),
			].each {Survey.findByCode('survey_period2').addToPrograms(it).save(failOnError: true, flush: true)}
		} 
	}
	
	static def createSurveySections() {
		if (!SurveySection.count()) {
			// survey_period2 - service_delivery
			[	new SurveySection(code: 'core_identifiers', names_en: 'Core Facility Identifiers', order: 1, typeCodes: ['district_hospital', 'health_center']),
				new SurveySection(code: 'mch', names_en: 'Maternal & Child Health', order: 2, typeCodes: ['district_hospital', 'health_center']),
			].each {SurveyProgram.findByCode('service_delivery').addToSections(it).save(failOnError: true, flush: true)}
			
			// survey_period2 - geographical_access
			[	new SurveySection(code: 'energy', names_en: 'Energy', order: 1, typeCodes: ['district_hospital', 'health_center']),
				new SurveySection(code: 'water', names_en: 'Water', order: 2, typeCodes: ['district_hospital', 'health_center']),
			].each {SurveyProgram.findByCode('geographical_access').addToSections(it).save(failOnError: true, flush: true)}

			// survey_period2 - human_resources
			[	new SurveySection(code: 'staffing', names_en: 'Staffing', order: 1, typeCodes: ['district_hospital', 'health_center']),
			].each {SurveyProgram.findByCode('human_resources').addToSections(it).save(failOnError: true, flush: true)}
		}
	}
	
	static def createSurveyQuestions() {
		if (!SurveyQuestion.count()) {
			// survey_period2 - service_delivery - core_identifiers
			[	new SurveySimpleQuestion(code: 'catchment_area', names_en: 'What is the population of the catchment area',
					order: 1, typeCodes: ['district_hospital', 'health_center'])
				.addToSurveyElements(new SurveyElement(code:'catchment_area', dataElement: RawDataElement.findByCode('catchment_area')))
			].each {SurveySection.findByCode('core_identifiers').addToQuestions(it).save(failOnError: true, flush: true)}
			
			// survey_period2 - service_delivery - mch
			[	new SurveySimpleQuestion(code: 'prenuptial_consultation', names_en: 'Does this facility provide pre-nuptial consultations?',
					order: 1, typeCodes: ['district_hospital', 'health_center'])
				.addToSurveyElements(new SurveyElement(dataElement: RawDataElement.findByCode('prenuptial_consultation'))),
				new SurveySimpleQuestion(code: 'in_facility_birth', names_en: 'How many births took place in this facility?',
					order: 2, typeCodes: ['district_hospital', 'health_center'])
				.addToSurveyElements(new SurveyElement(code: 'in_facility_birth', dataElement: RawDataElement.findByCode('in_facility_birth'))),
				new SurveySimpleQuestion(code: 'out_facility_birth', names_en: 'How many births in this catchment area occurred outside of this facility?',
					order: 3, typeCodes: ['district_hospital', 'health_center'])
				.addToSurveyElements(new SurveyElement(code: 'out_facility_birth', dataElement: RawDataElement.findByCode('out_facility_birth')))
			].each {SurveySection.findByCode('mch').addToQuestions(it).save(failOnError: true, flush: true)}
			
			// survey_period2 - geographical_access - energy
			[	new SurveySimpleQuestion(code: 'energy_source', names_en: 'What is the source of energy at this facility',
					order: 1, typeCodes: ['district_hospital', 'health_center'])
				.addToSurveyElements(new SurveyElement(code:'energy_source', dataElement: RawDataElement.findByCode('energy_source'))),
				new SurveySimpleQuestion(code: 'energy_needs_covered', names_en: 'What is the source of energy at this facility',
					order: 1, typeCodes: ['district_hospital', 'health_center'])
				.addToSurveyElements(new SurveyElement(code:'energy_needs_covered', dataElement: RawDataElement.findByCode('energy_needs_covered')))
			].each {SurveySection.findByCode('energy').addToQuestions(it).save(failOnError: true, flush: true)}
			
			// survey_period2 - geographical_access - water
			[	new SurveyCheckboxQuestion(code: 'rainwater_harvesting', names_en: 'Which of the following rainwater harvesting structures are in place?',
					order: 1, typeCodes: ['district_hospital', 'health_center'], section: SurveySection.findByCode('water'))
				.addToSurveyElements(new SurveyElement(code: 'rainwater_pipes', dataElement: RawDataElement.findByCode('rainwater_pipes')))
				.addToSurveyElements(new SurveyElement(code: 'rainwater_tanks', dataElement: RawDataElement.findByCode('rainwater_tanks')))
				.addToSurveyElements(new SurveyElement(code: 'rainwater_gutters', dataElement: RawDataElement.findByCode('rainwater_gutters')))
				.addToSurveyElements(new SurveyElement(code: 'rainwater_none', dataElement: RawDataElement.findByCode('rainwater_none'))).save(failOnError: true, flush: true)			
				.addToOptions(new SurveyCheckboxOption(code: 'rainwater_pipes', names_en: 'Pipes', order: 1, surveyElement: SurveyElement.findByCode('rainwater_pipes'), typeCodes: ['district_hospital', 'health_center']))
				.addToOptions(new SurveyCheckboxOption(code: 'rainwater_tanks', names_en: 'Tank(s)', order: 2, surveyElement: SurveyElement.findByCode('rainwater_tanks'), typeCodes: ['district_hospital', 'health_center']))
				.addToOptions(new SurveyCheckboxOption(code: 'rainwater_gutters', names_en: 'Gutters', order: 3, surveyElement: SurveyElement.findByCode('rainwater_gutters'), typeCodes: ['district_hospital', 'health_center']))
				.addToOptions(new SurveyCheckboxOption(code: 'rainwater_none', names_en: 'No Structures, N/A', order: 4, surveyElement: SurveyElement.findByCode('rainwater_none'), typeCodes: ['district_hospital', 'health_center'])),
				
				new SurveySimpleQuestion(code: 'has_water', names_en: 'Is water available at this facility?',
					order: 2, typeCodes: ['district_hospital', 'health_center'])
				.addToSurveyElements(new SurveyElement(code:'has_water', dataElement: RawDataElement.findByCode('has_water'))),
				
				new SurveySimpleQuestion(code: 'water_sources', names_en: 'Please list the details of all water sources used by this factility.',
					order: 2, typeCodes: ['district_hospital', 'health_center'])
				.addToSurveyElements(new SurveyElement(code:'water_sources', dataElement: RawDataElement.findByCode('water_sources'), headers: [
					'[_].water_source': ['en': 'Water Source'],
					'[_].description': ['en': 'Description'],
					'[_].identifiers': ['en': 'Identifiers'],
					'[_].identifiers.percent_needs_covered': ['en': 'Needs covered in %']
				])),
			].each {SurveySection.findByCode('water').addToQuestions(it).save(failOnError: true, flush: true)}
			
			// survey_period2 - human_resources - staffing
			[	new SurveySimpleQuestion(code: 'staff', names_en: 'Please list the details of all the staff at this factility.',
					order: 1, typeCodes: ['district_hospital', 'health_center'])
				.addToSurveyElements(new SurveyElement(code:'human_resources', dataElement: RawDataElement.findByCode('human_resources'), headers: [
					'[_].family_name': ['en': 'Family Name'],
					'[_].given_name': ['en': 'Given Name'],
					'[_].birth_date': ['en': 'Birth Date'],
					'[_].personal_information': ['en': 'Personal Information'],
					'[_].personal_information.sex': ['en': 'Sex'],
					'[_].personal_information.nationality': ['en': 'Nationality'],
					'[_].personal_information.age': ['en': 'Age'],
					'[_].work_history': ['en': 'Work History'],
					'[_].work_history.primary_function': ['en': 'Primary Function'],
					'[_].work_history.departments_served': ['en': 'Departments served'],
					'[_].work_history.departments_served.administration': ['en': 'This is an administrative function'],
				])),
			
				new SurveyTableQuestion(code: 'support_staff', names_en: 'Provide the following information about the support staff.', order: 2, 
					typeCodes: ['district_hospital', 'health_center'], section: SurveySection.findByCode('staffing'))
				.addToSurveyElements(new SurveyElement(code: 'number_of_cooks', dataElement: RawDataElement.findByCode('number_of_cooks')))
				.addToSurveyElements(new SurveyElement(code: 'needed_number_of_cooks', dataElement: RawDataElement.findByCode('needed_number_of_cooks')))
				.addToSurveyElements(new SurveyElement(code: 'number_of_technicians', dataElement: RawDataElement.findByCode('number_of_technicians')))
				.addToSurveyElements(new SurveyElement(code: 'needed_number_of_technicians', dataElement: RawDataElement.findByCode('needed_number_of_technicians'))).save(failOnError: true, flush: true)
				.addToColumns(new SurveyTableColumn(code: 'actual_number', names_en: 'Actual Number', order: 1, typeCodes: ['district_hospital', 'health_center']))
				.addToColumns(new SurveyTableColumn(code: 'needed_number', names_en: 'Needed Number', order: 2, typeCodes: ['district_hospital', 'health_center'])).save(failOnError: true, flush: true)
				.addToRows(new SurveyTableRow(code: 'cooks', names_en: 'Cooks', order: 1, typeCodes: ['district_hospital', 'health_center'], surveyElements: 
					[	(SurveyTableColumn.findByCode('actual_number')): SurveyElement.findByCode('number_of_cooks'),
						(SurveyTableColumn.findByCode('needed_number')): SurveyElement.findByCode('needed_number_of_cooks')]))
				.addToRows(new SurveyTableRow(code: 'technicians', names_en: 'Technicians', order: 2, typeCodes: ['district_hospital', 'health_center'], surveyElements:
					[	(SurveyTableColumn.findByCode('actual_number')): SurveyElement.findByCode('number_of_technicians'),
						(SurveyTableColumn.findByCode('needed_number')): SurveyElement.findByCode('needed_number_of_technicians')]))
			
			].each {SurveySection.findByCode('staffing').addToQuestions(it).save(failOnError: true, flush: true)}
 		}
	}
	
	static def createValidationRules() {
		if (!FormValidationRule.count()) {
			// in_facility_birth
			[	new FormValidationRule(code: 'in_facility_birth_not_bigger_than_catchment_area', expression: '\$'+SurveyElement.findByCode('in_facility_birth').id+' < \$'+SurveyElement.findByCode('catchment_area').id,
				prefix: '', allowOutlier: true, messages_en: 'The number is bigger than the size of the catchment area.', typeCodes: ['district_hospital', 'health_center'])
			].each {SurveyElement.findByCode('in_facility_birth').addToValidationRules(it).save(failOnError: true, flush: true)}
			
			// out_facility_birth
			[	new FormValidationRule(code: 'out_facility_birth_not_bigger_than_catchment_area', expression: '\$'+SurveyElement.findByCode('out_facility_birth').id+' < \$'+SurveyElement.findByCode('catchment_area').id,
				prefix: '', allowOutlier: true, messages_en: 'The number is bigger than the size of the catchment area.', typeCodes: ['district_hospital', 'health_center'])
			].each {SurveyElement.findByCode('out_facility_birth').addToValidationRules(it).save(failOnError: true, flush: true)}
			
			// rainwater_harvesting
			[	new FormValidationRule(code: 'rainwater_harvesting_none_selected', expression: 
				'not \$'+SurveyElement.findByCode('rainwater_none').id+' or (not \$'+SurveyElement.findByCode('rainwater_pipes').id+' and not \$'+SurveyElement.findByCode('rainwater_tanks').id+' and not \$'+SurveyElement.findByCode('rainwater_gutters').id+')',
				prefix: '', allowOutlier: true, messages_en: 'You cannot select other options if none is checked.', typeCodes: ['district_hospital', 'health_center'])
			].each {SurveyElement.findByCode('rainwater_none').addToValidationRules(it).save(failOnError: true, flush: true)}
		}
	}
	
	static def createSurveySkipRules() {
		if (!SurveySkipRule.count()) {
			[ // water skip sources
				new SurveySkipRule(code: 'skip_water_sources_no_water', expression: 'not \$'+SurveyElement.findByCode('has_water').id)
					.addToSkippedSurveyQuestions(SurveyQuestion.findByCode('water_sources')),
					
				new SurveySkipRule(code: 'skip_description_not_other', expression: '\$'+SurveyElement.findByCode('water_sources').id+'[_].water_source != "other"',
					skippedFormElements: [(SurveyElement.findByCode('water_sources')): '[_].description'])
					
				// energy source
			].each {Survey.findByCode('survey_period2').addToSkipRules(it).save(failOnError: true, flush: true)}
		}
	}
	
//	static def createQuestionaire(){
//		if(!Survey.count()){
//
//			def dh = DataLocationType.findByCode("District Hospital")
//			def hc = DataLocationType.findByCode("Health Center")
//
//			//Creating Survey
//			def surveyOne = new Survey(
//					code:"Survey1",
//					names: j(["en":"Survey Number 1"]),
//					descriptions: j(["en":"Survey Number 1 Description"]),
//					period: Period.list([cache: true])[1],
//					lastPeriod: Period.list([cache: true])[0],
//					active: true,
//					)
//			def surveyTwo = new Survey(
//					code:"Survey2",
//					names: j(["en":"Survey Number 2"]),
//					descriptions: j(["en":"Survey Number 2 Description"]),
//					period: Period.list([cache: true])[1],
//					)
//
//			//Creating Program
//			def serviceDev = new SurveyProgram(
//				code:"Program1",
//				names: j(["en":"Service Delivery"]),
//				order: 2,
//				typeCodeString: "District Hospital,Health Center"
//			)
//			def hResourceHealth = new SurveyProgram(
//				code:"Program2",
//				names: j(["en":"Human Resources for Health"]),
//				order: 4,
//				typeCodeString: "District Hospital,Health Center",
//			)
//
//			def geoAccess = new SurveyProgram(
//				code:"Program3",
//				names: j(["en":"Geographic Access"]),
//				order: 5,
//				typeCodeString: "District Hospital,Health Center",
//			)
//
//			def institutCap = new SurveyProgram(
//				code:"Program4",
//				names: j(["en":"Institutional Capacity"]),
//				order: 3,
//				typeCodeString: "Health Center",
//			)
//
//			def coreFacId = new SurveyProgram(
//				code:"Program5",
//				names: j(["en":"Core Facility Identify"]),
//				order: 1,
//				typeCodeString: "District Hospital,Health Center",
//			)
//
//			def finance = new SurveyProgram(
//				code:"Program6",
//				names: j(["en":"Finance"]),
//				order: 6,
//				typeCodeString: "District Hospital,Health Center",
//			)
//
//			def dvandC = new SurveyProgram(
//				code:"Program7",
//				names: j(["en":"Drugs, Vaccines, and Consumables"]),
//				order: 7,
//				typeCodeString: "District Hospital,Health Center",
//			)
//
//
//			surveyOne.addProgram(serviceDev)
//			surveyOne.addProgram(coreFacId)
//			surveyOne.addProgram(hResourceHealth)
//			surveyOne.addProgram(finance)
//			surveyOne.save(failOnError:true)
//
//			surveyTwo.addProgram(geoAccess)
//			surveyTwo.addProgram(dvandC)
//			surveyTwo.addProgram(institutCap)
//			surveyTwo.save(failOnError:true)
//
//			//Adding section to program
//			def facilityId = new SurveySection(
//				code:"Section1",
//				names: j(["en":"Facility Identifier"]),
//				order: 1,
//				program: coreFacId,
//				typeCodeString: "District Hospital,Health Center"
//			)
//
//			coreFacId.addSection(facilityId)
//			coreFacId.save(failOnError:true);
//
//			def services=new SurveySection(
//				code:"Section2",
//				names: j(["en":"Services"]),
//				order: 2,
//				program: serviceDev,
//				typeCodeString: "District Hospital,Health Center"
//			)
//			def labTests= new SurveySection(
//				code:"Section3",
//				names: j(["en":"Lab Tests"]),
//				order: 1,
//				program: serviceDev,
//				typeCodeString: "District Hospital"
//			)
//
//			def patientReg=new SurveySection(
//				code:"Section4",
//				names: j(["en":"Patient Registration"]),
//				order: 3,
//				program: serviceDev,
//				typeCodeString: "District Hospital,Health Center"
//			)
//
//			def patientQ1 = new SurveySimpleQuestion(
//				code:"Question1",
//				names: j(["en":"Patient Section Simple Question NUMBER"]),
//				descriptions: j([:]),
//				order: 3,
//				typeCodeString: "District Hospital,Health Center"
//			)
//			patientReg.addQuestion(patientQ1)
//			patientReg.save(failOnError: true)
//
//			def surveyElementPatientQ1 = new SurveyElement(dataElement: RawDataElement.findByCode("CODE1"), surveyQuestion: patientQ1).save(failOnError: true)
//			patientQ1.surveyElement = surveyElementPatientQ1
//			patientQ1.save(failOnError: true)
//
//			serviceDev.addSection(services)
//			serviceDev.addSection(labTests)
//			serviceDev.addSection(patientReg)
//			serviceDev.save(failOnError:true);
//
//			def rulePatient1 = new FormValidationRule(
//				formElement: surveyElementPatientQ1,
//				expression: "\$"+surveyElementPatientQ1.id+" > 100",
//				messages: j(["en":"Validation error {0,here}"]),
//				dependencies: [surveyElementPatientQ1],
//				typeCodeString: "District Hospital,Health Center",
//				allowOutlier: false
//			).save(failOnError: true)
//			def rulePatient2 = new FormValidationRule(
//				formElement: surveyElementPatientQ1,
//				expression: "\$"+surveyElementPatientQ1.id+" > 140",
//				messages: j(["en":"Validation error {0,here}"]),
//				dependencies: [surveyElementPatientQ1],
//				typeCodeString: "District Hospital,Health Center",
//				allowOutlier: true
//			).save(failOnError: true)
//			
//			surveyElementPatientQ1.addValidationRule(rulePatient1)
//			surveyElementPatientQ1.addValidationRule(rulePatient2)
//			surveyElementPatientQ1.save(failOnError: true)
//
//			def staffing=new SurveySection(
//				code:"Section5",
//				names: j(["en":"Staffing"]),
//				order: 1,
//				program: hResourceHealth,
//				typeCodeString: "District Hospital,Health Center"
//			)
//
//			def continuingEd = new SurveySection(
//				code:"Section6",
//				names: j(["en":"Continuing Education"]),
//				order: 2,
//				program: hResourceHealth,
//				typeCodeString: "Health Center"
//			)
//
//			def openResponse = new SurveySection(
//				code:"Section7",
//				names: j(["en":"Open Response"]),
//				order: 3,
//				program: hResourceHealth,
//				typeCodeString: "District Hospital,Health Center"
//			)
//
//
//			hResourceHealth.addSection(staffing)
//			hResourceHealth.addSection(continuingEd)
//			hResourceHealth.addSection(openResponse)
//			hResourceHealth.save(failOnError:true);
//
//			def infrastructure = new SurveySection(
//				code:"Section8",
//				names: j(["en":"Infrastructure"]),
//				order: 3,
//				program: geoAccess,
//				typeCodeString: "District Hospital,Health Center"
//			)
//			def medicalEq=new SurveySection(
//				code:"Section9",
//				names: j(["en":"Medical Equipment"]),
//				order: 2,
//				program: geoAccess,
//				typeCodeString: "District Hospital,Health Center"
//			)
//			def wasteMgmnt=new SurveySection(
//				code:"Section10",
//				names: j(["en":"Waste Management"]),
//				order: 1,
//				program: geoAccess,
//				typeCodeString: "District Hospital,Health Center"
//			)
//
//
//			geoAccess.addSection(infrastructure)
//			geoAccess.addSection(medicalEq)
//			geoAccess.addSection(wasteMgmnt)
//			geoAccess.save(failOnError:true);
//
//			//Adding questions to sections
//			def serviceQ1 = new SurveySimpleQuestion(
//				code:"Question2",
//				names: j(["en":"Service Section Simple Question NUMBER"]),
//				descriptions: j(["en":"<br/>"]),
//				order: 3,
//				typeCodeString: "District Hospital,Health Center"
//			)
//
//			services.addQuestion(serviceQ1)
//			services.save(failOnError:true, flush:true)
//
//			def surveyElementServiceQ1 = new SurveyElement(dataElement: RawDataElement.findByCode("CODE1"), surveyQuestion: serviceQ1).save(failOnError: true)
//			serviceQ1.surveyElement = surveyElementServiceQ1
//			serviceQ1.save(failOnError: true)
//
//			def serviceQ2 = new SurveySimpleQuestion(
//				code:"Question3",
//				names: j(["en":"Service Section Simple Question BOOL"]),
//				descriptions: j(["en":""]),
//				order: 0,
//				typeCodeString: "District Hospital,Health Center"
//			)
//
//			services.addQuestion(serviceQ2)
//			services.save(failOnError:true, flush:true)
//
//			def surveyElementServiceQ2 = new SurveyElement(dataElement: RawDataElement.findByCode("CODE7"), surveyQuestion: serviceQ2).save(failOnError: true)
//			serviceQ2.surveyElement = surveyElementServiceQ2
//			serviceQ2.save(failOnError: true)
//
//			def serviceQ3 = new SurveySimpleQuestion(
//				code:"Question4",
//				names: j(["en":"Service Section Simple Question ENUM "]),
//				descriptions: j([:]),
//				order: 0,
//				typeCodeString: "District Hospital,Health Center"
//			)
//
//			services.addQuestion(serviceQ3)
//			services.save(failOnError:true, flush:true)
//
//			def surveyElementServiceQ3 = new SurveyElement(dataElement: RawDataElement.findByCode("CODE3"), surveyQuestion: serviceQ3).save(failOnError: true)
//			serviceQ3.surveyElement = surveyElementServiceQ3
//			serviceQ3.save(failOnError: true)
//
////			def serviceQ4 = new SurveySimpleQuestion(
////					names: j(["en":"Service Section Simple Question LIST"]),
////					descriptions: j(["en":"Help text"]),
////  				order: o(["en":4]),
////					typeCodeString: "District Hospital,Health Center"
////					)
////			services.addQuestion(serviceQ4)
////			services.save(failOnError:true, flush:true)
//
//
//			//			def surveyElementServiceQ4 = new SurveyElement(dataElement: DataElement.findByCode("LIST1"), surveyQuestion: serviceQ4).save(failOnError: true)
//			//			serviceQ4.surveyElement = surveyElementServiceQ4
//			//			serviceQ4.save(failOnError: true)
//
//			def serviceQ5 = new SurveySimpleQuestion(
//					code:"Question5",
//					names: j(["en":"Service Section Simple Question MAP"]),
//					descriptions: j(["en":"<div>Help text</div>"]),
//					order: 5,
//
////					order: o(["en":5]),
//					typeCodeString: "District Hospital,Health Center"
//					)
//			services.addQuestion(serviceQ5)
//			services.save(failOnError:true, flush:true)
//
//			def surveyElementServiceQ5 = new SurveyElement(
//					dataElement: RawDataElement.findByCode("MAP1"),
//					surveyQuestion: serviceQ5,
//					headers: [
//						".key1.key11.key111":j(["en": "Header 1"])
//					]).save(failOnError: true)
//			serviceQ5.surveyElement = surveyElementServiceQ5
//			serviceQ5.save(failOnError: true)
//
//			def serviceQ6 = new SurveySimpleQuestion(
//					code:"Question6",
//					names: j(["en":"Service Section Simple Question LIST of MAP"]),
//					descriptions: j(["en":"Help text"]),
//					order: 6,
//
////					order: o(["en":6]),
//					typeCodeString: "District Hospital,Health Center"
//					)
//			services.addQuestion(serviceQ6)
//			services.save(failOnError:true, flush:true)
//
//			def surveyElementServiceQ6 = new SurveyElement(
//				dataElement: RawDataElement.findByCode("LISTMAP2"),
//				surveyQuestion: serviceQ6,
//				headers: [
//					"[_].key0": j(["en":"Name"]),
//					"[_].key0.key01": j(["en":"Select from list"]),
//					"[_].key0.key02": j(["en":"If other"]),
//					"[_].key1": j(["en":"Identifiers"]),
//					"[_].key1.key11": j(["en":"Type of equipment"]),
//					"[_].key1.key11.key111": j(["en":"Select from list"]),
//					"[_].key1.key11.key112": j(["en":"If other, specify:"]),
//					"[_].key1.key12": j(["en":"Description"]),
//					"[_].key1.key13": j(["en":"Serial"]),
//					"[_].key1.key13.key131": j(["en":"Select from list"]),
//					"[_].key1.key13.key132": j(["en":"If other, please specify"]),
//					"[_].key1.key14": j(["en":"Model"]),
//					"[_].key1.key15": j(["en":"Manufacturer"]),
//					"[_].key1.key16": j(["en":"Status"]),
//					"[_].key1.key16.key161": j(["en":"Please select from list:"]),
//					"[_].key1.key16.key162": j(["en":"If not fully functional:"]),
//					"[_].key1.key17": j(["en":"Primary location"]),
//					"[_].key1.key18": j(["en":"Avg. daily hours of use"]),
//					"[_].key2": j(["en":"Supply and Maintenance"]),
//					"[_].key2.key21": j(["en":"Supplier Name"]),
//					"[_].key2.key22": j(["en":"Supplier Type"]),
//					"[_].key2.key23": j(["en":"Supplier Mobile"]),
//					"[_].key2.key24": j(["en":"Date Acquired"]),
//					"[_].key2.key25": j(["en":"Service Provider Name"]),
//					"[_].key2.key26": j(["en":"Service Provider Type"]),
//					"[_].key2.key27": j(["en":"Service Provider Mobile"]),
//					"[_].key2.key28": j(["en":"Date of last repair"]),
//					"[_].key2.key29": j(["en":"Date of last service"])
//				]).save(failOnError: true)
//			
//			serviceQ6.surveyElement = surveyElementServiceQ6
//			serviceQ6.save(failOnError: true, flush: true)
//
//			services.addQuestion(serviceQ2)
//			services.addQuestion(serviceQ1)
//			services.addQuestion(serviceQ3)
//			//			services.addQuestion(serviceQ4)
//			services.addQuestion(serviceQ5)
//			services.addQuestion(serviceQ6)
//			services.save(failOnError:true)
//
//			def ruleQ6 = new FormValidationRule(
//				formElement: surveyElementServiceQ6,
//				prefix: "[_].key1.key18",
//				expression: "\$"+surveyElementServiceQ6.id+"[_].key1.key18 < 24",
//				messages: j(["en":"Validation error {0,here}"]),
//				dependencies: [surveyElementServiceQ6],
//				typeCodeString: "District Hospital,Health Center",
//				allowOutlier: false
//			).save(failOnError: true)
//
//			surveyElementServiceQ6.addValidationRule(ruleQ6)
//			surveyElementServiceQ6.save(failOnError: true)
//
//			def rule1 = new FormValidationRule(
//				formElement: surveyElementServiceQ1,
//				expression: "\$"+surveyElementServiceQ1.id+" > 100",
//				messages: j(["en":"Validation error {0,here}"]),
//				dependencies: [surveyElementServiceQ1],
//				typeCodeString: "District Hospital,Health Center",
//				allowOutlier: false
//			).save(failOnError: true)
//			def rule2 = new FormValidationRule(
//				formElement: surveyElementServiceQ1,
//				expression: "\$"+surveyElementServiceQ1.id+" > 140",
//				messages: j(["en":"Validation error {0,here}"]),
//				typeCodeString: "District Hospital,Health Center",
//				dependencies: [surveyElementServiceQ1],
//				allowOutlier: true
//			).save(failOnError: true)
//			surveyElementServiceQ1.addValidationRule(rule1)
//			surveyElementServiceQ1.addValidationRule(rule2)
//			surveyElementServiceQ1.save(failOnError: true)
//
//			def openQ = new SurveySimpleQuestion(
//					code: "Question9",
//					names: j(["en":"Sample Open Question Enter the cumulative number of training days spent on that module. To do so, add up all of the days spent by every person who participated in that module."]),
//					descriptions: j(["en":"Help text"]),
//					order: 1,
//
////					order: o(["en":1]),
//					typeCodeString: "District Hospital,Health Center"
//					)
//			openResponse.addQuestion(openQ)
//			openResponse.save(failOnError:true, flush: true)
//
//			def surveyElementOpenQ = new SurveyElement(dataElement: RawDataElement.findByCode("CODE12"), surveyQuestion: openQ).save(failOnError: true)
//			openQ.surveyElement = surveyElementOpenQ
//			openQ.save(failOnError: true)
//
//			def checkBoxQ = new SurveyCheckboxQuestion(
//					code: "Question10",
//					names: j(["en":"Service Section CheckBox Question"]),
//					descriptions: j(["en":"Help text"]),
//					order: 2,
//
////					order: o(["en":2]),
//					typeCodeString: "District Hospital,Health Center"
//					)
//			staffing.addQuestion(checkBoxQ)
//			staffing.save(failOnError:true, flush: true)
//
//			def surveyElementChecboxQ1 = new SurveyElement(dataElement: RawDataElement.findByCode("CODE4"), surveyQuestion: checkBoxQ).save(failOnError: true)
//			def surveyElementChecboxQ2 = new SurveyElement(dataElement: RawDataElement.findByCode("CODE5"), surveyQuestion: checkBoxQ).save(failOnError: true)
//			def surveyElementChecboxQ3 = new SurveyElement(dataElement: RawDataElement.findByCode("CODE6"), surveyQuestion: checkBoxQ).save(failOnError: true)
//
//			//Checkbox Option
//			def option1 = new SurveyCheckboxOption(
//					names: j(["en":"None Or Not Applicable"]),
//					order: 2,
////					order: o(["en":2]),
//					typeCodeString: "District Hospital,Health Center",
//					surveyElement: surveyElementChecboxQ1
//					)
//			def option2 = new SurveyCheckboxOption(
//					names: j(["en":"Second Option"]),
//					order: 1,
//
////					order: o(["en":1]),
//					typeCodeString: "District Hospital",
//					surveyElement: surveyElementChecboxQ2
//					)
//			def option3 = new SurveyCheckboxOption(
//					names: j(["en":"Third Option"]),
//					order: 3,
////					order: o(["en":3]),
//					typeCodeString: "District Hospital,Health Center",
//					surveyElement: surveyElementChecboxQ3
//					)
//			checkBoxQ.addOption(option1)
//			checkBoxQ.addOption(option2)
//			checkBoxQ.addOption(option3)
//			checkBoxQ.save(failOnError:true)
//
//
//			def staffingQ1 = new SurveySimpleQuestion(
//					code:"Question7",
//					names: j(["en":"List all of your staff"]),
//					descriptions: j(["en":"Help text"]),
//					order: 10,
////					order: o(["en":10]),
//					typeCodeString: "District Hospital,Health Center"
//					)
//			staffing.addQuestion(staffingQ1)
//			staffing.save(failOnError:true, flush:true)
//
//			def staffingElementQ1 = new SurveyElement(
//					dataElement: RawDataElement.findByCode("LISTMAP1"),
//					surveyQuestion: staffingQ1,
//					headers: [
//						"[_].key1":j(["en": "Family Name"]),
//						"[_].key2":j(["en": "Given Name"]),
//						"[_].key3":j(["en": "National ID Number"]),
//						"[_].key4":j(["en": "Date of Birth"]),
//						"[_].key5":j(["en": "Sex"]),
//						"[_].key6":j(["en": "Nationality"]),
//						"[_].key7":j(["en": "Education & Training"]),
//						"[_].key7.key71":j(["en": "Highest level of education"]),
//						"[_].key7.key72":j(["en": "Corresponding Institution"]),
//						"[_].key7.key73":j(["en": "Days of Training received between July 2010 and June 2011, by Area"]),
//						"[_].key7.key73.key731":j(["en": "Clinical"]),
//						"[_].key7.key73.key731.key731_1":j(["en": "HIV/AIDS"]),
//						"[_].key7.key73.key731.key731_2":j(["en": "Malaria"]),
//						"[_].key7.key73.key731.key731_3":j(["en": "Tuberculosis"]),
//						"[_].key7.key73.key731.key731_4":j(["en": "Diarrheal Diseases"]),
//						"[_].key7.key73.key731.key731_5":j(["en": "Other Infections & Parasitic Diseases"]),
//						"[_].key7.key73.key731.key731_6":j(["en": "Trauma & Burns"]),
//						"[_].key7.key73.key731.key731_7":j(["en": "Mental Health"]),
//						"[_].key7.key73.key731.key731_8":j(["en": "Environmental Health"]),
//						"[_].key7.key73.key731.key731_9":j(["en": "Internal Medicine"]),
//						"[_].key7.key73.key731.key731_10":j(["en": "Reproductive Health - Female"]),
//						"[_].key7.key73.key731.key731_11":j(["en": "Reproductive Health - Male"]),
//						"[_].key7.key73.key731.key731_12":j(["en": "Prenatal & Neonatal"]),
//						"[_].key7.key73.key731.key731_13":j(["en": "Oral Health"]),
//						"[_].key7.key73.key731.key731_14":j(["en": "Respiratory Health"]),
//						"[_].key7.key73.key731.key731_15":j(["en": "Nutrition"]),
//						"[_].key7.key73.key731.key731_16":j(["en": "Intestinal Health"]),
//						"[_].key7.key73.key731.key731_17":j(["en": "Cardiovascular Health"]),
//						"[_].key7.key73.key731.key731_18":j(["en": "Sexual Health (not HIV)"]),
//						"[_].key7.key73.key731.key731_19":j(["en": "Other"]),
//						"[_].key7.key73.key732":j(["en": "Non-Clinical"]),
//						"[_].key7.key73.key732.key732_1":j(["en": "Human Resources Management"]),
//						"[_].key7.key73.key732.key732_2":j(["en": "Facility Operations"]),
//						"[_].key7.key73.key732.key732_3":j(["en": "Clinical Supervision & Management"]),
//						"[_].key7.key73.key732.key732_4":j(["en": "Pharmacy Supervision & Management"]),
//						"[_].key7.key73.key732.key732_5":j(["en": "CHW Supervision & Management"]),
//						"[_].key7.key73.key732.key732_6":j(["en": "Laboratory Supervision & Management"]),
//						"[_].key7.key73.key732.key732_7":j(["en": "Administrative Procedures & Management"]),
//						"[_].key7.key73.key732.key732_8":j(["en": "Mutuelle Procedures & Protocol"]),
//						"[_].key7.key73.key732.key732_9":j(["en": "Claims Processing"]),
//						"[_].key7.key73.key732.key732_10":j(["en": "Performance Based Financing"]),
//						"[_].key7.key73.key732.key732_11":j(["en": "Health Economics"]),
//						"[_].key7.key73.key732.key732_12":j(["en": "Health Insurance"]),
//						"[_].key7.key73.key732.key732_13":j(["en": "ICT"]),
//						"[_].key7.key73.key732.key732_14":j(["en": "Basic Statistics / Analytics"]),
//						"[_].key7.key73.key732.key732_15":j(["en": "Presentation Techniques"]),
//						"[_].key7.key73.key732.key732_16":j(["en": "Communication Skills"]),
//						"[_].key8":j(["en": "Work History"]),
//						"[_].key8.key81":j(["en": "Primary Function"]),
//						"[_].key8.key82":j(["en": "Primary Department"]),
//						"[_].key8.key83":j(["en": "% of Time Spent on Primary Department"]),
//						"[_].key8.key84":j(["en": "Departments Served 1+ Day in a typical Week"]),
//						"[_].key8.key84.key84_1":j(["en": "Administration"]),
//						"[_].key8.key84.key84_2":j(["en": "Chronic Disease"]),
//						"[_].key8.key84.key84_3":j(["en": "Community Health"]),
//						"[_].key8.key84.key84_4":j(["en": "Dentistry"]),
//						"[_].key8.key84.key84_5":j(["en": "Emergency"]),
//						"[_].key8.key84.key84_6":j(["en": "Family Planning"]),
//						"[_].key8.key84.key84_7":j(["en": "General Consultation"]),
//						"[_].key8.key84.key84_8":j(["en": "HIV/AIDS"]),
//						"[_].key8.key84.key84_9":j(["en": "Inpatient"]),
//						"[_].key8.key84.key84_10":j(["en": "Intensive Care"]),
//						"[_].key8.key84.key84_11":j(["en": "Internal Medicine"]),
//						"[_].key8.key84.key84_12":j(["en": "Laboratory"]),
//						"[_].key8.key84.key84_13":j(["en": "Maternity"]),
//						"[_].key8.key84.key84_14":j(["en": "Mental Health"]),
//						"[_].key8.key84.key84_15":j(["en": "Mutuelle"]),
//						"[_].key8.key84.key84_16":j(["en": "Nutrition"]),
//						"[_].key8.key84.key84_17":j(["en": "Ophthalmology"]),
//						"[_].key8.key84.key84_18":j(["en": "Pediatrics"]),
//						"[_].key8.key84.key84_19":j(["en": "Pharmacy"]),
//						"[_].key8.key84.key84_20":j(["en": "Reception"]),
//						"[_].key8.key84.key84_21":j(["en": "Supporting Departments (Laundry, etc.)"]),
//						"[_].key8.key84.key84_22":j(["en": "Surgery"]),
//						"[_].key8.key84.key84_23":j(["en": "Tuberculosis"]),
//						"[_].key8.key84.key84_24":j(["en": "Vaccination"]),
//						"[_].key8.key85":j(["en": "Began Employment at this Facility"]),
//						"[_].key8.key86":j(["en": "Ended Employment at this Facility"]),
//						"[_].key9":j(["en": "Compensation"]),
//						"[_].key9.key91":j(["en": "Total Financial Compensation between July 2010 and June 2011 from the following"]),
//						"[_].key9.key91.key911":j(["en": "Facility"]),
//						"[_].key9.key91.key912":j(["en": "PBF"]),
//						"[_].key9.key91.key913":j(["en": "Non-Government Partner"]),
//						"[_].key9.key91.key914":j(["en": "Other"]),
//						"[_].key9.key92":j(["en": "Non-monetary Compensation received"]),
//						"[_].key9.key92.key921":j(["en": "Housing"]),
//						"[_].key9.key92.key922":j(["en": "Transportation"]),
//						"[_].key9.key92.key923":j(["en": "Mobile Credit"]),
//						"[_].key9.key92.key924":j(["en": "Fuel Credit"])
//					]).save(failOnError: true)
//			staffingQ1.surveyElement = staffingElementQ1
//			staffingQ1.save(failOnError: true)
//
//			//Adding a table type question
//			def tableQ = new SurveyTableQuestion(
//					code:"Question8",
//					names: j(["en":"For each training module:"]),
//					descriptions: j(["en":"(a) Enter the total number of staff members that received training in this subject from July 2009 - June 2010, regardless of how many days' training they received.<br/>(b) Enter the cumulative number of training days spent on that module. To do so, add up all of the days spent by every person who participated in that module. "]),
//					tableNames: j(["en":"Training Modules"]),
//					order: 1,
//
////					order: o(["en":1]),
//					typeCodeString: "Health Center,District Hospital"
//					)
//			staffing.addQuestion(tableQ)
//			staffing.save(failOnError:true, flush: true)
//
//			//Add columns
//			def tabColumnOne = new SurveyTableColumn(
//					names: j(["en":"Number Who Attended Training"]),
//					order: 1,
//
////					order: o(["en":1]),
//					typeCodeString: "District Hospital,Health Center",
//					question: tableQ
//					)
//			def tabColumnTwo = new SurveyTableColumn(
//					names: j(["en":"Sum Total Number of Days"]),
//					order: 2,
//
////					order: o(["en":2]),
//					typeCodeString: "District Hospital,Health Center",
//					question: tableQ
//					)
//			def tabColumnThree = new SurveyTableColumn(
//					names: j(["en":"Who Provided the Training"]),
//					order: 3,
//
////					order: o(["en":3]),
//					typeCodeString: "Health Center",
//					question: tableQ
//					)
//			def tabColumnFour = new SurveyTableColumn(
//					names: j(["en":"Due Date"]),
//					order: 4,
//
////					order: o(["en":4]),
//					typeCodeString: "District Hospital",
//					question: tableQ
//					)
//
//			tableQ.addColumn(tabColumnThree)
//			tableQ.addColumn(tabColumnTwo)
//			tableQ.addColumn(tabColumnOne)
//			tableQ.addColumn(tabColumnFour)
//
//			Map<SurveyTableColumn,SurveyElement> dataElmntsLine1= new LinkedHashMap<SurveyTableColumn,SurveyElement>();
//
//			def surveyElementTable1 = new SurveyElement(dataElement: RawDataElement.findByCode("CODE8"), surveyQuestion: tableQ).save(failOnError: true)
//			def surveyElementTable2 = new SurveyElement(dataElement: RawDataElement.findByCode("CODE9"), surveyQuestion: tableQ).save(failOnError: true)
//			def surveyElementTable3 = new SurveyElement(dataElement: RawDataElement.findByCode("CODE10"), surveyQuestion: tableQ).save(failOnError: true)
//			def surveyElementTable4 = new SurveyElement(dataElement: RawDataElement.findByCode("CODE11"), surveyQuestion: tableQ).save(failOnError: true)
//			dataElmntsLine1.put(tabColumnOne, surveyElementTable1)
//			dataElmntsLine1.put(tabColumnTwo, surveyElementTable2)
//			dataElmntsLine1.put(tabColumnThree, surveyElementTable3)
//			dataElmntsLine1.put(tabColumnFour, surveyElementTable4)
//
//			def ruleTable1 = new FormValidationRule(
//				formElement: surveyElementTable1,
//				expression: "\$"+surveyElementTable1.id+" < 100",
//				messages: j(["en":"Validation error {0,here}"]),
//				dependencies: [surveyElementTable1],
//				typeCodeString: "District Hospital,Health Center",
//				allowOutlier: false
//			).save(failOnError: true)
//
//			surveyElementTable1.addValidationRule(ruleTable1)
//			surveyElementTable1.save(failOnError: true)
//
//			Map<SurveyTableColumn,SurveyElement> dataElmntsLine2= new LinkedHashMap<SurveyTableColumn,SurveyElement>();
//
//			def surveyElementTable21 = new SurveyElement(dataElement: RawDataElement.findByCode("CODE81"), surveyQuestion: tableQ).save(failOnError: true)
//			def surveyElementTable22 = new SurveyElement(dataElement: RawDataElement.findByCode("CODE91"), surveyQuestion: tableQ).save(failOnError: true)
//			def surveyElementTable23 = new SurveyElement(dataElement: RawDataElement.findByCode("CODE101"), surveyQuestion: tableQ).save(failOnError: true)
//			def surveyElementTable24 = new SurveyElement(dataElement: RawDataElement.findByCode("CODE111"), surveyQuestion: tableQ).save(failOnError: true)
//
//			dataElmntsLine2.put(tabColumnOne, surveyElementTable21)
//			dataElmntsLine2.put(tabColumnTwo, surveyElementTable22)
//			dataElmntsLine2.put(tabColumnThree, surveyElementTable23)
//			dataElmntsLine2.put(tabColumnFour, surveyElementTable24)
//
//			//Add rows
//			def tabRowOne = new SurveyTableRow(
//					names: j(["en":"Clinical Pharmacy :"]),
//					order: 1,
//					//					order: o(["en":1]),
//					question: tableQ,
//					typeCodeString: "District Hospital,Health Center",
//					surveyElements: dataElmntsLine1
//					)
//			def tabRowTwo = new SurveyTableRow(
//					names: j(["en":"Clinical Nurse Training :"]),
//					order: 2,
//					//					order: o(["en":2]),
//					question: tableQ,
//					typeCodeString: "Health Center",
//					surveyElements: dataElmntsLine2
//					)
//
//			tableQ.addRow(tabRowOne)
//			tableQ.addRow(tabRowTwo)
//			tableQ.save(failOnError:true)
//
//			def ruleCheckbox = new FormValidationRule(
//				formElement: surveyElementChecboxQ3,
//				expression: "if(\$"+surveyElementTable21.id+" < 100) \$"+surveyElementChecboxQ3.id+" else true",
//				messages: j(["en":"Validation error {0,here}"]),
//				dependencies: [surveyElementTable21],
//				typeCodeString: "District Hospital,Health Center",
//				allowOutlier: false
//			).save(failOnError: true)
//
//			surveyElementChecboxQ3.addValidationRule(ruleCheckbox)
//			surveyElementChecboxQ3.save(failOnError: true)
//
//			def skipRule1 = new SurveySkipRule(survey: surveyOne, expression: "1==1", skippedFormElements: [(surveyElementTable2): ""]);
//			def skipRule2 = new SurveySkipRule(survey: surveyOne, expression: "\$"+surveyElementTable1.id+"==1", skippedFormElements: [(surveyElementTable22): "", (surveyElementTable3): ""]);
//			def skipRule3 = new SurveySkipRule(survey: surveyOne, expression: "\$"+surveyElementTable1.id+"==2", skippedSurveyQuestions: [checkBoxQ]);
//			def skipRule4 = new SurveySkipRule(survey: surveyOne, expression: "\$"+surveyElementPatientQ1.id+"==1000", skippedSurveyQuestions: [tableQ], skippedFormElements: [(surveyElementChecboxQ1): ""]);
//			def skipRule5 = new SurveySkipRule(survey: surveyOne, expression: "\$"+surveyElementServiceQ6.id+"[_].key0.key01=='value1'", skippedSurveyQuestions: [], skippedFormElements: [(surveyElementServiceQ6): "[_].key0.key02"]);
//
//			surveyOne.addSkipRule(skipRule1)
//			surveyOne.addSkipRule(skipRule2)
//			surveyOne.addSkipRule(skipRule3)
//			surveyOne.addSkipRule(skipRule4)
//			surveyOne.addSkipRule(skipRule5)
//
//			surveyOne.save()
//		}
//	}
	
}
