<ul class='horizontal' id='tab-nav'>
	
	<li><a ${controllerName == 'dashboard' ? 'class="selected"':''}
		href="${createLinkByTab(controller:'dashboard', action:actionName, params:linkParams)}">
		Performance</a>
	</li>
	<li><a ${controllerName == 'dsr' ? 'class="selected"':''}
		href="${createLinkByTab(controller:'dsr', action:actionName, params:linkParams)}">
		District Summary</a>
	</li>
	<li><a ${controllerName == 'fct' ? 'class="selected"':''}
		href="${createLinkByTab(controller:'fct', action:actionName, params:linkParams)}">
		Facility Count</a>
	</li>	 		
</ul>