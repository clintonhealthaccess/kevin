<div class="filter">	
	<g:each in="${levels}" var="level">
		<% def levelLinkParams = new HashMap(linkParams) %>
		<% levelLinkParams << [level:level.id+""] %>
		<g:if test="${currentLevel != null && currentLevel == level}">
			<a class="selected" style="color:red" data-type="level" data-level="${currentLevel.id}" href="#">
				<span><g:i18n field="${level.names}"/></span>
			</a>
		</g:if>
		<g:else>
			<a data-type="level" data-level="${currentLevel?.id}" href="${createLinkByFilter(controller:controller, action:action, params:levelLinkParams)}">
				<span><g:i18n field="${level.names}"/></span>
			</a>
		</g:else>
	</g:each>
</div>