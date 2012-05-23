/**
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
package org.chai.kevin.importer
import java.sql.Types;
import java.text.SimpleDateFormat;
import org.chai.kevin.data.DataService;
import org.chai.kevin.data.Enum;
import org.chai.kevin.data.EnumOption;
import org.chai.kevin.data.RawDataElementControllerSpec;
import org.chai.kevin.IntegrationTests;
import org.chai.kevin.Period;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Type;
import org.chai.kevin.location.DataLocation
import org.chai.kevin.location.DataLocationType
import org.chai.kevin.location.Location
import org.chai.kevin.location.LocationLevel;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.value.Value;
import org.chai.kevin.value.ValueService;
import org.chai.kevin.LocationService;
import org.hibernate.SessionFactory;
import org.springframework.transaction.PlatformTransactionManager;

/**
* @author Jean Kahigiso M.
*
*/

class ImporterSpec extends IntegrationTests {
	
	// there is no rollback so each element inserted is kept between tests
	static transactional = false
	
	LocationService locationService;
	ValueService valueService;
	DataService dataService;
	SessionFactory sessionFactory;
	PlatformTransactionManager transactionManager;

	def importerService;

	def setupSpec() {
		setupLocationTree()
		newPeriod()
	}
	
	def cleanup() {
		RawDataElementValue.executeUpdate("delete RawDataElementValue")
		RawDataElement.executeUpdate("delete RawDataElement")
	} 
	
	def cleanupSpec() {
		DataLocation.executeUpdate("delete DataLocation")
		Location.executeUpdate("delete Location")
		LocationLevel.executeUpdate("delete LocationLevel")
		DataLocationType.executeUpdate("delete DataLocationType")
		Period.executeUpdate("delete Period")
	}
		
	def "get normalized import string data from csv"(){
		when:
		def type = Type.TYPE_LIST(Type.TYPE_MAP(["key1": Type.TYPE_STRING()]))
		def csvString = 
			"code,key1\n"+
			BUTARO+",value\n"
			
		def dataElement = newRawDataElement(CODE(1), type)
		
		def importerErrorManager = new ImporterErrorManager();
		importerErrorManager.setNumberOfSavedRows(0)
		importerErrorManager.setNumberOfUnsavedRows(0)
		importerErrorManager.setNumberOfRowsSavedWithError(0)
		
		NormalizedDataImporter importer = new NormalizedDataImporter(
			locationService, valueService, dataService,
			sessionFactory, transactionManager,
			importerErrorManager, dataElement, Period.list()[0]
		);
	
	    importer.importData("File Name",new StringReader(csvString))
			
		then:
		RawDataElementValue.count() == 1
		RawDataElementValue.list()[0].location.equals(DataLocation.findByCode(BUTARO))
		RawDataElementValue.list()[0].data.equals(RawDataElement.findByCode(CODE(1)))
		//RawDataElementValue.list()[0].period.equals(Period.list()[0])
		RawDataElementValue.list()[0].value.equals(Value.VALUE_LIST([Value.VALUE_MAP(["key1":Value.VALUE_STRING("value")])]))
		importerErrorManager.errors.size() == 0 
	}
	
	def "get normalized import bool data from csv"(){
		when:
		def typeBool = Type.TYPE_LIST(Type.TYPE_MAP(["marital_status": Type.TYPE_BOOL()]))
		
		def csvBoolString =
		"code,marital_status\n"+
		BUTARO+",0\n"+
		BUTARO+",1\n"+
		BUTARO+",N\n"

		def dataBoolElement = newRawDataElement(CODE(2), typeBool)
		
		def importerErrorManagerBool = new ImporterErrorManager();
		importerErrorManagerBool.setNumberOfSavedRows(0)
		importerErrorManagerBool.setNumberOfUnsavedRows(0)
		importerErrorManagerBool.setNumberOfRowsSavedWithError(0)
		
		NormalizedDataImporter importer = new NormalizedDataImporter(
			locationService, valueService, dataService,
			sessionFactory, transactionManager,
			importerErrorManagerBool, dataBoolElement, Period.list()[0]
		);
	
		importer.importData("File Name",new StringReader(csvBoolString))
		
		then:
		importerErrorManagerBool.errors.size() == 1
		typeBool.getValue(RawDataElementValue.list()[0].value, "[0].marital_status").getBooleanValue().equals(false)
		typeBool.getValue(RawDataElementValue.list()[0].value, "[1].marital_status").getBooleanValue().equals(true)
		typeBool.getValue(RawDataElementValue.list()[0].value, "[2].marital_status").isNull()
				
	}
	
