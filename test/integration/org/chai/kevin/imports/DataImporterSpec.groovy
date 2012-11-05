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
package org.chai.kevin.imports
import java.sql.Types;
import java.text.SimpleDateFormat;
import org.chai.kevin.data.DataService;
import org.chai.kevin.data.Enum;
import org.chai.kevin.data.EnumOption;
import org.chai.kevin.data.RawDataElementControllerSpec;
import org.chai.kevin.IntegrationTests;
import org.chai.kevin.Period;
import org.chai.kevin.PeriodService;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Type;
import org.chai.kevin.imports.GeneralDataImporter;
import org.chai.kevin.imports.ImporterErrorManager;
import org.chai.location.DataLocation
import org.chai.location.DataLocationType
import org.chai.location.Location
import org.chai.location.LocationLevel;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.value.Value;
import org.chai.kevin.value.ValueService;
import org.chai.location.LocationService;
import org.hibernate.SessionFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.supercsv.io.CsvMapReader;
import org.supercsv.prefs.CsvPreference;

/**
 * @author Jean Kahigiso M.
 *
 */

class DataImporterSpec extends IntegrationTests {

	// there is no rollback so each element inserted is kept between tests
	static transactional = false

	LocationService locationService;
	ValueService valueService;
	DataService dataService;
	SessionFactory sessionFactory;
	PlatformTransactionManager transactionManager;
	PeriodService periodService;

	def importerService;

	def setupSpec() {
		setupLocationTree()
		newPeriod()
	}

	def cleanup() {
		RawDataElementValue.executeUpdate("delete RawDataElementValue")
		RawDataElement.executeUpdate("delete RawDataElement")
		EnumOption.executeUpdate("delete EnumOption")
		Enum.executeUpdate("delete Enum")
	}

	def cleanupSpec() {
		DataLocation.executeUpdate("delete DataLocation")
		Location.executeUpdate("delete Location")
		LocationLevel.executeUpdate("delete LocationLevel")
		DataLocationType.executeUpdate("delete DataLocationType")
		Period.executeUpdate("delete Period")
	}

	def "get nominative import string data from csv"(){
		when:
		def type = Type.TYPE_LIST(Type.TYPE_MAP(["key1": Type.TYPE_STRING()]))
		def csvString =
				"location_code,key1\n"+
				BUTARO+",value\n";

		def dataElement = newRawDataElement(CODE(1), type)

		def importerErrorManager = new ImporterErrorManager();

		NominativeDataImporter importer = new NominativeDataImporter(
				locationService, valueService, dataService,
				sessionFactory, transactionManager,
				importerErrorManager, dataElement, Period.list()[0]
				);

		importer.importData("File Name",new CsvMapReader(new StringReader(csvString), CsvPreference.EXCEL_PREFERENCE))

		then:
		RawDataElementValue.count() == 1
		RawDataElementValue.list()[0].location.equals(DataLocation.findByCode(BUTARO))
		RawDataElementValue.list()[0].data.equals(RawDataElement.findByCode(CODE(1)))
		//RawDataElementValue.list()[0].period.equals(Period.list()[0])
		RawDataElementValue.list()[0].value.equals(Value.VALUE_LIST([
			Value.VALUE_MAP(["key1":Value.VALUE_STRING("value")])
		]))
		importerErrorManager.errors.size() == 0
	}

	def "import data when data already exists csv"(){
		when:
		def type = Type.TYPE_LIST(Type.TYPE_MAP(["key1": Type.TYPE_STRING()]))
		def csvString =
				"location_code,key1\n"+
				BUTARO+",value\n"

		def dataElement = newRawDataElement(CODE(1), type)
		def rawDataElementValue = newRawDataElementValue(dataElement,Period.list()[0],DataLocation.findByCode(BUTARO),Value.VALUE_LIST([
			Value.VALUE_MAP(["key1":Value.VALUE_STRING("test")])
		]))

		def importerErrorManager = new ImporterErrorManager();
		NominativeDataImporter importer = new NominativeDataImporter(
				locationService, valueService, dataService,
				sessionFactory, transactionManager,
				importerErrorManager, dataElement, Period.list()[0]
				);

		importer.importData("File Name",new CsvMapReader(new StringReader(csvString), CsvPreference.EXCEL_PREFERENCE))

		then:
		importerErrorManager.errors.size() == 0
		RawDataElementValue.count() == 1
		RawDataElementValue.list()[0].location.equals(DataLocation.findByCode(BUTARO))
		RawDataElementValue.list()[0].data.equals(RawDataElement.findByCode(CODE(1)))
		//RawDataElementValue.list()[0].period.equals(Period.list()[0])
		RawDataElementValue.list()[0].value.equals(Value.VALUE_LIST([
			Value.VALUE_MAP(["key1":Value.VALUE_STRING("value")])
		]))

	}


