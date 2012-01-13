<ul class='horizontal' id='tab-nav'>
	<li><a ${reportTab == 'dashboard' ? 'class="selected"':''}
	href="${createLink(controller:'dashboard', action:'view', params:linkParams)}">Performance</a></li>
	<li><a ${reportTab == 'dsr' ? 'class="selected"':''}
		href="${createLink(controller:'dsr', action:'view', params:linkParams)}">District Summary</a></li>
	<li><a ${reportTab == 'cost' ? 'class="selected"':''}
	 href="${createLink(controller:'cost', action:'view', params:linkParams)}">Costing</a></li>
	<li><a ${reportTab == 'fct' ? 'class="selected"':''}
	 href="${createLink(controller:'fct', action:'view', params:linkParams)}">Facility Count</a></li>
	<li><a ${reportTab == 'maps' ? 'class="selected"':''}
	 href="${createLink(controller:'maps', action:'view', params:linkParams)}">Map</a></li>
</ul>