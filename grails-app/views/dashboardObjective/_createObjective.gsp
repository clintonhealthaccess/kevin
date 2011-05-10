<div class="entity-form-container" id="add-dashboard-objective">
	<g:form url="[controller:'dashboardObjective', action:'save']" useToken="true">
		<div class="row ${hasErrors(bean:objectiveEntry?.entry,field:'name','errors')}">
			<label for="entry.name">Name</label>		
			<input name="entry.name" value="${fieldValue(bean:objectiveEntry?.entry,field:'name')}"></input>
			<div class="error-list"><g:renderErrors bean="${objectiveEntry?.entry}" field="name" /></div>
		</div>
		<div class="row ${hasErrors(bean:objectiveEntry?.entry,field:'description','errors')}">
			<label for="entry.description">Description</label>
			<textarea name="entry.description" rows="5">${fieldValue(bean:objectiveEntry?.entry,field:'description')}</textarea>
			<div class="error-list"><g:renderErrors bean="${objectiveEntry?.entry}" field="description" /></div>
		</div>
		
		<g:if test="${currentObjective != null}">
			<input type="hidden" name="currentObjective" value="${currentObjective.id}"></input>
		</g:if>
		<g:else>
			<input type="hidden" name="entry.id" value="${objectiveEntry.entry.id}"></input>
			<input type="hidden" name="id" value="${objectiveEntry.id}"></input>
		</g:else>
		
		<div class="row ${hasErrors(bean:objectiveEntry,field:'weight','errors')}">
			<label for="weight">Weight</label>
			<input type="text" name="weight" value="${fieldValue(bean:objectiveEntry,field:'weight')}"></input>
			<div class="error-list"><g:renderErrors bean="${objectiveEntry}" field="weight" /></div>
		</div>
		
		<div class="row ${hasErrors(bean:objectiveEntry,field:'order','errors')}">
			<label for="order">Order</label>
			<input type="text" name="order" value="${fieldValue(bean:objectiveEntry,field:'order')}"></input>
			<div class="error-list"><g:renderErrors bean="${objectiveEntry}" field="order" /></div>
		</div>
		
		<div class="row">
			<button type="submit">Save objective</button>
			<button id="cancel-button">Cancel</button>
		</div>
    </g:form>
	<div class="clear"></div>
</div>