	def "get nominative import bool data from csv"(){
		when:
		def typeBool = Type.TYPE_LIST(Type.TYPE_MAP(["marital_status": Type.TYPE_BOOL()]))

		def csvBoolString =
				"location_code,marital_status\n"+
				BUTARO+",TRUE\n"+
				BUTARO+",FALSE\n"+
				BUTARO+",N\n"

		def dataBoolElement = newRawDataElement(CODE(2), typeBool)

		def importerErrorManagerBool = new ImporterErrorManager();

		NominativeDataImporter importer = new NominativeDataImporter(
				locationService, valueService, dataService,
				sessionFactory, transactionManager,
				importerErrorManagerBool, dataBoolElement, Period.list()[0]
				);

		importer.importData("File Name",new CsvMapReader(new StringReader(csvBoolString), CsvPreference.EXCEL_PREFERENCE))

		then:
		importerErrorManagerBool.errors.size() == 1
		typeBool.getValue(RawDataElementValue.list()[0].value, "[0].marital_status").getBooleanValue().equals(true)
		typeBool.getValue(RawDataElementValue.list()[0].value, "[1].marital_status").getBooleanValue().equals(false)
		typeBool.getValue(RawDataElementValue.list()[0].value, "[2].marital_status").isNull()

	}

	def "get nominative import date data from csv"(){
		when:
		def typeDate = Type.TYPE_LIST(Type.TYPE_MAP(["birth_date": Type.TYPE_DATE()]))
		def csvDateString =
				"location_code,birth_date\n"+
				BUTARO+",15-08-1971\n"

		def dataDateElement = newRawDataElement(CODE(3), typeDate)

		def importerErrorManagerDate = new ImporterErrorManager();

		NominativeDataImporter importer = new NominativeDataImporter(
				locationService, valueService, dataService,
				sessionFactory, transactionManager,
				importerErrorManagerDate, dataDateElement, Period.list()[0]
				);

		importer.importData("File Name",new CsvMapReader(new StringReader(csvDateString), CsvPreference.EXCEL_PREFERENCE))

		then:
		importerErrorManagerDate.errors.size() == 0
		typeDate.getValue(RawDataElementValue.list()[0].value, "[0].birth_date").getDateValue().equals(new SimpleDateFormat("dd-MM-yyyy").parse("15-08-1971"));
	}

	def "get nominative import number data from csv"(){
		when:
		def typeNumber = Type.TYPE_LIST(Type.TYPE_MAP(["age": Type.TYPE_NUMBER()]))

		def csvNumberString =
				"location_code,age\n"+
				BUTARO+",1\n"+
				BUTARO+",ff\n"

		def dataNumberElement = newRawDataElement(CODE(4), typeNumber)

		def importerErrorManagerNumber = new ImporterErrorManager();

		NominativeDataImporter importer = new NominativeDataImporter(
				locationService, valueService, dataService,
				sessionFactory, transactionManager,
				importerErrorManagerNumber, dataNumberElement, Period.list()[0]
				);
		importer.importData("File Name",new CsvMapReader(new StringReader(csvNumberString), CsvPreference.EXCEL_PREFERENCE))

		then:
		importerErrorManagerNumber.errors.size() == 1
		typeNumber.getValue(RawDataElementValue.list()[0].value, "[0].age").getNumberValue()==1
		typeNumber.getValue(RawDataElementValue.list()[0].value, "[1].age").isNull();
	}



