<div class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">
			<g:message code="default.new.label" args="[message(code:'survey.section.label',default:'Section')]"/>
		</h3>
		<g:locales />
		<div class="clear"></div>
	</div>

	<g:form url="[controller:'section', action:'save', params:[targetURI:targetURI]]" useToken="true">
		<g:i18nInput name="names" bean="${section}" value="${section?.names}" label="Name" field="names" />
		<g:i18nRichTextarea name="descriptions" bean="${section}" value="${section?.descriptions}" label="Descriptions" field="descriptions" height="100"  width="300" maxHeight="100" />
			
		<div class="row">
			<div class="${hasErrors(bean:section, field:'objective', 'errors')}">
				<label for="objective.id"><g:message code="survey.objective.label" default="Objective"/>:</label>
				<select class="objective-list" name="objective.id">
					<option value="null">-- <g:message code="survey.selectanobjective.label" default="Select an Objective"/> --</option>
					<g:each in="${objectives}" var="objective">
						<option value="${objective.id}" ${objective.id+''==fieldValue(bean: section, field: 'objective.id')+''?'selected="selected"':''}>
							<g:i18n field="${objective.names}"/>
						</option>
					</g:each>
				</select>
				<div class="error-list"><g:renderErrors bean="${section}" field="objective" /></div>
			</div>
		</div>
		<div class="row">
			<div class="${hasErrors(bean:section, field:'groupUuidString', 'errors')}">
				<label for="groups"><g:message code="facility.type.label" default="Facility Groups"/>:</label>
					<select  name="groupUuids" multiple="multiple" size="5" >
						<g:each in="${groups}" var="group">
							<option value="${group.uuid}" ${groupUuids.contains(group.uuid)?'selected="selected"':''}>
					           ${group.name}
				            </option>
						</g:each>
					</select>
				<div class="error-list">
					<g:renderErrors bean="${section}" field="groupUuidString" />
				</div>
			</div>
		</div>
		<g:input name="order" label="Order" bean="${section}" field="order"/>
		<g:if test="${section.id != null}">
			<input type="hidden" name="id" value="${section.id}"></input>
		</g:if>
		<div class="row">
			<button type="submit" class="rich-textarea-form"><g:message code="default.button.save.label" default="Save"/></button>
			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label" default="Cancel"/></a>
		</div>
	</g:form>
	<div class="clear"></div>
</div>
<script type="text/javascript">
	$(document).ready(function() {
		getRichTextContent();	
	})					
</script>
