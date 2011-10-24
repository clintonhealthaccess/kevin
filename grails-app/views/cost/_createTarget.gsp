<%@ page import="org.chai.kevin.cost.CostTarget.CostType" %>

<div class="entity-form-container togglable">

	<div class="entity-form-header">
		<h3 class="title">Costing target</h3>
		<g:locales/>
		<div class="clear"></div>
	</div>

	<g:form url="[controller:'costTarget', action:'save', params:[targetURI:targetURI]]" useToken="true">
		<g:i18nInput name="names" bean="${target}" value="${target.names}" label="Name" field="names"/>
		<g:i18nTextarea name="descriptions" bean="${target}" value="${target.descriptions}" label="Description" field="descriptions"/>
		<g:input name="code" label="Code" bean="${target}" field="code"/>

		<div class="row">
			<h5>Expressions</h5>

			<ul id="expressions-block" class="horizontal">
				<g:each status="i" in="${['','End']}" var="suffix">
					<li class="${hasErrors(bean:target, field:'expression'+suffix, 'errors')}">
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
					</li>
				</g:each>
			</ul>
		</div>

		<g:multipleSelect name="groupUuids" label="${message(code:'facility.type.label')}" bean="${target}" field="groupUuidString" 
					from="${groups}" value="${target.groupUuids*.toString()}" optionValue="name" optionKey="uuid"/>

		<div class="row ${hasErrors(bean:target,field:'costRampUp','errors')}">
			<label for="costRampUp.id">Ramp up</label>

			<div class="float-right">
				<a href="${createLinkWithTargetURI(controller:'costRampUp', action:'create')}">new ramp-up</a>
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
			<button type="submit"><g:message code="default.button.save.label" default="Save"/></button>&nbsp;&nbsp;
			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label" default="Cancel"/></a>
		</div>
    </g:form>
	<div class="clear"></div>
</div>
