package org.chai.kevin

import org.apache.commons.lang.StringUtils;
import org.hisp.dhis.dataelement.Constant;
import org.chai.kevin.DataElement;
import org.hisp.dhis.dataset.DataSet;

class DataService {

    static transactional = true

	DataElement getDataElement(Long id) {
		return DataElement.get(id)
	}
	
	
	def searchConstants(String text) {
		def constants = Constant.list()
		StringUtils.split(text).each { chunk ->
			constants.retainAll { element ->
				DataService.matches(chunk, element.name) ||
				DataService.matches(chunk, element.alternativeName) ||
				DataService.matches(chunk, element.code) ||
				DataService.matches(chunk, element.shortName)
			}
		}
		return constants.sort {it.name}
	}
	
    def searchDataElements(String text, DataSet dataSet) {
		def dataElements;
		if (dataSet == null) {
			dataElements = DataElement.list();
		}
		else {
			dataElements = dataSet.getDataElements();
		}
		StringUtils.split(text).each { chunk ->
			dataElements.retainAll { element ->
				DataService.matches(chunk, element.name) ||
				DataService.matches(chunk, element.alternativeName) ||
				DataService.matches(chunk, element.code) ||
				DataService.matches(chunk, element.shortName)
			}
		}
		return dataElements.sort {it.name}
    }
	
	private static boolean matches(String text, String value) {
		if (value == null) return false;
		return value.matches("(?i).*"+text+".*");
	}
	
	def getDataSets() {
		return DataSet.list().sort {it.name}
	}
}
