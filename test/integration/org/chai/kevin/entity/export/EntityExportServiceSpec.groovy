package org.chai.kevin.entity.export

import java.lang.reflect.Field
import org.apache.commons.lang.StringUtils;
import org.chai.kevin.IntegrationTests
import org.chai.kevin.Translation
import org.chai.kevin.dashboard.DashboardTarget
import org.chai.kevin.data.Type;
import org.chai.kevin.entity.export.EntityHeaderSorter
import org.chai.kevin.form.FormEnteredValue;
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.Location;
import org.chai.kevin.survey.SurveyQuestion
import org.chai.kevin.survey.SurveyCheckboxQuestion
import org.chai.kevin.survey.SurveySimpleQuestion
import org.chai.kevin.survey.SurveyTableQuestion
import org.chai.kevin.survey.SurveyElement
import org.chai.kevin.survey.SurveyIntegrationTests
import org.chai.kevin.util.Utils;

class EntityExportServiceSpec extends IntegrationTests {

	def entityExportService
	
	def "test for export entity"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = SurveyIntegrationTests.newSurvey(j(["en":"survey"]), period)
		def program = SurveyIntegrationTests.newSurveyProgram(j(["en":"program"]), survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = SurveyIntegrationTests.newSurveySection(j(["en":"section"]), program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = SurveyIntegrationTests.newSimpleQuestion(j(["en":"question"]), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def type = Type.TYPE_NUMBER()
		def element = SurveyIntegrationTests.newSurveyElement(question, newRawDataElement(CODE(1), type))
		FormEnteredValue formEnteredValue = newFormEnteredValue(element, period, DataLocation.findByCode(BUTARO), v("10"))		
		Map<SurveyElement, FormEnteredValue> surveyElementValueMap = new HashMap<SurveyElement, FormEnteredValue>()
		surveyElementValueMap.put(formEnteredValue.getFormElement(), formEnteredValue)
		
		when:
		def file = entityExportService.getExportFile("file", SurveySimpleQuestion.class)
		def zipFile = Utils.getZipFile(file, "file")
		
		then:
		zipFile.exists() == true
		zipFile.length() > 0
	}
	
	def "test for valid export filename"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = SurveyIntegrationTests.newSurvey(j(["en":"survey"]), period)
		def program = SurveyIntegrationTests.newSurveyProgram(j(["en":"program"]), survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = SurveyIntegrationTests.newSurveySection(j(["en":"section"]), program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = SurveyIntegrationTests.newSimpleQuestion(j(["en":"question"]), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def type = Type.TYPE_NUMBER()
		def element = SurveyIntegrationTests.newSurveyElement(question, newRawDataElement(CODE(1), type))
		FormEnteredValue formEnteredValue = newFormEnteredValue(element, period, DataLocation.findByCode(BUTARO), v("10"))		
		Map<SurveyElement, FormEnteredValue> surveyElementValueMap = new HashMap<SurveyElement, FormEnteredValue>()
		surveyElementValueMap.put(formEnteredValue.getFormElement(), formEnteredValue)
		
		when:
		def file = entityExportService.getExportFilename(SurveySimpleQuestion.class)
		
		then:
		file.startsWith("SurveySimpleQuestion_")
	}	
	
	def "test for export multiple files in zip file"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		def survey = SurveyIntegrationTests.newSurvey(j(["en":"survey"]), period)
		def program = SurveyIntegrationTests.newSurveyProgram(j(["en":"program"]), survey, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def section = SurveyIntegrationTests.newSurveySection(j(["en":"section"]), program, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def question = SurveyIntegrationTests.newSimpleQuestion(j(["en":"question"]), section, 1, [(DISTRICT_HOSPITAL_GROUP)])
		def type = Type.TYPE_NUMBER()
		def element = SurveyIntegrationTests.newSurveyElement(question, newRawDataElement(CODE(1), type))
		FormEnteredValue formEnteredValue = newFormEnteredValue(element, period, DataLocation.findByCode(BUTARO), v("10"))
		Map<SurveyElement, FormEnteredValue> surveyElementValueMap = new HashMap<SurveyElement, FormEnteredValue>()
		surveyElementValueMap.put(formEnteredValue.getFormElement(), formEnteredValue)
		
		def entityClazz = [SurveyCheckboxQuestion.class, SurveySimpleQuestion.class, SurveyTableQuestion.class] 
		
		when:
		List<String> filenames = new ArrayList<String>();
		List<File> csvFiles = new ArrayList<File>();
		for(Class clazz : entityClazz){
			String filename = entityExportService.getExportFilename(clazz);
			filenames.add(filename);
			csvFiles.add(entityExportService.getExportFile(filename, clazz));
		}
		String zipFilename = StringUtils.join(filenames, "_")
		def zipFile = Utils.getZipFile(filenames, zipFilename)
		
		then:
		zipFilename.startsWith("SurveyCheckboxQuestion_SurveySimpleQuestion_SurveyTableQuestion_")
		zipFile.exists() == true
		zipFile.length() > 0
	}
	
	def "test for entity header sort"(){
		setup:
		def entitySurveyQuestionFieldHeaders = []			
		def headerClass = SurveyQuestion.class;
		while(headerClass != null && headerClass != Object.class){				
			Field[] classFields = headerClass.getDeclaredFields();
			for(Field field : classFields){
				entitySurveyQuestionFieldHeaders.add(field);
			}
			headerClass = headerClass.getSuperclass();
		}
		
		def entityDashboardTargetFieldHeaders = []
		headerClass = DashboardTarget.class;
		while(headerClass != null && headerClass != Object.class){
			Field[] classFields = headerClass.getDeclaredFields();
			for(Field field : classFields){
				entityDashboardTargetFieldHeaders.add(field);
			}
			headerClass = headerClass.getSuperclass();
		}
		
		when:
		Collections.sort(entitySurveyQuestionFieldHeaders, EntityHeaderSorter.BY_FIELD())
		def surveyQuestionHeaders = entitySurveyQuestionFieldHeaders.collect { it.getName() }
		Collections.sort(entityDashboardTargetFieldHeaders, EntityHeaderSorter.BY_FIELD())
		def dashboardTargetHeaders = entityDashboardTargetFieldHeaders.collect { it.getName() }
		
		then:
		surveyQuestionHeaders.equals(["code", "names", "order", "section", "typeCodeString", "descriptions"])
		dashboardTargetHeaders.equals(["code", "names", "order", "calculation", "program", "weight", "descriptions"])
	}	
	
	
	def "test entity is exportable"(){
		when:
		def ie = new IsExportableEntity()
		def clazz = Utils.isExportable(ie.class)
		
		then:
		clazz != null
	}
	
	def "test entity is exportable primitive"(){
		when:
		def clazz = Utils.isExportablePrimitive(Integer.class)
		
		then:
		clazz != null
	}
	
	def "test entity is not exportable"(){
		when:
		def ne = new IsNotExportableEntity()
		def clazz = Utils.isExportable(ne.class)
		
		then:
		clazz == null		
	}
	
	def "test entity fields are exportable"(){
		when:
		def ie = new IsExportableEntity("ieCode1", 1, new Date())
		 			
		List<Field> fields = new ArrayList<Field>();		
		Class<?> headerClass = ie.class;
		while(headerClass != null && headerClass != Object.class){
			Field[] classFields = headerClass.getDeclaredFields();
			for(Field field : classFields){
				fields.add(field);
			}
			headerClass = headerClass.getSuperclass();
		}
		
		def entityData = entityExportService.getEntityData(ie, fields)
		
		then:
		entityData.equals(["code", 1, "01-01-0001", "{}"])
		
		when:
		ie.trans = new Translation(j(["en":"English", "fr":"French"]))
		
		then:
		entityData.equals(["code", 1, "01-01-0001", "{\"en\":\"English\", \"fr\":\"French\"}"])
	}
	
	def "test entity fields that are exportable and not exportable"(){
		when:
		def te = new TestExportableEntity("testCode")
					 
		List<Field> fields = new ArrayList<Field>();
		Class<?> headerClass = te.class;
		while(headerClass != null && headerClass != Object.class){
			Field[] classFields = headerClass.getDeclaredFields();
			for(Field field : classFields){
				fields.add(field);
			}
			headerClass = headerClass.getSuperclass();
		}
		
		def entityData = entityExportService.getEntityData(te, fields)
		
		then:
		entityData.equals(["testCode", "[]", Utils.VALUE_NOT_EXPORTABLE])
		
		when:
		te.iee = new IsExportableEntity("ieCode", 1, new Date())
		te.inee = new IsNotExportableEntity()
		
		then:
		entityData.equals(["testCode", "[~ieCode~]", Utils.VALUE_NOT_EXPORTABLE])
	}
	
	def "test entity fields that are exportable lists and not exportable lists"(){
		when:
		def te = new TestExportableEntities("testCode")
					 
		List<Field> fields = new ArrayList<Field>();
		Class<?> headerClass = te.class;
		while(headerClass != null && headerClass != Object.class){
			Field[] classFields = headerClass.getDeclaredFields();
			for(Field field : classFields){
				fields.add(field);
			}
			headerClass = headerClass.getSuperclass();
		}
		
		def entityData = entityExportService.getEntityData(te, fields)
		
		then:
		entityData.equals(["testCode", "[]", Utils.VALUE_NOT_EXPORTABLE])
		
		when:
		te.listIee = [new IsExportableEntity("ieCode1", 1, new Date())] 
		te.listInee = [new IsNotExportableEntity(), new IsNotExportableEntity()]
		
		then:
		entityData.equals(["testCode", "[[~ieCode1~]]", Utils.VALUE_NOT_EXPORTABLE])
		
		when:
		te.listIee = [new IsExportableEntity("ieCode1", 1, new Date()),[new IsExportableEntity("ieCode2", 2, new Date())]]
		te.listInee = [new IsNotExportableEntity(), new IsNotExportableEntity()]
		
		then:
		entityData.equals(["testCode", "[[~ieCode1~], [~ieCod2e~]]", Utils.VALUE_NOT_EXPORTABLE])
	}
	
	
	public class IsExportableEntity implements Exportable {
		
		public Integer num;
		public String code;
		public Date dat;
		public Translation trans;
		
		public IsExportableEntity() {
			this.num = 0;
			this.code = "";
			this.dat = new Date();
			this.trans = new Translation();
		}
		
		public IsExportableEntity(String code, Integer num, Date dat) {
			this.num = num;
			this.code = code;
			this.dat = dat;
			this.trans = new Translation();
		}
		
		public String toExportString() {
			return "[" + Utils.formatExportCode(code) + "]";
		}
	}
	
	public class IsNotExportableEntity {
		IsNotExportableEntity() { }
	}		
	
	public class TestExportableEntity implements Exportable {
		
		public String code;
		public IsExportableEntity iee;
		public IsNotExportableEntity inee;
		
		public TestExportableEntity(String code) {
			this.code = code;
			iee = new IsExportableEntity()
			inee = new IsNotExportableEntity()
		}
				
		public String toExportString() {
			return "[" + Utils.formatExportCode(code) + "]";
		}
	}
	
	public class TestExportableEntities implements Exportable {
		
		public String code;
		public List<IsExportableEntity> listIee;
		public List<IsNotExportableEntity> listInee;
		
		public TestExportableEntities(String code) {
			this.code = code;
			listIee = []
			listInee = []
		}
			
		public String toExportString() {
			return "[" + Utils.formatExportCode(code) + "]";
		}
	}
}
