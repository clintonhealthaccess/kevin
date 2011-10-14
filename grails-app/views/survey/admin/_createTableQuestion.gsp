<div class="entity-form-container togglable">

	<div class="entity-form-header">
		<h3 class="title">Create a Table Question</h3>
		<g:locales />
		<div class="clear"></div>
	</div>
	
	<g:form url="[controller:'tableQuestion', action:'save', params:[targetURI: targetURI]]" useToken="true">
		<g:i18nInput name="tableNames" bean="${question}" value="${question.tableNames}" label="Table Names" field="tableNames"/>
		<g:i18nRichTextarea name="names" bean="${question}" value="${question.names}" label="Question" field="names" height="100" width="380" maxHeight="250" />
		<g:i18nRichTextarea name="descriptions" bean="${question}" value="${question.descriptions}" label="Help Text" field="descriptions" height="250" width="380" maxHeight="150" />
		
		<g:if test="${question.id != null}">
			<div class="float-right">
				<input type="hidden" name="id" value="${question.id}"></input>
				<a href="${createLinkWithTargetURI(controller:'tableColumn', action:'create',params:[questionId: question.id])}">
					Add Column
				</a>
				(<span id="column-number">${question.columns.size()}</span>) | 
				<a href="${createLinkWithTargetURI(controller:'tableRow', action:'create',params:[questionId: question.id])}">
					Add Row
				</a>
				(<span id="row-number">${question.rows.size()}</span>) |
				<a href="${createLinkWithTargetURI(controller:'tableQuestion', action:'preview',params:[questionId: question.id])}">
					Preview
				</a>
			</div>
		</g:if>
		<g:input name="order" label="Order" bean="${question}" field="order"/>
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