	def "get nominative import enum data from csv"(){
		when:
		def enumeGender = newEnume("gender");
		def enumGenderOption1 = newEnumOption(enumeGender,"male");
		def enumGenderOption2 = newEnumOption(enumeGender,"female");
		def typeEnum = Type.TYPE_LIST(Type.TYPE_MAP(["gender": Type.TYPE_ENUM("gender")]))

		def csvEnumString =
				"location_code,gender\n"+
				BUTARO+",Female\n"+
				BUTARO+",female\n"

		def dataEnumElement = newRawDataElement(CODE(5), typeEnum)

		def importerErrorManagerEnum = new ImporterErrorManager();

		NominativeDataImporter importer = new NominativeDataImporter(
				locationService, valueService, dataService,
				sessionFactory, transactionManager,
				importerErrorManagerEnum, dataEnumElement, Period.list()[0]
				);
		importer.importData("File Name",new CsvMapReader(new StringReader(csvEnumString), CsvPreference.EXCEL_PREFERENCE))

		then:
		importerErrorManagerEnum.errors.size() == 1
		EnumOption.findByValue(typeEnum.getValue(RawDataElementValue.list()[0].value, "[0].gender").getEnumValue())==null
		Enum.findByCode("gender").enumOptions.contains(EnumOption.findByValue(typeEnum.getValue(RawDataElementValue.list()[0].value, "[1].gender").getEnumValue()))
	}

	def "get nominative import wrong code from csv"(){
		when:
		def typeCode = Type.TYPE_LIST(Type.TYPE_MAP(["string": Type.TYPE_STRING()]))

		def csvCodeString =
				"location_code,string\n"+
				"uuu,Test-String\n"+
				BUTARO+",best String\n"

		def dataCodeElement = newRawDataElement(CODE(6), typeCode)

		def importerErrorManagerCode = new ImporterErrorManager();

		NominativeDataImporter importer = new NominativeDataImporter(
				locationService, valueService, dataService,
				sessionFactory, transactionManager,
				importerErrorManagerCode, dataCodeElement, Period.list()[0]
				);
		importer.importData("File Name",new CsvMapReader(new StringReader(csvCodeString), CsvPreference.EXCEL_PREFERENCE))

		then:
		importerErrorManagerCode.errors.size() == 1
		//check if the first row was skipped and the second was taken as the first and an error was saved
		RawDataElementValue.list()[0].value.equals(Value.VALUE_LIST([
			Value.VALUE_MAP(["string":Value.VALUE_STRING("best String")])
		]))
		//please change this error msg code if it is changed in ImporterService
		importerErrorManagerCode.errors[0].messageCode.equals("import.error.message.unknown.location");
	}

	def "test system don't break if the file from zip is not csv"(){
		when:
		def typeDate = Type.TYPE_LIST(Type.TYPE_MAP(["birth_date": Type.TYPE_DATE(), "test": Type.TYPE_STRING()]))
		def dateDataElement = newRawDataElement(CODE(3), typeDate)

		def importerErrorManagerDate = new ImporterErrorManager();

		NominativeDataImporter importer = new NominativeDataImporter(
				locationService, valueService, dataService,
				sessionFactory, transactionManager,
				importerErrorManagerDate, dateDataElement, Period.list()[0]
				);

		File file = new File("test/integration/org/chai/kevin/imports/nominativeTestFile.csv.zip");
		importer.importZipFiles(new FileInputStream(file), null, null);

		then:
		importerErrorManagerDate.errors.size() == 2
		RawDataElementValue.count()==1
		typeDate.getValue(RawDataElementValue.list()[0].value, "[0].birth_date").getDateValue().equals(new SimpleDateFormat("dd-MM-yyyy").parse("15-08-1971"));
	}

	def "test imports with right delimiter is correct"(){
		when:
		def typeDate = Type.TYPE_LIST(Type.TYPE_MAP(["birth_date": Type.TYPE_DATE(), "test": Type.TYPE_STRING()]))
		def dateDataElement = newRawDataElement(CODE(3), typeDate)

		def importerErrorManagerDate = new ImporterErrorManager();

		NominativeDataImporter importer = new NominativeDataImporter(
				locationService, valueService, dataService,
				sessionFactory, transactionManager,
				importerErrorManagerDate, dateDataElement, Period.list()[0]
				);

		File file = new File("test/integration/org/chai/kevin/imports/nominativeTestFile.csv.zip");
		importer.importZipFiles(new FileInputStream(file), null, ',' as Character);

		then:
		importerErrorManagerDate.errors.size() == 2
		RawDataElementValue.count()==1
		typeDate.getValue(RawDataElementValue.list()[0].value, "[0].birth_date").getDateValue().equals(new SimpleDateFormat("dd-MM-yyyy").parse("15-08-1971"));
		typeDate.getValue(RawDataElementValue.list()[0].value, "[0].test").getStringValue() == "éééé"
	}

