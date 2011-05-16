<div class="entity-form-container" id="add-dashboard-target">
	<g:form url="[controller:'dashboardTarget', action:'save']" useToken="true" class="flow-form">
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
		<div class="row">
			<h5>Expressions</h5>
			<div>
				<a id="add-expression-link" class="float-right" href="${createLink(controller:'expression', action:'create')}">new expression</a>
			</div>
			<div class="clear"></div>
			
			<div id="expressions-block">
				<g:each status="i" in="${groups}" var="group">
					<div id="group-${group.id}" class="group-list">
						<label for="entry.calculations[${group.uuid}].expression.id">Expression for ${group.name}:</label>
						<select class="expression-list" name="entry.calculations[${group.uuid}].expression.id">
							<option value="null">-- disabled --</option>
							<g:each in="${expressions}" var="expression">
								<option value="${expression.id}" ${objectiveEntry==null?'':fieldValue(bean:objectiveEntry?.entry?.calculations[group.uuid]?.expression, field:'id')+''==expression.id+''?'selected="selected"':''}>
									${expression.name}
								</option>
							</g:each>
						</select>
						<input type="hidden" name="entry.calculations[${group.uuid}].groupUuid" value="${group.uuid}"></input>
						<g:if test="${currentObjective == null}">
							<input type="hidden" name="entry.calculations[${group.uuid}].id" value="${objectiveEntry?.entry?.calculations[group.uuid]?.id}"></input>
						</g:if>
					</div>
				</g:each>
			</div>
		</div>
		
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
		
		<g:if test="${currentObjective != null}">
			<input type="hidden" name="currentObjective" value="${currentObjective.id}"></input>
		</g:if>
		<g:else>
			<input type="hidden" name="entry.id" value="${objectiveEntry.entry.id}"></input>
			<input type="hidden" name="id" value="${objectiveEntry.id}"></input>
		</g:else>
		<div class="row">
			<button type="submit">Save objective</button>
			<button id="cancel-button">Cancel</button>
		</div>
    </g:form>
	<div class="clear"></div>
</div>
<div class="hidden flow-container"></div>

<script type="text/javascript">
	$(document).ready(function() {
		$('#add-dashboard-target').flow({
			addLinks: '#add-expression-link',
			onSuccess: function(data) {
				if (data.result == 'success') {
					var expression = data.newEntity
					$('.expression-list').append('<option value="'+expression.id+'">'+expression.name+'</option>');
					$.sexyCombo.changeOptions('.expression-list');
				}
			}
		});
	});
</script>