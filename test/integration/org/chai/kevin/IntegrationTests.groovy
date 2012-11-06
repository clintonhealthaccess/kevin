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

import javax.servlet.ServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.util.WebUtils;
import org.chai.kevin.dashboard.DashboardProgram
import org.chai.kevin.dashboard.DashboardTarget
import org.chai.kevin.data.Aggregation;
import org.chai.kevin.data.Calculation;
import org.chai.kevin.data.RawDataElement
import org.chai.kevin.data.Enum
import org.chai.kevin.data.EnumOption
import org.chai.kevin.data.NormalizedDataElement;
import org.chai.kevin.data.Source;
import org.chai.kevin.data.Summ
import org.chai.kevin.data.Type;
import org.chai.kevin.exports.CalculationExport
import org.chai.kevin.exports.DataElementExport
import org.chai.kevin.exports.DataExport;
import org.chai.kevin.form.FormElement;
import org.chai.kevin.form.FormEnteredValue;
import org.chai.kevin.form.FormSkipRule;
import org.chai.kevin.form.FormValidationRule;
import org.chai.task.Progress;
import org.chai.kevin.util.JSONUtils;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.AggregationPartialValue;
import org.chai.kevin.value.CalculationPartialValue
import org.chai.kevin.value.RawDataElementValue
import org.chai.kevin.value.NormalizedDataElementValue
import org.chai.kevin.value.SumPartialValue;
import org.chai.kevin.value.Value;
import org.chai.location.CalculationLocation;
import org.chai.location.DataLocation;
import org.chai.location.DataLocationType;
import org.chai.location.Location;
import org.chai.location.LocationLevel;
import org.chai.kevin.reports.ReportProgram
import org.chai.kevin.security.User;
import org.chai.kevin.security.UserType;

abstract class IntegrationTests extends IntegrationSpec {
	
	def refreshValueService
	def springcacheService
	def sessionFactory
	
	public static final String CODE (def number) { return "CODE"+number }
	
	public static final Type INVALID_TYPE = new Type("invalid_type")
	
	public static final String HEALTH_CENTER_GROUP = "health_center"
	public static final String DISTRICT_HOSPITAL_GROUP = "district_hospital"
	
	public static final String NATIONAL = "country"
	public static final String PROVINCE = "province"
	public static final String DISTRICT = "district"
	public static final String SECTOR = "sector"
	
	public static final String RWANDA = "Rwanda"
	public static final String KIGALI_CITY = "Kigali City"
	public static final String NORTH = "North"
	public static final String BURERA = "Burera"
	public static final String BUTARO = "Butaro DH"
	public static final String KIVUYE = "Kivuye HC"
	
	public static final Date mar01 = getDate( 2005, 3, 1 );
	public static final Date mar31 = getDate( 2005, 3, 31 );
	
	public static String ROOT = "Root"
	
	public static String PROGRAM1 = "Program1"
	public static String TARGET1 = "Target 1"
	public static String TARGET2 = "Target 2"
	
	public static String PROGRAM2 = "Program2"
	public static String TARGET3 = "Target 3"
	
	public static String PROGRAM3 = "Program3"
	public static String TARGET4 = "Target 4"
	
	public static String CATEGORY1 = "Category1"
	
	def setup() {
		// using cache.use_second_level_cache = false in test mode doesn't work so
		// we flush the cache after each test
		springcacheService.flushAll()
	}
	
	static def setupLocationTree() {
		// for the test environment, the location level is set to 4
		// so we create a tree accordingly
		def hc = newDataLocationType(["en":HEALTH_CENTER_GROUP], HEALTH_CENTER_GROUP);
		def dh = newDataLocationType(["en":DISTRICT_HOSPITAL_GROUP], DISTRICT_HOSPITAL_GROUP);
		
		def country = newLocationLevel(NATIONAL, 1)
		def province = newLocationLevel(PROVINCE, 2)
		def district = newLocationLevel(DISTRICT, 3)
		def sector = newLocationLevel(SECTOR, 4)
		
		def rwanda = newLocation(["en":RWANDA], RWANDA, country)
		def north = newLocation(["en":NORTH], NORTH, rwanda, province)
		def burera = newLocation(["en":BURERA], BURERA, north, district)
		
		newDataLocation(["en":BUTARO], BUTARO, burera, dh)
		newDataLocation(["en":KIVUYE], KIVUYE, burera, hc)				
	}

