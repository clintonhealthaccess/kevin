package org.chai.kevin.cost

/*
* Copyright (c) 2011, Clinton Health Access Initiative.
*
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*     * Redistributions of source code must retain the above copyright
*       notice, this list of conditions and the following disclaimer.
*     * Redistributions in binary form must reproduce the above copyright
*       notice, this list of conditions and the following disclaimer in the
*       documentation and/or other materials provided with the distribution.
*     * Neither the name of the <organization> nor the
*       names of its contributors may be used to endorse or promote products
*       derived from this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
* ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
* WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
* DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
* (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
* LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

import org.chai.kevin.KevinPage;
import org.chai.kevin.ReportPage;

class CostPage extends ReportPage {

	static at = { title == "Costing" }
	static url = "/kevin/cost/view"
	
	static content = {
		addTarget { $("a", id:"add-cost-target-link") }
		addObjective { $("a", id:"add-cost-objective-link") }
		
		costTable { $('div', id:'values') }
		editLinks { costTable.find('tbody th.cell a', text: contains('edit')) }
		
		createTarget(required: false) { module CreateCostTargetModule }
		createObejctive(required: false) { module CreateCostObjectiveModule }
		
		periodFilter { $('div.filter')[0] }
		organisationFilter { $('div.filter')[1] }
		objectiveFilter { $('div.filter')[2] }
		
	}
	
	def getTarget(def text) {
		costTable.find('tbody th.label', text: contains(text))
	}
	
	def pickObjective(def text) {
		objectiveFilter.find('a').first().click();
		waitFor {
			objectiveFilter.find('div.dropdown-list').displayed
		}
		objectiveFilter.find('li a', text: contains(text)).click()
	}
	
	def pickOrganisation(def text) {
		organisationFilter.find('a').first().click();
		waitFor {
			organisationFilter.find('div.dropdown-list').displayed
		}
		organisationFilter.find('li a', text: contains(text)).click()
	}
	
}
