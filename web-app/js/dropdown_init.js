/**
 * drop-down menus
 * TODO transform in jQuery Plugin style
 **/
$(document).delegate('.js_dropdown .selected', 'click', function(e) {
  $("div.js_dropdown-list").hide();
	$(this).parent(".js_dropdown").find("div.js_dropdown-list").toggle();
	e.stopPropagation();
	return false;
});
$(".js_dropdown-list a.js_dropdown-link").bind('click', function() {
	$(this).parents('.js_dropdown-list').hide();
//	return false;
});
$(document).bind('click', function(e) {
	var clicked = e.target;
	$(".js_dropdown a.selected").each(function(){
		if (clicked != this) {
			$(this).parent(".js_dropdown").find("div.js_dropdown-list").hide();
		}
	});
//	return false;
});
