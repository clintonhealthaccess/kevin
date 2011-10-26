/**
 * drop-down menus
 * TODO transform in jQuery Plugin style
 **/
$(document).delegate('.dropdown .selected', 'click', function(e) {
	$(this).parent(".dropdown").find("div.dropdown-list").toggle();
	e.stopEventPropagation();
	return false;
});
$(".dropdown-list a.dropdown-link").bind('click', function() {
	$(this).parents('.dropdown-list').hide();
});
$(document).bind('click', function(e) {
	var clicked = e.target;
	$(".dropdown a.selected").each(function(){
		if (clicked != this) {
			$(this).parent(".dropdown").find("div.dropdown-list").hide();	
		}
	});
});