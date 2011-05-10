package org.chai.kevin

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import geb.Module;

class EntityFormModule extends Module {

	private static final Log log = LogFactory.getLog(EntityFormModule)
	
	static content = {
		saveButton { entityFormContainer.find("button", type: "submit") }
		cancelButton { entityFormContainer.find("button", id: "cancel-button") }
	}
	
	def save() {
		saveButton.click()
		waitFor { 
			EntityFormModule.log.debug("testing if connections are active")
			def active = js.exec ("return jQuery.active")
			EntityFormModule.log.debug ("connections active test: ${active}")
			active == 0
		}
	}
	
	
	def cancel() {
		cancelButton.click()
		waitFor { 
			!entityFormContainer.displayed 
		}
	}
	
	
	def hasError(def field) {
		return field.parent().classes().contains("errors");
	}
	
}
