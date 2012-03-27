package org.chai.kevin.planning

import grails.plugin.spock.UnitSpec;

import org.chai.kevin.data.Type;
import org.chai.kevin.form.FormElement;
import org.chai.kevin.form.FormEnteredValue;
import org.chai.kevin.value.ValidatableValue;
import org.chai.kevin.value.Value;

class PlanningListUnitSpec extends UnitSpec {

	def "get or create planning entry sets uuid"() {
		setup:
		def value = Value.NULL_INSTANCE()
		def type = Type.TYPE_LIST(Type.TYPE_MAP(["key0": Type.TYPE_STRING()]))
		
		def formEnteredValue = Mock(FormEnteredValue)
		formEnteredValue.getValue() >> value
		formEnteredValue.getValidatable() >> new ValidatableValue(value, type)
		def formElement = Mock(FormElement)
		formElement.getId() >> 0
		def planningType = Mock(PlanningType)
		planningType.getFormElement() >> formElement
		def planningList = new PlanningList(planningType, null, formEnteredValue, null, null, null)
		
		when:
		def planningEntry = planningList.getOrCreatePlanningEntry(0)
		
		then:
		planningEntry.getUuid() != null
	}
	
}
