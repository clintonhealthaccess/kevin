<div class="left">
	<span class="dropdown js_dropdown">
		<a class="survey js_dropdown-link nice-button with-highlight" href="#" >
			<g:if test="${currentPlanning != null}">
					<g:i18n field="${currentPlanning.names}"/>
			</g:if>
			<g:else>
					<g:message code="default.select.label" args="[message(code:'planning.label')]" />
			</g:else> 
		</a>
		<div class="dropdown-list js_dropdown-list push-top-10">
			<ul>
				<g:each in="${plannings}" var="planning">
					<li>
						<% def planningLinkParams = new HashMap(params) %>
						<% planningLinkParams['planning'] = planning.id+"" %>
						<a href="${createLink(controller:controllerName, action:actionName, params:planningLinkParams)}">
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