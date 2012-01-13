<div class="filter">
	<span class="bold"><g:message code="filter.period.label"/></span>
	<span class="dropdown subnav-dropdown">
		<a class="time selected" href="#" data-period="${currentPeriod.id}" data-type="period">
			<g:dateFormat format="yyyy" date="${currentPeriod.startDate}"/>
		</a>
		<div class="hidden dropdown-list">
			<ul>
				<g:each in="${periods}" var="period">
					<% def periodLinkParams = new HashMap(linkParams) %>
					<% periodLinkParams << [period:period.id] %>
					<li>
						<a href="${createLinkByFilter(controller:controllerName, action:actionName, params:periodLinkParams)}">
							<span><g:dateFormat format="yyyy" date="${period.startDate}" /></span> 
						</a>
					</li>
				</g:each>
			</ul>
		</div> 
	</span>
</div>