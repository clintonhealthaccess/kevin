<div id="add-constant" class="entity-form-container">
	<g:form url="[controller:'constant', action:'save']" useToken="true">
		<div class="row ${hasErrors(bean:constant,field:'name','errors')}">
			<label for="name">Name</label>		
			<input name="name" value="${fieldValue(bean:constant,field:'name')}"></input>
			<div class="error-list"><g:renderErrors bean="${constant}" field="name" /></div>
		</div>
		<div class="row ${hasErrors(bean:constant,field:'shortName','errors')}">
			<label for="shortName">Short name</label>		
			<input name="shortName" value="${fieldValue(bean:constant,field:'shortName')}"></input>
			<div class="error-list"><g:renderErrors bean="${constant}" field="shortName" /></div>
		</div>
		<div class="row ${hasErrors(bean:constant,field:'value','errors')}">
			<label for="value">Value</label>		
			<input name="value" value="${fieldValue(bean:constant,field:'value')}"></input>
			<div class="error-list"><g:renderErrors bean="${constant}" field="value" /></div>
		</div>
		<div class="row ${hasErrors(bean:constant,field:'description','errors')}">
			<label for="description">Description</label>
			<textarea name="description" rows="5">${fieldValue(bean:constant,field:'description')}</textarea>
			<div class="error-list"><g:renderErrors bean="${constant}" field="description" /></div>
		</div>
		
		<g:if test="${constant.id != null}">
			<input type="hidden" name="id" value="${constant.id}"></input>
		</g:if>
		
		<div class="row">
			<button type="submit">Save Constant</button>
			<button id="cancel-button">Cancel</button>
		</div>
	</g:form>
</div>