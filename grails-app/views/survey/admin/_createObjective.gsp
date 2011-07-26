<div id="add-objective" class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">Create Objective</h3>
		<g:locales />
		<div class="clear"></div>
	</div>

	<g:form url="[controller:'objective', action:'save']" useToken="true">
		<g:i18nInput name="names" bean="${objective}"
			value="${objective?.names}" label="Name" field="names" />
		<g:i18nTextarea name="descriptions" bean="${objective}"
			value="${objective?.descriptions}" label="Description"
			field="descriptions" />
		<div class="row">
			<div id="survey-block">
				<input type="hidden" name="survey.id" value="${objective.survey.id}" />
			    <div class="row"><label for="survey">Survey:</label> <g:i18n field="${objective.survey.names}"/></div>
		    <div class="clear"></div>

			</div>
		</div>
		<div class="row">
			<div id="orgunitgroup-block">
				<div
					class="group-list ${hasErrors(bean:objective, field:'groupUuidString', 'errors')}">
					<label for="groups">Organisation Unit Group:</label>
						<select class="group-list" name="groupUuids" multiple="multiple" size="5" >
							<g:each in="${groups}" var="group">
								<option value="${group.uuid}" ${groupUuids.contains(group.uuid)?'selected="selected"':''}>
						           ${group.name}
					            </option>
							</g:each>
						</select>
					<div class="error-list">
						<g:renderErrors bean="${objective}" field="groupUuidString" />
					</div>
				</div>
			</div>
		</div>
		<g:input name="order" label="Order" bean="${objective}" field="order"/>
		<g:if test="${objective.id != null}">
			<input type="hidden" name="id" value="${objective.id}"></input>
		</g:if>
		<div class="row">
			<button type="submit">Save Objective</button>
			&nbsp;&nbsp;
			<button id="cancel-button">Cancel</button>
		</div>
	</g:form>
	<div class="clear"></div>
</div>
<div class="hidden flow-container"></div>
<script type="text/javascript">
	$(document).ready(function() {
			$('#add-objective').flow({
				addLinks : '#new-survey-link',
				onSuccess : function(data) {
				    if (data.result == 'success') {
						var period = data.newEntity;
						  $('.survey-list').append('<option value="'+survey.id+'">'+ survey.names[data.local] + '</option>');
						  $.sexyCombo.changeOptions('.survey-list');
					}
				}
			});
		})					
</script>
