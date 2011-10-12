package org.chai.kevin.survey;

import org.chai.kevin.data.Type.ValueType;
import org.chai.kevin.data.Type;
import org.chai.kevin.value.Value;
import org.chai.kevin.DataService
import org.chai.kevin.GroupCollection;
import org.chai.kevin.LocaleService
import org.chai.kevin.OrganisationService
import org.chai.kevin.data.DataElement
import org.chai.kevin.data.Type.PrefixPredicate;
import org.chai.kevin.survey.validation.SurveyEnteredObjective
import org.chai.kevin.survey.validation.SurveyEnteredQuestion
import org.chai.kevin.survey.validation.SurveyEnteredSection
import org.chai.kevin.survey.validation.SurveyEnteredValue
import org.hibernate.Criteria
import org.hibernate.FlushMode
import org.hibernate.criterion.MatchMode
import org.hibernate.criterion.Projections
import org.hibernate.criterion.Restrictions
import org.hisp.dhis.organisationunit.OrganisationUnit
import org.hisp.dhis.organisationunit.OrganisationUnitGroup


class SurveyElementService {
	
	static transactional = true
	
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
	
	Integer getNumberOfSurveyEnteredObjectives(Survey survey, OrganisationUnit organisationUnit, Boolean closed, Boolean complete, Boolean invalid) {
		def c = SurveyEnteredObjective.createCriteria()
		c.add(Restrictions.eq("organisationUnit", organisationUnit))
		
		if (complete!=null) c.add(Restrictions.eq("complete", complete))
		if (invalid!=null) c.add(Restrictions.eq("invalid", invalid))
		if (closed!=null) c.add(Restrictions.eq("closed", closed))
		
		c.createAlias("objective", "o").add(Restrictions.eq('o.survey', survey))
		c.setProjection(Projections.rowCount())
		c.setCacheable(false);
		c.setFlushMode(FlushMode.COMMIT)
		c.uniqueResult();
	}
	
	Integer getNumberOfSurveyEnteredQuestions(Survey survey, OrganisationUnit organisationUnit, 
		SurveyObjective objective, SurveySection section, Boolean complete, Boolean invalid) {
		def c = SurveyEnteredQuestion.createCriteria()
		c.add(Restrictions.eq("organisationUnit", organisationUnit))
		
		if (complete!=null) c.add(Restrictions.eq("complete", complete))
		if (invalid!=null) c.add(Restrictions.eq("invalid", invalid))
		
		c.createAlias("question", "sq")
		.createAlias("sq.section", "ss")
		.createAlias("ss.objective", "so")
		.add(Restrictions.eq("so.survey", survey))
		
		if (section != null) c.add(Restrictions.eq("sq.section", section))
		if (objective != null) c.add(Restrictions.eq("ss.objective", objective))
		
		c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
		c.setProjection(Projections.rowCount())
		c.setCacheable(false);
		c.setFlushMode(FlushMode.COMMIT)
		c.uniqueResult();
	}
	
	SurveyEnteredSection getSurveyEnteredSection(SurveySection surveySection, OrganisationUnit organisationUnit) {
		def c = SurveyEnteredSection.createCriteria()
		c.add(Restrictions.naturalId()
			.set("organisationUnit", organisationUnit)
			.set("section", surveySection)
		)
		
		c.setFlushMode(FlushMode.COMMIT)
		def result = c.uniqueResult();
		if (log.isDebugEnabled()) log.debug("getSurveyEnteredSection(...)="+result);
		return result
	}
	
	SurveyEnteredObjective getSurveyEnteredObjective(SurveyObjective surveyObjective, OrganisationUnit organisationUnit) {
		def c = SurveyEnteredObjective.createCriteria()
		c.add(Restrictions.naturalId()
			.set("organisationUnit", organisationUnit)
			.set("objective", surveyObjective)
		)
		c.setFlushMode(FlushMode.COMMIT)
		
		def result = c.uniqueResult();
		if (log.isDebugEnabled()) log.debug("getSurveyEnteredObjective(...)="+result);
		return result
	}

