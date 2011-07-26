package org.chai.kevin.survey

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.chai.kevin.survey.validation.SurveyEnteredObjective;
import org.chai.kevin.survey.validation.SurveyEnteredValue;
import org.chai.kevin.survey.validation.SurveySkipRule;
import org.chai.kevin.value.ExpressionValue;
import org.hibernate.FlushMode;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

class SurveyElementService {

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
//		.createAlias("skippedSurveyElements", "se")
//		.add(Restrictions.idEq("se"))
//		c.add(Restrictions.)
//		.
	}
	
//	Set<SurveySkipRule> getSkipRules(SurveyQuestion surveyQuestion) {
//		
//	}

}