	def "get normalized import date data from csv"(){
		when:
		def typeDate = Type.TYPE_LIST(Type.TYPE_MAP(["birth_date": Type.TYPE_DATE()]))
		def csvDateString =
		"code,birth_date\n"+
		BUTARO+",15-08-1971\n"
		
		def dataDateElement = newRawDataElement(CODE(3), typeDate)
		
		def importerErrorManagerDate = new ImporterErrorManager();
		importerErrorManagerDate.setNumberOfSavedRows(0)
		importerErrorManagerDate.setNumberOfUnsavedRows(0)
		importerErrorManagerDate.setNumberOfRowsSavedWithError(0)
		
		NormalizedDataImporter importer = new NormalizedDataImporter(
			locationService, valueService, dataService,
			sessionFactory, transactionManager,
			importerErrorManagerDate, dataDateElement, Period.list()[0]
		);
	
		importer.importData("File Name",new StringReader(csvDateString))
					
		then:
		importerErrorManagerDate.errors.size() == 0
		typeDate.getValue(RawDataElementValue.list()[0].value, "[0].birth_date").getDateValue().equals(new SimpleDateFormat("dd-MM-yyyy").parse("15-08-1971"));
	}
	
	def "get normalized import number data from csv"(){
		when:
		def typeNumber = Type.TYPE_LIST(Type.TYPE_MAP(["age": Type.TYPE_NUMBER()]))
		
		def csvNumberString =
		"code,age\n"+
		BUTARO+",1\n"+
		BUTARO+",ff\n"
		
		
		def dataNumberElement = newRawDataElement(CODE(4), typeNumber)
		
		def importerErrorManagerNumber = new ImporterErrorManager();
		importerErrorManagerNumber.setNumberOfSavedRows(0)
		importerErrorManagerNumber.setNumberOfUnsavedRows(0)
		importerErrorManagerNumber.setNumberOfRowsSavedWithError(0)
		
		NormalizedDataImporter importer = new NormalizedDataImporter(
			locationService, valueService, dataService,
			sessionFactory, transactionManager,
			importerErrorManagerNumber, dataNumberElement, Period.list()[0]
		);
		importer.importData("File Name",new StringReader(csvNumberString))
			
		then:
		importerErrorManagerNumber.errors.size() == 1
		typeNumber.getValue(RawDataElementValue.list()[0].value, "[0].age").getNumberValue()==1
		typeNumber.getValue(RawDataElementValue.list()[0].value, "[1].age").isNull();
	}
	
	
	
