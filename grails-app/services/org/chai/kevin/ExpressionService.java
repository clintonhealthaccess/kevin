package org.chai.kevin;

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
import org.chai.kevin.DataElement;
import org.chai.kevin.DataValue;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.system.util.MathUtils;
import org.nfunk.jep.JEP;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly=true)
public class ExpressionService {

	private static final Log log = LogFactory.getLog(ExpressionService.class);
	
    private final Pattern FORMULA_PATTERN = Pattern.compile("\\[.+?\\]");
    private final Pattern CONSTANT_PATTERN = Pattern.compile("\\[c.+?\\]");
	
    final String NULL_REPLACEMENT = "0";

	private DataService dataService;
	private DataValueService dataValueService;
	private OrganisationService organisationService;
	
	private Integer organisationLevel;
	
	private static JEP getJEPParser() {
    	final JEP parser = new JEP();
    	parser.addStandardConstants();
    	parser.addStandardFunctions();
    	return parser;
    }
	
	private static Object evaluate(String expression) {
		log.debug("evaluate(expression="+expression+")");
		JEP parser = getJEPParser();
        
        parser.parseExpression( expression );
        Object value = parser.getValueAsObject();
        log.debug("evaluate(...)="+value);
        return value;
	}
	
	/**
	 * Returns the value or null if it is not aggregated and the organisation has missing values
	 * 
	 * @param expression
	 * @param period
	 * @param organisation
	 * @param valuesForOrganisation
	 * @return
	 */
	public Object getValue(Expression expression, Period period, Organisation organisation, Map<DataElement, Object> valuesForOrganisation) {
		if (log.isDebugEnabled()) log.debug("getValue(expression="+expression+",period="+period+",organisation="+organisation+")");
		
		Map<Organisation, Map<DataElement, Object>> values = new HashMap<Organisation, Map<DataElement,Object>>();
		String stringExpression = generateExpression(expression.getExpression(), period, organisation, values);

		if (values.containsKey(organisation)) {
			if (valuesForOrganisation.size() > 1) log.error("getting value of one organisation but it was aggregated somehow");
			valuesForOrganisation.putAll(values.get(organisation));
		}
		else {
			log.error("no organisation was added");
		}

		if (stringExpression == null) return null;
		return evaluate(stringExpression);
	}
	
	/**
	 * Returns the value or null if it is not aggregated and the organisation has missing values
	 * 
	 * @param expression
	 * @param period
	 * @param organisation
	 * @param values
	 * @return
	 */
	public Double getAggregatedValue(Expression expression, Period period, Organisation organisation, Map<Organisation, Map<DataElement, Object>> values) {
		if (log.isDebugEnabled()) log.debug("getAggregatedValue(expression="+expression+",period="+period+",organisation="+organisation+")");
		
		Set<DataElement> elements = getDataElementsInExpression(expression.getExpression());
		
		// TODO maybe we want to calculate this in another way,
		// or let the user decide
		boolean aggregatable = true;
		for (DataElement dataElement : elements) {
			if (!dataElement.isAggregatable()) aggregatable = false;
		}
		
		Double value;
		
		if (aggregatable) {
			Map<DataElement, Object> valuesForOrganisation = new HashMap<DataElement, Object>();
			values.put(organisation, valuesForOrganisation);
			value = (Double)getValue(expression, period, organisation, valuesForOrganisation);
		}
		else {
			value = getAverageValue(expression, period, organisation, values);
		}
		return value;
	}
		
		
	private Double getAverageValue(Expression expression, Period period, Organisation organisation, Map<Organisation, Map<DataElement, Object>> values) {
		if (log.isDebugEnabled()) log.debug("getAverageValue(expression="+expression+",period="+period+",organisation="+organisation+")");
		organisationService.getLevel(organisation);
		
		Double value;
		if (organisation.getLevel() == organisationLevel.intValue()) {
			Map<DataElement, Object> valuesForOrganisation = new HashMap<DataElement, Object>();
			values.put(organisation, valuesForOrganisation);
			value = (Double)getValue(expression, period, organisation, valuesForOrganisation);
		}
		else {
			value = getValueFromChildren(expression, period, organisation, values);
		}
		return value;
	}
	
