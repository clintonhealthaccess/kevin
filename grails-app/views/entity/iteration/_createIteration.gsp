<div class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">Create Iteration</h3>
		<div class="clear"></div>
	</div>
	<g:form url="[controller:'iteration', action:'save', params:[targetURI: targetURI]]" useToken="true">
		<div class="row">
			<g:message code="general.text.startdate" default="Start Date" />
			<g:datePicker name="startDate" value="${iteration.startDate}"  precision="day"  years="${Calendar.getInstance().get(Calendar.YEAR)+10..1990}"/>
			
		</div>
		<div class="row ${hasErrors(bean:iteration,field:'endDate','errors')}">
			<g:message code="general.text.enddate" default="End Date" />
			<g:datePicker name="endDate" value="${iteration.endDate}" precision="day"  years="${Calendar.getInstance().get(Calendar.YEAR)+10..1990}"/>
			<div class="error-list"><g:renderErrors bean="${iteration}" field="endDate" /></div>
		</div>
		<g:if test="${iteration.id != null}">
			<input type="hidden" name="id" value="${iteration.id}"></input>
		</g:if>
		<div class="row">
			<button type="submit">Save Iteration</button>
			<a href="${createLink(uri: targetURI)}">cancel</a>
		</div>
		
	</g:form>
</div>