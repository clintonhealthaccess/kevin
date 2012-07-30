package org.chai.kevin.task

import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.imports.DataImporter;
import org.chai.kevin.imports.FileImporter;
import org.chai.kevin.imports.ImporterErrorManager;
import org.chai.kevin.imports.NominativeDataImporter;
import org.chai.kevin.Period;

class NominativeImportTask extends ImportTask {

	def locationService;
	def valueService;
	def dataService;
	def sessionFactory;
	def transactionManager;
	
	Long rawDataElementId
	Long periodId
	
	String getInformation() {
		def period = Period.get(periodId)
		def rawDataElement = RawDataElement.get(rawDataElementId)
		
		return message(code: 'period.label') + ': '+period.code + ', '+message(code: 'rawdataelement.label')+': '+rawDataElement.code+'<br/>'+message(code:'import.file.label')+': '+getInputFilename()
	}
	
	FileImporter getImporter(ImporterErrorManager errorManager) {
		def period = Period.get(periodId)
		def rawDataElement = RawDataElement.get(rawDataElementId)
		
		if (period != null && rawDataElement != null) {
			return new NominativeDataImporter(
				locationService, valueService, dataService,
				sessionFactory, transactionManager,
				errorManager, rawDataElement, period
			);
		}
		else return null
	}
	
	String getFormView() {
		return 'nominativeImport'	
	}
	
	Map getFormModel() {
		List<RawDataElement> dataElements = []
		def rawDataElement = RawDataElement.get(rawDataElementId)
		if (rawDataElement != null) dataElements << rawDataElement
		
		return [
			periods: Period.list([cache: true]),
			dataElements: dataElements,
			task: this
		]
	}
	
	static constraints = {
		periodId(blank:false, nullable:false)
		rawDataElementId(blank:false, nullable:false)
	}
}
