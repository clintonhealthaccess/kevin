<div class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">
			<g:message code="default.new.label" args="[message(code:'planningtype.label')]"/>
		</h3>
		<g:locales/>
	</div>
	<g:form url="[controller:'planningOutput', action:'save', params:[targetURI: targetURI]]" useToken="true">
		<input type="hidden" name="planning.id" value="${planningOutput.planning.id}"/>
	
		<g:i18nInput name="names" bean="${planningOutput}" value="${planningOutput.names}" label="${message(code:'entity.name.label')}" field="names"/>

		<g:i18nTextarea name="captions" bean="${planningOutput}" value="${planningOutput.captions}" label="${message(code:'planning.planningoutput.caption.label')}" field="captions" height="150"  width="300" maxHeight="150" />
		<g:i18nTextarea name="helps" bean="${planningOutput}" value="${planningOutput.helps}" label="${message(code:'planning.planningoutput.help.label')}" field="helps" height="150"  width="300" maxHeight="150" />

		<g:selectFromList name="fixedHeader" label="${message(code:'planning.planningoutput.fixedheader.label')}" bean="${planningOutput}" field="fixedHeader" multiple="false"
			from="${valuePrefixes}" value="${planningOutput.fixedHeader}"/>
		
		<g:selectFromList name="dataElement.id" label="${message(code:'planning.planningoutput.dataelement.label')}" bean="${planningOutput}" field="dataElement" optionKey="id" multiple="false"
			ajaxLink="${createLink(controller:'data', action:'getAjaxData', params:[class:'DataElement'])}"
			from="${dataElements}" value="${planningOutput?.dataElement?.id}" values="${dataElements.collect{i18n(field:it.names)+' ['+it.code+'] ['+it.class.simpleName+']'}}" />
	
		<div class="row">
			<label><g:message code="planning.planningoutput.displaytotal.label"/></label>
			<g:checkBox name="displayTotal" value="${planningOutput.displayTotal}" />
		</div>
	
		<g:input name="order" label="${message(code:'entity.order.label')}" bean="${planningOutput}" field="order"/>
	
		<g:if test="${planningOutput.id != null}">
			<input type="hidden" name="id" value="${planningOutput.id}"></input>
		</g:if>
		<div class="row">
			<button type="submit" class="rich-textarea-form"><g:message code="default.button.save.label"/></button>
			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label"/></a>
		</div>
    </g:form>
	<div class="clear"></div>
</div>