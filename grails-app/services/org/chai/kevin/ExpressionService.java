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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.value.CalculationValue;
import org.chai.kevin.value.DataValue;
import org.chai.kevin.value.ExpressionValue;
import org.chai.kevin.value.ExpressionValue.Status;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.system.util.MathUtils;
import org.nfunk.jep.JEP;
import org.springframework.transaction.annotation.Transactional;

public class ExpressionService {

	private static final Log log = LogFactory.getLog(ExpressionService.class);
	
    private final Pattern FORMULA_PATTERN = Pattern.compile("\\[.+?\\]");
    private final Pattern CONSTANT_PATTERN = Pattern.compile("\\[c.+?\\]");
	
	private DataService dataService;
	private OrganisationService organisationService;
	private ValueService valueService;

	/**
	 * 
	 * @param calculation
	 * @param period
	 * @param organisation
	 * @return
	 */
	@Transactional(readOnly=true)
	public Map<Organisation, ExpressionValue> calculateExpressionValues(Calculation calculation, Period period, Organisation organisation) {
		Map<Organisation, ExpressionValue> result = new HashMap<Organisation, ExpressionValue>();
		List<Organisation> organisations = organisationService.getChildrenOfLevel(organisation, organisationService.getFacilityLevel());
		
		for (Organisation child : organisations) {
			// TODO use group collection ?
			organisationService.loadGroup(child);
			
			ExpressionValue expressionValue = null;
			Expression expression = getMatchingExpression(calculation, child);
			if (expression != null) { 
				expressionValue = valueService.getExpressionValue(child.getOrganisationUnit(), expression, period);
			}
			result.put(child, expressionValue);
		}
		return result;
	}
	
	/**
	 * Returns the value or null if it is not aggregated and the organisation has missing values
	 * 
	 * If all data elements in the expression are aggregatable, the aggregated value at this organisation
	 * is returned.
	 * 
	 * If one of the data elements in the expression is not aggregatable, the average value of all the organisation's
	 * children is returned.
	 * 
	 * 
	 * @param expression
	 * @param period
	 * @param organisation
	 * @param values
	 * @return
	 */
	// TODO decide if this can be called with a facility, 
	@Transactional(readOnly=true)
	public CalculationValue calculateValues(Calculation calculation, Period period, Organisation organisation) {
		if (log.isDebugEnabled()) log.debug("getValues(calculation="+calculation+",period="+period+",organisation="+organisation+")");
		
		Map<Organisation, ExpressionValue> result = calculateExpressionValues(calculation, period, organisation);
		CalculationValue calculationValue = new CalculationValue(calculation, organisation.getOrganisationUnit(), period, result);
		
		if (log.isDebugEnabled()) log.debug("getValues(...)="+calculationValue);
		return calculationValue;
	}
	
	/**
	 * 
	 * @param expression
	 * @param period
	 * @param organisation
	 * @return
	 */
	@Transactional(readOnly=true)
	public Map<Organisation, Map<DataElement, DataValue>> calculateDataValues(Expression expression, Period period, Organisation organisation) {
		Map<Organisation, Map<DataElement, DataValue>> values = new HashMap<Organisation, Map<DataElement,DataValue>>();
		generateExpression(expression.getExpression(), period, organisation, values);
		return values;
	}
	
	/**
	 * The expression has to be aggregatable for this to work
	 * 
	 * @param expression
	 * @param period
	 * @param organisation
	 * @param valuesForOrganisation
	 * @return
	 */
	@Transactional(readOnly=true)
	public ExpressionValue calculateValue(Expression expression, Period period, Organisation organisation) {
		if (log.isDebugEnabled()) log.debug("getValue(expression="+expression+",period="+period+",organisation="+organisation+")");
		
		String value = null;
		Status status = null;
		if (!isAggregatable(expression) && organisationService.getLevel(organisation) != organisationService.getFacilityLevel()) {
			status = Status.NOT_AGGREGATABLE;
			value = null;
		}
		else {
			
			Map<Organisation, Map<DataElement, DataValue>> values = new HashMap<Organisation, Map<DataElement, DataValue>>();
			String stringExpression = generateExpression(expression.getExpression(), period, organisation, values);
	
			if (values.containsKey(organisation) && hasNullValues(values.get(organisation).values())) {
				// this means we got non-aggregated values and some of them are null, we don't calculate anything
				value = null;
				status = Status.MISSING_VALUE;
			}
			else {
				Object evaluatedValue = evaluate(stringExpression);
				if (evaluatedValue == null) {
					if (log.isErrorEnabled()) log.error("evaluated value is null but there are no null values: "+stringExpression);
					value = null;
					// TODO add ERROR status
					status = Status.MISSING_VALUE;
				}
				else {
					value = evaluate(stringExpression).toString();
					status = Status.VALID;
				}
			}
		}
		ExpressionValue expressionValue = new ExpressionValue(value, status, organisation.getOrganisationUnit(), expression, period);
			
		if (log.isDebugEnabled()) log.debug("getValue()="+expressionValue);
		return expressionValue;
		
	}

