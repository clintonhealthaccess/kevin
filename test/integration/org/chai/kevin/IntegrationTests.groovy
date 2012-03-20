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
import org.chai.kevin.dsr.DsrTarget
import org.chai.kevin.dsr.DsrTargetCategory;
import org.chai.kevin.fct.FctTarget
import org.chai.kevin.util.JSONUtils;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.AggregationPartialValue;
import org.chai.kevin.value.AveragePartialValue;
import org.chai.kevin.value.CalculationPartialValue
import org.chai.kevin.value.RawDataElementValue
import org.chai.kevin.value.NormalizedDataElementValue
import org.chai.kevin.value.SumPartialValue;
import org.chai.kevin.value.Value;
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.location.DataEntityType;
import org.chai.kevin.location.LocationEntity;
import org.chai.kevin.location.LocationLevel;
import org.chai.kevin.reports.ReportProgram
import org.chai.kevin.security.SurveyUser;
import org.chai.kevin.security.User;
import org.hisp.dhis.period.Period

abstract class IntegrationTests extends IntegrationSpec {
	
	def refreshValueService
	def springcacheService
	def sessionFactory
	
	static final String CODE (def number) { return "CODE"+number }
	
	static final Type INVALID_TYPE = new Type("invalid_type")
	
	static final String HEALTH_CENTER_GROUP = "Health Center"
	static final String DISTRICT_HOSPITAL_GROUP = "District Hospital"
	
	static final String COUNTRY = "Country"
	static final String PROVINCE = "Province"
	static final String DISTRICT = "District"
	static final String SECTOR = "Sector"
	static final String FACILITY = "Facility"
	
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
	
	def setup() {
		// using cache.use_second_level_cache = false in test mode doesn't work so
		// we flush the cache after each test
		springcacheService.flushAll()
	}
	
	static def setupLocationTree() {
		// for the test environment, the facility level is set to 4
		// so we create a tree accordingly
		def hc = newDataEntityType(j(["en":HEALTH_CENTER_GROUP]), HEALTH_CENTER_GROUP);
		def dh = newDataEntityType(j(["en":DISTRICT_HOSPITAL_GROUP]), DISTRICT_HOSPITAL_GROUP);
		
		def country = newLocationLevel(COUNTRY, 1)
		def province = newLocationLevel(PROVINCE, 2)
		def district = newLocationLevel(DISTRICT, 3)
		def sector = newLocationLevel(SECTOR, 4)
		
		def rwanda = newLocationEntity(j(["en":RWANDA]), RWANDA, country)
		def north = newLocationEntity(j(["en":NORTH]), NORTH, rwanda, province)
		def burera = newLocationEntity(j(["en":BURERA]), BURERA, north, district)
		
		newDataLocationEntity(j(["en":BUTARO]), BUTARO, burera, dh)
		newDataLocationEntity(j(["en":KIVUYE]), KIVUYE, burera, hc)
	}

