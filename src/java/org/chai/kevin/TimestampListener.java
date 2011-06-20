package org.chai.kevin;

import java.util.Date;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.EntityMode;
import org.hibernate.event.AbstractEvent;
import org.hibernate.event.AbstractPreDatabaseOperationEvent;
import org.hibernate.event.PreInsertEvent;
import org.hibernate.event.PreInsertEventListener;
import org.hibernate.event.PreUpdateEvent;
import org.hibernate.event.PreUpdateEventListener;

public class TimestampListener implements PreUpdateEventListener, PreInsertEventListener {

	private static final Log log = LogFactory.getLog(TimestampListener.class);
	
	private static final long serialVersionUID = -1401730506021256875L;

	private void updateTimestamp(AbstractPreDatabaseOperationEvent event, Object[] state) {
		Object object = event.getEntity();
		if (object instanceof Timestamped) {
			Timestamped entity = (Timestamped) object;
			String[] propertyNames = event.getPersister().getEntityMetamodel().getPropertyNames();
			Date date = new Date();
			entity.setTimestamp(date);
			int index = ArrayUtils.indexOf(propertyNames, "timestamp");
			event.getPersister().setPropertyValue(event.getEntity(), index, date, EntityMode.POJO);
		}
	}

	@Override
	public boolean onPreInsert(PreInsertEvent arg0) {
		updateTimestamp(arg0, arg0.getState());
		return false;
	}

	@Override
	public boolean onPreUpdate(PreUpdateEvent arg0) {
		updateTimestamp(arg0, arg0.getState());
		return false;
	}
	
}
