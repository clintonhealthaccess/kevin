$(function() {	
//	$(window).scroll(function() {
//		animateCharts(false, null);
//	});

	$('.tooltip').tipsy({
		gravity : 's',
		fade : true,
		live : true,
		html : true
	});

	// NOTE: this event is separate from input event
	// because label click triggers input click event
	$('.check_filter label').click(function(e) {
		e.stopPropagation();
	});

	$('.check_filter input').click(function(e) {
		e.stopPropagation();

		// TODO: add here Ajax filter request
		//
		// alert($(this).prop('checked'))
		// alert($(this).prop('id'))
	})
});

var animateCharts = function(compare, table) {
	
//	$('.horizontal-bar').hide();
//	$('.horizontal-bar-avg').hide();
//	var bars = null;
//	
//	$('.horizontal-graph-avg').hide();			
//	var tickmark = null;
//	var tooltip = null;
//	
//	if(compare){
//		if(table == 'program'){
//			bars = $('.horizontal-bar, .horizontal-bar-avg');
//		}
//		if(table == 'location'){
//			var tickmark = $('.horizontal-graph-avg').children('.horizontal-graph-marker');
//			var tooltip = $('.horizontal-graph-avg').children('.tooltip');	
//		}
//	}
//	else{
//		bars = $('.horizontal-bar').not('.horizontal-bar-avg');
//	}

	var bars = null;
	bars = $('.horizontal-bar').not('.horizontal-bar-avg');
	for ( var i = 0; i < bars.length; i++) {
		var bar = $(bars[i]);
		if (isScrolledIntoView(bar.parents('table.horizontal-graph'))) {
			var bar_percentage = bar.data('percentage');
			if(bar_percentage == 'N/A')
				bar_percentage = 0;
			else if(bar_percentage > 100)
				bar_percentage = 100;			
			
			bar.show();
			if(bar_percentage != 'N/A'){				
				bar.animate({
					width : bar_percentage + "%"
				}, 1500);	
			}			
		}
	}
	
//	if(compare){
//		if(table == 'location'){
//			if (isScrolledIntoView(tooltip.previous('table.horizontal-graph'))) {
//				var tick_percentage = tooltip.data('percentage');
//				if(tick_percentage == 'N/A')
//					tick_percentage = 0;
//				else if(tick_percentage > 100)
//					tick_percentage = 100;			
//				
//				tickmark.show();
//				tooltip.show();			
//				if(tick_percentage != 'N/A'){					
//					tooltip.animate({
//						left : tick_percentage + "%"
//					}, 1500);
//					tickmark.animate({
//						width : tick_percentage + "%"
//					}, 1500);
//				}
//			}
//		}
//	}	
}

var isScrolledIntoView = function(elem) {
	var docViewTop = $(window).scrollTop();
	var docViewBottom = docViewTop + $(window).height();

	var elemTop = $(elem).offset().top;
	var elemBottom = elemTop + $(elem).height();

	return ((elemBottom >= docViewTop) && (elemTop <= docViewBottom)
			&& (elemBottom <= docViewBottom) && (elemTop >= docViewTop));
}