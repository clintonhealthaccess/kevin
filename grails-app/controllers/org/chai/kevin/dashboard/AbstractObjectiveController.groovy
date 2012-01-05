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

abstract class AbstractObjectiveController extends AbstractEntityController {

	def locationService
	def dataService
	
	def validateEntity(def entity) {
		return entity.entry.validate()&entity.validate()
	}

	def getEntity(def id) {
		return DashboardObjectiveEntry.get(id);
	}
	
	def saveEntity(def entity) {
		if (entity.id == null) {
			def currentObjective = DashboardObjective.get(params['currentObjective']);
			currentObjective.addObjectiveEntry entity
			entity.save()
			currentObjective.save()
		}
		else {
			entity.save()
		}
	}
	
	def deleteEntity(def entity) {
		if (!entity.entry.hasChildren()) {
			entity.parent.objectiveEntries.remove(entity)
			entity.delete()
		}
	}
	
	def getModel(def entity) {
		def currentObjective = null;
		if (params['currentObjective']) {
			currentObjective = DashboardObjective.get(params['currentObjective']);
			if (log.isInfoEnabled()) log.info('fetched current objective: '+currentObjective);
		}
		
		def groups = DataEntityType.list()
		return [objectiveEntry: entity, groups: groups, currentObjective: currentObjective]
	}
	
}
