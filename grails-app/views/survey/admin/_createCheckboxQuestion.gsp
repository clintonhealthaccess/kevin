<div id="add-question" class="entity-form-container togglable">

	<div class="entity-form-header">
		<h3 class="title">Create a Checkbox Question</h3>
		<g:locales />
		<div class="clear"></div>
	</div>
	<div id="add-question-col">
		<g:form url="[controller:'checkboxQuestion', action:'save']" useToken="true">
			<g:i18nRichTextarea name="names" bean="${question}" value="${question.names}" label="Question" field="names" height="250" width="400" maxHeight="250" />
			<g:input name="order" label="Order" bean="${question}" field="order"/>
			
			<input type="hidden" name="descriptions.jsonText" value=" "/>
			
			<table id="question-option">
				<g:each in="${question.options}" status="i" var="option">
					<tr class="question-option"> 
						<td id="question-option-${option.id}"><g:render template="/templates/checkboxOption" model="[option: option]" /></td>
					</tr>
				</g:each>
			</table>
			<g:if test="${question.id != null}">
				<div>
					<a id="add-option-link" class="flow-add float-right" href="${createLink(controller:'checkboxOption', action:'create',params:[questionId: question.id])}">
					<g:message code="general.text.addOption" default="Add Option" />
					</a>
				</div>
			</g:if>
			<div class="row ${hasErrors(bean:question, field:'section', 'errors')}">
				<label for="section.id">Section:</label>
				<select class="section-list" name="section.id">
					<option value="null">-- Select an Section --</option>
					<g:each in="${sections}" var="section">
						<option value="${section.id}" ${section.id+''==fieldValue(bean: question, field: 'section.id')+''?'selected="selected"':''}>
							<g:i18n field="${section.names}"/>
						</option>
					</g:each>
				</select>
				<div class="error-list"><g:renderErrors bean="${question}" field="section" /></div>
			</div>
			<div class="row ${hasErrors(bean:question, field:'groupUuidString', 'errors')}">
				<label for="groups" class="display-in-block">Organisation Unit Group:</label>
				<select class="group-list" name="groupUuids" multiple="multiple" size="5" >
					<g:each in="${groups}" var="group">
						<option value="${group.uuid}" ${groupUuids.contains(group.uuid)?'selected="selected"':''}>
				           ${group.name}
			            </option>
					</g:each>
				</select>
				<div class="error-list">
					<g:renderErrors bean="${question}" field="groupUuidString" />
				</div>
			</div>
			<g:if test="${question.id != null}">
				<input type="hidden" name="id" value="${question.id}"></input>
			</g:if>
			<div class="row">
				<button type="submit" class="rich-textarea-form">Save Question</button>
				<button id="cancel-button">Cancel</button>
			</div>
		</g:form>
	</div>
	<div class="clear"></div>
</div>

<div class="hidden flow-container"></div>

<script type="text/javascript">
$(document).ready(function() {
	getRichTextContent();
	getEditOption('.flow-edit-option');
	getAddedOption('#add-option-link');
});

function getEditOption(selector) {
	$('#add-question').flow({
		addLinks : [ selector ],
		onSuccess : function(data) {
			if (data.result == 'success') {
			var checkboxOptionHtml = data.html;
			var checkboxOptionId = data.newEntity;
			var selector = '#question-option-'
					+ checkboxOptionId.id;
			$(selector).replaceWith(
					'<td id="question-option-'+checkboxOptionId.id+'">'
							+ checkboxOptionHtml + '</td>');
			}
		 	getEditOption('#question-option-'+checkboxOptionId.id+' .flow-edit-option');
		}
	});
}

function getAddedOption(selector) {
	$('#add-question').flow({
		addLinks : [ '#add-option-link' ],
		onSuccess : function(data) {
			if (data.result == 'success') {
				var checkboxOptionHtml = data.html;
				var checkboxOptionId = data.newEntity;
				$('#question-option').append(
						'<tr class="question-option"><td id="question-option-'+checkboxOptionId.id+'">'
								+ checkboxOptionHtml + '</td></tr>');
				getEditOption('#question-option-'+checkboxOptionId.id+' .flow-edit-option');
			}
		}
	});
}
</script>