<div id="add-enum-option" class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">Create Enum Option</h3>
		<g:locales/>
		<div class="clear"></div>
	</div>
	<g:form url="[controller:'enumOption', action:'save']" useToken="true">
	<input type="hidden" name="enume.id" value="${option.enume.id}"/>
	<g:i18nTextarea name="names" bean="${option}" value="${option.names}" label="Option" field="names" height="100"  width="300" maxHeight="100" />
	<g:i18nTextarea name="descriptions" bean="${option}" value="${option.descriptions}" label="Descriptions" field="descriptions" height="100"  width="300" maxHeight="100" />
	<g:input name="value" label="Value" bean="${option}" field="value"/>
	<g:input name="code" label="Code" bean="${option}" field="code"/>
	<g:input name="order" label="Order" bean="${option}" field="order"/>
		<g:if test="${option.id != null}">
			<input type="hidden" name="id" value="${option.id}"></input>
		</g:if>
		<div class="row">
			<button type="submit" class="rich-textarea-form">Save Enum Option</button>
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