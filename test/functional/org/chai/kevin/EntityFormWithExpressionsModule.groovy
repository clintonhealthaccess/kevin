package org.chai.kevin

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.CreateExpressionModule;
import org.chai.kevin.EntityFormModule;

import geb.Module;
import geb.error.RequiredPageContentNotPresent;

abstract class EntityFormWithExpressionsModule extends EntityFormModule {

	private static final Log log = LogFactory.getLog(EntityFormWithExpressionsModule);
	
	static content = {
		expressionFields { entityFormContainer.find("select.expression-list") }
		
		addExpression { $("a", id:"add-expression-link") }
		createExpression (required: false) { module CreateExpressionModule }
	}
	
	def addExpression() {
		addExpression.jquery.click()
		waitFor {
			try {
				EntityFormWithExpressionsModule.log.debug("waiting for creation pane to be displayed");
				createExpression.present?createExpression.saveButton.displayed:false
			} catch (RequiredPageContentNotPresent e) {
				false;
			}
		}
		waitFor {
			Thread.sleep 1000
			true
		}
	}
	
	
	def hasExpression(def text) {
		return expressionFields.find("option", text: contains(text))
	}
	
}
