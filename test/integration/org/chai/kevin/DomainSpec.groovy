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

import grails.validation.ValidationException;

import java.util.List;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.Enum;
import org.chai.kevin.data.EnumOption;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.Initializer;
import org.chai.kevin.data.Average;
import org.chai.kevin.data.Calculation;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.Enum;
import org.chai.kevin.data.Expression;
import org.chai.kevin.data.Sum;
import org.chai.kevin.data.Type;
import org.chai.kevin.survey.Survey
import org.chai.kevin.survey.SurveyElement
import org.chai.kevin.survey.SurveyObjective
import org.chai.kevin.survey.SurveySection
import org.chai.kevin.survey.SurveyTableColumn
import org.chai.kevin.survey.SurveyTableQuestion
import org.chai.kevin.survey.SurveyTableRow
import org.chai.kevin.util.JSONUtils;
import org.chai.kevin.value.ExpressionValue;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.json.JSONObject;
import org.springframework.dao.DataIntegrityViolationException;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;

class DomainSpec extends IntegrationTests {

	private static final Log log = LogFactory.getLog(DomainSpec.class)

	def setup() {
		Initializer.createDummyStructure();
	}

	//	def "expression date is updated on save"() {
	//		setup:
	//		new Expression(code:"CODE", expression: "1", type: JSONUtils.TYPE_NUMBER, timestamp: new Date()).save(failOnError: true)
	//
	//		when:
	//		def expression = Expression.findByCode("CODE");
	//		def oldDate = expression.timestamp
	//
	//		then:
	//		oldDate != null
	//
	//		when:
	//		expression.save(failOnError: true)
	//		expression = Expression.findByCode("CODE");
	//		def newDate = expression.timestamp
	//
	//		then:
	//		!oldDate.equals(newDate)
	//
	//	}

	def "organisation unit hashcode and equals"() {
		when:
		def org1 = new OrganisationUnit(name: "Test")
		def org2 = new OrganisationUnit(name: "Test")

		then:
		org1.hashCode() == org2.hashCode()
		org1.equals(org2)
		org2.equals(org1)

	}

	//	def "period hashcode and equals"() {
	//		when:
	//		def period1 =
	//		def period2 =
	//
	//		then:
	//		org1.hashCode() == org2.hashCode()
	//		org1.equals(org2)
	//		org2.equals(org1)
	//
	//	}

	def "expression value hashcode and equals"() {
		setup:
		new Expression(code: "EXPR", expression:"10", type: JSONUtils.TYPE_NUMBER).save(failOnError: true)

		when:
		def expr1 = new ExpressionValue(period: Period.list()[0], organisationUnit: OrganisationUnit.findByName("Butaro DH"), expression: Expression.findByCode("EXPR"));
		def expr2 = new ExpressionValue(period: Period.list()[0], organisationUnit: OrganisationUnit.findByName("Butaro DH"), expression: Expression.findByCode("EXPR"));

		then:
		expr1.hashCode() == expr2.hashCode();
		expr1.equals(expr2)
		expr2.equals(expr1)

		when:
		def set = new HashSet()
		set.add(expr1)

		then:
		set.contains(expr2)
	}

	def "invalid expression"() {
		when:
		new Expression(code:"CODE", expression: formula, type: JSONUtils.TYPE_NUMBER).save(failOnError:true)

		then:
		thrown ValidationException

		where:
		formula << [
			"if((123) 1 else 0",
			"if(3) 3",
			"if(\$328==1 || \$286==1 || \$277==1 || \$215==1) \"&#10003;\" else \"NEGS\""
		]
	}

	def "cannot delete expression with associated calculation"() {
		setup:
		def expression = new Expression(code: "EXPR", expression:"10", type: JSONUtils.TYPE_NUMBER).save(failOnError: true)
		def calculation = new Average(expressions: [
			"Health Center": Expression.findByCode("EXPR")
		], timestamp:new Date(), type: JSONUtils.TYPE_NUMBER)
		calculation.save(failOnError: true)
		
		when:
		expression.delete()
		
		then:
		thrown Exception
	}
	
