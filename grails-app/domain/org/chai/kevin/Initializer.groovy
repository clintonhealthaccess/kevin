package org.chai.kevin

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

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.shiro.crypto.hash.Sha256Hash;
import org.chai.kevin.cost.CostRampUp;
import org.chai.kevin.cost.CostRampUpYear;
import org.chai.kevin.cost.CostTarget;
import org.chai.kevin.cost.CostTarget.CostType;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.location.Location;
import org.chai.kevin.location.LocationLevel;
import org.chai.kevin.maps.MapsTarget;
import org.chai.kevin.util.JSONUtils;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.value.Value;
import org.chai.kevin.dashboard.DashboardProgram
import org.chai.kevin.dashboard.DashboardTarget
import org.chai.kevin.data.ExpressionMap;
import org.chai.kevin.data.NormalizedDataElement;
import org.chai.kevin.data.Calculation;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Enum;
import org.chai.kevin.data.EnumOption;
import org.chai.kevin.data.Sum
import org.chai.kevin.data.Type;
import org.chai.kevin.planning.Planning;
import org.chai.kevin.planning.PlanningCost;
import org.chai.kevin.planning.PlanningCost.PlanningCostType;
import org.chai.kevin.planning.PlanningOutput;
import org.chai.kevin.planning.PlanningOutputColumn;
import org.chai.kevin.planning.PlanningSkipRule;
import org.chai.kevin.planning.PlanningType;
import org.chai.kevin.reports.ReportProgram
import org.chai.kevin.security.User;
import org.chai.kevin.security.Role;
import org.chai.kevin.security.UserType;
import org.chai.kevin.survey.*;
import org.chai.kevin.dsr.DsrTarget;
import org.chai.kevin.dsr.DsrTargetCategory;
import org.chai.kevin.exports.DataElementExport;
import org.chai.kevin.exports.DataExport;
import org.chai.kevin.fct.FctTarget
import org.chai.kevin.fct.FctTargetOption
import org.chai.kevin.form.FormElement;
import org.chai.kevin.form.FormEnteredValue;
import org.chai.kevin.form.FormSkipRule;
import org.chai.kevin.form.FormValidationRule;

class Initializer {

	static Date mar01 = getDate( 2005, 3, 1 );
	static Date mar31 = getDate( 2005, 3, 31 );
	static Date mar011 = getDate( 2006, 3, 1 );
	static Date mar311 = getDate( 2006, 3, 31 );

	static def createUsers() {
		def reportAllReadonly = new Role(name: "report-all-readonly")
		reportAllReadonly.addToPermissions("menu:reports")
		reportAllReadonly.addToPermissions("dashboard:*")
		reportAllReadonly.addToPermissions("dsr:*")
		reportAllReadonly.addToPermissions("maps:*")
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

		def user = new User(userType: UserType.OTHER,code:"dhsst", username: "dhsst", firstname: "Dhsst", lastname: "Dhsst", email:'dhsst@dhsst.org', passwordHash: new Sha256Hash("dhsst").toHex(), active: true, confirmed: true, uuid:'dhsst_uuid', defaultLanguage:'fr', phoneNumber: '+250 11 111 11 11', organisation:'org')
		user.addToRoles(reportAllReadonly)
		user.addToRoles(surveyAllReadonly)
		// access to site
		user.save(failOnError: true)

		def admin = new User(userType: UserType.OTHER, code:"admin", firstname: "Super", lastname: "Admin", username: "admin", email:'admin@dhsst.org', passwordHash: new Sha256Hash("admin").toHex(), active: true, confirmed: true, uuid:'admin_uuid', phoneNumber: '+250 11 111 11 11', organisation:'org')
		admin.addToPermissions("*")
		admin.save(failOnError: true)

		def butaro = new User(userType: UserType.SURVEY, code:"butaro",username: "butaro", firstname: "butaro", lastname: "butaro", locationId: DataLocation.findByCode("Butaro DH").id, passwordHash: new Sha256Hash("123").toHex(), active: true, confirmed: true, uuid: 'butaro_uuid', phoneNumber: '+250 11 111 11 11', organisation:'org')
		butaro.addToPermissions("editSurvey:view")
		butaro.addToPermissions("editSurvey:*:"+DataLocation.findByCode("Butaro DH").id)
		butaro.addToPermissions("menu:survey")
		butaro.addToPermissions("menu:reports")
		butaro.addToPermissions("home:*")
		butaro.save(failOnError: true)
		
		def kivuye = new User(userType: UserType.PLANNING, code:"kivuye",username: "kivuye", firstname: "kivuye", lastname: "kivuye", locationId: DataLocation.findByCode("Kivuye HC").id, passwordHash: new Sha256Hash("123").toHex(), active: true, confirmed: true, uuid: 'kivuye_uuid', phoneNumber: '+250 11 111 11 11', organisation:'org')
		kivuye.addToPermissions("editPlanning:view")
		kivuye.addToPermissions("editPlanning:*:"+DataLocation.findByCode("Kivuye HC").id)
		kivuye.addToPermissions("menu:planning")
		kivuye.addToPermissions("menu:reports")
		kivuye.addToPermissions("home:*")
		kivuye.save(failOnError: true)
	}

	static def createDummyStructure() {

		if (!Period.count()) {
			// periods
			def period = new Period(code:"period1", startDate: mar01, endDate: mar31)
			period.save(failOnError: true)

			def period2 = new Period(code:"period2",startDate: mar011, endDate: mar311)
			period2.save(failOnError: true, flush: true)
		}

		if (!Location.count()) {

			def hc = new DataLocationType(names: j(["en":"Health Center"]), code: "Health Center").save(failOnError: true)
			def dh = new DataLocationType(names: j(["en":"District Hospital"]), code: "District Hospital").save(failOnError: true)

			def country = new LocationLevel(names: j(["en":"National"]), code: "National", order: 1).save(failOnError: true)
			def province = new LocationLevel(names: j(["en":"Province"]), code: "Province", order: 2).save(failOnError: true)
			def district = new LocationLevel(names: j(["en":"District"]), code: "District", order: 3).save(failOnError: true)
			def sector = new LocationLevel(names: j(["en":"Sector"]), code: "Sector", order: 4).save(failOnError: true)

			def rwanda = new Location(names: j(["en":"Rwanda"]), code: "Rwanda", parent: null, level: country).save(failOnError: true)

			def kigali = new Location(names: j(["en":"Kigali City"]), code: "Kigali City", parent: rwanda, level: province).save(failOnError: true)
			def north = new Location(names: j(["en":"North"]), code: "North", parent: rwanda, level: province).save(failOnError: true)
			def south = new Location(names: j(["en":"South"]), code: "South", parent: rwanda, level: province).save(failOnError: true)
			def east = new Location(names: j(["en":"East"]), code: "East", parent: rwanda, level: province).save(failOnError: true)
			def west = new Location(names: j(["en":"West"]), code: "West", parent: rwanda, level: province).save(failOnError: true)

			def burera = new Location(names: j(["en":"Burera"]), code: "Burera", parent: north, level: district).save(failOnError: true)

			rwanda.children = [
				kigali,
				north,
				south,
				east,
				west
			]
			north.children = [burera]
			rwanda.save(failOnError: true)
			north.save(failOnError: true)

			country.locations = [rwanda]
			province.locations = [
				kigali,
				north,
				south,
				east,
				west
			]
			district.locations = [burera]
			country.save(failOnError: true)
			province.save(failOnError: true)
			district.save(failOnError: true)
			
			def butaro = new DataLocation(names: j(["en":"Butaro"]), code: "Butaro DH", location: burera, type: dh).save(failOnError: true)
			def kivuye = new DataLocation(names: j(["en":"Kivuye"]), code: "Kivuye HC", location: burera, type: hc).save(failOnError: true)
			burera.dataLocations = [butaro, kivuye]
			burera.save(failOnError: true)
		}

		if (!ReportProgram.count()) {
			def root = new ReportProgram(names:j(["en":"Strategic Programs"]), code:"Strategic Programs", descriptions:j(["en":"Strategic Programs"]), parent: null)
			root.save(failOnError: true)

			def ga = new ReportProgram(names:j(["en":"Geographical Access"]), code:"Geographical Access", descriptions:j(["en":"Geographical Access"]), parent: root).save(failOnError: true)
			def hrh = new ReportProgram(names:j(["en":"Human Resources for Health"]), code:"Human Resources for Health", descriptions:j(["en":"Human Resources for Health"]), parent: root).save(failOnError: true)
			def sd = new ReportProgram(names:j(["en":"Service Delivery"]), descriptions:j(["en":"Service Delivery"]), code: "Service Delivery", parent: root).save(failOnError:true)
			def ic= new ReportProgram(names:j(["en":"Institutional Capacity"]), descriptions:j(["en":"Institutional Capacity"]), code:"Institutional Capacity", parent: root).save(failOnError:true)

			root.children << hrh
			root.children << ga
			root.children << sd
			root.children << ic
			root.save(failOnError: true)

			def staffing = new ReportProgram(names:j(["en":"Staffing"]), code:"Staffing", descriptions:j(["en":"Staffing"]), parent: hrh).save(failOnError: true, flush: true)
			hrh.children << staffing

			hrh.save(failOnError: true)
		}
	}

