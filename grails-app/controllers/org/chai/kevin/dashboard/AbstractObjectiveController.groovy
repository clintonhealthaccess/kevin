package org.chai.kevin.dashboard

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

import org.chai.kevin.AbstractEntityController
import org.chai.kevin.data.Aggregation;
import org.chai.kevin.data.Average;
import org.chai.kevin.data.Calculation;
import org.chai.kevin.reports.ReportObjective

abstract class AbstractObjectiveController extends AbstractEntityController {

	def reportService
	def organisationService
	def dataService
	
	def validateEntity(def entity) {
		return entity.validate()
	}

	def saveEntity(def entity) {
		entity.save()
	}
	
	def deleteEntity(def entity) {	
//		if (log.isInfoEnabled()) log.info("delete entity: "+entity)
		List<DashboardEntity> dashboardEntities = reportService.getDashboardEntities(entity.getReportObjective());
//		//objective
		//this might work, check with db objectives
//		if(dashboardEntities.size() == 0){
//			if (log.isInfoEnabled()) log.info("deleting objective entity: "+entity)
//			entity.delete()
//		}
//		//target
		//FIX this doesn't work
//		if(dashboardEntities.size() == 1 && dashboardEntities.get(0).equals(entity)){
//			if (log.isInfoEnabled()) log.info("deleting target entity: "+entity)
//			entity.delete(flush: true)
//		}
		entity.delete()
	}
	
	def getModel(def entity) {
		def groups = organisationService.getGroupsForExpression()
		return [entity: entity, groups: groups]
	}
	
}