	def "test imports with wrong delimiter is incorrect"(){
		when:
		def typeDate = Type.TYPE_LIST(Type.TYPE_MAP(["birth_date": Type.TYPE_DATE(), "test": Type.TYPE_STRING()]))
		def dateDataElement = newRawDataElement(CODE(3), typeDate)

		def importerErrorManagerDate = new ImporterErrorManager();

		NominativeDataImporter importer = new NominativeDataImporter(
				locationService, valueService, dataService,
				sessionFactory, transactionManager,
				importerErrorManagerDate, dateDataElement, Period.list()[0]
				);

		File file = new File("test/integration/org/chai/kevin/imports/nominativeTestFile.csv.zip");
		importer.importZipFiles(new FileInputStream(file), null, ';' as Character);

		then:
		importerErrorManagerDate.errors.size() == 5
		RawDataElementValue.count()==0
	}

	def "test system imports file correctly when right encoding is specified"(){
		when:
		def typeDate = Type.TYPE_LIST(Type.TYPE_MAP(["birth_date": Type.TYPE_DATE(), "test": Type.TYPE_STRING()]))
		def dateDataElement = newRawDataElement(CODE(3), typeDate)

		def importerErrorManagerDate = new ImporterErrorManager();

		NominativeDataImporter importer = new NominativeDataImporter(
				locationService, valueService, dataService,
				sessionFactory, transactionManager,
				importerErrorManagerDate, dateDataElement, Period.list()[0]
				);

		File file = new File("test/integration/org/chai/kevin/imports/nominativeTestFile.csv.zip");
		importer.importZipFiles(new FileInputStream(file), "utf-8", null);

		then:
		importerErrorManagerDate.errors.size() == 2
		RawDataElementValue.count()==1
		typeDate.getValue(RawDataElementValue.list()[0].value, "[0].birth_date").getDateValue().equals(new SimpleDateFormat("dd-MM-yyyy").parse("15-08-1971"));
		typeDate.getValue(RawDataElementValue.list()[0].value, "[0].test").getStringValue() == "éééé"
	}

	def "test system imports file incorrectly when wrong encoding is specified"(){
		when:
		def typeDate = Type.TYPE_LIST(Type.TYPE_MAP(["birth_date": Type.TYPE_DATE(), "test": Type.TYPE_STRING()]))
		def dateDataElement = newRawDataElement(CODE(3), typeDate)

		def importerErrorManagerDate = new ImporterErrorManager();

		NominativeDataImporter importer = new NominativeDataImporter(
				locationService, valueService, dataService,
				sessionFactory, transactionManager,
				importerErrorManagerDate, dateDataElement, Period.list()[0]
				);

		File file = new File("test/integration/org/chai/kevin/imports/nominativeTestFile.csv.zip");
		importer.importZipFiles(new FileInputStream(file), "ISO-8859-1", null);

		then:
		importerErrorManagerDate.errors.size() ==2
		RawDataElementValue.count()==1
		typeDate.getValue(RawDataElementValue.list()[0].value, "[0].birth_date").getDateValue().equals(new SimpleDateFormat("dd-MM-yyyy").parse("15-08-1971"));
		typeDate.getValue(RawDataElementValue.list()[0].value, "[0].test").getStringValue() != "éééé"
	}

	def "get general import string data from csv"(){
		when:
		def type = Type.TYPE_STRING()
		def dataElement = newRawDataElement(CODE(7), type)
		def period = Period.list()[0];
		def csvString =
				"location_code,period_code,data_code,data_value,value_address\n"+
				BUTARO+","+period.code+","+dataElement.code+",value,\n"

		def importerErrorManager = new ImporterErrorManager();

		GeneralDataImporter importer = new GeneralDataImporter(
				locationService, valueService, dataService,
				sessionFactory, transactionManager,
				importerErrorManager, periodService
				);

		importer.importData("File Name",new CsvMapReader(new StringReader(csvString), CsvPreference.EXCEL_PREFERENCE))

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
		def period = Period.list()[0];
		def dataElementOne = newRawDataElement(CODE(8), type)
		def dataElementTwo = newRawDataElement(CODE(9), type)
		def dataElementThree = newRawDataElement(CODE(10), type)

		def csvString =
				"location_code,period_code,data_code,data_value,value_address\n"+
				BUTARO+","+period.code+","+dataElementOne.code+",TRUE,\n"+
				BUTARO+","+period.code+","+dataElementTwo.code+",FALSE,\n"+
				BUTARO+","+period.code+","+dataElementThree.code+",,\n"

		def importerErrorManager = new ImporterErrorManager();

		GeneralDataImporter importer = new GeneralDataImporter(
				locationService, valueService, dataService,
				sessionFactory, transactionManager,
				importerErrorManager, periodService
				);

		importer.importData("File Name",new CsvMapReader(new StringReader(csvString), CsvPreference.EXCEL_PREFERENCE))

		then:
		RawDataElementValue.count() == 3
		type.getValue(RawDataElementValue.list()[0].value, "").getBooleanValue().equals(true)
		type.getValue(RawDataElementValue.list()[1].value, "").getBooleanValue().equals(false)
		type.getValue(RawDataElementValue.list()[2].value, "").getBooleanValue()==null
		importerErrorManager.errors.size() == 1
	}

