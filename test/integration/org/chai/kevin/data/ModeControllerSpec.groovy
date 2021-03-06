package org.chai.kevin.data

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.value.ModePartialValue;
import org.chai.location.DataLocation;
import org.chai.location.DataLocationType;
import org.chai.location.Location;

class ModeControllerSpec extends IntegrationTests {

	def modeController
	
	def "save works"() {
		setup:
		modeController = new ModeController()
		
		when:
		modeController.params.code = CODE(1)
		modeController.params.expression = "1"
		modeController.params['typeBuilderString'] = 'type { number }'
		modeController.saveWithoutTokenCheck()
		
		then:
		Mode.count() == 1
		Mode.list()[0].code == CODE(1)
		Mode.list()[0].expression == "1"
		
	}

	def "save validates"() {
		setup:
		modeController = new ModeController()
		
		when: //code = null
		modeController.params.code = null
		modeController.params.expression = "1"
		modeController.params['typeBuilderString'] = 'type { number }'
		modeController.saveWithoutTokenCheck()
		
		then:
		Mode.count() == 0
		
		when: //expression = null
		modeController.params.code = CODE(1)
		modeController.params.expression = null
		modeController.params['typeBuilderString'] = 'type { number }'
		modeController.saveWithoutTokenCheck()
		
		then:
		Mode.count() == 0
		
		when: //type = null
		modeController.params.code = CODE(1)
		modeController.params.expression = "1"
		modeController.params['typeBuilderString'] = ''
		modeController.saveWithoutTokenCheck()
		
		then:
		Mode.count() == 0
	}
	
	def "delete mode deletes values"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def mode = newMode("1", CODE(1), Type.TYPE_NUMBER())
		newModePartialValue(mode, period, Location.findByCode(RWANDA), DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), v("1")) 
		modeController = new ModeController()
		
		when:
		modeController.params.id = mode.id
		modeController.delete()
		
		then:
		Mode.count() == 0
		ModePartialValue.count() == 0
	}

	def "save mode does not delete values"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def mode = newMode("1", CODE(1), Type.TYPE_NUMBER())
		newModePartialValue(mode, period, Location.findByCode(RWANDA), DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), v("1"))
		modeController = new ModeController()
		
		when:
		modeController.params.id = mode.id
		modeController.save()
		
		then:
		Mode.count() == 1
		ModePartialValue.count() == 1
	}	
	
	def "save mode updates timestamp"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def mode = newMode("1", CODE(1), Type.TYPE_NUMBER())
		modeController = new ModeController()
		def time1 = mode.timestamp
		
		when:
		modeController.params.id = mode.id
		modeController.params['typeBuilderString'] = 'type { number }'

		modeController.save()
		
		then:
		Mode.count() == 1
		!Mode.list()[0].timestamp.equals(time1)
	}	
	
	
	def "can change mode type if it has no values" () {
		setup:
		setupLocationTree()
		modeController = new ModeController()
		def mode = newMode("1", CODE(1), Type.TYPE_NUMBER())

		when:
		modeController.params.id = mode.id
		modeController.params.code = mode.code
		modeController.params['typeBuilderString'] = 'type { bool }'
		modeController.saveWithoutTokenCheck()

		then:
		modeController.response.redirectedUrl.equals(modeController.getTargetURI())
		mode.type.equals(Type.TYPE_BOOL())
	}
		
	def "cannot change mode type if it has values" () {
		setup:
		setupLocationTree()
		def period = newPeriod()
		modeController = new ModeController()
		def mode = newMode("1", CODE(1), Type.TYPE_NUMBER())
		newModePartialValue(mode, period, Location.findByCode(RWANDA), DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), v("1"))

		when:
		modeController.params.id = mode.id
		modeController.params.code = mode.code
		modeController.params['typeBuilderString'] = 'type { bool }'
		modeController.saveWithoutTokenCheck()

		then:
		mode.type.equals(Type.TYPE_NUMBER())
	}
	
}
