<%@ page import="org.chai.kevin.cost.CostTarget.CostType" %>

<div id="add-cost-target" class="entity-form-container">
	<g:form url="[controller:'costTarget', action:'save']" useToken="true">
		<div class="row ${hasErrors(bean:target,field:'name','errors')}">
			<label for="name">Name</label>		
			<input name="name" value="${fieldValue(bean:target,field:'name')}"></input>
			<div class="error-list"><g:renderErrors bean="${target}" field="name" /></div>
		</div>
		<div class="row ${hasErrors(bean:target,field:'description','errors')}">
			<label for="description">Description</label>
			<textarea name="description" rows="5">${fieldValue(bean:target,field:'description')}</textarea>
			<div class="error-list"><g:renderErrors bean="${target}" field="description" /></div>
		</div>
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
								<option value="${expression.id}" ${expression.id+''==fieldValue(bean: target, field: 'expression'+suffix+'.id')+''?'selected="selected"':''}>
									${expression.name}
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
					<option value="${costRampUp.id}" ${costRampUp.id+''==fieldValue(bean:target, field: 'costRampUp.id')+''?'selected="selected"':''}>
						${costRampUp.name}
					</option>
				</g:each>
			</select>
			<div class="error-list"><g:renderErrors bean="${target}" field="costRampUp" /></div>
			
		</div>		
		
		<div class="row ${hasErrors(bean:target,field:'costType','errors')}">
			<label for="costType">Type</label>
			<select name="costType">
				<g:each in="${CostType.values()}" var="costType">
					<option value="${costType.key}" ${costType.key+''==fieldValue(bean:target, field:'costType.key')+''?'selected="selected"':''}>
						${costType}
					</option>
				</g:each>
			</select>
			<div class="error-list"><g:renderErrors bean="${target}" field="costType" /></div>
		</div>
		
		<div class="row ${hasErrors(bean:target,field:'order','errors')}">
			<label for="order">Order</label>
			<input type="text" id="order" name="order" value="${fieldValue(bean:target,field:'order')}"></input>
			<div class="error-list"><g:renderErrors bean="${target}" field="order" /></div>
		</div>
		
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
					$('.ramp-up-list').append('<option value="'+rampUp.id+'">'+rampUp.name+'</option>');
// 					$.sexyCombo.changeOptions('.ramp-up-list');
				}
			}
		});
		
		$('#add-cost-target').flow({
			addLinks: '#add-expression-link',
			onSuccess: function(data) {
				if (data.result == 'success') {
					var expression = data.newEntity
					$('.expression-list').append('<option value="'+expression.id+'">'+expression.name+'</option>');
					$.sexyCombo.changeOptions('.expression-list');
				}
			}
		});
	});
</script>
