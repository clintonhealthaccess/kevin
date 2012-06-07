package org.chai.kevin.exports

import org.chai.kevin.AbstractController;
import org.chai.kevin.location.CalculationLocation;
import org.chai.kevin.survey.Survey;
import org.chai.kevin.survey.SurveyProgram;
import org.chai.kevin.survey.SurveySection;
import org.chai.kevin.util.Utils;

class SurveyExportController extends AbstractController {

	def locationService
	def surveyExportService
	
	def export = {
		CalculationLocation location = locationService.getCalculationLocation(params.int('location'), CalculationLocation.class)
		SurveySection section = SurveySection.get(params.int('section'))
		SurveyProgram program = SurveyProgram.get(params.int('program'))
		Survey survey = Survey.get(params.int('survey'))

		String filename = surveyExportService.getExportFilename(location, section, program, survey);
		File csvFile = surveyExportService.getSurveyExportFile(filename, location, section, program, survey);
		def zipFile = Utils.getZipFile(csvFile, filename)
			
		if(zipFile.exists()){
			response.setHeader("Content-disposition", "attachment; filename=" + zipFile.getName());
			response.setContentType("application/zip");
			response.setHeader("Content-length", zipFile.length().toString());
			response.outputStream << zipFile.newInputStream()
		}
	}
	
}