	def "get general import enum data from csv"(){
		when:
		def period = Period.list()[0];
		def enumeGender = newEnume("gender");
		def enumGenderOption1 = newEnumOption(enumeGender,"male");
		def enumGenderOption2 = newEnumOption(enumeGender,"female");
		def type = Type.TYPE_ENUM("gender")
		def dataElementOne = newRawDataElement(CODE(11), type)
		def dataElementTwo = newRawDataElement(CODE(12), type)

		def csvString =
				"location_code,period_code,data_code,data_value,value_address\n"+
				BUTARO+","+period.code+","+dataElementOne.code+",female,\n"+
				BUTARO+","+period.code+","+dataElementTwo.code+",Male,\n"

		def importerErrorManager = new ImporterErrorManager();
		GeneralDataImporter importer = new GeneralDataImporter(
				locationService, valueService, dataService,
				sessionFactory, transactionManager,
				importerErrorManager, periodService
				);

		importer.importData("File Name",new CsvMapReader(new StringReader(csvString), CsvPreference.EXCEL_PREFERENCE))

		then:
		importerErrorManager.errors.size() == 1
		EnumOption.findByValue(type.getValue(RawDataElementValue.findByData(dataElementOne).value, "").getEnumValue())!=null
		EnumOption.findByValue(type.getValue(RawDataElementValue.findByData(dataElementTwo).value, "").getEnumValue())==null
		Enum.findByCode("gender").enumOptions.contains(EnumOption.findByValue(type.getValue(RawDataElementValue.findByData(dataElementOne).value, "").getEnumValue()))
	}

	def "get general import number data from csv"(){
		when:
		def period = Period.list()[0];
		def type = Type.TYPE_NUMBER()
		def dataElementOne = newRawDataElement(CODE(13), type)
		def dataElementTwo = newRawDataElement(CODE(14), type)

		def csvString =
				"location_code,period_code,data_code,data_value,value_address\n"+
				BUTARO+","+period.code+","+dataElementOne.code+",zz,\n"+
				BUTARO+","+period.code+","+dataElementTwo.code+",44,\n"

		def importerErrorManager = new ImporterErrorManager();
		GeneralDataImporter importer = new GeneralDataImporter(
				locationService, valueService, dataService,
				sessionFactory, transactionManager,
				importerErrorManager, periodService
				);

		importer.importData("File Name",new CsvMapReader(new StringReader(csvString), CsvPreference.EXCEL_PREFERENCE))

		then:
		importerErrorManager.errors.size() == 1
		RawDataElementValue.list().size()==2
		type.getValue(RawDataElementValue.findByData(dataElementOne).value, "").getNumberValue()==null
		type.getValue(RawDataElementValue.findByData(dataElementTwo).value, "").getNumberValue()==44;
	}

	def "get general import date data from csv"(){
		when:
		def period = Period.list()[0];
		def type = Type.TYPE_DATE()
		def dataElementOne = newRawDataElement(CODE(15), type)
		def dataElementTwo = newRawDataElement(CODE(16), type)

		def csvString =
				"location_code,period_code,data_code,data_value,value_address\n"+
				BUTARO+","+period.code+","+dataElementOne.code+",15-08-1971,\n"+
				BUTARO+","+period.code+","+dataElementTwo.code+",44,\n"

		def importerErrorManager = new ImporterErrorManager();
		GeneralDataImporter importer = new GeneralDataImporter(
				locationService, valueService, dataService,
				sessionFactory, transactionManager,
				importerErrorManager, periodService
				);

		importer.importData("File Name",new CsvMapReader(new StringReader(csvString), CsvPreference.EXCEL_PREFERENCE))

		then:
		importerErrorManager.errors.size() == 1
		type.getValue(RawDataElementValue.findByData(dataElementOne).value, "").getDateValue().equals(new SimpleDateFormat("dd-MM-yyyy").parse("15-08-1971"));
		type.getValue(RawDataElementValue.findByData(dataElementTwo).value, "").getDateValue()==null
	}

