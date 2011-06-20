package org.chai.kevin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.period.Period;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly=true)
public class DataValueService {

	private static final Log log = LogFactory.getLog(DataValueService.class);
	
	private SessionFactory sessionFactory;
	
	public DataValue getDataValue(DataElement dataElement, Period period, Organisation organisation) {
		if (log.isDebugEnabled()) log.debug("getDataValue(dataElement="+dataElement+", period="+period+", organisation="+organisation+")");
		
		 Criteria criteria = sessionFactory.getCurrentSession().createCriteria(DataValue.class)
         .add(Restrictions.naturalId()
        		 .set("dataElement", dataElement)
        		 .set("period", period)
        		 .set("organisationUnit", organisation.getOrganisationUnit())
         )
         .setCacheRegion("org.hibernate.cache.DataValueQueryCache")
         .setCacheable(true);

		 DataValue value = (DataValue)criteria.uniqueResult();
		 if (log.isDebugEnabled()) log.debug("getDataValue = "+value);
		 return value;
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
}
