<div class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">
			<g:message code="default.new.label" args="[message(code:'planning.label')]"/>
		</h3>
		<g:locales/>
	</div>
	<g:form url="[controller:'planning', action:'save', params:[targetURI: targetURI]]" useToken="true">
		<g:i18nInput name="names" bean="${planning}" value="${planning?.names}" label="${message(code:'entity.name.label')}" field="names"/>
		
		<g:i18nTextarea name="overviewHelps" bean="${planning}" value="${planning.overviewHelps}" label="${message(code:'planning.overviewhelp.label')}" field="overviewHelps" height="150"  width="300" maxHeight="150" />
		<g:i18nTextarea name="budgetHelps" bean="${planning}" value="${planning.budgetHelps}" label="${message(code:'planning.budgethelp.label')}" field="budgetHelps" height="150"  width="300" maxHeight="150" />
		
		<g:selectFromList name="period.id" label="${message(code:'planning.period.label')}" bean="${planning}" field="period"
			from="${periods}" value="${planning.period?.id}" values="${periods.collect{it.startDate.toString()+' - '+it.endDate.toString()}}" optionKey="id" multiple="false"/>
	
		<g:selectFromList name="typeCodes" label="${message(code:'entity.datalocationtype.label')}" bean="${planning}" field="typeCodeString" 
			from="${types}" value="${planning.typeCodes*.toString()}" values="${types.collect{i18n(field:it.names)}}" optionKey="code" multiple="true"/>
	
		<div class="row">
			<label><g:message code="planning.active.label"/></label>
			<g:checkBox name="active" value="${planning.active}" />
		</div>
		
		<g:if test="${planning.id != null}">
			<input type="hidden" name="id" value="${planning.id}"></input>
		</g:if>
		<div class="row">
			<button type="submit" class="rich-textarea-form"><g:message code="default.button.save.label"/></button>&nbsp;&nbsp;
			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label"/></a>
		</div>
    </g:form>
	<div class="clear"></div>
</div>