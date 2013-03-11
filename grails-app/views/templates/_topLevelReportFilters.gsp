<g:periodFilter 
	linkParams="${params}" 
	selected="${currentPeriod}"/>
<g:programFilter 
	linkParams="${params}" 
	selected="${currentProgram}"
	selectedTargetClass="${selectedTargetClass}"
	showProgramData="${showProgramData}"
	exclude="${['dashboardEntity', 'dsrCategory', 'fctTarget', 'indicators']}"/>
<g:locationFilter 
	linkParams="${params}" 
	selected="${currentLocation}"
	selectedTypes="${currentLocationTypes}"
	skipLevels="${locationSkipLevels}"/>
<g:dataLocationTypeFilter 
	linkParams="${params}" 
	selected="${currentLocationTypes}" />