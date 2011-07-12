<%@ page import="org.chai.kevin.cost.CostTarget.CostType" %>

<div id="add-cost-target" class="entity-form-container togglable">

	<div class="entity-form-header">
		<h3 class="title">Costing target</h3>
		<g:locales/>
		<div class="clear"></div>
	</div>

	<g:form url="[controller:'costTarget', action:'save']" useToken="true">
		<g:i18nInput name="names" bean="${target}" value="${target.names}" label="Name" field="names"/>
		<g:i18nTextarea name="descriptions" bean="${target}" value="${target.descriptions}" label="Description" field="descriptions"/>
		<g:input name="code" label="Code" bean="${target}" field="code"/>

		<div class="row">
			<h5>Expressions</h5>
			<div class="float-right">
				<a id="add-expression-link" href="${createLink(controller:'expression', action:'create')}">new expression</a>
			</div>
			<div class="clear"></div>
			
			<div id="expressions-block">
				<g:each status="i" in="${['','End']}" var="suffix">
					<div class="group-list ${hasErrors(bean:target, field:'expression'+suffix, 'errors')}">
						<label for="expression${suffix}.id">${suffix} Expression:</label>
						<select class="expression-list" name="expression${suffix}.id">
							<option value="null">-- select an expression --</option>
							<g:each in="${expressions}" var="expression">
								<g:if test="${suffix == 'End'}">
									<option value="${expression.id}" ${expression.id==target.expressionEnd?.id?'selected="selected"':''}>
								</g:if>
								<g:else>
									<option value="${expression.id}" ${expression.id==target.expression?.id?'selected="selected"':''}>
								</g:else>
									<g:i18n field="${expression.names}"/>
								</option>
							</g:each>
						</select>
						<div class="error-list"><g:renderErrors bean="${target}" field="expression${suffix}" /></div>
					</div>
				</g:each>
			</div>
		</div>

		<div class="row ${hasErrors(bean:target, field:'groupUuidString', 'errors')}">
			<label for="groupUuids">Applies to:</label>
			<select class="group-list" name="groupUuids" multiple="multiple">
				<g:each in="${groups}" var="group">
					<option value="${group.uuid}" ${groupUuids.contains(group.uuid)?'selected="selected"':''}>
						${group.name}
					</option>
				</g:each>
			</select>
			<div class="error-list"><g:renderErrors bean="${target}" field="groupUuidString" /></div>
		</div>

		<div class="row ${hasErrors(bean:target,field:'costRampUp','errors')}">
			<label for="costRampUp.id">Ramp up</label>

			<div class="float-right">
				<a id="add-ramp-up-link" href="${createLink(controller:'costRampUp', action:'create')}">new ramp-up</a>
			</div>
	
			<select name="costRampUp.id" class="ramp-up-list">
				<g:each in="${costRampUps}" var="costRampUp">
					<option value="${costRampUp.id}" ${costRampUp.id==target.costRampUp?.id?'selected="selected"':''}>
						<g:i18n field="${costRampUp.names}"/>
					</option>
				</g:each>
			</select>
			<div class="error-list"><g:renderErrors bean="${target}" field="costRampUp" /></div>
			
		</div>		
		
		<g:selectFromEnum name="costType" bean="${target}" values="${CostType.values()}" field="costType" label="Type"/>
		<g:input name="order" label="Order" bean="${target}" field="order"/>
		
		<g:if test="${currentObjective != null}">
			<input type="hidden" name="currentObjective" value="${currentObjective.id}"></input>
		</g:if>
		<g:else>
			<input type="hidden" name="id" value="${target.id}"></input>
		</g:else>
		<div class="row">
			<button type="submit">Save target</button>
			<button id="cancel-button">Cancel</button>
		</div>
    </g:form>
	<div class="clear"></div>
</div>

<div class="hidden flow-container"></div>


<script type="text/javascript">
	$(document).ready(function() {
		$('#add-cost-target').flow({
			addLinks: '#add-ramp-up-link',
			onSuccess: function(data) {
				if (data.result == 'success') {
					var rampUp = data.newEntity;
					$('.ramp-up-list').append('<option value="'+rampUp.id+'">'+rampUp.names[data.locale]+'</option>');
// 					$.sexyCombo.changeOptions('.ramp-up-list');
				}
			}
		});
		
		$('#add-cost-target').flow({
			addLinks: '#add-expression-link',
			onSuccess: function(data) {
				if (data.result == 'success') {
					var expression = data.newEntity
					$('.expression-list').append('<option value="'+expression.id+'">'+expression.names[data.locale]+'</option>');
// 					$.sexyCombo.changeOptions('.expression-list');
				}
			}
		});
	});
</script>
