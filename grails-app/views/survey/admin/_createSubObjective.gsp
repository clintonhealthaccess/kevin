<div id="add-subobjective" class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">Create Sub-Objective</h3>
		<g:locales />
		<div class="clear"></div>
	</div>

	<g:form url="[controller:'subObjective', action:'save']" useToken="true">
		<g:i18nInput name="names" bean="${subobjective}"
			value="${subobjective?.names}" label="Name" field="names" />
		<g:i18nTextarea name="descriptions" bean="${subobjective}"
			value="${subobjective?.descriptions}" label="Description"
			field="descriptions" />
			<div class="row">
			<div id="objective-block">
					<div class="group-list ${hasErrors(bean:subobjective, field:'objective', 'errors')}">
						<label for="objective.id">Objective:</label>
						<select class="objective-list" name="objective.id">
							<option value="null">-- Select an Objective --</option>
							<g:each in="${objectives}" var="objective">
								<option value="${objective.id}" ${objective.id+''==fieldValue(bean: subobjective, field: 'objective.id')+''?'selected="selected"':''}>
									<g:i18n field="${objective.names}"/>
								</option>
							</g:each>
						</select>
						<div class="error-list"><g:renderErrors bean="${subobjective}" field="objective" /></div>
					</div>
			</div>
			</div>
		<div class="row">
			<div id="orgunitgroup-block">
				<div
					class="group-list ${hasErrors(bean:subobjective, field:'groupUuidString', 'errors')}">
					<label for="groups">Organisation Unit Group:</label>
						<select class="group-list" name="groupUuids" multiple="multiple" size="5" >
							<g:each in="${groups}" var="group">
								<option value="${group.uuid}" ${groupUuids.contains(group.uuid)?'selected="selected"':''}>
						           ${group.name}
					            </option>
							</g:each>
						</select>
					<div class="error-list">
						<g:renderErrors bean="${subobjective}" field="groupUuidString" />
					</div>
				</div>
			</div>
		</div>
		<g:input name="order" label="Order" bean="${subobjective}" field="order"/>
		<g:if test="${subobjective.id != null}">
			<input type="hidden" name="id" value="${subobjective.id}"></input>
		</g:if>
		<div class="row">
			<button type="submit">Save Sub-Objective</button>
			&nbsp;&nbsp;
			<button id="cancel-button">Cancel</button>
		</div>
	</g:form>
	<div class="clear"></div>
</div>
<div class="hidden flow-container"></div>
<script type="text/javascript">
	$(document).ready(function() {
			$('#add-subobjective').flow({
				addLinks : '#new-subobjective-link',
				onSuccess : function(data) {
				    if (data.result == 'success') {
						var period = data.newEntity;
						  $('.objective-list').append('<option value="'+objective.id+'">'+ objective.names[data.local] + '</option>');
						  $.sexyCombo.changeOptions('.objective-list');
					}
				}
			});
		})					
</script>
