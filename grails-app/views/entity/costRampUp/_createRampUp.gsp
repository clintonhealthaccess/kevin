<%@ page import="org.chai.kevin.cost.CostTarget.CostType" %>

<div class="entity-form-container togglable">

	<div class="entity-form-header">
		<h3 class="title">Costing ramp-up</h3>
		<g:locales/>
		<div class="clear"></div>
	</div>
	
	<g:form url="[controller:'costRampUp', action:'save']" useToken="true">
		<g:i18nInput name="names" bean="${rampUp}" value="${rampUp.names}" label="Name" field="names"/>
		<g:i18nTextarea name="descriptions" bean="${rampUp}" value="${rampUp.descriptions}" label="Description" field="descriptions"/>
		<g:input name="code" label="Code" bean="${rampUp}" field="code"/>
		
		<g:if test="${rampUp != null}">
			<input type="hidden" name="id" value="${rampUp.id}"></input>
		</g:if>
		
		<g:each in="${years}" var="year">
			<div class="row ${rampUp?.years!=null?hasErrors(bean:rampUp?.years[year],field:'value','errors'):''}">
				<label for="years[${year}].value">Year ${year}</label>
				<input name="years[${year}].value" value="${rampUp?.years!=null?fieldValue(bean:rampUp?.years[year],field:'value'):''}"></input>
				<div class="error-list"><g:if test="${rampUp?.years != null}"><g:renderErrors bean="${rampUp?.years[year]}" field="value" /></g:if></div>
			</div>
			<input type="hidden" name="years[${year}].year" value="${year}"/>
			<g:if test="${rampUp?.years != null && rampUp?.years[year] != null}">
				<input type="hidden" name="years[${year}].id" value="${rampUp?.years[year].id}"/>
			</g:if>
		</g:each>
		
		<div class="row">
			<button type="submit">Save ramp-up</button>
			<button id="cancel-button">Cancel</button>
		</div>
    </g:form>
	<div class="clear"></div>
</div>

