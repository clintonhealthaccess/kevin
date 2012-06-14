package org.chai.kevin.table

import org.chai.kevin.data.Type;
import org.chai.kevin.value.Value;

import grails.plugin.spock.UnitSpec;

class TableUnitSpec extends UnitSpec {

	def "line with null name returns null display name"() {
		when:
		def line1 = new NumberLine(null, [], [])
		
		then:
		line1.displayName == null
	}
	
	def "table with lines and no group"() {
		when:
		def line1 = new NumberLine("Line 1", [Value.VALUE_NUMBER(1)], [Type.TYPE_NUMBER()])
		def line2 = new NumberLine("Line 2", [Value.VALUE_NUMBER(1)], [Type.TYPE_NUMBER()])
		def table = new TableLine([
			line1, line2
		])
		
		then:
		table.getNestedTables() == []
		table.getSingleLines() == [line1, line2]
	}
	
	def "table with lines and one group"() {
		when:
		def line1 = new NumberLine("Group - Line 1", [Value.VALUE_NUMBER(1)], [Type.TYPE_NUMBER()])
		def line2 = new NumberLine("Line 2", [Value.VALUE_NUMBER(1)], [Type.TYPE_NUMBER()])
		def table = new TableLine([
			line1, line2
		])
		
		then:
		table.getNestedTables().size() == 1
		table.getNestedTables()[0].getSingleLines() == [line1]
		table.getNestedTables()[0].getNestedTables() == []
		table.getSingleLines() == [line2]
	}
	
	def "table with lines and one group with several members"() {
		when:
		def line1 = new NumberLine("Group - Line 1", [Value.VALUE_NUMBER(1)], [Type.TYPE_NUMBER()])
		def line2 = new NumberLine("Line 2", [Value.VALUE_NUMBER(1)], [Type.TYPE_NUMBER()])
		def line3 = new NumberLine("Group - Line 2", [Value.VALUE_NUMBER(1)], [Type.TYPE_NUMBER()])
		def table = new TableLine([
			line1, line2, line3
		])
		
		then:
		table.getNestedTables().size() == 1
		table.getNestedTables()[0].getSingleLines() == [line1, line3]
		table.getNestedTables()[0].getNestedTables() == []
		table.getSingleLines() == [line2]
	}
	
	def "table with lines and two groups"() {
		when:
		def line1 = new NumberLine("Group 1 - Line 1", [Value.VALUE_NUMBER(1)], [Type.TYPE_NUMBER()])
		def line2 = new NumberLine("Line 2", [Value.VALUE_NUMBER(1)], [Type.TYPE_NUMBER()])
		def line3 = new NumberLine("Group 2 - Line 2", [Value.VALUE_NUMBER(1)], [Type.TYPE_NUMBER()])
		def table = new TableLine([
			line1, line2, line3
		])
		
		then:
		table.getNestedTables().size() == 2
		table.getNestedTables()[0].getSingleLines() == [line1]
		table.getNestedTables()[0].getNestedTables() == []
		table.getNestedTables()[1].getSingleLines() == [line3]
		table.getNestedTables()[1].getNestedTables() == []
		table.getSingleLines() == [line2]
	}
	
	def "table with lines and nested groups"() {
		when:
		def line1 = new NumberLine("Group 1 - Group 2 - Line 1", [Value.VALUE_NUMBER(1)], [Type.TYPE_NUMBER()])
		def line2 = new NumberLine("Group 1 - Group 2 - Line 2", [Value.VALUE_NUMBER(1)], [Type.TYPE_NUMBER()])
		def line3 = new NumberLine("Group 1 - Line 3", [Value.VALUE_NUMBER(1)], [Type.TYPE_NUMBER()])
		def table = new TableLine([
			line1, line2, line3
		])
		
		then:
		table.getNestedTables().size() == 1
		table.getNestedTables()[0].getSingleLines() == [line3]
		table.getNestedTables()[0].getNestedTables().size() == 1
		table.getNestedTables()[0].getNestedTables()[0].getSingleLines() == [line1, line2]
		table.getSingleLines() == []
	}

