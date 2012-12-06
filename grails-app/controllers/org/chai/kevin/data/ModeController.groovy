package org.chai.kevin.data

import org.chai.kevin.data.Type.ValueType

class ModeController extends AbstractCalculationController {
	
	def getEntity(def id) {
		return Mode.get(id)
	}
	
	def getTemplate() {
		return "/entity/data/createCalculationMode"
	}
	
	def createEntity() {
		def mode = new Mode()
		mode.type = new Type()
		return mode
	}
	
	def validateEntity(def entity) {
		//TODO check for duplicate code
		boolean valid = entity.validate()
		//invalid if entity exists, and old type != new type, and # of values > 0
		if (entity.id != null && !params['oldType'].equals(entity.type) && valueService.getNumberOfValues(entity)) {
			// error if types are different
			entity.errors.rejectValue('type', 'calculation.type.cannotChange', 'Could not change type because the calculation has associated values.')
			valid = false
		}
		return valid;
	}
	
	def bindParams(def entity) {
		params['oldType'] = entity.type
		
		bindData(entity, params, [exclude:'typeString'])
		
		// we assign the new type only if there are no associated values and type = list
		if (entity.id == null || !valueService.getNumberOfValues(entity)){
			bindData(entity, params, [include:'typeString'])
		}
	}
	
	def getLabel() {
		return 'mode.label'
	}
	
	def getEntityClass(){
		return Mode.class;
	}
}
