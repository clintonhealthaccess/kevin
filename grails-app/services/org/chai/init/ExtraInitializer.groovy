package org.chai.init


class ExtraInitializer {

//	static def createDataElementsAndExpressions() {
//		
//		if (!RawDataElement.count()) {
//			// Data Elements
//			def dataElement10 = new RawDataElement(names:j(["en":"Element 10"]), descriptions:j([:]), code:"CODE10", type: Type.TYPE_ENUM (Enum.findByCode('ENUM2').code))
//			def dataElement1 = new RawDataElement(names:j(["en":"Element 1"]), descriptions:j([:]), code:"CODE1", type: Type.TYPE_NUMBER(), source: Source.findByCode('dhsst'))
//			def dataElement2 = new RawDataElement(names:j(["en":"Element 2"]), descriptions:j([:]), code:"CODE2", type: Type.TYPE_NUMBER())
//			def dataElement3 = new RawDataElement(names:j(["en":"Element 3"]), descriptions:j([:]), code:"CODE3", type: Type.TYPE_ENUM (Enum.findByCode('ENUM1').code))
//			def dataElement4 = new RawDataElement(names:j(["en":"Element 4"]), descriptions:j([:]), code:"CODE4", type: Type.TYPE_BOOL())
//			def dataElement5 = new RawDataElement(names:j(["en":"Element 5"]), descriptions:j([:]), code:"CODE5", type: Type.TYPE_BOOL())
//			def dataElement6 = new RawDataElement(names:j(["en":"Element 6"]), descriptions:j([:]), code:"CODE6", type: Type.TYPE_BOOL())
//			def dataElement7 = new RawDataElement(names:j(["en":"Element 7"]), descriptions:j([:]), code:"CODE7", type: Type.TYPE_BOOL())
//			def dataElement8 = new RawDataElement(names:j(["en":"Element 8"]), descriptions:j([:]), code:"CODE8", type: Type.TYPE_NUMBER())
//			def dataElement9 = new RawDataElement(names:j(["en":"Element 9"]), descriptions:j([:]), code:"CODE9", type: Type.TYPE_NUMBER())
//			def dataElement11 = new RawDataElement(names:j(["en":"Element 11"]), descriptions:j([:]), code:"CODE11", type: Type.TYPE_DATE())
//			def dataElement12 = new RawDataElement(names:j(["en":"Element 12"]), descriptions:j([:]), code:"CODE12", type: Type.TYPE_TEXT())
//			def dataElement81 = new RawDataElement(names:j(["en":"Element 81"]), descriptions:j([:]), code:"CODE81", type: Type.TYPE_NUMBER())
//			def dataElement91 = new RawDataElement(names:j(["en":"Element 91"]), descriptions:j([:]), code:"CODE91", type: Type.TYPE_NUMBER())
//			def dataElement101 = new RawDataElement(names:j(["en":"Element 101"]), descriptions:j([:]), code:"CODE101", type: Type.TYPE_ENUM (Enum.findByCode('ENUM2').code))
//			def dataElement111 = new RawDataElement(names:j(["en":"Element 111"]), descriptions:j([:]), code:"CODE111", type: Type.TYPE_DATE())
//
//			def dataElementList = new RawDataElement(names:j(["en":"Element 111"]), descriptions:j([:]), code:"LIST1", type:
//					Type.TYPE_LIST(
//					Type.TYPE_MAP([
//						"key0": Type.TYPE_STRING(),
//						"key1": Type.TYPE_MAP([
//							"key11": Type.TYPE_MAP([
//								"key111": Type.TYPE_NUMBER()
//							])
//						])
//					])
//
//				)
//			)
//			def dataElementMap = new RawDataElement(names:j(["en":"Element 111"]), descriptions:j([:]), code:"MAP1",
//				type: Type.TYPE_MAP([
//					"key1": Type.TYPE_MAP([
//						"key11": Type.TYPE_MAP([
//							"key111": Type.TYPE_NUMBER()
//						])
//					])
//				])
//			)
//			
//			def planningElement = new RawDataElement(names:j(["en":"Element Planning"]), descriptions:j([:]), code:"PLANNINGELEMENT",
//				type: Type.TYPE_LIST(Type.TYPE_MAP([
//					"basic": Type.TYPE_MAP([
//						"description": Type.TYPE_STRING(),
//						"activity": Type.TYPE_ENUM(Enum.findByCode('ENUM1').code),
//						"area": Type.TYPE_ENUM(Enum.findByCode('ENUM1').code),
//						"instances": Type.TYPE_NUMBER(),
//						"responsible": Type.TYPE_STRING(),
//						"new_structure": Type.TYPE_BOOL(),
//						"test_test": Type.TYPE_LIST(Type.TYPE_NUMBER()),
//					]),
//					"staffing": Type.TYPE_MAP([
//						"nurse": Type.TYPE_MAP([
//							"nurse_time": Type.TYPE_NUMBER(),
//							"nurse_level": Type.TYPE_ENUM(Enum.findByCode('ENUM1').code)
//						], true),
//						"doctor": Type.TYPE_MAP([
//							"doctor_time": Type.TYPE_NUMBER(),
//							"doctor_level": Type.TYPE_ENUM(Enum.findByCode('ENUM1').code)
//						], true),
//						"other": Type.TYPE_MAP([
//							"other_time": Type.TYPE_NUMBER(),
//							"other_type": Type.TYPE_ENUM(Enum.findByCode('ENUM1').code)
//						], true)
//					]),
//					"consumables": Type.TYPE_MAP([
//						"tests": Type.TYPE_MAP([
//							"blood_sugar": Type.TYPE_NUMBER(),
//							"hiv": Type.TYPE_NUMBER()
//						]),
//						"medicine": Type.TYPE_MAP([
//							"arv": Type.TYPE_NUMBER(),
//							"tb": Type.TYPE_NUMBER(),
//							"malaria": Type.TYPE_NUMBER()
//						]),
//						"other": Type.TYPE_LIST(
//							Type.TYPE_MAP([
//								"type": Type.TYPE_ENUM(Enum.findByCode('ENUM1').code),
//								"number": Type.TYPE_NUMBER()
//							])
//						)
//					]),
//					"monthly_breakdown": Type.TYPE_MAP([
//						"january": Type.TYPE_NUMBER(),
//						"february": Type.TYPE_NUMBER(),
//						"march": Type.TYPE_NUMBER(),
//						"april": Type.TYPE_NUMBER(),
//						"mai": Type.TYPE_NUMBER(),
//						"june": Type.TYPE_NUMBER(),
//						"july": Type.TYPE_NUMBER(),
//						"august": Type.TYPE_NUMBER(),
//						"september": Type.TYPE_NUMBER(),
//						"october": Type.TYPE_NUMBER(),
//						"november": Type.TYPE_NUMBER(),
//						"december": Type.TYPE_NUMBER()
//					]),
//					"funding_sources": Type.TYPE_MAP([
//						"general_fund": Type.TYPE_BOOL(),
//						"sources": Type.TYPE_MAP([
//							"location": Type.TYPE_NUMBER(),
//							"minisante": Type.TYPE_NUMBER(),
//							"hospital": Type.TYPE_NUMBER(),
//							"gfatm": Type.TYPE_NUMBER(),
//							"other": Type.TYPE_NUMBER()
//						])
//					])
//				]))
//			)
//			
//			def siyelo2 = new RawDataElement(names:j(["en":"Element 111"]), descriptions:j([:]), code:"LISTMAP2",
//					type: Type.TYPE_LIST(
//					Type.TYPE_MAP([
//						"key0": Type.TYPE_MAP([
//							"key01": Type.TYPE_STRING(),
//							"key02": Type.TYPE_STRING()
//						], true),
//						"key1": Type.TYPE_MAP([
//							"key11": Type.TYPE_MAP([
//								"key111": Type.TYPE_ENUM(Enum.findByCode('ENUM1').code),
//								"key112": Type.TYPE_STRING()
//							], true),
//							"key12": Type.TYPE_STRING(),
//							"key13": Type.TYPE_MAP([
//								"key131": Type.TYPE_ENUM(Enum.findByCode('ENUM1').code),
//								"key132": Type.TYPE_STRING()
//							], true),
//							"key14": Type.TYPE_STRING(),
//							"key15": Type.TYPE_STRING(),
//							"key16": Type.TYPE_MAP([
//								"key161": Type.TYPE_ENUM(Enum.findByCode('ENUM1').code),
//								"key162": Type.TYPE_STRING()
//							], true),
//							"key17": Type.TYPE_ENUM(Enum.findByCode('ENUM1').code),
//							"key18": Type.TYPE_NUMBER()
//						]),
//						"key2": Type.TYPE_MAP([
//							"key21": Type.TYPE_STRING(),
//							"key22": Type.TYPE_ENUM(Enum.findByCode('ENUM1').code),
//							"key23": Type.TYPE_STRING(),
//							"key24": Type.TYPE_DATE(),
//							"key25": Type.TYPE_STRING(),
//							"key26": Type.TYPE_ENUM(Enum.findByCode('ENUM1').code),
//							"key27": Type.TYPE_STRING(),
//							"key28": Type.TYPE_DATE(),
//							"key29": Type.TYPE_DATE()
//						])
//					])
//					)
//					)
//
//			def siyelo1 = new RawDataElement(names:j(["en":"Element 111"]), descriptions:j([:]), code:"LISTMAP1",
//					type: Type.TYPE_LIST(
//					Type.TYPE_MAP([
//						"key1": Type.TYPE_STRING(),
//						"key2": Type.TYPE_STRING(),
//						"key3": Type.TYPE_STRING(),
//						"key4": Type.TYPE_DATE(),
//						"key5": Type.TYPE_ENUM(Enum.findByCode('ENUM1').code),
//						"key6": Type.TYPE_STRING(),
//						"key7": Type.TYPE_MAP([
//							"key71": Type.TYPE_STRING(),
//							"key72": Type.TYPE_STRING(),
//							"key73": Type.TYPE_MAP([
//								"key731": Type.TYPE_MAP([
//									"key731_1": Type.TYPE_NUMBER(),
//									"key731_2": Type.TYPE_NUMBER(),
//									"key731_3": Type.TYPE_NUMBER(),
//									"key731_4": Type.TYPE_NUMBER(),
//									"key731_5": Type.TYPE_NUMBER(),
//									"key731_6": Type.TYPE_NUMBER(),
//									"key731_7": Type.TYPE_NUMBER(),
//									"key731_8": Type.TYPE_NUMBER(),
//									"key731_9": Type.TYPE_NUMBER(),
//									"key731_10": Type.TYPE_NUMBER(),
//									"key731_11": Type.TYPE_NUMBER(),
//									"key731_12": Type.TYPE_NUMBER(),
//									"key731_13": Type.TYPE_NUMBER(),
//									"key731_14": Type.TYPE_NUMBER(),
//									"key731_15": Type.TYPE_NUMBER(),
//									"key731_16": Type.TYPE_NUMBER(),
//									"key731_17": Type.TYPE_NUMBER(),
//									"key731_18": Type.TYPE_NUMBER(),
//									"key731_19": Type.TYPE_NUMBER()
//								]),
//								"key732": Type.TYPE_MAP([
//									"key732_1": Type.TYPE_NUMBER(),
//									"key732_2": Type.TYPE_NUMBER(),
//									"key732_3": Type.TYPE_NUMBER(),
//									"key732_4": Type.TYPE_NUMBER(),
//									"key732_5": Type.TYPE_NUMBER(),
//									"key732_6": Type.TYPE_NUMBER(),
//									"key732_7": Type.TYPE_NUMBER(),
//									"key732_8": Type.TYPE_NUMBER(),
//									"key732_9": Type.TYPE_NUMBER(),
//									"key732_10": Type.TYPE_NUMBER(),
//									"key732_11": Type.TYPE_NUMBER(),
//									"key732_12": Type.TYPE_NUMBER(),
//									"key732_13": Type.TYPE_NUMBER(),
//									"key732_14": Type.TYPE_NUMBER(),
//									"key732_15": Type.TYPE_NUMBER(),
//									"key732_16": Type.TYPE_NUMBER()
//								])
//							]),
//						]),
//						"key8": Type.TYPE_MAP([
//							"key81": Type.TYPE_STRING(),
//							"key82": Type.TYPE_STRING(),
//							"key83": Type.TYPE_NUMBER(),
//							"key84": Type.TYPE_MAP([
//								"key84_1": Type.TYPE_BOOL(),
//								"key84_2": Type.TYPE_BOOL(),
//								"key84_3": Type.TYPE_BOOL(),
//								"key84_4": Type.TYPE_BOOL(),
//								"key84_5": Type.TYPE_BOOL(),
//								"key84_6": Type.TYPE_BOOL(),
//								"key84_7": Type.TYPE_BOOL(),
//								"key84_8": Type.TYPE_BOOL(),
//								"key84_9": Type.TYPE_BOOL(),
//								"key84_10": Type.TYPE_BOOL(),
//								"key84_11": Type.TYPE_BOOL(),
//								"key84_12": Type.TYPE_BOOL(),
//								"key84_13": Type.TYPE_BOOL(),
//								"key84_14": Type.TYPE_BOOL(),
//								"key84_15": Type.TYPE_BOOL(),
//								"key84_16": Type.TYPE_BOOL(),
//								"key84_17": Type.TYPE_BOOL(),
//								"key84_18": Type.TYPE_BOOL(),
//								"key84_19": Type.TYPE_BOOL(),
//								"key84_20": Type.TYPE_BOOL(),
//								"key84_21": Type.TYPE_BOOL(),
//								"key84_22": Type.TYPE_BOOL(),
//								"key84_23": Type.TYPE_BOOL(),
//								"key84_24": Type.TYPE_BOOL()
//							]),
//							"key85": Type.TYPE_DATE(),
//							"key86": Type.TYPE_DATE(),
//						]),
//						"key9": Type.TYPE_MAP([
//							"key91": Type.TYPE_MAP([
//								"key911": Type.TYPE_NUMBER(),
//								"key912": Type.TYPE_NUMBER(),
//								"key913": Type.TYPE_NUMBER(),
//								"key914": Type.TYPE_NUMBER()
//							]),
//							"key92": Type.TYPE_MAP([
//								"key921": Type.TYPE_BOOL(),
//								"key922": Type.TYPE_BOOL(),
//								"key923": Type.TYPE_BOOL(),
//								"key924": Type.TYPE_BOOL()
//							])
//						])
//					]))
//					)
//
//			def siyelo3 = new RawDataElement(names:j(["en":"Importer","fr":"Importer"]), descriptions:j([:]), code:"LISTMAP3",
//					type: Type.TYPE_LIST(
//					Type.TYPE_MAP([
//						"family_name": Type.TYPE_STRING(),
//						"given_name": Type.TYPE_STRING(),
//						"birth_date": Type.TYPE_DATE(),
//						"personal_information": Type.TYPE_MAP([
//							"sex": Type.TYPE_ENUM("gender"),
//							"nationality": Type.TYPE_STRING(),
//							"age": Type.TYPE_NUMBER()
//						]),
//						"work_history": Type.TYPE_MAP([
//							"primary_function": Type.TYPE_ENUM("primaryfunction"),
//							"departments_served": Type.TYPE_MAP([
//								"administration": Type.TYPE_BOOL()
//							])
//						])
//					])
//					)
//					)
//
//			dataElement10.save(failOnError: true, flush:true)
//			dataElement1.save(failOnError: true, flush: true)
//			dataElement2.save(failOnError: true, flush: true)
//			dataElement3.save(failOnError: true, flush:true)
//			dataElement4.save(failOnError: true, flush:true)
//			dataElement5.save(failOnError: true, flush:true)
//			dataElement6.save(failOnError: true, flush:true)
//			dataElement7.save(failOnError: true, flush:true)
//			dataElement8.save(failOnError: true, flush:true)
//			dataElement9.save(failOnError: true, flush:true)
//			dataElement11.save(failOnError: true, flush:true)
//			dataElement81.save(failOnError: true, flush:true)
//			dataElement91.save(failOnError: true, flush:true)
//			dataElement101.save(failOnError: true, flush:true)
//			dataElement111.save(failOnError: true, flush:true)
//			dataElement12.save(failOnError: true, flush:true)
//
//			dataElementList.save(failOnError: true, flush:true)
//			dataElementMap.save(failOnError: true, flush:true)
//			siyelo1.save(failOnError: true, flush:true)
//			siyelo2.save(failOnError: true, flush:true)
//			siyelo3.save(failOnError: true, flush:true)
//			planningElement.save(failOnError: true, flush:true)
//			
//			// data value
//			new RawDataElementValue(
//					data: RawDataElement.findByCode("CODE1"),
//					period: Period.list([cache: true])[0],
//					location: DataLocation.findByCode("322"),
//					value: v("30"),
//					timestamp: new Date(),
//					).save(failOnError: true)
//			// data value
//			new RawDataElementValue(
//					data: RawDataElement.findByCode("CODE1"),
//					period: Period.list([cache: true])[0],
//					location: DataLocation.findByCode("327"),
//					value: v("40"),
//					timestamp: new Date(),
//					).save(failOnError: true)
//			// data value
//			new RawDataElementValue(
//					data: RawDataElement.findByCode("CODE3"),
//					period: Period.list([cache: true])[0],
//					location: DataLocation.findByCode("327"),
//					value: v("\"value1\""),
//					timestamp: new Date(),
//					).save(failOnError: true, flush:true)
//			new RawDataElementValue(
//					data: RawDataElement.findByCode("CODE4"),
//					period: Period.list([cache: true])[0],
//					location: DataLocation.findByCode("327"),
//					value: v("true"),
//					timestamp: new Date(),
//					).save(failOnError: true, flush:true)
//			new RawDataElementValue(
//					data: RawDataElement.findByCode("CODE6"),
//					period: Period.list([cache: true])[0],
//					location: DataLocation.findByCode("327"),
//					value: v("false"),
//					timestamp: new Date(),
//					).save(failOnError: true, flush:true)
//			new RawDataElementValue(
//					data: RawDataElement.findByCode("CODE8"),
//					period: Period.list([cache: true])[0],
//					location: DataLocation.findByCode("327"),
//					value: v("10"),
//					timestamp: new Date(),
//					).save(failOnError: true, flush:true)
//			new RawDataElementValue(
//					data: RawDataElement.findByCode("CODE9"),
//					period: Period.list([cache: true])[0],
//					location: DataLocation.findByCode("327"),
//					value: v("31"),
//					timestamp: new Date(),
//					).save(failOnError: true, flush:true)
//			new RawDataElementValue(
//					data: RawDataElement.findByCode("CODE10"),
//					period: Period.list([cache: true])[0],
//					location: DataLocation.findByCode("327"),
//					value: v("\"NGO or Partner\""),
//					timestamp: new Date(),
//					).save(failOnError: true, flush:true)
//			new RawDataElementValue(
//					data: RawDataElement.findByCode("CODE11"),
//					period: Period.list([cache: true])[0],
//					location: DataLocation.findByCode("327"),
//					value: v("\"2011-06-29\""),
//					timestamp: new Date(),
//					).save(failOnError: true, flush:true)
//			new RawDataElementValue(
//					data: RawDataElement.findByCode("CODE81"),
//					period: Period.list([cache: true])[0],
//					location: DataLocation.findByCode("327"),
//					value: v("44"),
//					timestamp: new Date(),
//					).save(failOnError: true, flush:true)
//			new RawDataElementValue(
//					data: RawDataElement.findByCode("CODE91"),
//					period: Period.list([cache: true])[0],
//					location: DataLocation.findByCode("327"),
//					value: v("33"),
//					timestamp: new Date(),
//					).save(failOnError: true, flush:true)
//			new RawDataElementValue(
//					data: RawDataElement.findByCode("CODE101"),
//					period: Period.list([cache: true])[0],
//					location: DataLocation.findByCode("327"),
//					value: v("\"Ministry of Health\""),
//					timestamp: new Date(),
//					).save(failOnError: true, flush:true)
//			new RawDataElementValue(
//					data: RawDataElement.findByCode("CODE111"),
//					period: Period.list([cache: true])[0],
//					location: DataLocation.findByCode("327"),
//					value: v("\"2011-06-30\""),
//					timestamp: new Date(),
//					).save(failOnError: true, flush:true)
//
//			new RawDataElementValue(
//					data: RawDataElement.findByCode("CODE12"),
//					period: Period.list([cache: true])[0],
//					location: DataLocation.findByCode("327"),
//					value: v("\"I can not get into the Settings menu at all, when the phone is unlocked there is a blank screen.\""),
//					timestamp: new Date(),
//					).save(failOnError: true, flush:true)
//					
//			new RawDataElementValue(
//					data: RawDataElement.findByCode("LISTMAP2"),
//					period: Period.list([cache: true])[0],
//					location: DataLocation.findByCode("327"),
//					value: Value.VALUE_LIST([Value.VALUE_MAP(['key0': Value.VALUE_MAP(['key01': Value.VALUE_STRING('text')])])]),
//					timestamp: new Date(),
//					).save(failOnError: true, flush:true)
//		}
//
//
//		if (!NormalizedDataElement.count()) {
//			def period1 = Period.list([cache: true])[0]
//			def dh = DataLocationType.findByCode('District Hospital')
//			def hc = DataLocationType.findByCode('Health Center')
//
//			// indicators
//			new NormalizedDataElement(names:j(["en":"Constant 10"]), descriptions:j([:]), code:"Constant 10", expressionMap: e([(period1.id+''):[(hc.code):"10", (dh.code):"10"]]), type: Type.TYPE_NUMBER(), timestamp:new Date()).save(failOnError: true, flush: true)
//			new NormalizedDataElement(names:j(["en":"Constant 20"]), descriptions:j([:]), code:"Constant 20", expressionMap: e([(period1.id+''):[(hc.code):"20", (dh.code):"20"]]), type: Type.TYPE_NUMBER(), timestamp:new Date()).save(failOnError: true, flush: true)
//			new NormalizedDataElement(names:j(["en":"Constant 30"]), descriptions:j([:]), code:"Constant 30", expressionMap: e([(period1.id+''):[(hc.code):"30", (dh.code):"30"]]), type: Type.TYPE_NUMBER(), timestamp:new Date()).save(failOnError: true, flush: true)
//			new NormalizedDataElement(names:j(["en":"Constant 40"]), descriptions:j([:]), code:"Constant 40", expressionMap: e([(period1.id+''):[(hc.code):"40", (dh.code):"40"]]), type: Type.TYPE_NUMBER(), timestamp:new Date()).save(failOnError: true, flush: true)
//			
//			def rd1 = RawDataElement.findByCode("CODE1").id
//			new NormalizedDataElement(names:j(["en":"Element 1"]), descriptions:j([:]),
//				code:"Element 1",
//				expressionMap: e([(period1.id+''):[(hc.code):"\$"+rd1+"+\$"+rd1, (dh.code):"\$"+rd1+"+\$"+rd1]]),
//				type: Type.TYPE_NUMBER(),
//				timestamp:new Date()).save(failOnError: true, flush: true)
//				
//			def rd2 = RawDataElement.findByCode("CODE2").id
//			new NormalizedDataElement(names:j(["en":"Element 2"]), descriptions:j([:]),
//				code:"Element 2",
//				expressionMap: e([(period1.id+''):[(hc.code):"\$"+rd2, (dh.code):"\$"+rd2]]),
//				type: Type.TYPE_NUMBER(),
//				timestamp:new Date()).save(failOnError: true, flush: true)
//				
//			def rd3 = RawDataElement.findByCode("CODE3").id
//			new NormalizedDataElement(names:j(["en":"Element 3"]), descriptions:j([:]),
//				code:"Element 3",
//				expressionMap: e([(period1.id+''):[(hc.code):"\$"+rd3, (dh.code):"\$"+rd3]]),
//				type: Type.TYPE_NUMBER(),
//				timestamp:new Date()).save(failOnError: true, flush: true)
//				
//			new NormalizedDataElement(names:j(["en":"TRUE"]), descriptions:j([:]), code:"TRUE", expressionMap: e([(period1.id+''):[(hc.code):"true", (dh.code):"true"]]), type: Type.TYPE_BOOL(), timestamp:new Date()).save(failOnError: true, flush: true)
//			new NormalizedDataElement(names:j(["en":"FALSE"]), descriptions:j([:]), code:"FALSE", expressionMap: e([(period1.id+''):[(hc.code):"false", (dh.code):"false"]]), type: Type.TYPE_BOOL(), timestamp:new Date()).save(failOnError: true, flush: true)
//			
//			new NormalizedDataElement(names:j(["en":"ZERO"]), descriptions:j([:]), code:"ZERO", expressionMap: e([(period1.id+''):[(hc.code):"0", (dh.code):"0"]]), type: Type.TYPE_NUMBER(), timestamp:new Date()).save(failOnError: true, flush: true)
//			new NormalizedDataElement(names:j(["en":"ONE"]), descriptions:j([:]), code:"ONE", expressionMap: e([(period1.id+''):[(hc.code):"1", (dh.code):"1"]]), type: Type.TYPE_NUMBER(), timestamp:new Date()).save(failOnError: true, flush: true)
//			new NormalizedDataElement(names:j(["en":"MixHC"]), descriptions:j([:]), code:"MixHC", expressionMap: e([(period1.id+''):[(hc.code):"1", (dh.code):"0"]]), type: Type.TYPE_NUMBER(), timestamp:new Date()).save(failOnError: true, flush: true)
//			new NormalizedDataElement(names:j(["en":"MixDH"]), descriptions:j([:]), code:"MixDH", expressionMap: e([(period1.id+''):[(hc.code):"0", (dh.code):"1"]]), type: Type.TYPE_NUMBER(), timestamp:new Date()).save(failOnError: true, flush: true)
//		}
//	}
//
//	static def createDashboard() {
//		if (!DashboardProgram.count()) {
//
//			def root = ReportProgram.findByCode('Strategic Programs');
//			def hrh = ReportProgram.findByCode('Human Resources for Health');
//			def staffing = ReportProgram.findByCode('Staffing');
//
//			def dashboardRoot = new DashboardProgram(names:j(["en":"Strategic Programs"]), weight: 0, code:"Strategic Programs", program: root)
//			dashboardRoot.save(failOnError:true, flush: true)
//			def dashboardHrh = new DashboardProgram(names:j(["en":"Human Resources for Health"]), weight: 1, order: 1, code:"Human Resources for Health", program: hrh)
//			dashboardHrh.save(failOnError: true, flush: true)
//			def dashboardStaffing = new DashboardProgram(names:j(["en":"Staffing"]), weight: 1, order: 1, code: "Staffing", program: staffing)
//			dashboardStaffing.save(failOnError: true, flush: true)
//
//			def calculation1 = new Sum(expression:"\$"+NormalizedDataElement.findByCode("Constant 10").id, code:"Ratio constant 10", timestamp:new Date())
//			calculation1.save(failOnError: true)
//
//			def nursea1 = new DashboardTarget(
//					names:j(["en":"Nurse A1"]), code:"A1", descriptions:j(["en":"Nurse A1"]),
//					data: calculation1, program: staffing,
//					weight: 1, order: 1).save(failOnError: true, flush:true)
//
//			def calculation2 = new Sum(expression:"\$"+NormalizedDataElement.findByCode("Constant 20").id, code:"Ratio constant 20", timestamp:new Date())
//			calculation2.save(failOnError: true)
//
//			def nursea2 = new DashboardTarget(
//					names:j(["en":"Nurse A2"]), code:"A2", descriptions:j(["en":"Nurse A2"]),
//					data: calculation2,  program: staffing,
//					weight: 1, order: 2).save(failOnError: true, flush:true)
//
//			def calculation3 = new Sum(expression:"\$"+NormalizedDataElement.findByCode("Element 1").id, code:"Ratio 1", timestamp:new Date())
//			calculation3.save(failOnError: true)
//
//			def target1 = new DashboardTarget(
//					names:j(["en":"Target 1"]), code:"TARGET1", descriptions:j(["en":"Target 1"]),
//					data: calculation3,  program: staffing,
//					weight: 1, order: 3).save(failOnError: true, flush:true)
//
//			def calculation4 = new Sum(expression:"\$"+NormalizedDataElement.findByCode("Element 2").id, code:"Ratio 2", timestamp:new Date())
//			calculation4.save(failOnError: true)
//
//			def missexpr = new DashboardTarget(
//					names:j(["en":"Missing Expression"]), code:"MISSING EXPRESSION", descriptions:j(["en":"Missing Expression"]),
//					data: calculation4,  program: staffing,
//					weight: 1, order: 4).save(failOnError: true, flush:true)
//
//			def calculation5 = new Sum(expression:"\$"+NormalizedDataElement.findByCode("Element 3").id, code:"Ratio 3", timestamp:new Date())
//			calculation5.save(failOnError: true)
//
//			def missdata = new DashboardTarget(
//					names:j(["en":"Missing Data"]), code:"MISSING DATA", descriptions:j(["en":"Missing Data"]),
//					data: calculation5,  program: staffing,
//					weight: 1, order: 5).save(failOnError: true, flush:true)
//
//			def calculation6 = new Sum(expression:"\$"+NormalizedDataElement.findByCode("Element 3").id, code:"Ratio 4", timestamp:new Date())
//			calculation6.save(failOnError: true)
//
//			def enume = new DashboardTarget(
//					names:j(["en":"Enum"]), code:"ENUM", descriptions:j(["en":"Enum"]),
//					data: calculation6, program: staffing,
//					weight: 1, order: 6).save(failOnError: true, flush:true)
//
//			nursea1.save(failOnError: true)
//			nursea2.save(failOnError: true)
//			target1.save(failOnError: true)
//			missexpr.save(failOnError: true)
//			missdata.save(failOnError: true)
//			enume.save(failOnError: true)
//
//			staffing.save(failOnError: true, flush:true)
//		}
//	}
//	
//	static def createDsr() {
//		if (!DsrTarget.count()) {
//			def dh = DataLocationType.findByCode("District Hospital")
//			def hc = DataLocationType.findByCode("Health Center")
//
//			def servDeliv = ReportProgram.findByCode("Service Delivery")
//			def instCap = ReportProgram.findByCode("Institutional Capacity")
//			def hmr = ReportProgram.findByCode("Human Resources for Health")
//
//			def root = ReportProgram.findByCode("Strategic Programs")
//			root.addChild(servDeliv)
//			root.addChild(instCap)
//			root.addChild(hmr)
//			root.save(failOnError: true, flush: true)
//
//			def infectiousDiseaseCat1 = new DsrTargetCategory(
//					names:j(["en":"Infectious Disease Testing Offered 1"]),
//					order: 1,
//					descriptions:j(["en":"Infectious Disease Testing Offered 1"]),
//					code: "Infectious Disease Testing Offered 1"
//					)
//			def infectiousDiseaseCat2 = new DsrTargetCategory(
//					names:j(["en":"Infectious Disease Testing Offered 2"]),
//					order: 2,
//					descriptions:j(["en":"Infectious Disease Testing Offered 2"]),
//					code: "Infectious Disease Testing Offered 2"
//					)
//			def nursesCat = new DsrTargetCategory(
//					names:j(["en":"Nurses"]),
//					descriptions:j(["en":"Nurses"]),
//					order: 3,
//					code: "Nurses"
//					)
//			def waterAndPowerCat = new DsrTargetCategory(
//					names:j(["en":"Facility Water and Power Sources"]),
//					order: 2,
//					descriptions:j(["en":"Facility Water and Power Sources"]),
//					code: "Facility Water and Power Sources"
//					)
//
//			def dsrAvg = new Sum(expression:"\$"+NormalizedDataElement.findByCode("Constant 10").id, code:"Dsr Average constant 10", timestamp:new Date())
//			dsrAvg.save(failOnError: true)
//			
//			def dsrSum = new Sum(expression: "\$"+NormalizedDataElement.findByCode("Constant 10").id, code:"Dsr Sum constant 10", timestamp:new Date());
//			dsrSum.save(failOnError: true);
//			
//
//			new DsrTarget(
//					names:j(["en":"A0"]), descriptions:j(["en":"A0"]),
//					program: hmr,
//					data: RawDataElement.findByCode("CODE1"),
//					average: null,
//					order: 1,
//					code: "A0",
//					category: nursesCat,
//					).save(failOnError:true)
//										
//			new DsrTarget(
//					names:j(["en":"A1"]), descriptions:j(["en":"A1"]),
//					program: hmr,
//					data: NormalizedDataElement.findByCode("TRUE"),
//					order: 2,
//					code: "A1",
//					category: nursesCat,
//					).save(failOnError:true)
//
//			new DsrTarget(
//					names:j(["en":"A2"]), descriptions:j(["en":"A2"]),
//					program: hmr,
//					data: NormalizedDataElement.findByCode("FALSE"),
//					order: 3,
//					code:"A2",
//					category: nursesCat,
//					).save(failOnError:true)
//
//			new DsrTarget(
//					names:j(["en":"A3"]), descriptions:j(["en":"A3"]),
//					program: hmr,
//					data: dsrAvg,
//					average: true,
//					order: 4,
//					code: "A3",
//					category: nursesCat,
//					).save(failOnError:true)
//
//			new DsrTarget(
//					names:j(["en":"A4"]), descriptions:j(["en":"A4"]),
//					program: hmr,
//					data: dsrSum,
//					order: 5,
//					code: "A4",
//					category: nursesCat,
//					).save(failOnError:true)
//					
//			new DsrTarget(
//					names:j(["en":"Mental Health Service"]), descriptions:j(["en":"Mental Health Service"]),
//					program: servDeliv,
//					data: NormalizedDataElement.findByCode("Constant 20"),
//					order: 11,
//					code: "Mental Health Service",
//					category: infectiousDiseaseCat2
//					).save(failOnError:true)
//
//			new DsrTarget(
//					names:j(["en":"Malaria Rapid Test"]), descriptions:j(["en":"Malaria Rapid Test"]),
//					program: servDeliv,
//					data: NormalizedDataElement.findByCode("Constant 20"),
//					order: 7,
//					code: "Malaria Rapid Test",
//					category: infectiousDiseaseCat1
//					).save(failOnError:true)
//
//			new DsrTarget(
//					names:j(["en":"HIV Rapid Test"]), descriptions:j(["en":"HIV Rapid Test"]),
//					program: servDeliv,
//					data: NormalizedDataElement.findByCode("Constant 10"),
//					order: 9,
//					code: "HIV Rapid Test",
//					category: infectiousDiseaseCat1
//					).save(failOnError:true)
//
//			new DsrTarget(
//					names:j(["en":"TB Stain Test"]), descriptions:j(["en":"TB Stain Test"]),
//					program: servDeliv,
//					data: NormalizedDataElement.findByCode("Constant 20"),
//					order: 10,
//					code: "TB Stain Test",
//					category: infectiousDiseaseCat2
//					).save(failOnError:true)
//
//			new DsrTarget(
//					names:j(["en":"Facility Water Status"]), descriptions:j(["en":"Facility Water Status"]),
//					program: instCap,
//					data: NormalizedDataElement.findByCode("Constant 10"),
//					order: 3,
//					code: "Facility Water Status",
//					category: waterAndPowerCat
//					).save(failOnError:true)
//
//			new DsrTarget(
//					names:j(["en":"Incinerator Availability"]), descriptions:j(["en":"Incinerator Availability"]),
//					program: instCap,
//					data: NormalizedDataElement.findByCode("Constant 10"),
//					order: 2,
//					code: "Incinerator Availability",
//					category: waterAndPowerCat
//					).save(failOnError:true)
//
//			infectiousDiseaseCat1.addTarget(DsrTarget.findByCode("Malaria Rapid Test"));
//			infectiousDiseaseCat1.addTarget(DsrTarget.findByCode("HIV Rapid Test"));
//			infectiousDiseaseCat1.save(failOnError:true);
//			
//			infectiousDiseaseCat2.addTarget(DsrTarget.findByCode("Mental Health Service"));
//			infectiousDiseaseCat2.addTarget(DsrTarget.findByCode("TB Stain Test"));
//			infectiousDiseaseCat2.save(failOnError:true);
//
//			nursesCat.addTarget(DsrTarget.findByCode("A0"));
//			nursesCat.addTarget(DsrTarget.findByCode("A1"));
//			nursesCat.addTarget(DsrTarget.findByCode("A2"));
//			nursesCat.addTarget(DsrTarget.findByCode("A3"));
//			nursesCat.addTarget(DsrTarget.findByCode("A4"));
//			nursesCat.save(failOnError:true);
//
//			waterAndPowerCat.addTarget(DsrTarget.findByCode("Facility Water Status"));
//			waterAndPowerCat.addTarget(DsrTarget.findByCode("Incinerator Availability"));
//			waterAndPowerCat.save(failOnError:true);
//		}
//	}
//
//	static def createFct() {
//		if (!FctTarget.count()) {
//			def dh = DataLocationType.findByCode("District Hospital")
//			def hc = DataLocationType.findByCode("Health Center")
//			def hmr = ReportProgram.findByCode("Human Resources for Health")
//			
//			def sumZero = new Sum(expression: "\$"+NormalizedDataElement.findByCode("ZERO").id, code:"Sum ZERO", timestamp:new Date());
//			sumZero.save(failOnError: true);
//			
//			def sumOne = new Sum(expression: "\$"+NormalizedDataElement.findByCode("ONE").id, code:"Sum ONE", timestamp:new Date());
//			sumOne.save(failOnError: true);
//			
//			def sumMixHC = new Sum(expression: "\$"+NormalizedDataElement.findByCode("MixHC").id, code:"Sum MixHC", timestamp:new Date());
//			sumMixHC.save(failOnError: true);
//				
//			def sumMixDH = new Sum(expression: "\$"+NormalizedDataElement.findByCode("MixDH").id, code:"Sum MixDH", timestamp:new Date());
//			sumMixDH.save(failOnError: true);
//			
//			FctTarget fctTarget1 = new FctTarget(
//
//				names:j(["en":"Fct Target 1"]),
//				program: hmr,
//				descriptions:j([:]),
//				code:"TARGET 1"
//			).save(failOnError:true)
//			
//			FctTargetOption fctTargetOption1 = new FctTargetOption(
//				names:j(["en": "Target Option 1"]),
//				target: fctTarget1,
//				descriptions:j([:]),
//				code:"TARGET OPTION 1",
//				data: sumOne
//			).save(failOnError:true)
//			
//			FctTargetOption fctTargetOption2 = new FctTargetOption(
//				names:j(["en": "Target Option 2"]),
//				target: fctTarget1,
//				descriptions:j([:]),
//				code:"TARGET OPTION 2",
//				data: sumZero
//			).save(failOnError:true)
//			
//			fctTarget1.targetOptions << [fctTargetOption1, fctTargetOption2]
//			fctTarget1.save(failOnError:true)
//
//			FctTarget fctTarget2 = new FctTarget(
//				names:j(["en":"Fct Target 2"]), descriptions:j([:]),
//				program: hmr,
//				code:"TARGET 2"
//			).save(failOnError:true)
//			
//			FctTargetOption fctTargetOption3 = new FctTargetOption(
//				names:j(["en": "Target Option 3"]),
//				target: fctTarget2,
//				descriptions:j([:]),
//				code:"TARGET OPTION 3",
//				data: sumZero
//			).save(failOnError:true)
//			
//			FctTargetOption fctTargetOption4 = new FctTargetOption(
//				names:j(["en": "Target Option 4"]),
//				target: fctTarget2,
//				descriptions:j([:]),
//				code:"TARGET OPTION 4",
//				data: sumOne
//			).save(failOnError:true)
//			
//			fctTarget2.targetOptions << [fctTargetOption3, fctTargetOption4]
//			fctTarget2.save(failOnError:true)
//			
//			FctTarget fctTarget3 = new FctTarget(
//				names:j(["en":"Fct Target 3"]), descriptions:j([:]),
//				program: hmr,
//				targetOptions: [],
//				code:"TARGET 3"
//			).save(failOnError:true)
//			
//			FctTargetOption fctTargetOption5 = new FctTargetOption(
//				names:j(["en": "Target Option 5"]),
//				target: fctTarget3,
//				descriptions:j([:]),
//				code:"TARGET OPTION 5",
//				data: sumMixHC
//			).save(failOnError:true)
//			
//			FctTargetOption fctTargetOption6 = new FctTargetOption(
//				names:j(["en": "Target Option 6"]),
//				target: fctTarget3,
//				descriptions:j([:]),
//				code:"TARGET OPTION 6",
//				data: sumMixDH
//			).save(failOnError:true)
//			
//			fctTarget3.targetOptions << [fctTargetOption5, fctTargetOption6]
//			fctTarget3.save(failOnError:true)
//			
//			FctTarget fctTarget4 = new FctTarget(
//				names:j(["en":"Fct Target 4"]), descriptions:j([:]),
//				program: hmr,
//				targetOptions: [],
//				code:"TARGET 4"
//			).save(failOnError:true)
//			
//			FctTargetOption fctTargetOption7 = new FctTargetOption(
//				names:j(["en": "Target Option 7"]),
//				target: fctTarget4,
//				descriptions:j([:]),
//				code:"TARGET OPTION 7",
//				data: sumMixHC
//			).save(failOnError:true)
//			
//			fctTarget4.targetOptions << [fctTargetOption7]
//			
//			hmr.save(failOnError:true)
//		}
//	}
//	
//	
//	static def createPlanning() {
//		
//		def planning = new Planning(
//			period: Period.list([cache: true])[0],
//			names: j(["en":"Planning 2011"]),
//			typeCodeString: "Health Center",
//			overviewHelps: j(["en": "Some help information for the planning tool - overview"]),
//			budgetHelps: j(["en": "Some help information for the planning tool - budget"]),
//			active: true
//		).save(failOnError: true)
//		
//		def formElement = new FormElement(
//				dataElement: RawDataElement.findByCode("PLANNINGELEMENT"),
//				headers: [
//					"[_].basic": j(["en":"Basic Information"]),
//					"[_].basic.description": j(["en":"Description"]),
//					"[_].basic.activity": j(["en":"Activity"]),
//					"[_].basic.area": j(["en":"Service area"]),
//					"[_].basic.instances": j(["en":"Number of instances"]),
//					"[_].basic.responsible": j(["en":"Person responsible"]),
//					"[_].basic.new_structure": j(["en":"Requires new room/structure"]),
//					"[_].staffing": j(["en":"Staffing Requirements"]),
//					"[_].staffing.nurse": j(["en":"Nurse"]),
//					"[_].staffing.nurse.nurse_time": j(["en":"Time per instance"]),
//					"[_].staffing.nurse.nurse_level": j(["en":"Level of nurse"]),
//					"[_].staffing.doctor": j(["en":"Doctor"]),
//					"[_].staffing.doctor.doctor_time": j(["en":"Time per instance"]),
//					"[_].staffing.doctor.doctor_level": j(["en":"Level of doctor"]),
//					"[_].staffing.other": j(["en":"Other staff"]),
//					"[_].staffing.other.other_time": j(["en":"Time per instance"]),
//					"[_].staffing.other.other_type": j(["en":"Level of staff"]),
//					"[_].consumables": j(["en":"Consumables"]),
//					"[_].consumables.tests": j(["en":"Number of required tests"]),
//					"[_].consumables.tests.blood_sugar": j(["en":"Sugar in blood"]),
//					"[_].consumables.tests.hiv": j(["en":"HIV"]),
//					"[_].consumables.medicine": j(["en":"Drugs required"]),
//					"[_].consumables.medicine.arv": j(["en":"ARV drugs"]),
//					"[_].consumables.medicine.tb": j(["en":"TB drugs"]),
//					"[_].consumables.medicine.malaria": j(["en":"Malaria drugs"]),
//					"[_].consumables.other": j(["en":"Other"]),
//					"[_].consumables.other[_].type": j(["en":"Type"]),
//					"[_].consumables.other[_].number": j(["en":"Number required"]),
//					"[_].monthly_breakdown": j(["en":"Monthly Breakdown"]),
//					"[_].monthly_breakdown.january": j(["en":"January"]),
//					"[_].monthly_breakdown.february": j(["en":"February"]),
//					"[_].monthly_breakdown.march": j(["en":"March"]),
//					"[_].monthly_breakdown.april": j(["en":"April"]),
//					"[_].monthly_breakdown.mai": j(["en":"Mai"]),
//					"[_].monthly_breakdown.june": j(["en":"June"]),
//					"[_].monthly_breakdown.july": j(["en":"July"]),
//					"[_].monthly_breakdown.august": j(["en":"August"]),
//					"[_].monthly_breakdown.september": j(["en":"September"]),
//					"[_].monthly_breakdown.october": j(["en":"October"]),
//					"[_].monthly_breakdown.november": j(["en":"November"]),
//					"[_].monthly_breakdown.december": j(["en":"December"]),
//					"[_].funding_sources": j(["en":"Funding Sources"]),
//					"[_].funding_sources.general_fund": j(["en":"Funded by the general fund"]),
//					"[_].funding_sources.sources": j(["en":"Individual sources"]),
//					"[_].funding_sources.sources.location": j(["en":"Facility"]),
//					"[_].funding_sources.sources.minisante": j(["en":"Minisanté"]),
//					"[_].funding_sources.sources.hospital": j(["en":"District hospital"]),
//					"[_].funding_sources.sources.gfatm": j(["en":"Global Fund"]),
//					"[_].funding_sources.sources.other": j(["en":"Other"])
//				]
//			).save(failOnError: true)
//		
//			def validationRule1 = new FormValidationRule(
//				formElement: formElement,
//				prefix: '[_].basic.instances',
//				expression: "\$"+formElement.id+"[_].basic.instances > 100",
//				messages: j(["en":"Validation error {0,here}"]),
//				dependencies: [formElement],
//				typeCodeString: "District Hospital,Health Center",
//				allowOutlier: false
//			).save(failOnError: true)
//			
//			def validationRule2 = new FormValidationRule(
//				formElement: formElement,
//				prefix: '[_].basic.test_test[_]',
//				expression: "\$"+formElement.id+"[_].basic.test_test[_] > 100",
//				messages: j(["en":"Validation error"]),
//				dependencies: [],
//				typeCodeString: "District Hospital,Health Center",
//				allowOutlier: false
//			).save(failOnError: true)
//			
//		formElement.addValidationRule(validationRule1)
//		formElement.addValidationRule(validationRule2)
//		formElement.save(failOnError: true)
//		
//		// add validation and skip rules
//		def formSkip = new PlanningSkipRule(planning: planning, expression: "\$"+formElement.id+"[_].basic.instances == 1", skippedFormElements: [(formElement): "[_].basic.responsible,[_].consumables"]).save(failOnError: true);
//		planning.addSkipRule(formSkip)
//		planning.save(failOnError: true)
//		
//		def planningType = new PlanningType(
//			names: j(["en":"Activity"]),
//			namesPlural: j(["en":"Activities"]),
//			sectionDescriptions: [
//				"[_].basic": j(["en":"Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat."]),
//				"[_].staffing": j(["en":"Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat."]),
//				"[_].consumables": j(["en":"Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat."]),
//				"[_].monthly_breakdown": j(["en":"Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat."]),
//				"[_].funding_sources": j(["en":"Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat."])
//			],
//			formElement: formElement,
//			fixedHeader: '[_].basic.description',
//			listHelps: j(["en": "Some help information for the planning tool - list"]),
//			newHelps: j(["en": "Some help information for the planning tool - new"]),
//			planning: planning
//		).save(failOnError: true);
//		planning.planningTypes << planningType
//		planning.save(failOnError: true)
//	
//		def planningElement1 = new NormalizedDataElement(
//			code: 'SUMPLANNING1',
//			type: Type.TYPE_LIST(Type.TYPE_NUMBER()),
//			expressionMap: e([(Period.list([cache: true])[0].id+''):[("Health Center"): '($'+RawDataElement.findByCode("PLANNINGELEMENT").id+' -> transform each x (if (x.basic.activity == "value1") x.basic.instances * 2 else 0))']])
//		).save(failOnError: true)
//		
//		def planningCost1 = new PlanningCost(
//			planningType: planningType,
//			type: PlanningCostType.INCOMING,
//			dataElement: planningElement1,
//			names: j(["en":"Group - Salaries"])
//		).save(failOnError: true)
//	
//		def planningElement2 = new NormalizedDataElement(
//			code: 'SUMPLANNING2',
//			type: Type.TYPE_LIST(Type.TYPE_NUMBER()),
//			expressionMap: e([(Period.list([cache: true])[0].id+''):[("Health Center"): '($'+RawDataElement.findByCode("PLANNINGELEMENT").id+' -> transform each x (if (x.basic.activity == "value1") x.basic.instances * 10 else 0))']])
//		).save(failOnError: true)
//		
//		def planningCost2 = new PlanningCost(
//			planningType: planningType,
//			type: PlanningCostType.OUTGOING,
//			dataElement: planningElement2,
//			names: j(["en":"Group - Patient"])
//		).save(failOnError: true)
//		
//		planningType.costs << planningCost1
//		planningType.costs << planningCost2
//		planningType.save(failOnError: true)
//		
//		def planningOutput1 = new PlanningOutput(
//			planning: planning,
//			fixedHeader: '[_].basic.activity',
//			dataElement: RawDataElement.findByCode("PLANNINGELEMENT"),
//			names: j(["en": "Follow-up"])
//		).save(failOnError: true)
//		
//		planning.planningOutputs << planningOutput1
//		planning.save(failOnError: true)
//		
//		def planningOutputColumn = new PlanningOutputColumn(
//			planningOutput: planningOutput1,
//			prefix: '[_].basic.instances',
//			names: j(["en": "Test"])
//		)
//		planningOutputColumn.save(failOnError: true)
//		planningOutput1.columns << planningOutputColumn
//		planningOutput1.save(failOnError: true)
//		
//		new FormEnteredValue(
//			formElement: formElement,
//			dataLocation: DataLocation.findByCode("327"),
//			value: Value.VALUE_LIST([
//				Value.VALUE_MAP([
//					"basic": Value.VALUE_MAP([
//						"description": Value.VALUE_STRING("Test - Activity"),
//						"activity": Value.VALUE_STRING("value1"),
//						"instances": Value.VALUE_NUMBER(10)
//					])
//				])
//			]),
//			timestamp: new Date()
//		).save(failOnError: true)
//	}
//	
//	static def createDataElementExport(){
//		if(!DataElementExport.count()){
//			def dh = DataLocationType.findByCode("District Hospital")
//			def hc = DataLocationType.findByCode("Health Center")
//			def periodOne = Period.list()[0];
//			def periodTwo = Period.list()[1];
//			
//			def dEtwo = NormalizedDataElement.findByCode("SUMPLANNING2");
//			def dEthree = RawDataElement.findByCode("CODE3");
//			def dEfour = RawDataElement.findByCode("CODE4");
//			def dEfive = RawDataElement.findByCode("CODE11");
//			def dEsix = RawDataElement.findByCode("CODE12");
//			def dMap = RawDataElement.findByCode("LISTMAP1");
//			def nData = NormalizedDataElement.findByCode("Constant 10");
//			
//			def dataLocationOne = DataLocation.findByCode("327");
//			def dataLocationTwo = DataLocation.findByCode("322");
//			def burera = Location.findByCode("0404");
//			def est = Location.findByCode("East");
//			def south = Location.findByCode("South");
//			
//			def exporterThree = new DataElementExport(
//				descriptions: j(["en":"Exporter Raw Data Element Three"]),
//				date: new Date(),
//				typeCodeString:"Health Center",
//				locations:[south,dataLocationTwo],
//				dataElements:[dMap,dEtwo,dEthree,nData,dEfive,dEsix],
//				periods: [periodOne,periodTwo]
//			).save(failOnError: true)
//			
//			def exporterTwo = new DataElementExport(
//				descriptions: j(["en":"Exporter Raw Data Element Two"]),
//				date: new Date(),
//				typeCodeString:"Health Center",
//				locations:[south,burera],
//				dataElements:[dEtwo,dEthree,dMap],
//				periods: [periodOne]
//			).save(failOnError: true)
//				
//			def exporterOne = new DataElementExport(
//				descriptions: j(["en":"Exporter Raw Data Element One"]),
//				date: new Date(),
//				locations:[est,burera,south],
//				typeCodeString:"District Hospital,Health Center",
//				dataElements:[dMap,dEtwo,dEthree,dEfour,dEfive,dEsix],
//				periods: [periodOne,periodTwo]
//			).save(failOnError: true)
//				
//			def exporterFour = new DataElementExport(
//				descriptions: j(["en":"Exporter Raw Data Element Four"]),
//				date: new Date(),
//				typeCodeString:"District Hospital",
//				locations:[est,dataLocationOne],
//				dataElements:[dMap,dEtwo,dEfour,dEfive,dEsix],
//				periods: [periodOne,periodTwo]
//			).save(failOnError: true)
//		}
//	}
//	
//	static def createCalculationExport(){
//		if(!CalculationExport.count()){
//			def dh = DataLocationType.findByCode("District Hospital")
//			def hc = DataLocationType.findByCode("Health Center")
//			def periodOne = Period.list()[0];
//			def periodTwo = Period.list()[1];
//			
//			def dEtwo = Sum.findByCode("Ratio constant 20");
//			def dEthree = Sum.findByCode("Ratio constant 10");
//			def dEfour = Sum.findByCode("Ratio 1");
//			def dEfive = Sum.findByCode("Ratio 2");
//		
//			def dataLocationOne = DataLocation.findByCode("327");
//			def dataLocationTwo = DataLocation.findByCode("322");
//			def burera = Location.findByCode("0404");
//			def est = Location.findByCode("East");
//			def south = Location.findByCode("South");
//			
//			def exporterThree = new CalculationExport(
//				descriptions: j(["en":"Exporter Calculation Three"]),
//				date: new Date(),
//				typeCodeString:"Health Center",
//				locations:[south,dataLocationTwo],
//				calculations:[dEtwo,dEthree,dEfive],
//				periods: [periodOne,periodTwo]
//			).save(failOnError: true, flush: true)
//			
//			def exporterTwo = new CalculationExport(
//				descriptions: j(["en":"Exporter Calculation Two"]),
//				date: new Date(),
//				typeCodeString:"Health Center",
//				locations:[south,burera],
//				calculations:[dEtwo,dEthree],
//				periods: [periodOne]
//			).save(failOnError: true)
//				
//			def exporterOne = new CalculationExport(
//				descriptions: j(["en":"Exporter Calculation One"]),
//				date: new Date(),
//				locations:[est,burera,south],
//				typeCodeString:"District Hospital,Health Center",
//				calculations:[dEtwo,dEthree,dEfour,dEfive],
//				periods: [periodOne,periodTwo]
//			).save(failOnError: true)
//				
//			def exporterFour = new CalculationExport(
//				descriptions: j(["en":"Exporter Calculation Four"]),
//				date: new Date(),
//				typeCodeString:"District Hospital",
//				locations:[est,dataLocationOne],
//				calculations:[dEtwo,dEfour,dEfive],
//				periods: [periodOne,periodTwo]
//			).save(failOnError: true, flush: true)
//		}
//	}
//		
//	
//	public static Value v(def value) {
//		return new Value("{\"value\":"+value+"}");
//	}
//	
//	public static Translation j(def map) {
//		return new Translation(jsonText: JSONUtils.getJSONFromMap(map));
//	}
	
}
