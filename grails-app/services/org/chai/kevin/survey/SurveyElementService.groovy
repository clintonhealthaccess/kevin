package org.chai.kevin.survey;

import org.hibernate.criterion.MatchMode;
import org.chai.kevin.DataService;
import org.chai.kevin.OrganisationService;
import org.chai.kevin.LocaleService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.chai.kevin.Organisation;
import org.chai.kevin.OrganisationService;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.survey.validation.SurveyEnteredObjective;
import org.chai.kevin.survey.validation.SurveyEnteredValue;
import org.chai.kevin.value.ExpressionValue;
import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.chai.kevin.util.Utils;
import org.chai.kevin.data.DataElement
import org.chai.kevin.survey.validation.SurveyEnteredObjective
import org.chai.kevin.survey.validation.SurveyEnteredQuestion;
import org.chai.kevin.survey.validation.SurveyEnteredSection
import org.chai.kevin.survey.validation.SurveyEnteredValue
import org.chai.kevin.survey.validation.SurveyEnteredObjective.ObjectiveStatus;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions
import org.apache.commons.lang.math.NumberUtils;


class SurveyElementService {
	
	LocaleService localeService;
	OrganisationService organisationService;
	DataService dataService;
	def sessionFactory;
	
	SurveyElement getSurveyElement(Long id) {
		return SurveyElement.get(id)
	}

	SurveyQuestion getSurveyQuestion(Long id) {
		// TODO test this with Grails 2.0
		return sessionFactory.currentSession.get(SurveyQuestion.class, id)
	}
	
	void save(SurveyEnteredObjective surveyEnteredObjective) {
		if (log.isDebugEnabled()) log.debug("save(surveyEnteredObjective=${surveyEnteredObjective}})")
		surveyEnteredObjective.save();
	}

	void save(SurveyEnteredValue surveyEnteredValue) {
		if (log.isDebugEnabled()) log.debug("save(surveyEnteredValue=${surveyEnteredValue}})")
		surveyEnteredValue.save();
	}
	
	void save(SurveyEnteredQuestion surveyEnteredQuestion) {
		if (log.isDebugEnabled()) log.debug("save(surveyEnteredQuestion=${surveyEnteredQuestion}})")
		surveyEnteredQuestion.save();
	}
	
	void save(SurveyEnteredSection surveyEnteredSection) {
		surveyEnteredSection.save();
	}
	
	Integer getNumberOfSurveyEnteredObjectives(Survey survey, OrganisationUnit organisationUnit, ObjectiveStatus status) {
		def c = SurveyEnteredObjective.createCriteria()
		c.add(Restrictions.eq("organisationUnit", organisationUnit))
		if (status != null) c.add(Restrictions.eq("status", status))
		c.createAlias("objective", "o").add(Restrictions.eq('o.survey', survey))
		c.setProjection(Projections.rowCount())
		c.setFlushMode(FlushMode.MANUAL).uniqueResult();
	}
	
	Integer getNumberOfSurveyEnteredValues(Survey survey, OrganisationUnit organisationUnit, SurveyObjective objective, SurveySection section) {
		def c = SurveyEnteredValue.createCriteria()
		c.add(Restrictions.eq("organisationUnit", organisationUnit))
		c.add(Restrictions.or(
			Restrictions.eq("skipped", true),
			Restrictions.isNotNull("value"))
		)
		
		c.createAlias("surveyElement", "se")
		.createAlias("se.surveyQuestion", "sq")
		.createAlias("sq.section", "ss")
		.createAlias("ss.objective", "so")
		.add(Restrictions.eq("so.survey", survey))
		
		if (section != null) c.add(Restrictions.eq("sq.section", section))
		if (objective != null) c.add(Restrictions.eq("ss.objective", objective))
		
		c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
		c.setProjection(Projections.rowCount())
		c.setFlushMode(FlushMode.MANUAL).uniqueResult();
	}
	
	SurveyEnteredSection getSurveyEnteredSection(SurveySection surveySection, OrganisationUnit organisationUnit) {
		def c = SurveyEnteredSection.createCriteria()
		c.add(Restrictions.naturalId()
			.set("organisationUnit", organisationUnit)
			.set("section", surveySection)
		)
		.setCacheable(true)
		.setFlushMode(FlushMode.MANUAL)
		.setCacheRegion("org.hibernate.cache.SurveyEnteredSectioneQueryCache")
		.uniqueResult();
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

	SurveyEnteredQuestion getSurveyEnteredQuestion(SurveyQuestion surveyQuestion, OrganisationUnit organisationUnit) {
		def c = SurveyEnteredQuestion.createCriteria()
		c.add(Restrictions.naturalId()
			.set("organisationUnit", organisationUnit)
			.set("question", surveyQuestion)
		)
		.setCacheable(true)
		.setFlushMode(FlushMode.MANUAL)
		.setCacheRegion("org.hibernate.cache.SurveyEnteredQuestionQueryCache")
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

	Set<SurveyValidationRule> searchValidationRules(SurveyElement surveyElement) {
		def c = SurveyValidationRule.createCriteria()
		c.add(
			Restrictions.like("expression", "\$${surveyElement.id}", MatchMode.ANYWHERE)
		)
		.setCacheable(true)
		.setFlushMode(FlushMode.MANUAL)
		.setCacheRegion("org.hibernate.cache.SurveyValidationRuleSearch")
		.list()
	}
	
	Set<SurveySkipRule> searchSkipRules(SurveyElement surveyElement) {
		def c = SurveySkipRule.createCriteria()
		c.add(
			Restrictions.like("expression", "\$${surveyElement.id}", MatchMode.ANYWHERE)
		)
		.setCacheable(true)
		.setFlushMode(FlushMode.MANUAL)
		.setCacheRegion("org.hibernate.cache.SurveySkipRuleSearch")
		.list()
	}
	
//	Set<SurveySkipRule> getSkipRules(SurveyElement surveyElement) {
//		Survey survey = surveyElement.getSurvey();
//		Set<SurveySkipRule> result = new HashSet<SurveySkipRule>();
//		survey.skipRules.each { rule ->
//			if (rule.skippedSurveyElements.contains(surveyElement)) result.add(rule)	
//		}
//		return result;
//	}

//	Set<SurveySkipRule> getSkipRules(SurveyQuestion surveyQuestion) {
//		Survey survey = surveyQuestion.getSurvey();
//		Set<SurveySkipRule> result = new HashSet<SurveySkipRule>();
//		survey.skipRules.each { rule ->
//			if (rule.skippedSurveyQuestions.contains(surveyQuestion)) result.add(rule)
//		}
//		return result;
//	}

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

		for(DataElement dataElement : dataElements) {
			if(!survey) {
				surveyElements.addAll(this.getSurveyElements(dataElement));
			}
			else {
				for(SurveyElement surveyElement: this.getSurveyElements(dataElement)) {
					if(surveyElement.surveyQuestion.section.objective.survey==survey) surveyElements.add(surveyElement);
				}
			}
		}

		return surveyElements.sort {
			it.dataElement.names[localeService.getCurrentLanguage()]
		}
	}

}
