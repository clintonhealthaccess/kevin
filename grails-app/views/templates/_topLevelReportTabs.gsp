<ul class='horizontal' id='tab-nav'>
	
	<% def tabLinkParams = new HashMap(linkParams) %>
	<% tabLinkParams.remove("dashboardEntity") %>
	<% tabLinkParams['program'] = currentProgram.id+"" %>
	<% linkParams = tabLinkParams %>
	
	<li><a ${controllerName == 'dashboard' ? 'class="selected"':''}
		href="${createLink(controller:'dashboard', action:actionName, params:linkParams)}">Performance</a></li>
	<li><a ${controllerName == 'dsr' ? 'class="selected"':''}
		href="${createLink(controller:'dsr', action:actionName, params:linkParams)}">District Summary</a></li>
	<li><a ${controllerName == 'fct' ? 'class="selected"':''}
		href="${createLink(controller:'fct', action:actionName, params:linkParams)}">Facility Count</a></li>
	 		
	<!-- <li><a ${controllerName == 'cost' ? 'class="selected"':''}
	 href="${createLink(controller:'cost', action:actionName, params:linkParams)}">Costing</a></li>	
	<li><a ${controllerName == 'maps' ? 'class="selected"':''}
	 href="${createLink(controller:'maps', action:actionName, params:linkParams)}">Map</a></li> -->
</ul>