$(document).ready(function(){
	/**
	 * foldables
	 */
	$('.js_foldable .js_foldable-toggle').click(function(event) {
		$(this).parent('.js_foldable').children('.js_foldable-container').toggle();
		$(this).parent('.js_foldable').next('.js_foldable-container').toggle();
		$(this).toggleClass('toggled');
		return false;
	});
	// we hide everything
	$('.js_foldable .js_foldable-container').hide();
	// we show the current
	var current = $('.js_foldable.current');
	while (current.hasClass('js_foldable') && current.size() > 0) {
		current = current.parents('js_foldable');
	}
	$('.opened').children('.js_foldable-container').show();
	$('.opened').children('.js_foldable-toggle').addClass('toggled');
});