	static def setupProgramTree() {
		def period = newPeriod()
		
		def root = newReportProgram(ROOT)
		def dashboardRoot = newDashboardProgram(ROOT, root, 0)
		
		def program1 = newReportProgram(PROGRAM1, root)
		def dashboardProgram1 = newDashboardProgram(PROGRAM1, program1, 1)

		def program2 = newReportProgram(PROGRAM2, root)
		def dashboardProgram2 = newDashboardProgram(PROGRAM2, program2, 1)
		
		def dataElement1 = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"40",(HEALTH_CENTER_GROUP):"40"]]))
		def average1 = newAverage("\$"+dataElement1.id, CODE(2))
		def target1 = newDashboardTarget(TARGET1, average1, program1, 1)
		
		def dataElement2 = newNormalizedDataElement(CODE(3), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"20"]]))
		def average2 = newAverage("\$"+dataElement2.id, CODE(4))
		def target2 = newDashboardTarget(TARGET2, average2, program1, 1)
		
		def dataElement3 = newNormalizedDataElement(CODE(5), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"10",(HEALTH_CENTER_GROUP):"10"]]))
		def average3 = newAverage("\$"+dataElement3.id, CODE(6))
		def target3 = newDashboardTarget(TARGET2, average3, program2, 1)
	}
		
	static def newPeriod() {
		def period = new Period(startDate: mar01, endDate: mar31)
		return period.save(failOnError: true)
	} 
	
	static def newDataEntityType(def code) {
		return newDataEntityType([:], code)
	}
	
	static def newDataEntityType(def names, def code) {
		return new DataEntityType(names: names, code: code).save(failOnError: true)
	}
		
	static def newDataLocationEntity(def code, def location, def type) {
		return newDataLocationEntity([:], code, location, type)
	}
	
	static def newDataLocationEntity(def names, def code, def location, def type) {
		def entity = new DataLocationEntity(names: names, code: code, location: location, type: type).save(failOnError: true)
		if (location != null) {
			 location.dataEntities << entity
			 location.save(failOnError: true)
		}
		return entity
	}
	
	static def newLocationLevel(String code, def order) {
		return new LocationLevel(code: code, order: order).save(failOnError: true)
	}
	
	static def newLocationEntity(String code, def level) {
		return newLocationEntity([:], code, null, level)
	}

	static def newLocationEntity(def names, def code, def level) {
		return newLocationEntity(names, code, null, level)
	}
		
	static def newLocationEntity(String code, def parent, def level) {
		return newLocationEntity([:], code, parent, level)
	}
	
	static def newLocationEntity(def names, def code, def parent, def level) {
		def entity = new LocationEntity(names: names, code: code, parent: parent, level: level).save(failOnError: true)
		level.locations << entity
		level.save(failOnError: true)
		if (parent != null) {
			parent.children << entity
			parent.save(failOnError: true)
		}
		return entity
	}
	
	static def newDashboardProgram(def code, def program) {
		return new DashboardProgram(code: code, program: program, weight: 1).save(failOnError: true)
	}
	
	static def newDashboardProgram(def code, def program, def weight) {
		return new DashboardProgram(code: code, program: program, weight: weight).save(failOnError: true)
	}

	static def newDashboardTarget(def code, def calculation, def parent, def weight) {
		def dashboardTarget = new DashboardTarget(code: code, calculation: calculation, program: parent, weight: weight).save(failOnError: true)
		return dashboardTarget
	}

	static def newDsrTarget(def code, def dataElement, def format, def types, def program, DsrTargetCategory category) {
		def target = new DsrTarget(names: [:],
			code: code,
			format: format,
			dataElement: dataElement,
			program: program,
			category: category,
			typeCodeString: Utils.unsplit(types)
		).save(failOnError: true)
		if (category != null) {
			category.targets << target
			category.save(failOnError: true)
		}
		program.save(failOnError: true)
		return target
	}
	
	static def newDsrTarget(def code, def dataElement, def types, def program) {
		return newDsrTarget(code, dataElement, null, types, program, null)
	}
	
	static def newFctTarget(def code, def sum, def format, def types, def program) {
		def target = new FctTarget(names: [:],
			code: code,
			format: format,
			sum: sum,
			program: program,
			typeCodeString: Utils.unsplit(types)).save(failOnError: true)
			
		program.targets << target
		program.save(failOnError: true)
		return target
	}
		
	static def newFctTarget(def code, def sum, def types, def program) {
		return newFctTarget(code, sum, null, types, program)
	}
			
	static def newUser(def username, def uuid) {
		return new User(username: username, permissionString: '', passwordHash:'', uuid: uuid).save(failOnError: true)
	}
	
	static def newSurveyUser(def username, def uuid, def entityId) {
		return new SurveyUser(username: username, permissionString: '', passwordHash:'', uuid: uuid, entityId: entityId).save(failOnError: true)
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
	
	RawDataElementValue newRawDataElementValue(def rawDataElement, def period, def entity, def value) {
		return new RawDataElementValue(data: rawDataElement, period: period, entity: entity, value: value).save(failOnError: true, flush: true)
	}
	
	AggregationPartialValue newAggregationPartialValue(def aggregation, def period, def entity, def type, def expressionData, def value) {
		return new AggregationPartialValue(data: aggregation, period: period, entity: entity, type: type, expressionData: expressionData, value: value).save(failOnError: true)
	}
	
	SumPartialValue newSumPartialValue(def sum, def period, def entity, def type, def value) {
		return new SumPartialValue(data: sum, period: period, entity: entity, type: type, value: value).save(failOnError: true)
	}
	
	AveragePartialValue newAveragePartialValue(def average, def period, def entity, def type, def numberOfFacilities, def value) {
		return new AveragePartialValue(data: average, period: period, entity: entity, type: type, numberOfFacilities: numberOfFacilities, value: value).save(failOnError: true)
	}
	
	RawDataElement newRawDataElement(def code, def type) {
		return newRawDataElement(j([:]), code, type)
	}
	
	RawDataElement newRawDataElement(def names, def code, def type) {
		return newRawDataElement(names, code, type, null)
	}
	
	RawDataElement newRawDataElement(def names, def code, def type, def info) {
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
	
	NormalizedDataElementValue newNormalizedDataElementValue(def normalizedDataElement, def entity, def period, def status, def value) {
		return new NormalizedDataElementValue(data: normalizedDataElement, period: period, entity: entity, status: status, value: value).save(failOnError: true)
	}
	
	Aggregation newAggregation(def names, def expression, def code) {
		return new Aggregation(names: names, expression: expression, code: code).save(failOnError: true)
	}

	Aggregation newAggregation(def expression, def code) {
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

	static def Sum newSum(def names, def expression, def code) {
		return new Sum(names: names, expression: expression, code: code).save(failOnError: true, flush: true)
	}
	
	static def Sum newSum(def expression, def code) {
		return newSum([:], expression, code)
	}
	
	Enum newEnume(def code) {
		return new Enum(code: code).save(failOnError: true, flush: true)
	}
	
	EnumOption newEnumOption(def enume, def value) {
		return newEnumOption(enume, value, new Ordering())
	}
	
	EnumOption newEnumOption(def enume, def value, def order) {
		def enumOption = new EnumOption(enume: enume, value: value, order: order).save(failOnError: true)
		enume.addEnumOption(enumOption)
		enume.save(failOnError: true)
		return enumOption
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
	
	static def getCalculationEntity(def code) {
		def entity = LocationEntity.findByCode(code)
		if (entity == null) entity = DataLocationEntity.findByCode(code)
		return entity
	}
	
	static def getLocations(def codes) {
		def result = []
		for (String code : codes) {
			result.add(LocationEntity.findByCode(code))
		}
		return result
	}
	
	static def getDataEntities(def codes) {
		def result = []
		for (String code : codes) {
			result.add(DataLocationEntity.findByCode(code))
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
