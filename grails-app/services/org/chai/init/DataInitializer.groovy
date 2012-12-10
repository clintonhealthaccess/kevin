package org.chai.init

import org.chai.kevin.Period
import org.chai.kevin.data.Enum
import org.chai.kevin.data.EnumOption
import org.chai.kevin.data.Mode
import org.chai.kevin.data.NormalizedDataElement
import org.chai.kevin.data.RawDataElement
import org.chai.kevin.data.Source
import org.chai.kevin.data.Summ
import org.chai.kevin.data.Type
import org.chai.kevin.value.RawDataElementValue
import org.chai.kevin.value.Value
import org.chai.location.DataLocation
import org.chai.location.DataLocationType

class DataInitializer {
	
	static def createEnums() {
		if (!Enum.count()) {
			def gender = new Enum(code: "gender", names_en: 'Gender')
			[	new EnumOption(code: "male", names_en: 'Male', value: 'male', order: ["en":1, "fr":2]),
				new EnumOption(code: "female", names_en: 'Female', value: 'female', order: ["en":1, "fr":2])
			].each {gender.addToEnumOptions(it)}
			gender.save(failOnError: true)
			
			def function = new Enum(code: "primary_function", names_en: 'Primary Function')
			[	new EnumOption(code: "doctor", names_en: 'Doctor', names_fr: 'Docteur', value: 'doctor', order: ["en":2, "fr":2]),
				new EnumOption(code: "nurse", names_en: 'Nurse', names_fr: 'Infirmière', value: 'doctor', order: ["en":3, "fr":3]),
				new EnumOption(code: "anesthesist", names_en: 'Anesthesist', names_fr: 'Anesthésiste', value: 'anesthesist', order: ["en":1, "fr":1]),
				new EnumOption(code: "receptionist", names_en: 'Receptionist', names_fr: 'Receptionist', value: 'receptionist', order: ["en":4, "fr":4]),
			].each {function.addToEnumOptions(it)}
			function.save(failOnError: true)
			
			def energy = new Enum(code: 'energy_source', names_en: 'Energy Source')
			[	new EnumOption(code: 'national_grid', names_en: 'National Grid', names_fr: 'Réseau national', value: 'national_grid', order: ["en":3, "fr":3]),
				new EnumOption(code: 'generator', names_en: 'Generator', names_fr: 'Générateur', value: 'generator', order: ["en":1, "fr":1]),
				new EnumOption(code: 'solar', names_en: 'Solar', names_fr: 'Solaire', value: 'solar', order: ["en":2, "fr":2]),
				new EnumOption(code: 'other', names_en: 'Other', names_fr: 'Autre', value: 'other', order: ["en":4, "fr":4]),
				new EnumOption(code: 'none', names_en: 'None', names_fr: 'Aucune', value: 'none', order: ["en":5, "fr":5]),
			].each {energy.addToEnumOptions(it)}
			energy.save(failOnError: true)
			
			def water = new Enum(code: 'water_source', names_en: 'Water')
			[	new EnumOption(code: 'national_grid', names_en: 'National Grid', names_fr: 'Réseau national', value: 'national_grid', order: ["en":3, "fr":3]),
				new EnumOption(code: 'local_surface', names_en: 'Local Surface Water', names_fr: 'Eau de surface', value: 'local_surface', order: ["en":1, "fr":1]),
				new EnumOption(code: 'rainwater', names_en: 'Rainwater Harvesting', names_fr: "Récupération de l'eau de pluie", value: 'rainwater', order: ["en":2, "fr":2]),
				new EnumOption(code: 'tanker', names_en: 'Tanker Truck', names_fr: 'Camion citerne', value: 'tanker', order: ["en":4, "fr":4]),
				new EnumOption(code: 'well', names_en: 'Well or Borehole', names_fr: 'Puis', value: 'well', order: ["en":4, "fr":4]),
				new EnumOption(code: 'other', names_en: 'Other', names_fr: 'Autre', value: 'other', order: ["en":5, "fr":5]),
			].each {water.addToEnumOptions(it)}
			water.save(failOnError: true)
			
			def percent = new Enum(code: 'percent_by_25', names_en: 'Percent by 25')
			[	new EnumOption(code: '0_percent', names_en: '0%', names_fr: '0%', value: '0', order: ["en":1, "fr":1]),
				new EnumOption(code: '25_percent', names_en: '25%', names_fr: '25%', value: '25', order: ["en":2, "fr":2]),
				new EnumOption(code: '50_percent', names_en: '50%', names_fr: '50%', value: '50', order: ["en":3, "fr":3]),
				new EnumOption(code: '75_percent', names_en: '75%', names_fr: '75%', value: '75', order: ["en":4, "fr":4]),
				new EnumOption(code: '100_percent', names_en: '100%', names_fr: '100%', value: '100', order: ["en":5, "fr":5]),
			].each {percent.addToEnumOptions(it)}
			percent.save(failOnError: true)
			
//			def enume = new Enum(names:j(["en":"Enum 1"]), descriptions:j([:]), code:"ENUM1");
//			def enumOption1 = new EnumOption(code:"EnumOption1",names:j(["en":"Value 1"]), descriptions:j(["en":"Lorem Ipsum blabla bli blabla bla Lorem Ipsum Sit Amet Description is huge"]), value:"value1", enume: enume, order: o(["en":1,"fr":2]));
//			def enumOption2 = new EnumOption(code:"EnumOption2",names:j(["en":"Value 2"]), descriptions:j(["en":"Small Description Lorem Ipsum"]), value:"value2", enume: enume, order: o(["en":2,"fr":1]));
//
//			def enume2 = new Enum(names:j(["en":"Enum 2"]), descriptions:j([:]), code:"ENUM2");
//			def enumOption01 = new EnumOption(code:"EnumOption3",names:j(["en":"N/A Did not receive training"]), value:"N/A Did not receive training", enume: enume2);
//			def enumOption02 = new EnumOption(code:"EnumOption4",names:j(["en":"NGO or Partner"]), value:"NGO or Partner", enume: enume2);
//			def enumOption03 = new EnumOption(code:"EnumOption5",names:j(["en":"Ministry of Health"]), value:"Ministry of Health", enume: enume2);
//
//			def enumeGender = new Enum(names:j(["en":"Table Sex"]), descriptions:j([:]), code:"gender");
//			def enumGenderOption1 = new EnumOption(code:"EnumOption6",names:j(["en":"Male"]), value:"male", enume: enumeGender, order: o(["en":1,"fr":2]));
//			def enumGenderOption2 = new EnumOption(code:"EnumOption7",names:j(["en":"Female"]), value:"female", enume: enumeGender, order: o(["en":2,"fr":1]));
//
//			def primaryFunction = new Enum(names:j(["en":"Primary function table"]), descriptions:j([:]), code:"primaryfunction");
//			def primaryFunctionOp1 = new EnumOption(code:"EnumOption8",names:j(["en":"PrimaryFunction1"]), value:"primaryFunction1", enume: primaryFunction, order: o(["en":1,"fr":2]));
//			def primaryFunctionOp2 = new EnumOption(code:"EnumOption9",names:j(["en":"PrimaryFunction2"]), value:"primaryFunction2", enume: primaryFunction, order: o(["en":2,"fr":1]));
		}
	}

