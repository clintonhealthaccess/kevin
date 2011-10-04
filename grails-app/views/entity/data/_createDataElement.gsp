<%@ page import="org.chai.kevin.data.Type.ValueType" %>

<div id="add-data-element" class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">Create Data Element</h3>
		<g:locales/>
		<div class="clear"></div>
	</div>
	<g:form url="[controller:'dataElement', action:'save']" useToken="true">
		<g:set var="readonly" value="${dataElement.id != null}"/>
	
		<g:i18nInput name="names" bean="${dataElement}" value="${dataElement.names}" label="Name" field="names" />
		<g:i18nTextarea name="descriptions" bean="${dataElement}" value="${dataElement.descriptions}" label="Descriptions" field="descriptions" height="150"  width="300" maxHeight="150" />
		
		<g:input name="code" label="Code" bean="${dataElement}" field="code" />
		<g:textarea name="type.jsonType" label="Type" bean="${dataElement}" field="type" readonly="${hasValues}"/>
		
		<g:input name="info" label="Info" bean="${dataElement}" field="info"/>
		
		<g:if test="${dataElement.id != null}">
			<input type="hidden" name="id" value="${dataElement.id}"/>
		</g:if>
		<div class="row">
			<button type="submit" class="rich-textarea-form">Save Date Element</button>
			<button id="cancel-button">Cancel</button>
		</div>
		
	</g:form>
	<div class="clear"></div>
</div>
