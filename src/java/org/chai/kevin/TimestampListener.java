package org.chai.kevin;

/* 
 * Copyright (c) 2011, Clinton Health Access Initiative.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