	static def createRawDataElements() {
		if (!RawDataElement.count()) {
			
			// service delivery
			new RawDataElement(code: 'catchment_area', type: Type.TYPE_NUMBER(), source: Source.findByCode('dhsst')).save(failOnError: true)
			new RawDataElement(code: 'in_facility_birth', type: Type.TYPE_NUMBER(), source: Source.findByCode('dhsst')).save(failOnError: true)
			new RawDataElement(code: 'out_facility_birth', type: Type.TYPE_NUMBER(), source: Source.findByCode('dhsst')).save(failOnError: true)
			new RawDataElement(code: 'prenuptial_consultation', type: Type.TYPE_BOOL(), source: Source.findByCode('dhsst')).save(failOnError: true)
			
			// geographical access
			// TODO make a nominative version of this ?
			new RawDataElement(code: 'energy_source', type: Type.TYPE_ENUM('energy_source'), source: Source.findByCode('dhsst')).save(failOnError: true)
			new RawDataElement(code: 'energy_needs_covered', type: Type.TYPE_ENUM('percent_by_25'), source: Source.findByCode('dhsst')).save(failOnError: true)
			new RawDataElement(code: 'rainwater_pipes', type: Type.TYPE_BOOL(), source: Source.findByCode('dhsst')).save(failOnError: true)
			new RawDataElement(code: 'rainwater_tanks', type: Type.TYPE_BOOL(), source: Source.findByCode('dhsst')).save(failOnError: true)
			new RawDataElement(code: 'rainwater_gutters', type: Type.TYPE_BOOL(), source: Source.findByCode('dhsst')).save(failOnError: true)
			new RawDataElement(code: 'rainwater_none', type: Type.TYPE_BOOL(), source: Source.findByCode('dhsst')).save(failOnError: true)
			new RawDataElement(code: 'has_water', type: Type.TYPE_BOOL(), source: Source.findByCode('dhsst')).save(failOnError: true)
			new RawDataElement(code: 'water_sources', type: Type.TYPE_LIST(
				Type.TYPE_MAP([
					"water_source": Type.TYPE_ENUM('water_source'),
					"description": Type.TYPE_STRING(),
					"identifiers": Type.TYPE_MAP([
						"percent_needs_covered": Type.TYPE_ENUM('percent_by_25')
					])
				])
			), source: Source.findByCode('dhsst')).save(failOnError: true)
			new RawDataElement(code: 'number_of_motos', type: Type.TYPE_NUMBER(), source: Source.findByCode('dhsst')).save(failOnError: true)
			
			// human resources
			new RawDataElement(code: 'number_of_doctors', type: Type.TYPE_NUMBER(), source: Source.findByCode('dhsst')).save(failOnError: true)
			new RawDataElement(code: 'needed_number_of_doctors', type: Type.TYPE_NUMBER(), source: Source.findByCode('dhsst')).save(failOnError: true)
			new RawDataElement(code: 'number_of_nurses', type: Type.TYPE_NUMBER(), source: Source.findByCode('dhsst')).save(failOnError: true)
			new RawDataElement(code: 'needed_number_of_nurses', type: Type.TYPE_NUMBER(), source: Source.findByCode('dhsst')).save(failOnError: true)
			new RawDataElement(code: 'number_of_cooks', type: Type.TYPE_NUMBER(), source: Source.findByCode('dhsst')).save(failOnError: true)
			new RawDataElement(code: 'needed_number_of_cooks', type: Type.TYPE_NUMBER(), source: Source.findByCode('dhsst')).save(failOnError: true)
			new RawDataElement(code: 'number_of_technicians', type: Type.TYPE_NUMBER(), source: Source.findByCode('dhsst')).save(failOnError: true)
			new RawDataElement(code: 'needed_number_of_technicians', type: Type.TYPE_NUMBER(), source: Source.findByCode('dhsst')).save(failOnError: true)
			
			// human resources
			new RawDataElement(code: 'human_resources', type: Type.TYPE_LIST(
				Type.TYPE_MAP([
					"family_name": Type.TYPE_STRING(),
					"given_name": Type.TYPE_STRING(),
					"birth_date": Type.TYPE_DATE(),
					"personal_information": Type.TYPE_MAP([
						"sex": Type.TYPE_ENUM("gender"),
						"nationality": Type.TYPE_STRING(),
						"age": Type.TYPE_NUMBER()
					]),
					"work_history": Type.TYPE_MAP([
						"primary_function": Type.TYPE_ENUM("primary_function"),
						"departments_served": Type.TYPE_MAP([
							"administration": Type.TYPE_BOOL()
						])
					])
				])
			), source: Source.findByCode('dhsst')).save(failOnError: true)
			
		}
	}
	
