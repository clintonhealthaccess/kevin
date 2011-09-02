package org.chai.kevin.survey

import org.chai.kevin.DataService;
import org.chai.kevin.OrganisationService
import org.chai.kevin.data.DataElement
import org.chai.kevin.survey.validation.SurveyEnteredObjective
import org.chai.kevin.survey.validation.SurveyEnteredValue
import org.chai.kevin.survey.validation.SurveySkipRule
import org.hibernate.FlushMode;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.organisationunit.OrganisationUnit
import org.hisp.dhis.organisationunit.OrganisationUnitGroup
import org.apache.commons.lang.math.NumberUtils;



class SurveyElementService {
	def localeService;
	OrganisationService organisationService;
	DataService dataService;

	SurveyElement getSurveyElement(Long id) {
		return SurveyElement.get(id)
	}

	void save(SurveyEnteredObjective surveyEnteredObjective) {
		if (log.isDebugEnabled()) log.debug("save(surveyEnteredObjective=${surveyEnteredObjective}})")
		surveyEnteredObjective.save();
	}

	void save(SurveyEnteredValue surveyEnteredValue) {
		if (log.isDebugEnabled()) log.debug("save(surveyEnteredValue=${surveyEnteredValue}})")
		surveyEnteredValue.save();
	}

	void delete(SurveyEnteredValue surveyEnteredValue) {
		surveyEnteredValue.delete();
	}

	SurveyEnteredObjective getSurveyEnteredObjective(SurveyObjective surveyObjective, OrganisationUnit organisationUnit) {
		def c = SurveyEnteredObjective.createCriteria()
		c.add(Restrictions.naturalId()
				.set("organisationUnit", organisationUnit)
				.set("objective", surveyObjective)
				)
				.setCacheable(true)
				.setFlushMode(FlushMode.MANUAL)
				.setCacheRegion("org.hibernate.cache.SurveyEnteredObjectiveQueryCache")
				.uniqueResult();
	}

	SurveyEnteredValue getSurveyEnteredValue(SurveyElement surveyElement, OrganisationUnit organisationUnit) {
		def c = SurveyEnteredValue.createCriteria()
		c.add(Restrictions.naturalId()
				.set("organisationUnit", organisationUnit)
				.set("surveyElement", surveyElement)
				)
				.setCacheable(true)
				.setFlushMode(FlushMode.MANUAL)
				.setCacheRegion("org.hibernate.cache.SurveyEnteredValueQueryCache")
				.uniqueResult();
	}

	Set<SurveySkipRule> getSkipRules(SurveyElement surveyElement) {
		def c = SurveySkipRule.createCriteria()
				.createAlias("skippedSurveyElements", "se")
				.add(Restrictions.eq("se.id", surveyElement.id))
				.list();
	}

	Set<SurveySkipRule> getSkipRules(SurveyQuestion surveyQuestion) {
		def c = SurveySkipRule.createCriteria()
				.createAlias("skippedSurveyQuestions", "sq")
				.add(Restrictions.eq("sq.id", surveyQuestion.id))
				.list();
	}


	Set<SurveyElement> getSurveyElements(DataElement dataElement) {
		SurveyElement.createCriteria()
				.add(Restrictions.eq("dataElement", dataElement))
				.list();
	}

	Integer getTotalOrgUnitApplicable(SurveyElement surveyElement){
		Set<String> uuIds = surveyElement.getOrganisationUnitGroupApplicable();
		Integer number = 0;
		for(String uuId: uuIds)
			number = number+organisationService.getNumberOfOrganisationForGroup(OrganisationUnitGroup.findByUuid(uuId));
		return number;
	}

	Set<SurveyElement> searchSurveyElements(String text, Survey survey) {
		Set<SurveyElement> surveyElements = new HashSet<SurveyElement>();
		List<DataElement> dataElements = dataService.searchDataElements(text);

		for(DataElement dataElement : dataElements)
			if(!survey){
				surveyElements.addAll(this.getSurveyElements(dataElement));
			}else{
				for(SurveyElement surveyElement: this.getSurveyElements(dataElement))
					if(surveyElement.surveyQuestion.section.objective.survey==survey)
						surveyElements.add(surveyElement);
			}

		return surveyElements.sort {
			it.dataElement.names[localeService.getCurrentLanguage()]
		}
	}
}
