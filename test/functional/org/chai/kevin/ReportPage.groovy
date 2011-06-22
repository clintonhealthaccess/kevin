package org.chai.kevin

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.KevinPage;

import geb.Page;
import geb.error.RequiredPageContentNotPresent;

abstract class ReportPage extends KevinPage {
	
	private static final Log log = LogFactory.getLog(ReportPage)
	
	static content = {

	}
	
	def addTarget() {
		addTarget.jquery.click()
		waitFor {
			try { 
				ReportPage.log.debug("waiting for creation pane to be displayed");
				createTarget.present?createTarget.saveButton.displayed:false
			} catch (RequiredPageContentNotPresent e) {
				false;
			} 
		}
		waitFor {
			Thread.sleep 1000
			true
		}
	}
	
	def editTarget() {
		editLinks.first().jquery.click()
		waitFor {
			try {
				ReportPage.log.debug("waiting for creation pane to be displayed");
				createTarget.present?createTarget.saveButton.displayed:false
			} catch (RequiredPageContentNotPresent e) {
				false;
			}
		}
		waitFor {
			Thread.sleep 1000
			true
		}
	}
	
	def addObjective() {
		addObjective.jquery.click()
		waitFor { 
			try {
				ReportPage.log.debug("waiting for creation pane to be displayed");
				createObjective.present?createObjective.saveButton.displayed:false
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