	static def createRawDataElementValues() {
		if (!RawDataElementValue.count()) {
			// in_facility_birth - period1
			newValue('butaro_hd', 'period1', 'in_facility_birth', Value.VALUE_NUMBER(456))
			newValue('kivuye_cs', 'period1', 'in_facility_birth', Value.VALUE_NUMBER(23))
			newValue('rusasa_cs', 'period1', 'in_facility_birth', Value.VALUE_NUMBER(45))
			
			// in_facility_birth - period2
			newValue('butaro_hd', 'period2', 'in_facility_birth', Value.VALUE_NUMBER(789))
			newValue('kivuye_cs', 'period2', 'in_facility_birth', Value.VALUE_NUMBER(36))
			newValue('rusasa_cs', 'period2', 'in_facility_birth', Value.VALUE_NUMBER(33))
			
			// out_facility_birth - period1
			newValue('butaro_hd', 'period1', 'out_facility_birth', Value.VALUE_NUMBER(322))
			newValue('kivuye_cs', 'period1', 'out_facility_birth', Value.VALUE_NUMBER(12))
			newValue('rusasa_cs', 'period1', 'out_facility_birth', Value.VALUE_NUMBER(32))
			
			// out_facility_birth - period2
			newValue('butaro_hd', 'period2', 'out_facility_birth', Value.VALUE_NUMBER(233))
			newValue('kivuye_cs', 'period2', 'out_facility_birth', Value.VALUE_NUMBER(13))
			newValue('rusasa_cs', 'period2', 'out_facility_birth', Value.VALUE_NUMBER(21))
			
			// energy_source - period1
			newValue('butaro_hd', 'period1', 'energy_source', Value.VALUE_STRING('national_grid'))
			newValue('kivuye_cs', 'period1', 'energy_source', Value.VALUE_STRING('generator'))
			newValue('rusasa_cs', 'period1', 'energy_source', Value.VALUE_STRING('solar'))
			
			// energy_source - period2
			newValue('butaro_hd', 'period2', 'energy_source', Value.VALUE_STRING('national_grid'))
			newValue('kivuye_cs', 'period2', 'energy_source', Value.VALUE_STRING('other'))
			newValue('rusasa_cs', 'period2', 'energy_source', Value.VALUE_STRING('solar'))
			
			// percent_needs_covered - period1
			newValue('butaro_hd', 'period1', 'energy_needs_covered', Value.VALUE_STRING('75'))
			newValue('kivuye_cs', 'period1', 'energy_needs_covered', Value.VALUE_STRING('50'))
			newValue('rusasa_cs', 'period1', 'energy_needs_covered', Value.VALUE_STRING('0'))
			
			// percent_needs_covered - period2
			newValue('butaro_hd', 'period2', 'energy_needs_covered', Value.VALUE_STRING('100'))
			newValue('kivuye_cs', 'period2', 'energy_needs_covered', Value.VALUE_STRING('75'))
			newValue('rusasa_cs', 'period2', 'energy_needs_covered', Value.VALUE_STRING('0'))
			
			// catchment_area - period1
			newValue('butaro_hd', 'period1', 'catchment_area', Value.VALUE_NUMBER(250000))
			newValue('kivuye_cs', 'period1', 'catchment_area', Value.VALUE_NUMBER(25000))
			newValue('rusasa_cs', 'period1', 'catchment_area', Value.VALUE_NUMBER(12000))
			
			// catchment_area - period2
			newValue('butaro_hd', 'period2', 'catchment_area', Value.VALUE_NUMBER(260000))
			newValue('kivuye_cs', 'period2', 'catchment_area', Value.VALUE_NUMBER(26000))
			newValue('rusasa_cs', 'period2', 'catchment_area', Value.VALUE_NUMBER(13000))
			
			// number_of_doctors - period1
			newValue('butaro_hd', 'period1', 'number_of_doctors', Value.VALUE_NUMBER(5))
			newValue('kivuye_cs', 'period1', 'number_of_doctors', Value.VALUE_NUMBER(2))
			newValue('rusasa_cs', 'period1', 'number_of_doctors', Value.VALUE_NUMBER(1))
			
			// number_of_motos - period1
			newValue('butaro_hd', 'period1', 'number_of_motos', Value.VALUE_NUMBER(2))
			newValue('kivuye_cs', 'period1', 'number_of_motos', Value.VALUE_NUMBER(0))
			newValue('rusasa_cs', 'period1', 'number_of_motos', Value.VALUE_NUMBER(0))
			
			// number_of_motos - period2
			newValue('butaro_hd', 'period2', 'number_of_motos', Value.VALUE_NUMBER(1))
			newValue('kivuye_cs', 'period2', 'number_of_motos', Value.VALUE_NUMBER(2))
			newValue('rusasa_cs', 'period2', 'number_of_motos', Value.VALUE_NUMBER(1))
			
			// human_resources - period2
			newValue('butaro_hd', 'period2', 'human_resources', Value.VALUE_LIST([
				Value.VALUE_MAP([
					"family_name": Value.VALUE_STRING("Kahigiso"),
					"given_name": Value.VALUE_STRING("Jean"),
					"work_history": Value.VALUE_MAP([
						"primary_function": Value.VALUE_STRING("doctor")
					])
				]),
				Value.VALUE_MAP([
					"family_name": Value.VALUE_STRING("Munyaneza"),
					"given_name": Value.VALUE_STRING("Eugene"),
					"work_history": Value.VALUE_MAP([
						"primary_function": Value.VALUE_STRING("doctor")
					])
				]),
				Value.VALUE_MAP([
					"family_name": Value.VALUE_STRING("Reggio d'Aci"),
					"given_name": Value.VALUE_STRING("Paolo"),
					"work_history": Value.VALUE_MAP([
						"primary_function": Value.VALUE_STRING("nurse")
					])
				])
			]))
			newValue('kivuye_cs', 'period2', 'human_resources', Value.VALUE_LIST([
				Value.VALUE_MAP([
					"family_name": Value.VALUE_STRING("Lister"),
					"given_name": Value.VALUE_STRING("Susan"),
					"work_history": Value.VALUE_MAP([
						"primary_function": Value.VALUE_STRING("nurse")
					])
				]),
				Value.VALUE_MAP([
					"family_name": Value.VALUE_STRING("Terrier"),
					"given_name": Value.VALUE_STRING("François"),
					"work_history": Value.VALUE_MAP([
						"primary_function": Value.VALUE_STRING("receptionist")
					])
				]),
			]))
			newValue('rusasa_cs', 'period2', 'human_resources', Value.VALUE_LIST([
				Value.VALUE_MAP([
					"family_name": Value.VALUE_STRING("Mota"),
					"given_name": Value.VALUE_STRING("Antonio"),
					"work_history": Value.VALUE_MAP([
						"primary_function": Value.VALUE_STRING("doctor")
					])
				]),
				Value.VALUE_MAP([
					"family_name": Value.VALUE_STRING("Ntwali"),
					"given_name": Value.VALUE_STRING("Alain"),
					"work_history": Value.VALUE_MAP([
						"primary_function": Value.VALUE_STRING("doctor")
					])
				]),
			]))
		}
	}
	
