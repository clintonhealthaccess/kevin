<div id="add-dsr-objective" class="entity-form-container">
	<g:form url="[controller:'dsrObjective', action:'save']" useToken="true">
		<div class="row ${hasErrors(bean:objective,field:'name','errors')}">
			<label for="name">Name</label>		
			<input name="name" value="${fieldValue(bean:objective,field:'name')}"></input>
			<div class="error-list"><g:renderErrors bean="${objective}" field="name" /></div>
		</div>
		<div class="row ${hasErrors(bean:objective,field:'description','errors')}">
			<label for="description">Description</label>
			<textarea name="description" rows="5">${fieldValue(bean:objective,field:'description')}</textarea>
			<div class="error-list"><g:renderErrors bean="${objective}" field="description" /></div>
		</div>
		
		<g:if test="${objective?.id != null}">
			<input type="hidden" name="id" value="${objective.id}"></input>
		</g:if>
		
		<div class="row ${hasErrors(bean:objective,field:'order','errors')}">
			<label for="order">Order</label>
			<input type="text" name="order" value="${fieldValue(bean:objective,field:'order')}"></input>
			<div class="error-list"><g:renderErrors bean="${objective}" field="order" /></div>
		</div>
		
		<div class="row">
			<button type="submit">Save objective</button>
			<button id="cancel-button">Cancel</button>
		</div>
    </g:form>
	<div class="clear"></div>
</div>