<div id="add-survey" class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">Create Survey</h3>
		<g:locales/>
		<div class="clear"></div>
	</div>
	<g:form url="[controller:'createSurvey', action:'save']" useToken="true">
		<g:i18nInput name="names" bean="${survey}" value="${survey?.names}" label="Name" field="names"/>
		<g:i18nRichTextarea name="descriptions" bean="${survey}" value="${survey?.descriptions}" label="Descriptions" field="descriptions" height="100"  width="300" maxHeight="100" />
	   <div class="row">
			<div>
				<a id="add-iteration-link" class="float-right"  href="${createLink(controller:'iteration', action:'create')}">New Iteration</a>
			</div>
			<div class="clear"></div>
			<div id="iteration-block">
					<div class="group-list ${hasErrors(bean:survey, field:'period', 'errors')}">
						<label for="period">Period:</label>
						<select class="iteration-list" name="period.id">
							<option value="null">-- Select an Iteration --</option>
							<g:each in="${periods}" var="period">
								<option value="${period.id}" ${period.id+''==fieldValue(bean: survey, field: 'period.id')+''?'selected="selected"':''}>
									${period.startDate} &harr; ${period.endDate}
								</option>
							</g:each>
						</select>
						<div class="error-list"><g:renderErrors bean="${survey}" field="period" /></div>
					</div>
			</div>
		</div>
		<div class="clear"></div>
		<g:if test="${survey?.id != null}">
			<input type="hidden" name="id" value="${survey?.id}"></input>
		</g:if>
		<div class="row">
			<button type="submit" class="rich-textarea-form">Save Survey</button>&nbsp;&nbsp;
			<button id="cancel-button">Cancel</button>
		</div>
    </g:form>
	<div class="clear"></div>
</div>
<div class="hidden flow-container"></div>
<script type="text/javascript">
	$(document).ready(function() {
		getRichTextContent();
		$('#add-survey').flow({
			addLinks: '#add-iteration-link',
			onSuccess: function(data) {
				if (data.result == 'success') {
					var period = data.newEntity;
					$('.iteration-list').append('<option value="'+period.id+'">'+period.startDate+'<->'+periodendDate+'</option>');
					$.sexyCombo.changeOptions('.iteration-list');
				}
			}
		});
	})
		</script>