<div class="left">
	<span class="dropdown js_dropdown">
		<a class="time js_dropdown-link nice-button with-highlight" href="#">
			<g:if test="${currentPeriod}">
				<g:dateFormat format="yyyy" date="${currentPeriod.startDate}"/>
			</g:if>
			<g:else>
				<g:message code="filter.period.noselection.label"/>
			</g:else>
		</a>
		<div class="dropdown-list js_dropdown-list push-top-10">
			<g:if test="${!periods.empty}">
				<ul>
					<g:each in="${periods}" var="period">
						<% def periodLinkParams = new HashMap(linkParams) %>
						<% periodLinkParams << [period:period.id] %>
						<% linkParams = periodLinkParams %>
						<li>
							<a href="${createLink(controller:controllerName, action:actionName, params:linkParams)}">
								<span><g:dateFormat format="yyyy" date="${period.startDate}" /></span> 
							</a>
						</li>
					</g:each>
				</ul>
			</g:if>
			<g:else>
				<div><g:message code="filter.period.no.periods"/></div>
			</g:else>
		</div> 
	</span>
</div>