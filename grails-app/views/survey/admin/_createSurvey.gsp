<div class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">
			<g:message code="default.new.label" args="[message(code:'survey.label')]"/>
		</h3>
		<g:locales/>
	</div>
	<g:form url="[controller:'survey', action:'save', params:[targetURI: targetURI]]" useToken="true">
		<g:i18nInput name="names" bean="${survey}" value="${survey?.names}" label="${message(code:'entity.name.label')}" field="names"/>
		<g:i18nRichTextarea name="descriptions" bean="${survey}" value="${survey?.descriptions}" label="${message(code:'entity.description.label')}" field="descriptions" height="100"  width="300" maxHeight="100" />
		
		<g:input name="code" label="${message(code:'entity.code.label')}" bean="${survey}" field="code" />
		
		<g:selectFromList name="period.id" label="${message(code:'period.label')}" bean="${survey}" field="period"
			from="${periods}" value="${survey.period?.id}" values="${periods.collect{it.startDate.toString()+' - '+it.endDate.toString()}}" optionKey="id" multiple="false"/>
	
		<g:selectFromList name="lastPeriod.id" label="${message(code:'period.label')}" bean="${survey}" field="lastPeriod"
			from="${periods}" value="${survey.lastPeriod?.id}" values="${periods.collect{it.startDate.toString()+' - '+it.endDate.toString()}}" optionKey="id" multiple="false"/>
	
		<div class="row">
			<label><g:message code="survey.active.label"/></label>
			<g:checkBox name="active" value="${survey.active}" />
		</div>
		
		<div class="clear"></div>
		<g:if test="${survey?.id != null}">
			<input type="hidden" name="id" value="${survey?.id}"></input>
		</g:if>
		<div class="row">
			<button type="submit" class="rich-textarea-form"><g:message code="default.button.save.label"/></button>
			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label"/></a>
		</div>
    </g:form>
	<div class="clear"></div>
</div>
<script type="text/javascript">
	$(document).ready(function() {
		getRichTextContent();
	})
</script>