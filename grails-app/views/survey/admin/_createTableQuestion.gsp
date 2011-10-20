<div id="add-table-question" class="entity-form-container togglable">

	<div class="entity-form-header">
		<h3 class="title">
			<g:message code="default.new.label" args="${[message(code:'survey.tablequestion.label',default:'Table Question')]}"/>
		</h3>
		<g:locales />
		<div class="clear"></div>
	</div>
	
	<g:form url="[controller:'tableQuestion', action:'save']" useToken="true">
		<g:i18nInput name="tableNames" bean="${question}" value="${question.tableNames}" label="Table Names" field="tableNames"/>
		<g:i18nRichTextarea name="names" bean="${question}" value="${question.names}" label="Question" field="names" height="250" width="380" maxHeight="250" />
		
		<input type="hidden" name="descriptions.jsonText" value=" "/>
		
		<g:if test="${question.id != null}">
			<div class="float-right">
				<input type="hidden" name="id" value="${question.id}"></input>
				<a class="flow-add-column" href="${createLink(controller:'tableColumn', action:'create',params:[questionId: question.id])}">
				  	<g:message code="survey.addcolumn.label" default="Add Column"/>
				</a>(<span id="column-number">${question.columns.size()}</span>) | 
				<a class="flow-add-row" href="${createLink(controller:'tableRow', action:'create',params:[questionId: question.id])}">
					 <g:message code="survey.addrow.label" default="Add Row"/>
				</a>(<span id="row-number">${question.rows.size()}</span>) |
				<a class="flow-preview" href="${createLink(controller:'tableQuestion', action:'preview',params:[questionId: question.id])}">
				 <g:message code="general.text.preview" default="Preview"/></a>
			</div>
		</g:if>
		<g:input name="order" label="Order" bean="${question}" field="order"/>
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
		<div class="row">
			<button type="submit" class="rich-textarea-form"><g:message code="default.button.save.label" default="Save"/></button>
			<button id="cancel-button"><g:message code="general.text.cancel" default="Cancel"/></button>
		</div>
	</g:form>
	<div class="clear"></div>
</div>
<div class="hidden flow-container"></div>

<script type="text/javascript">
	
	$(document).ready(
			function() {
				getRichTextContent();

				$('#add-table-question').flow({
					addLinks : [ '.flow-preview' ],
					onSuccess : function(data) {
					}
				});
				
				$('#add-table-question').flow(
						{
							addLinks : [ '.flow-add-column' ],
							onSuccess : function(data) {
								if (data.result == 'success') {
									var colsNum = (parseInt($('#column-number')
											.text()) + 1).toString();
									$('#column-number').text(colsNum);
								}
							}
						});
				
				$('#add-table-question').flow(
						{
							addLinks : [ '.flow-add-row' ],
							onSuccess : function(data) {
								if (data.result == 'success') {
									var rowsNum = (parseInt($('#row-number')
											.text()) + 1).toString();
									$('#row-number').text(rowsNum);
								}
							}
						});

			})
</script>