package org.chai.task;

import i18nfields.I18nFieldsHelper;

import java.util.Map;
import org.chai.task.Task.TaskStatus;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.chai.kevin.exports.CalculationExport;
import org.chai.kevin.exports.DataElementExport;
import org.chai.kevin.exports.DataExport;
import org.chai.kevin.security.User;

class DataExportTask extends Task {

	Long exportId
	
	def languageService
	def calculationExportService
	def dataElementExportService

	private def getExport() {
		def export = DataElementExport.get(exportId)
		if (export == null) export = CalculationExport.get(exportId)
		return export
	}
		
	def executeTask() {
		def csvFile = null
		Task.withTransaction {
			def export = getExport()
			if (export != null) {
				def user = User.findByUuid(principal)
				def language = user.defaultLanguage != null ? user.defaultLanguage : languageService.fallbackLanguage
				
				def previousLocale = I18nFieldsHelper.getLocale()
				I18nFieldsHelper.setLocale(new Locale(language))

				if (export instanceof DataElementExport) csvFile = dataElementExportService.exportData(export)
				else if (export instanceof CalculationExport) csvFile = calculationExportService.exportData(export)
				else {} // TODO exception
				
				I18nFieldsHelper.setLocale(previousLocale)
				
			}
		}
		if (csvFile != null) {
			File outputFile = new File(getFolder(), getOutputFilename())
			FileUtils.moveFile(csvFile, outputFile);
		}
	}
	
	boolean isUnique() {
		def task = DataExportTask.findByExportId(exportId)
		return task == null || task.status == TaskStatus.COMPLETED || task.status == TaskStatus.ABORTED
	}
	
	String getOutputFilename() {
		return 'exportOutput.csv'
	}
	
	String getInformation() {
		return getExport().descriptions
	}

	String getFormView() {
		return null
	}
	
	Map getFormModel() {
		return null
	}

}