	def "get normalized import enum data from csv"(){
		when:
		def enumeGender = newEnume("gender");
		def enumGenderOption1 = newEnumOption(enumeGender,"male");
		def enumGenderOption2 = newEnumOption(enumeGender,"female");
		def typeEnum = Type.TYPE_LIST(Type.TYPE_MAP(["gender": Type.TYPE_ENUM("gender")]))
		
		def csvEnumString =
		"code,gender\n"+
		BUTARO+",Female\n"+
		BUTARO+",female\n"
		
		def dataEnumElement = newRawDataElement(CODE(5), typeEnum)
		
		def importerErrorManagerEnum = new ImporterErrorManager();
		importerErrorManagerEnum.setNumberOfSavedRows(0)
		importerErrorManagerEnum.setNumberOfUnsavedRows(0)
		importerErrorManagerEnum.setNumberOfRowsSavedWithError(0)
		
		NormalizedDataImporter importer = new NormalizedDataImporter(
			locationService, valueService, dataService,
			sessionFactory, transactionManager,
			importerErrorManagerEnum, dataEnumElement, Period.list()[0]
		);
		importer.importData("File Name",new StringReader(csvEnumString))
		
		then:
		importerErrorManagerEnum.errors.size() == 1
		EnumOption.findByValue(typeEnum.getValue(RawDataElementValue.list()[0].value, "[0].gender").getEnumValue())==null
		Enum.findByCode("gender").enumOptions.contains(EnumOption.findByValue(typeEnum.getValue(RawDataElementValue.list()[0].value, "[1].gender").getEnumValue()))
	}
	
	def "get normalized import wrong code from csv"(){
		when:
		def typeCode = Type.TYPE_LIST(Type.TYPE_MAP(["string": Type.TYPE_STRING()]))
		
		def csvCodeString =
		"code,string\n"+
		"uuu,Test-String\n"+
		BUTARO+",best String\n"
		
		def dataCodeElement = newRawDataElement(CODE(6), typeCode)
		
		def importerErrorManagerCode = new ImporterErrorManager();
		importerErrorManagerCode.setNumberOfSavedRows(0)
		importerErrorManagerCode.setNumberOfUnsavedRows(0)
		importerErrorManagerCode.setNumberOfRowsSavedWithError(0)
		
		NormalizedDataImporter importer = new NormalizedDataImporter(
			locationService, valueService, dataService,
			sessionFactory, transactionManager,
			importerErrorManagerCode, dataCodeElement, Period.list()[0]
		);
		importer.importData("File Name",new StringReader(csvCodeString))
	
		then:
		importerErrorManagerCode.errors.size() == 1
		//check if the first row was skipped and the second was taken as the first and an error was saved
		RawDataElementValue.list()[0].value.equals(Value.VALUE_LIST([Value.VALUE_MAP(["string":Value.VALUE_STRING("best String")])]))
		//please change this error msg code if it is changed in ImporterService
		importerErrorManagerCode.errors[0].messageCode.equals("import.error.message.unknown.location");
	}
	
	def "get general import string data from csv"(){
		when:
		def type = Type.TYPE_STRING()
		def dataElement = newRawDataElement(CODE(7), type)
		
		def csvString =
			"code,raw_data_element,data_value\n"+
			BUTARO+","+dataElement.code+",value\n"
			
		def importerErrorManager = new ImporterErrorManager();
		importerErrorManager.setNumberOfSavedRows(0)
		importerErrorManager.setNumberOfUnsavedRows(0)
		importerErrorManager.setNumberOfRowsSavedWithError(0)
		
		GeneralDataImporter importer = new GeneralDataImporter(
			locationService, valueService, dataService,
			importerErrorManager,  Period.list()[0]
			);

		importer.importData("File Name",new StringReader(csvString))
					
		then:
		RawDataElementValue.count() == 1
		RawDataElementValue.list()[0].location.equals(DataLocation.findByCode(BUTARO))
		RawDataElementValue.list()[0].data.equals(RawDataElement.findByCode(CODE(7)))
		//RawDataElementValue.list()[0].period.equals(Period.list()[0])
		RawDataElementValue.list()[0].value.equals(Value.VALUE_STRING("value"))
		importerErrorManager.errors.size() == 0
	}
	
