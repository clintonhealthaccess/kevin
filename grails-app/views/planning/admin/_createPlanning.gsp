<div class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">
			<g:message code="default.new.label" args="[message(code:'planning.label',default:'Planning')]"/>
		</h3>
		<g:locales/>
		<div class="clear"></div>
	</div>
	<g:form url="[controller:'planning', action:'save', params:[targetURI: targetURI]]" useToken="true">
		<g:i18nInput name="names" bean="${planning}" value="${planning?.names}" label="Name" field="names"/>
		
		<g:selectFromList name="period.id" label="${message(code:'period.label')}" bean="${planning}" field="period"
			from="${periods}" value="${planning.period?.id}" values="${periods.collect{it.startDate.toString()+' - '+it.endDate.toString()}}" optionKey="id" multiple="false"/>
	
		<div class="row">
			<label><g:message code="planning.active.label" default="Active"/></label>
			<g:checkBox name="active" value="${planning.active}" />
		</div>
		
		<g:if test="${planning.id != null}">
			<input type="hidden" name="id" value="${planning.id}"></input>
		</g:if>
		<div class="row">
			<button type="submit" class="rich-textarea-form"><g:message code="default.button.save.label" default="Save"/></button>&nbsp;&nbsp;
			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label" default="Cancel"/></a>
		</div>
    </g:form>
	<div class="clear"></div>
</div>