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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.Initializer;
import org.chai.kevin.IntegrationTests;
import org.chai.kevin.IntegrationTestInitializer;
import org.chai.kevin.data.Calculation;

import grails.plugin.spock.UnitSpec;

class DomainSpec extends IntegrationTests {

	private static final Log log = LogFactory.getLog(DomainSpec.class)

	def setup() {
		Initializer.createDummyStructure();
		IntegrationTestInitializer.createExpressions()
		IntegrationTestInitializer.createDashboard()
	}
	
	def "test calculations"() {
		expect:
		Calculation.count() == 2
		def nurse = DashboardTarget.findByCode("A1");
		nurse.save();
		Calculation.count() == 2
	}
	
	def "call twice in a row"() {
		
		expect:
		DashboardObjective.findByCode(objectiveCode).objectiveEntries.size() == expectedSize
		
		where:
		objectiveCode	| expectedSize
		"STAFFING"		| 2
		"STAFFING"		| 2
		"HRH"			| 1
		
	}
	
	def "objective order can be null"() {
		when:
		def objective = DashboardObjective.findByCode("HRH");
		objective.addObjectiveEntry new DashboardObjectiveEntry(entry: new DashboardObjective(names:j(["en":"Test"]), code:"TEST"), weight: 1);
		objective.save(flush: true)
		
		then:
		def newObjective = DashboardObjective.findByCode("HRH");
		newObjective.objectiveEntries.size() == 2
	}
	
	def "objective save preserves order"() {
		when:
		def objective = DashboardObjective.findByCode("HRH");
		objective.addObjectiveEntry new DashboardObjectiveEntry(entry: new DashboardObjective(names:j(["en":"Test 4"]), code:"TEST4"), weight: 1, order: 5);
		objective.addObjectiveEntry new DashboardObjectiveEntry(entry: new DashboardObjective(names:j(["en":"Test 5"]), code:"TEST5"), weight: 1, order: 4);
		objective.save(flush: true)
		
		then:
		def newObjective = DashboardObjective.findByCode("HRH");
		newObjective.objectiveEntries.size() == 3
		newObjective.objectiveEntries[2].order == 5
		newObjective.objectiveEntries[1].order == 4
	}
	
	// integration test
	def "weighted objective cascade"() {
		
		when:
		def objective = DashboardObjective.findByCode("STAFFING");
		
		then:
		objective.objectiveEntries.size() == 2
	}
	
	// integration test
	def "objective delete cascade deletes objective entry"() {
		when:
		def dashboardTargetCount = DashboardTarget.count()
		def objective = DashboardObjective.findByCode("STAFFING");
		objective.parent.parent.objectiveEntries.remove(objective.parent)
		new ArrayList(objective.objectiveEntries).each { 
			it.parent = null
		}
		objective.delete(flush: true)
		
		then:
		DashboardObjectiveEntry.count() == 3
		DashboardTarget.count() == dashboardTargetCount;
	}

	def "delete objective entry cascade deletes targets"() {
		when:
		def dashboardObjectiveEntryCount = DashboardObjectiveEntry.count()
		def dashboardTargetCount = DashboardTarget.count()
		def objective = DashboardObjective.findByCode("STAFFING");
		new ArrayList(objective.objectiveEntries).each { 
			objective.objectiveEntries.remove(it);
			it.delete(flush: true); 
		}
		
		then:
		DashboardObjectiveEntry.count() == dashboardObjectiveEntryCount - 2
		DashboardTarget.count() == dashboardTargetCount - 2;
	}
	
	def "objective save does not cascade objective entries"() {
		when:
		def dashboardObjectiveEntryCount = DashboardObjectiveEntry.count()
		def dashboardTargetCount = DashboardTarget.count()
		def objective = DashboardObjective.findByCode("STAFFING");
		objective.objectiveEntries.clear()
		objective.save(flush: true)
		
		then:
		DashboardObjectiveEntry.count() == dashboardObjectiveEntryCount
		DashboardTarget.count() == dashboardTargetCount;
	}
	
	def "remove target deletes parent objective entry"() {
		when:
		def dashboardObjectiveEntryCount = DashboardObjectiveEntry.count()
		def dashboardTargetCount = DashboardTarget.count()
		def objective = DashboardTarget.findByCode("A1");
		objective.parent.parent.objectiveEntries.remove(objective.parent)
		objective.delete(flush: true)
		
		then:
		DashboardObjectiveEntry.count() == dashboardObjectiveEntryCount - 1
		DashboardTarget.count() == dashboardTargetCount - 1;
	}
	
	// integration test
	def "objective entries has parents"() {
		
		expect:
		DashboardObjectiveEntry.count() == 4
	}
	
	// integration test
	def "weighted objectives for target"() {
		when:
		def objective = DashboardObjective.findByCode("STAFFING");
		def objectiveEntry = DashboardObjectiveEntry.findByEntry(objective);
		
		then:
		objectiveEntry != null
		objectiveEntry.getParent() != null
		
	}
	
}