	static def createDataElementsAndExpressions() {

		if (!Enum.count()) {
			// Enumerations
			def enume = new Enum(names:j(["en":"Enum 1"]), descriptions:j([:]), code:"ENUM1");
			def enumOption1 = new EnumOption(code:"EnumOption1",names:j(["en":"Value 1"]), descriptions:j(["en":"Lorem Ipsum blabla bli blabla bla Lorem Ipsum Sit Amet Description is huge"]), value:"value1", enume: enume, order: o(["en":1,"fr":2]));
			def enumOption2 = new EnumOption(code:"EnumOption2",names:j(["en":"Value 2"]), descriptions:j(["en":"Small Description Lorem Ipsum"]), value:"value2", enume: enume, order: o(["en":2,"fr":1]));
			
			def enume2 = new Enum(names:j(["en":"Enum 2"]), descriptions:j([:]), code:"ENUM2");
			def enumOption01 = new EnumOption(code:"EnumOption3",names:j(["en":"N/A Did not receive training"]), value:"N/A Did not receive training", enume: enume2);
			def enumOption02 = new EnumOption(code:"EnumOption4",names:j(["en":"NGO or Partner"]), value:"NGO or Partner", enume: enume2);
			def enumOption03 = new EnumOption(code:"EnumOption5",names:j(["en":"Ministry of Health"]), value:"Ministry of Health", enume: enume2);
			
			def enumeGender = new Enum(names:j(["en":"Table Sex"]), descriptions:j([:]), code:"gender");
			def enumGenderOption1 = new EnumOption(code:"EnumOption6",names:j(["en":"Male"]), value:"male", enume: enumeGender, order: o(["en":1,"fr":2]));
			def enumGenderOption2 = new EnumOption(code:"EnumOption7",names:j(["en":"Female"]), value:"female", enume: enumeGender, order: o(["en":2,"fr":1]));
			
			def primaryFunction = new Enum(names:j(["en":"Primary function table"]), descriptions:j([:]), code:"primaryfunction");
			def primaryFunctionOp1 = new EnumOption(code:"EnumOption8",names:j(["en":"PrimaryFunction1"]), value:"primaryFunction1", enume: primaryFunction, order: o(["en":1,"fr":2]));
			def primaryFunctionOp2 = new EnumOption(code:"EnumOption9",names:j(["en":"PrimaryFunction2"]), value:"primaryFunction2", enume: primaryFunction, order: o(["en":2,"fr":1]));
			
			enume.enumOptions = [
				enumOption1, 
				enumOption2
			]
			enume.save(failOnError: true)
			enumOption1.save(failOnError: true)
			enumOption2.save(failOnError: true, flush:true)

			enume2.enumOptions = [
				enumOption01,
				enumOption02,
				enumOption03
			]
			enume2.save(failOnError: true)
			enumOption01.save(failOnError: true)
			enumOption02.save(failOnError: true, flush:true)
			enumOption03.save(failOnError: true)

			enumeGender.enumOptions = [
				enumGenderOption1,
				enumGenderOption2
			]
			enumeGender.save(failOnError: true)
			enumGenderOption1.save(failOnError: true)
			enumGenderOption2.save(failOnError: true, flush:true)
			
			primaryFunction.enumOptions = [
				primaryFunctionOp1,
				primaryFunctionOp2
			]
			primaryFunction.save(failOnError: true)
			primaryFunctionOp1.save(failOnError: true)
			primaryFunctionOp2.save(failOnError: true, flush:true)
		}

		if (!RawDataElement.count()) {
			// Data Elements
			def dataElement10 = new RawDataElement(names:j(["en":"Element 10"]), descriptions:j([:]), code:"CODE10", type: Type.TYPE_ENUM (Enum.findByCode('ENUM2').code))
			def dataElement1 = new RawDataElement(names:j(["en":"Element 1"]), descriptions:j([:]), code:"CODE1", type: Type.TYPE_NUMBER())
			def dataElement2 = new RawDataElement(names:j(["en":"Element 2"]), descriptions:j([:]), code:"CODE2", type: Type.TYPE_NUMBER())
			def dataElement3 = new RawDataElement(names:j(["en":"Element 3"]), descriptions:j([:]), code:"CODE3", type: Type.TYPE_ENUM (Enum.findByCode('ENUM1').code))
			def dataElement4 = new RawDataElement(names:j(["en":"Element 4"]), descriptions:j([:]), code:"CODE4", type: Type.TYPE_BOOL())
			def dataElement5 = new RawDataElement(names:j(["en":"Element 5"]), descriptions:j([:]), code:"CODE5", type: Type.TYPE_BOOL())
			def dataElement6 = new RawDataElement(names:j(["en":"Element 6"]), descriptions:j([:]), code:"CODE6", type: Type.TYPE_BOOL())
			def dataElement7 = new RawDataElement(names:j(["en":"Element 7"]), descriptions:j([:]), code:"CODE7", type: Type.TYPE_BOOL())
			def dataElement8 = new RawDataElement(names:j(["en":"Element 8"]), descriptions:j([:]), code:"CODE8", type: Type.TYPE_NUMBER())
			def dataElement9 = new RawDataElement(names:j(["en":"Element 9"]), descriptions:j([:]), code:"CODE9", type: Type.TYPE_NUMBER())
			def dataElement11 = new RawDataElement(names:j(["en":"Element 11"]), descriptions:j([:]), code:"CODE11", type: Type.TYPE_DATE())
			def dataElement12 = new RawDataElement(names:j(["en":"Element 12"]), descriptions:j([:]), code:"CODE12", type: Type.TYPE_TEXT())
			def dataElement81 = new RawDataElement(names:j(["en":"Element 81"]), descriptions:j([:]), code:"CODE81", type: Type.TYPE_NUMBER())
			def dataElement91 = new RawDataElement(names:j(["en":"Element 91"]), descriptions:j([:]), code:"CODE91", type: Type.TYPE_NUMBER())
			def dataElement101 = new RawDataElement(names:j(["en":"Element 101"]), descriptions:j([:]), code:"CODE101", type: Type.TYPE_ENUM (Enum.findByCode('ENUM2').code))
			def dataElement111 = new RawDataElement(names:j(["en":"Element 111"]), descriptions:j([:]), code:"CODE111", type: Type.TYPE_DATE())

			def dataElementList = new RawDataElement(names:j(["en":"Element 111"]), descriptions:j([:]), code:"LIST1", type:
					Type.TYPE_LIST(
					Type.TYPE_MAP([
						"key0": Type.TYPE_STRING(),
						"key1": Type.TYPE_MAP([
							"key11": Type.TYPE_MAP([
								"key111": Type.TYPE_NUMBER()
							])
						])
					])

				)
			)
			def dataElementMap = new RawDataElement(names:j(["en":"Element 111"]), descriptions:j([:]), code:"MAP1", 
				type: Type.TYPE_MAP([
					"key1": Type.TYPE_MAP([
						"key11": Type.TYPE_MAP([
							"key111": Type.TYPE_NUMBER()
						])	
					])	
				])
			)
			
			def planningElement = new RawDataElement(names:j(["en":"Element Planning"]), descriptions:j([:]), code:"PLANNINGELEMENT",
				type: Type.TYPE_LIST(Type.TYPE_MAP([
					"basic": Type.TYPE_MAP([
						"description": Type.TYPE_STRING(),
						"activity": Type.TYPE_ENUM(Enum.findByCode('ENUM1').code),
						"area": Type.TYPE_ENUM(Enum.findByCode('ENUM1').code),
						"instances": Type.TYPE_NUMBER(),
						"responsible": Type.TYPE_STRING(),
						"new_structure": Type.TYPE_BOOL(),
						"test_test": Type.TYPE_LIST(Type.TYPE_NUMBER()),
					]),
					"staffing": Type.TYPE_MAP([
						"nurse": Type.TYPE_MAP([
							"nurse_time": Type.TYPE_NUMBER(),
							"nurse_level": Type.TYPE_ENUM(Enum.findByCode('ENUM1').code)
						], true),
						"doctor": Type.TYPE_MAP([
							"doctor_time": Type.TYPE_NUMBER(),
							"doctor_level": Type.TYPE_ENUM(Enum.findByCode('ENUM1').code)
						], true),
						"other": Type.TYPE_MAP([
							"other_time": Type.TYPE_NUMBER(),
							"other_type": Type.TYPE_ENUM(Enum.findByCode('ENUM1').code)
						], true)
					]),
					"consumables": Type.TYPE_MAP([
						"tests": Type.TYPE_MAP([
							"blood_sugar": Type.TYPE_NUMBER(),
							"hiv": Type.TYPE_NUMBER()
						]),
						"medicine": Type.TYPE_MAP([
							"arv": Type.TYPE_NUMBER(),
							"tb": Type.TYPE_NUMBER(),
							"malaria": Type.TYPE_NUMBER()
						]),
						"other": Type.TYPE_LIST(
							Type.TYPE_MAP([
								"type": Type.TYPE_ENUM(Enum.findByCode('ENUM1').code),
								"number": Type.TYPE_NUMBER()	
							])	
						)
					]),
					"monthly_breakdown": Type.TYPE_MAP([
						"january": Type.TYPE_NUMBER(),
						"february": Type.TYPE_NUMBER(),
						"march": Type.TYPE_NUMBER(),
						"april": Type.TYPE_NUMBER(),
						"mai": Type.TYPE_NUMBER(),
						"june": Type.TYPE_NUMBER(),
						"july": Type.TYPE_NUMBER(),
						"august": Type.TYPE_NUMBER(),
						"september": Type.TYPE_NUMBER(),
						"october": Type.TYPE_NUMBER(),
						"november": Type.TYPE_NUMBER(),
						"december": Type.TYPE_NUMBER()
					]),
					"funding_sources": Type.TYPE_MAP([
						"general_fund": Type.TYPE_BOOL(),
						"sources": Type.TYPE_MAP([
							"location": Type.TYPE_NUMBER(),
							"minisante": Type.TYPE_NUMBER(),
							"hospital": Type.TYPE_NUMBER(),
							"gfatm": Type.TYPE_NUMBER(),
							"other": Type.TYPE_NUMBER()
						])
					])
				]))
			)
			
			def siyelo2 = new RawDataElement(names:j(["en":"Element 111"]), descriptions:j([:]), code:"LISTMAP2",
					type: Type.TYPE_LIST(
					Type.TYPE_MAP([
						"key0": Type.TYPE_MAP([
							"key01": Type.TYPE_STRING(),
							"key02": Type.TYPE_STRING()
						], true),
						"key1": Type.TYPE_MAP([
							"key11": Type.TYPE_MAP([
								"key111": Type.TYPE_ENUM(Enum.findByCode('ENUM1').code),
								"key112": Type.TYPE_STRING()
							], true),
							"key12": Type.TYPE_STRING(),
							"key13": Type.TYPE_MAP([
								"key131": Type.TYPE_ENUM(Enum.findByCode('ENUM1').code),
								"key132": Type.TYPE_STRING()
							], true),
							"key14": Type.TYPE_STRING(),
							"key15": Type.TYPE_STRING(),
							"key16": Type.TYPE_MAP([
								"key161": Type.TYPE_ENUM(Enum.findByCode('ENUM1').code),
								"key162": Type.TYPE_STRING()
							], true),
							"key17": Type.TYPE_ENUM(Enum.findByCode('ENUM1').code),
							"key18": Type.TYPE_NUMBER()
						]),
						"key2": Type.TYPE_MAP([
							"key21": Type.TYPE_STRING(),
							"key22": Type.TYPE_ENUM(Enum.findByCode('ENUM1').code),
							"key23": Type.TYPE_STRING(),
							"key24": Type.TYPE_DATE(),
							"key25": Type.TYPE_STRING(),
							"key26": Type.TYPE_ENUM(Enum.findByCode('ENUM1').code),
							"key27": Type.TYPE_STRING(),
							"key28": Type.TYPE_DATE(),
							"key29": Type.TYPE_DATE()
						])
					])
					)
					)

			def siyelo1 = new RawDataElement(names:j(["en":"Element 111"]), descriptions:j([:]), code:"LISTMAP1",
					type: Type.TYPE_LIST(
					Type.TYPE_MAP([
						"key1": Type.TYPE_STRING(),
						"key2": Type.TYPE_STRING(),
						"key3": Type.TYPE_STRING(),
						"key4": Type.TYPE_DATE(),
						"key5": Type.TYPE_ENUM(Enum.findByCode('ENUM1').code),
						"key6": Type.TYPE_STRING(),
						"key7": Type.TYPE_MAP([
							"key71": Type.TYPE_STRING(),
							"key72": Type.TYPE_STRING(),
							"key73": Type.TYPE_MAP([
								"key731": Type.TYPE_MAP([
									"key731_1": Type.TYPE_NUMBER(),
									"key731_2": Type.TYPE_NUMBER(),
									"key731_3": Type.TYPE_NUMBER(),
									"key731_4": Type.TYPE_NUMBER(),
									"key731_5": Type.TYPE_NUMBER(),
									"key731_6": Type.TYPE_NUMBER(),
									"key731_7": Type.TYPE_NUMBER(),
									"key731_8": Type.TYPE_NUMBER(),
									"key731_9": Type.TYPE_NUMBER(),
									"key731_10": Type.TYPE_NUMBER(),
									"key731_11": Type.TYPE_NUMBER(),
									"key731_12": Type.TYPE_NUMBER(),
									"key731_13": Type.TYPE_NUMBER(),
									"key731_14": Type.TYPE_NUMBER(),
									"key731_15": Type.TYPE_NUMBER(),
									"key731_16": Type.TYPE_NUMBER(),
									"key731_17": Type.TYPE_NUMBER(),
									"key731_18": Type.TYPE_NUMBER(),
									"key731_19": Type.TYPE_NUMBER()
								]),
								"key732": Type.TYPE_MAP([
									"key732_1": Type.TYPE_NUMBER(),
									"key732_2": Type.TYPE_NUMBER(),
									"key732_3": Type.TYPE_NUMBER(),
									"key732_4": Type.TYPE_NUMBER(),
									"key732_5": Type.TYPE_NUMBER(),
									"key732_6": Type.TYPE_NUMBER(),
									"key732_7": Type.TYPE_NUMBER(),
									"key732_8": Type.TYPE_NUMBER(),
									"key732_9": Type.TYPE_NUMBER(),
									"key732_10": Type.TYPE_NUMBER(),
									"key732_11": Type.TYPE_NUMBER(),
									"key732_12": Type.TYPE_NUMBER(),
									"key732_13": Type.TYPE_NUMBER(),
									"key732_14": Type.TYPE_NUMBER(),
									"key732_15": Type.TYPE_NUMBER(),
									"key732_16": Type.TYPE_NUMBER()
								])
							]),
						]),
						"key8": Type.TYPE_MAP([
							"key81": Type.TYPE_STRING(),
							"key82": Type.TYPE_STRING(),
							"key83": Type.TYPE_NUMBER(),
							"key84": Type.TYPE_MAP([
								"key84_1": Type.TYPE_BOOL(),
								"key84_2": Type.TYPE_BOOL(),
								"key84_3": Type.TYPE_BOOL(),
								"key84_4": Type.TYPE_BOOL(),
								"key84_5": Type.TYPE_BOOL(),
								"key84_6": Type.TYPE_BOOL(),
								"key84_7": Type.TYPE_BOOL(),
								"key84_8": Type.TYPE_BOOL(),
								"key84_9": Type.TYPE_BOOL(),
								"key84_10": Type.TYPE_BOOL(),
								"key84_11": Type.TYPE_BOOL(),
								"key84_12": Type.TYPE_BOOL(),
								"key84_13": Type.TYPE_BOOL(),
								"key84_14": Type.TYPE_BOOL(),
								"key84_15": Type.TYPE_BOOL(),
								"key84_16": Type.TYPE_BOOL(),
								"key84_17": Type.TYPE_BOOL(),
								"key84_18": Type.TYPE_BOOL(),
								"key84_19": Type.TYPE_BOOL(),
								"key84_20": Type.TYPE_BOOL(),
								"key84_21": Type.TYPE_BOOL(),
								"key84_22": Type.TYPE_BOOL(),
								"key84_23": Type.TYPE_BOOL(),
								"key84_24": Type.TYPE_BOOL()
							]),
							"key85": Type.TYPE_DATE(),
							"key86": Type.TYPE_DATE(),
						]),
						"key9": Type.TYPE_MAP([
							"key91": Type.TYPE_MAP([
								"key911": Type.TYPE_NUMBER(),
								"key912": Type.TYPE_NUMBER(),
								"key913": Type.TYPE_NUMBER(),
								"key914": Type.TYPE_NUMBER()
							]),
							"key92": Type.TYPE_MAP([
								"key921": Type.TYPE_BOOL(),
								"key922": Type.TYPE_BOOL(),
								"key923": Type.TYPE_BOOL(),
								"key924": Type.TYPE_BOOL()
							])
						])
					]))
					)

			def siyelo3 = new RawDataElement(names:j(["en":"Importer","fr":"Importer"]), descriptions:j([:]), code:"LISTMAP3",
					type: Type.TYPE_LIST(
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
							"primary_function": Type.TYPE_ENUM("primaryfunction"),
							"departments_served": Type.TYPE_MAP([
								"administration": Type.TYPE_BOOL()
							])
						])
					])
					)
					)

			dataElement10.save(failOnError: true, flush:true)
			dataElement1.save(failOnError: true, flush: true)
			dataElement2.save(failOnError: true, flush: true)
			dataElement3.save(failOnError: true, flush:true)
			dataElement4.save(failOnError: true, flush:true)
			dataElement5.save(failOnError: true, flush:true)
			dataElement6.save(failOnError: true, flush:true)
			dataElement7.save(failOnError: true, flush:true)
			dataElement8.save(failOnError: true, flush:true)
			dataElement9.save(failOnError: true, flush:true)
			dataElement11.save(failOnError: true, flush:true)
			dataElement81.save(failOnError: true, flush:true)
			dataElement91.save(failOnError: true, flush:true)
			dataElement101.save(failOnError: true, flush:true)
			dataElement111.save(failOnError: true, flush:true)
			dataElement12.save(failOnError: true, flush:true)

			dataElementList.save(failOnError: true, flush:true)
			dataElementMap.save(failOnError: true, flush:true)
			siyelo1.save(failOnError: true, flush:true)
			siyelo2.save(failOnError: true, flush:true)
			siyelo3.save(failOnError: true, flush:true)
			planningElement.save(failOnError: true, flush:true)
			
			// data value
			new RawDataElementValue(
					data: RawDataElement.findByCode("CODE1"),
					period: Period.list([cache: true])[0],
					location: DataLocation.findByCode("Butaro DH"),
					value: v("30"),
					timestamp: new Date(),
					).save(failOnError: true)
			// data value
			new RawDataElementValue(
					data: RawDataElement.findByCode("CODE1"),
					period: Period.list([cache: true])[0],
					location: DataLocation.findByCode("Kivuye HC"),
					value: v("40"),
					timestamp: new Date(),
					).save(failOnError: true)
			// data value
			new RawDataElementValue(
					data: RawDataElement.findByCode("CODE3"),
					period: Period.list([cache: true])[0],
					location: DataLocation.findByCode("Kivuye HC"),
					value: v("\"value1\""),
					timestamp: new Date(),
					).save(failOnError: true, flush:true)
			new RawDataElementValue(
					data: RawDataElement.findByCode("CODE4"),
					period: Period.list([cache: true])[0],
					location: DataLocation.findByCode("Kivuye HC"),
					value: v("true"),
					timestamp: new Date(),
					).save(failOnError: true, flush:true)
			new RawDataElementValue(
					data: RawDataElement.findByCode("CODE6"),
					period: Period.list([cache: true])[0],
					location: DataLocation.findByCode("Kivuye HC"),
					value: v("false"),
					timestamp: new Date(),
					).save(failOnError: true, flush:true)
			new RawDataElementValue(
					data: RawDataElement.findByCode("CODE8"),
					period: Period.list([cache: true])[0],
					location: DataLocation.findByCode("Kivuye HC"),
					value: v("10"),
					timestamp: new Date(),
					).save(failOnError: true, flush:true)
			new RawDataElementValue(
					data: RawDataElement.findByCode("CODE9"),
					period: Period.list([cache: true])[0],
					location: DataLocation.findByCode("Kivuye HC"),
					value: v("31"),
					timestamp: new Date(),
					).save(failOnError: true, flush:true)
			new RawDataElementValue(
					data: RawDataElement.findByCode("CODE10"),
					period: Period.list([cache: true])[0],
					location: DataLocation.findByCode("Kivuye HC"),
					value: v("\"NGO or Partner\""),
					timestamp: new Date(),
					).save(failOnError: true, flush:true)
			new RawDataElementValue(
					data: RawDataElement.findByCode("CODE11"),
					period: Period.list([cache: true])[0],
					location: DataLocation.findByCode("Kivuye HC"),
					value: v("\"2011-06-29\""),
					timestamp: new Date(),
					).save(failOnError: true, flush:true)
			new RawDataElementValue(
					data: RawDataElement.findByCode("CODE81"),
					period: Period.list([cache: true])[0],
					location: DataLocation.findByCode("Kivuye HC"),
					value: v("44"),
					timestamp: new Date(),
					).save(failOnError: true, flush:true)
			new RawDataElementValue(
					data: RawDataElement.findByCode("CODE91"),
					period: Period.list([cache: true])[0],
					location: DataLocation.findByCode("Kivuye HC"),
					value: v("33"),
					timestamp: new Date(),
					).save(failOnError: true, flush:true)
			new RawDataElementValue(
					data: RawDataElement.findByCode("CODE101"),
					period: Period.list([cache: true])[0],
					location: DataLocation.findByCode("Kivuye HC"),
					value: v("\"Ministry of Health\""),
					timestamp: new Date(),
					).save(failOnError: true, flush:true)
			new RawDataElementValue(
					data: RawDataElement.findByCode("CODE111"),
					period: Period.list([cache: true])[0],
					location: DataLocation.findByCode("Kivuye HC"),
					value: v("\"2011-06-30\""),
					timestamp: new Date(),
					).save(failOnError: true, flush:true)

			new RawDataElementValue(
					data: RawDataElement.findByCode("CODE12"),
					period: Period.list([cache: true])[0],
					location: DataLocation.findByCode("Kivuye HC"),
					value: v("\"I can not get into the Settings menu at all, when the phone is unlocked there is a blank screen.\""),
					timestamp: new Date(),
					).save(failOnError: true, flush:true)
		}


		if (!NormalizedDataElement.count()) {
			def period1 = Period.list([cache: true])[0]
			def dh = DataLocationType.findByCode('District Hospital')
			def hc = DataLocationType.findByCode('Health Center')

			// indicators
			new NormalizedDataElement(names:j(["en":"Constant 10"]), descriptions:j([:]), code:"Constant 10", expressionMap: e([(period1.id+''):[(hc.code):"10", (dh.code):"10"]]), type: Type.TYPE_NUMBER(), timestamp:new Date()).save(failOnError: true, flush: true)
			new NormalizedDataElement(names:j(["en":"Constant 20"]), descriptions:j([:]), code:"Constant 20", expressionMap: e([(period1.id+''):[(hc.code):"20", (dh.code):"20"]]), type: Type.TYPE_NUMBER(), timestamp:new Date()).save(failOnError: true, flush: true)
			new NormalizedDataElement(names:j(["en":"Constant 30"]), descriptions:j([:]), code:"Constant 30", expressionMap: e([(period1.id+''):[(hc.code):"30", (dh.code):"30"]]), type: Type.TYPE_NUMBER(), timestamp:new Date()).save(failOnError: true, flush: true)
			new NormalizedDataElement(names:j(["en":"Constant 40"]), descriptions:j([:]), code:"Constant 40", expressionMap: e([(period1.id+''):[(hc.code):"40", (dh.code):"40"]]), type: Type.TYPE_NUMBER(), timestamp:new Date()).save(failOnError: true, flush: true)
			
			def rd1 = RawDataElement.findByCode("CODE1").id
			new NormalizedDataElement(names:j(["en":"Element 1"]), descriptions:j([:]), 
				code:"Element 1", 
				expressionMap: e([(period1.id+''):[(hc.code):"\$"+rd1+"+\$"+rd1, (dh.code):"\$"+rd1+"+\$"+rd1]]), 
				type: Type.TYPE_NUMBER(), 
				timestamp:new Date()).save(failOnError: true, flush: true)
				
			def rd2 = RawDataElement.findByCode("CODE2").id
			new NormalizedDataElement(names:j(["en":"Element 2"]), descriptions:j([:]), 
				code:"Element 2", 
				expressionMap: e([(period1.id+''):[(hc.code):"\$"+rd2, (dh.code):"\$"+rd2]]), 
				type: Type.TYPE_NUMBER(), 
				timestamp:new Date()).save(failOnError: true, flush: true)
				
			def rd3 = RawDataElement.findByCode("CODE3").id
			new NormalizedDataElement(names:j(["en":"Element 3"]), descriptions:j([:]), 
				code:"Element 3", 
				expressionMap: e([(period1.id+''):[(hc.code):"\$"+rd3, (dh.code):"\$"+rd3]]), 
				type: Type.TYPE_NUMBER(), 
				timestamp:new Date()).save(failOnError: true, flush: true)
				
			new NormalizedDataElement(names:j(["en":"TRUE"]), descriptions:j([:]), code:"TRUE", expressionMap: e([(period1.id+''):[(hc.code):"true", (dh.code):"true"]]), type: Type.TYPE_BOOL(), timestamp:new Date()).save(failOnError: true, flush: true)
			new NormalizedDataElement(names:j(["en":"FALSE"]), descriptions:j([:]), code:"FALSE", expressionMap: e([(period1.id+''):[(hc.code):"false", (dh.code):"false"]]), type: Type.TYPE_BOOL(), timestamp:new Date()).save(failOnError: true, flush: true)
			
			new NormalizedDataElement(names:j(["en":"ZERO"]), descriptions:j([:]), code:"ZERO", expressionMap: e([(period1.id+''):[(hc.code):"0", (dh.code):"0"]]), type: Type.TYPE_NUMBER(), timestamp:new Date()).save(failOnError: true, flush: true)
			new NormalizedDataElement(names:j(["en":"ONE"]), descriptions:j([:]), code:"ONE", expressionMap: e([(period1.id+''):[(hc.code):"1", (dh.code):"1"]]), type: Type.TYPE_NUMBER(), timestamp:new Date()).save(failOnError: true, flush: true)
		}
	}

	static def createMaps() {
		if (!MapsTarget.count()) {
			def calculation1 = new Sum(expression: "\$"+NormalizedDataElement.findByCode("Element 1").id, code: "Maps sum 1", timestamp:new Date())
			calculation1.save(failOnError:true)
			new MapsTarget(names:j(["en":"Map Target 3"]), descriptions:j([:]), code:"TARGET3", calculation: calculation1).save(failOnError: true, flush:true)
		}
	}

	static def createCost() {
		if (!CostRampUp.count()) {
			// Cost
			new CostRampUp(names:j(["en":"Constant"]), descriptions:j([:]), code:"CONST", years: [
						1: new CostRampUpYear(year: 1, value: 0.2),
						2: new CostRampUpYear(year: 2, value: 0.2),
						3: new CostRampUpYear(year: 3, value: 0.2),
						4: new CostRampUpYear(year: 4, value: 0.2),
						5: new CostRampUpYear(year: 5, value: 0.2)
					]).save(failOnError: true);
		}

		if (!ReportProgram.count()) {
			def ga = ReportProgram.findByCode("Geographical Access")

			new CostTarget(
					names:j(["en":"Annual Internet Access Cost"]), code:"Internet Cost", descriptions:j(["en":"Annual Internet Access Cost"]),
					program: ga,
					dataElement: NormalizedDataElement.findByCode("Constant 10"),
					costType: CostType.OPERATION,
					costRampUp: CostRampUp.findByCode("CONST"),
					typeCodeString: "District Hospital,Health Center"
					).save(failOnError: true)

			new CostTarget(
					names:j(["en":"Connecting Centers to the Internet"]), code:"Connecting Centers", descriptions:j(["en":"Connecting Facilities to the Internet"]),
					program: ga,
					dataElement: NormalizedDataElement.findByCode("Constant 10"),
					costType: CostType.INVESTMENT,
					costRampUp: CostRampUp.findByCode("CONST"),
					typeCodeString: "District Hospital,Health Center"
					).save(failOnError: true)

			new CostTarget(
					names:j(["en":"New Phones for CHW Head Leader/Trainer & Assistant-Maintenance & Insurance"]), code:"New Phones CHW", descriptions:j(["en":"New Phones for CHW Head Leader/Trainer & Assistant-Maintenance & Insurance"]),
					program: ga,
					dataElement: NormalizedDataElement.findByCode("Constant 10"),
					costType: CostType.INVESTMENT,
					costRampUp: CostRampUp.findByCode("CONST"),
					typeCodeString: "District Hospital,Health Center"
					).save(failOnError: true)

			def hrh = ReportProgram.findByCode("Human Resources for Health")

			new CostTarget(
					names:j(["en":"Facility Staff Training"]), code:"Facility Staff Training", descriptions:j(["en":"Facility Staff Training"]),
					program: hrh,
					dataElement: NormalizedDataElement.findByCode("Constant 10"),
					costType: CostType.INVESTMENT,
					costRampUp: CostRampUp.findByCode("CONST")
					).save(failOnError: true)
		}
	}
	
	static def createDashboard() {
		if (!DashboardProgram.count()) {

			def root = ReportProgram.findByCode('Strategic Programs');
			def hrh = ReportProgram.findByCode('Human Resources for Health');
			def staffing = ReportProgram.findByCode('Staffing');

			def dashboardRoot = new DashboardProgram(names:j(["en":"Strategic Programs"]), weight: 0, code:"Strategic Programs", program: root)
			dashboardRoot.save(failOnError:true, flush: true)
			def dashboardHrh = new DashboardProgram(names:j(["en":"Human Resources for Health"]), weight: 1, order: 1, code:"Human Resources for Health", program: hrh)
			dashboardHrh.save(failOnError: true, flush: true)
			def dashboardStaffing = new DashboardProgram(names:j(["en":"Staffing"]), weight: 1, order: 1, code: "Staffing", program: staffing)
			dashboardStaffing.save(failOnError: true, flush: true)

			def calculation1 = new Sum(expression:"\$"+NormalizedDataElement.findByCode("Constant 10").id, code:"Ratio constant 10", timestamp:new Date())
			calculation1.save(failOnError: true)

			def nursea1 = new DashboardTarget(
					names:j(["en":"Nurse A1"]), code:"A1", descriptions:j(["en":"Nurse A1"]),
					calculation: calculation1, program: staffing,
					weight: 1, order: 1).save(failOnError: true, flush:true)

			def calculation2 = new Sum(expression:"\$"+NormalizedDataElement.findByCode("Constant 20").id, code:"Ratio constant 20", timestamp:new Date())
			calculation2.save(failOnError: true)

			def nursea2 = new DashboardTarget(
					names:j(["en":"Nurse A2"]), code:"A2", descriptions:j(["en":"Nurse A2"]),
					calculation: calculation2,  program: staffing,
					weight: 1, order: 2).save(failOnError: true, flush:true)

			def calculation3 = new Sum(expression:"\$"+NormalizedDataElement.findByCode("Element 1").id, code:"Ratio 1", timestamp:new Date())
			calculation3.save(failOnError: true)

			def target1 = new DashboardTarget(
					names:j(["en":"Target 1"]), code:"TARGET1", descriptions:j(["en":"Target 1"]),
					calculation: calculation3,  program: staffing,
					weight: 1, order: 3).save(failOnError: true, flush:true)

			def calculation4 = new Sum(expression:"\$"+NormalizedDataElement.findByCode("Element 2").id, code:"Ratio 2", timestamp:new Date())
			calculation4.save(failOnError: true)

			def missexpr = new DashboardTarget(
					names:j(["en":"Missing Expression"]), code:"MISSING EXPRESSION", descriptions:j(["en":"Missing Expression"]),
					calculation: calculation4,  program: staffing,
					weight: 1, order: 4).save(failOnError: true, flush:true)

			def calculation5 = new Sum(expression:"\$"+NormalizedDataElement.findByCode("Element 3").id, code:"Ratio 3", timestamp:new Date())
			calculation5.save(failOnError: true)

			def missdata = new DashboardTarget(
					names:j(["en":"Missing Data"]), code:"MISSING DATA", descriptions:j(["en":"Missing Data"]),
					calculation: calculation5,  program: staffing,
					weight: 1, order: 5).save(failOnError: true, flush:true)

			def calculation6 = new Sum(expression:"\$"+NormalizedDataElement.findByCode("Element 3").id, code:"Ratio 4", timestamp:new Date())
			calculation6.save(failOnError: true)

			def enume = new DashboardTarget(
					names:j(["en":"Enum"]), code:"ENUM", descriptions:j(["en":"Enum"]),
					calculation: calculation6, program: staffing,
					weight: 1, order: 6).save(failOnError: true, flush:true)

			nursea1.save(failOnError: true)
			nursea2.save(failOnError: true)
			target1.save(failOnError: true)
			missexpr.save(failOnError: true)
			missdata.save(failOnError: true)
			enume.save(failOnError: true)

			staffing.save(failOnError: true, flush:true)
		}
	}
	
	static def createDsr() {
		if (!DsrTarget.count()) {
			def dh = DataLocationType.findByCode("District Hospital")
			def hc = DataLocationType.findByCode("Health Center")

			def servDeliv = ReportProgram.findByCode("Service Delivery")
			def instCap = ReportProgram.findByCode("Institutional Capacity")
			def hmr = ReportProgram.findByCode("Human Resources for Health")

			def root = ReportProgram.findByCode("Strategic Programs")
			root.addChild(servDeliv)
			root.addChild(instCap)
			root.addChild(hmr)
			root.save(failOnError: true, flush: true)

			def infectiousDiseaseCat1 = new DsrTargetCategory(
					names:j(["en":"Infectious Disease Testing Offered 1"]),
					order: 1,
					descriptions:j(["en":"Infectious Disease Testing Offered 1"]),
					code: "Infectious Disease Testing Offered 1"
					)
			def infectiousDiseaseCat2 = new DsrTargetCategory(
					names:j(["en":"Infectious Disease Testing Offered 2"]),
					order: 2,
					descriptions:j(["en":"Infectious Disease Testing Offered 2"]),
					code: "Infectious Disease Testing Offered 2"
					)			
			def nursesCat = new DsrTargetCategory(
					names:j(["en":"Nurses"]),
					descriptions:j(["en":"Nurses"]),
					order: 3,
					code: "Nurses"
					)
			def waterAndPowerCat = new DsrTargetCategory(
					names:j(["en":"Facility Water and Power Sources"]),
					order: 2,
					descriptions:j(["en":"Facility Water and Power Sources"]),
					code: "Facility Water and Power Sources"
					)

			def dsrRatio = new Sum(expression:"\$"+NormalizedDataElement.findByCode("Constant 10").id, code:"Dsr Ratio constant 10", timestamp:new Date())
			dsrRatio.save(failOnError: true)
			
			def dsrSum = new Sum(expression: "\$"+NormalizedDataElement.findByCode("Constant 10").id, code:"Dsr Sum constant 10", timestamp:new Date());
			dsrSum.save(failOnError: true);			
			

			new DsrTarget(
					names:j(["en":"A0"]), descriptions:j(["en":"A0"]),
					program: hmr,
					data: RawDataElement.findByCode("CODE1"),
					order: 1,
					code: "A0",
					category: nursesCat,
					).save(failOnError:true)
										
			new DsrTarget(
					names:j(["en":"A1"]), descriptions:j(["en":"A1"]),
					program: hmr,
					data: NormalizedDataElement.findByCode("TRUE"),
					order: 2,
					code: "A1",
					category: nursesCat,
					).save(failOnError:true)

			new DsrTarget(
					names:j(["en":"A2"]), descriptions:j(["en":"A2"]),
					program: hmr,
					data: NormalizedDataElement.findByCode("FALSE"),
					order: 3,
					code:"A2",
					category: nursesCat,
					).save(failOnError:true)

			new DsrTarget(
					names:j(["en":"A3"]), descriptions:j(["en":"A3"]),
					program: hmr,
					data: dsrRatio,
					order: 4,
					code: "A3",
					category: nursesCat,
					).save(failOnError:true)

			new DsrTarget(
					names:j(["en":"A4"]), descriptions:j(["en":"A4"]),
					program: hmr,
					data: dsrSum,
					order: 5,
					code: "A4",
					category: nursesCat,
					).save(failOnError:true)
					
			new DsrTarget(
					names:j(["en":"Mental Health Service"]), descriptions:j(["en":"Mental Health Service"]),
					program: servDeliv,
					data: NormalizedDataElement.findByCode("Constant 20"),
					order: 11,
					code: "Mental Health Service",
					category: infectiousDiseaseCat2
					).save(failOnError:true)

			new DsrTarget(
					names:j(["en":"Malaria Rapid Test"]), descriptions:j(["en":"Malaria Rapid Test"]),
					program: servDeliv,
					data: NormalizedDataElement.findByCode("Constant 20"),
					order: 7,
					code: "Malaria Rapid Test",
					category: infectiousDiseaseCat1
					).save(failOnError:true)

			new DsrTarget(
					names:j(["en":"HIV Rapid Test"]), descriptions:j(["en":"HIV Rapid Test"]),
					program: servDeliv,
					data: NormalizedDataElement.findByCode("Constant 10"),
					order: 9,
					code: "HIV Rapid Test",
					category: infectiousDiseaseCat1
					).save(failOnError:true)

			new DsrTarget(
					names:j(["en":"TB Stain Test"]), descriptions:j(["en":"TB Stain Test"]),
					program: servDeliv,
					data: NormalizedDataElement.findByCode("Constant 20"),
					order: 10,
					code: "TB Stain Test",
					category: infectiousDiseaseCat2
					).save(failOnError:true)

			new DsrTarget(
					names:j(["en":"Facility Water Status"]), descriptions:j(["en":"Facility Water Status"]),
					program: instCap,
					data: NormalizedDataElement.findByCode("Constant 10"),
					order: 3,
					code: "Facility Water Status",
					category: waterAndPowerCat
					).save(failOnError:true)

			new DsrTarget(
					names:j(["en":"Incinerator Availability"]), descriptions:j(["en":"Incinerator Availability"]),
					program: instCap,
					data: NormalizedDataElement.findByCode("Constant 10"),
					order: 2,
					code: "Incinerator Availability",
					category: waterAndPowerCat
					).save(failOnError:true)

			infectiousDiseaseCat1.addTarget(DsrTarget.findByCode("Malaria Rapid Test"));
			infectiousDiseaseCat1.addTarget(DsrTarget.findByCode("HIV Rapid Test"));
			infectiousDiseaseCat1.save(failOnError:true);
			
			infectiousDiseaseCat2.addTarget(DsrTarget.findByCode("Mental Health Service"));
			infectiousDiseaseCat2.addTarget(DsrTarget.findByCode("TB Stain Test"));
			infectiousDiseaseCat2.save(failOnError:true);

			nursesCat.addTarget(DsrTarget.findByCode("A0"));
			nursesCat.addTarget(DsrTarget.findByCode("A1"));
			nursesCat.addTarget(DsrTarget.findByCode("A2"));
			nursesCat.addTarget(DsrTarget.findByCode("A3"));
			nursesCat.addTarget(DsrTarget.findByCode("A4"));
			nursesCat.save(failOnError:true);

			waterAndPowerCat.addTarget(DsrTarget.findByCode("Facility Water Status"));
			waterAndPowerCat.addTarget(DsrTarget.findByCode("Incinerator Availability"));
			waterAndPowerCat.save(failOnError:true);			
		}
	}

	static def createFct() {
		if (!FctTarget.count()) {
			def dh = DataLocationType.findByCode("District Hospital")
			def hc = DataLocationType.findByCode("Health Center")
			def hmr = ReportProgram.findByCode("Human Resources for Health")
			
			def sumZero = new Sum(expression: "\$"+NormalizedDataElement.findByCode("ZERO").id, code:"Sum ZERO", timestamp:new Date());
			sumZero.save(failOnError: true);
			
			def sumOne = new Sum(expression: "\$"+NormalizedDataElement.findByCode("ONE").id, code:"Sum ONE", timestamp:new Date());
			sumOne.save(failOnError: true);
						
			FctTarget fctTarget1 = new FctTarget(

				names:j(["en":"Fct Target 1"]), 
				program: hmr,
				descriptions:j([:]), 
				code:"TARGET 1"
			).save(failOnError:true)
			
			FctTargetOption fctTargetOption1 = new FctTargetOption(
				names:j(["en": "Target Option 1"]),
				target: fctTarget1,
				descriptions:j([:]), 
				code:"TARGET OPTION 1",
				sum: sumOne
			).save(failOnError:true)
			
			FctTargetOption fctTargetOption2 = new FctTargetOption(
				names:j(["en": "Target Option 2"]),
				target: fctTarget1,
				descriptions:j([:]),
				code:"TARGET OPTION 2",
				sum: sumZero
			).save(failOnError:true)
			
			fctTarget1.targetOptions << [fctTargetOption1, fctTargetOption2]
			fctTarget1.save(failOnError:true)						

			FctTarget fctTarget2 = new FctTarget(
				names:j(["en":"Fct Target 2"]), descriptions:j([:]),
				program: hmr,
				code:"TARGET 2"
			).save(failOnError:true)
			
			FctTargetOption fctTargetOption3 = new FctTargetOption(
				names:j(["en": "Target Option 3"]),
				target: fctTarget2,
				descriptions:j([:]),
				code:"TARGET OPTION 3",
				sum: sumZero
			).save(failOnError:true)
			
			FctTargetOption fctTargetOption4 = new FctTargetOption(
				names:j(["en": "Target Option 4"]),
				target: fctTarget2,
				descriptions:j([:]),
				code:"TARGET OPTION 4",
				sum: sumOne
			).save(failOnError:true)
			
			fctTarget2.targetOptions << [fctTargetOption3, fctTargetOption4]
			fctTarget2.save(failOnError:true)
			
			FctTarget fctTarget3 = new FctTarget(
				names:j(["en":"Fct Target 3"]), descriptions:j([:]),
				program: hmr,
				targetOptions: [],
				code:"TARGET 3"
			).save(failOnError:true)
			
			hmr.save(failOnError:true)
		}
	}
	
	
	static def createPlanning() {
		
		def planning = new Planning(
			period: Period.list([cache: true])[0],
			names: j(["en":"Planning 2011"]),
			typeCodeString: "Health Center",
			overviewHelps: j(["en": "Some help information for the planning tool - overview"]),
			budgetHelps: j(["en": "Some help information for the planning tool - budget"]),
			active: true
		).save(failOnError: true)
		
		def formElement = new FormElement(
				dataElement: RawDataElement.findByCode("PLANNINGELEMENT"),
				headers: [
					"[_].basic": j(["en":"Basic Information"]),
					"[_].basic.description": j(["en":"Description"]),
					"[_].basic.activity": j(["en":"Activity"]),
					"[_].basic.area": j(["en":"Service area"]),
					"[_].basic.instances": j(["en":"Number of instances"]),
					"[_].basic.responsible": j(["en":"Person responsible"]),
					"[_].basic.new_structure": j(["en":"Requires new room/structure"]),
					"[_].staffing": j(["en":"Staffing Requirements"]),
					"[_].staffing.nurse": j(["en":"Nurse"]),
					"[_].staffing.nurse.nurse_time": j(["en":"Time per instance"]),
					"[_].staffing.nurse.nurse_level": j(["en":"Level of nurse"]),
					"[_].staffing.doctor": j(["en":"Doctor"]),
					"[_].staffing.doctor.doctor_time": j(["en":"Time per instance"]),
					"[_].staffing.doctor.doctor_level": j(["en":"Level of doctor"]),
					"[_].staffing.other": j(["en":"Other staff"]),
					"[_].staffing.other.other_time": j(["en":"Time per instance"]),
					"[_].staffing.other.other_type": j(["en":"Level of staff"]),
					"[_].consumables": j(["en":"Consumables"]),
					"[_].consumables.tests": j(["en":"Number of required tests"]),
					"[_].consumables.tests.blood_sugar": j(["en":"Sugar in blood"]),
					"[_].consumables.tests.hiv": j(["en":"HIV"]),
					"[_].consumables.medicine": j(["en":"Drugs required"]),
					"[_].consumables.medicine.arv": j(["en":"ARV drugs"]),
					"[_].consumables.medicine.tb": j(["en":"TB drugs"]),
					"[_].consumables.medicine.malaria": j(["en":"Malaria drugs"]),
					"[_].consumables.other": j(["en":"Other"]),
					"[_].consumables.other[_].type": j(["en":"Type"]),
					"[_].consumables.other[_].number": j(["en":"Number required"]),
					"[_].monthly_breakdown": j(["en":"Monthly Breakdown"]),
					"[_].monthly_breakdown.january": j(["en":"January"]),
					"[_].monthly_breakdown.february": j(["en":"February"]),
					"[_].monthly_breakdown.march": j(["en":"March"]),
					"[_].monthly_breakdown.april": j(["en":"April"]),
					"[_].monthly_breakdown.mai": j(["en":"Mai"]),
					"[_].monthly_breakdown.june": j(["en":"June"]),
					"[_].monthly_breakdown.july": j(["en":"July"]),
					"[_].monthly_breakdown.august": j(["en":"August"]),
					"[_].monthly_breakdown.september": j(["en":"September"]),
					"[_].monthly_breakdown.october": j(["en":"October"]),
					"[_].monthly_breakdown.november": j(["en":"November"]),
					"[_].monthly_breakdown.december": j(["en":"December"]),
					"[_].funding_sources": j(["en":"Funding Sources"]),
					"[_].funding_sources.general_fund": j(["en":"Funded by the general fund"]),
					"[_].funding_sources.sources": j(["en":"Individual sources"]),
					"[_].funding_sources.sources.location": j(["en":"Facility"]),
					"[_].funding_sources.sources.minisante": j(["en":"Minisanté"]),
					"[_].funding_sources.sources.hospital": j(["en":"District hospital"]),
					"[_].funding_sources.sources.gfatm": j(["en":"Global Fund"]),
					"[_].funding_sources.sources.other": j(["en":"Other"])
				]
			).save(failOnError: true)
		
			def validationRule1 = new FormValidationRule(
				formElement: formElement,
				prefix: '[_].basic.instances',
				expression: "\$"+formElement.id+"[_].basic.instances > 100",
				messages: j(["en":"Validation error {0,here}"]),
				dependencies: [formElement],
				typeCodeString: "District Hospital,Health Center",
				allowOutlier: false
			).save(failOnError: true)
			
			def validationRule2 = new FormValidationRule(
				formElement: formElement,
				prefix: '[_].basic.test_test[_]',
				expression: "\$"+formElement.id+"[_].basic.test_test[_] > 100",
				messages: j(["en":"Validation error"]),
				dependencies: [],
				typeCodeString: "District Hospital,Health Center",
				allowOutlier: false
			).save(failOnError: true)
			
		formElement.addValidationRule(validationRule1)
		formElement.addValidationRule(validationRule2)
		formElement.save(failOnError: true)
		
		// add validation and skip rules
		def formSkip = new PlanningSkipRule(planning: planning, expression: "\$"+formElement.id+"[_].basic.instances == 1", skippedFormElements: [(formElement): "[_].basic.responsible,[_].consumables"]).save(failOnError: true);
		planning.addSkipRule(formSkip)
		planning.save(failOnError: true)
		
		def planningType = new PlanningType(
			names: j(["en":"Activity"]),
			namesPlural: j(["en":"Activities"]),
			sectionDescriptions: [
				"[_].basic": j(["en":"Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat."]),
				"[_].staffing": j(["en":"Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat."]),
				"[_].consumables": j(["en":"Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat."]),
				"[_].monthly_breakdown": j(["en":"Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat."]),
				"[_].funding_sources": j(["en":"Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat."])
			],
			formElement: formElement,
			fixedHeader: '[_].basic.description',
			listHelps: j(["en": "Some help information for the planning tool - list"]),
			newHelps: j(["en": "Some help information for the planning tool - new"]),
			planning: planning
		).save(failOnError: true);
		planning.planningTypes << planningType
		planning.save(failOnError: true)
	
		def planningElement1 = new NormalizedDataElement(
			code: 'SUMPLANNING1',
			type: Type.TYPE_LIST(Type.TYPE_NUMBER()),
			expressionMap: e([(Period.list([cache: true])[0].id+''):[("Health Center"): '($'+RawDataElement.findByCode("PLANNINGELEMENT").id+' -> transform each x (if (x.basic.activity == "value1") x.basic.instances * 2 else 0))']])
		).save(failOnError: true)
		
		def planningCost1 = new PlanningCost(
			planningType: planningType,
			type: PlanningCostType.INCOMING,
			dataElement: planningElement1,
			names: j(["en":"Group - Salaries"])
		).save(failOnError: true)
	
		def planningElement2 = new NormalizedDataElement(
			code: 'SUMPLANNING2',
			type: Type.TYPE_LIST(Type.TYPE_NUMBER()),
			expressionMap: e([(Period.list([cache: true])[0].id+''):[("Health Center"): '($'+RawDataElement.findByCode("PLANNINGELEMENT").id+' -> transform each x (if (x.basic.activity == "value1") x.basic.instances * 10 else 0))']])
		).save(failOnError: true)
		
		def planningCost2 = new PlanningCost(
			planningType: planningType,
			type: PlanningCostType.OUTGOING,
			dataElement: planningElement2,
			names: j(["en":"Group - Patient"])
		).save(failOnError: true)
		
		planningType.costs << planningCost1
		planningType.costs << planningCost2
		planningType.save(failOnError: true)
		
		def planningOutput1 = new PlanningOutput(
			planning: planning,
			fixedHeader: '[_].basic.activity',
			dataElement: RawDataElement.findByCode("PLANNINGELEMENT"),
			names: j(["en": "Follow-up"])
		).save(failOnError: true)
		
		planning.planningOutputs << planningOutput1
		planning.save(failOnError: true)
		
		def planningOutputColumn = new PlanningOutputColumn(
			planningOutput: planningOutput1,
			normalizedDataElement: planningElement2,
			names: j(["en": "Test"])
		)
		planningOutputColumn.save(failOnError: true)
		planningOutput1.columns << planningOutputColumn
		planningOutput1.save(failOnError: true)
		
		new FormEnteredValue(
			formElement: formElement,
			dataLocation: DataLocation.findByCode("Kivuye HC"),
			value: Value.VALUE_LIST([
				Value.VALUE_MAP([
					"basic": Value.VALUE_MAP([
						"description": Value.VALUE_STRING("Test - Activity"),
						"activity": Value.VALUE_STRING("value1"), 
						"instances": Value.VALUE_NUMBER(10)
					])
				])
			]),
			timestamp: new Date()
		).save(failOnError: true)
	}
	
	
	static def createExporter(){
		if(!DataElementExport.count()){
			def dh = DataLocationType.findByCode("District Hospital")
			def hc = DataLocationType.findByCode("Health Center")
			def periodOne = Period.list()[0];
			def periodTwo = Period.list()[1];
			
			def dEtwo = RawDataElement.findByCode("CODE2");
			def dEthree = RawDataElement.findByCode("CODE3");
			def dEfour = RawDataElement.findByCode("CODE4");
			def dEfive = RawDataElement.findByCode("CODE11");
			def dEsix = RawDataElement.findByCode("CODE12");
			def dMap = RawDataElement.findByCode("LISTMAP1");
			
			def dataLocationOne = DataLocation.findByCode("Kivuye HC");
			def dataLocationTwo = DataLocation.findByCode("Butaro DH");
			def burera = Location.findByCode("Burera");
			def est = Location.findByCode("East");
			def south = Location.findByCode("South");
			
			
			def exporterThree = new DataElementExport(
				descriptions: j(["en":"Exporter Raw Data Element Three"]),
				date: new Date(),
				typeCodeString:"Health Center",
				locations:[south,dataLocationTwo],
				dataElements:[dMap,dEtwo,dEthree,dEfive,dEsix],
				periods: [periodOne,periodTwo]
				).save(failOnError: true)
			
			def exporterTwo = new DataElementExport(
				descriptions: j(["en":"Exporter Raw Data Element Two"]),
				date: new Date(),
				typeCodeString:"Health Center",
				locations:[south,burera],
				dataElements:[dEtwo,dEthree,dMap],
				periods: [periodOne]
				).save(failOnError: true)
				
			def exporterOne = new DataElementExport(
				descriptions: j(["en":"Exporter Raw Data Element One"]),
				date: new Date(),
				locations:[est,burera,south],
				typeCodeString:"District Hospital,Health Center",
				dataElements:[dMap,dEtwo,dEthree,dEfour,dEfive,dEsix],
				periods: [periodOne,periodTwo]
				).save(failOnError: true)
				
			def exporterFour = new DataElementExport(
				descriptions: j(["en":"Exporter Raw Data Element Four"]),
				date: new Date(),
				typeCodeString:"District Hospital",
				locations:[est,dataLocationOne],
				dataElements:[dMap,dEtwo,dEfour,dEfive,dEsix],
				periods: [periodOne,periodTwo]
				).save(failOnError: true)
			
			
				
		}
	}

		
	static def createQuestionaire(){
		if(!Survey.count()){

			def dh = DataLocationType.findByCode("District Hospital")
			def hc = DataLocationType.findByCode("Health Center")

			//Creating Survey
			def surveyOne = new Survey(
					code:"Survey1",
					names: j(["en":"Survey Number 1"]),
					descriptions: j(["en":"Survey Number 1 Description"]),
					period: Period.list([cache: true])[1],
					lastPeriod: Period.list([cache: true])[0],
					active: true,
					)
			def surveyTwo = new Survey(
					code:"Survey2",
					names: j(["en":"Survey Number 2"]),
					descriptions: j(["en":"Survey Number 2 Description"]),
					period: Period.list([cache: true])[1],
					)

			//Creating Program
			def serviceDev = new SurveyProgram(
				code:"Program1",
				names: j(["en":"Service Delivery"]),
				order: 2,
				typeCodeString: "District Hospital,Health Center"
			)
			def hResourceHealth = new SurveyProgram(
				code:"Program2",
				names: j(["en":"Human Resources for Health"]),
				order: 4,
				typeCodeString: "District Hospital,Health Center",
			)

			def geoAccess = new SurveyProgram(
				code:"Program3",
				names: j(["en":"Geographic Access"]),
				order: 5,
				typeCodeString: "District Hospital,Health Center",
			)

			def institutCap = new SurveyProgram(
				code:"Program4",
				names: j(["en":"Institutional Capacity"]),
				order: 3,
				typeCodeString: "Health Center",
			)

			def coreFacId = new SurveyProgram(
				code:"Program5",
				names: j(["en":"Core Facility Identify"]),
				order: 1,
				typeCodeString: "District Hospital,Health Center",
			)

			def finance = new SurveyProgram(
				code:"Program6",
				names: j(["en":"Finance"]),
				order: 6,
				typeCodeString: "District Hospital,Health Center",
			)

			def dvandC = new SurveyProgram(
				code:"Program7",
				names: j(["en":"Drugs, Vaccines, and Consumables"]),
				order: 7,
				typeCodeString: "District Hospital,Health Center",
			)


			surveyOne.addProgram(serviceDev)
			surveyOne.addProgram(coreFacId)
			surveyOne.addProgram(hResourceHealth)
			surveyOne.addProgram(finance)
			surveyOne.save(failOnError:true)

			surveyTwo.addProgram(geoAccess)
			surveyTwo.addProgram(dvandC)
			surveyTwo.addProgram(institutCap)
			surveyTwo.save(failOnError:true)

			//Adding section to program
			def facilityId = new SurveySection(
				code:"Section1",
				names: j(["en":"Facility Identifier"]),
				order: 1,
				program: coreFacId,
				typeCodeString: "District Hospital,Health Center"
			)

			coreFacId.addSection(facilityId)
			coreFacId.save(failOnError:true);

			def services=new SurveySection(
				code:"Section2",
				names: j(["en":"Services"]),
				order: 2,
				program: serviceDev,
				typeCodeString: "District Hospital,Health Center"
			)
			def labTests= new SurveySection(
				code:"Section3",
				names: j(["en":"Lab Tests"]),
				order: 1,
				program: serviceDev,
				typeCodeString: "District Hospital"
			)

			def patientReg=new SurveySection(
				code:"Section4",
				names: j(["en":"Patient Registration"]),
				order: 3,
				program: serviceDev,
				typeCodeString: "District Hospital,Health Center"
			)

			def patientQ1 = new SurveySimpleQuestion(
				code:"Question1",
				names: j(["en":"Patient Section Simple Question NUMBER"]),
				descriptions: j([:]),
				order: 3,
				typeCodeString: "District Hospital,Health Center"
			)
			patientReg.addQuestion(patientQ1)
			patientReg.save(failOnError: true)

			def surveyElementPatientQ1 = new SurveyElement(dataElement: RawDataElement.findByCode("CODE1"), surveyQuestion: patientQ1).save(failOnError: true)
			patientQ1.surveyElement = surveyElementPatientQ1
			patientQ1.save(failOnError: true)

			serviceDev.addSection(services)
			serviceDev.addSection(labTests)
			serviceDev.addSection(patientReg)
			serviceDev.save(failOnError:true);

			def rulePatient1 = new FormValidationRule(
				formElement: surveyElementPatientQ1,
				expression: "\$"+surveyElementPatientQ1.id+" > 100",
				messages: j(["en":"Validation error {0,here}"]),
				dependencies: [surveyElementPatientQ1],
				typeCodeString: "District Hospital,Health Center",
				allowOutlier: false
			).save(failOnError: true)
			def rulePatient2 = new FormValidationRule(
				formElement: surveyElementPatientQ1,
				expression: "\$"+surveyElementPatientQ1.id+" > 140",
				messages: j(["en":"Validation error {0,here}"]),
				dependencies: [surveyElementPatientQ1],
				typeCodeString: "District Hospital,Health Center",
				allowOutlier: true
			).save(failOnError: true)
			
			surveyElementPatientQ1.addValidationRule(rulePatient1)
			surveyElementPatientQ1.addValidationRule(rulePatient2)
			surveyElementPatientQ1.save(failOnError: true)

			def staffing=new SurveySection(
				code:"Section5",
				names: j(["en":"Staffing"]),
				order: 1,
				program: hResourceHealth,
				typeCodeString: "District Hospital,Health Center"	
			)

			def continuingEd = new SurveySection(
				code:"Section6",
				names: j(["en":"Continuing Education"]),
				order: 2,
				program: hResourceHealth,
				typeCodeString: "Health Center"
			)

			def openResponse = new SurveySection(
				code:"Section7",
				names: j(["en":"Open Response"]),
				order: 3,
				program: hResourceHealth,
				typeCodeString: "District Hospital,Health Center"
			)


			hResourceHealth.addSection(staffing)
			hResourceHealth.addSection(continuingEd)
			hResourceHealth.addSection(openResponse)
			hResourceHealth.save(failOnError:true);

			def infrastructure = new SurveySection(
				code:"Section8",
				names: j(["en":"Infrastructure"]),
				order: 3,
				program: geoAccess,
				typeCodeString: "District Hospital,Health Center"
			)
			def medicalEq=new SurveySection(
				code:"Section9",
				names: j(["en":"Medical Equipment"]),
				order: 2,
				program: geoAccess,
				typeCodeString: "District Hospital,Health Center"	
			)
			def wasteMgmnt=new SurveySection(
				code:"Section10",
				names: j(["en":"Waste Management"]),
				order: 1,
				program: geoAccess,
				typeCodeString: "District Hospital,Health Center"
			)


			geoAccess.addSection(infrastructure)
			geoAccess.addSection(medicalEq)
			geoAccess.addSection(wasteMgmnt)
			geoAccess.save(failOnError:true);

			//Adding questions to sections
			def serviceQ1 = new SurveySimpleQuestion(
				code:"Question2",
				names: j(["en":"Service Section Simple Question NUMBER"]),
				descriptions: j(["en":"<br/>"]),
				order: 3,
				typeCodeString: "District Hospital,Health Center"
			)

			services.addQuestion(serviceQ1)
			services.save(failOnError:true, flush:true)

			def surveyElementServiceQ1 = new SurveyElement(dataElement: RawDataElement.findByCode("CODE1"), surveyQuestion: serviceQ1).save(failOnError: true)
			serviceQ1.surveyElement = surveyElementServiceQ1
			serviceQ1.save(failOnError: true)

			def serviceQ2 = new SurveySimpleQuestion(
				code:"Question3",
				names: j(["en":"Service Section Simple Question BOOL"]),
				descriptions: j(["en":""]),
				order: 0,
				typeCodeString: "District Hospital,Health Center"
			)

			services.addQuestion(serviceQ2)
			services.save(failOnError:true, flush:true)

			def surveyElementServiceQ2 = new SurveyElement(dataElement: RawDataElement.findByCode("CODE7"), surveyQuestion: serviceQ2).save(failOnError: true)
			serviceQ2.surveyElement = surveyElementServiceQ2
			serviceQ2.save(failOnError: true)

			def serviceQ3 = new SurveySimpleQuestion(
				code:"Question4",
				names: j(["en":"Service Section Simple Question ENUM "]),
				descriptions: j([:]),
				order: 0,
				typeCodeString: "District Hospital,Health Center"
			)

			services.addQuestion(serviceQ3)
			services.save(failOnError:true, flush:true)

			def surveyElementServiceQ3 = new SurveyElement(dataElement: RawDataElement.findByCode("CODE3"), surveyQuestion: serviceQ3).save(failOnError: true)
			serviceQ3.surveyElement = surveyElementServiceQ3
			serviceQ3.save(failOnError: true)

//			def serviceQ4 = new SurveySimpleQuestion(
//					names: j(["en":"Service Section Simple Question LIST"]),
//					descriptions: j(["en":"Help text"]),
//  				order: o(["en":4]),
//					typeCodeString: "District Hospital,Health Center"
//					)
//			services.addQuestion(serviceQ4)
//			services.save(failOnError:true, flush:true)


			//			def surveyElementServiceQ4 = new SurveyElement(dataElement: DataElement.findByCode("LIST1"), surveyQuestion: serviceQ4).save(failOnError: true)
			//			serviceQ4.surveyElement = surveyElementServiceQ4
			//			serviceQ4.save(failOnError: true)

			def serviceQ5 = new SurveySimpleQuestion(
					code:"Question5",
					names: j(["en":"Service Section Simple Question MAP"]),
					descriptions: j(["en":"<div>Help text</div>"]),
					order: 5,

//					order: o(["en":5]),
					typeCodeString: "District Hospital,Health Center"
					)
			services.addQuestion(serviceQ5)
			services.save(failOnError:true, flush:true)

			def surveyElementServiceQ5 = new SurveyElement(
					dataElement: RawDataElement.findByCode("MAP1"),
					surveyQuestion: serviceQ5,
					headers: [
						".key1.key11.key111":j(["en": "Header 1"])
					]).save(failOnError: true)
			serviceQ5.surveyElement = surveyElementServiceQ5
			serviceQ5.save(failOnError: true)

			def serviceQ6 = new SurveySimpleQuestion(
					code:"Question6",
					names: j(["en":"Service Section Simple Question LIST of MAP"]),
					descriptions: j(["en":"Help text"]),
					order: 6,

//					order: o(["en":6]),
					typeCodeString: "District Hospital,Health Center"
					)
			services.addQuestion(serviceQ6)
			services.save(failOnError:true, flush:true)

			def surveyElementServiceQ6 = new SurveyElement(
				dataElement: RawDataElement.findByCode("LISTMAP2"),
				surveyQuestion: serviceQ6,
				headers: [
					"[_].key0": j(["en":"Name"]),
					"[_].key0.key01": j(["en":"Select from list"]),
					"[_].key0.key02": j(["en":"If other"]),
					"[_].key1": j(["en":"Identifiers"]),
					"[_].key1.key11": j(["en":"Type of equipment"]),
					"[_].key1.key11.key111": j(["en":"Select from list"]),
					"[_].key1.key11.key112": j(["en":"If other, specify:"]),
					"[_].key1.key12": j(["en":"Description"]),
					"[_].key1.key13": j(["en":"Serial"]),
					"[_].key1.key13.key131": j(["en":"Select from list"]),
					"[_].key1.key13.key132": j(["en":"If other, please specify"]),
					"[_].key1.key14": j(["en":"Model"]),
					"[_].key1.key15": j(["en":"Manufacturer"]),
					"[_].key1.key16": j(["en":"Status"]),
					"[_].key1.key16.key161": j(["en":"Please select from list:"]),
					"[_].key1.key16.key162": j(["en":"If not fully functional:"]),
					"[_].key1.key17": j(["en":"Primary location"]),
					"[_].key1.key18": j(["en":"Avg. daily hours of use"]),
					"[_].key2": j(["en":"Supply and Maintenance"]),
					"[_].key2.key21": j(["en":"Supplier Name"]),
					"[_].key2.key22": j(["en":"Supplier Type"]),
					"[_].key2.key23": j(["en":"Supplier Mobile"]),
					"[_].key2.key24": j(["en":"Date Acquired"]),
					"[_].key2.key25": j(["en":"Service Provider Name"]),
					"[_].key2.key26": j(["en":"Service Provider Type"]),
					"[_].key2.key27": j(["en":"Service Provider Mobile"]),
					"[_].key2.key28": j(["en":"Date of last repair"]),
					"[_].key2.key29": j(["en":"Date of last service"])
				]).save(failOnError: true) 
			
			serviceQ6.surveyElement = surveyElementServiceQ6
			serviceQ6.save(failOnError: true, flush: true)

			services.addQuestion(serviceQ2)
			services.addQuestion(serviceQ1)
			services.addQuestion(serviceQ3)
			//			services.addQuestion(serviceQ4)
			services.addQuestion(serviceQ5)
			services.addQuestion(serviceQ6)
			services.save(failOnError:true)

			def ruleQ6 = new FormValidationRule(
				formElement: surveyElementServiceQ6,
				prefix: "[_].key1.key18",
				expression: "\$"+surveyElementServiceQ6.id+"[_].key1.key18 < 24",
				messages: j(["en":"Validation error {0,here}"]),
				dependencies: [surveyElementServiceQ6],
				typeCodeString: "District Hospital,Health Center",
				allowOutlier: false
			).save(failOnError: true)

			surveyElementServiceQ6.addValidationRule(ruleQ6)
			surveyElementServiceQ6.save(failOnError: true)

			def rule1 = new FormValidationRule(
				formElement: surveyElementServiceQ1,
				expression: "\$"+surveyElementServiceQ1.id+" > 100",
				messages: j(["en":"Validation error {0,here}"]),
				dependencies: [surveyElementServiceQ1],
				typeCodeString: "District Hospital,Health Center",
				allowOutlier: false
			).save(failOnError: true)
			def rule2 = new FormValidationRule(
				formElement: surveyElementServiceQ1,
				expression: "\$"+surveyElementServiceQ1.id+" > 140",
				messages: j(["en":"Validation error {0,here}"]),
				typeCodeString: "District Hospital,Health Center",
				dependencies: [surveyElementServiceQ1],
				allowOutlier: true
			).save(failOnError: true)
			surveyElementServiceQ1.addValidationRule(rule1)
			surveyElementServiceQ1.addValidationRule(rule2)
			surveyElementServiceQ1.save(failOnError: true)

			def openQ = new SurveySimpleQuestion(
				    code: "Question9",
					names: j(["en":"Sample Open Question Enter the cumulative number of training days spent on that module. To do so, add up all of the days spent by every person who participated in that module."]),
					descriptions: j(["en":"Help text"]),
					order: 1,

//					order: o(["en":1]),
					typeCodeString: "District Hospital,Health Center"
					)
			openResponse.addQuestion(openQ)
			openResponse.save(failOnError:true, flush: true)

			def surveyElementOpenQ = new SurveyElement(dataElement: RawDataElement.findByCode("CODE12"), surveyQuestion: openQ).save(failOnError: true)
			openQ.surveyElement = surveyElementOpenQ
			openQ.save(failOnError: true)

			def checkBoxQ = new SurveyCheckboxQuestion(
					code: "Question10",
					names: j(["en":"Service Section CheckBox Question"]),
					descriptions: j(["en":"Help text"]),
					order: 2,

//					order: o(["en":2]),
					typeCodeString: "District Hospital,Health Center"
					)
			staffing.addQuestion(checkBoxQ)
			staffing.save(failOnError:true, flush: true)

			def surveyElementChecboxQ1 = new SurveyElement(dataElement: RawDataElement.findByCode("CODE4"), surveyQuestion: checkBoxQ).save(failOnError: true)
			def surveyElementChecboxQ2 = new SurveyElement(dataElement: RawDataElement.findByCode("CODE5"), surveyQuestion: checkBoxQ).save(failOnError: true)
			def surveyElementChecboxQ3 = new SurveyElement(dataElement: RawDataElement.findByCode("CODE6"), surveyQuestion: checkBoxQ).save(failOnError: true)

			//Checkbox Option
			def option1 = new SurveyCheckboxOption(
					names: j(["en":"None Or Not Applicable"]),
					order: 2,
//					order: o(["en":2]),
					typeCodeString: "District Hospital,Health Center",
					surveyElement: surveyElementChecboxQ1
					)
			def option2 = new SurveyCheckboxOption(
					names: j(["en":"Second Option"]),
					order: 1,

//					order: o(["en":1]),
					typeCodeString: "District Hospital",
					surveyElement: surveyElementChecboxQ2
					)
			def option3 = new SurveyCheckboxOption(
					names: j(["en":"Third Option"]),
					order: 3,
//					order: o(["en":3]),
					typeCodeString: "District Hospital,Health Center",
					surveyElement: surveyElementChecboxQ3
					)
			checkBoxQ.addOption(option1)
			checkBoxQ.addOption(option2)
			checkBoxQ.addOption(option3)
			checkBoxQ.save(failOnError:true)


			def staffingQ1 = new SurveySimpleQuestion(
					code:"Question7",
					names: j(["en":"List all of your staff"]),
					descriptions: j(["en":"Help text"]),
					order: 10,
//					order: o(["en":10]),
					typeCodeString: "District Hospital,Health Center"
					)
			staffing.addQuestion(staffingQ1)
			staffing.save(failOnError:true, flush:true)

			def staffingElementQ1 = new SurveyElement(
					dataElement: RawDataElement.findByCode("LISTMAP1"),
					surveyQuestion: staffingQ1,
					headers: [
						"[_].key1":j(["en": "Family Name"]),
						"[_].key2":j(["en": "Given Name"]),
						"[_].key3":j(["en": "National ID Number"]),
						"[_].key4":j(["en": "Date of Birth"]),
						"[_].key5":j(["en": "Sex"]),
						"[_].key6":j(["en": "Nationality"]),
						"[_].key7":j(["en": "Education & Training"]),
						"[_].key7.key71":j(["en": "Highest level of education"]),
						"[_].key7.key72":j(["en": "Corresponding Institution"]),
						"[_].key7.key73":j(["en": "Days of Training received between July 2010 and June 2011, by Area"]),
						"[_].key7.key73.key731":j(["en": "Clinical"]),
						"[_].key7.key73.key731.key731_1":j(["en": "HIV/AIDS"]),
						"[_].key7.key73.key731.key731_2":j(["en": "Malaria"]),
						"[_].key7.key73.key731.key731_3":j(["en": "Tuberculosis"]),
						"[_].key7.key73.key731.key731_4":j(["en": "Diarrheal Diseases"]),
						"[_].key7.key73.key731.key731_5":j(["en": "Other Infections & Parasitic Diseases"]),
						"[_].key7.key73.key731.key731_6":j(["en": "Trauma & Burns"]),
						"[_].key7.key73.key731.key731_7":j(["en": "Mental Health"]),
						"[_].key7.key73.key731.key731_8":j(["en": "Environmental Health"]),
						"[_].key7.key73.key731.key731_9":j(["en": "Internal Medicine"]),
						"[_].key7.key73.key731.key731_10":j(["en": "Reproductive Health - Female"]),
						"[_].key7.key73.key731.key731_11":j(["en": "Reproductive Health - Male"]),
						"[_].key7.key73.key731.key731_12":j(["en": "Prenatal & Neonatal"]),
						"[_].key7.key73.key731.key731_13":j(["en": "Oral Health"]),
						"[_].key7.key73.key731.key731_14":j(["en": "Respiratory Health"]),
						"[_].key7.key73.key731.key731_15":j(["en": "Nutrition"]),
						"[_].key7.key73.key731.key731_16":j(["en": "Intestinal Health"]),
						"[_].key7.key73.key731.key731_17":j(["en": "Cardiovascular Health"]),
						"[_].key7.key73.key731.key731_18":j(["en": "Sexual Health (not HIV)"]),
						"[_].key7.key73.key731.key731_19":j(["en": "Other"]),
						"[_].key7.key73.key732":j(["en": "Non-Clinical"]),
						"[_].key7.key73.key732.key732_1":j(["en": "Human Resources Management"]),
						"[_].key7.key73.key732.key732_2":j(["en": "Facility Operations"]),
						"[_].key7.key73.key732.key732_3":j(["en": "Clinical Supervision & Management"]),
						"[_].key7.key73.key732.key732_4":j(["en": "Pharmacy Supervision & Management"]),
						"[_].key7.key73.key732.key732_5":j(["en": "CHW Supervision & Management"]),
						"[_].key7.key73.key732.key732_6":j(["en": "Laboratory Supervision & Management"]),
						"[_].key7.key73.key732.key732_7":j(["en": "Administrative Procedures & Management"]),
						"[_].key7.key73.key732.key732_8":j(["en": "Mutuelle Procedures & Protocol"]),
						"[_].key7.key73.key732.key732_9":j(["en": "Claims Processing"]),
						"[_].key7.key73.key732.key732_10":j(["en": "Performance Based Financing"]),
						"[_].key7.key73.key732.key732_11":j(["en": "Health Economics"]),
						"[_].key7.key73.key732.key732_12":j(["en": "Health Insurance"]),
						"[_].key7.key73.key732.key732_13":j(["en": "ICT"]),
						"[_].key7.key73.key732.key732_14":j(["en": "Basic Statistics / Analytics"]),
						"[_].key7.key73.key732.key732_15":j(["en": "Presentation Techniques"]),
						"[_].key7.key73.key732.key732_16":j(["en": "Communication Skills"]),
						"[_].key8":j(["en": "Work History"]),
						"[_].key8.key81":j(["en": "Primary Function"]),
						"[_].key8.key82":j(["en": "Primary Department"]),
						"[_].key8.key83":j(["en": "% of Time Spent on Primary Department"]),
						"[_].key8.key84":j(["en": "Departments Served 1+ Day in a typical Week"]),
						"[_].key8.key84.key84_1":j(["en": "Administration"]),
						"[_].key8.key84.key84_2":j(["en": "Chronic Disease"]),
						"[_].key8.key84.key84_3":j(["en": "Community Health"]),
						"[_].key8.key84.key84_4":j(["en": "Dentistry"]),
						"[_].key8.key84.key84_5":j(["en": "Emergency"]),
						"[_].key8.key84.key84_6":j(["en": "Family Planning"]),
						"[_].key8.key84.key84_7":j(["en": "General Consultation"]),
						"[_].key8.key84.key84_8":j(["en": "HIV/AIDS"]),
						"[_].key8.key84.key84_9":j(["en": "Inpatient"]),
						"[_].key8.key84.key84_10":j(["en": "Intensive Care"]),
						"[_].key8.key84.key84_11":j(["en": "Internal Medicine"]),
						"[_].key8.key84.key84_12":j(["en": "Laboratory"]),
						"[_].key8.key84.key84_13":j(["en": "Maternity"]),
						"[_].key8.key84.key84_14":j(["en": "Mental Health"]),
						"[_].key8.key84.key84_15":j(["en": "Mutuelle"]),
						"[_].key8.key84.key84_16":j(["en": "Nutrition"]),
						"[_].key8.key84.key84_17":j(["en": "Ophthalmology"]),
						"[_].key8.key84.key84_18":j(["en": "Pediatrics"]),
						"[_].key8.key84.key84_19":j(["en": "Pharmacy"]),
						"[_].key8.key84.key84_20":j(["en": "Reception"]),
						"[_].key8.key84.key84_21":j(["en": "Supporting Departments (Laundry, etc.)"]),
						"[_].key8.key84.key84_22":j(["en": "Surgery"]),
						"[_].key8.key84.key84_23":j(["en": "Tuberculosis"]),
						"[_].key8.key84.key84_24":j(["en": "Vaccination"]),
						"[_].key8.key85":j(["en": "Began Employment at this Facility"]),
						"[_].key8.key86":j(["en": "Ended Employment at this Facility"]),
						"[_].key9":j(["en": "Compensation"]),
						"[_].key9.key91":j(["en": "Total Financial Compensation between July 2010 and June 2011 from the following"]),
						"[_].key9.key91.key911":j(["en": "Facility"]),
						"[_].key9.key91.key912":j(["en": "PBF"]),
						"[_].key9.key91.key913":j(["en": "Non-Government Partner"]),
						"[_].key9.key91.key914":j(["en": "Other"]),
						"[_].key9.key92":j(["en": "Non-monetary Compensation received"]),
						"[_].key9.key92.key921":j(["en": "Housing"]),
						"[_].key9.key92.key922":j(["en": "Transportation"]),
						"[_].key9.key92.key923":j(["en": "Mobile Credit"]),
						"[_].key9.key92.key924":j(["en": "Fuel Credit"])
					]).save(failOnError: true)
			staffingQ1.surveyElement = staffingElementQ1
			staffingQ1.save(failOnError: true)

			//Adding a table type question
			def tableQ = new SurveyTableQuestion(
					code:"Question8",
					names: j(["en":"For each training module:"]),
					descriptions: j(["en":"(a) Enter the total number of staff members that received training in this subject from July 2009 - June 2010, regardless of how many days' training they received.<br/>(b) Enter the cumulative number of training days spent on that module. To do so, add up all of the days spent by every person who participated in that module. "]),
					tableNames: j(["en":"Training Modules"]),
					order: 1,

//					order: o(["en":1]),
					typeCodeString: "Health Center,District Hospital"
					)
			staffing.addQuestion(tableQ)
			staffing.save(failOnError:true, flush: true)

			//Add columns
			def tabColumnOne = new SurveyTableColumn(
					names: j(["en":"Number Who Attended Training"]),
					order: 1,

//					order: o(["en":1]),
					typeCodeString: "District Hospital,Health Center",
					question: tableQ
					)
			def tabColumnTwo = new SurveyTableColumn(
					names: j(["en":"Sum Total Number of Days"]),
					order: 2,

//					order: o(["en":2]),
					typeCodeString: "District Hospital,Health Center",
					question: tableQ
					)
			def tabColumnThree = new SurveyTableColumn(
					names: j(["en":"Who Provided the Training"]),
					order: 3,

//					order: o(["en":3]),
					typeCodeString: "Health Center",
					question: tableQ
					)
			def tabColumnFour = new SurveyTableColumn(
					names: j(["en":"Due Date"]),
					order: 4,

//					order: o(["en":4]),
					typeCodeString: "District Hospital",
					question: tableQ
					)

			tableQ.addColumn(tabColumnThree)
			tableQ.addColumn(tabColumnTwo)
			tableQ.addColumn(tabColumnOne)
			tableQ.addColumn(tabColumnFour)

			Map<SurveyTableColumn,SurveyElement> dataElmntsLine1= new LinkedHashMap<SurveyTableColumn,SurveyElement>();

			def surveyElementTable1 = new SurveyElement(dataElement: RawDataElement.findByCode("CODE8"), surveyQuestion: tableQ).save(failOnError: true)
			def surveyElementTable2 = new SurveyElement(dataElement: RawDataElement.findByCode("CODE9"), surveyQuestion: tableQ).save(failOnError: true)
			def surveyElementTable3 = new SurveyElement(dataElement: RawDataElement.findByCode("CODE10"), surveyQuestion: tableQ).save(failOnError: true)
			def surveyElementTable4 = new SurveyElement(dataElement: RawDataElement.findByCode("CODE11"), surveyQuestion: tableQ).save(failOnError: true)
			dataElmntsLine1.put(tabColumnOne, surveyElementTable1)
			dataElmntsLine1.put(tabColumnTwo, surveyElementTable2)
			dataElmntsLine1.put(tabColumnThree, surveyElementTable3)
			dataElmntsLine1.put(tabColumnFour, surveyElementTable4)

			def ruleTable1 = new FormValidationRule(
				formElement: surveyElementTable1,
				expression: "\$"+surveyElementTable1.id+" < 100",
				messages: j(["en":"Validation error {0,here}"]),
				dependencies: [surveyElementTable1],
				typeCodeString: "District Hospital,Health Center",
				allowOutlier: false
			).save(failOnError: true)

			surveyElementTable1.addValidationRule(ruleTable1)
			surveyElementTable1.save(failOnError: true)

			Map<SurveyTableColumn,SurveyElement> dataElmntsLine2= new LinkedHashMap<SurveyTableColumn,SurveyElement>();

			def surveyElementTable21 = new SurveyElement(dataElement: RawDataElement.findByCode("CODE81"), surveyQuestion: tableQ).save(failOnError: true)
			def surveyElementTable22 = new SurveyElement(dataElement: RawDataElement.findByCode("CODE91"), surveyQuestion: tableQ).save(failOnError: true)
			def surveyElementTable23 = new SurveyElement(dataElement: RawDataElement.findByCode("CODE101"), surveyQuestion: tableQ).save(failOnError: true)
			def surveyElementTable24 = new SurveyElement(dataElement: RawDataElement.findByCode("CODE111"), surveyQuestion: tableQ).save(failOnError: true)

			dataElmntsLine2.put(tabColumnOne, surveyElementTable21)
			dataElmntsLine2.put(tabColumnTwo, surveyElementTable22)
			dataElmntsLine2.put(tabColumnThree, surveyElementTable23)
			dataElmntsLine2.put(tabColumnFour, surveyElementTable24)

			//Add rows
			def tabRowOne = new SurveyTableRow(
					names: j(["en":"Clinical Pharmacy :"]),
					order: 1,
					//					order: o(["en":1]),
					question: tableQ,
					typeCodeString: "District Hospital,Health Center",
					surveyElements: dataElmntsLine1
					)
			def tabRowTwo = new SurveyTableRow(
					names: j(["en":"Clinical Nurse Training :"]),
					order: 2,
					//					order: o(["en":2]),
					question: tableQ,
					typeCodeString: "Health Center",
					surveyElements: dataElmntsLine2
					)

			tableQ.addRow(tabRowOne)
			tableQ.addRow(tabRowTwo)
			tableQ.save(failOnError:true)

			def ruleCheckbox = new FormValidationRule(
				formElement: surveyElementChecboxQ3,
				expression: "if(\$"+surveyElementTable21.id+" < 100) \$"+surveyElementChecboxQ3.id+" else true",
				messages: j(["en":"Validation error {0,here}"]),
				dependencies: [surveyElementTable21],
				typeCodeString: "District Hospital,Health Center",
				allowOutlier: false
			).save(failOnError: true)

			surveyElementChecboxQ3.addValidationRule(ruleCheckbox)
			surveyElementChecboxQ3.save(failOnError: true)

			def skipRule1 = new SurveySkipRule(survey: surveyOne, expression: "1==1", skippedFormElements: [(surveyElementTable2): ""]);
			def skipRule2 = new SurveySkipRule(survey: surveyOne, expression: "\$"+surveyElementTable1.id+"==1", skippedFormElements: [(surveyElementTable22): "", (surveyElementTable3): ""]);
			def skipRule3 = new SurveySkipRule(survey: surveyOne, expression: "\$"+surveyElementTable1.id+"==2", skippedSurveyQuestions: [checkBoxQ]);
			def skipRule4 = new SurveySkipRule(survey: surveyOne, expression: "\$"+surveyElementPatientQ1.id+"==1000", skippedSurveyQuestions: [tableQ], skippedFormElements: [(surveyElementChecboxQ1): ""]);
			def skipRule5 = new SurveySkipRule(survey: surveyOne, expression: "\$"+surveyElementServiceQ6.id+"[_].key0.key01=='value1'", skippedSurveyQuestions: [], skippedFormElements: [(surveyElementServiceQ6): "[_].key0.key02"]);

			surveyOne.addSkipRule(skipRule1)
			surveyOne.addSkipRule(skipRule2)
			surveyOne.addSkipRule(skipRule3)
			surveyOne.addSkipRule(skipRule4)
			surveyOne.addSkipRule(skipRule5)

			surveyOne.save()
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

	public static ExpressionMap e(def map) {
		return new ExpressionMap(jsonText: JSONUtils.getJSONFromMap(map))
	}

	public static Value v(def value) {
		return new Value("{\"value\":"+value+"}");
	}
	
	public static Translation j(def map) {
		return new Translation(jsonText: JSONUtils.getJSONFromMap(map));
	}

	public static Ordering o(def map) {
		return new Ordering(jsonText: JSONUtils.getJSONFromMap(map));
	}
}