	def "data element code is unique"() {
		when:
		new DataElement(code: "CODE", type: JSONUtils.TYPE_NUMBER).save(failOnError:true)
		new DataElement(code: "CODE", type: JSONUtils.TYPE_NUMBER).save(failOnError:true)

		then:
		thrown ValidationException

	}
	
	def "data element enum is present when type is enum"() {
		when:
		new DataElement(code: "CODE", type: JSONUtils.TYPE_ENUM).save(failOnError:true)
		
		then:
		thrown ValidationException
		
		when:
		def enume = new Enum(code: "ENUM").save(failOnError:true)
		new DataElement(code: "CODE", type: JSONUtils.TYPE_ENUM, enume: enume).save(failOnError:true)
		
		then:
		DataElement.count() == 1
	}
	
	def "data element type is valid"() {
		when:
		new DataElement(code: "CODE", type: "{\"type\":\"number\"}").save(failOnError: true)
		
		then:
		DataElement.count() == 1
	}
	
//	def "constant saved properly" () {
//		when:
//		new Constant(names:j(["en":"Constant"]), code:"CONST", value:"10", type:JSONUtils.TYPE_NUMBER).save(failOnError: true)
//
//		then:
//		Constant.count() == 1
//	}
//
//	def "constant code cannot be null"() {
//		when:
//		new Constant(names:j(["en":""]), code:"CODE", value:"10", type:JSONUtils.TYPE_NUMBER).save(failOnError: true)
//
//		then:
//		Constant.count() == 1
//
//		when:
//		new Constant(names:j(["en":""]), value:"10", type:JSONUtils.TYPE_NUMBER).save(failOnError: true)
//
//		then:
//		thrown ValidationException
//	}
//
//	def "constant code is unique"() {
//		when:
//		new Constant(names:j(["en":""]), code:"CODE", value:"10", type:JSONUtils.TYPE_NUMBER).save(failOnError: true)
//
//		then:
//		Constant.count() == 1
//
//		when:
//		new Constant(names:j(["en":""]), code:"CODE", value:"10", type:JSONUtils.TYPE_NUMBER).save(failOnError: true)
//
//		then:
//		thrown ValidationException
//	}
//
//	def "constant type cannot be null"() {
//		when:
//		new Constant(names:j(["en":""]), code:"CODE1", value:"10", type:JSONUtils.TYPE_NUMBER).save(failOnError: true)
//
//		then:
//		Constant.count() == 1
//
//		when:
//		new Constant(names:j(["en":""]), code:"CODE2", value:"10").save(failOnError: true)
//
//		then:
//		thrown ValidationException
//	}
//
//	def "constant value cannot be empty"() {
//		when:
//		new Constant(names:j(["en":"Constant"]), code:"CONST", value:"", type:JSONUtils.TYPE_NUMBER).save(failOnError: true)
//
//		then:
//		thrown ValidationException
//	}
//
//	def "constant value cannot be null"() {
//		when:
//		new Constant(names:j(["en":"Constant"]), code:"CONST", type:JSONUtils.TYPE_NUMBER).save(failOnError: true)
//
//		then:
//		thrown ValidationException
//	}
//
//
//	def "constant constraint: code cannot be blank"() {
//		when:
//		new Constant(names:j(["en":"Constant"]), code:"", value:"1", type: JSONUtils.TYPE_NUMBER).save(failOnError:true)
//
//		then:
//		thrown ValidationException
//	}
//
//	def "constant constraint: code is unique"() {
//		when:
//		new Constant(names:j(["en":"Constant"]), code:"Unique", value:"1", type: JSONUtils.TYPE_NUMBER).save(failOnError:true)
//
//		then:
//		Constant.count() == 1
//
//		when:
//		new Constant(names:j(["en":"Constant"]), code:"Unique", value:"1", type: JSONUtils.TYPE_NUMBER).save(failOnError:true)
//
//		then:
//		thrown ValidationException
//	}
	
