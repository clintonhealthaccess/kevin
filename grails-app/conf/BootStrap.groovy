import org.chai.kevin.security.Role;

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

import org.chai.kevin.Initializer;
import org.chai.kevin.cost.CostRampUpYear;

import java.util.Date;

import grails.util.GrailsUtil;

import org.apache.commons.lang.time.DateUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.chai.kevin.cost.CostRampUp;
import org.chai.kevin.cost.CostTarget;
import org.chai.kevin.cost.CostTarget.CostType;
import org.chai.kevin.dashboard.DashboardTarget;
import org.chai.kevin.dashboard.DashboardProgram;
import org.chai.kevin.data.Calculation;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Enum;
import org.chai.kevin.data.EnumOption;
import org.chai.kevin.security.User;
import org.chai.kevin.value.RawDataElementValue;

class BootStrap {

    def init = { servletContext ->

		switch (GrailsUtil.environment) {
		case "production":
			
			if (Role.findByName('reports-all-readonly') == null) {
				def reportAllReadonly = new Role(name: "report-all-readonly")
				reportAllReadonly.addToPermissions("menu:reports")
				reportAllReadonly.addToPermissions("dashboard:*")
				reportAllReadonly.addToPermissions("dsr:*")
				reportAllReadonly.addToPermissions("maps:*")
				reportAllReadonly.addToPermissions("cost:*")
				reportAllReadonly.addToPermissions("fct:*")
				reportAllReadonly.save()
			}
			
			if (Role.findByName('survey-all-readonly') == null) {
				def surveyAllReadonly = new Role(name: "survey-all-readonly")
				surveyAllReadonly.addToPermissions("menu:survey")
				surveyAllReadonly.addToPermissions("editSurvey:view")
				surveyAllReadonly.addToPermissions("editSurvey:summaryPage")
				surveyAllReadonly.addToPermissions("editSurvey:sectionTable")
				surveyAllReadonly.addToPermissions("editSurvey:programTable")
				surveyAllReadonly.addToPermissions("editSurvey:surveyPage")
				surveyAllReadonly.addToPermissions("editSurvey:programPage")
				surveyAllReadonly.addToPermissions("editSurvey:sectionPage")
				surveyAllReadonly.addToPermissions("editSurvey:print")
				surveyAllReadonly.save()
			}
			
//			if (User.findByUsername('admin') == null) {
//				def user = new User(username: "admin", passwordHash: new Sha256Hash("123admin!").toHex())
//				user.addToPermissions("*")
//				user.save()
//			}
			
//			if (User.findByUsername('dhsst') == null) {
//				def user = new User(username: "dhsst", passwordHash: new Sha256Hash("123chai!").toHex())
////				user.addToRoles(Role.findByName('survey-all-readonly'))
//				user.addToRoles(Role.findByName('reports-all-readonly'))
//				user.save()
//			}
			
			break;
		case "development":
			Initializer.createDummyStructure();
			Initializer.createUsers();
			Initializer.createDataElementsAndExpressions();
			Initializer.createDashboard();
			Initializer.createCost();
			Initializer.createDsr();
			Initializer.createFct();
			Initializer.createMaps();
			Initializer.createQuestionaire();
			Initializer.createPlanning();
			Initializer.createExporter();
			
			break;
		}
		
    }

    def destroy = {
//		switch (GrailsUtil.environment) {
//			case "production":
//				break;
//			case "development":
////				deleteAll();
//				break;
//		}
    }

}