	private static def newValue(def locationCode, def periodCode, def dataCode, def value) {
		new RawDataElementValue(
			location: DataLocation.findByCode(locationCode),
			period: Period.findByCode(periodCode),
			data: RawDataElement.findByCode(dataCode),
			value: value
		).save(failOnError: true)
	}
	
	static def createNormalizedDataElements() {
		if (!NormalizedDataElement.count()) {
			// service delivery - total birth
			new NormalizedDataElement(code: 'total_birth', type: Type.TYPE_NUMBER(), expressionMap: Period.list().collectEntries ([:], { period ->
				[(period.id.toString()), DataLocationType.list().collectEntries ([:], { type ->
					[(type.code), '\$' + RawDataElement.findByCode('in_facility_birth').id + ' + \$' + RawDataElement.findByCode('out_facility_birth').id]
				})]
			})).save(failOnError: true)
			
			// geographical access - use of solar
			new NormalizedDataElement(code: 'use_solar', type: Type.TYPE_BOOL(), expressionMap: Period.list().collectEntries ([:], { period ->
				[(period.id.toString()), DataLocationType.list().collectEntries ([:], { type ->
					[(type.code), '\$' + RawDataElement.findByCode('energy_source').id + ' == "solar"']
				})]
			})).save(failOnError: true)
			
			// geographical access - energy source
			new NormalizedDataElement(code: 'primary_energy_source_enum', type: Type.TYPE_ENUM('energy_source'), expressionMap: Period.list().collectEntries ([:], { period ->
				[(period.id.toString()), DataLocationType.list().collectEntries ([:], { type ->
					[(type.code), '\$' + RawDataElement.findByCode('energy_source').id]
				})]
			})).save(failOnError: true)
			
			// geographical access - harvesting infrastructure
			new NormalizedDataElement(code: 'rainwater_harvesting', type: Type.TYPE_STRING(), expressionMap: Period.list().collectEntries ([:], { period ->
				[(period.id.toString()), DataLocationType.list().collectEntries ([:], { type ->
					[(type.code), ' "TODO"']
				})]
			})).save(failOnError: true)
			
			// geographical access - consistent energy
			new NormalizedDataElement(code: 'energy_consistent', type: Type.TYPE_BOOL(), expressionMap: Period.list().collectEntries ([:], { period ->
				[(period.id.toString()), DataLocationType.list().collectEntries ([:], { type ->
					[(type.code), 'convert(\$' + RawDataElement.findByCode('energy_needs_covered').id + ', schema double) > 75']
				})]
			})).save(failOnError: true)
			
			// geographical access - limited energy
			new NormalizedDataElement(code: 'energy_limited', type: Type.TYPE_BOOL(), expressionMap: Period.list().collectEntries ([:], { period ->
				[(period.id.toString()), DataLocationType.list().collectEntries ([:], { type ->
					[(type.code), 'percent = convert(\$' + RawDataElement.findByCode('energy_needs_covered').id + ', schema double); percent <= 75 and percent > 0']
				})]
			})).save(failOnError: true)
			
			// geographical access - no energy
			new NormalizedDataElement(code: 'energy_none', type: Type.TYPE_BOOL(), expressionMap: Period.list().collectEntries ([:], { period ->
				[(period.id.toString()), DataLocationType.list().collectEntries ([:], { type ->
					[(type.code), 'convert(\$' + RawDataElement.findByCode('energy_needs_covered').id + ', schema double) == 0']
				})]
			})).save(failOnError: true)
			
			// human resources - population per doctor
			new NormalizedDataElement(code: 'population_per_doctor', type: Type.TYPE_NUMBER(), expressionMap: [
				(Period.findByCode('period1').id.toString()): DataLocationType.list().collectEntries ([:], { type ->
					[(type.code), '\$' + RawDataElement.findByCode('catchment_area').id + ' / \$' + RawDataElement.findByCode('number_of_doctors').id ]
				}),
				(Period.findByCode('period2').id.toString()): DataLocationType.list().collectEntries ([:], { type ->
					[(type.code),
						'\$' + RawDataElement.findByCode('catchment_area').id +
						' / (\$' + RawDataElement.findByCode('human_resources').id +
						' -> filter $.work_history.primary_function == "doctor" -> union([]) -> count())']
				})
			]).save(failOnError: true)
		}
	}
	
