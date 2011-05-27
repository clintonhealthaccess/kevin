<div class="entity-form-container togglable" id="add-dashboard-target">
	
	<div class="entity-form-header">
		<h3 class="title">Dashboard target</h3>
		<g:locales/>
		<div class="clear"></div>
	</div>

	<g:form url="[controller:'dashboardTarget', action:'save']" useToken="true" class="flow-form">
		<g:i18nInput name="entry.names" label="Name" bean="${objectiveEntry?.entry}" value="${objectiveEntry?.entry.names}" field="names"/>
		<g:i18nInput name="entry.descriptions" label="Description" bean="${objectiveEntry?.entry}" value="${objectiveEntry?.entry.descriptions}" field="descriptions"/>
		<g:input name="entry.code" label="Code" bean="${objectiveEntry?.entry}" field="code"/>
		
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
									<g:i18n field="${expression.names}"/>
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
		
		<g:input name="weight" label="Weight" bean="${objectiveEntry}" field="weight"/>
		<g:input name="order" label="Order" bean="${objectiveEntry}" field="order"/>
		
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
					$('.expression-list').append('<option value="'+expression.id+'">'+expression.names[data.language]+'</option>');
					$.sexyCombo.changeOptions('.expression-list');
				}
			}
		});
	});
</script>