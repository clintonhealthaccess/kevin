package org.chai.kevin.dashboard

import grails.plugin.spock.UnitSpec;

class DomainUnitSpec extends UnitSpec {
   
	def "find strategic objective"() {
        setup:
		mockDomain(DashboardObjective)
		
        when:
        new DashboardObjective(code: "target", root: false).save()

        then:
        DashboardObjective.findByCodee("target") != null
    }
	
	def "ordering strategic objective"() {
		setup:
		mockDomain(DashboardObjective)
		mockDomain(DashboardTarget)
		mockDomain(DashboardObjectiveEntry)
		
		when:
		def weightedObjective1 = new DashboardObjectiveEntry(entry: new DashboardTarget(code: "target1"), weight: 1, order: 10)
		def weightedObjective2 = new DashboardObjectiveEntry(entry: new DashboardTarget(code: "target2"), weight: 1, order: 3)
		def objective = new DashboardObjective(code: "objective", objectiveEntries: [])
		objective.save(failOnError: true)
		objective.addObjectiveEntry weightedObjective2
		objective.addObjectiveEntry weightedObjective1
		weightedObjective1.save(failOnError: true)
		weightedObjective2.save(failOnError: true)
		objective.save(failOnError: true)
		
		then:
		DashboardObjective.findByCode("objective").objectiveEntries.size() == 2
		new ArrayList(DashboardObjective.findByCode("objective").objectiveEntries)[index].entry.code == expectedCode
		new ArrayList(DashboardObjective.findByCode("objective").objectiveEntries)[index].order == expectedOrder
		
		where:
		index	| expectedCode	| expectedOrder
		0		| "target2"		| 3
		1		| "target1"		| 10
	}
	
//	def "dashboardTarget validation"() {
//		setup:
//		mockForConstraintsTests(DashboardTarget)
//		
//		when:
//		def target = new DashboardTarget();
//		target.validate()
//		
//		then:
//		target.errors.count == 1
//		target.errors["name"] == "blank"
//	}
	
//	def "dashboardObjective validation"() {
//		setup:
//		mockForConstraintsTests(DashboardTarget)
//		
//		when:
//		def target = new DashboardTarget();
//		target.validate()
//		
//		then:
//		target.errors.count == 1
//		target.errors["name"] == "blank"
//	}
	
}