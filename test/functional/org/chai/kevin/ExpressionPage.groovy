package org.chai.kevin

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import geb.Page;
import geb.error.RequiredPageContentNotPresent;

class ExpressionPage extends KevinPage {
	
	private static final Log log = LogFactory.getLog(ExpressionPage)
	
	static at = { title == "Expression List" }
	static url = "/kevin/expression/list"
	
	static content = {
		addExpression { $('a', id:'add-expression-link') }
			
		createExpression (required: false) { module CreateExpressionModule }
	}
	
	def addExpression() {
		addExpression.jquery.click()
		waitFor {
			try {
				ExpressionPage.log.debug("waiting for creation pane to be displayed");
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
	
}

