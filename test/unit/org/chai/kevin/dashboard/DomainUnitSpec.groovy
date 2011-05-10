package org.chai.kevin.dashboard

import grails.plugin.spock.UnitSpec;

class DomainUnitSpec extends UnitSpec {
   
	def "find strategic objective"() {
        setup:
		mockDomain(DashboardObjective)
		
        when:
        new DashboardObjective(name: "target", root: false).save()

        then:
        DashboardObjective.findByName("target") != null
    }
	
	def "ordering strategic objective"() {
		setup:
		mockDomain(DashboardObjective)
		mockDomain(DashboardTarget)
		mockDomain(DashboardObjectiveEntry)
		def target1 = new DashboardTarget(name: "target1")
		def target2 = new DashboardTarget(name: "target2")
		def weightedObjective1 = new DashboardObjectiveEntry(entry: target1, weight: 1, order: 10)
		def weightedObjective2 = new DashboardObjectiveEntry(entry: target2, weight: 1, order: 3)
		def objective = new DashboardObjective(name: "objective", objectiveEntries: [weightedObjective2, weightedObjective1])
		
		when:
		target1.save(failOnError: true)
		target2.save(failOnError: true)
		objective.save(failOnError: true)
		
		then:
		DashboardObjective.findByName("objective").objectiveEntries.size() == 2
		new ArrayList(DashboardObjective.findByName("objective").objectiveEntries)[index].entry.name == expectedName
		new ArrayList(DashboardObjective.findByName("objective").objectiveEntries)[index].order == expectedOrder
		
		where:
		index	| expectedName	| expectedOrder
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