	static def setupProgramTree() {
		def root = newReportProgram(ROOT)		
		def program1 = newReportProgram(PROGRAM1, 1, root)
		def program2 = newReportProgram(PROGRAM2, 2, root)
		def program3 = newReportProgram(PROGRAM3, 3, root)	
	}
		
	static def newPeriod() {
		newPeriod(2005)
	} 
	
	static def newPeriod(def code) {
		def period = new Period(code: code, startDate: mar01, endDate: mar31)
		return period.save(failOnError: true, flush: true)
	}
	
	static def newDataLocationType(def names, def code) {
		newDataLocationType(names, code, null)
	}
	
	static def newDataLocationType(def names, def code, def order) {
		def dataLocationType = new DataLocationType(code: code, order: order)
		setLocaleValueInMap(dataLocationType, names, "Names")
		return dataLocationType.save(failOnError: true)
	}
	
	static def newLocationLevel(String code, def order) {
		def locationLevel = new LocationLevel(code: code, order: order)
		return locationLevel.save(failOnError: true)
	}
	
	static def newLocation(def names, def code, def level) {
		return newLocation(names, code, null, level)
	}
	
	static def newLocation(def names, def code, def parent, def level) {
		def location = new Location(code: code, parent: parent, level: level)
		setLocaleValueInMap(location,names,"Names")
		location.save(failOnError: true)
		level.addToLocations(location)
		level.save(failOnError: true)
		if (parent != null) {
			parent.addToChildren(location)
			parent.save(failOnError: true)
		}
		return location
	}
	
	static def newDataLocation(def names, def code, def location, def type) {
		def dataLocation = new DataLocation(code: code, location: location, type: type)
		setLocaleValueInMap(dataLocation,names,"Names")
		dataLocation.save(failOnError: true)
		if (location != null) {
			location.addToDataLocations(dataLocation)
			location.save(failOnError: true)
		}
		if (type != null) {
			type.addToDataLocations(dataLocation)
			type.save(failOnError: true)
	   }
		return dataLocation
	}
	
	static def newDataElementExport(def code, def descriptions, def periods, def locationType, def locations, def dataElements){
		def export = new DataElementExport(code: code, periods:periods, typeCodeString:Utils.unsplit(locationType, Utils.DEFAULT_TYPE_CODE_DELIMITER), locations:locations, dataElements:dataElements, date:new Date())
		setLocaleValueInMap(export, descriptions, "Descriptions")
		export.save(failOnError: true)
		return export
	}
	
	static def newCalculationExport(def code, def descriptions, def periods, def locationType, def locations, def calculations){
		def export = new CalculationExport(code: code, periods:periods,typeCodeString:Utils.unsplit(locationType, Utils.DEFAULT_TYPE_CODE_DELIMITER),locations:locations,calculations:calculations,date:new Date())
		setLocaleValueInMap(export, descriptions, "Descriptions")
		export.save(failOnError: true)
		return export
	}
		
	static def newUser(def username, def uuid) {
		return new User(userType: UserType.OTHER, code: username, username: username, permissionString: '', passwordHash:'', uuid: uuid, firstname: 'first', lastname: 'last', organisation: 'org', phoneNumber: '+250 11 111 11 11', defaultLanguage: 'en').save(failOnError: true)
	}
	
