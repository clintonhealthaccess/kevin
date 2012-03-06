package org.chai.kevin.survey

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Type;
import org.chai.kevin.survey.validation.SurveyEnteredProgram;
import org.chai.kevin.survey.validation.SurveyEnteredQuestion;
import org.chai.kevin.survey.validation.SurveyEnteredSection;
import org.chai.kevin.survey.validation.SurveyEnteredValue;
import org.chai.kevin.util.Utils;

abstract class SurveyIntegrationTests extends IntegrationTests {

	def static newSurvey(def period) {
		return newSurvey([:], period, false)
	}
	
	def static newSurvey(def names, def period) {
		return newSurvey(names, period, false)
	}
	
	def static newSurvey(def names, def period, def active) {
		return new Survey(names: names, period: period, active: active).save(failOnError: true);
	}
	
	def static newSurveyProgram(def survey, def order, def types) {
		return newSurveyProgram([:], survey, order, types)
	}
	
	def static newSurveyProgram(def names, def survey, def order, def types) {
		def program = new SurveyProgram(names: names, survey: survey, order: order, typeCodeString: Utils.unsplit(types)).save(failOnError: true)
		survey.addProgram(program)
		survey.save(failOnError: true)
		return program
	}
	
	def static newSurveySection(def program, def order, def types) {
		def section = newSurveySection([:], program, order, types)
	}
	
	def static newSurveySection(def names, def program, def order, def types) {
		def section = new SurveySection(names: names, program: program, order: order, typeCodeString: Utils.unsplit(types)).save(failOnError: true)
		program.addSection(section)
		program.save(failOnError: true)
		return section
	}
	
	def static newSurveyElement(def question, def dataElement) {
		def element = newSurveyElement(question, dataElement, [:])
	}
	
	def static newSurveyElement(def question, def dataElement, def headers) {
		def element = new SurveyElement(surveyQuestion: question, dataElement: dataElement, headers: headers).save(failOnError: true, flush: true)
		if (question instanceof SurveySimpleQuestion) {
			question.surveyElement = element
			question.save(failOnError: true, flush: true)
		}
		return element;
	}
	
	def static newSurveyEnteredValue(def element, def period, def entity, def value) {
		return new SurveyEnteredValue(surveyElement: element, value: value, entity: entity).save(failOnError: true, flush: true)
	}

	def static newSurveyEnteredQuestion(def question, def period, def entity, def invalid, def complete) {
		return new SurveyEnteredQuestion(question: question, entity: entity, invalid: invalid, complete: complete).save(failOnError: true, flush: true)
	}
		
	def static newSurveyEnteredSection(def section, def period, def entity, def invalid, def complete) {
		return new SurveyEnteredSection(section: section, entity: entity, invalid: invalid, complete: complete).save(failOnError: true)
	}

	def static newSurveyEnteredProgram(def program, def period, def entity, def invalid, def complete, def closed) {
		return new SurveyEnteredProgram(program: program, entity: entity, invalid: invalid, complete: complete, closed: closed).save(failOnError: true)
	}
	
	def static newSurveyValidationRule(def element, def prefix, def types, def expression, boolean allowOutlier, def dependencies = []) {
		def validationRule = new SurveyValidationRule(expression: expression, messages: [:], surveyElement: element, typeCodeString: Utils.unsplit(types), dependencies: dependencies, allowOutlier: allowOutlier).save(failOnError: true)
		element.addValidationRule(validationRule)
		element.save(failOnError: true)
		return validationRule
	}
	
	def static newSurveyValidationRule(def element, def prefix, def types, def expression, def dependencies = []) {
		return newSurveyValidationRule(element, prefix, types, expression, false, dependencies)
	}
	
	def static newSkipRule(def survey, def expression, def skippedElements, def skippedQuestions) {
		def skipRule = new SurveySkipRule(survey: survey, expression: expression, skippedSurveyElements: skippedElements, skippedSurveyQuestions: skippedQuestions).save(failOnError: true)
		survey.addSkipRule(skipRule)
		survey.save(failOnError: true, flush: true)
		return skipRule
	}
	
	def static newSimpleQuestion(def names, def section, def order, def types) {
		def question = new SurveySimpleQuestion(names: names, section: section, order: order, typeCodeString: Utils.unsplit(types)).save(failOnError: true)
		section.addQuestion(question)
		section.save(failOnError: true, flush: true)
		return question
	}
	
	def static newSimpleQuestion(def section, def order, def types) {
		return newSimpleQuestion([:], section, order, types)
	}
	
	def static newTableQuestion(def section, def order, def types) {
		def question = new SurveyTableQuestion(section: section, order: order, typeCodeString: Utils.unsplit(types)).save(failOnError: true)
		section.addQuestion(question)
		section.save(failOnError: true)
		return question
	}
	
	def static newTableColumn(def question, def order, def types) {
		def column = new SurveyTableColumn(question: question, order: order, typeCodeString: Utils.unsplit(types)).save(failOnError: true)
		question.addColumn(column)
		question.save(failOnError: true)
		return column
	}
	
	def static newTableRow(def question, def order, def types, def elements) {
		def row = new SurveyTableRow(question: question, order: order, typeCodeString: Utils.unsplit(types), surveyElements: elements).save(failOnError: true)
		question.addRow(row)
		question.save(failOnError: true)
		return row
	}
	
	def static newCheckboxQuestion(def section, def order, def types) {
		def question = new SurveyCheckboxQuestion(section: section, order: order, typeCodeString: Utils.unsplit(types)).save(failOnError: true)
		section.addQuestion(question)
		section.save(failOnError: true)
		return question
	}
	
	def static newCheckboxOption(def question, def order, def types, def element) {
		def option = new SurveyCheckboxOption(question: question, order: order, typeCodeString: Utils.unsplit(types), surveyElement: element).save(failOnError: true)
		question.addOption(option)
		question.save(failOnError: true)
		return option
	}

//
//	
//	def static newCheckboxOption(def question)
}
