/**
 * drop-down menus
 * TODO transform in jQuery Plugin style
 **/
$(document).delegate('.js_dropdown .selected', 'click', function(e) {
	$(".js_dropdown-list").hide();
	$(this).parents(".js_dropdown").find(".js_dropdown-list").toggle();
	e.stopPropagation();
	return false;
});
$(".js_dropdown .js_dropdown-link").bind('click', function(e) {
	reset(e.target);
	$(this).parents('.js_dropdown').find('.js_dropdown-list').toggle();
	$(this).parents('.js_dropdown').find('a').toggleClass('dropdown-selected');
	if ($(this).attr('href') == '#') return false;
});
$(document).bind('click', function(e) {
	if (!$(e.target).hasClass('js_dropdown-ignore')) {
		reset(e.target)
	}	
});

function reset(clicked) {
	$(".js_dropdown a.js_dropdown-link").each(function(){
		if (clicked != this) {
			$(this).parents(".js_dropdown").find("div.js_dropdown-list").hide();
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