	static def newUser(def username, def active, def confirmed) {
		return new User(userType: UserType.OTHER, code: 'not_important', username: username, email: username,
			passwordHash: '', active: active, confirmed: confirmed, uuid: 'uuid', firstname: 'first', lastname: 'last',
			organisation: 'org', phoneNumber: '+250 11 111 11 11', defaultLanguage: 'en').save(failOnError: true)
	}
	
	static def newUser(def username, def passwordHash, def active, def confirmed) {
		return new User(userType: UserType.OTHER, code: 'not_important', username: username, email: username,
			passwordHash: passwordHash, active: active, confirmed: confirmed, uuid: 'uuid', firstname: 'first', lastname: 'last',
			organisation: 'org', phoneNumber: '+250 11 111 11 11', defaultLanguage: 'en').save(failOnError: true)
	}
	
	static def newSurveyUser(def username, def uuid, def locationId) {
		return new User(userType: UserType.SURVEY, code: username, username: username, permissionString: '', passwordHash:'', uuid: uuid, locationId: locationId, firstname: 'first', lastname: 'last', organisation: 'org', phoneNumber: '+250 11 111 11 11', defaultLanguage: 'en').save(failOnError: true)
	}
	
	static def newPlanningUser(def username, def uuid, def locationId) {
		return new User(userType: UserType.PLANNING, code: username, username: username, permissionString: '', passwordHash:'', uuid: uuid, locationId: locationId, firstname: 'first', lastname: 'last', organisation: 'org', phoneNumber: '+250 11 111 11 11', defaultLanguage: 'en').save(failOnError: true)
	}
	
	static def newReportProgram(def code) {
		return newReportProgram(code, null, null)
	}
		
	static def newReportProgram(String code, Integer order) {
		return newReportProgram(code, order, null)
	}
	
	static def newReportProgram(String code, ReportProgram parent) {
		return newReportProgram(code, null, parent)
	}
	
	static def newReportProgram(String code, Integer order, def parent) {
		return newReportProgram(null, code, order, parent)
	}
	
	static def newReportProgram(def names, String code, Integer order, def parent) {
		def reportProgram = new ReportProgram(code: code, parent: parent, order: order).save(failOnError: true, flush: true);
		setLocaleValueInMap(reportProgram, names, "Names")
		if (parent != null) {
			 parent.addToChildren(reportProgram)
			 parent.save(failOnError: true)
		}
		return reportProgram
	}
	
	static def newReportProgram(def code, def parent, def children){
		return new ReportProgram(code: code, parent: parent, children: children).save(failOnError: true, flush: true);
	}
	
	static RawDataElementValue newRawDataElementValue(def rawDataElement, def period, def location, def value) {
		return new RawDataElementValue(data: rawDataElement, period: period, location: location, value: value).save(failOnError: true, flush: true)
	}
	
	static AggregationPartialValue newAggregationPartialValue(def aggregation, def period, def location, def type, def expressionData, def value) {
		return new AggregationPartialValue(data: aggregation, period: period, location: location, type: type, expressionData: expressionData, value: value).save(failOnError: true)
	}
	
	static SumPartialValue newSumPartialValue(def sum, def period, def location, def type, def numberOfDataLocations, def value) {
		return new SumPartialValue(data: sum, period: period, location: location, type: type, numberOfDataLocations: numberOfDataLocations, value: value).save(failOnError: true)
	}
	
	static SumPartialValue newSumPartialValue(def sum, def period, def location, def type, def value) {
		return new SumPartialValue(data: sum, period: period, location: location, type: type, numberOfDataLocations:0, value: value).save(failOnError: true)
	}
	
	static RawDataElement newRawDataElement(def code, def type) {
		return newRawDataElement(null, code, type)
	}
	
	static RawDataElement newRawDataElement(def code, def type, Source source) {
		return newRawDataElement(null, code, type, null, source)
	}
	
	static RawDataElement newRawDataElement(def names, def code, def type, String info) {
		return newRawDataElement(names, code, type, info, null)
	}
	
	static RawDataElement newRawDataElement(def names, def code, def type) {
		return newRawDataElement(names, code, type, null, null)
	}
	
