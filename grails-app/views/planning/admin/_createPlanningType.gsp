<div class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">
			<g:message code="default.new.label" args="[message(code:'planningtype.label')]"/>
		</h3>
		<g:locales/>
	</div>
	<g:form url="[controller:'planningType', action:'save', params:[targetURI: targetURI]]" useToken="true">
		<input type="hidden" name="planning.id" value="${planningType.planning.id}"/>
	
		<g:i18nInput name="names" bean="${planningType}" value="${planningType.names}" label="${message(code:'entity.name.label')}" field="names"/>
		<g:i18nInput name="namesPlural" bean="${planningType}" value="${planningType.namesPlural}" label="${message(code:'planning.planningtype.nameplural.label')}" field="namesPlural"/>

		<g:selectFromList name="fixedHeader" label="${message(code:'planning.planningtype.fixedheader.label')}" bean="${planningType}" field="fixedHeader" multiple="false"
			from="${valuePrefixes}" value="${planningType.fixedHeader}"/>
		
		<g:selectFromList name="formElement.dataElement.id" label="${message(code:'planning.planningtype.rawdataelement.label')}" bean="${planningType}" field="formElement.dataElement" optionKey="id" multiple="false"
			ajaxLink="${createLink(controller:'data', action:'getAjaxData', params:[class:'RawDataElement'])}"
			from="${dataElements}" value="${planningType.formElement?.dataElement?.id}" values="${dataElements.collect{i18n(field:it.names)+' ['+it.code+'] ['+it.class.simpleName+']'}}" />
	
		<g:if test="${headerPrefixes != null && !headerPrefixes.empty}">
			<div class="row ${hasErrors(bean:planningType, field:'formElement.headers', 'errors')}">
				<a href="#" onclick="$(this).next().toggle();return false;"><g:message code="formelement.headers.label"/>:</a> 
				<div class="hidden">
					<g:each in="${headerPrefixes}" var="headerPrefix">
						<input type="hidden" name="headerList" value="${headerPrefix}"/>
						<g:i18nRichTextarea name="headerList[${headerPrefix}]" bean="${planningType}" value="${planningType.formElement.headers[headerPrefix]}" label="${headerPrefix}" field="headers" height="50"/>
					</g:each>
				</div>
			</div>
		</g:if>
		
		<g:if test="${sections != null && !sections.empty}">
			<div class="row ${hasErrors(bean:planningType, field:'sectionDescriptions', 'errors')}">
				<a href="#" onclick="$(this).next().toggle();return false;"><g:message code="planning.planningtype.sections.label"/>:</a> 
				<div class="hidden">
					<g:each in="${sections}" var="section">
						<input type="hidden" name="sectionList" value="${section}"/>
						<g:i18nRichTextarea name="sectionList[${section}]" bean="${planningType}" value="${planningType.sectionDescriptions[section]}" label="${section}" field="sectionDescriptions" height="50"/>
					</g:each>
				</div>
			</div>
		</g:if>
		
		<g:input name="maxNumber" label="${message(code:'planning.planningtype.maxnumber.label')}" bean="${planningType}" field="maxNumber"/>
		
		<g:if test="${planningType?.formElement?.id != null}">
			<input type="hidden" name="formElement.id" value="${planningType.formElement.id}"></input>
		</g:if>
		<g:if test="${planningType.id != null}">
			<input type="hidden" name="id" value="${planningType.id}"></input>
		</g:if>
		<div class="row">
			<button type="submit" class="rich-textarea-form"><g:message code="default.button.save.label"/></button>
			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label"/></a>
		</div>
    </g:form>
	<div class="clear"></div>
</div>