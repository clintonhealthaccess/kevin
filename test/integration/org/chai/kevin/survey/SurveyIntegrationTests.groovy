package org.chai.kevin.survey

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Type;
import org.chai.kevin.form.FormEnteredValue;
import org.chai.kevin.form.FormValidationRule;
import org.chai.kevin.survey.validation.SurveyEnteredProgram;
import org.chai.kevin.survey.validation.SurveyEnteredQuestion;
import org.chai.kevin.survey.validation.SurveyEnteredSection;
import org.chai.kevin.util.Utils;

abstract class SurveyIntegrationTests extends IntegrationTests {
	
	def static newSurvey(def period) {
		return newSurvey([:], period, false)
	}
	
	def static newSurvey(def names, def period) {
		return newSurvey(names, period, false)
	}
	
	def static newSurvey(def names, def period, def active) {
		inc++
		return new Survey(code: code, names: names, period: period, active: active).save(failOnError: true);
	}
	
	def static newSurveyProgram(def survey, def order, def types) {
		return newSurveyProgram([:], survey, order, types)
	}
	
	def static newSurveyProgram(def names, def survey, def order, def types) {
		inc++
		def program = new SurveyProgram(code: code, names: names, survey: survey, order: order, typeCodeString: Utils.unsplit(types)).save(failOnError: true)
		survey.addProgram(program)
		survey.save(failOnError: true)
		return program
	}
	
	def static newSurveySection(def program, def order, def types) {
		def section = newSurveySection([:], program, order, types)
	}
	
	def static newSurveySection(def names, def program, def order, def types) {
		inc++
		def section = new SurveySection(code: code, names: names, program: program, order: order, typeCodeString: Utils.unsplit(types)).save(failOnError: true)
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
	
	def static newSurveyEnteredQuestion(def question, def period, def dataLocation, def invalid, def complete) {
		return new SurveyEnteredQuestion(question: question, dataLocation: dataLocation, invalid: invalid, complete: complete).save(failOnError: true, flush: true)
	}
		
	def static newSurveyEnteredSection(def section, def period, def dataLocation, def invalid, def complete) {
		return new SurveyEnteredSection(section: section, dataLocation: dataLocation, invalid: invalid, complete: complete).save(failOnError: true)
	}

	def static newSurveyEnteredProgram(def program, def period, def dataLocation, def invalid, def complete, def closed) {
		return new SurveyEnteredProgram(program: program, dataLocation: dataLocation, invalid: invalid, complete: complete, closed: closed).save(failOnError: true)
	}

	def static newSurveySkipRule(def survey, def expression, def skippedElements, def skippedQuestions) {
		inc++
		def skipRule = new SurveySkipRule(code: code, survey: survey, expression: expression, skippedFormElements: skippedElements, skippedSurveyQuestions: skippedQuestions).save(failOnError: true)
		survey.addSkipRule(skipRule)
		survey.save(failOnError: true, flush: true)
		return skipRule
	}
	
	def static newSimpleQuestion(def names, def section, def order, def types) {
		inc++
		def question = new SurveySimpleQuestion(code: code, names: names, section: section, order: order, typeCodeString: Utils.unsplit(types)).save(failOnError: true)
		section.addQuestion(question)
		section.save(failOnError: true, flush: true)		
		return question
	}
	
	def static newSimpleQuestion(def section, def order, def types) {
		return newSimpleQuestion([:], section, order, types)
	}
	
	def static newTableQuestion(def section, def order, def types) {
		inc++
		def question = new SurveyTableQuestion(code: code, section: section, order: order, typeCodeString: Utils.unsplit(types)).save(failOnError: true)
		section.addQuestion(question)
		section.save(failOnError: true)
		return question
	}
	
	def static newTableColumn(def question, def order, def types) {
		inc++
		def column = new SurveyTableColumn(code: code, question: question, order: order, typeCodeString: Utils.unsplit(types)).save(failOnError: true)
		question.addColumn(column)
		question.save(failOnError: true)
		return column
	}
	
	def static newTableRow(def question, def order, def types, def elements) {
		inc++
		def row = new SurveyTableRow(code: code, question: question, order: order, typeCodeString: Utils.unsplit(types), surveyElements: elements).save(failOnError: true)
		question.addRow(row)
		question.save(failOnError: true)
		return row
	}
	
	def static newCheckboxQuestion(def section, def order, def types) {
		inc++
		def question = new SurveyCheckboxQuestion(code: code, section: section, order: order, typeCodeString: Utils.unsplit(types)).save(failOnError: true)
		section.addQuestion(question)
		section.save(failOnError: true)
		return question
	}
	
	def static newCheckboxOption(def question, def order, def types) {
		return newCheckboxOption(question, order, types, null)
	}
	
	def static newCheckboxOption(def question, def order, def types, def element) {
		inc++
		def option = new SurveyCheckboxOption(code: code, question: question, order: order, typeCodeString: Utils.unsplit(types), surveyElement: element).save(failOnError: true)
		question.addOption(option)
		question.save(failOnError: true)
		return option
	}

}
