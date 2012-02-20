<ul class="horizontal" id="tab-nav">
	<li><a class="${selected=='undertakings'?'selected':''}" href="${createLink(controller:'planning', action:'overview', params:[planning: planning.id, location: location.id])}">Undertakings</a></li>
	<li><a class="${selected=='budget'?'selected':''}" href="${createLink(controller:'planning', action:'budget', params:[planning: planning.id, location: location.id])}">Projected Budget</a></li>
	<li class="settings"><a href="#">Settings</a></li>
</ul>
