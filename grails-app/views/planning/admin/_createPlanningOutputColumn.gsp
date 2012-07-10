<div class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">
			<g:message code="default.new.label" args="[message(code:'planningtype.label')]"/>
		</h3>
		<g:locales/>
	</div>
	<g:form url="[controller:'planningOutputColumn', action:'save', params:[targetURI: targetURI]]" useToken="true">
		<input type="hidden" name="planningOutput.id" value="${planningOutputColumn.planningOutput.id}"/>
	
		<g:i18nInput name="names" bean="${planningOutputColumn}" value="${planningOutputColumn.names}" label="${message(code:'entity.name.label')}" field="names"/>

		<g:selectFromList name="prefix" label="${message(code:'planning.planningoutput.planningoutputcolumn.prefix.label')}" bean="${planningOutputColumn}" field="prefix" multiple="false"
			from="${valuePrefixes}" value="${planningOutputColumn.prefix}"/>
		
		<g:input name="order" label="${message(code:'entity.order.label')}" bean="${planningOutputColumn}" field="order"/>
	
		<g:if test="${planningOutputColumn.id != null}">
			<input type="hidden" name="id" value="${planningOutputColumn.id}"></input>
		</g:if>
		<div class="row">
			<button type="submit" class="rich-textarea-form"><g:message code="default.button.save.label"/></button>
			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label"/></a>
		</div>
    </g:form>
	<div class="clear"></div>
</div>