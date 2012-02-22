<div class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">
			<g:message code="default.new.label" args="[message(code:'planningType.label',default:'Planning')]"/>
		</h3>
		<g:locales/>
		<div class="clear"></div>
	</div>
	<g:form url="[controller:'planningType', action:'save', params:[targetURI: targetURI]]" useToken="true">
		<input type="hidden" name="planning.id" value="${planningType.planning.id}"/>
	
		<g:i18nInput name="names" bean="${planningType}" value="${planningType.names}" label="Name" field="names"/>
		<g:i18nInput name="namesPlural" bean="${planningType}" value="${planningType.namesPlural}" label="Name (plural)" field="namesPlural"/>

		<g:input name="discriminator" bean="${planningType}" value="${planningType.discriminator}" label="Discriminator" field="discriminator"/>
		
		
		
		<g:if test="${planningType.id != null}">
			<input type="hidden" name="id" value="${planningType.id}"></input>
		</g:if>
		<div class="row">
			<button type="submit" class="rich-textarea-form"><g:message code="default.button.save.label" default="Save"/></button>&nbsp;&nbsp;
			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label" default="Cancel"/></a>
		</div>
    </g:form>
	<div class="clear"></div>
</div>