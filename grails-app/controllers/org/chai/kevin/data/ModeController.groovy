package org.chai.kevin.data

import org.chai.kevin.data.Type.ValueType
import org.chai.kevin.util.Utils

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
	
	def bindParams(def entity) {
		params['oldType'] = entity.type
		bindData(entity, params, [exclude:'typeBuilderString'])
		try {
			// returns null if params['typeBuilderString'] is null
			def newType  = Utils.buildType(params['typeBuilderString'])
			// we assign the newType to pass to the custom validation Type.isValid()
			if(entity.id == null){
				entity.type = newType
			}
		} catch (Exception e) {
			// we get here if params['typeBuilderString'] is garbage (syntax error)
			params['typeBuilderError'] = e.getMessage()
		}
	}

	def validateEntity(def entity) {
		boolean valid = entity.validate()

		if (log.isDebugEnabled()){
			 log.debug ('validating entity with old type:'+params['oldType']+', typeBuilderString:'+params['typeBuilderString']+', entityType:'+entity.type)
		}

		try {
			// returns null if params['typeBuilderString'] is null
			def newType  = Utils.buildType(params['typeBuilderString'])

			if (log.isDebugEnabled()) log.debug ('entity id:'+entity.id)
			if (log.isDebugEnabled()) log.debug ('old type:'+params['oldType']+', new type:'+newType+', entityType:'+entity.type)

			if(entity.id != null){
				if (log.isDebugEnabled()) log.debug ('# of values:'+valueService.getNumberOfValues(entity))

				if (!newType.equals(params['oldType']) && valueService.getNumberOfValues(entity) > 0) {
					log.debug ('cannot change old type:'+params['oldType']+', typeBuilderString:'+params['typeBuilderString']+', entityType:'+entity.type)
					entity.errors.rejectValue('type', 'calculation.type.cannotChange', 'Could not change type because the calculation has associated values.')
					valid = false
				} 
				else {
					entity.type = newType
				}
			}
			if (log.isDebugEnabled()) log.debug ('old type:'+params['oldType']+', new type:'+newType+', entityType:'+entity.type)

		} catch (Exception e) {
			log.debug ('syntax error old type:'+params['oldType']+', typeBuilderString:'+params['typeBuilderString']+', entityType:'+entity.type)
			entity.errors.rejectValue('type', 'data.type.invalid', [params['typeBuilderError']] as Object[], 'Syntax error: [{0}]')
			valid = false
		}

		return valid;
	}
	
	def getLabel() {
		return 'mode.label'
	}
	
	def getEntityClass(){
		return Mode.class;
	}
}
