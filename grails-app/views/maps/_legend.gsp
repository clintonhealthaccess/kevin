<!-- map legend -->
<ul class="horizontal map_legend">
	<g:each in="${indicators}" var="indicator" status="i">
		<li>
			<span class="${i == indicators.size()-1 ? 'indicator-worst': i == 0 ? 'indicator-best': 'indicator-middle'}"></span>
			<g:i18n field="${indicator.names}" />
		</li>
	</g:each>
</ul>
<!-- map colors -->
<r:script>
	var best = $('.map_legend .indicator-best');
	var middle = $('.map_legend .indicator-middle');
	var worst = $('.map_legend .indicator-worst');
	
	var mapMarkerColors = {
		'indicator-best' : $(best).length == 1 ? rgb2hex($(best).css('background-color')) : 'green',		//green
		'indicator-middle': $(middle).length == 1 ? rgb2hex($(middle).css('background-color')) : 'yellow',	//yellow
		'indicator-worst': $(worst).length == 1 ? rgb2hex($(worst).css('background-color')) : 'red'			//red
	}

	function rgb2hex(rgb){
		rgb = rgb.match(/^rgb\((\d+),\s*(\d+),\s*(\d+)\)$/);
		return "#" + 
			("0" + parseInt(rgb[1],10).toString(16)).slice(-2) +
			("0" + parseInt(rgb[2],10).toString(16)).slice(-2) +
			("0" + parseInt(rgb[3],10).toString(16)).slice(-2);
	}
</r:script>