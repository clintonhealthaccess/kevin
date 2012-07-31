package org.chai.kevin.planning

import org.chai.kevin.data.NormalizedDataElement;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Type;
import org.chai.kevin.value.NormalizedDataElementValue;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.value.Value;

import grails.plugin.spock.UnitSpec;

class PlanningOutputTableUnitSpec extends UnitSpec  {

	def "get header type returns header type"() {
		
		when:
		def type = Type.TYPE_LIST(Type.TYPE_MAP(["header": Type.TYPE_STRING()]))
		def dataElement = new RawDataElement(type: type)
		def output = new PlanningOutput(dataElement: dataElement, fixedHeader: "[_].header")
		def outputTable = new PlanningOutputTable(output, null)
		
		then:
		outputTable.headerType.equals(Type.TYPE_STRING())
	}
	
	def "get header type with non-matching header returns null"() {
		
		when:
		def type = Type.TYPE_LIST(Type.TYPE_MAP(["header": Type.TYPE_STRING()]))
		def dataElement = new RawDataElement(type: type)
		def output = new PlanningOutput(dataElement: dataElement, fixedHeader: "[_].non_existant")
		def outputTable = new PlanningOutputTable(output, null)
		
		then:
		outputTable.headerType == null
	}
	
	def "get header values when data element value is null"() {
		
		when:
		def type = Type.TYPE_LIST(Type.TYPE_MAP(["header": Type.TYPE_STRING()]))
		def dataElement = new RawDataElement(type: type)
		def output = new PlanningOutput(dataElement: dataElement, fixedHeader: "[_].header")
		def outputTable = new PlanningOutputTable(output, null)
		
		then:
		outputTable.rows == []
	}
	
	def "get header values when data element value value is null"() {
		
		when:
		def type = Type.TYPE_LIST(Type.TYPE_MAP(["header": Type.TYPE_STRING()]))
		def dataElement = new RawDataElement(type: type)
		def output = new PlanningOutput(dataElement: dataElement, fixedHeader: "[_].header")
		def value = new RawDataElementValue(value: Value.NULL_INSTANCE())
		def outputTable = new PlanningOutputTable(output, value)
		
		then:
		outputTable.rows == []
	}
	
	def "get header values lists values"() {
		
		when:
		def type = Type.TYPE_LIST(Type.TYPE_MAP(["header": Type.TYPE_STRING()]))
		def dataElement = new RawDataElement(type: type)
		def output = new PlanningOutput(dataElement: dataElement, fixedHeader: "[_].header")
		def value = new RawDataElementValue(value: Value.VALUE_LIST(
			[Value.VALUE_MAP(["header": Value.VALUE_STRING("123")]), Value.VALUE_MAP("header": Value.VALUE_STRING("456"))]))
		def outputTable = new PlanningOutputTable(output, value)
		
		then:
		outputTable.rows.equals([Value.VALUE_STRING("123"), Value.VALUE_STRING("456")])
	}
	
	def "get column value type"() {
		
		when:
		def dataElement = new NormalizedDataElement(type: Type.TYPE_LIST(Type.TYPE_NUMBER()))
		def output = new PlanningOutput(dataElement: dataElement, fixedHeader: "[_]")
		def outputColumn = new PlanningOutputColumn(prefix: '[_]')
		def outputTable = new PlanningOutputTable(output, null)
		
		then:
		outputTable.getValueType(outputColumn).equals(Type.TYPE_NUMBER())
	}
	
	def "get column value type when normalized data element is not of type list"() {
		
		when:
		def dataElement = new NormalizedDataElement(type: Type.TYPE_NUMBER())
		def output = new PlanningOutput(dataElement: dataElement, fixedHeader: "[_]")
		def outputColumn = new PlanningOutputColumn(prefix: '[_]')
		def outputTable = new PlanningOutputTable(output, null)
		outputTable.getValueType(outputColumn).equals(Type.TYPE_NUMBER())
		
		then:
		thrown IllegalArgumentException
	}
	
	def "get column value works"() {
		
		when:
		def dataElement = new NormalizedDataElement(type: Type.TYPE_LIST(Type.TYPE_NUMBER()))
		def output = new PlanningOutput(dataElement: dataElement, fixedHeader: "[_]")
		def outputColumn = new PlanningOutputColumn(prefix: '[_]')
		def value = new NormalizedDataElementValue(value: Value.VALUE_LIST([Value.VALUE_NUMBER(1), Value.VALUE_NUMBER(2)]))
		def outputTable = new PlanningOutputTable(output, value)
		
		then:
		outputTable.getValue(0, outputColumn).equals(Value.VALUE_NUMBER(1))
		outputTable.getValue(1, outputColumn).equals(Value.VALUE_NUMBER(2))
	}
	
	def "get column value when normalized data element value is null"() {
		
		when:
		def outputColumn = new PlanningOutputColumn(prefix: '[_]')
		def outputTable = new PlanningOutputTable(null, null)
		
		then:
		outputTable.getValue(0, outputColumn) == null
		outputTable.getValue(1, outputColumn) == null
	}
	
	def "get column value when normalized data element value value is null"() {
		
		when:
		def outputColumn = new PlanningOutputColumn(prefix: '')
		def value = new NormalizedDataElementValue(value: Value.NULL_INSTANCE())
		def outputTable = new PlanningOutputTable(null, value)
		
		then:
		outputTable.getValue(0, outputColumn) == null
		outputTable.getValue(1, outputColumn) == null
	}
	
}