	def "get general import wrong code, period and data_element from csv"(){
		when:
		def period = Period.list()[0];
		def type = Type.TYPE_STRING()
		def dataElement = newRawDataElement(CODE(17), type)
		def csvCodeString =
				"location_code,period_code,data_code,data_value,value_address\n"+
				"BURARO"+","+period.code+","+dataElement.code+",Text1,\n"+ //Wrong facility code
				KIVUYE+","+period.code+","+dataElement.code+",Text2,\n"+ //Correct row
				BUTARO+","+period.code+","+"xxxxxx"+",Text4,\n"+ //Code data element code
				BUTARO+","+"period"+","+dataElement.code+",Text3,\n" //Wrong Period code


		def importerErrorManager = new ImporterErrorManager();
		GeneralDataImporter importer = new GeneralDataImporter(
				locationService, valueService, dataService,
				sessionFactory, transactionManager,
				importerErrorManager, periodService
				);

		importer.importData("File Name",new CsvMapReader(new StringReader(csvCodeString), CsvPreference.EXCEL_PREFERENCE))

		then:
		RawDataElementValue.list().size()==1
		//check if the first row was skipped and the second was taken as the first and an error was saved
		RawDataElementValue.list()[0].value.equals(Value.VALUE_STRING("Text2"))
		importerErrorManager.errors.size() == 3
		//please change this error msg code if it is changed in ImporterService
		importerErrorManager.errors[0].messageCode.equals("import.error.message.unknown.data.location");
		importerErrorManager.errors[1].messageCode.equals("import.error.message.not.raw.data.element");
		importerErrorManager.errors[2].messageCode.equals("import.error.message.unknown.period");

	}

	def "general import data being override from csv"(){
		when:
		def period = Period.list()[0];
		def type = Type.TYPE_STRING()
		def dataElement = newRawDataElement(CODE(18), type)

		def csvString =
				"location_code,period_code,data_code,data_value,value_address\n"+
				BUTARO+","+period.code+","+dataElement.code+",overrideStringOne,\n"+
				BUTARO+","+period.code+","+dataElement.code+",overrideStringTwo,\n"

		def importerErrorManager = new ImporterErrorManager();
		GeneralDataImporter importer = new GeneralDataImporter(
				locationService, valueService, dataService,
				sessionFactory, transactionManager,
				importerErrorManager, periodService
				);
		importer.importData("File Name",new CsvMapReader(new StringReader(csvString), CsvPreference.EXCEL_PREFERENCE))

		then:
		importerErrorManager.errors.size() == 1
		RawDataElementValue.list().size()==1
		//check if the first element was override
		RawDataElementValue.list()[0].value.equals(Value.VALUE_STRING("overrideStringTwo"))
		//please change this error msg code if it is changed in ImporterService
		importerErrorManager.errors[0].messageCode.equals("import.error.message.data.duplicated");
	}

	def "general import nominative data with same value address"(){
		when:
		def period = Period.list()[0];
		def type = Type.TYPE_LIST(Type.TYPE_NUMBER())
		def dataElement = newRawDataElement(CODE(2), type)
		def csvString =
				"location_code,period_code,data_code,data_value,value_address\n"+
				BUTARO+","+period.code+","+dataElement.code+",1,[1]\n"+
				BUTARO+","+period.code+","+dataElement.code+",1,[0]\n";

		def importerErrorManager = new ImporterErrorManager();
		GeneralDataImporter importer = new GeneralDataImporter(
				locationService, valueService, dataService,
				sessionFactory, transactionManager,
				importerErrorManager, periodService
				);
		importer.importData("File Name",new CsvMapReader(new StringReader(csvString), CsvPreference.EXCEL_PREFERENCE))

		then:
		RawDataElementValue.list().size() == 1
		RawDataElementValue.list()[0].value.listValue.size() == 2
		RawDataElementValue.list()[0].value.listValue[0].numberValue == 1d
		RawDataElementValue.list()[0].value.listValue[1].numberValue == 1d
	}
	
