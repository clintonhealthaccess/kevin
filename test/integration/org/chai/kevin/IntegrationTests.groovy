package org.chai.kevin

/**
* Copyright (c) 2011, Clinton Health Access Initiative.
*
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*     * Redistributions of source code must retain the above copyright
*       notice, this list of conditions and the following disclaimer.
*     * Redistributions in binary form must reproduce the above copyright
*       notice, this list of conditions and the following disclaimer in the
*       documentation and/or other materials provided with the distribution.
*     * Neither the name of the <organization> nor the
*       names of its contributors may be used to endorse or promote products
*       derived from this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
* ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
* WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
* DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
* (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
* LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

import grails.plugin.spock.IntegrationSpec
import grails.plugin.springcache.annotations.CacheFlush;

import java.util.Date

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.chai.kevin.dashboard.DashboardProgram
import org.chai.kevin.dashboard.DashboardTarget
import org.chai.kevin.data.Aggregation;
import org.chai.kevin.data.Average
import org.chai.kevin.data.Calculation;
import org.chai.kevin.data.RawDataElement
import org.chai.kevin.data.Enum
import org.chai.kevin.data.EnumOption
import org.chai.kevin.data.ExpressionMap;
import org.chai.kevin.data.NormalizedDataElement;
import org.chai.kevin.data.Sum
import org.chai.kevin.data.Type;
import org.chai.kevin.form.FormElement;
import org.chai.kevin.form.FormEnteredValue;
import org.chai.kevin.form.FormSkipRule;
import org.chai.kevin.form.FormValidationRule;
import org.chai.kevin.util.JSONUtils;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.AggregationPartialValue;
import org.chai.kevin.value.AveragePartialValue;
import org.chai.kevin.value.CalculationPartialValue
import org.chai.kevin.value.RawDataElementValue
import org.chai.kevin.value.NormalizedDataElementValue
import org.chai.kevin.value.SumPartialValue;
import org.chai.kevin.value.Value;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.location.Location;
import org.chai.kevin.location.LocationLevel;
import org.chai.kevin.reports.ReportProgram
import org.chai.kevin.security.SurveyUser;
import org.chai.kevin.security.User;

abstract class IntegrationTests extends IntegrationSpec {
	
	def refreshValueService
	def springcacheService
	def sessionFactory
	
	static final String CODE (def number) { return "CODE"+number }
	
	static final Type INVALID_TYPE = new Type("invalid_type")
	
	static final String HEALTH_CENTER_GROUP = "Health Center"
	static final String DISTRICT_HOSPITAL_GROUP = "District Hospital"
	
	static final String NATIONAL = "National"
	static final String PROVINCE = "Province"
	static final String DISTRICT = "District"
	static final String SECTOR = "Sector"
	
	static final String RWANDA = "Rwanda"
	static final String KIGALI_CITY = "Kigali City"
	static final String NORTH = "North"
	static final String BURERA = "Burera"
	static final String BUTARO = "Butaro DH"
	static final String KIVUYE = "Kivuye HC"
	
	static final Date mar01 = getDate( 2005, 3, 1 );
	static final Date mar31 = getDate( 2005, 3, 31 );
	
	static String ROOT = "Root"
	
	static String PROGRAM1 = "Program1"
	static String TARGET1 = "Target 1"
	static String TARGET2 = "Target 2"
	
	static String PROGRAM2 = "Program2"
	static String TARGET3 = "Target 3"
	
	static String PROGRAM3 = "Program3"
	
	def setup() {
		// using cache.use_second_level_cache = false in test mode doesn't work so
		// we flush the cache after each test
		springcacheService.flushAll()
	}
	
	static def setupLocationTree() {
		// for the test environment, the location level is set to 4
		// so we create a tree accordingly
		def hc = newDataLocationType(j(["en":HEALTH_CENTER_GROUP]), HEALTH_CENTER_GROUP);
		def dh = newDataLocationType(j(["en":DISTRICT_HOSPITAL_GROUP]), DISTRICT_HOSPITAL_GROUP);
		
		def country = newLocationLevel(NATIONAL, 1)
		def province = newLocationLevel(PROVINCE, 2)
		def district = newLocationLevel(DISTRICT, 3)
		def sector = newLocationLevel(SECTOR, 4)
		
		def rwanda = newLocation(j(["en":RWANDA]), RWANDA, country)
		def north = newLocation(j(["en":NORTH]), NORTH, rwanda, province)
		def burera = newLocation(j(["en":BURERA]), BURERA, north, district)
		
		newDataLocation(j(["en":BUTARO]), BUTARO, burera, dh)
		newDataLocation(j(["en":KIVUYE]), KIVUYE, burera, hc)
	}

	static def setupProgramTree() {
		def root = newReportProgram(ROOT)		
		def program1 = newReportProgram(PROGRAM1, root)
		def program2 = newReportProgram(PROGRAM2, root)
		def program3 = newReportProgram(PROGRAM3, root)	
	}
		
	static def newPeriod() {
		def period = new Period(startDate: mar01, endDate: mar31)
		return period.save(failOnError: true)
	} 
	
	static def newDataLocationType(def code) {
		return newDataLocationType([:], code)
	}
	
	static def newDataLocationType(def names, def code) {
		return new DataLocationType(names: names, code: code).save(failOnError: true)
	}
		
	static def newDataLocation(def code, def location, def type) {
		return newDataLocation([:], code, location, type)
	}
	
	static def newDataLocation(def names, def code, def location, def type) {
		def dataLocation = new DataLocation(names: names, code: code, location: location, type: type).save(failOnError: true)
		if (location != null) {
			 location.dataLocations << dataLocation
			 location.save(failOnError: true)
		}
		return dataLocation
	}
	
	static def newLocationLevel(String code, def order) {
		return new LocationLevel(code: code, order: order).save(failOnError: true)
	}
	
	static def newLocation(String code, def level) {
		return newLocation([:], code, null, level)
	}

	static def newLocation(def names, def code, def level) {
		return newLocation(names, code, null, level)
	}
		
	static def newLocation(String code, def parent, def level) {
		return newLocation([:], code, parent, level)
	}
	
	static def newLocation(def names, def code, def parent, def level) {
		def location = new Location(names: names, code: code, parent: parent, level: level).save(failOnError: true)
		level.locations << location
		level.save(failOnError: true)
		if (parent != null) {
			parent.children << location
			parent.save(failOnError: true)
		}
		return location
	}	
			
	static def newUser(def username, def uuid) {
		return new User(username: username, permissionString: '', passwordHash:'', uuid: uuid).save(failOnError: true)
	}
	
	static def newSurveyUser(def username, def uuid, def dataLocationId) {
		return new SurveyUser(username: username, permissionString: '', passwordHash:'', uuid: uuid, dataLocationId: dataLocationId).save(failOnError: true)
	}
	
	static def newReportProgram(def code) {
		return new ReportProgram(code: code, parent: null, names: [:]).save(failOnError: true, flush: true);
	}
	
	static def newReportProgram(def code, def parent) {
		def reportProgram = new ReportProgram(code: code, parent: parent, names: [:]).save(failOnError: true, flush: true);
		parent.children << reportProgram
		parent.save(failOnError: true)
		return reportProgram
	}
	
	static def newReportProgram(def code, def parent, def children){
		return new ReportProgram(code: code, parent: parent, children: children, names: [:]).save(failOnError: true, flush: true);
	}
	
	static RawDataElementValue newRawDataElementValue(def rawDataElement, def period, def location, def value) {
		return new RawDataElementValue(data: rawDataElement, period: period, location: location, value: value).save(failOnError: true, flush: true)
	}
	
	static AggregationPartialValue newAggregationPartialValue(def aggregation, def period, def location, def type, def expressionData, def value) {
		return new AggregationPartialValue(data: aggregation, period: period, location: location, type: type, expressionData: expressionData, value: value).save(failOnError: true)
	}
	
	static SumPartialValue newSumPartialValue(def sum, def period, def location, def type, def value) {
		return new SumPartialValue(data: sum, period: period, location: location, type: type, value: value).save(failOnError: true)
	}
	
	static AveragePartialValue newAveragePartialValue(def average, def period, def location, def type, def numberOfDataLocations, def value) {
		return new AveragePartialValue(data: average, period: period, location: location, type: type, numberOfDataLocations: numberOfDataLocations, value: value).save(failOnError: true)
	}
	
	static RawDataElement newRawDataElement(def code, def type) {
		return newRawDataElement(j([:]), code, type)
	}
	
	static RawDataElement newRawDataElement(def names, def code, def type) {
		return newRawDataElement(names, code, type, null)
	}
	
	static RawDataElement newRawDataElement(def names, def code, def type, def info) {
		return new RawDataElement(names: names, code: code, type: type, info: info).save(failOnError: true, flush:true)
	}

	static def newNormalizedDataElement(def names, def code, def type, def expressionMap, Map params) {
		params << [failOnError: true]
		params << [flush: true]
		return new NormalizedDataElement(names: names, code: code, type: type, expressionMap: expressionMap).save(params)
	}

	static def newNormalizedDataElement(def names, String code, def type, def expressionMap) {
		return newNormalizedDataElement(names, code, type, expressionMap, [:])
	}
	
	static def newNormalizedDataElement(def code, Type type, def expressionMap) {
		return newNormalizedDataElement([:], code, type, expressionMap, [:])
	}
	
	static def newNormalizedDataElement(def code, Type type, def expressionMap, Map params) {
		return newNormalizedDataElement([:], code, type, expressionMap, params)
	}
	
	static NormalizedDataElementValue newNormalizedDataElementValue(def normalizedDataElement, def location, def period, def status, def value) {
		return new NormalizedDataElementValue(data: normalizedDataElement, period: period, location: location, status: status, value: value).save(failOnError: true)
	}
	
	static Aggregation newAggregation(def names, def expression, def code) {
		return new Aggregation(names: names, expression: expression, code: code).save(failOnError: true)
	}

	static Aggregation newAggregation(def expression, def code) {
		return newAggregation([:], expression, code)
	}

	static def newAverage(def names, String expression, def code, def calculated) {
		return new Average(names: names, expression: expression, code: code, calculated: calculated).save(failOnError: true)
	}

	static def newAverage(def names, String expression, String code) {
		return newAverage(names, expression, code, null)
	}

	static def newAverage(String expression, def code) {
		return newAverage([:], expression, code, null)
	}
	
	static def newAverage(String expression, def code, Date calculated) {
		return newAverage([:], expression, code, calculated)
	}

	static Sum newSum(def names, def expression, def code) {
		return new Sum(names: names, expression: expression, code: code).save(failOnError: true, flush: true)
	}
	
	static Sum newSum(def expression, def code) {
		return newSum([:], expression, code)
	}
	
	static Enum newEnume(def code) {
		return new Enum(code: code).save(failOnError: true, flush: true)
	}
	
	static Enum newEnume(def code, def names, def descriptions){
		return new Enum(code: code, names:j("en":names),descriptions:j("en":descriptions)).save(failOnError: true, flush: true)
	}
		
	static EnumOption newEnumOption(def enume, def value) {
		return newEnumOption([:], enume, value, new Ordering())
	}
	
	static EnumOption newEnumOption(def names, Enum enume, def value) {
		return newEnumOption(names, enume, value, new Ordering())
	}
	
	static EnumOption newEnumOption(Enum enume, def value, def order) {
		return newEnumOption([:], enume, value, order)
	}
	
	static EnumOption newEnumOption(def names, Enum enume, def value, def order) {
		def enumOption = new EnumOption(names: names, enume: enume, value: value, order: order).save(failOnError: true)
		enume.addEnumOption(enumOption)
		enume.save(failOnError: true)
		return enumOption
	}
	
	def static newFormEnteredValue(def element, def period, def dataLocation, def value) {
		return new FormEnteredValue(formElement: element, value: value, dataLocation: dataLocation).save(failOnError: true, flush: true)
	}
	
	def static newFormValidationRule(def element, def prefix, def types, def expression, boolean allowOutlier, def dependencies = []) {
		def validationRule = new FormValidationRule(expression: expression, prefix: prefix, messages: [:], formElement: element, typeCodeString: Utils.unsplit(types), dependencies: dependencies, allowOutlier: allowOutlier).save(failOnError: true)
		element.addValidationRule(validationRule)
		element.save(failOnError: true)
		return validationRule
	}
	
	def static newFormValidationRule(def element, def prefix, def types, def expression, def dependencies = []) {
		return newFormValidationRule(element, prefix, types, expression, false, dependencies)
	}
	
	def static newFormElement(def dataElement) {
		return new FormElement(dataElement: dataElement).save(failOnError: true)
	}
	
	def static newFormSkipRule(def expression, def skippedElements) {
		return new FormSkipRule(expression: expression, skippedFormElements: skippedElements).save(failOnError: true)
	}

	def refresh() {
		refreshNormalizedDataElement()
		refreshCalculation()
	}
	
	def refreshNormalizedDataElement() {
		NormalizedDataElement.list().each {
			refreshValueService.refreshNormalizedDataElement(it)
		}
	}
	
	def refreshCalculation() {
		Average.list().each {
			refreshValueService.refreshCalculation(it)
		}
		Sum.list().each {
			refreshValueService.refreshCalculation(it)
		}
		Aggregation.list().each {
			refreshValueService.refreshCalculation(it)
		}
	}
	
	def setupSecurityManager(def user) {
		def subject = [getPrincipal: { user?.uuid }, isAuthenticated: { user==null?false:true }] as Subject
		ThreadContext.put( ThreadContext.SECURITY_MANAGER_KEY, [ getSubject: { subject } ] as SecurityManager )
		SecurityUtils.metaClass.static.getSubject = { subject }
	}
	
	static def g(def types) {
		return Utils.unsplit(types)
	}
	
	static def getLocationLevels(def levels) {
		def result = []
		for (def level : levels) {
			result.add LocationLevel.findByCode(level)
		}
		return result;
	}
	
//	static def getLocation(def name) {
//		return new Location(Location.findByName(name))
//	}
	
	static def getCalculationLocation(def code) {
		def location = Location.findByCode(code)
		if (location == null) location = DataLocation.findByCode(code)
		return location
	}
	
	static def getLocations(def codes) {
		def result = []
		for (String code : codes) {
			result.add(Location.findByCode(code))
		}
		return result
	}
	
	static def getDataLocations(def codes) {
		def result = []
		for (String code : codes) {
			result.add(DataLocation.findByCode(code))
		}
		return result
	}
	
	static s(def list) {
		return new HashSet(list)
	}
	
	static e(def map) {
		return new ExpressionMap(jsonText: JSONUtils.getJSONFromMap(map))
	}
	
	static j(def map) {
		return new Translation(jsonText: JSONUtils.getJSONFromMap(map));
	}
	
	static o(def map) {
		return new Ordering(jsonText: JSONUtils.getJSONFromMap(map));
	}
	
	static v(def value) {
		return new Value("{\"value\":"+value+"}");
	}
	
	static Date getDate( int year, int month, int day )
	{
		final Calendar calendar = Calendar.getInstance();

		calendar.clear();
		calendar.set( Calendar.YEAR, year );
		calendar.set( Calendar.MONTH, month - 1 );
		calendar.set( Calendar.DAY_OF_MONTH, day );

		return calendar.getTime();
	}
}
