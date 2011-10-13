<div id="add-enum" class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">Create Enum</h3>
		<g:locales/>
		<div class="clear"></div>
	</div>
	<g:form url="[controller:'enum', action:'save', params:[targetURI: targetURI]]" useToken="true">
		<g:i18nInput name="names" bean="${enumeration}" value="${enumeration?.names}" label="Name" field="names" />
		
		<g:i18nTextarea name="descriptions" bean="${enumeration}" value="${enumeration.descriptions}" label="Descriptions" field="descriptions" height="150"  width="300" maxHeight="150" />
		
		<g:if test="${enumeration.id != null}">		
			<p class="red">Warning: some survey fields might not be displayed correctly if the code is changed.</p>
		</g:if>
		<g:input name="code" label="Code" bean="${enumeration}" field="code" />
	
		<table id="enum-option">
			<g:each in="${enumeration.enumOptions}" status="i" var="option">
				<tr class="white-box">
					<td id="enum-option-${option.id}">
						<g:render template="/entity/data/enumOption" model="[option: option]" />
					</td>
				</tr>
			</g:each>
		</table>
			
		<g:if test="${enumeration.id != null}">
			<input type="hidden" name="id" value="${enumeration.id}"></input>
		</g:if>
		
		<div class="row">
			<button type="submit" class="rich-textarea-form">Save Enum</button>
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