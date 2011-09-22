<div id="add-validation-message" class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">Create a Validation Message</h3>
		<g:locales />
		<div class="clear"></div>
	</div>
    <g:form url="[controller:'validationMessage', action:'save']" useToken="true">
		<g:i18nRichTextarea name="messages" bean="${message}" value="${message.messages}" label="Messages" field="messages" height="150"  width="400" maxHeight="100" />
		<g:if test="${message.id != null}">
			<g:if test="${!message.validationRules.isEmpty()}">
				<ul>
					<g:each in="${message.validationRules}" status="i" var="validationRule">
		   				<li>
							Data Element: <g:i18n field="${validationRule.surveyElement.dataElement.names}" />
						</li>
					</g:each>
				</ul>
			</g:if>
			<input type="hidden" name="id" value="${message.id}"></input>
		</g:if>
		<div class="row">
			<button type="submit" class="rich-textarea-form">Save Message</button>
			&nbsp;&nbsp;
			<button id="cancel-button">Cancel</button>
		</div>
	</g:form>
	<div class="clear"></div>
</div>
<div class="hidden flow-container"></div>
<script type="text/javascript">
	$(document).ready(function() {
		getRichTextContent();	 
	});					
</script>