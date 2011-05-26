package org.chai.kevin

import grails.validation.ValidationException;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.Initializer;
import org.json.JSONObject;
import org.springframework.dao.DataIntegrityViolationException;

class DomainSpec extends IntegrationTests {

	private static final Log log = LogFactory.getLog(DomainSpec.class)

	def setup() {
		Initializer.createDummyStructure();
	}
	
	def "data element code is unique"() {
		when:
		new DataElement(code: "CODE", type: ValueType.VALUE).save(failOnError:true)
		new DataElement(code: "CODE", type: ValueType.VALUE).save(failOnError:true)
		
		then:
		thrown ValidationException
		
	}
	
	def "constant saved properly" () {
		when:
		new Constant(names:j(["en":"Constant"]), code:"CONST", value:"10", type:ValueType.VALUE).save(failOnError: true)
		
		then:
		Constant.count() == 1
	}
	
	def "constant code cannot be null"() {
		when:
		new Constant(names:j(["en":""]), code:"CODE", value:"10", type:ValueType.VALUE).save(failOnError: true)
		
		then:
		Constant.count() == 1
		
		when:
		new Constant(names:j(["en":""]), value:"10", type:ValueType.VALUE).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "constant code is unique"() {
		when:
		new Constant(names:j(["en":""]), code:"CODE", value:"10", type:ValueType.VALUE).save(failOnError: true)
		
		then:
		Constant.count() == 1
		
		when:
		new Constant(names:j(["en":""]), code:"CODE", value:"10", type:ValueType.VALUE).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "constant type cannot be null"() {
		when:
		new Constant(names:j(["en":""]), code:"CODE1", value:"10", type:ValueType.VALUE).save(failOnError: true)
		
		then:
		Constant.count() == 1
		
		when:
		new Constant(names:j(["en":""]), code:"CODE2", value:"10").save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "constant value cannot be empty"() {
		when:
		new Constant(names:j(["en":"Constant"]), code:"CONST", value:"", type:ValueType.VALUE).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "constant value cannot be null"() {
		when:
		new Constant(names:j(["en":"Constant"]), code:"CONST", type:ValueType.VALUE).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	
	def "constant constraint: code cannot be blank"() {
		when:
		new Constant(names:j(["en":"Constant"]), code:"", value:"1", type: ValueType.VALUE).save(failOnError:true)
		
		then:
		thrown ValidationException
	}
	
	def "constant constraint: code is unique"() {
		when:
		new Constant(names:j(["en":"Constant"]), code:"Unique", value:"1", type: ValueType.VALUE).save(failOnError:true)
		
		then:
		Constant.count() == 1
		
		when:
		new Constant(names:j(["en":"Constant"]), code:"Unique", value:"1", type: ValueType.VALUE).save(failOnError:true)
		
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
		new Expression(names:j(["en":"Expression"]), code:"EXPR", type:ValueType.VALUE, expression:"1").save(failOnError:true)
		
		then:
		Expression.count();
		
		when:
		new Expression(names:j(["en":"Expression"]), code:"EXPR", type:ValueType.VALUE, expression:"1").save(failOnError:true)
		
		then:
		thrown ValidationException
		
	}
	
	def "expression can be a constant"() {
		setup:
		IntegrationTestInitializer.createConstants()
		
		when:
		new Expression(names:j(["en":"Expression"]), code:"EXPR", type:ValueType.VALUE, expression:"[c"+Constant.findByCode("CONST1").id+"]").save(failOnError:true)
		
		then:
		Expression.count() == 1;
		
	}

	
	def "empty name transfers properly to json"() {
		when:
		new DataElement(names:new Translation(), code:"TEST", type: ValueType.VALUE).save(failOnError:true)
		
		then:
		def dataElement = DataElement.findByCode("TEST");
		dataElement.names["en"] == null
		dataElement.names["fr"] == null
	}
	
	
	def "translatable set map sets json"() {
		when:
		new DataElement(names:j(["en":"English", "fr":"Francais"]), code:"TEST", type: ValueType.VALUE).save(failOnError:true)
		
		then:
		def dataElement = DataElement.findByCode("TEST");
		dataElement.names == j(["en":"English", "fr":"Francais"])
		dataElement.names["en"] == "English"
		dataElement.names["fr"] == "Anglais"
	}
	
	
	def "translatable set json sets map"() {
		when:
		new DataElement(names:j("en":"test"), code:"TEST", type: ValueType.VALUE).save(failOnError:true)
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
		new DataElement(names:new Translation(), code:"TEST", type: ValueType.VALUE).save(failOnError:true)
		def dataElement = DataElement.findByCode("TEST")
		dataElement.names = new Translation(jsonText: j([en: "English", fr: "Anglais"]));
		dataElement.save(failOnError:true)
		dataElement.names = new Translation(jsonText: j([en: "English"]));
		dataElement.save(failOnError: true)
		dataElement = DataElement.findByCode("TEST");
		
		then:
		dataElement.names.jsonText == new JSONObject().put("en", "English").toString()
		dataElement.names["en"] == "English"
		dataElement.names["fr"] == null
	}
	
}
