<div class="entity-form-container">
	
	<div class="entity-form-header">
		<h3 class="title">Survey User</h3>
		<div class="clear"></div>
	</div>
	<div class="data-field-column">
		<g:form url="[controller:'surveyUser', action:'save', params: [targetURI: targetURI]]" useToken="true">
			<g:input name="username" label="Username" bean="${user}" field="username"/>
			<g:input name="firstname" label="First name" bean="${user}" field="firstname"/>
			<g:input name="lastname" label="Last name" bean="${user}" field="lastname"/>
			<g:input name="location" label="Location" bean="${user}" field="location"/>
			<g:input name="email" label="Email" bean="${user}" field="email"/>
			<g:input name="permissionString" label="Permission" bean="${user}" field="permissionString"/>
			<g:input name="password" label="Password" type="password" bean="${cmd}" field="password"/>
			<g:input name="repeat" label="Repeat password" type="password" bean="${cmd}"  field="repeat"/>
			
			<g:selectFromList name="entityId" label="Location" bean="${surveyUser}" field="entityId" optionKey="id" multiple="false"
			ajaxLink="${createLink(controller:'dataLocation', action:'getAjaxData')}"
			from="${dataLocations}" value="${surveyUser?.entityId}" values="${dataLocations.collect{i18n(field:it.names)}}" />
						
			<div class="row">
				<label><g:message code="user.confirmed.label" default="Confirmed"/></label>
				<g:checkBox name="confirmed" value="${user.confirmed}" />
			</div>
			
			<div class="row">
				<label><g:message code="user.active.label" default="Active"/></label>
				<g:checkBox name="active" value="${user.active}" />
			</div>
			
			<g:selectFromList name="roles" label="${message(code:'user.roles.label', default: 'Roles')}" bean="${user}" field="roles" 
				from="${roles}" value="${user.roles*.id}" optionValue="name" optionKey="id" multiple="true"/>
			
			<g:if test="${user.id != null}">
				<input type="hidden" name="id" value="${user.id}"/>
			</g:if>
			
			<div class="row">
				<button type="submit"><g:message code="default.button.save.label" default="Save"/></button>
				<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label" default="Cancel"/></a>
			</div>
		</g:form>
	</div>
</div>
