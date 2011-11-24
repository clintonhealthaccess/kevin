<div class="entity-form-container togglable">

	<div class="entity-form-header">
		<h3 class="title">
			<g:message code="default.new.label" args="[message(code:'survey.tablequestion.label',default:'Table Question')]"/>
		</h3>
		<g:locales />
		<div class="clear"></div>
	</div>
	
	<g:form url="[controller:'tableQuestion', action:'save', params:[targetURI: targetURI]]" useToken="true">
		<g:i18nInput name="tableNames" bean="${question}" value="${question.tableNames}" label="Table Names" field="tableNames"/>
		<g:i18nRichTextarea name="names" bean="${question}" value="${question.names}" label="Question" field="names" height="100" width="380" maxHeight="250" />
		<g:i18nRichTextarea name="descriptions" bean="${question}" value="${question.descriptions}" label="Help Text" field="descriptions" height="250" width="380" maxHeight="150" />
		
		<g:if test="${question.id != null}">
			<input type="hidden" name="id" value="${question.id}"></input>
			<div class="row">
				<a href="${createLinkWithTargetURI(controller:'tableQuestion', action:'preview',params:[questionId: question.id])}">
					<g:message code="survey.tablequestion.preview.label" default="Preview"/></a>
				</a>
			</div>
			
			<div class="row">
				<a href="#" onclick="$(this).next().toggle();return false;"><g:message code="survey.tablequestion.tablecolumn.label"/>:</a>
				<div class="hidden">
					<ul>
						<g:each in="${columns}" status="i" var="column">
							<li>
								<g:render template="/survey/admin/tableColumn" model="[column: column, index: i]" />
							</li>
						</g:each>
					</ul>
					<a href="${createLinkWithTargetURI(controller:'tableColumn', action:'create', params:[questionId: question.id])}">
						<g:message code="default.add.label" args="[message(code:'survey.tablequestion.tablecolumn.label')]" />
					</a>
				</div>
			</div>
			
			<div class="row">
				<a href="#" onclick="$(this).next().toggle();return false;"><g:message code="survey.tablequestion.tablerow.label"/>:</a>
				<div class="hidden">
					<ul>
						<g:each in="${rows}" status="i" var="row">
							<li>
								<g:render template="/survey/admin/tableRow" model="[row: row, index: i]" />
							</li>
						</g:each>
					</ul>
					<a href="${createLinkWithTargetURI(controller:'tableRow', action:'create', params:[questionId: question.id])}">
						<g:message code="default.add.label" args="[message(code:'survey.tablequestion.tablerow.label')]" />
					</a>
				</div>
			</div>
		</g:if>
		
		<g:input name="order" label="Order" bean="${question}" field="order"/>
		<div class="row ${hasErrors(bean:question, field:'section', 'errors')}">
			<label for="section.id"><g:message code="survey.section.label" default="Section"/>:</label>
			<select class="section-list" name="section.id">
				<option value="null">-- <g:message code="default.select.label" args="[message(code:'survey.section.label')]" default="Select a Section"/> --</option>
				<g:each in="${sections}" var="section">
					<option value="${section.id}" ${section.id+''==fieldValue(bean: question, field: 'section.id')+''?'selected="selected"':''}>
						<g:i18n field="${section.names}"/>
					</option>
				</g:each>
			</select>
			<div class="error-list"><g:renderErrors bean="${question}" field="section" /></div>
		</div>
		
		<g:selectFromList name="groupUuids" label="${message(code:'facility.type.label')}" bean="${question}" field="groupUuidString" 
			from="${groups}" value="${question.groupUuids*.toString()}" optionValue="name" optionKey="uuid" multiple="true"/>

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
	});
</script>