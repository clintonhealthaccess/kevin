<div id="add-category-objective" class="entity-form-container togglable">

	<div class="entity-form-header">
		<h3 class="title">D.S.Rs Category</h3>
		<g:locales/>
		<div class="clear"></div>
	</div>
	
	<g:form url="[controller:'dsrTargetCategory', action:'save', params:[targetURI:targetURI]]" useToken="true">
		<g:i18nInput name="names" bean="${category}" value="${category.names}" label="Name" field="names"/>
		<g:i18nTextarea name="descriptions" bean="${category}" value="${category.descriptions}" label="Description" field="descriptions"/>
		<g:input name="code" label="Code" bean="${category}" field="code"/>
		<g:input name="order" label="Order" bean="${category}" field="order"/>
	
		<g:if test="${category?.id != null}">
			<input type="hidden" name="id" value="${category.id}"></input>
		</g:if>
		
		<div class="row">
			<button type="submit">Save Category</button>
			<a href="${createLink(uri: targetURI)}">cancel</a>
		</div>
    </g:form>
	<div class="clear"></div>
</div>