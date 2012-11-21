package org.chai.init

import org.apache.shiro.crypto.hash.Sha256Hash
import org.chai.kevin.Period
import org.chai.kevin.data.Source
import org.chai.kevin.security.Role
import org.chai.kevin.security.User
import org.chai.kevin.security.UserType
import org.chai.location.DataLocation
import org.chai.location.DataLocationType
import org.chai.location.Location
import org.chai.location.LocationLevel

public class StructureInitializer {

	static def createRoles() {
		if (!Role.count()) {
			def reportAllReadonly = new Role(name: "report-all-readonly")
			reportAllReadonly.addToPermissions("menu:reports")
			reportAllReadonly.addToPermissions("dashboard:*")
			reportAllReadonly.addToPermissions("dsr:*")
			reportAllReadonly.addToPermissions("cost:*")
			reportAllReadonly.addToPermissions("fct:*")
			reportAllReadonly.save()
	
			def surveyAllReadonly = new Role(name: "survey-all-readonly")
			surveyAllReadonly.addToPermissions("menu:survey")
			surveyAllReadonly.addToPermissions("summary:*")
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
	}
	
	static def createUsers() {
		if (!User.count()) {
			def user = new User(
				userType: UserType.OTHER, code:"dhsst", username: "dhsst", 
				firstname: "Dhsst", lastname: "Dhsst", 
				email:'dhsst@dhsst.org', passwordHash: new Sha256Hash("dhsst").toHex(), 
				active: true, confirmed: true, uuid:'dhsst_uuid', 
				defaultLanguage:'fr', phoneNumber: '+250 11 111 11 11', organisation:'org')
			[	Role.findByName('report-all-readonly'), 
				Role.findByName('survey-all-readonly')
			].each {user.addToRoles(it)}
			user.save(failOnError: true)
	
			def admin = new User(
				userType: UserType.OTHER, code:"admin", username: "admin",
				firstname: "Super", lastname: "Admin", defaultLanguage: 'en',
				email:'admin@dhsst.org', passwordHash: new Sha256Hash("admin").toHex(), 
				active: true, confirmed: true, uuid:'admin_uuid', 
				phoneNumber: '+250 11 111 11 11', organisation:'org')
			admin.addToPermissions("*")
			admin.save(failOnError: true)
	
			def butaro = new User(userType: UserType.SURVEY, code:"butaro",
				username: "butaro", firstname: "butaro", lastname: "butaro", defaultLanguage: 'en',
				locationId: DataLocation.findByCode("butaro_hd").id, passwordHash: new Sha256Hash("123").toHex(), 
				active: true, confirmed: true, uuid: 'butaro_uuid', 
				phoneNumber: '+250 11 111 11 11', organisation:'org')
			[	"editSurvey:view", 
				"editSurvey:*:"+DataLocation.findByCode("butaro_hd").id, 
				"menu:survey", 
				"menu:reports", 
				"home:*"].each {butaro.addToPermissions(it)}
			butaro.save(failOnError: true)
			
			def kivuye = new User(userType: UserType.PLANNING, code:"kivuye",
				username: "kivuye", firstname: "kivuye", lastname: "kivuye", defaultLanguage: 'en',
				locationId: DataLocation.findByCode("kivuye_cs").id, passwordHash: new Sha256Hash("123").toHex(),
				active: true, confirmed: true, uuid: 'kivuye_uuid',
				phoneNumber: '+250 11 111 11 11', organisation:'org')
			[	"editPlanning:view",
				"editPlanning:*:"+DataLocation.findByCode("kivuye_cs").id,
				"menu:planning",
				"menu:reports",
				"home:*"].each {kivuye.addToPermissions(it)}
			kivuye.save(failOnError: true)
		}
	}
	
	static def createPeriods() {
		if (!Period.count()) {
			// periods
			new Period(code:"period1", startDate: getDate( 2005, 3, 1 ), endDate: getDate( 2005, 3, 31 ), defaultSelected: false).save(failOnError: true)
			new Period(code:"period2", startDate: getDate( 2006, 3, 1 ), endDate: getDate( 2006, 3, 31 ), defaultSelected: true).save(failOnError: true)
		}
	}

	static def createSources() {
		if (!Source.count()) {
			new Source(code:"dhsst", names_en: "DHSST").save(failOnError: true)
		}
	}
	
	static def createLocationLevels() {
		if (!LocationLevel.count()) {
			new LocationLevel(code: "country", names_en: "Country", order: 1).save(failOnError: true)
			new LocationLevel(code: "province", names_en: "Province", order: 2).save(failOnError: true)
			new LocationLevel(code: "district", names_en: "District", order: 3).save(failOnError: true)
			new LocationLevel(code: "sector", names_en: "Sector", order: 4).save(failOnError: true)
		}
	}
	
	static def createDataLocationTypes() {
		if (!DataLocationType.count()) {
			new DataLocationType(code: 'health_center', names_en: "Health Center", defaultSelected: true).save(failOnError: true)
			new DataLocationType(code: 'district_hospital', names_en: "District Hospital", defaultSelected: true).save(failOnError: true)
		}
	}
	
	static def createLocations() {
		if (!Location.count()) {
			def rwanda 		= new Location(code: "rwanda", names_en: 'Rwanda', parent: null, level: LocationLevel.findByCode('country')).save(failOnError: true)
			def north 		= new Location(code: "north", names_en: 'North', parent: rwanda, level: LocationLevel.findByCode('province')).save(failOnError: true)
			def south 		= new Location(code: "south", names_en: 'South', parent: rwanda, level: LocationLevel.findByCode('province')).save(failOnError: true)
			def east 		= new Location(code: "east", names_en: 'East', parent: rwanda, level: LocationLevel.findByCode('province')).save(failOnError: true)
			def west 		= new Location(code: "west", names_en: 'West', parent: rwanda, level: LocationLevel.findByCode('province')).save(failOnError: true)
			def kigali 		= new Location(code: "kigali", names_en: 'Kigali City', parent: rwanda, level: LocationLevel.findByCode('province')).save(failOnError: true)
			def gasabo 		= new Location(code: "gasabo", names_en: 'Gasabo', parent: kigali, level: LocationLevel.findByCode('district')).save(failOnError: true)
			def kicukiro 	= new Location(code: "kicukiro", names_en: 'Kicukiro', parent: kigali, level: LocationLevel.findByCode('district')).save(failOnError: true)
			def nyarugenge 	= new Location(code: "Nyarugenge", names_en: 'Nyarugenge', parent: kigali, level: LocationLevel.findByCode('district')).save(failOnError: true)
			def burera 		= new Location(code: "burera", names_en: 'Burera', parent: north, level: LocationLevel.findByCode('district')).save(failOnError: true)
		}
	}
	
	static def createDataLocations() {
		if (!DataLocation.count()) {
			[	new DataLocation(code: "butaro_hd", names_en: 'Butaro HD', type: DataLocationType.findByCode('district_hospital')),
				new DataLocation(code: "kivuye_cs", names_en: 'Kivuye CS', type: DataLocationType.findByCode('health_center')),
				new DataLocation(code: "rusasa_cs", names_en: 'Rusasa CS', type: DataLocationType.findByCode('health_center'))
			].each {Location.findByCode('burera').addToDataLocations(it).save(failOnError: true)}
		}
	}
	
	public static Date getDate( int year, int month, int day ) {
		final Calendar calendar = Calendar.getInstance();

		calendar.clear();
		calendar.set( Calendar.YEAR, year );
		calendar.set( Calendar.MONTH, month - 1 );
		calendar.set( Calendar.DAY_OF_MONTH, day );

		return calendar.getTime();
	}

}
