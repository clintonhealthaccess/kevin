<!-- map legend -->
<ul class="horizontal map_legend">
	<g:each in="${indicators}" var="indicator" status="i">
		<li>
			<span class="${i == indicators.size()-1 ? 'indicator-worst': i == 0 ? 'indicator-best': 'indicator-middle'}"></span>
			<g:i18n field="${indicator.names}" />
			<span class="${i == indicators.size()-1 ? 'indicator-worst': i == 0 ? 'indicator-best': 'indicator-middle'}-dark"></span>
		</li>
	</g:each>
</ul>
<!-- map colors -->
<r:script>
	var best = $('.map_legend .indicator-best').css('background-color');
	var middle = $('.map_legend .indicator-middle').css('background-color');
	var worst = $('.map_legend .indicator-worst').css('background-color');
	
	var mapMarkerColors = {
		'indicator-best' : best != null ? rgb2hex(best) : 'green',
		'indicator-middle': middle != null ? rgb2hex(middle) : 'yellow',
		'indicator-worst': worst != null ? rgb2hex(worst) : 'red'
	}

	var bestDark = $('.map_legend .indicator-best-dark').css('background-color');
	var middleDark = $('.map_legend .indicator-middle-dark').css('background-color');
	var worstDark = $('.map_legend .indicator-worst-dark').css('background-color');

	var mapMarkerDarkerColors = {
		'indicator-best' : bestDark != null ? rgb2hex(bestDark) : 'darkGreen',
		'indicator-middle': middleDark != null ? rgb2hex(middleDark) : 'darkYellow',
		'indicator-worst': worstDark != null ? rgb2hex(worstDark) : 'darkRed'
	}

	function rgb2hex(rgb){
		rgb = rgb.match(/^rgb\((\d+),\s*(\d+),\s*(\d+)\)$/);
		return "#" + 
			("0" + parseInt(rgb[1],10).toString(16)).slice(-2) +
			("0" + parseInt(rgb[2],10).toString(16)).slice(-2) +
			("0" + parseInt(rgb[3],10).toString(16)).slice(-2);
	}
</r:script>