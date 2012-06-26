/**
 * drop-down menus
 * TODO transform in jQuery Plugin style
 **/

$(document).delegate(".js_dropdown a.js_dropdown-link").bind('click', function(e) {
	if ($(e.target).hasClass('dropdown-selected')) {
		reset(null);
	}
	else {
		reset(e.target);
		$(e.target).addClass('dropdown-selected');
		$(e.target).parents('.js_dropdown').find('.js_dropdown-list').show();
		if ($(e.target).attr('href') == '#') return false;
	}
});
$(document).bind('click', function(e) {
	if (!$(e.target).hasClass('js_dropdown-ignore')) {
		//reset(e.target)
	}	
});

function reset(clicked) {
	$(".js_dropdown .js_dropdown-link").each(function(){
		if (clicked != this) {
			$(this).parents(".js_dropdown").find(".js_dropdown-list").hide();
			$(this).parents(".js_dropdown").find(".dropdown-selected").removeClass('dropdown-selected');
		}
	});
//	return false;
}

// Check all / Uncheck all
$('#js_uncheckall').click(function() {
	$('#js_location-type-filter input').prop('checked', false);
	return false;
});
$('#js_checkall').click(function() {
	$('#js_location-type-filter input').prop('checked', true);
	return false;
});