	static RawDataElement newRawDataElement(def names, def code, def type, def info, def source) {
		def dataElement = new RawDataElement(code: code, type: type, info: info, source: source).save(failOnError: true, flush:true)
		setLocaleValueInMap(dataElement, names, "Names")
		return dataElement
	}
	
	static Source newSource(def code) {
		return new Source(code: code).save(failOnError: true, flush: true)
	}

	static def newNormalizedDataElement(def names, def code, def type, def expressionMap, Map params) {
		params << [failOnError: true]
		params << [flush: true]
		def dataElement = new NormalizedDataElement(code: code, type: type, expressionMap: expressionMap).save(params)
		setLocaleValueInMap(dataElement, names, "Names")
		return dataElement
	}

	static def newNormalizedDataElement(def names, String code, def type, def expressionMap) {
		return newNormalizedDataElement(names, code, type, expressionMap, [:])
	}
	
	static def newNormalizedDataElement(def code, Type type, def expressionMap) {
		return newNormalizedDataElement(null, code, type, expressionMap, [:])
	}
	
	static def newNormalizedDataElement(def code, Type type, def expressionMap, Map params) {
		return newNormalizedDataElement(null, code, type, expressionMap, params)
	}
	
	static NormalizedDataElementValue newNormalizedDataElementValue(def normalizedDataElement, def location, def period, def status, def value) {
		return new NormalizedDataElementValue(data: normalizedDataElement, period: period, location: location, status: status, value: value).save(failOnError: true)
	}
	
	static Aggregation newAggregation(def names, def expression, def code) {
		def data = new Aggregation(expression: expression, code: code).save(failOnError: true)
		setLocaleValueInMap(data, names, "Names")
		return data
	}

	static Aggregation newAggregation(def expression, def code) {
		return newAggregation(null, expression, code)
	}

	static def newSum(String expression, def code) {
		return newSum(null, expression, code)
	}
	
	static Summ newSum(def names, def expression, def code) {
		def data = new Summ(expression: expression, code: code).save(failOnError: true, flush: true)
		setLocaleValueInMap(data, names, "Names")
		return data
	}
	
	static Summ newSum(def expression, def code) {
		return newSum(null, expression, code)
	}
	
	static Enum newEnume(def code) {
		return newEnume(code, null)
	}
	
	static Enum newEnume(def code, def names) {
		def enume = new Enum(code: code).save(failOnError: true, flush: true)
		setLocaleValueInMap(enume, names, "Names")
		return enume;
	}
	
	static EnumOption newEnumOption(def enume, def value) {
		return newEnumOption(null, enume, value, null)
	}
	
	static EnumOption newEnumOption(def names, Enum enume, def value) {
		return newEnumOption(names, enume, value, null)
	}
	
	static EnumOption newEnumOption(Enum enume, def value, def order) {
		return newEnumOption(null, enume, value, order)
	}
	
	static EnumOption newEnumOption(def names, Enum enume, def value, def order) {
		def enumOption = new EnumOption(code: enume.code+value, enume: enume, value: value).save(failOnError: true)
		setLocaleValueInMap(enumOption, names, "Names")
		setLocaleValueInMap(enumOption, order, "Orders")
		enume.addToEnumOptions(enumOption)
		enume.save(failOnError: true)
		return enumOption
	}
	
	def static newFormEnteredValue(def element, def period, def dataLocation, def value) {
		return new FormEnteredValue(formElement: element, value: value, dataLocation: dataLocation).save(failOnError: true, flush: true)
	}
	
	def static newFormValidationRule(def code, def element, def prefix, def types, def expression, boolean allowOutlier, def dependencies = []) {
		def validationRule = new FormValidationRule(code: code, expression: expression, prefix: prefix, formElement: element, typeCodeString: Utils.unsplit(types, Utils.DEFAULT_TYPE_CODE_DELIMITER), dependencies: dependencies, allowOutlier: allowOutlier).save(failOnError: true)
		element.addToValidationRules(validationRule)
		element.save(failOnError: true)
		return validationRule
	}
	
