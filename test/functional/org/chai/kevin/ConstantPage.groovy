package org.chai.kevin

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import geb.Page;
import geb.error.RequiredPageContentNotPresent;

class ConstantPage extends KevinPage {
	
	private static final Log log = LogFactory.getLog(ConstantPage) 
	
	static url = "/kevin/constant/list"
	static at = { title == "Constant List" }
	
	static content = {
		addConstant { $('a', id:'add-constant-link') }
		entityList { $('div.entity-list') }	
		entityRows { entityList.find("tbody").find("tr") }
		constants { entityList.find('div', id:"constants") }
		
		createConstant (required: false) { module CreateConstantModule }
	}
	
	def addConstant() {
		addConstant.jquery.click()
		waitFor {
			try {
				ConstantPage.log.debug("waiting for creation pane to be displayed");
				createConstant.present?createConstant.saveButton.displayed:false
			} catch (RequiredPageContentNotPresent e) {
				false;
			}
		}
		waitFor {
			Thread.sleep 1000
			true
		}
	}
	
	def hasConstant(def text) {
		entityRows.find("td", text: contains(text))
	}
	
	def editConstant(def text) {
		entityRows.find("td.edit-constant-link a", text: contains(text)).first().jquery.click()
		waitFor {
			try {
				ConstantPage.log.debug("waiting for creation pane to be displayed");
				createConstant.present?createConstant.saveButton.displayed:false
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

