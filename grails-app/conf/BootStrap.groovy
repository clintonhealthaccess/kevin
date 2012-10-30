import org.chai.kevin.survey.Survey;
import org.chai.location.CalculationLocation;
import org.chai.location.Location;
import org.chai.task.Progress;

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

import org.chai.init.DataInitializer;
import org.chai.init.ReportInitializer;
import org.chai.init.StructureInitializer;
import org.chai.init.SurveyInitializer;

import java.util.Date;
import java.nio.channels.Channel;
import org.chai.kevin.security.Role;
import grails.util.GrailsUtil;
import org.apache.commons.lang.time.DateUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.chai.kevin.dashboard.DashboardTarget;
import org.chai.kevin.dashboard.DashboardProgram;
import org.chai.kevin.data.Calculation;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Enum;
import org.chai.kevin.data.EnumOption;
import org.chai.kevin.security.User;
import org.chai.task.Task;
import org.chai.task.Task.TaskStatus;
import org.chai.kevin.value.RawDataElementValue;
import org.springframework.amqp.rabbit.core.ChannelCallback;

import com.ibm.jaql.lang.expr.core.TimeoutExpr.TaskState;

class BootStrap {
	
	def sessionFactory
	def rabbitTemplate
	def taskService
	def refreshValueService
	def surveyPageService
	
    def init = { servletContext ->

		// we clear the queue
		// assumption is that at this point of the startup process,
		// no task has been picked for processing if
		// some tasks are still in the queue
		try {
			rabbitTemplate.execute(new ChannelCallback() {
				public Object doInRabbit(com.rabbitmq.client.Channel channel) throws Exception {
					if (log.isDebugEnabled()) log.debug("purging adminQueue queue")
					channel.queuePurge('adminQueue')
					return null;
				}
			})
		} catch (Exception e) {
			if (log.isWarnEnabled()) log.warn("cannot connect to rabbitmq - did not delete queues", e);
		}
		
		// we reset the NEW and IN_PROGRESS tasks to the queue
		def tasks = Task.findAllByStatusInList([TaskStatus.IN_PROGRESS, TaskStatus.NEW])
		tasks.each { task ->
			task.status = TaskStatus.NEW
			task.save(failOnError: true) 
			taskService.sendToQueue(task) 
		}
		
		switch (GrailsUtil.environment) {
		case "production":
			
			if (Role.findByName('report-all-readonly') == null) {
				def reportAllReadonly = new Role(name: "report-all-readonly")
				reportAllReadonly.addToPermissions("menu:reports")
				reportAllReadonly.addToPermissions("dashboard:*")
				reportAllReadonly.addToPermissions("dsr:*")
				reportAllReadonly.addToPermissions("maps:*")
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
			// we initialize the structure
			StructureInitializer.createLocationLevels()
			StructureInitializer.createDataLocationTypes()
			StructureInitializer.createLocations()
			StructureInitializer.createDataLocations()
			sessionFactory.currentSession.flush()
			// TODO review this
			StructureInitializer.createRoles();
			StructureInitializer.createUsers();
			StructureInitializer.createPeriods();
			StructureInitializer.createSources();

			// we initialize the data
			DataInitializer.createEnums();
			DataInitializer.createRawDataElements();
			DataInitializer.createRawDataElementValues();
			DataInitializer.createNormalizedDataElements();
			DataInitializer.createSums();
						
			refreshValueService.refreshAll(null)
			
			// we initialize the reports
			ReportInitializer.createReportPrograms();
			ReportInitializer.createDsrTargetCategories();
			ReportInitializer.createDsrTargets();
			ReportInitializer.createFctTargets();
			ReportInitializer.createFctTargetOptions();
			
			// we initialize the survey
			SurveyInitializer.createSurveys()
			SurveyInitializer.createSurveyPrograms()
			SurveyInitializer.createSurveySections()
			SurveyInitializer.createSurveyQuestions()
			SurveyInitializer.createValidationRules()
			SurveyInitializer.createSurveySkipRules()
			
			// refresh
			surveyPageService.refresh(Location.findByCode('rwanda'), Survey.findByCode('survey_period2'), false, true, null);
			
//			Initializer.createDummyStructure();
//			Initializer.createUsers();
//			Initializer.createDataElementsAndExpressions();
//			Initializer.createDashboard();
//			Initializer.createDsr();
//			Initializer.createFct();
//			Initializer.createQuestionaire();
//			Initializer.createPlanning();
//			Initializer.createDataElementExport();
//			Initializer.createCalculationExport();
			
			break;
		}
		
    }

    def destroy = { }
	
}
