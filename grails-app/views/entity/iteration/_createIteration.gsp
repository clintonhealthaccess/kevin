<div class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">Create Iteration</h3>
		<div class="clear"></div>
	</div>
	<g:form url="[controller:'iteration', action:'save', params:[targetURI: targetURI]]" useToken="true">
		<div class="row">
			<label><g:message code="period.startdate.label" default="Start Date" /></label>
			<g:datePicker name="startDate" value="${iteration.startDate}"  precision="day"  years="${Calendar.getInstance().get(Calendar.YEAR)+10..1990}"/>
			
		</div>
		<div class="row ${hasErrors(bean:iteration,field:'endDate','errors')}">
			<label><g:message code="period.enddate.label" default="End Date" /></label>
			<g:datePicker name="endDate" value="${iteration.endDate}" precision="day"  years="${Calendar.getInstance().get(Calendar.YEAR)+10..1990}"/>
			<div class="error-list"><g:renderErrors bean="${iteration}" field="endDate" /></div>
		</div>
		<g:if test="${iteration.id != null}">
			<input type="hidden" name="id" value="${iteration.id}"></input>
		</g:if>
		<br />
		<div class="row">
			<button type="submit"><g:message code="default.button.save.label" default="Save"/></button>&nbsp;&nbsp;
			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label" default="Cancel"/></a>
		</div>
	</g:form>
</div>