	def "average type cannot be invalid"() {
		when:
		new Average(expressions: [:], code:"C", type:new Type("123")).save(failOnError:true)
		
		then:
		thrown ValidationException
	}
	
	def "sum type cannot be invalid"() {
		when:
		new Sum(expressions: [:], code:"SUM", type:new Type("123")).save(failOnError:true)
		
		then:
		thrown ValidationException
	}
	
	def "data element type cannot be invalid"() {
		when:
		new DataElement(names:j(["en":"Expression"]), code:"DATA", type:new Type("123")).save(failOnError:true)
		
		then:
		thrown ValidationException
	}
	
	def "expression type cannot be invalid"() {
		when:
		new Expression(names:j(["en":"Expression"]), code:"EXPR", type:new Type("123"), expression:"1").save(failOnError:true)
		
		then:
		thrown ValidationException
	}

	def "expression type cannot be null"() {
		when:
		def expression = new Expression(names:j(["en":"Expression"]), code:"EXPR")
		expression.save(failOnError:true)

		then:
		thrown ValidationException

	}

	def "expression code is unique"() {
		when:
		new Expression(names:j(["en":"Expression"]), code:"EXPR", type:JSONUtils.TYPE_NUMBER, expression:"1").save(failOnError:true)

		then:
		Expression.count();

		when:
		new Expression(names:j(["en":"Expression"]), code:"EXPR", type:JSONUtils.TYPE_NUMBER, expression:"1").save(failOnError:true)

		then:
		thrown ValidationException

	}

//	def "expression can be a constant"() {
//		setup:
//		IntegrationTestInitializer.createConstants()
//
//		when:
//		new Expression(names:j(["en":"Expression"]), code:"EXPR", type:JSONUtils.TYPE_NUMBER, expression:"["+Constant.findByCode("CONST1").id+"]").save(failOnError:true)
//
//		then:
//		Expression.count() == 1;
//
//	}


	def "empty name transfers properly to json"() {
		when:
		new DataElement(names:new Translation(), code:"TEST", type: JSONUtils.TYPE_NUMBER).save(failOnError:true)

		then:
		def dataElement = DataElement.findByCode("TEST");
		dataElement.names["en"] == null
		dataElement.names["fr"] == null
	}


	def "translatable set map sets json"() {
		when:
		new DataElement(names:j(["en":"English", "fr":"Francais"]), code:"TEST", type: JSONUtils.TYPE_NUMBER).save(failOnError:true)

		then:
		def dataElement = DataElement.findByCode("TEST");
		(new HashMap(["en":"English", "fr":"Francais"])).equals(dataElement.names)
		dataElement.names["en"] == "English"
		dataElement.names["fr"] == "Francais"
	}


	def "translatable set json sets map"() {
		when:
		new DataElement(names:j("en":"test"), code:"TEST", type: JSONUtils.TYPE_NUMBER).save(failOnError:true)
		def dataElement = DataElement.findByCode("TEST")
		dataElement.names.putAll([en: "English", fr: "Anglais"]);
		dataElement.save(failOnError:true);
		dataElement = DataElement.findByCode("TEST")

		then:
		dataElement.names["en"] == "English"
		dataElement.names["fr"] == "Anglais"
	}


	def "translatable set map modifies json"() {
		when:
		new DataElement(names:new Translation(), code:"TEST", type: JSONUtils.TYPE_NUMBER).save(failOnError:true)
		def dataElement = DataElement.findByCode("TEST")
		dataElement.names = new Translation(jsonText: JSONUtils.getJSONFromMap([en: "English", fr: "Anglais"]));
		dataElement.save(failOnError:true)
		dataElement.names = new Translation(jsonText: JSONUtils.getJSONFromMap([en: "English"]));
		dataElement.save(failOnError: true)
		dataElement = DataElement.findByCode("TEST");

		then:
		dataElement.names.getJsonText() == new JSONObject().put("en", "English").toString()
		dataElement.names["en"] == "English"
		dataElement.names["fr"] == null
	}

}