	private Double getValueFromChildren(Expression expression, Period period, Organisation organisation, Map<Organisation, Map<DataElement, Object>> values) {
		if (log.isDebugEnabled()) log.debug("getAggregatedValueFromChildren(expression="+expression+",period="+period+",organisation="+organisation+")");
		organisationService.loadChildren(organisation);
		
		Double sum = 0.0d;
		Integer total = 0;
		for (Organisation child : organisation.getChildren()) {
			Double childValue = getAverageValue(expression, period, child, values);
			if (childValue != null) {
				sum += childValue;
				total++;
			}
			else {
				// we skip it
			}
		}
		return sum / total;
	}
	
	private String generateExpression(String formula, Period period, Organisation organisation, Map<Organisation, Map<DataElement, Object>> values) {
		try {
			boolean isNull = false;
			Map<DataElement, Object> valuesForOrganisation = new HashMap<DataElement, Object>();
			values.put(organisation, valuesForOrganisation);
			
			StringBuffer buffer = new StringBuffer();

			Matcher matcher = CONSTANT_PATTERN.matcher(formula);
			while (matcher.find()) {
				String match = matcher.group();
				match = match.replaceAll("[\\[c\\]]", "");
				Constant constant = dataService.getConstant(Long.parseLong(match));
				
				// TODO constant does not exist
				Object value = constant.getValue();

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
				
				DataElement dataElement = dataService.getDataElement(Long.parseLong(match));

				// TODO constant does not exist
				Object value = getDataValue(dataElement, period, organisation, values);
				
				valuesForOrganisation.put(dataElement, value==null?null:String.valueOf(value));
				matcher.appendReplacement(buffer, String.valueOf(value));

				if (value == null) isNull = true;
			}

			matcher.appendTail(buffer);
			
			if (isNull) return null;
			return buffer.toString();
		} catch (NumberFormatException ex) {
			throw new RuntimeException("Illegal DataElement id", ex);
		}
	}
	
	public Object getDataValue(DataElement dataElement, Period period, Organisation organisation, Map<Organisation, Map<DataElement, Object>> values) {
		if (log.isDebugEnabled()) log.debug("getDataValue(dataElement="+dataElement+", period="+period+", organisation="+organisation+")");
		
		Object result = null;
		// TODO this should be decided otherwise, 
		// or defined by the user
		if (!dataElement.isAggregatable()) {
			DataValue dataValue = dataValueService.getDataValue(dataElement, period, organisation);
			if (dataValue != null) result = dataValue.getValue();
		}
		else {
			List<Organisation> children = organisationService.getChildrenOfLevel(organisation, organisationLevel.intValue());
			Double value = 0d;
			for (Organisation child : children) {
				Map<DataElement, Object> valuesForOrganisation = new HashMap<DataElement, Object>();
				values.put(child, valuesForOrganisation);
				DataValue dataValue = dataValueService.getDataValue(dataElement, period, child);
				valuesForOrganisation.put(dataElement, dataValue!=null?dataValue.getValue():null);
				if (dataValue != null) {
					value += Double.parseDouble(dataValue.getValue());
				}
			}
			result = value;
		}
		return result;
	}
	
	public static boolean hasNullValues(Collection<Object> values) {
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
            	if ( dataService.getDataElement( Long.parseLong(match)) == null) {
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
                DataElement dataElement = dataService.getDataElement(Long.parseLong(match));

                if ( dataElement != null )  {
                    dataElementsInExpression.add( dataElement );
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
                    match = NULL_REPLACEMENT;
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
	
	public void setOrganisationLevel(Integer organisationLevel) {
		this.organisationLevel = organisationLevel;
	}
	
	public void setDataValueService(DataValueService dataValueService) {
		this.dataValueService = dataValueService;
	}
	
}
