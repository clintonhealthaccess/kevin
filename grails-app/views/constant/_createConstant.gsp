<%@ page import="org.chai.kevin.ValueType" %>

<div id="add-constant" class="entity-form-container togglable">

	<div class="entity-form-header">
		<h3 class="title">Constant</h3>
		<g:locales/>
		<div class="clear"></div>
	</div>
	
	<g:form url="[controller:'constant', action:'save']" useToken="true">

		<g:i18nInput name="names" bean="${constant}" value="${constant.names}" label="Name" field="names"/>
		<g:i18nTextarea name="descriptions" bean="${constant}" value="${constant.descriptions}" label="Description" field="descriptions"/>
		<g:input name="code" label="Code" bean="${constant}" field="code"/>
		<g:input name="value" label="Value" bean="${constant}" field="value"/>
	
		<g:selectFromEnum name="type" bean="${constant}" values="${ValueType.values()}" field="type" label="Type"/>
		
		<g:if test="${constant.id != null}">
			<input type="hidden" name="id" value="${constant.id}"></input>
		</g:if>
		
		<div class="row">
			<button type="submit">Save Constant</button>
			<button id="cancel-button">Cancel</button>
		</div>
	</g:form>
</div>