package org.chai.kevin.survey

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Type;
import org.chai.kevin.form.FormEnteredValue;
import org.chai.kevin.form.FormValidationRule;
import org.chai.location.DataLocationType;
import org.chai.kevin.util.Utils;

abstract class SurveyIntegrationTests extends IntegrationTests {
	
	def static newSurvey(def code, def period) {
		return newSurvey(code, null, period, false)
	}
	
	def static newSurvey(def code, def names, def period) {
		return newSurvey(code, names, period, false)
	}
	
	def static newSurvey(def code, def names, def period, def active) {
		def survey = new Survey(code: code, period: period, active: active)
		setLocaleValueInMap(survey, names, "Names")
		return survey.save(failOnError: true)
	}
	
	def static newSurveyProgram(def code, def survey, def order, def types) {
		return newSurveyProgram(code, null, survey, order, types)
	}
	
	def static newSurveyProgram(def code, def names, def survey, def order, def types) {
		def program = new SurveyProgram(code: code, survey: survey, order: order, typeCodeString: Utils.unsplit(types, Utils.DEFAULT_TYPE_CODE_DELIMITER)).save(failOnError: true)
		setLocaleValueInMap(program, names, "Names")
		survey.addToPrograms(program)
		survey.save(failOnError: true)
		return program
	}
	
	def static newSurveySection(def code, def program, def order, def types) {
		def section = newSurveySection(code, null, program, order, types)
	}
	
	def static newSurveySection(def code, def names, def program, def order, def types) {
		def section = new SurveySection(code: code, program: program, order: order, typeCodeString: Utils.unsplit(types, Utils.DEFAULT_TYPE_CODE_DELIMITER)).save(failOnError: true)
		setLocaleValueInMap(section, names, "Names")
		program.addToSections(section)
		program.save(failOnError: true)
		return section
	}
	
	def static newSurveyEnteredQuestion(def question, def period, def dataLocation, def invalid, def complete) {
		return new SurveyEnteredQuestion(question: question, dataLocation: dataLocation, invalid: invalid, complete: complete).save(failOnError: true, flush: true)
	}
		
	def static newSurveyEnteredSection(def section, def period, def dataLocation, def invalid, def complete) {
		return newSurveyEnteredSection(section, period, dataLocation, invalid, complete, 0, 0)
	}
	
	def static newSurveyEnteredSection(def section, def period, def dataLocation, def invalid, def complete, def completedQuestions, def totalQuestions) {
		return new SurveyEnteredSection(section: section, dataLocation: dataLocation, invalid: invalid, complete: complete, completedQuestions: completedQuestions, totalQuestions: totalQuestions).save(failOnError: true)
	}
	
	def static newSurveyEnteredProgram(def program, def period, def dataLocation, def invalid, def complete, def closed) {
		return newSurveyEnteredProgram(program, period, dataLocation, invalid, complete, closed, 0, 0)
	}
	
	def static newSurveyEnteredProgram(def program, def period, def dataLocation, def invalid, def complete, def closed, def completedQuestions, def totalQuestions) {
		return new SurveyEnteredProgram(program: program, dataLocation: dataLocation, invalid: invalid, complete: complete, closed: closed, completedQuestions: completedQuestions, totalQuestions: totalQuestions).save(failOnError: true)
	}

	def static newSurveySkipRule(def code, def survey, def expression, def skippedElements, def skippedQuestions) {
		def skipRule = new SurveySkipRule(code: code, survey: survey, expression: expression, skippedFormElements: skippedElements, skippedSurveyQuestions: skippedQuestions).save(failOnError: true)
		survey.addToSkipRules(skipRule)
		survey.save(failOnError: true, flush: true)
		return skipRule
	}
	
	def static newSimpleQuestion(def code, def names, def section, def order, def types) {
		def question = new SurveySimpleQuestion(code: code, section: section, order: order, typeCodeString: Utils.unsplit(types, Utils.DEFAULT_TYPE_CODE_DELIMITER)).save(failOnError: true)
		setLocaleValueInMap(question, names, "Names")
		section.addToQuestions(question)
		section.save(failOnError: true, flush: true)		
		return question
	}
	
	def static newSimpleQuestion(def code, def section, def order, def types) {
		return newSimpleQuestion(code, null, section, order, types)
	}
	
	def static newTableQuestion(def code, def section, def order, def types) {
		def question = new SurveyTableQuestion(code: code, section: section, order: order, typeCodeString: Utils.unsplit(types, Utils.DEFAULT_TYPE_CODE_DELIMITER)).save(failOnError: true)
		section.addToQuestions(question)
		section.save(failOnError: true)
		return question
	}
	
	def static newTableColumn(def code, def question, def order, def types) {
		def column = new SurveyTableColumn(code: code, question: question, order: order, typeCodeString: Utils.unsplit(types, Utils.DEFAULT_TYPE_CODE_DELIMITER)).save(failOnError: true)
		question.addToColumns(column)
		question.save(failOnError: true)
		return column
	}
	
	def static newTableRow(def code, def question, def order, def types, def elements) {
		def row = new SurveyTableRow(code: code, question: question, order: order, typeCodeString: Utils.unsplit(types, Utils.DEFAULT_TYPE_CODE_DELIMITER), surveyElements: elements).save(failOnError: true)
		question.addToRows(row)
		question.save(failOnError: true)
		return row
	}
	
	def static newCheckboxQuestion(def code, def section, def order, def types) {
		def question = new SurveyCheckboxQuestion(code: code, section: section, order: order, typeCodeString: Utils.unsplit(types, Utils.DEFAULT_TYPE_CODE_DELIMITER)).save(failOnError: true)
		section.addToQuestions(question)
		section.save(failOnError: true)
		return question
	}
	
	def static newCheckboxOption(def code, def question, def order, def types) {
		return newCheckboxOption(code, question, order, types, null)
	}
	
	def static newCheckboxOption(def code, def question, def order, def types, def element) {
		def option = new SurveyCheckboxOption(code: code, question: question, order: order, typeCodeString: Utils.unsplit(types, Utils.DEFAULT_TYPE_CODE_DELIMITER), surveyElement: element).save(failOnError: true)
		question.addToOptions(option)
		question.save(failOnError: true)
		return option
	}

	// TODO why no code here?
	def static newSurveyElement(def question, def dataElement) {
		def element = newSurveyElement(question, dataElement, null)
	}
	
	// TODO why no code here?
	def static newSurveyElement(def question, def dataElement, def headers) {
		def element = new SurveyElement(question: question, dataElement: dataElement, headers: headers).save(failOnError: true, flush: true)
		question.addToSurveyElements(element)
		question.save(failOnError: true, flush: true)
		return element;
	}
}
