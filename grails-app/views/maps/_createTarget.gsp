<div id="add-maps-target" class="entity-form-container">

	<div class="entity-form-header">
		<h3 class="title">Maps target</h3>
		<g:locales/>
		<div class="clear"></div>
	</div>

	<g:form url="[controller:'mapsTarget', action:'save', params:[targetURI:targetURI]]" useToken="true">
		<g:i18nInput name="names" bean="${target}" value="${target.names}" label="Name" field="names"/>
		<g:i18nTextarea name="descriptions" bean="${target}" value="${target.descriptions}" label="Description" field="descriptions"/>
		<g:input name="code" label="Code" bean="${target}" field="code"/>
	
		<g:selectFromList name="calculation.id" label="${message(code:'maps.target.calculation.label', default:'Calculation')}" bean="${target}" field="calculation" optionKey="id" multiple="false"
			from="${calculations}" value="${target.calculation?.id}" values="${calculations.collect{i18n(field:it.names)+' ['+it.code+'] ['+it.class.simpleName+']'}}" />
	
		<div class="row ${hasErrors(bean:target,field:'order','errors')}">
			<label for="order">Order</label>
			<input type="text" name="order" value="${fieldValue(bean:target,field:'order')}"></input>
			<div class="error-list"><g:renderErrors bean="${target}" field="order" /></div>
		</div>
		
		<g:if test="${target?.id != null}">
			<input type="hidden" name="id" value="${target.id}"></input>
		</g:if>
		
		<div class="row">
			<button type="submit"><g:message code="default.button.save.label" default="Save"/></button>&nbsp;&nbsp;
			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label" default="Cancel"/></a>
		</div>
    </g:form>
</div>

<script type="text/javascript">
	$(document).ready(function() {
		selectMapsType();
		$('#add-maps-target select[name=type]').bind('change', function(){
			selectMapsType();
		});
	});
	function selectMapsType() {
		var value = $('#add-maps-target select[name=type]').val();
		$('#add-maps-target .selectable').each(function(index, element){
			if ($(element).data('type') != value) $(element).hide();
			else $(element).show();
		});
	}
</script>