	def "get general import bool data from csv"(){
		when:
		def type = Type.TYPE_BOOL()
		def dataElementOne = newRawDataElement(CODE(8), type)
		def dataElementTwo = newRawDataElement(CODE(9), type)
		def dataElementThree = newRawDataElement(CODE(10), type)
		
		def csvString =
			"code,raw_data_element,data_value\n"+
			BUTARO+","+dataElementOne.code+",1\n"+
			BUTARO+","+dataElementTwo.code+",0\n"+
			BUTARO+","+dataElementThree.code+",N\n"
			
		def importerErrorManager = new ImporterErrorManager();
		importerErrorManager.setNumberOfSavedRows(0)
		importerErrorManager.setNumberOfUnsavedRows(0)
		importerErrorManager.setNumberOfRowsSavedWithError(0)
		
		GeneralDataImporter importer = new GeneralDataImporter(
			locationService, valueService, dataService,
			importerErrorManager,  Period.list()[0]
			);

		importer.importData("File Name",new StringReader(csvString))
						
		then:
		RawDataElementValue.count() == 3
		type.getValue(RawDataElementValue.list()[0].value, "").getBooleanValue().equals(true)
		type.getValue(RawDataElementValue.list()[1].value, "").getBooleanValue().equals(false)
		type.getValue(RawDataElementValue.list()[2].value, "").getBooleanValue()==null
		importerErrorManager.errors.size() == 1
	}
		
	def "get general import enum data from csv"(){
		when:
		def enumeGender = newEnume("gender");
		def enumGenderOption1 = newEnumOption(enumeGender,"male");
		def enumGenderOption2 = newEnumOption(enumeGender,"female");
		def type = Type.TYPE_ENUM("gender")
		def dataElementOne = newRawDataElement(CODE(11), type)
		def dataElementTwo = newRawDataElement(CODE(12), type)
		
		def csvString =
		"code,raw_data_element,data_value\n"+
		BUTARO+","+dataElementOne.code+",female\n"+
		BUTARO+","+dataElementTwo.code+",Male\n"

		def importerErrorManager = new ImporterErrorManager();
		importerErrorManager.setNumberOfSavedRows(0)
		importerErrorManager.setNumberOfUnsavedRows(0)
		importerErrorManager.setNumberOfRowsSavedWithError(0)
		
		GeneralDataImporter importer = new GeneralDataImporter(
			locationService, valueService, dataService,
			importerErrorManager,  Period.list()[0]
			);

		importer.importData("File Name",new StringReader(csvString))
						
			
		then:
		importerErrorManager.errors.size() == 1
		EnumOption.findByValue(type.getValue(RawDataElementValue.list()[0].value, "").getEnumValue())!=null
		EnumOption.findByValue(type.getValue(RawDataElementValue.list()[1].value, "").getEnumValue())==null
		Enum.findByCode("gender").enumOptions.contains(EnumOption.findByValue(type.getValue(RawDataElementValue.list()[0].value, "").getEnumValue()))
	}
	
	def "get general import number data from csv"(){
		when:
		def type = Type.TYPE_NUMBER()
		def dataElementOne = newRawDataElement(CODE(13), type)
		def dataElementTwo = newRawDataElement(CODE(14), type)
		
		def csvNumberString =
		"code,raw_data_element,data_value\n"+
		BUTARO+","+dataElementOne.code+",zz\n"+
		BUTARO+","+dataElementTwo.code+",44\n"
	
		
		def importerErrorManager = new ImporterErrorManager();
		importerErrorManager.setNumberOfSavedRows(0)
		importerErrorManager.setNumberOfUnsavedRows(0)
		importerErrorManager.setNumberOfRowsSavedWithError(0)
		
		GeneralDataImporter importer = new GeneralDataImporter(
			locationService, valueService, dataService,
			importerErrorManager,  Period.list()[0]
			);

		importer.importData("File Name",new StringReader(csvNumberString))
							
		then:
		importerErrorManager.errors.size() == 1
		RawDataElementValue.list().size()==2
		type.getValue(RawDataElementValue.list()[0].value, "").getNumberValue()==null
		type.getValue(RawDataElementValue.list()[1].value, "").getNumberValue()==44;
	}
	
