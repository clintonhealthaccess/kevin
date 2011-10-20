<div id="add-survey" class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">
			<g:message code="default.new.label" args="${[message(code:'general.text.survey',default:'Survey')]}"/>
		</h3>
		<g:locales/>
		<div class="clear"></div>
	</div>
	<g:form url="[controller:'survey', action:'save']" useToken="true">
		<g:i18nInput name="names" bean="${survey}" value="${survey?.names}" label="Name" field="names"/>
		<g:i18nRichTextarea name="descriptions" bean="${survey}" value="${survey?.descriptions}" label="Descriptions" field="descriptions" height="100"  width="300" maxHeight="100" />
		
		<div class="row">
			<div>
				<a id="add-iteration-link" class="float-right"  href="${createLink(controller:'iteration', action:'create')}">
					<g:message code="default.new.label" args="${[message(code:'general.text.iteration',default:'Iteration')]}"/>
				</a>
			</div>
			<div class="clear"></div>
			<div id="iteration-block">
				<div class="${hasErrors(bean:survey, field:'period', 'errors')}">
					<label for="period.id"><g:message code="general.text.period"default="Period"/>:</label>
					<select class="iteration-list" name="period.id">
						<option value="null">-- <g:message code="survey.selectaniteration.label" default="Select an Iteration"/> --</option>
						<g:each in="${periods}" var="period">
							<option value="${period.id}" ${period.id==survey.period?.id?'selected="selected"':''}>
								${period.startDate} &harr; ${period.endDate}
							</option>
						</g:each>
					</select>
					<div class="error-list"><g:renderErrors bean="${survey}" field="period" /></div>
				</div>
			</div>
		</div>
		
		<div class="row">
			<div id="iteration-block">
				<div class="${hasErrors(bean:survey, field:'period', 'errors')}">
					<label for="lastPeriod.id"><g:message code="survey.lastperiodforreference.label" default="Last period (for reference to old values)"/>:</label>
					<select class="iteration-list" name="lastPeriod.id">
						<option value="null">-- <g:message code="survey.selectaniteration.label" default="Select an Iteration"/> --</option>
						<g:each in="${periods}" var="period">
							<option value="${period.id}" ${period.id==survey.lastPeriod?.id?'selected="selected"':''}>
								${period.startDate} &harr; ${period.endDate}
							</option>
						</g:each>
					</select>
					<div class="error-list"><g:renderErrors bean="${survey}" field="lastPeriod" /></div>
				</div>
			</div>
		</div>
		
		<div class="row">
			<label><g:message code="general.text.active" default="Active"/></label>
			<g:checkBox name="active" value="${survey.active}" />
		</div>
		
		<div class="clear"></div>
		<g:if test="${survey?.id != null}">
			<input type="hidden" name="id" value="${survey?.id}"></input>
		</g:if>
		<div class="row">
			<button type="submit" class="rich-textarea-form"><g:message code="default.button.save.label" default="Save"/></button>&nbsp;&nbsp;
			<button id="cancel-button"><g:message code="general.text.cancel" default="Cancel"/></button>
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
					$('.iteration-list').append('<option value="'+period.id+'">'+period.startDate+'<->'+period.endDate+'</option>');
				}
			}
		});
	})
</script>