<div class="filter">
	<span class="bold"><g:message code="filter.objective.label" default="Strategic Objective"/></span>
	<span class="js_dropdown dropdown">
		<g:if test="${currentObjective != null}">
			<a href="#" class="selected" data-objective="${currentObjective.id}" data-type="objective">
				<g:i18n field="${currentObjective.names}"/>
			</a>
		</g:if>
		<g:else>
			<a href="#" class="selected" data-type="objective">
				<g:message code="filter.objective.noselection.label" default="no objective selected"/>
			</a>
		</g:else>
		<div class="hidden dropdown-list js_dropdown-list">
			<g:if test="${!objectives.empty}">
				<ul>
					<g:each in="${objectives}" var="objective">
						<% def newLinkParams = new HashMap(linkParams) %>
						<% newLinkParams << [objective: objective.id] %>
						<li>
							<span>
								<a href="${createLinkByFilter(controller:controllerName, action:actionName, params:newLinkParams)}">
									<g:i18n field="${objective.names}"/>
								</a>
							</span>
					</g:each>
				</ul>
			</g:if>
			<g:else>
				<span><g:message code="filter.objective.empty.label" default="no objectives found"/></span>
			</g:else>
		</div>
	</span>
</div>