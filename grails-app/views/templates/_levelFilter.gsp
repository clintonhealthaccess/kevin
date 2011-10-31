<div class="filter">
	<span class="bold"><g:message code="filter.level.label" />&nbsp;&nbsp;&nbsp;</span>
	<g:each in="${levels}" var="level">
		<% linkParams << [level:level.id] %>
		<g:if test="${currentLevel != null && currentLevel == level}">
			<a class="selected" style="color:red" data-type="level" data-level="${currentLevel.id}" href="#">
			<span>${level.name}</span></a>
		</g:if>
		<g:else>
			<a data-type="level" data-level="${currentLevel?.id}"
			href="${createLinkByFilter(controller:controller, action:action, params:linkParams)}">
			<span>${level.name}</span></a>
		</g:else>
	</g:each>
</div>