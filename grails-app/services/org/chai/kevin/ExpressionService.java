package org.chai.kevin;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.common.AbstractNameableObject;
import org.hisp.dhis.dataelement.Constant;
import org.hisp.dhis.dataelement.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.system.util.MathUtils;
import org.nfunk.jep.JEP;

public class ExpressionService {

    private final Pattern FORMULA_PATTERN = Pattern.compile("\\[.+?\\]");
    private final Pattern CONSTANT_PATTERN = Pattern.compile("\\[c.+?\\]");
	
	private ConstantService constantService;
	private DataElementService dataElementService;
	private AggregationService aggregationService;
	private DataElementCategoryService dataElementCategoryService;
	
	private org.hisp.dhis.expression.ExpressionService dhisExpressionService;
	
	private static JEP getJEPParser() {
    	final JEP parser = new JEP();
    	parser.addStandardConstants();
    	parser.addStandardFunctions();
    	return parser;
    }
	
	private static Object evaluate(String expression) {
		JEP parser = getJEPParser();
        
        parser.parseExpression( expression );
        return parser.getValueAsObject();
	}
	
	public Object getValue(Expression expression, Period period, OrganisationUnit organisationUnit, Map<AbstractNameableObject, Object> values) {
		String stringExpression = generateExpression(expression.getExpression(), period.getStartDate(), period.getEndDate(), organisationUnit, values);
		return evaluate(stringExpression);
	}
	
	public static boolean hasNullValues(Collection<Object> values) {
		for (Object object : values) {
			if (object == null) return true;
		}
		return false;
	}
	
	private String generateExpression(String formula, Date startDate, Date endDate, OrganisationUnit organisationUnit, Map<AbstractNameableObject, Object> values) {
		try {
			StringBuffer buffer = new StringBuffer();

			Matcher matcher = CONSTANT_PATTERN.matcher(formula);
			while (matcher.find()) {
				String match = matcher.group();
				match = match.replaceAll("[\\[c\\]]", "");
				Constant constant = constantService.getConstant(Integer.parseInt(match));
				Object value = constant.getValue();
				match = String.valueOf(value);
				matcher.appendReplacement(buffer, match);
				values.put(constant, value);
			}

			matcher.appendTail(buffer);
			matcher = FORMULA_PATTERN.matcher(buffer.toString());

			buffer = new StringBuffer();
			while (matcher.find()) {
				String match = matcher.group();

				DataElementOperand operand = DataElementOperand.getOperand(match);
				DataElement dataElement = dataElementService.getDataElement(operand.getDataElementId());
				DataElementCategoryOptionCombo optionCombo = !operand.isTotal() ? dataElementCategoryService.getDataElementCategoryOptionCombo(operand.getOptionComboId()) : null;

				Object value = null;
				if (dataElement.isAggregatable()) 
					value = aggregationService.getAggregatedDataValue(dataElement, optionCombo, startDate, endDate, organisationUnit);
				else
					value = aggregationService.getNonAggregatedDataValue(dataElement, optionCombo, startDate, endDate, organisationUnit);

				match = value == null ? "0" : String.valueOf(value);
				matcher.appendReplacement(buffer, match);

				values.put(dataElement, value);
			}

			matcher.appendTail(buffer);

			return buffer.toString();
		} catch (NumberFormatException ex) {
			throw new RuntimeException("Illegal DataElement id", ex);
		}
	}
	
    public String expressionIsValid( String formula )
    {
        if (formula == null) return org.hisp.dhis.expression.ExpressionService.EXPRESSION_IS_EMPTY;
       
        StringBuffer buffer = new StringBuffer();
        
		Matcher matcher = CONSTANT_PATTERN.matcher(formula);
		while (matcher.find()) {
			String match = matcher.group();
			match = match.replaceAll("[\\[c\\]]", "");
			try {
				if (!constantService.constantExists(Integer.parseInt(match))) {
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
            DataElementOperand operand = null;
            try {
                operand = DataElementOperand.getOperand( matcher.group() );
            }
            catch ( NumberFormatException ex ) {
                return org.hisp.dhis.expression.ExpressionService.ID_NOT_NUMERIC;
            }
            if ( !dataElementService.dataElementExists( operand.getDataElementId()  ) ) {
                return org.hisp.dhis.expression.ExpressionService.DATAELEMENT_DOES_NOT_EXIST;
            }
            if ( !operand.isTotal() && !dataElementService.dataElementCategoryOptionComboExists( operand.getOptionComboId() ) ){
                return org.hisp.dhis.expression.ExpressionService.CATEGORYOPTIONCOMBO_DOES_NOT_EXIST;
            }
            matcher.appendReplacement( buffer, "1.1" );
        }
        
        matcher.appendTail( buffer );
        if ( MathUtils.expressionHasErrors( buffer.toString() ) ) {
            return org.hisp.dhis.expression.ExpressionService.EXPRESSION_NOT_WELL_FORMED;
        }        

        return org.hisp.dhis.expression.ExpressionService.VALID;
    }


	public Set<DataElement> getDataElementsInExpression(String formula) {
		return dhisExpressionService.getDataElementsInExpression(formula);
	}

	public String convertStringExpression(String formula, Map<Integer, String> replacement) {
		return dhisExpressionService.convertStringExpression(formula, replacement);
	}
	
	
	public void setAggregationService(AggregationService aggregationService) {
		this.aggregationService = aggregationService;
	}
	
	public void setConstantService(ConstantService constantService) {
		this.constantService = constantService;
	}
	
	public void setDataElementCategoryService(DataElementCategoryService dataElementCategoryService) {
		this.dataElementCategoryService = dataElementCategoryService;
	}
	
	public void setDataElementService(DataElementService dataElementService) {
		this.dataElementService = dataElementService;
	}
	
	public void setDhisExpressionService(org.hisp.dhis.expression.ExpressionService dhisExpressionService) {
		this.dhisExpressionService = dhisExpressionService;
	}

}
