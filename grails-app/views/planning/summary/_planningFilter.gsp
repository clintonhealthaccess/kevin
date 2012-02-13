<div class="filter">
	<span class="dropdown js_dropdown">
		<g:if test="${currentPlanning != null}">
			<a class="survey selected" href="#" data-type="planning" data-planning="${currentPlanning.id}">
				<g:i18n field="${currentPlanning.names}"/>
			</a>
		</g:if>
		<g:else>
			<a class="survey selected" href="#" data-type="planning">
				<g:message code="filter.planning.noselection.label" default="Please select a planning"/>
			</a>
		</g:else> 
		<div class="hidden dropdown-list js_dropdown-list">
			<ul>
				<g:each in="${plannings}" var="planning">
					<li>
						<% def planningLinkParams = new HashMap(params) %>
						<% planningLinkParams['planning'] = planning.id+"" %>
						<a href="${createLinkByFilter(controller:controllerName, action:actionName, params:planningLinkParams)}">
							<span>
								<g:i18n field="${planning.names}"/>
							</span> 
						</a>
					</li>
				</g:each>
			</ul>
		</div> 
	</span>
</div>