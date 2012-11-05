<div class="entity-form-container togglable">

	<div class="entity-form-header">
		<h3 class="title">
			<g:message code="default.new.label" args="[message(code:'survey.tablequestion.label')]"/>
		</h3>
		<g:locales />
	</div>
	
	<g:form url="[controller:'tableQuestion', action:'save', params:[targetURI: targetURI]]" useToken="true">
		<g:i18nInput name="tableNames" bean="${question}" value="${question.tableNames}" label="${message(code:'survey.tablequestion.caption.label')}" field="tableNames"/>
		<g:i18nRichTextarea name="names" bean="${question}" value="${question.names}" label="${message(code:'survey.question.label')}" field="names" height="100" width="380" maxHeight="250" />
		<g:i18nRichTextarea name="descriptions" bean="${question}" value="${question.descriptions}" label="${message(code:'survey.question.description.label')}" field="descriptions" height="250" width="380" maxHeight="150" />
		
		<g:if test="${question.id != null}">
			<input type="hidden" name="id" value="${question.id}"></input>
			<div class="row">
				<a href="${createLinkWithTargetURI(controller:'tableQuestion', action:'preview',params:['question': question.id])}">
					<g:message code="survey.tablequestion.preview.label"/></a>
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
					<a href="${createLinkWithTargetURI(controller:'tableColumn', action:'create', params:['question.id': question.id])}">
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
					<a href="${createLinkWithTargetURI(controller:'tableRow', action:'create', params:['question.id': question.id])}">
						<g:message code="default.add.label" args="[message(code:'survey.tablequestion.tablerow.label')]" />
					</a>
				</div>
			</div>
		</g:if>
		
		<g:input name="code" label="${message(code:'entity.code.label')}" bean="${question}" field="code" />
		<g:input name="order" label="${message(code:'entity.order.label')}" bean="${question}" field="order"/>
		
		<g:selectFromList name="section.id" label="${message(code:'survey.section.label')}" field="section" optionKey="id" multiple="false"
			from="${sections}" value="${question.section?.id}" bean="${question}" values="${sections.collect {i18n(field:it.names)}}" />

		<g:selectFromList name="typeCodes" label="${message(code:'entity.datalocationtype.label')}" bean="${question}" field="typeCodeString" 
			from="${types}" value="${question.typeCodes*.toString()}" values="${types.collect{i18n(field:it.names)}}" optionKey="code" multiple="true"/>

		<div class="row">
			<button type="submit" class="rich-textarea-form"><g:message code="default.button.save.label"/></button>
			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label"/></a>
		</div>
	</g:form>
	<div class="clear"></div>
</div>
