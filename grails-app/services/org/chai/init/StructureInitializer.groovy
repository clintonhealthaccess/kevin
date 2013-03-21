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
				locationId: DataLocation.findByCode("322").id, passwordHash: new Sha256Hash("123").toHex(), 
				active: true, confirmed: true, uuid: 'butaro_uuid', 
				phoneNumber: '+250 11 111 11 11', organisation:'org')
			[	"editSurvey:view", 
				"editSurvey:*:"+DataLocation.findByCode("322").id, 
				"menu:survey", 
				"menu:reports", 
				"home:*"].each {butaro.addToPermissions(it)}
			butaro.save(failOnError: true)
			
			def kivuye = new User(userType: UserType.PLANNING, code:"kivuye",
				username: "kivuye", firstname: "kivuye", lastname: "kivuye", defaultLanguage: 'en',
				locationId: DataLocation.findByCode("327").id, passwordHash: new Sha256Hash("123").toHex(),
				active: true, confirmed: true, uuid: 'kivuye_uuid',
				phoneNumber: '+250 11 111 11 11', organisation:'org')
			[	"editPlanning:view",
				"editPlanning:*:"+DataLocation.findByCode("327").id,
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
			def rwanda 		= new Location(code: "0", names_en: 'Rwanda', parent: null, level: LocationLevel.findByCode('country')).save(failOnError: true)
			rwanda.coordinates = "[[[30.419105,-1.134659],[30.816135,-1.698914],[30.758309,-2.28725],[30.469696,-2.413858],[29.938359,-2.348487],[29.632176,-2.917858],[29.024926,-2.839258],[29.117479,-2.292211],[29.254835,-2.21511],[29.291887,-1.620056],[29.579466,-1.341313],[29.821519,-1.443322],[30.419105,-1.134659]]]"
			rwanda.save(failOnError: true)
			def north 		= new Location(code: "04", names_en: 'North', parent: rwanda, level: LocationLevel.findByCode('province')).save(failOnError: true)
			north.coordinates = "[[[29.6383,-1.589],[29.6266,-1.5866],[29.6242,-1.5825],[29.612,-1.5866],[29.6076,-1.585],[29.5953,-1.5906],[29.5747,-1.5831],[29.5715,-1.576],[29.5583,-1.5679],[29.5556,-1.5738],[29.5448,-1.569],[29.5363,-1.5741],[29.5267,-1.5653],[29.521,-1.5664],[29.5065,-1.5595],[29.5058,-1.5596],[29.488,-1.5607],[29.4687,-1.534],[29.4678,-1.5327],[29.4606,-1.5222],[29.4596,-1.5205],[29.4494,-1.5035],[29.4751,-1.4674],[29.4918,-1.4467],[29.4951,-1.4404],[29.5037,-1.4376],[29.5042,-1.4324],[29.5188,-1.4272],[29.5178,-1.4225],[29.5244,-1.4109],[29.5441,-1.3993],[29.5566,-1.387],[29.5745,-1.3843],[29.5856,-1.3896],[29.5915,-1.3845],[29.607,-1.3833],[29.6304,-1.385],[29.6477,-1.3841],[29.6592,-1.3908],[29.6678,-1.3836],[29.6778,-1.3802],[29.6923,-1.3597],[29.7167,-1.3409],[29.7328,-1.3377],[29.7456,-1.3394],[29.7582,-1.3442],[29.767,-1.3512],[29.7758,-1.3634],[29.7957,-1.3701],[29.7957,-1.3478],[29.8088,-1.3272],[29.8186,-1.3248],[29.8224,-1.3064],[29.8286,-1.3123],[29.842,-1.318],[29.8405,-1.3311],[29.8489,-1.336],[29.8515,-1.3438],[29.8586,-1.3498],[29.861,-1.3579],[29.8701,-1.3529],[29.8823,-1.3538],[29.8804,-1.3698],[29.8839,-1.4053],[29.8874,-1.4109],[29.8849,-1.4208],[29.8896,-1.4256],[29.8935,-1.4496],[29.9024,-1.4589],[29.9136,-1.4796],[29.9245,-1.4771],[29.9335,-1.4693],[29.9605,-1.4593],[29.9775,-1.4544],[29.9961,-1.4437],[30.0024,-1.4286],[30.0071,-1.4267],[30.0132,-1.417],[30.0235,-1.4125],[30.0366,-1.4184],[30.0416,-1.4245],[30.052,-1.4286],[30.0552,-1.4184],[30.0507,-1.3955],[30.0699,-1.3891],[30.0745,-1.3893],[30.0832,-1.402],[30.0823,-1.411],[30.0941,-1.4113],[30.0948,-1.4233],[30.0991,-1.4229],[30.1065,-1.4354],[30.1061,-1.4374],[30.1137,-1.4472],[30.1229,-1.4632],[30.1222,-1.4648],[30.1223,-1.4668],[30.1234,-1.4684],[30.127,-1.4772],[30.1288,-1.4777],[30.133,-1.4921],[30.1401,-1.4981],[30.1405,-1.4986],[30.1422,-1.5019],[30.1424,-1.5023],[30.1439,-1.5074],[30.1439,-1.5079],[30.1453,-1.5132],[30.139,-1.5164],[30.1426,-1.5266],[30.1431,-1.5265],[30.1486,-1.5228],[30.1491,-1.5226],[30.1514,-1.5186],[30.1702,-1.5155],[30.1706,-1.5154],[30.1819,-1.5163],[30.1776,-1.529],[30.1685,-1.5319],[30.168,-1.5321],[30.1622,-1.5406],[30.1639,-1.5567],[30.1694,-1.5667],[30.1703,-1.5817],[30.1917,-1.6186],[30.1919,-1.6191],[30.1969,-1.6218],[30.1973,-1.6219],[30.2086,-1.6263],[30.2088,-1.6268],[30.2125,-1.64],[30.2129,-1.6401],[30.2158,-1.6476],[30.2222,-1.6509],[30.2276,-1.6664],[30.2413,-1.6711],[30.2518,-1.684],[30.2517,-1.6895],[30.2621,-1.7061],[30.2467,-1.7166],[30.2518,-1.7246],[30.2548,-1.7376],[30.2529,-1.7507],[30.2546,-1.7598],[30.2636,-1.7804],[30.273,-1.793],[30.2756,-1.8012],[30.2706,-1.8145],[30.2749,-1.8279],[30.2629,-1.8253],[30.2576,-1.8299],[30.2573,-1.8309],[30.2554,-1.839],[30.2471,-1.8432],[30.2349,-1.8432],[30.2167,-1.8383],[30.2055,-1.8302],[30.1813,-1.8028],[30.1612,-1.7874],[30.1476,-1.7908],[30.1418,-1.778],[30.132,-1.7769],[30.1274,-1.8064],[30.1183,-1.8167],[30.1132,-1.8183],[30.0981,-1.8106],[30.096,-1.8164],[30.0796,-1.8235],[30.0692,-1.8243],[30.0591,-1.8287],[30.048,-1.8479],[30.0286,-1.8626],[30.0254,-1.8533],[30.0209,-1.8499],[30.0146,-1.8335],[29.9991,-1.8387],[29.9905,-1.8452],[29.9919,-1.8528],[29.9883,-1.8633],[29.9783,-1.8697],[29.9767,-1.8768],[29.9811,-1.891],[29.9916,-1.9052],[29.9811,-1.9094],[29.9733,-1.9067],[29.9726,-1.9061],[29.9647,-1.9037],[29.9642,-1.9043],[29.9607,-1.9033],[29.9612,-1.9026],[29.9554,-1.8954],[29.9569,-1.8908],[29.9492,-1.8874],[29.9483,-1.8879],[29.9449,-1.8865],[29.9443,-1.8862],[29.9445,-1.8841],[29.9448,-1.8833],[29.9418,-1.8766],[29.9305,-1.8723],[29.9248,-1.864],[29.9193,-1.8674],[29.9169,-1.8621],[29.9163,-1.8622],[29.9074,-1.8546],[29.9059,-1.8578],[29.9037,-1.858],[29.8913,-1.8596],[29.8788,-1.8515],[29.8623,-1.8662],[29.8516,-1.8709],[29.8473,-1.8617],[29.8396,-1.8538],[29.8112,-1.8487],[29.8017,-1.8501],[29.7962,-1.8437],[29.8018,-1.8367],[29.7983,-1.8271],[29.7905,-1.823],[29.7796,-1.8093],[29.7715,-1.8082],[29.7686,-1.8012],[29.7686,-1.7997],[29.769,-1.7886],[29.7646,-1.7815],[29.7641,-1.7807],[29.7591,-1.7751],[29.7581,-1.7753],[29.7483,-1.7708],[29.7435,-1.7611],[29.7456,-1.7476],[29.7384,-1.7489],[29.7278,-1.7459],[29.7191,-1.7535],[29.7103,-1.7506],[29.7089,-1.7504],[29.7006,-1.7391],[29.7002,-1.7371],[29.686,-1.7291],[29.6727,-1.7297],[29.6727,-1.7297],[29.6662,-1.7285],[29.6576,-1.7331],[29.6494,-1.7244],[29.6443,-1.7234],[29.6437,-1.7223],[29.6387,-1.7216],[29.6337,-1.7118],[29.6338,-1.6972],[29.6393,-1.6736],[29.637,-1.6528],[29.6326,-1.6467],[29.6328,-1.6457],[29.6327,-1.6061],[29.6383,-1.589]]]"
			north.save(failOnError: true)
			def south 		= new Location(code: "02", names_en: 'South', parent: rwanda, level: LocationLevel.findByCode('province')).save(failOnError: true)
			def east 		= new Location(code: "05", names_en: 'East', parent: rwanda, level: LocationLevel.findByCode('province')).save(failOnError: true)
			def west 		= new Location(code: "03", names_en: 'West', parent: rwanda, level: LocationLevel.findByCode('province')).save(failOnError: true)
			def kigali 		= new Location(code: "01", names_en: 'Kigali City', parent: rwanda, level: LocationLevel.findByCode('province')).save(failOnError: true)
			def gasabo 		= new Location(code: "0102", names_en: 'Gasabo', parent: kigali, level: LocationLevel.findByCode('district')).save(failOnError: true)
			def kicukiro 	= new Location(code: "0103", names_en: 'Kicukiro', parent: kigali, level: LocationLevel.findByCode('district')).save(failOnError: true)
			def nyarugenge 	= new Location(code: "0101", names_en: 'Nyarugenge', parent: kigali, level: LocationLevel.findByCode('district')).save(failOnError: true)
			def burera 		= new Location(code: "0404", names_en: 'Burera', parent: north, level: LocationLevel.findByCode('district')).save(failOnError: true)
			burera.coordinates = "[[[29.9824,-1.4517],[29.9851,-1.4569],[29.9873,-1.4596],[29.9857,-1.4621],[29.9832,-1.4634],[29.9837,-1.4653],[29.9809,-1.4658],[29.9795,-1.4683],[29.9799,-1.4722],[29.9787,-1.4739],[29.9802,-1.4746],[29.9816,-1.4791],[29.9825,-1.4805],[29.9825,-1.4838],[29.9819,-1.4844],[29.9761,-1.4872],[29.9752,-1.4881],[29.9752,-1.4904],[29.9764,-1.4935],[29.978,-1.4949],[29.9787,-1.4966],[29.9783,-1.4979],[29.98,-1.4989],[29.9824,-1.4989],[29.9846,-1.4983],[29.9871,-1.4987],[29.9883,-1.4996],[29.9928,-1.4982],[29.9934,-1.5029],[29.9946,-1.5071],[29.9932,-1.5087],[29.9924,-1.5107],[29.9885,-1.5125],[29.986,-1.5152],[29.9824,-1.5183],[29.9795,-1.5187],[29.976,-1.5158],[29.9745,-1.5152],[29.969,-1.5155],[29.967,-1.518],[29.9659,-1.5228],[29.9642,-1.5234],[29.9623,-1.5234],[29.9622,-1.5245],[29.9611,-1.5241],[29.9608,-1.5254],[29.9631,-1.5304],[29.9637,-1.5324],[29.9655,-1.535],[29.9658,-1.5374],[29.9689,-1.5376],[29.9702,-1.5342],[29.9717,-1.5351],[29.972,-1.5389],[29.9734,-1.541],[29.9767,-1.543],[29.9783,-1.5426],[29.9798,-1.5448],[29.9799,-1.546],[29.9812,-1.5479],[29.9839,-1.5492],[29.9856,-1.5513],[29.9889,-1.5536],[29.9903,-1.556],[29.9922,-1.5579],[29.9951,-1.5626],[29.9958,-1.5654],[29.9949,-1.566],[29.9905,-1.5665],[29.989,-1.5678],[29.9854,-1.5725],[29.979,-1.5776],[29.972,-1.5842],[29.9723,-1.5779],[29.9717,-1.5721],[29.9708,-1.5672],[29.9636,-1.5717],[29.9573,-1.5647],[29.9552,-1.5628],[29.9541,-1.5644],[29.9524,-1.568],[29.9513,-1.5705],[29.9505,-1.5741],[29.9504,-1.5771],[29.9507,-1.5808],[29.9512,-1.5814],[29.9522,-1.5865],[29.9537,-1.5897],[29.9535,-1.591],[29.951,-1.5929],[29.9487,-1.591],[29.9473,-1.5908],[29.9471,-1.5919],[29.9482,-1.5951],[29.9448,-1.5934],[29.9431,-1.5939],[29.9416,-1.5929],[29.94,-1.5947],[29.9387,-1.5953],[29.9352,-1.5957],[29.9334,-1.595],[29.9323,-1.5936],[29.9273,-1.5907],[29.9248,-1.5904],[29.9223,-1.5895],[29.9203,-1.5912],[29.9174,-1.5915],[29.9176,-1.5925],[29.9164,-1.5936],[29.9158,-1.5916],[29.9138,-1.5896],[29.9104,-1.5886],[29.9085,-1.5871],[29.9052,-1.5875],[29.9003,-1.5875],[29.9043,-1.5925],[29.9045,-1.5966],[29.9037,-1.5982],[29.9031,-1.6015],[29.9037,-1.6043],[29.9045,-1.6057],[29.9001,-1.607],[29.9004,-1.609],[29.8987,-1.6098],[29.8975,-1.6089],[29.8973,-1.6062],[29.8903,-1.6059],[29.8884,-1.6081],[29.8858,-1.6092],[29.8861,-1.6073],[29.8849,-1.6062],[29.8841,-1.6067],[29.8822,-1.6061],[29.8803,-1.6072],[29.8762,-1.6077],[29.8748,-1.6062],[29.8714,-1.6058],[29.8688,-1.6064],[29.863,-1.6097],[29.862,-1.6095],[29.8595,-1.6072],[29.8599,-1.6061],[29.8578,-1.6045],[29.8573,-1.6012],[29.8577,-1.6004],[29.8561,-1.5957],[29.8564,-1.5929],[29.8551,-1.5897],[29.8556,-1.5885],[29.8537,-1.587],[29.855,-1.5824],[29.8527,-1.5787],[29.853,-1.5774],[29.8486,-1.5781],[29.845,-1.5779],[29.8425,-1.5785],[29.8407,-1.5773],[29.8405,-1.5741],[29.8388,-1.5707],[29.8378,-1.5697],[29.8359,-1.5663],[29.8376,-1.5658],[29.8372,-1.5632],[29.8391,-1.5629],[29.8399,-1.5621],[29.8396,-1.5604],[29.837,-1.5588],[29.8359,-1.557],[29.8336,-1.558],[29.8322,-1.5581],[29.8303,-1.559],[29.8296,-1.5585],[29.8273,-1.5596],[29.8258,-1.5598],[29.8231,-1.5588],[29.8217,-1.559],[29.8181,-1.5606],[29.8175,-1.5615],[29.8144,-1.5615],[29.8121,-1.5631],[29.8084,-1.5629],[29.807,-1.5644],[29.8065,-1.5627],[29.8039,-1.5614],[29.8021,-1.5635],[29.7992,-1.5638],[29.7964,-1.5658],[29.7941,-1.5642],[29.7905,-1.5659],[29.7859,-1.5665],[29.7839,-1.5651],[29.7819,-1.5643],[29.7803,-1.5617],[29.7808,-1.5613],[29.7808,-1.558],[29.7784,-1.5565],[29.7773,-1.5575],[29.7733,-1.5574],[29.7721,-1.5557],[29.7724,-1.5548],[29.7699,-1.5517],[29.7656,-1.5489],[29.7635,-1.5485],[29.7611,-1.547],[29.7624,-1.5439],[29.7644,-1.5365],[29.7646,-1.5317],[29.7642,-1.5244],[29.7627,-1.5147],[29.7595,-1.503],[29.7563,-1.4964],[29.7498,-1.4849],[29.7463,-1.4794],[29.7437,-1.481],[29.7399,-1.484],[29.7353,-1.487],[29.7261,-1.4895],[29.7242,-1.4902],[29.7233,-1.483],[29.7202,-1.4796],[29.7182,-1.4805],[29.7172,-1.4798],[29.7161,-1.4763],[29.7135,-1.4738],[29.7127,-1.4711],[29.7104,-1.4695],[29.7103,-1.4679],[29.7093,-1.4673],[29.7066,-1.4673],[29.7026,-1.465],[29.7008,-1.4629],[29.701,-1.4589],[29.6982,-1.4583],[29.6963,-1.4568],[29.6952,-1.4549],[29.6942,-1.4544],[29.6928,-1.4576],[29.6906,-1.4563],[29.6882,-1.4586],[29.6855,-1.4595],[29.684,-1.4592],[29.6814,-1.4557],[29.6794,-1.454],[29.678,-1.4534],[29.6737,-1.4542],[29.6722,-1.4528],[29.6722,-1.4499],[29.6708,-1.4432],[29.6677,-1.4437],[29.6647,-1.4449],[29.6592,-1.4476],[29.6589,-1.4469],[29.6602,-1.4423],[29.6598,-1.4404],[29.6584,-1.437],[29.6576,-1.4341],[29.6565,-1.4279],[29.6562,-1.4143],[29.6574,-1.4113],[29.6568,-1.4086],[29.6576,-1.4018],[29.6588,-1.4005],[29.6594,-1.3987],[29.6596,-1.3948],[29.6584,-1.3923],[29.6591,-1.3905],[29.6597,-1.3907],[29.6614,-1.3891],[29.6648,-1.3864],[29.6659,-1.385],[29.6678,-1.3836],[29.6707,-1.3823],[29.6778,-1.3802],[29.6783,-1.3791],[29.6818,-1.3761],[29.6838,-1.3729],[29.6859,-1.3683],[29.6887,-1.3654],[29.6905,-1.362],[29.6923,-1.3597],[29.6948,-1.359],[29.6969,-1.3578],[29.6986,-1.3551],[29.6998,-1.3524],[29.7029,-1.3505],[29.7059,-1.3499],[29.7072,-1.3492],[29.7089,-1.3467],[29.7113,-1.3445],[29.7135,-1.3435],[29.7167,-1.3409],[29.7235,-1.34],[29.7262,-1.3392],[29.7285,-1.3389],[29.7328,-1.3377],[29.7392,-1.3379],[29.7456,-1.3394],[29.7526,-1.3418],[29.7582,-1.3442],[29.7628,-1.3473],[29.767,-1.3512],[29.7736,-1.3594],[29.7758,-1.3634],[29.7825,-1.3644],[29.7847,-1.3653],[29.7957,-1.3701],[29.7963,-1.3675],[29.795,-1.3653],[29.7944,-1.3616],[29.7956,-1.3581],[29.7965,-1.3577],[29.795,-1.3541],[29.7962,-1.3508],[29.7957,-1.3478],[29.7991,-1.3431],[29.8002,-1.3411],[29.8015,-1.34],[29.8031,-1.3373],[29.8045,-1.3358],[29.8045,-1.3332],[29.8062,-1.3312],[29.8088,-1.3272],[29.8112,-1.326],[29.8145,-1.3261],[29.8159,-1.3247],[29.8186,-1.3248],[29.8188,-1.3197],[29.8204,-1.3169],[29.8205,-1.3139],[29.8182,-1.3121],[29.8198,-1.3085],[29.8225,-1.3064],[29.8239,-1.3063],[29.8251,-1.3071],[29.8286,-1.3123],[29.8302,-1.3131],[29.833,-1.3125],[29.8352,-1.3143],[29.8364,-1.3161],[29.8402,-1.3167],[29.842,-1.318],[29.8423,-1.3203],[29.8396,-1.3233],[29.8403,-1.3242],[29.8401,-1.3263],[29.8413,-1.3279],[29.8404,-1.3298],[29.8405,-1.3311],[29.8426,-1.3327],[29.8446,-1.3332],[29.8476,-1.3349],[29.8489,-1.336],[29.8488,-1.3381],[29.8512,-1.3416],[29.8515,-1.3438],[29.8549,-1.345],[29.8563,-1.3461],[29.8586,-1.3498],[29.8576,-1.352],[29.8584,-1.3545],[29.861,-1.3579],[29.8643,-1.3557],[29.8701,-1.3529],[29.8823,-1.3538],[29.8806,-1.3563],[29.8804,-1.3621],[29.8811,-1.3651],[29.8804,-1.3698],[29.8809,-1.3749],[29.8801,-1.3757],[29.8817,-1.3787],[29.8815,-1.3806],[29.8828,-1.385],[29.8822,-1.3896],[29.8823,-1.395],[29.8834,-1.4015],[29.8831,-1.4026],[29.8839,-1.4053],[29.8851,-1.4062],[29.8871,-1.4095],[29.8874,-1.4109],[29.8854,-1.4129],[29.8849,-1.4162],[29.8849,-1.4208],[29.8863,-1.4232],[29.8896,-1.4256],[29.8903,-1.4279],[29.8896,-1.4285],[29.8894,-1.4308],[29.8908,-1.4346],[29.8916,-1.4377],[29.8929,-1.4406],[29.893,-1.443],[29.8924,-1.4452],[29.8926,-1.4473],[29.8935,-1.4496],[29.8956,-1.4531],[29.8974,-1.4554],[29.9003,-1.4564],[29.9024,-1.4589],[29.9027,-1.4604],[29.9049,-1.4635],[29.9073,-1.4685],[29.9104,-1.4736],[29.9136,-1.4796],[29.9175,-1.4789],[29.9245,-1.4771],[29.9276,-1.4742],[29.931,-1.4715],[29.9337,-1.4705],[29.9335,-1.4693],[29.9408,-1.4664],[29.9432,-1.4657],[29.9605,-1.4593],[29.9624,-1.4598],[29.968,-1.4582],[29.9727,-1.4563],[29.9769,-1.4561],[29.9775,-1.4544],[29.982,-1.4515],[29.9824,-1.4517]]]"
			burera.save(failOnError: true)
		}
	}
	
	static def createDataLocations() {
		if (!DataLocation.count()) {
			[	new DataLocation(code: "322", names_en: 'Butaro HD', type: DataLocationType.findByCode('district_hospital'), coordinates: '[29.836585,-1.408931]'),
				new DataLocation(code: "327", names_en: 'Kivuye CS', type: DataLocationType.findByCode('health_center'), coordinates: '[29.93128,-1.49218]'),
				new DataLocation(code: "332", names_en: 'Rusasa CS', type: DataLocationType.findByCode('health_center'), coordinates: '[29.8704,-1.45112]')
			].each {Location.findByCode('0404').addToDataLocations(it).save(failOnError: true)}
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
