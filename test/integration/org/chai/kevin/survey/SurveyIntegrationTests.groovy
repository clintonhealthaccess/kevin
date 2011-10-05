package org.chai.kevin.survey

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Type;
import org.chai.kevin.maps.MapsTarget.MapsTargetType;
import org.chai.kevin.survey.validation.SurveyEnteredValue;
import org.chai.kevin.util.Utils;

abstract class SurveyIntegrationTests extends IntegrationTests {

	def newSurvey(def period) {
		return new Survey(period: period).save(failOnError: true);
	}
	
	def newSurveyObjective(def survey, def order, def groups) {
		def objective = new SurveyObjective(survey: survey, order: order, groupUuidString: Utils.unsplit(groups)).save(failOnError: true)
		survey.addObjective(objective)
		survey.save(failOnError: true)
		return objective
	}
	
	def newSurveySection(def objective, def order, def groups) {
		def section = new SurveySection(objective: objective, order: order, groupUuidString: Utils.unsplit(groups)).save(failOnError: true)
		objective.addSection(section)
		objective.save(failOnError: true)
		return section
	}
	
	def newSurveyElement(def question, def dataElement) {
		def element = new SurveyElement(surveyQuestion: question, dataElement: dataElement).save(failOnError: true)
		if (question instanceof SurveySimpleQuestion) {
			question.surveyElement = element
			question.save(failOnError: true)
		}
		return element;
	}
	
	def newSurveyEnteredValue(def element, def period, def organisationUnit, def value) {
		return new SurveyEnteredValue(surveyElement: element, value: value, organisationUnit: organisationUnit).save(failOnError: true, flush: true)
	}
	
	def newValidationMessage() {
		return newValidationMessage([:])
	}
	
	def newValidationMessage(def messages) {
		return new SurveyValidationMessage(messages: messages).save(failOnError: true)
	}
	
	def newSurveyValidationRule(def element, def prefix, def groups, def expression, def validationMessage, def dependencies = []) {
		def validationRule = new SurveyValidationRule(expression: expression, surveyElement: element, groupUuidString: Utils.unsplit(groups), validationMessage: validationMessage, dependencies: dependencies, allowOutlier: false).save(failOnError: true)
		element.addValidationRule(validationRule)
		element.save(failOnError: true)
		validationMessage.addValidationRule(validationRule)
		validationMessage.save(failOnError: true)
		return validationRule
	}
	
	def newSkipRule(def survey, def expression, def skippedElements, def skippedQuestions) {
		def skipRule = new SurveySkipRule(survey: survey, expression: expression, skippedSurveyElements: skippedElements, skippedSurveyQuestions: skippedQuestions).save(failOnError: true)
		survey.addSkipRule(skipRule)
		survey.save(failOnError: true, flush: true)
		return skipRule
	}
	
	def newSimpleQuestion(def names, def section, def order, def groups) {
		def question = new SurveySimpleQuestion(names: names, section: section, order: order, groupUuidString: Utils.unsplit(groups)).save(failOnError: true)
		section.addQuestion(question)
		section.save(failOnError: true)
		return question
	}
	
	def newSimpleQuestion(def section, def order, def groups) {
		return newSimpleQuestion([:], section, order, groups)
	}
	
	def newTableQuestion(def section, def order, def groups) {
		def question = new SurveyTableQuestion(section: section, order: order, groupUuidString: Utils.unsplit(groups)).save(failOnError: true)
		section.addQuestion(question)
		section.save(failOnError: true)
		return question
	}
	
	def newTableColumn(def question, def order, def groups) {
		def column = new SurveyTableColumn(question: question, order: order, groupUuidString: Utils.unsplit(groups)).save(failOnError: true)
		question.addColumn(column)
		question.save(failOnError: true)
		return column
	}
	
	def newTableRow(def question, def order, def groups, def elements) {
		def row = new SurveyTableRow(question: question, order: order, groupUuidString: Utils.unsplit(groups), surveyElements: elements).save(failOnError: true)
		question.addRow(row)
		question.save(failOnError: true)
		return row
	}
	
//	def newCheckboxQuestion(def section, def order, def groups) {
//		def question = new SurveySimpleQuestion(section: section, order: order, groupUuidString: Utils.unsplit(groups))
//		question.
//	}
//	

//
//	
//	def newCheckboxOption(def question)
}
