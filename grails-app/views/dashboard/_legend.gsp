<!-- dashboard map legend -->
<ul class="horizontal map_legend">
	<li>
		<span class="indicator-quartile-na"></span>
			<g:message code="report.value.na"/>
			<span class="indicator-quartile-dark-na"></span>
	</li>
	<g:each in="${(0..<5)}" var="i">
		<g:set var="lowerColorRange" value="${i*25}"/>
		<g:set var="upperColorRange" value="${(i+1)*25}"/>
		<li>
			<span class="indicator-quartile-${i}"></span>
			<g:if test="${i == 4}">${lowerColorRange}%+</g:if>
			<g:else>${lowerColorRange+'-'+upperColorRange+'%'}</g:else>
			<span class="indicator-quartile-dark-${i}"></span>
		</li>
	</g:each>
</ul>
<!-- dashboard map colors -->
<r:script>
	var mapPolygonColors = {}
	var mapPolygonColorsDark = {}

	var rgbColorNa = $('.map_legend .indicator-quartile-na').css('background-color');
	var rgbColorNaDark = $('.map_legend .indicator-quartile-dark-na').css('background-color');

	mapPolygonColors['indicator-quartile-na'] = rgb2hex(rgbColorNa)
	mapPolygonColorsDark['indicator-quartile-na'] = rgb2hex(rgbColorNaDark)

	$('.map_legend span').each(function(index, value) {
		// polygon fill colors
		var rgbColor = $('.map_legend .indicator-quartile-'+index).css('background-color');
		if(rgbColor != null){
			var hexColor = rgb2hex(rgbColor);
			mapPolygonColors['indicator-quartile-'+index] = hexColor
		}
		// polygon colors
		var rgbColorDark = $('.map_legend .indicator-quartile-dark-'+index).css('background-color');
		if(rgbColorDark != null){
			var hexColorDark = rgb2hex(rgbColorDark);
			mapPolygonColorsDark['indicator-quartile-'+index] = hexColorDark
		}
	});

	function rgb2hex(rgb){
		rgb = rgb.match(/^rgb\((\d+),\s*(\d+),\s*(\d+)\)$/);
		return "#" + 
			("0" + parseInt(rgb[1],10).toString(16)).slice(-2) +
			("0" + parseInt(rgb[2],10).toString(16)).slice(-2) +
			("0" + parseInt(rgb[3],10).toString(16)).slice(-2);
	}
</r:script>