<div class="entity-form-container togglable">

	<div class="entity-form-header">
		<h3 class="title">Create a Checkbox Question</h3>
		<g:locales />
		<div class="clear"></div>
	</div>
	
	<g:form url="[controller:'checkboxQuestion', action:'save', params:[targetURI: targetURI]]" useToken="true">
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
				<a class="float-right" href="${createLinkWithTargetURI(controller:'checkboxOption', action:'create', params:[questionId: question.id])}">
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
			<label for="groups">Organisation Unit Group:</label>
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
			<a href="${createLink(uri: targetURI)}">cancel</a>
		</div>
	</g:form>
	<div class="clear"></div>
</div>

<script type="text/javascript">
	$(document).ready(function() {
		getRichTextContent();
	});
</script>