	def "general import nominative with duplicate column in the file"(){
		when:
		def typeCode = Type.TYPE_LIST(Type.TYPE_MAP(["string": Type.TYPE_STRING()]))

		def csvCodeString =
				"location_code,string,string\n"+
				BUTARO+",best String,string\n"

		def dataCodeElement = newRawDataElement(CODE(6), typeCode)

		def importerErrorManagerCode = new ImporterErrorManager();

		NominativeDataImporter importer = new NominativeDataImporter(
				locationService, valueService, dataService,
				sessionFactory, transactionManager,
				importerErrorManagerCode, dataCodeElement, Period.list()[0]
				);
		importer.importData("File Name",new CsvMapReader(new StringReader(csvCodeString), CsvPreference.EXCEL_PREFERENCE))

		then:
		importerErrorManagerCode.errors.size() == 1
		//please change this error msg code if it is changed in ImporterService
		importerErrorManagerCode.errors[0].messageCode.equals("import.error.message.duplicate.column");
	}


	def "general import nominative data with same value address, but defferent data location from csv"(){
		when:
		def period = Period.list()[0];
		def type = Type.TYPE_LIST(Type.TYPE_MAP(["marital_status": Type.TYPE_BOOL()]))
		def dataElement = newRawDataElement(CODE(2), type)
		def csvString =
				"location_code,period_code,data_code,data_value,value_address\n"+
				BUTARO+","+period.code+","+dataElement.code+",TRUE,[0].marital_status\n"+
				BUTARO+","+period.code+","+dataElement.code+",,[2].marital_status\n"+
				BUTARO+","+period.code+","+dataElement.code+",FALSE,[1].marital_status\n"+
				KIVUYE+","+period.code+","+dataElement.code+",,[2].marital_status\n"+
				KIVUYE+","+period.code+","+dataElement.code+",TRUE,[0].marital_status\n"+
				KIVUYE+","+period.code+","+dataElement.code+",FALSE,[1].marital_status\n";

		def importerErrorManager = new ImporterErrorManager();
		GeneralDataImporter importer = new GeneralDataImporter(
				locationService, valueService, dataService,
				sessionFactory, transactionManager,
				importerErrorManager, periodService
				);
		importer.importData("File Name",new CsvMapReader(new StringReader(csvString), CsvPreference.EXCEL_PREFERENCE))

		then:
		RawDataElementValue.list().size() == 2
		importerErrorManager.errors.size() == 2
		RawDataElementValue.list()[0].value.listValue.size() == 3
		type.getValue(RawDataElementValue.list()[0].value, "[0].marital_status").getBooleanValue().equals(true)
		type.getValue(RawDataElementValue.list()[0].value, "[1].marital_status").getBooleanValue().equals(false)
		RawDataElementValue.list()[0].value.listValue[2].mapValue['marital_status'].isNull()
		RawDataElementValue.list()[1].value.listValue.size() == 3
		type.getValue(RawDataElementValue.list()[1].value, "[0].marital_status").getBooleanValue().equals(true)
		type.getValue(RawDataElementValue.list()[1].value, "[1].marital_status").getBooleanValue().equals(false)
		RawDataElementValue.list()[1].value.listValue[2].mapValue['marital_status'].isNull()
	}