	static def createSums() {
		if (!Summ.count()) {
			// energy - consistent energy
			new Summ(code: 'energy_consistent_count', expression: 'if (\$'+NormalizedDataElement.findByCode('energy_consistent').id+') 1 else 0', type: Type.TYPE_NUMBER()).save(failOnError: true)
			// energy - limited energy
			new Summ(code: 'energy_limited_count', expression: 'if (\$'+NormalizedDataElement.findByCode('energy_limited').id+') 1 else 0', type: Type.TYPE_NUMBER()).save(failOnError: true)
			// energy - consistent energy
			new Summ(code: 'energy_none_count', expression: 'if (\$'+NormalizedDataElement.findByCode('energy_none').id+') 1 else 0', type: Type.TYPE_NUMBER()).save(failOnError: true)
			
			// human resources - above recommended population per doctor
			new Summ(code: 'population_per_doctor_above', expression: 'if (\$'+NormalizedDataElement.findByCode('population_per_doctor').id+' < 10000) 1 else 0', type: Type.TYPE_NUMBER()).save(failOnError: true)
			// human resources - below recommended population per doctor
			new Summ(code: 'population_per_doctor_below', expression: 'if (\$'+NormalizedDataElement.findByCode('population_per_doctor').id+
				' >= 10000 or \$'+NormalizedDataElement.findByCode('population_per_doctor').id+' == "null") 1 else 0', type: Type.TYPE_NUMBER()).save(failOnError: true)
		}
	}
	
	static def createModes() {
		if (!Mode.count()) {
			// geographical access - use of solar
			new Mode(code: 'use_solar_mode_bool', expression: '(\$'+NormalizedDataElement.findByCode('use_solar').id+')', 
				type: Type.TYPE_LIST(Type.TYPE_BOOL())).save(failOnError: true)
			// geographical access - primary energy source
			new Mode(code: 'primary_energy_source_mode_enum', expression: '(\$'+NormalizedDataElement.findByCode('primary_energy_source_enum').id+')', 
				type: Type.TYPE_LIST(Type.TYPE_ENUM('energy_source'))).save(failOnError: true)
			// geographical access - TODO mode number
			new Mode(code: 'number_of_motos_mode_number', expression: '(\$'+RawDataElement.findByCode('number_of_motos').id+')', 
				type: Type.TYPE_LIST(Type.TYPE_NUMBER())).save(failOnError: true)
			// geographical access - TODO mode string/text
		}
	}
	
}
