package org.chai.kevin.survey;

import javax.persistence.Entity;

import org.apache.shiro.SecurityUtils
import org.chai.kevin.LocationService
import org.chai.kevin.data.RawDataElement
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.survey.validation.SurveyEnteredProgram
import org.chai.kevin.survey.validation.SurveyEnteredQuestion
import org.chai.kevin.survey.validation.SurveyEnteredSection
import org.chai.kevin.survey.validation.SurveyEnteredValue
import org.hibernate.Criteria
import org.hibernate.FlushMode
import org.hibernate.criterion.Projections
import org.hibernate.criterion.Restrictions


class SurveyValueService {
	
	static transactional = true
	
	def sessionFactory;
	
	private LocationService locationService;
	
	void save(SurveyEnteredProgram surveyEnteredProgram) {
		if (log.isDebugEnabled()) log.debug("save(surveyEnteredProgram=${surveyEnteredProgram}})")
		surveyEnteredProgram.setUserUuid(SecurityUtils.subject.principal)
		surveyEnteredProgram.setTimestamp(new Date());
		surveyEnteredProgram.save();
	}

	void delete(SurveyEnteredProgram surveyEnteredProgram) {
		surveyEnteredProgram.delete()
	}
	
	void save(SurveyEnteredValue surveyEnteredValue) {
		if (log.isDebugEnabled()) log.debug("save(surveyEnteredValue=${surveyEnteredValue}})")
		surveyEnteredValue.setUserUuid(SecurityUtils.subject.principal)
		surveyEnteredValue.setTimestamp(new Date());
		surveyEnteredValue.save();
	}
	
	void delete(SurveyEnteredValue surveyEnteredValue) {
		surveyEnteredValue.delete()
	}
	
	void deleteEnteredValues(SurveyElement element) {
		sessionFactory.getCurrentSession()
		.createQuery("delete from SurveyEnteredValue where surveyElement = :surveyElement")
		.setParameter("surveyElement", element)
		.executeUpdate();
	}
	
	void save(SurveyEnteredQuestion surveyEnteredQuestion) {
		if (log.isDebugEnabled()) log.debug("save(surveyEnteredQuestion=${surveyEnteredQuestion}})")
		surveyEnteredQuestion.setUserUuid(SecurityUtils.subject.principal)
		surveyEnteredQuestion.setTimestamp(new Date());
		surveyEnteredQuestion.save();
	}
	
	void delete(SurveyEnteredQuestion surveyEnteredQuestion) {
		surveyEnteredQuestion.delete()
	}
	
	void save(SurveyEnteredSection surveyEnteredSection) {
		if (log.isDebugEnabled()) log.debug("save(surveyEnteredSection=${surveyEnteredSection}})")
		surveyEnteredSection.setUserUuid(SecurityUtils.subject.principal)
		surveyEnteredSection.setTimestamp(new Date());
		surveyEnteredSection.save();
	}
	
	void delete(SurveyEnteredSection surveyEnteredSection) {
		surveyEnteredSection.delete()
	}
	
	Integer getNumberOfSurveyEnteredPrograms(Survey survey, DataLocationEntity entity, Boolean closed, Boolean complete, Boolean invalid) {
		def c = SurveyEnteredProgram.createCriteria()
		c.add(Restrictions.eq("entity", entity))
		
		if (complete!=null) c.add(Restrictions.eq("complete", complete))
		if (invalid!=null) c.add(Restrictions.eq("invalid", invalid))
		if (closed!=null) c.add(Restrictions.eq("closed", closed))
		
		c.createAlias("program", "o").add(Restrictions.eq('o.survey', survey))
		c.setProjection(Projections.rowCount())
		c.setCacheable(false);
		c.setFlushMode(FlushMode.COMMIT)
		c.uniqueResult();
	}
	
