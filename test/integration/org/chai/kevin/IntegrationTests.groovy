package org.chai.kevin

/*
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
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.chai.kevin.data.Aggregation;
import org.chai.kevin.data.Average
import org.chai.kevin.data.RawDataElement
import org.chai.kevin.data.Enum
import org.chai.kevin.data.EnumOption
import org.chai.kevin.data.ExpressionMap;
import org.chai.kevin.data.NormalizedDataElement;
import org.chai.kevin.data.Sum
import org.chai.kevin.data.Type;
import org.chai.kevin.util.JSONUtils;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.AggregationPartialValue;
import org.chai.kevin.value.AveragePartialValue;
import org.chai.kevin.value.CalculationPartialValue
import org.chai.kevin.value.RawDataElementValue
import org.chai.kevin.value.NormalizedDataElementValue
import org.chai.kevin.value.SumPartialValue;
import org.chai.kevin.value.Value;
import org.chai.kevin.security.SurveyUser;
import org.chai.kevin.security.User;
import org.hisp.dhis.organisationunit.OrganisationUnit
import org.hisp.dhis.organisationunit.OrganisationUnitGroup
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet
import org.hisp.dhis.organisationunit.OrganisationUnitLevel
import org.hisp.dhis.period.MonthlyPeriodType
import org.hisp.dhis.period.Period

abstract class IntegrationTests extends IntegrationSpec {
	
	def refreshValueService
	def springcacheService
	
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
	
	static final String GROUP_SET_TYPE = "Type"
	
	static Date mar01 = getDate( 2005, 3, 1 );
	static Date mar31 = getDate( 2005, 3, 31 );
	
	def setup() {
		springcacheService.flushAll()
	}
	
	def setupOrganisationUnitTree() {
		// for the test environment, the facility level is set to 4
		// so we create a tree accordingly
		def set = newOrganisationUnitGroupSet(GROUP_SET_TYPE);
		def hc = newOrganisationUnitGroup(HEALTH_CENTER_GROUP, set);
		def dh = newOrganisationUnitGroup(DISTRICT_HOSPITAL_GROUP, set);
		
		newOrganisationUnitLevel(COUNTRY, 1)
		newOrganisationUnitLevel(PROVINCE, 2)
		newOrganisationUnitLevel(DISTRICT, 3)
		newOrganisationUnitLevel(FACILITY, 4)
		
		def rwanda = newOrganisationUnit(RWANDA)
		def north = newOrganisationUnit(NORTH, rwanda)
		def burera = newOrganisationUnit(BURERA, north)
		
		newOrganisationUnit(BUTARO, burera, dh)
		newOrganisationUnit(KIVUYE, burera, hc)
	}
	
	Period newPeriod() {
		def monthly = new MonthlyPeriodType();
		monthly.save(failOnError: true)
		def period = new Period(periodType: monthly, startDate: mar01, endDate: mar31)
		return period.save(failOnError: true)
	} 
	
	private OrganisationUnitLevel newOrganisationUnitLevel(def name, def level) {
		return new OrganisationUnitLevel(level: level, name: name).save(failOnError: true)
	}
	
	private OrganisationUnit newOrganisationUnit(def name) {
		return newOrganisationUnit(name, null, null)
	}
	
	private OrganisationUnit newOrganisationUnit(def name, def parent) {
		return newOrganisationUnit(name, parent, null)
	}
	
	private OrganisationUnit newOrganisationUnit(def name, def parent, def group) {
		def organisation = new OrganisationUnit(name: name, shortName: name).save(failOnError: true)
		if (group != null) {
			organisation.groups << group
			group.members << organisation
			group.save(failOnError: true)
		}
		if (parent != null) {
			organisation.parent = parent
			parent.children << organisation
			parent.save(failOnError: true)
		}
		organisation.save(failOnError: true, flush: true)
		return organisation
	}
	
	private OrganisationUnitGroupSet newOrganisationUnitGroupSet(def name) {
		return new OrganisationUnitGroupSet(name: name).save(failOnError: true)
	} 
	
	private OrganisationUnitGroup newOrganisationUnitGroup(def name, def set) {
		def group = new OrganisationUnitGroup(name: name, uuid: name, groupSet: set).save(failOnError: true)
		set.organisationUnitGroups << group
		set.save(failOnError: true)
		return group
	}
	
	def newUser(def username, def uuid) {
		return new User(username: username, permissionString: '', passwordHash:'', uuid: uuid).save(failOnError: true)
	}
	
	def newSurveyUser(def username, def uuid, def organisation) {
		return new SurveyUser(username: username, permissionString: '', passwordHash:'', uuid: uuid, organisationUnitId: organisation.id).save(failOnError: true)
	}
	
	RawDataElementValue newRawDataElementValue(def rawDataElement, def period, def organisationUnit, def value) {
		return new RawDataElementValue(data: rawDataElement, period: period, organisationUnit: organisationUnit, value: value).save(failOnError: true)
	}
	
	AggregationPartialValue newAggregationPartialValue(def aggregation, def period, def organisationUnit, def groupUuid, def expressionData, def hasMissingValues, def hasMissingExpression, def value) {
		return new AggregationPartialValue(data: aggregation, period: period, organisationUnit: organisationUnit, groupUuid: groupUuid, expressionData: expressionData, hasMissingValues: hasMissingValues, hasMissingExpression: hasMissingExpression, value: value).save(failOnError: true)
	}
	
	SumPartialValue newSumPartialValue(def sum, def period, def organisationUnit, def groupUuid, def hasMissingValues, def hasMissingExpression, def value) {
		return new SumPartialValue(data: sum, period: period, organisationUnit: organisationUnit, groupUuid: groupUuid, hasMissingValue: hasMissingValues, hasMissingExpression: hasMissingExpression, value: value).save(failOnError: true)
	}
	
	AveragePartialValue newAveragePartialValue(def average, def period, def organisationUnit, def groupUuid, def numberOfFacilities, def hasMissingValues, def hasMissingExpression, def value) {
		return new AveragePartialValue(data: average, period: period, organisationUnit: organisationUnit, groupUuid: groupUuid, numberOfFacilities: numberOfFacilities, hasMissingValue: hasMissingValues, hasMissingExpression: hasMissingExpression, value: value).save(failOnError: true)
	}
	
	RawDataElement newRawDataElement(def code, def type) {
		return newRawDataElement(j([:]), code, type)
	}
	
	RawDataElement newRawDataElement(def names, def code, def type) {
		return newRawDataElement(names, code, type, null)
	}
	
	RawDataElement newRawDataElement(def names, def code, def type, def info) {
		return new RawDataElement(names: names, code: code, type: type, info: info).save(failOnError: true)
	}

	NormalizedDataElement newNormalizedDataElement(def names, def code, def type, def expressionMap) {
		return new NormalizedDataElement(names: names, code: code, type: type, expressionMap: expressionMap).save(failOnError:true)
	}

	NormalizedDataElement newNormalizedDataElement(def code, def type, def expressionMap) {
		return newNormalizedDataElement([:], code, type, expressionMap)
	}
	
	NormalizedDataElementValue newNormalizedDataElementValue(def normalizedDataElement, def organisationUnit, def period, def status, def value) {
		return new NormalizedDataElementValue(data: normalizedDataElement, period: period, organisationUnit: organisationUnit, status: status, value: value).save(failOnError: true)
	}
	
//	NormalizedDataElementValue newExpressionValue(def expression, def period, def organisationUnit, def status, def value) {
//		return new NormalizedDataElementValue(expression: expression, period: period, organisationUnit: organisationUnit, status: status, value: value).save(failOnError: true)
//	}
	
//	NormalizedDataElementValue newExpressionValue(def expression, def period, def organisationUnit) {
//		return newExpressionValue(expression, period, organisationUnit, Status.VALID, Value.NULL)
//	}


//	Expression newExpression(def code, def type, String formula, def arguments = [:]) {
//		return newExpression([:], code, type, formula, arguments)
//	}
		
//	Expression newExpression(def names, def code, def type, String formula, def arguments = [:]) {
//		arguments.failOnError = true
//		return new Expression(names: names, code: code, type: type, expression: formula).save(arguments)
//	}
	
	Aggregation newAggregation(def names, def expression, def code) {
		return new Aggregation(names: names, expression: expression, code: code).save(failOnError: true)
	}

	Aggregation newAggregation(def expression, def code) {
		return newAggregation([:], expression, code)
	}

	Average newAverage(def names, def expression, def code) {
		return new Average(names: names, expression: expression, code: code).save(failOnError: true)
	}

	Average newAverage(def expression, def code) {
		return newAverage([:], expression, code)
	}

	Sum newSum(def names, def expression, def code) {
		return new Sum(names: names, expression: expression, code: code).save(failOnError: true, flush: true)
	}
	
	Sum newSum(def expression, def code) {
		return newSum([:], expression, code)
	}
	
	Enum newEnume(def code) {
		return new Enum(code: code).save(failOnError: true)
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
		Calculation.list().each {
			refreshValueService.refreshCalculation(it)
		}
	}
	
	def setupSecurityManager(def user) {
		def subject = [getPrincipal: { user?.uuid }, isAuthenticated: { user==null?false:true }] as Subject
		ThreadContext.put( ThreadContext.SECURITY_MANAGER_KEY, [ getSubject: { subject } ] as SecurityManager )
		SecurityUtils.metaClass.static.getSubject = { subject }
	}
	
	static def g(def groups) {
		return Utils.unsplit(groups)
	}
	
	static def getOrganisationUnitLevels(def levels) {
		def result = []
		for (def level : levels) {
			result.add OrganisationUnitLevel.findByLevel(new Integer(level).intValue())
		}
		return result;
	}
	
	static def getOrganisation(def name) {
		return new Organisation(OrganisationUnit.findByName(name))
	}
	
	static def getOrganisations(def names) {
		def result = []
		for (String name : names) {
			result.add(getOrganisation(name))
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