	def static newFormValidationRule(def code, def element, def prefix, def types, def expression, def dependencies = []) {
		return newFormValidationRule(code, element, prefix, types, expression, false, dependencies)
	}
	
	def static newFormSkipRule(def code, def expression, def skippedElements) {
		return new FormSkipRule(code: code, expression: expression, skippedFormElements: skippedElements).save(failOnError: true)
	}
	
	def static newFormElement(def dataElement) {
		return newFormElement(dataElement, [:])
	}
	
	def static newFormElement(def dataElement, def headers) {
		def element = new FormElement(dataElement: dataElement)
		element.setHeaders(headers)
		element.save(failOnError: true)
		return element
	}

	def refresh() {
		refreshNormalizedDataElement()
		refreshCalculation()
	}
	
	def refreshNormalizedDataElement() {
		sessionFactory.currentSession.flush()
		
		NormalizedDataElement.list().each { nde ->
			Period.list().each { period ->
				DataLocation.list().each { location ->
					refreshValueService.updateNormalizedDataElementValue(nde, location, period)
				}
			}
		}
	}
	
	def refreshCalculation() {
		sessionFactory.currentSession.flush()
		
		Period.list().each { period ->
			sessionFactory.currentSession.createCriteria(CalculationLocation.class).list().each { location ->
				Summ.list().each { sum ->
					refreshValueService.updateCalculationPartialValues(sum, location, period)
				}
				Aggregation.list().each { aggregation ->
					refreshValueService.updateCalculationPartialValues(aggregation, location, period)
				}
			}
		}
	}
	
	def setupSecurityManager(def user) {
		def subject = [getPrincipal: { user?.uuid }, isAuthenticated: { user==null?false:true }, login: { token -> null }] as Subject
		ThreadContext.put( ThreadContext.SECURITY_MANAGER_KEY, [ getSubject: { subject } ] as SecurityManager )
		SecurityUtils.metaClass.static.getSubject = { subject }
		WebUtils.metaClass.static.getSavedRequest = { ServletRequest request -> null }
	}
	
	static def getCalculationLocation(def code) {
		def location = Location.findByCode(code)
		if (location == null) location = DataLocation.findByCode(code)
		return location
	}
	
	static def g(def types) {
		return Utils.unsplit(types, Utils.DEFAULT_TYPE_CODE_DELIMITER)
	}
	
	static s(def list) {
		return new HashSet(list)
	}
	
	static v(def value) {
		return new Value("{\"value\":"+value+"}");
	}
	
	static Date getDate( int year, int month, int day ) {
		final Calendar calendar = Calendar.getInstance();

		calendar.clear();
		calendar.set( Calendar.YEAR, year );
		calendar.set( Calendar.MONTH, month - 1 );
		calendar.set( Calendar.DAY_OF_MONTH, day );

		return calendar.getTime();
	}
	
	static def setLocaleValueInMap(def object, def map, def fieldName) {
		if (map == null) return
		
		def methodName = 'set'+fieldName
		// TODO replace with CONF variable if this fails
		def grailsApplication = new User().domainClass.grailsApplication
		grailsApplication.config.i18nFields.locales.each{ loc ->
			if(map.get(loc) != null) object."$methodName"(map.get(loc)+'', new Locale(loc))
			else object."$methodName"("", new Locale(loc))
		}
	}
	
}

class TestProgress implements Progress {
	
	def progress = 0
	def max = 0
	def aborted = false
	
	void incrementProgress() {
		progress++
	}
	
	void incrementProgress(Long increment) {
		progress += increment
	}
	
	Double retrievePercentage() {
		return null;
	}
	
	void setMaximum(Long max) {
		this.max = max
	}
	
	void abort() {
		this.aborted = true
	}
	
	boolean isAborted() {
		return aborted
	}
	
}
