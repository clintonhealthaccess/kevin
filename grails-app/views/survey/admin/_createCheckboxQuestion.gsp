<div class="entity-form-container togglable">

	<div class="entity-form-header">
		<h3 class="title">
			<g:message code="default.new.label" args="[message(code:'survey.checkboxquestion.label')]"/>
		</h3>
		<g:locales />
	</div>
	
	<g:form url="[controller:'checkboxQuestion', action:'save', params:[targetURI: targetURI]]" useToken="true">
		<g:i18nRichTextarea name="names" bean="${question}" value="${question.names}" label="${message(code:'survey.question.label')}" field="names" height="100" width="400" maxHeight="250" />
		<g:i18nRichTextarea name="descriptions" bean="${question}" value="${question.descriptions}" label="${message(code:'survey.question.description.label')}" field="descriptions" height="250" width="400" maxHeight="150" />
		
		<g:input name="order" label="${message(code:'entity.order.label')}" bean="${question}" field="order"/>
		
		<g:if test="${question.id != null}">
			<div class="row">
				<a href="#" onclick="$(this).next().toggle();return false;"><g:message code="survey.checkboxquestion.checkboxoption.label"/>:</a>
				<div class="hidden">
					<ul>
						<g:each in="${options}" status="i" var="option">
							<li>
								<g:render template="/survey/admin/checkboxOption" model="[option: option, index: i]" />
							</li>
						</g:each>
					</ul>
					<a href="${createLinkWithTargetURI(controller:'checkboxOption', action:'create', params:['question.id': question.id])}">
						<g:message code="default.add.label" args="[message(code:'survey.checkboxquestion.checkboxoption.label')]" />
					</a>
				</div>
			</div>
		</g:if>
		
		<g:selectFromList name="section.id" label="${message(code:'survey.section.label')}" field="section" optionKey="id" multiple="false"
			from="${sections}" value="${question.section?.id}" bean="${question}" values="${sections.collect {i18n(field:it.names)}}" />
		
		<g:selectFromList name="typeCodes" label="${message(code:'entity.locationtype.label')}" bean="${question}" field="typeCodeString" 
			from="${types}" value="${question.typeCodes*.toString()}" values="${types.collect{i18n(field:it.names)}}" optionKey="code" multiple="true"/>

		<g:if test="${question.id != null}">
			<input type="hidden" name="id" value="${question.id}"></input>
		</g:if>
		<div class="row">
			<button type="submit" class="rich-textarea-form"><g:message code="default.button.save.label"/></button>
			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label"/></a>
		</div>
	</g:form>
	<div class="clear"></div>
</div>

<script type="text/javascript">
	$(document).ready(function() {
		getRichTextContent();
	});
</script>