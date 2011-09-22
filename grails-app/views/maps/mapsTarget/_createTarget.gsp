<%@ page import="org.chai.kevin.maps.MapsTarget.MapsTargetType" %>

<div id="add-maps-target" class="entity-form-container">

	<div class="entity-form-header">
		<h3 class="title">Maps target</h3>
		<g:locales/>
		<div class="clear"></div>
	</div>

	<g:form url="[controller:'mapsTarget', action:'save']" useToken="true">
		<g:i18nInput name="names" bean="${target}" value="${target.names}" label="Name" field="names"/>
		<g:i18nTextarea name="descriptions" bean="${target}" value="${target.descriptions}" label="Description" field="descriptions"/>
		<g:input name="code" label="Code" bean="${target}" field="code"/>
	
		<g:selectFromEnum name="type" bean="${target}" values="${MapsTargetType.values()}" field="type" label="Type"/>
		
		<div class="row">
			<h5>Expressions</h5>
			<div class="float-right">
				<a id="add-expression-link" href="${createLink(controller:'expression', action:'create')}">new expression</a>
			</div>
			<div class="clear"></div>
			
			<div class="selectable" data-type="AGGREGATION">
				<div class="${hasErrors(bean:target, field:'expression', 'errors')}">
					<label for="expression.id">Expression:</label>
					<select class="expression-list" name="expression.id">
						<option value="null">-- select an expression --</option>
						<g:each in="${expressions}" var="expression">
							<option value="${expression.id}" ${expression.id==target.expression?.id?'selected="selected"':''}>
								<g:i18n field="${expression.names}"/>
							</option>
						</g:each>
					</select>
					<div class="error-list"><g:renderErrors bean="${target}" field="expression" /></div>
				</div>
				<div class="row ${hasErrors(bean:target,field:'maxValue','errors')}">
					<label for="maxValue">Maximum value</label>
					<input type="text" name="maxValue" value="${fieldValue(bean:target,field:'maxValue')}"></input>
					<div class="error-list"><g:renderErrors bean="${target}" field="maxValue" /></div>
				</div>
			</div>
			
			<div class="selectable" data-type="AVERAGE">
				<g:each status="i" in="${groups}" var="group">
					<div id="group-${group.id}" class="group-list">
						<label for="calculation.expressions[${group.uuid}].id">Expression for ${group.name}:</label>
						<select class="expression-list" name="calculation.expressions[${group.uuid}].id">
							<option value="null">-- disabled --</option>
							<g:each in="${expressions}" var="expression">
								<option value="${expression.id}" ${target.calculation?.expressions==null?'':target.calculation?.expressions[group.uuid]?.id==expression.id?'selected="selected"':''}>
									<g:i18n field="${expression.names}"/>
								</option>
							</g:each>
						</select>
					</div>
				</g:each>
				<g:if test="${target?.calculation?.id != null}">
					<input type="hidden" name="calculation.id" value="${target.calculation.id}"></input>
				</g:if>
			</div>
		</div>

		<div class="row ${hasErrors(bean:target,field:'order','errors')}">
			<label for="order">Order</label>
			<input type="text" name="order" value="${fieldValue(bean:target,field:'order')}"></input>
			<div class="error-list"><g:renderErrors bean="${target}" field="order" /></div>
		</div>
		
		<g:if test="${target?.id != null}">
			<input type="hidden" name="id" value="${target.id}"></input>
		</g:if>
		
		<div class="row">
			<button type="submit">Save target</button>
			<button id="cancel-button">Cancel</button>
		</div>
    </g:form>
	<div class="clear"></div>
</div>

<div class="hidden flow-container"></div>

<script type="text/javascript)">
	$(document).ready(function() {
		$('#add-maps-target').flow({
			addLinks: '#add-expression-link',
			onSuccess: function(data) {
				if (data.result == 'success') {
					var expression = data.newEntity
					$('.expression-list').append('<option value="'+expression.id+'">'+expression.name+'</option>');
// 					$.sexyCombo.changeOptions('.expression-list');
				}
			}
		});
		
		selectMapsType();
		$('#add-maps-target select[name="type"]').bind('change', function(){
			selectMapsType();
		});
	});
	function selectMapsType() {
		var value = $('#add-maps-target select[name="type"]').val();
		$('#add-maps-target .selectable').each(function(index, element){
			if ($(element).data('type') != value) $(element).hide();
			else $(element).show();
		});
	}
</script>
