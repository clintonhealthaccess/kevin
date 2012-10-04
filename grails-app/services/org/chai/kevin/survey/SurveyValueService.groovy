package org.chai.kevin.survey;

import javax.persistence.Entity;

import org.apache.shiro.SecurityUtils
import org.chai.kevin.LocationService
import org.chai.kevin.data.RawDataElement
import org.chai.kevin.form.FormElement;
import org.chai.kevin.form.FormEnteredValue;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.survey.validation.SurveyEnteredProgram
import org.chai.kevin.survey.validation.SurveyEnteredQuestion
import org.chai.kevin.survey.validation.SurveyEnteredSection
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
	
	void deleteEnteredValues(FormElement element) {
		sessionFactory.getCurrentSession()
		.createQuery("delete from FormEnteredValue where formElement = :formElement")
		.setParameter("formElement", element)
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
	
	Integer getNumberOfSurveyEnteredPrograms(Survey survey, DataLocation dataLocation, Boolean closed, Boolean complete, Boolean invalid) {
		def c = SurveyEnteredProgram.createCriteria()
		c.add(Restrictions.eq("dataLocation", dataLocation))
		
		if (complete!=null) c.add(Restrictions.eq("complete", complete))
		if (invalid!=null) c.add(Restrictions.eq("invalid", invalid))
		if (closed!=null) c.add(Restrictions.eq("closed", closed))
		
		c.createAlias("program", "o").add(Restrictions.eq('o.survey', survey))
		c.setProjection(Projections.rowCount())
		c.setCacheable(false);
		c.setFlushMode(FlushMode.COMMIT)
		c.uniqueResult();
	}
	
//	Integer getNumberOfSurveyEnteredQuestions(Survey survey, DataLocation dataLocation, 
//		SurveyProgram program, SurveySection section, Boolean complete, Boolean invalid, Boolean skippedAsComplete) {
//		def c = SurveyEnteredQuestion.createCriteria()
//		c.add(Restrictions.eq("dataLocation", dataLocation))
//		
//		if (complete!=null) {
//			if (skippedAsComplete!=null) {
//				def or = Restrictions.disjunction();
//				or.add(Restrictions.eq("complete", complete))
//				if (!skippedAsComplete) or.add(Restrictions.isEmpty("skippedRules"))
//				else or.add(Restrictions.isNotEmpty("skippedRules"))
//				c.add(or)
//			}
//			else {
//				c.add(Restrictions.eq("complete", complete))
//			}
//		}
//		if (invalid!=null) c.add(Restrictions.eq("invalid", invalid))
//		
//		
//		c.createAlias("question", "sq")
//		.createAlias("sq.section", "ss")
//		.createAlias("ss.program", "so")
//		.add(Restrictions.eq("so.survey", survey))
//		
//		if (section != null) c.add(Restrictions.eq("sq.section", section))
//		if (program != null) c.add(Restrictions.eq("ss.program", program))
//		
//		c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
//		c.setProjection(Projections.rowCount())
//		c.setCacheable(false);
//		c.setFlushMode(FlushMode.COMMIT)
//		c.uniqueResult();
//	}
	
	SurveyEnteredSection getSurveyEnteredSection(SurveySection surveySection, DataLocation dataLocation) {
		def c = SurveyEnteredSection.createCriteria()
		c.add(Restrictions.naturalId()
			.set("dataLocation", dataLocation)
			.set("section", surveySection)
		)
		
		c.setFlushMode(FlushMode.COMMIT)
		def result = c.uniqueResult();
		if (log.isDebugEnabled()) log.debug("getSurveyEnteredSection(...)="+result);
		return result
	}
	
	SurveyEnteredProgram getSurveyEnteredProgram(SurveyProgram surveyProgram, DataLocation dataLocation) {
		def c = SurveyEnteredProgram.createCriteria()
		c.add(Restrictions.naturalId()
			.set("dataLocation", dataLocation)
			.set("program", surveyProgram)
		)
		c.setFlushMode(FlushMode.COMMIT)
		
		def result = c.uniqueResult();
		if (log.isDebugEnabled()) log.debug("getSurveyEnteredProgram(...)="+result);
		return result
	}

	SurveyEnteredQuestion getOrCreateSurveyEnteredQuestion(DataLocation dataLocation, SurveyQuestion surveyQuestion) {
		SurveyEnteredQuestion enteredQuestion = getSurveyEnteredQuestion(surveyQuestion, dataLocation);
		if (enteredQuestion == null) {
			enteredQuestion = new SurveyEnteredQuestion(surveyQuestion, dataLocation, false, false);
		}
		return enteredQuestion;
	}
	
	SurveyEnteredQuestion getSurveyEnteredQuestion(SurveyQuestion surveyQuestion, DataLocation dataLocation) {
		def c = SurveyEnteredQuestion.createCriteria()
		c.add(Restrictions.naturalId()
			.set("dataLocation", dataLocation)
			.set("question", surveyQuestion)
		)
		
		c.setFlushMode(FlushMode.COMMIT)
		def result = c.uniqueResult();
		if (log.isDebugEnabled()) log.debug("getSurveyEnteredQuestion(...)="+result);
		return result
	}
	
	List<FormEnteredValue> getFormEnteredValues(DataLocation dataLocation, SurveySection section, SurveyProgram program, Survey survey) {
		def c = FormEnteredValue.createCriteria()
		c.add(Restrictions.eq("dataLocation", dataLocation))
		
		if (survey != null || program != null || section != null) c.createAlias("formElement", "se")
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
