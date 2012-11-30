package org.chai.init

import org.chai.kevin.data.NormalizedDataElement
import org.chai.kevin.data.RawDataElement
import org.chai.kevin.data.Mode
import org.chai.kevin.data.Summ
import org.chai.kevin.dsr.DsrTarget
import org.chai.kevin.dsr.DsrTargetCategory
import org.chai.kevin.fct.FctTarget
import org.chai.kevin.fct.FctTargetOption
import org.chai.kevin.reports.ReportProgram

class ReportInitializer {

	static def createReportPrograms() {
		if (!ReportProgram.count()) {
			def root 				= new ReportProgram(code: "strategic_programs", names_en: "Strategic Programs", parent: null).save(failOnError: true)
			def geographical_access = new ReportProgram(code: "geographical_access", names_en: "Geographical Access", parent: root, order: 1).save(failOnError: true)
			def service_delivery	= new ReportProgram(code: "service_delivery", names_en: "Service Delivery", parent: root, order: 2).save(failOnError: true)
			def human_resources		= new ReportProgram(code: "human_resources", names_en: "Human Resources", parent: root, order: 3).save(failOnError: true)
		}
	}
	
	static def createDsrTargetCategories() {
		if (!DsrTargetCategory.count()) {
			new DsrTargetCategory(code: 'sd_critical_indicators', names_en: 'Critical Indicators', program: ReportProgram.findByCode('service_delivery'), order: 1).save(failOnError: true)
			new DsrTargetCategory(code: 'ga_energy', names_en: 'Energy', program: ReportProgram.findByCode('geographical_access'), order: 1).save(failOnError: true)
			new DsrTargetCategory(code: 'ga_patient_access', names_en: 'Patient Access', program: ReportProgram.findByCode('geographical_access'), order: 1).save(failOnError: true)
			new DsrTargetCategory(code: 'water_plumbing', names_en: 'Water & Plumbing', program: ReportProgram.findByCode('geographical_access'), order: 2).save(failOnError: true)
		}
	}
	
	static def createDsrTargets() {
		if (!DsrTarget.count()) {
			// service delivery - critical indicators
			[	new DsrTarget(code: 'dsr_in_facility_birth', names_en: 'In Facility Births', average: false, data: RawDataElement.findByCode('in_facility_birth')),
				new DsrTarget(code: 'dsr_out_facility_birth', names_en: 'Out of Facility Births', average: false, data: RawDataElement.findByCode('out_facility_birth')),
				new DsrTarget(code: 'dsr_total_facility_birth', names_en: 'Total Births', average: false, data: NormalizedDataElement.findByCode('total_birth'))
			].each {DsrTargetCategory.findByCode('sd_critical_indicators').addToTargets(it).save(failOnError: true)}
			
			// geographical access - energy
			[	new DsrTarget(code: 'dsr_primary_energy_source', names_en: 'Primary Energy Source', data: Mode.findByCode('primary_energy_source_mode_enum')),
				new DsrTarget(code: 'dsr_use_solar', names_en: 'Use of Solar Power', data: Mode.findByCode('use_solar_mode_bool'))
			].each {DsrTargetCategory.findByCode('ga_energy').addToTargets(it).save(failOnError: true)}
			
			// geographical access - patient access
			[	new DsrTarget(code: 'dsr_number_of_motos', names_en: 'Number of Motos', data: Mode.findByCode('number_of_motos_mode_number'))
			].each {DsrTargetCategory.findByCode('ga_patient_access').addToTargets(it).save(failOnError: true)}
			
			// geographical access - rainwater harvesting
			[	new DsrTarget(code: 'rainwater_harvesting', names_en: 'Rainwater Harvesting', data: NormalizedDataElement.findByCode('rainwater_harvesting'))
			].each {DsrTargetCategory.findByCode('water_plumbing').addToTargets(it).save(failOnError: true)}
		}
	}
	
	static def createFctTargets() {
		if (!FctTarget.count()) {
			new FctTarget(code: 'energy_supply', names_en: 'Energy Supply', program: ReportProgram.findByCode('geographical_access')).save(failOnError: true)
			new FctTarget(code: 'hr_population', names_en: 'Population per Doctor', program: ReportProgram.findByCode('human_resources')).save(failOnError: true)
		}
	}
	
	static def createFctTargetOptions() {
		if (!FctTargetOption.count()) {
			// energy supply
			[	new FctTargetOption(code: 'fct_energy_supply_consistent', names_en: 'Facilities with sufficient Energy Supply', data: Summ.findByCode('energy_consistent_count')),
				new FctTargetOption(code: 'fct_energy_supply_limited', names_en: 'Facilities with limited Energy Supply', data: Summ.findByCode('energy_limited_count')),
				new FctTargetOption(code: 'fct_energy_supply_none', names_en: 'Facilities with no Energy Supply', data: Summ.findByCode('energy_none_count'))
			].each {FctTarget.findByCode('energy_supply').addToTargetOptions(it).save(failOnError: true)}
			
			// human resources
			[	new FctTargetOption(code: 'population_doctor_above', names_en: 'Facilities at or above recommended', data: Summ.findByCode('population_per_doctor_above')),
				new FctTargetOption(code: 'population_doctor_below', names_en: 'Facilities below the recommended', data: Summ.findByCode('population_per_doctor_below'))
			].each {FctTarget.findByCode('hr_population').addToTargetOptions(it).save(failOnError: true)}
		}
	}
	
}
