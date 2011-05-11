package org.chai.kevin

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import geb.Module;

class EntityFormModule extends Module {

	private static final Log log = LogFactory.getLog(EntityFormModule)
	
	static content = {
		saveButton { entityFormContainer.find("button", type: "submit").first() }
		cancelButton { entityFormContainer.find("button", id: "cancel-button") }
	}
	
	def save() {
		saveButton.jquery.click()
		waitFor {
			Thread.sleep(1000)
			true
		}
//		waitFor {
//			present?(entityFormContainer.find(".errors")):true
//		}
	}
	
	
	def cancel() {
		cancelButton.jquery.click()
		waitFor { 
			!entityFormContainer.displayed 
		}
		waitFor {
			Thread.sleep(2000)
			true
		}
	}
	
	
	def hasError(def field) {
		return field.parent().classes().contains("errors");
	}
	
}