	def "general import list data with skipped indexes"(){
		setup:
		def period = Period.list()[0];
		def type = Type.TYPE_LIST(Type.TYPE_MAP(["marital_status": Type.TYPE_BOOL()]))
		def dataElement = newRawDataElement(CODE(2), type)

		def importerErrorManager = new ImporterErrorManager();
		GeneralDataImporter importer = new GeneralDataImporter(
				locationService, valueService, dataService,
				sessionFactory, transactionManager,
				importerErrorManager, periodService
				);

		when: "first import, third line"
		def csvString =
				"location_code,period_code,data_code,data_value,value_address\n"+
				BUTARO+","+period.code+","+dataElement.code+",TRUE,[2].marital_status\n";

		importer.importData("File Name",new CsvMapReader(new StringReader(csvString), CsvPreference.EXCEL_PREFERENCE))

		then:
		RawDataElementValue.list().size() == 1
		importerErrorManager.errors.size() == 0
		RawDataElementValue.list()[0].value.listValue.size() == 3
		type.getValue(RawDataElementValue.list()[0].value, "[0]").mapValue['marital_status'].isNull()
		type.getValue(RawDataElementValue.list()[0].value, "[1]").mapValue['marital_status'].isNull()
		type.getValue(RawDataElementValue.list()[0].value, "[2].marital_status").getBooleanValue().equals(true)

		when: "second import, first line"
		csvString =
				"location_code,period_code,data_code,data_value,value_address\n"+
				BUTARO+","+period.code+","+dataElement.code+",TRUE,[0].marital_status\n";

		importer.importData("File Name",new CsvMapReader(new StringReader(csvString), CsvPreference.EXCEL_PREFERENCE))

		then:
		RawDataElementValue.list().size() == 1
		importerErrorManager.errors.size() == 0
		RawDataElementValue.list()[0].value.listValue.size() == 3
		type.getValue(RawDataElementValue.list()[0].value, "[0].marital_status").getBooleanValue().equals(true)
		type.getValue(RawDataElementValue.list()[0].value, "[1]").mapValue['marital_status'].isNull()
		type.getValue(RawDataElementValue.list()[0].value, "[2].marital_status").getBooleanValue().equals(true)
	}

	def "general import save after row containing an enume"(){
		when:
		def period = Period.list()[0];
		def enumeGender = newEnume("gendertype");
		def enumGenderOption1 = newEnumOption(enumeGender,"maletype");
		def enumGenderOption2 = newEnumOption(enumeGender,"femaletype");
		def type = Type.TYPE_LIST(Type.TYPE_MAP([
					"gender":  Type.TYPE_ENUM("gendertype"),
					"age":  Type.TYPE_NUMBER()
				]))
		def dataElement = newRawDataElement(CODE(66), type)

		def csvString =
				"location_code,period_code,data_code,data_value,value_address\n"+
				BUTARO+","+period.code+","+dataElement.code+",femaletype,[0].gender\n"+
				BUTARO+","+period.code+","+dataElement.code+",6,[0].age\n";

		def importerErrorManager = new ImporterErrorManager();
		GeneralDataImporter importer = new GeneralDataImporter(
				locationService, valueService, dataService,
				sessionFactory, transactionManager,
				importerErrorManager, periodService
				);

		importer.importData("File Name",new CsvMapReader(new StringReader(csvString), CsvPreference.EXCEL_PREFERENCE))

		then:
		EnumOption.findByValue(type.getValue(RawDataElementValue.list()[0].value, "[0].gender").getEnumValue())!=null
		type.getValue(RawDataElementValue.list()[0].value, "[0]").mapValue['age'].getNumberValue()==6
		importerErrorManager.errors.size() == 0
	}

	def "general import number line dont overlap "(){
		setup:
		def period = Period.list()[0];
		def type = Type.TYPE_LIST(Type.TYPE_MAP(["status": Type.TYPE_BOOL()]))
		def typeNumber = Type.TYPE_NUMBER()
		def dataElementOne = newRawDataElement(CODE(99), type)
		def dataElementTwo = newRawDataElement(CODE(88), typeNumber)
		
		when:
		def csvString =
				"location_code,period_code,data_code,data_value,value_address\n"+
				BUTARO+","+period.code+","+dataElementOne.code+",TRUE,[2].status\n"+
				BUTARO+","+period.code+","+dataElementOne.code+",yyy,[0].status\n"+
				KIVUYE+","+period.code+","+dataElementTwo.code+",yyy,\n"+
				BUTARO+","+period.code+","+dataElementTwo.code+",zz,\n";
	
	
		def importerErrorManager = new ImporterErrorManager();
		GeneralDataImporter importer = new GeneralDataImporter(
				locationService, valueService, dataService,
				sessionFactory, transactionManager,
				importerErrorManager, periodService
				);
			
		importer.importData("File Name",new CsvMapReader(new StringReader(csvString), CsvPreference.EXCEL_PREFERENCE))
		
		then:
		importerErrorManager.errors.size() == 3
		//Change message code if changed in messages.properties
		importerErrorManager.errors[0].messageCode.equals("import.error.message.boolean")
		importerErrorManager.errors[1].messageCode.equals("import.error.message.number")
		importerErrorManager.errors[2].messageCode.equals("import.error.message.number")
		
		

	}

}
