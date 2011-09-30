package org.chai.kevin

class ConstantSpec extends IntegrationTests {

	
	//	def "constant saved properly" () {
	//		when:
	//		new Constant(names:j(["en":"Constant"]), code:"CONST", value:"10", type:Type.TYPE_NUMBER).save(failOnError: true)
	//
	//		then:
	//		Constant.count() == 1
	//	}
	//
	//	def "constant code cannot be null"() {
	//		when:
	//		new Constant(names:j(["en":""]), code:"CODE", value:"10", type:Type.TYPE_NUMBER).save(failOnError: true)
	//
	//		then:
	//		Constant.count() == 1
	//
	//		when:
	//		new Constant(names:j(["en":""]), value:"10", type:Type.TYPE_NUMBER).save(failOnError: true)
	//
	//		then:
	//		thrown ValidationException
	//	}
	//
	//	def "constant code is unique"() {
	//		when:
	//		new Constant(names:j(["en":""]), code:"CODE", value:"10", type:Type.TYPE_NUMBER).save(failOnError: true)
	//
	//		then:
	//		Constant.count() == 1
	//
	//		when:
	//		new Constant(names:j(["en":""]), code:"CODE", value:"10", type:Type.TYPE_NUMBER).save(failOnError: true)
	//
	//		then:
	//		thrown ValidationException
	//	}
	//
	//	def "constant type cannot be null"() {
	//		when:
	//		new Constant(names:j(["en":""]), code:"CODE1", value:"10", type:Type.TYPE_NUMBER).save(failOnError: true)
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
	//		new Constant(names:j(["en":"Constant"]), code:"CONST", value:"", type:Type.TYPE_NUMBER).save(failOnError: true)
	//
	//		then:
	//		thrown ValidationException
	//	}
	//
	//	def "constant value cannot be null"() {
	//		when:
	//		new Constant(names:j(["en":"Constant"]), code:"CONST", type:Type.TYPE_NUMBER).save(failOnError: true)
	//
	//		then:
	//		thrown ValidationException
	//	}
	//
	//
	//	def "constant constraint: code cannot be blank"() {
	//		when:
	//		new Constant(names:j(["en":"Constant"]), code:"", value:"1", type: Type.TYPE_NUMBER).save(failOnError:true)
	//
	//		then:
	//		thrown ValidationException
	//	}
	//
	//	def "constant constraint: code is unique"() {
	//		when:
	//		new Constant(names:j(["en":"Constant"]), code:"Unique", value:"1", type: Type.TYPE_NUMBER).save(failOnError:true)
	//
	//		then:
	//		Constant.count() == 1
	//
	//		when:
	//		new Constant(names:j(["en":"Constant"]), code:"Unique", value:"1", type: Type.TYPE_NUMBER).save(failOnError:true)
	//
	//		then:
	//		thrown ValidationException
	//	}
}
