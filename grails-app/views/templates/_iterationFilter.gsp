<div class="filter">
	<span class="bold"><g:message code="filter.period.label"/></span>
	<span class="dropdown dropdown-period subnav-dropdown">
		<a class="selected" href="#" data-period="${currentPeriod.id}" data-type="period">
			<g:dateFormat format="yyyy" date="${currentPeriod.startDate}"/>
		</a>
		<div class="hidden dropdown-list">
			<ul>
				<g:each in="${periods}" var="period">
					<% linkParams << [period:period.id] %>
					<li>
						<a href="${createLinkByFilter(controller:controllerName, action:actionName, params:linkParams)}">
							<span><g:dateFormat format="yyyy" date="${period.startDate}" /></span> 
						</a>
					</li>
				</g:each>
			</ul>
		</div> 
	</span>
</div>