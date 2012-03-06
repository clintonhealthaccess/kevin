<div class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">Fct Target</h3>
		<g:locales/>
		<div class="clear"></div>
	</div>
	<g:form url="[controller:'fctTarget', action:'save', params:[targetURI:targetURI]]" useToken="true">
	    <g:if test="${target != null}">
			<input type="hidden" name="id" value="${target.id}"/>
		</g:if>
		<g:i18nInput name="names" bean="${target}" value="${target.names}" label="Name" field="names"/>
		<g:i18nTextarea name="descriptions" bean="${target}" value="${target.descriptions}" label="Description" field="descriptions"/>
		<g:input name="code" label="Code" bean="${target}" field="code"/>
		<g:input name="format" label="Format" bean="${target}" field="format"/>
		
   		<g:selectFromList name="typeCodes" label="${message(code:'facility.type.label')}" bean="${target}" field="typeCodeString" 
				from="${types}" value="${target.typeCodes*.toString()}" values="${types.collect{i18n(field:it.names)}}" optionKey="code" multiple="true"/>
	
		<g:selectFromList name="program.id" label="Program" bean="${target}" field="program" optionKey="id" multiple="false"
			from="${programs}" value="${target.program?.id}" values="${programs.collect{i18n(field:it.names)}}" />

		<g:selectFromList name="sum.id" label="Sum" bean="${target}" field="sum" optionKey="id" multiple="false"
			ajaxLink="${createLink(controller:'data', action:'getAjaxData', params:[class:'Sum'])}"
			from="${sums}" value="${target.sum?.id}" values="${sums.collect{i18n(field:it.names)+' ['+it.code+'] ['+it.class.simpleName+']'}}" />
		
		<g:input name="order" label="Order" bean="${target}" field="order"/>
		<div class="row">
			<button type="submit">Save Target</button>
			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label" default="Cancel"/></a>
		</div>
    </g:form>
	<div class="clear"></div>
</div>
