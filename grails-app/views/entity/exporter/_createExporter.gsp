<%@page import="org.chai.kevin.util.Utils"%>
<div class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">
			<g:message code="default.new.label" args="[message(code:'export.label')]"/>
		</h3>
		<g:locales/>
	</div>
	<div class="forms-container">
		<div class="data-field-column">
			<g:form url="[controller:'exporter', action:'save', params: [targetURI: targetURI]]" useToken="true">
				<g:i18nTextarea name="descriptions" bean="${exporter}" value="${exporter.descriptions}" label="${message(code:'entity.description.label')}" field="descriptions" height="150"  width="300" maxHeight="150" />
				
				<g:selectFromList name="periods" label="${message(code:'period.label')}" bean="${exporter}" field="periods" 
				from="${periods}" value="${exporter.periods*.id}" values="${periods.collect{Utils.formatDate(it.startDate)+' to '+Utils.formatDate(it.endDate)}}" optionKey="id" multiple="true"/>
				
				<g:selectFromList name="typeCodes" label="${message(code:'entity.datalocationtype.label')}" bean="${exporter}" field="typeCodeString" 
			from="${types}" value="${exporter.typeCodes*.toString()}" values="${types.collect{i18n(field:it.names)}}" optionKey="code" multiple="true"/>
				
				<g:selectFromList name="locations" label="${message(code:'location.label')}" field="locations" 
						optionKey="id" multiple="true" ajaxLink="${createLink(controller:'location', action:'getCalculationLocationAjaxData')}" 
						from="${locations}" value="${exporter.locations*.id}" bean="${exporter}" 
						values="${locations.collect {'['+it.class.simpleName+'] '+i18n(field:it.names)}}" />
						
				<g:selectFromList name="data" label="${message(code:'rawdataelement.label')}" field="data" 
						optionKey="id" multiple="true" ajaxLink="${createLink(controller:'data', action:'getAjaxData',params:[class: 'RawDataElement'])}" 
						from="${data}" value="${exporter.data*.id}" bean="${exporter}" 
						values="${data.collect{i18n(field:it.names)+' ['+it.code+'] ['+it.class.simpleName+']'}}" />
						
				<g:if test="${exporter.id != null}">
					<input type="hidden" name="id" value="${exporter.id}"></input>
				</g:if>
						
				<div class="row">
					<button type="submit"><g:message code="default.button.save.label"/></button>
					<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label"/></a>
				</div>
			</g:form>
		</div>
	</div>
	<div class="clear"></div>
</div>