	def "get general import date data from csv"(){
		when:
		def type = Type.TYPE_DATE()
		def dataElementOne = newRawDataElement(CODE(15), type)
		def dataElementTwo = newRawDataElement(CODE(16), type)
		
		def csvDateString =
		"code,raw_data_element,data_value\n"+
		BUTARO+","+dataElementOne.code+",15-08-1971\n"+
		BUTARO+","+dataElementTwo.code+",44\n"
		
		def importerErrorManager = new ImporterErrorManager();
		importerErrorManager.setNumberOfSavedRows(0)
		importerErrorManager.setNumberOfUnsavedRows(0)
		importerErrorManager.setNumberOfRowsSavedWithError(0)
		
		GeneralDataImporter importer = new GeneralDataImporter(
			locationService, valueService, dataService,
			importerErrorManager,  Period.list()[0]
			);

		importer.importData("File Name",new StringReader(csvDateString))
			
		then:
		importerErrorManager.errors.size() == 1
		type.getValue(RawDataElementValue.list()[0].value, "").getDateValue().equals(new SimpleDateFormat("dd-MM-yyyy").parse("15-08-1971"));
		type.getValue(RawDataElementValue.list()[1].value, "").getDateValue()==null
	}
	
	def "get general import wrong code and raw_data_element from csv"(){
		when:
		def type = Type.TYPE_STRING()
		def dataElement = newRawDataElement(CODE(17), type)
		def csvCodeString =
		"code,raw_data_element,data_value\n"+
		"BUTERO,"+dataElement.code+",Text1\n"+
		BUTARO+","+dataElement.code+",Text2\n"+
		BUTARO+","+"Code"+",Text2\n"
		
		
		def importerErrorManager = new ImporterErrorManager();
		importerErrorManager.setNumberOfSavedRows(0)
		importerErrorManager.setNumberOfUnsavedRows(0)
		importerErrorManager.setNumberOfRowsSavedWithError(0)
		
		GeneralDataImporter importer = new GeneralDataImporter(
			locationService, valueService, dataService,
			importerErrorManager,  Period.list()[0]
			);

		importer.importData("File Name",new StringReader(csvCodeString))
			
		then:
		importerErrorManager.errors.size() == 2
		RawDataElementValue.list().size()==1
		//check if the first row was skipped and the second was taken as the first and an error was saved
		RawDataElementValue.list()[0].value.equals(Value.VALUE_STRING("Text2"))
		//please change this error msg code if it is changed in ImporterService
		importerErrorManager.errors[0].messageCode.equals("import.error.message.unknown.data.location");
		importerErrorManager.errors[1].messageCode.equals("import.error.message.unknown.raw.data.element");
	}
	
	def "get general import data being override from csv"(){
		when:
		def type = Type.TYPE_STRING()
		def dataElement = newRawDataElement(CODE(18), type)
		def csvCodeString =
		"code,raw_data_element,data_value\n"+
		BUTARO+","+dataElement.code+",overrideStringOne\n"+
		BUTARO+","+dataElement.code+",overrideStringTwo\n"
		
		def importerErrorManager = new ImporterErrorManager();
		importerErrorManager.setNumberOfSavedRows(0)
		importerErrorManager.setNumberOfUnsavedRows(0)
		importerErrorManager.setNumberOfRowsSavedWithError(0)
		
		GeneralDataImporter importer = new GeneralDataImporter(
			locationService, valueService, dataService,
			importerErrorManager,  Period.list()[0]
			);

		importer.importData("File Name",new StringReader(csvCodeString))
			
			
		then:
		importerErrorManager.errors.size() == 1
		RawDataElementValue.list().size()==1
		//check if the first element was override
		RawDataElementValue.list()[0].value.equals(Value.VALUE_STRING("overrideStringTwo"))
		//please change this error msg code if it is changed in ImporterService
		importerErrorManager.errors[0].messageCode.equals("import.error.message.data.duplicated");
	}
		
}
