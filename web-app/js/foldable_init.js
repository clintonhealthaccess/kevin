$(document).ready(function(){
	/**
	 * foldables
	 */
	$('.foldable a.foldable-toggle').click(function(event) {
		$(this).parent('.foldable').children('ul').toggle();
		$(this).toggleClass('toggled');
		return false;
	});
	// we hide everything
	$('.foldable ul').hide();
	// we show the current
	var current = $('.foldable.current');
	while (current.hasClass('foldable') && current.size() > 0) {
		current.addClass('opened');
		current.children('ul').show();
		current.children('a').addClass('toggled');
		current = current.parents('li');
	}
});