	def "aggregate lines"() {
		when:
		def line1 = new NumberLine("Group - Line 1", [Value.VALUE_NUMBER(1)], [Type.TYPE_NUMBER()])
		def line2 = new NumberLine("Line 2", [Value.VALUE_NUMBER(1)], [Type.TYPE_NUMBER()])
		def aggregateLine1 = new AggregateLine("Service", [line1, line2])
		
		then:
		aggregateLine1.getNestedTables().size() == 1
		aggregateLine1.getNestedTables()[0].getSingleLines() == [line1]
		aggregateLine1.getNestedTables()[0].getNestedTables().size() == 0
		aggregateLine1.getSingleLines() == [line2]
	}
	
	def "aggregate lines and single lines"() {
		when:
		def line1 = new NumberLine("Group - Line 1", [Value.VALUE_NUMBER(1)], [Type.TYPE_NUMBER()])
		def line2 = new NumberLine("Line 2", [Value.VALUE_NUMBER(1)], [Type.TYPE_NUMBER()])
		def aggregateLine1 = new AggregateLine("Group - Service", [line2])
		def table = new TableLine([line1, aggregateLine1])
		
		then:
		table.getNestedTables().size() == 1
		table.getNestedTables()[0].getSingleLines() == [line1, aggregateLine1]
		table.getNestedTables()[0].getSingleLines()[1].getSingleLines() == [line2]
		table.getNestedTables()[0].getNestedTables().size() == 0
		table.getSingleLines() == []
	}
	
	def "aggregate lines with groups"() {
		when:
		def line1 = new NumberLine("Line 1", [Value.VALUE_NUMBER(1)], [Type.TYPE_NUMBER()])
		def line2 = new NumberLine("Line 2", [Value.VALUE_NUMBER(1)], [Type.TYPE_NUMBER()])
		def aggregateLine1 = new AggregateLine("Group - Line 1", [line1])
		def aggregateLine2 = new AggregateLine("Group - Line 2", [line2])
		def table = new TableLine([aggregateLine1, aggregateLine2])
		
		then:
		table.getNestedTables().size() == 1
		table.getNestedTables()[0].getSingleLines() == [aggregateLine1, aggregateLine2]
	}
	
	def "aggregate with groups"() {
		when:
		def line1 = new NumberLine("Group - Line 1", [Value.VALUE_NUMBER(1)], [Type.TYPE_NUMBER()])
		def line2 = new NumberLine("Group - Line 2", [Value.VALUE_NUMBER(1)], [Type.TYPE_NUMBER()])
		def aggregateLine1 = new AggregateLine("Test - Truc", [line1, line2])
		def table = new TableLine([aggregateLine1])
		
		then:
		table.getNestedTables().size() == 1
		table.getNestedTables()[0].getSingleLines() == [aggregateLine1]
		table.getNestedTables()[0].getSingleLines()[0].getNestedTables()[0].getSingleLines() == [line1, line2]
	}
	
	def "aggregate when header value is null"() {
		when:
		def line = new NumberLine("Line", [Value.VALUE_NUMBER(1)], [Type.TYPE_NUMBER()])
		def aggregateLine = new AggregateLine(null, [line])
		def tableLine = new TableLine([aggregateLine])
		
		then:
		tableLine.getSingleLines() == [aggregateLine]
	}
	
	def "value - aggregate with null value"() {
		when:
		def line = new NumberLine("Line 1", [null], [Type.TYPE_NUMBER()])
		def aggregateLine = new AggregateLine("Aggregate", [line])
		
		then:
		aggregateLine.getValueForColumn(0).equals( Value.VALUE_NUMBER(0d) )
	}
	
	def "value - aggregate with string value"() {
		when:
		def line = new NumberLine("Line 1", [null], [Type.TYPE_STRING()])
		def aggregateLine = new AggregateLine("Aggregate", [line])
		
		then:
		aggregateLine.getValueForColumn(0).equals( Value.NULL_INSTANCE() )
	}

	def "value - aggregate with no lines"() {
		when:
		def aggregateLine = new AggregateLine("Aggregate", [])
		def tableLine = new TableLine([aggregateLine])
		
		then:
		aggregateLine.getValueForColumn(0).equals(Value.NULL_INSTANCE())
	}
			
}
