<div id="add-checkbox-question" class="entity-form-container togglable">

	<div class="entity-form-header">
		<h3 class="title">
			<g:message code="default.new.label" args="${[message(code:'survey.checkboxquestion.label',default:'Checkbox Question')]}"/>
		</h3>
		<g:locales />
		<div class="clear"></div>
	</div>
	
	<g:form url="[controller:'checkboxQuestion', action:'save']" useToken="true">
		<g:i18nRichTextarea name="names" bean="${question}" value="${question.names}" label="Question" field="names" height="250" width="400" maxHeight="250" />
		<g:input name="order" label="Order" bean="${question}" field="order"/>
		
		<input type="hidden" name="descriptions.jsonText" value=" "/>
		
		<table id="question-option">
			<g:each in="${question.options}" status="i" var="option">
				<tr class="white-box"> 
					<td id="question-option-${option.id}"><g:render template="/survey/admin/checkboxOption" model="[option: option]" /></td>
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
			<label for="section.id"><g:message code="general.text.section" default="Section"/>:</label>
			<select class="section-list" name="section.id">
				<option value="null">-- <g:message code="survey.selectasection.label" default="Select a Section"/> --</option>
				<g:each in="${sections}" var="section">
					<option value="${section.id}" ${section.id+''==fieldValue(bean: question, field: 'section.id')+''?'selected="selected"':''}>
						<g:i18n field="${section.names}"/>
					</option>
				</g:each>
			</select>
			<div class="error-list"><g:renderErrors bean="${question}" field="section" /></div>
		</div>
		<div class="row ${hasErrors(bean:question, field:'groupUuidString', 'errors')}">
			<label for="groups"><g:message code="general.text.facilitygroups" default="Facility Groups"/>:</label>
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
			<button type="submit" class="rich-textarea-form"><g:message code="default.button.save.label" default="Save"/></button>
			<button id="cancel-button"><g:message code="general.text.cancel" default="Cancel"/></button>
		</div>
	</g:form>
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
		$('#add-checkbox-question').flow({
			addLinks : [ selector ],
			onSuccess : function(data) {
				if (data.result == 'success') {
					var checkboxOptionHtml = data.html;
					var checkboxOptionId = data.newEntity;
					var selector = '#question-option-'+checkboxOptionId.id;
					$(selector).replaceWith(
						'<td id="question-option-'+checkboxOptionId.id+'">'+checkboxOptionHtml+'</td>');
				}
			 	getEditOption('#question-option-'+checkboxOptionId.id+' .flow-edit-option');
			}
		});
	}
	
	function getAddedOption(selector) {
		$('#add-checkbox-question').flow({
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