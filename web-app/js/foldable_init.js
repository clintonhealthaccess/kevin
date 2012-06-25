$(document).ready(function(){
	/**
	 * foldables
	 */
	$('.js_foldable .js_foldable-toggle').click(function(event) {
		$(this).parents('.js_foldable').first().children('.js_foldable-container').toggle();
		$(this).parents('.js_foldable').first().next('.js_foldable-container').toggle();
		$(this).parents('.js_foldable').first().children('.js_foldable-toggle').first().toggleClass('toggled');
		return false;
	});
	// we hide everything
	$('.js_foldable .js_foldable-container').hide();
	// we show the current
	var current = $('.js_foldable.current');
	while (current.size() > 0) {
		current.addClass('opened');
		current = current.parents('.js_foldable').first();
	}
	$('.opened').children('.js_foldable-container').show();
	$('.opened').children('.js_foldable-toggle').addClass('toggled');
});