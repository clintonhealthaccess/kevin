package org.chai.kevin.survey;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.DataElement;
import org.chai.kevin.DataValue;
import org.chai.kevin.ExpressionService;
import org.chai.kevin.Organisation;
import org.chai.kevin.OrganisationService;
import org.hisp.dhis.period.Period;
import org.springframework.transaction.annotation.Transactional;

public class SurveyService {
	private Log log = LogFactory.getLog(SurveyService.class);
	private OrganisationService organisationService;
	private ExpressionService expressionService;
	private Integer organisationLevel;

	@Transactional(readOnly = true)
	public SurveyPage getSurvey(Period currentPeriod,
			Organisation currentOrganisation, SurveySubSection currentSection) {
		Map<SurveyQuestion, Map<DataElement, DataValue>> values = null;
		log.info("====>currentPeriod" + currentPeriod
				+ "====>currentOrganisation" + currentOrganisation
				+ "====>All Sections:" + currentSection);

//		if (currentSection != null) {
//			for (SurveySubSection subSection : currentSection.getSubSections()) {
//				Map<DataElement, DataValue> dataElementValue = null;
//				for (SurveyQuestion question : subSection.getQuestions()) {
//					if (question.getTemplate().equals("singleQuestion")) {
//						// dataElementValue.put((SurveySingleQuestion)question.,
//						// arg1);
//					}
//					if (question.getTemplate().equals("checkboxQuestion")) {
//
//					}
//					if (question.getTemplate().equals("tableQuestion")) {
//
//					}
//				}
//
//			}
//		}

		log.info("====>currentPeriod" + currentPeriod
				+ "====>currentOrganisation" + currentOrganisation
				+ "====>All Sections:" + currentSection);
		return new SurveyPage(currentPeriod, currentOrganisation,
				currentSection, values);
	}

	public void setOrganisationService(OrganisationService organisationService) {
		this.organisationService = organisationService;
	}

	public OrganisationService getOrganisationService() {
		return organisationService;
	}

	public void setExpressionService(ExpressionService expressionService) {
		this.expressionService = expressionService;
	}

	public ExpressionService getExpressionService() {
		return expressionService;
	}

	public void setOrganisationLevel(Integer organisationLevel) {
		this.organisationLevel = organisationLevel;
	}

	public Integer getOrganisationLevel() {
		return organisationLevel;
	}

}
