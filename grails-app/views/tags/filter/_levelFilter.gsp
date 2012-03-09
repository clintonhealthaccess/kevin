<div class="filter">	
	<span class="dropdown js_dropdown">
		<a class="level selected" href="#" data-period="${currentLevel?.id}" data-type="level">
			<g:i18n field="${currentLevel?.names}"/>
		</a>
		<div class="hidden dropdown-list js_dropdown-list">
			<ul>
				<g:each in="${levels}" var="level">
					<% def levelLinkParams = new HashMap(linkParams) %>
					<% levelLinkParams << [level:level.id+""] %>
					<% linkParams = levelLinkParams %>
					<li>
						<a href="${createLinkByFilter(controller:controllerName, action:actionName, params:linkParams)}">
							<span><g:i18n field="${level.names}"/></span> 
						</a>
					</li>
				</g:each>
			</ul>
		</div> 
	</span>
</div>