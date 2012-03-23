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
import org.chai.kevin.data.Enum;
import org.chai.kevin.data.EnumOption;

/**
* @author Jean Kahigiso M.
*
*/

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Type;
import org.chai.kevin.location.DataLocation
import org.chai.kevin.location.DataLocationType
import org.chai.kevin.location.Location
import org.chai.kevin.location.LocationLevel;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.value.Value;
import org.chai.kevin.LocationService;

class ImporterServiceSpec extends IntegrationTests {

	def importerService;
	
	def "get import hrh string data from csv"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		
		when:
		def type = Type.TYPE_LIST(Type.TYPE_MAP(["key1": Type.TYPE_STRING()]))
		def csvString = 
			"code,key1\n"+
			BUTARO+",value\n"
		def dataElement = newRawDataElement(CODE(1), type)
		def importerErrorManager = new ImporterErrorManager();
		importerService.importFile(dataElement, new StringReader(csvString), period,importerErrorManager)
			
		then:
		RawDataElementValue.count() == 1
		RawDataElementValue.list()[0].location.equals(DataLocation.findByCode(BUTARO))
		RawDataElementValue.list()[0].data.equals(RawDataElement.findByCode(CODE(1)))
		RawDataElementValue.list()[0].period.equals(period)
		RawDataElementValue.list()[0].value.equals(Value.VALUE_LIST([Value.VALUE_MAP(["key1":Value.VALUE_STRING("value")])]))
		importerErrorManager.errors.size() == 0 
	}
	
	def "get import hrh bool data from csv"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
			
		when:
		def typeBool = Type.TYPE_LIST(Type.TYPE_MAP(["marital_status": Type.TYPE_BOOL()]))
		
		def csvBoolString =
		"code,marital_status\n"+
		BUTARO+",0\n"+
		BUTARO+",1\n"+
		BUTARO+",N\n"
		
		def dataBoolElement = newRawDataElement(CODE(1), typeBool)
		def importerErrorManagerBool = new ImporterErrorManager();
		importerService.importFile(dataBoolElement, new StringReader(csvBoolString), period, importerErrorManagerBool)
			
		then:
		importerErrorManagerBool.errors.size() == 1
		typeBool.getValue(RawDataElementValue.list()[0].value, "[0].marital_status").getBooleanValue().equals(false)
		typeBool.getValue(RawDataElementValue.list()[0].value, "[1].marital_status").getBooleanValue().equals(true)
		typeBool.getValue(RawDataElementValue.list()[0].value, "[2].marital_status").isNull()
				
	}
	
	def "get import hrh date data from csv"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		
		when:
		def typeDate = Type.TYPE_LIST(Type.TYPE_MAP(["birth_date": Type.TYPE_DATE()]))
		def csvDateString =
		"code,birth_date\n"+
		BUTARO+",15-08-1971\n"
		def dataDateElement = newRawDataElement(CODE(1), typeDate)
		def importerErrorManagerDate = new ImporterErrorManager();
		importerService.importFile(dataDateElement, new StringReader(csvDateString), period, importerErrorManagerDate)
			
		then:
		importerErrorManagerDate.errors.size() == 0
		typeDate.getValue(RawDataElementValue.list()[0].value, "[0].birth_date").getDateValue().equals(new SimpleDateFormat("dd-MM-yyyy").parse("15-08-1971"));
	}
	
	def "get import hrh number data from csv"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
		
		when:
		def typeNumber = Type.TYPE_LIST(Type.TYPE_MAP(["age": Type.TYPE_NUMBER()]))
		
		def csvNumberString =
		"code,age\n"+
		BUTARO+",1\n"+
		BUTARO+",ff\n"
		
		
		def dataNumberElement = newRawDataElement(CODE(1), typeNumber)
		def importerErrorManagerNumber = new ImporterErrorManager();
		importerService.importFile(dataNumberElement, new StringReader(csvNumberString), period, importerErrorManagerNumber)
			
		then:
		importerErrorManagerNumber.errors.size() == 1
		typeNumber.getValue(RawDataElementValue.list()[0].value, "[0].age").getNumberValue()==1
		typeNumber.getValue(RawDataElementValue.list()[0].value, "[1].age").isNull();
	}
	
	
	
	def "get import hrh enum data from csv"(){
		setup:
		setupLocationTree()
		def period = newPeriod()
			
		when:
		def enumeGender = newEnume("gender");
		def enumGenderOption1 = newEnumOption(enumeGender,"male");
		def enumGenderOption2 = newEnumOption(enumeGender,"female");
		def typeEnum = Type.TYPE_LIST(Type.TYPE_MAP(["gender": Type.TYPE_ENUM("gender")]))
		
		def csvEnumString =
		"code,gender\n"+
		BUTARO+",Female\n"+
		BUTARO+",female\n"
		
		def dataEnumElement = newRawDataElement(CODE(1), typeEnum)
		def importerErrorManagerEnum = new ImporterErrorManager();
		importerService.importFile(dataEnumElement, new StringReader(csvEnumString), period, importerErrorManagerEnum)
			
		then:
		importerErrorManagerEnum.errors.size() == 1
		EnumOption.findByValue(typeEnum.getValue(RawDataElementValue.list()[0].value, "[0].gender").getEnumValue())==null
		Enum.findByCode("gender").enumOptions.contains(EnumOption.findByValue(typeEnum.getValue(RawDataElementValue.list()[0].value, "[1].gender").getEnumValue()))
	}
	
	def "get import hrh wrong code from csv"(){
		
		setup:
		setupLocationTree()
		def period = newPeriod()
		
		when:
		def typeCode = Type.TYPE_LIST(Type.TYPE_MAP(["string": Type.TYPE_STRING()]))
		
		def csvCodeString =
		"code,string\n"+
		"uuu,Test-String\n"+
		BUTARO+",best String\n"
		
		def dataCodeElement = newRawDataElement(CODE(1), typeCode)
		def importerErrorManagerCode = new ImporterErrorManager();
		importerService.importFile(dataCodeElement, new StringReader(csvCodeString), period, importerErrorManagerCode)
			
		then:
		importerErrorManagerCode.errors.size() == 1
		//check if the first row was skipped and the second was taken as the first and an error was save
		RawDataElementValue.list()[0].value.equals(Value.VALUE_LIST([Value.VALUE_MAP(["string":Value.VALUE_STRING("best String")])]))
		//please change this error msg code if it is changed in ImporterService
		importerErrorManagerCode.errors[0].messageCode.equals("import.error.message.unknown.location");
	}
		
}
