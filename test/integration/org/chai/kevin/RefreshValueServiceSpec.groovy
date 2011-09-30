package org.chai.kevin

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

import org.chai.kevin.data.Average;
import org.chai.kevin.data.Calculation;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.Expression;
import org.chai.kevin.data.Type;
import org.chai.kevin.util.JSONUtils;
import org.chai.kevin.value.CalculationValue;
import org.chai.kevin.value.DataValue;
import org.chai.kevin.value.ExpressionValue;
import org.chai.kevin.value.ExpressionValue.Status;
import org.chai.kevin.value.Value;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

class RefreshValueServiceSpec extends IntegrationTests {

	def refreshValueService;
	
	
	def "test get non calculated expressions"() {
		when:
		def period = newPeriod()
		def organisation = newOrganisationUnit(BUTARO)
		def expression = newExpression(CODE(1), Type.TYPE_NUMBER)
		
		then:
		List<ExpressionValue> values = refreshValueService.getNonCalculatedExpressions(expression);
		values.equals([new ExpressionValue(null, null, organisation, expression, period)])
	}
	
	def "test get non calculated expressions when already one expression value"() {
		when:
		def period = newPeriod()
		def organisation = newOrganisationUnit(BUTARO)
		def expression = newExpression(CODE(1), Type.TYPE_NUMBER)
		def expressionValue = newExpressionValue(expression, period, organisation, Status.VALID, Value.NULL)
		
		then:
		List<ExpressionValue> values = refreshValueService.getNonCalculatedExpressions(expression);
		values.size() == 0
	}
	
	
	def "test outdated expressions"() {
		setup:
		def period = newPeriod()
		def organisation = newOrganisationUnit(BUTARO)
		def expression = newExpression(CODE(1), Type.TYPE_NUMBER)
		def expressionValue = newExpressionValue(expression, period, organisation, Status.VALID, Value.NULL)
		def values = null
		
		when:
		expression.timestamp = new Date()
		expression.save()
		
		expressionValue.timestamp = new Date()
		expressionValue.save()
		values = refreshValueService.getOutdatedExpressions(expression);
		
		then:
		values.size() == 0
		
		when:
		expression.timestamp = new Date()
		expression.save()
		values = refreshValueService.getOutdatedExpressions(expression);
		
		then:
		values.size() == 1
	}
	
	def "test get non calculated calculations"() {
		when:
		def period = newPeriod()
		def organisation = newOrganisationUnit(BUTARO)
		def expression = newExpression(CODE(1), Type.TYPE_NUMBER)
		def average = newAverage([DISTRICT_HOSPITAL_GROUP:expression])
		
		List<CalculationValue> values = refreshValueService.getNonCalculatedCalculations(average);

		then:
		values.size() == 1
	}
	
	def "test outdated calculations"() {
		setup:
		def period = newPeriod()
		def organisation = newOrganisationUnit(BUTARO)
		def expression = newExpression(CODE(1), Type.TYPE_NUMBER)
		def average = newAverage([DISTRICT_HOSPITAL_GROUP:expression])
		def calculationValue = newCalculationValue(average, period, organisation, false, false, Value.NULL)
		def values = null
		
		when:
		average.timestamp = new Date()
		average.save()
		
		calculationValue.timestamp = new Date()
		calculationValue.save()
		
		values = refreshValueService.getOutdatedCalculations(average);
		
		then:
		values.size() == 0
		
		when:
		average.timestamp = new Date()
		average.save()
		
		values = refreshValueService.getOutdatedCalculations();

		then:
		values.size() == 1
		
		when:
		calculationValue.timestamp = new Date()
		calculationValue.save()
				
		values = refreshValueService.getOutdatedCalculations();

		then:
		values.size() == 0
		
		when:
		expression.timestamp = new Date()
		expression.save()
		
		values = refreshValueService.getOutdatedCalculations();
		
		then:
		values.size() == 1
	}
	
	
}