	Integer getNumberOfSurveyEnteredQuestions(Survey survey, DataLocationEntity entity, 
		SurveyProgram program, SurveySection section, Boolean complete, Boolean invalid, Boolean skippedAsComplete) {
		def c = SurveyEnteredQuestion.createCriteria()
		c.add(Restrictions.eq("entity", entity))
		
		if (complete!=null) {
			if (skippedAsComplete!=null) {
				def or = Restrictions.disjunction();
				or.add(Restrictions.eq("complete", complete))
				if (!skippedAsComplete) or.add(Restrictions.isEmpty("skippedRules"))
				else or.add(Restrictions.isNotEmpty("skippedRules"))
				c.add(or)
			}
			else {
				c.add(Restrictions.eq("complete", complete))
			}
		}
		if (invalid!=null) c.add(Restrictions.eq("invalid", invalid))
		
		
		c.createAlias("question", "sq")
		.createAlias("sq.section", "ss")
		.createAlias("ss.program", "so")
		.add(Restrictions.eq("so.survey", survey))
		
		if (section != null) c.add(Restrictions.eq("sq.section", section))
		if (program != null) c.add(Restrictions.eq("ss.program", program))
		
		c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
		c.setProjection(Projections.rowCount())
		c.setCacheable(false);
		c.setFlushMode(FlushMode.COMMIT)
		c.uniqueResult();
	}
	
	SurveyEnteredSection getSurveyEnteredSection(SurveySection surveySection, DataLocationEntity entity) {
		def c = SurveyEnteredSection.createCriteria()
		c.add(Restrictions.naturalId()
			.set("entity", entity)
			.set("section", surveySection)
		)
		
		c.setFlushMode(FlushMode.COMMIT)
		def result = c.uniqueResult();
		if (log.isDebugEnabled()) log.debug("getSurveyEnteredSection(...)="+result);
		return result
	}
	
	SurveyEnteredProgram getSurveyEnteredProgram(SurveyProgram surveyProgram, DataLocationEntity entity) {
		def c = SurveyEnteredProgram.createCriteria()
		c.add(Restrictions.naturalId()
			.set("entity", entity)
			.set("program", surveyProgram)
		)
		c.setFlushMode(FlushMode.COMMIT)
		
		def result = c.uniqueResult();
		if (log.isDebugEnabled()) log.debug("getSurveyEnteredProgram(...)="+result);
		return result
	}

	SurveyEnteredQuestion getSurveyEnteredQuestion(SurveyQuestion surveyQuestion, DataLocationEntity entity) {
		def c = SurveyEnteredQuestion.createCriteria()
		c.add(Restrictions.naturalId()
			.set("entity", entity)
			.set("question", surveyQuestion)
		)
		
		c.setFlushMode(FlushMode.COMMIT)
		def result = c.uniqueResult();
		if (log.isDebugEnabled()) log.debug("getSurveyEnteredQuestion(...)="+result);
		return result
	}
	
	SurveyEnteredValue getSurveyEnteredValue(SurveyElement surveyElement, DataLocationEntity entity) {
		def c = SurveyEnteredValue.createCriteria()
		c.add(Restrictions.naturalId()
			.set("entity", entity)
			.set("surveyElement", surveyElement)
		)
		c.setCacheable(true)
		c.setCacheRegion("surveyEnteredValueQueryCache")
		
		c.setFlushMode(FlushMode.COMMIT)
		def result = c.uniqueResult();
		if (log.isDebugEnabled()) log.debug("getSurveyEnteredValue(...)="+result);
		return result
	}
	
	List<SurveyEnteredValue> getSurveyEnteredValues(DataLocationEntity entity, SurveySection section, SurveyProgram program, Survey survey) {
		def c = SurveyEnteredValue.createCriteria()
		c.add(Restrictions.eq("entity", entity))
		
		if (survey != null || program != null || section != null) c.createAlias("surveyElement", "se")
		if (survey != null || program != null || section != null) c.createAlias("se.surveyQuestion", "sq")
		if (survey != null || program != null) c.createAlias("sq.section", "ss")
		if (survey != null) c.createAlias("ss.program", "so")

		if (section != null) c.add(Restrictions.eq("sq.section", section))
		if (program != null) c.add(Restrictions.eq("ss.program", program))
		if (survey != null) c.add(Restrictions.eq("so.survey", survey))
		
		def result = c.setFlushMode(FlushMode.COMMIT).list();
		if (log.isDebugEnabled()) log.debug("getSurveyEnteredValue(...)="+result);
		return result				
	}

}
