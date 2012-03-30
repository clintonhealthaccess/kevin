<div id="add-skip-rule" class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">
			<g:message code="default.new.label" args="[message(code:'planning.skiprule.label')]"/>
		</h3>
		<g:locales />
		<div class="clear"></div>
	</div>
	<div class="forms-container">
		<div class="data-field-column">
			<g:form url="[controller:'planningSkipRule', action:'save', params:[targetURI:targetURI]]" useToken="true">
				<input type="hidden" name="planning.id" value="${skip.planning.id}" />
				
			 	<g:i18nTextarea name="descriptions" bean="${skip}" value="${skip?.descriptions}" label="${message(code:'entity.description.label')}" field="descriptions" />
				<g:render template="/templates/skippedFormElements" model="[skip: skip]"/>
			 	<g:textarea name="expression" label="${message(code:'skiprule.expression.label')}" bean="${skip}" field="expression" value="${skip.expression}" rows="5"/>
			 
				<g:if test="${skip.id != null}">
					<input type="hidden" name="id" value="${skip.id}" />
				</g:if>
				<div class="row">
					<button type="submit" class="rich-textarea-form"><g:message code="default.button.save.label"/></button>
					<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label"/></a>
				</div>
			</g:form>
		</div>
		<g:render template="/templates/searchDataElement" model="[element: 'textarea[name="expression"]', formUrl: [controller:'formElement', action:'getHtmlData']]"/>
	</div>
</div>
