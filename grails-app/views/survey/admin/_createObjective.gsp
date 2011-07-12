<div id="add-survey" class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">Create Survey</h3>
		<g:locales/>
		<div class="clear"></div>
	</div>
	
	<g:form url="[controller:'createSurvey', action:'save']" useToken="true">
		<g:i18nInput name="names" bean="${survey}" value="${survey?.names}" label="Name" field="names"/>
		<g:i18nTextarea name="descriptions" bean="${survey}" value="${survey?.descriptions}" label="Description" field="descriptions"/>
	   <div class="row">
			<div>
				<a id="add-iteration-link" class="float-right"  href="${createLink(controller:'iteration', action:'create')}">New Iteration</a>
			</div>
			<div class="clear"></div>
			<div id="iteration-block">
					<div class="group-list ${hasErrors(bean:period, field:'objective', 'errors')}">
						<label for="period.id">Period:</label>
						<select class="iteration-list" name="period.id">
							<option value="null">-- Select an Objective --</option>
							<g:each in="${periods}" var="period">
								<option value="${period.id}" ${objective.id+''==fieldValue(bean: target, field: 'objective.id')+''?'selected="selected"':''}>
									${period.startDate <--> period.endDate}
								</option>
							</g:each>
						</select>
						<div class="error-list"><g:renderErrors bean="${target}" field="objective" /></div>
					</div>
			</div>
		</div>
		<g:if test="${survey?.id != null}">
			<input type="hidden" name="id" value="${survey?.id}"></input>
		</g:if>
		<div class="row">
			<button type="submit">Save Survey</button>&nbsp;&nbsp;
			<button id="cancel-button">Cancel</button>
		</div>
    </g:form>
	<div class="clear"></div>
</div>
<script type="text/javascript">
	$(document).ready(function() {
		
		$('#add-iteration-link').flow({
			addLinks: '#new-dsr-objective-link',
			onSuccess: function(data) {
				if (data.result == 'success') {
					var period = data.newEntity;
					$('.iteration-list').append('<option value="'+period.id+'">'+period.startDate+'<->'+endDate+'</option>');
					$.sexyCombo.changeOptions('.iteration-list');
				}
			}
		});
	})
		</script>