	private boolean isAggregatable(Expression expression) {
		Set<DataElement> elements = getDataElementsInExpression(expression.getExpression());
		for (DataElement dataElement : elements) {
			if (!dataElement.isAggregatable()) return false;
		}
		return true;
	}

	public Expression getMatchingExpression(Calculation calculation, Organisation organisation) {
		OrganisationUnitGroup group = organisation.getOrganisationUnitGroup();
		if (log.isDebugEnabled()) log.debug("group on organisation: "+group);
		if (log.isDebugEnabled()) log.debug("groups on calculations: "+calculation.getExpressions().keySet());

		Expression expression = calculation.getExpressions().get(group.getUuid());
		if (log.isDebugEnabled()) log.debug("found matching expression: "+expression);
		return expression;
	}
	
	
	private Object calculateDataValue(DataElement dataElement, Period period, Organisation organisation, Map<Organisation, Map<DataElement, DataValue>> values) {
		if (log.isDebugEnabled()) log.debug("getDataValue(dataElement="+dataElement+", period="+period+", organisation="+organisation+")");
		
		Object result = null;
		// TODO this should be decided otherwise, 
		// or defined by the user
		if (!dataElement.isAggregatable() || organisationService.getLevel(organisation) == organisationService.getFacilityLevel()) {
			Map<DataElement, DataValue> valuesForOrganisation = new HashMap<DataElement, DataValue>();
			values.put(organisation, valuesForOrganisation);

			DataValue dataValue = valueService.getDataValue(dataElement, period, organisation);
			valuesForOrganisation.put(dataElement, dataValue);
			if (dataValue != null) result = dataValue.getValue();
		}
		else {
			List<Organisation> children = organisationService.getChildrenOfLevel(organisation, organisationService.getFacilityLevel());
			Double value = 0d;
			for (Organisation child : children) {
				Map<DataElement, DataValue> valuesForOrganisation = new HashMap<DataElement, DataValue>();
				values.put(child, valuesForOrganisation);
				DataValue dataValue = valueService.getDataValue(dataElement, period, child);
				valuesForOrganisation.put(dataElement, dataValue);
				if (dataValue != null) {
					value += Double.parseDouble(dataValue.getValue());
				}
			}
			result = value;
		}
		if (log.isDebugEnabled()) log.debug("getDataValue(...)="+result);
		return result;
	}
	
	private String generateExpression(String formula, Period period, Organisation organisation, Map<Organisation, Map<DataElement, DataValue>> values) {
		if (log.isDebugEnabled()) log.debug("generateExpression(formula="+formula+", period="+period+", organisation="+organisation+")");
		
		StringBuffer buffer = new StringBuffer();

		Matcher matcher = CONSTANT_PATTERN.matcher(formula);
		while (matcher.find()) {
			String match = matcher.group();
			match = match.replaceAll("[\\[c\\]]", "");
			
			Object value = null;
			try {
				Constant constant = dataService.getConstant(Long.parseLong(match));
				// TODO constant does not exist
				value = constant.getValue();
			}
			catch (NumberFormatException e) {
				log.warn("parse exception: "+match, e);
			}
			
			matcher.appendReplacement(buffer, String.valueOf(value));
			// TODO check this
//				values.put(constant, value);
		}

		matcher.appendTail(buffer);
		matcher = FORMULA_PATTERN.matcher(buffer.toString());

		buffer = new StringBuffer();
		
		while (matcher.find()) {
			String match = matcher.group();
			match = match.replaceAll("[\\[\\]]", "");

			Object value = null;
			try {
				DataElement dataElement = dataService.getDataElement(Long.parseLong(match));
				// TODO data element does not exist
				// TODO if aggregated, then get data value only for the 
				// organisations that have all the values ?
				value = calculateDataValue(dataElement, period, organisation, values);
			}
			catch (NumberFormatException e) {
				log.warn("parse exception: "+match, e);
			}

			matcher.appendReplacement(buffer, String.valueOf(value));
		}

		matcher.appendTail(buffer);
		return buffer.toString();
	}
	
	@Transactional(readOnly=false)
	public void refreshExpressions() {
		for (ExpressionValue expressionValue : valueService.getOutdatedExpressions()) {
			ExpressionValue newValue = calculateValue(expressionValue.getExpression(), expressionValue.getPeriod(), organisationService.getOrganisation(expressionValue.getOrganisationUnit().getId()));
			expressionValue.setStatus(newValue.getStatus());
			expressionValue.setValue(newValue.getValue());
			valueService.save(expressionValue);
		}
		for (ExpressionValue expressionValue : valueService.getNonCalculatedExpressions()) {
			ExpressionValue newValue = calculateValue(expressionValue.getExpression(), expressionValue.getPeriod(), organisationService.getOrganisation(expressionValue.getOrganisationUnit().getId()));
			valueService.save(newValue);
		}
	}
	
