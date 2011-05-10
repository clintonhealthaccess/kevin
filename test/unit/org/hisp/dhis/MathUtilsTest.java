package org.hisp.dhis;

import grails.test.GrailsUnitTestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.system.util.MathUtils;
import org.nfunk.jep.JEP;

public class MathUtilsTest extends GrailsUnitTestCase {
	
	Log log = LogFactory.getLog(MathUtilsTest.class); 

	public void testValidIfExpression() {
		String expression = "if(1==1,1,1)";
		
		assertFalse(MathUtils.expressionHasErrors(expression));
	}
	
	public void testValidIfExpressionJEP() {
		String expression = "if(1==1,1,1)";

        final JEP parser = new JEP();
        parser.addStandardFunctions();
        parser.parseExpression( expression );
        
        log.debug(parser.getErrorInfo());
        assertFalse(parser.hasError());
	}
	
	public void testValidExpression() {
		String expression = "1";
		
		assertFalse(MathUtils.expressionHasErrors(expression));		
	}
	
	public void testValidExpression2() {
		String expression = "if(valid1==\"valid1\",1,1)";
		
		assertTrue(MathUtils.expressionHasErrors(expression));		
	}
	
	public void testValidStringExpression() {
		String expression = "if(1==1,\"Test\",\"0\")";
		
		final JEP parser = new JEP();
    	parser.addStandardConstants();
    	parser.addStandardFunctions();
    	parser.parseExpression(expression);
    	
		assertEquals("Test", parser.getValueAsObject());		
	}
	
	
}
