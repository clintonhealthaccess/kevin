package org.chai.kevin.binding;

import java.beans.PropertyEditorSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TranslationPropertyEditor extends PropertyEditorSupport {
	
	private static final Log log = LogFactory.getLog(TranslationPropertyEditor.class); 
	
	@Override
	public void setValue(Object value) {
		log.info("setValue(value="+value+")");
	}
	
	@Override
	public Object getValue() {
		log.info("getValue()");
		return null;
	}
}