	@Transactional(readOnly=false)
	public void refreshCalculations() {
		for (CalculationValue calculationValue : valueService.getOutdatedCalculations()) {
			CalculationValue newValue = calculateValues(calculationValue.getCalculation(), calculationValue.getPeriod(), organisationService.getOrganisation(calculationValue.getOrganisationUnit().getId()));
			calculationValue.setAverage(newValue.getAverage());
			calculationValue.setHasMissingExpression(newValue.getHasMissingExpression());
			calculationValue.setHasMissingValues(newValue.getHasMissingValues());
			valueService.save(calculationValue);
		}
		for (CalculationValue calculationValue : valueService.getNonCalculatedCalculations()) {
			CalculationValue newValue = calculateValues(calculationValue.getCalculation(), calculationValue.getPeriod(), organisationService.getOrganisation(calculationValue.getOrganisationUnit().getId()));
			valueService.save(newValue);
		}
	}
	
	private static JEP getJEPParser() {
    	final JEP parser = new JEP();
    	parser.addStandardConstants();
    	parser.addStandardFunctions();
    	return parser;
    }
	
	private static Object evaluate(String expression) {
		if (log.isDebugEnabled()) log.debug("evaluate(expression="+expression+")");
		JEP parser = getJEPParser();
        
        parser.parseExpression( expression );
        Object value = parser.getValueAsObject();
        if (log.isDebugEnabled()) log.debug("evaluate(...)="+value);
        return value;
	}

	
	private static <T extends Object> boolean hasNullValues(Collection<T> values) {
		for (Object object : values) {
			if (object == null) return true;
		}
		return false;
	}

    public String expressionIsValid(String formula) {
        if (formula == null) return org.hisp.dhis.expression.ExpressionService.EXPRESSION_IS_EMPTY;
        StringBuffer buffer = new StringBuffer();
        
		Matcher matcher = CONSTANT_PATTERN.matcher(formula);
		while (matcher.find()) {
			String match = matcher.group();
			match = match.replaceAll("[\\[c\\]]", "");
			try {
				if ( dataService.getConstant( Long.parseLong(match)) == null ) {
	                return "constant_does_not_exist";
				}
			}
			catch (NumberFormatException e) {
				return org.hisp.dhis.expression.ExpressionService.ID_NOT_NUMERIC;
			}
			matcher.appendReplacement(buffer, "1.1");
		}
		matcher.appendTail(buffer);
        matcher = FORMULA_PATTERN.matcher(buffer.toString());
		buffer = new StringBuffer();
        while ( matcher.find() )
        {
        	String match = matcher.group();
        	match = match.replaceAll("[\\[\\]]", "");
            try {
            	if ( dataService.getDataElement(Long.parseLong(match)) == null) {
            		return org.hisp.dhis.expression.ExpressionService.DATAELEMENT_DOES_NOT_EXIST;
            	}
            }
			catch (NumberFormatException e) {
				return org.hisp.dhis.expression.ExpressionService.ID_NOT_NUMERIC;
			}
            matcher.appendReplacement( buffer, "1.1" );
        }
        
        matcher.appendTail( buffer );
        if ( MathUtils.expressionHasErrors( buffer.toString() ) ) {
            return org.hisp.dhis.expression.ExpressionService.EXPRESSION_NOT_WELL_FORMED;
        }        

        return org.hisp.dhis.expression.ExpressionService.VALID;
    }
	
    
    public Set<DataElement> getDataElementsInExpression( String expression ) {
        Set<DataElement> dataElementsInExpression = null;
        if ( expression != null ) {
            dataElementsInExpression = new HashSet<DataElement>();
            final Matcher matcher = FORMULA_PATTERN.matcher( expression );
            
            while (matcher.find())  {
            	String match = matcher.group();
            	match = match.replaceAll("[\\[\\]]", "");
            	
            	if (!match.startsWith("c")) {
            		
	                DataElement dataElement = null;
	                try {
	                	dataElement = dataService.getDataElement(Long.parseLong(match));
	                }
	                catch (NumberFormatException e) {
	                	log.warn("wrong format for dataelement: "+match);
	                }
	
	                if ( dataElement != null )  {
	                    dataElementsInExpression.add( dataElement );
	                }
            	}
            }
        }
        return dataElementsInExpression;
    }

    public String convertStringExpression(String expression, Map<Long, String> mapping) {
        StringBuffer convertedFormula = new StringBuffer();
        
        if ( expression != null ) {
            final Matcher matcher = FORMULA_PATTERN.matcher( expression );

            while (matcher.find()) {
            	String match = matcher.group();
            	match = match.replaceAll("[\\[\\]]", "");
            	
                if (match == null) {
                	if (log.isInfoEnabled()) log.info( "Data element identifier refers to non-existing object: " + match );
                }
                else {
                	match = mapping.get(Long.parseLong(match));
                }
                matcher.appendReplacement( convertedFormula, match );
            }
            matcher.appendTail( convertedFormula );
        }
        return convertedFormula.toString();
    }
	
	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}
	
	public void setOrganisationService(OrganisationService organisationService) {
		this.organisationService = organisationService;
	}

	public void setValueService(ValueService valueService) {
		this.valueService = valueService;
	}
}
