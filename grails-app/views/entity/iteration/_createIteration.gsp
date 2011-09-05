<div id="add-iteration" class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">Create Iteration</h3>
		<div class="clear"></div>
	</div>
	<g:form url="[controller:'iteration', action:'save']" useToken="true">
		<span class="display-in-block"><g:message code="general.text.startdate" default="Start Date" />
		<g:datePicker name="startDate" value="${iteration.startDate}"  precision="day"  years="${Calendar.getInstance().get(Calendar.YEAR)+10..1990}"/></span>
		<span class="display-in-block"><g:message code="general.text.enddate" default="End Date" />
		<g:datePicker name="endDate" value="${iteration.endDate}" precision="day"  years="${Calendar.getInstance().get(Calendar.YEAR)+10..1990}"/></span>
		<g:if test="${iteration.id != null}">
			<input type="hidden" name="id" value="${iteration.id}"></input>
		</g:if>
		<div class="row">
			<button type="submit">Save Iteration</button>
			<button id="cancel-button">Cancel</button>
		</div>
	</g:form>
</div>