	SurveyEnteredQuestion getSurveyEnteredQuestion(SurveyQuestion surveyQuestion, OrganisationUnit organisationUnit) {
		def c = SurveyEnteredQuestion.createCriteria()
		c.add(Restrictions.naturalId()
			.set("organisationUnit", organisationUnit)
			.set("question", surveyQuestion)
		)
		
		c.setFlushMode(FlushMode.COMMIT)
		def result = c.uniqueResult();
		if (log.isDebugEnabled()) log.debug("getSurveyEnteredQuestion(...)="+result);
		return result
	}
	
	SurveyEnteredValue getSurveyEnteredValue(SurveyElement surveyElement, OrganisationUnit organisationUnit) {
		def c = SurveyEnteredValue.createCriteria()
		c.add(Restrictions.naturalId()
			.set("organisationUnit", organisationUnit)
			.set("surveyElement", surveyElement)
		)
		c.setCacheable(true)
		c.setCacheRegion("surveyEnteredValueQueryCache")
		
		c.setFlushMode(FlushMode.COMMIT)
		def result = c.uniqueResult();
		if (log.isDebugEnabled()) log.debug("getSurveyEnteredValue(...)="+result);
		return result
	}

	Set<SurveyValidationRule> searchValidationRules(SurveyElement surveyElement, String groupUuid) {
		if (log.isDebugEnabled()) log.debug("searchValidationRules(surveyElement="+surveyElement+", groupUuid="+groupUuid+")");
		
		def c = SurveyValidationRule.createCriteria()
		c.add(Restrictions.like("expression", "\$${surveyElement.id}", MatchMode.ANYWHERE))
		c.add(Restrictions.like("groupUuidString", groupUuid, MatchMode.ANYWHERE))
		
		List<SurveyValidationRule> rules = c.setFlushMode(FlushMode.COMMIT).list()
		return filter(rules, surveyElement);
	}
	
	Set<SurveySkipRule> searchSkipRules(SurveyElement surveyElement) {
		if (log.isDebugEnabled()) log.debug("searchSkipRules(surveyElement="+surveyElement+")");
		
		def c = SurveySkipRule.createCriteria()
		c.add(Restrictions.like("expression", "\$${surveyElement.id}", MatchMode.ANYWHERE))
		
		List<SurveySkipRule> rules = c.setFlushMode(FlushMode.COMMIT).list()
		return filter(rules, surveyElement);
	}
	
	
	static def filter(def rules, def element) {
		return rules.findAll { rule ->
			return rule.expression.matches(".*\\\$"+element.id+"(\\z|\\D|\$).*")
		}
	}
	
	Set<SurveyElement> getSurveyElements(DataElement dataElement, Survey survey) {
		def c = SurveyElement.createCriteria()
		if (survey != null) {
			c.createAlias("surveyQuestion", "sq")
			.createAlias("sq.section", "ss")
			.createAlias("ss.objective", "so")
			.add(Restrictions.eq("so.survey", survey))
		}
		c.add(Restrictions.eq("dataElement", dataElement))
		
		return c.setFlushMode(FlushMode.COMMIT).list()
	}

	List<SurveyElement> searchSurveyElements(String text, Survey survey, List<String> allowedTypes) {
		List<SurveyElement> surveyElements = new ArrayList<SurveyElement>();
		List<DataElement> dataElements = dataService.searchData(DataElement.class, text, allowedTypes, [:]);

		for(DataElement dataElement : dataElements) {
			surveyElements.addAll(this.getSurveyElements(dataElement, survey));
		}

		return surveyElements.sort {
			it.dataElement.names[localeService.getCurrentLanguage()]
		}
	}
	
	Integer getNumberOfOrganisationUnitApplicable(SurveyElement surveyElement) {
		Set<String> groupUuids = surveyElement.getOrganisationUnitGroupApplicable();
		int number = 0;
		for (String groupUuid : groupUuids) {
			OrganisationUnitGroup group = organisationService.getOrganisationUnitGroup(groupUuid);
			if (group != null) number += organisationService.getNumberOfOrganisationForGroup(group)
		}
		return number;
	}
	
	Set<String> getHeaderPrefixes(SurveyElement element) {
		
		element.getDataElement().getType().getPrefixes(null, new PrefixPredicate() {
			@Override
			public boolean holds(Type type, Value value, String prefix) {
				if (getParent() != null && getParent().getType() == ValueType.MAP) return true;
			}
		}).keySet();
		
	}

}
