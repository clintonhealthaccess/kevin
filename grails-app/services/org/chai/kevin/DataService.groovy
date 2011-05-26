package org.chai.kevin

import org.apache.commons.lang.StringUtils;
import org.chai.kevin.DataElement;

class DataService {

    static transactional = true

	def localeService
	
	DataElement getDataElement(Long id) {
		return DataElement.get(id)
	}
	
	Constant getConstant(Long id) {
		return Constant.get(id)
	}
	
	def searchConstants(String text) {
		def constants = Constant.list()
		StringUtils.split(text).each { chunk ->
			constants.retainAll { element ->
				DataService.matches(chunk, element.id+"")
				DataService.matches(chunk, element.names[localeService.getCurrentLanguage()]) ||
				DataService.matches(chunk, element.code) 
			}
		}
		return constants.sort {it.names[localeService.getCurrentLanguage()]}
	}
	
    def searchDataElements(String text) {
		def dataElements = DataElement.list();
//		if (dataSet == null) {
//			dataElements = DataElement.list();
//		}
//		else {
//			dataElements = dataSet.getDataElements();
//		}
		StringUtils.split(text).each { chunk ->
			dataElements.retainAll { element ->
				DataService.matches(chunk, element.id+"")
				DataService.matches(chunk, element.names[localeService.getCurrentLanguage()]) ||
				DataService.matches(chunk, element.code)
			}
		}
		return dataElements.sort {it.names[localeService.getCurrentLanguage()]}
    }
	
	private static boolean matches(String text, String value) {
		if (value == null) return false;
		return value.matches("(?i).*"+text+".*");
	}
	
//	def getDataSets() {
//		return DataSet.list().sort {it.name}
//	}
}
