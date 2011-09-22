<div id="add-enum" class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">Create Enum</h3>
		<g:locales/>
		<div class="clear"></div>
	</div>
	<g:form url="[controller:'enum', action:'save']" useToken="true">
		<g:i18nInput name="names" bean="${enumeration}" value="${enumeration?.names}" label="Name" field="names" />
		
		<g:i18nTextarea name="descriptions" bean="${enumeration}" value="${enumeration.descriptions}" label="Descriptions" field="descriptions" height="150"  width="300" maxHeight="150" />
		
		<g:input name="code" label="Code" bean="${enumeration}" field="code" />
		<div>
			<table id="enum-option">
				<g:each in="${enumeration.enumOptions}" status="i" var="option">
					<tr>
						<td id="enum-option-${option.id}">
							<g:render template="/templates/enumOption" model="[option: option]" />
						</td>
					</tr>
				</g:each>
			</table>
			<g:if test="${enumeration.id != null}">
				<div>
					<a class="add-option-link flow-add float-right" href="${createLink(controller:'enumOption', action:'create',params:[enumId: enumeration.id])}">
						<g:message code="general.text.addOption" default="Add Option" />
					</a>
				</div>
			</g:if>
			<div class="clear"></div>
		</div>

		<g:if test="${enumeration.id != null}">
			<input type="hidden" name="id" value="${enumeration.id}"></input>
		</g:if>
		
		<div class="row">
			<button type="submit" class="rich-textarea-form">Save Enum</button>
			<button id="cancel-button">Cancel</button>
		</div>
	</g:form>
	<div class="clear"></div>
</div>
<div class="hidden flow-container"></div>
<script type="text/javascript">
	$(document).ready(function() {
		getRichTextContent();
		getEditOption('.flow-edit-option');
		getAddedOption('.add-option-link');
	});
		
	function getEditOption(selector) {
		$('#add-enum').flow({
			addLinks : [ selector ],
			onSuccess : function(data) {
				if (data.result == 'success') {
				var checkboxOptionHtml = data.html;
				var checkboxOptionId = data.newEntity;
				var selector = '#enum-option-'
						+ checkboxOptionId.id;
				$(selector).replaceWith(
						'<td id="enum-option-'+checkboxOptionId.id+'">'
								+ checkboxOptionHtml + '</td>');
				}
			 	getEditOption('#enum-option-'+checkboxOptionId.id+' .flow-edit-option');
			}
		});
	}
	
	function getAddedOption(selector) {
		$('#add-enum').flow({
			addLinks : [ '.add-option-link' ],
			onSuccess : function(data) {
				if (data.result == 'success') {
					var checkboxOptionHtml = data.html;
					var checkboxOptionId = data.newEntity;
					$('#enum-option').append(
							'<tr><td id="enum-option-'+checkboxOptionId.id+'">'
									+ checkboxOptionHtml + '</td></tr>');
					getEditOption('#enum-option-'+checkboxOptionId.id+' .flow-edit-option');
				}
			}
		});
	}
</script>