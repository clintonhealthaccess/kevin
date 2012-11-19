package org.chai.kevin.survey;

import javax.persistence.Entity;

import org.apache.shiro.SecurityUtils
import org.chai.location.LocationService
import org.chai.kevin.data.RawDataElement
import org.chai.kevin.form.FormElement;
import org.chai.kevin.form.FormEnteredValue;
import org.chai.location.DataLocation;
import org.hibernate.Criteria
import org.hibernate.FlushMode
import org.hibernate.Query;
import org.hibernate.criterion.Projections
import org.hibernate.criterion.Restrictions


class SurveyValueService {
	
	static transactional = true
	
	def sessionFactory;
	
	private LocationService locationService;
	
	void save(SurveyEnteredProgram surveyEnteredProgram) {
		if (log.isDebugEnabled()) log.debug("save(surveyEnteredProgram=${surveyEnteredProgram}})")
		surveyEnteredProgram.save(failOnError: true);
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
		surveyEnteredQuestion.save(failOnError: true);
	}
	
	void delete(SurveyEnteredQuestion surveyEnteredQuestion) {
		surveyEnteredQuestion.delete()
	}
	
	void save(SurveyEnteredSection surveyEnteredSection) {
		if (log.isDebugEnabled()) log.debug("save(surveyEnteredSection=${surveyEnteredSection}})")
		surveyEnteredSection.save(failOnError: true);
	}
	
	void delete(SurveyEnteredSection surveyEnteredSection) {
		surveyEnteredSection.delete()
	}
	
	void deleteEnteredQuestions(DataLocation dataLocation) {
		String queryString = "delete from SurveyEnteredQuestion where dataLocation = :dataLocation";
		Query query = sessionFactory.getCurrentSession().createQuery(queryString);
		query.setParameter("dataLocation", dataLocation);
		query.executeUpdate();
	}
	
	void deleteEnteredSections(DataLocation dataLocation) {
		String queryString = "delete from SurveyEnteredSection where dataLocation = :dataLocation";
		Query query = sessionFactory.getCurrentSession().createQuery(queryString);
		query.setParameter("dataLocation", dataLocation);
		query.executeUpdate();
	}
	
	void deleteEnteredPrograms(DataLocation dataLocation) {
		String queryString = "delete from SurveyEnteredProgram where dataLocation = :dataLocation";
		Query query = sessionFactory.getCurrentSession().createQuery(queryString);
		query.setParameter("dataLocation", dataLocation);
		query.executeUpdate();
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
		if (survey != null || program != null || section != null) c.createAlias("se.question", "sq")
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
