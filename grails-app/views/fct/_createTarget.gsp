<div class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">Fct Target</h3>
		<g:locales/>
		<div class="clear"></div>
	</div>
	<g:form url="[controller:'fctTarget', action:'save', params:[targetURI:targetURI]]" useToken="true">
	    <g:if test="${target != null}">
			<input type="hidden" name="id" value="${target.id}"/>
		</g:if>
		<g:i18nInput name="names" bean="${target}" value="${target.names}" label="Name" field="names"/>
		<g:i18nTextarea name="descriptions" bean="${target}" value="${target.descriptions}" label="Description" field="descriptions"/>
		<g:input name="code" label="Code" bean="${target}" field="code"/>
		<g:input name="format" label="Format" bean="${target}" field="format"/>
	    <div class="row ${hasErrors(bean:target, field:'groupUuidString', 'errors')}">
			<label for="groups">Organisation Unit Group:</label>
			<select class="group-list" name="groupUuids" multiple="multiple" size="5" >
				<g:each in="${groups}" var="group">
					<option value="${group.uuid}" ${groupUuids.contains(group.uuid)?'selected="selected"':''}>
			           ${group.name}
		            </option>
				</g:each>
			</select>
			<div class="error-list"><g:renderErrors bean="${target}" field="groupUuidString" /></div>
		</div>
		<div class="row ${hasErrors(bean:target, field:'objective', 'errors')}">
			<label for="objective">Objective:</label>
			<select class="objective-list" name="objective.id">
				<option value="null">-- Select an Objective --</option>
				<g:each in="${objectives}" var="objective">
					<option value="${objective.id}" ${objective.id==target.objective?.id?'selected="selected"':''}>
						<g:i18n field="${objective.names}"/>
					</option>
				</g:each>
			</select>
			<div class="error-list"><g:renderErrors bean="${target}" field="objective" /></div>
		</div>
		<div class="row ${hasErrors(bean:target, field:'expression', 'errors')}">
			<label for="sum.id">Expression:</label>
			<select name="sum.id">
				<option value="null">-- Select an Expression --</option>
				<g:each in="${sums}" var="sum">
					<option value="${sum.id}" ${sum.id==target.sum?.id?'selected="selected"':''}>
						<g:i18n field="${sum.names}"/>
					</option>
				</g:each>
			</select>
			<div class="error-list"><g:renderErrors bean="${target}" field="expression" /></div>
		</div>
		<g:input name="order" label="Order" bean="${target}" field="order"/>
		<div class="row">
			<button type="submit">Save Target</button>
			<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label" default="Cancel"/></a>
		</div>
    </g:form>
	<div class="clear"></div>
</div>
