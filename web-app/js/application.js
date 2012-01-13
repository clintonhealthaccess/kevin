var animateCharts = function () {
  var bars = $('.js_bar_horizontal, .js_bar_vertical');

  for (var i = 0; i < bars.length; i++) {
    var bar = $(bars[i]);

    if (isScrolledIntoView(bar.parents('table'))) {
      var bar_percentage = bar.attr('data-percentage');

      if (bar.hasClass('js_bar_vertical')) {
        bar.animate({height: bar_percentage + "%"}, 1500);
      } else if (bar.hasClass('js_bar_horizontal')) {
        bar.animate({width: bar_percentage + "%"}, 1500);
      }
    }
  }

}

var isScrolledIntoView = function (elem) {
  var docViewTop = $(window).scrollTop();
  var docViewBottom = docViewTop + $(window).height();

  var elemTop = $(elem).offset().top;
  var elemBottom = elemTop + $(elem).height();

  return ((elemBottom >= docViewTop) && (elemTop <= docViewBottom)
    && (elemBottom <= docViewBottom) &&  (elemTop >= docViewTop) );
}

$(function () {
  animateCharts();

  $(window).scroll(function () {
    animateCharts();
  });

  $('.tooltip').tipsy({gravity: 's', fade: true, live: true, html: true});

  $('.tree_sign_plus, .tree_sign_minus').live('click', function (e) {
    var element = $(this);
    element.toggleClass('tree_sign_plus tree_sign_minus');
    element.next('tr.sub_tree').toggle();
  });

  // NOTE: this event is separate from input event
  // because label click triggers input click event
  $('.check_filter label').click(function (e) {
    e.stopPropagation();
  });

  $('.check_filter input').click(function (e) {
    e.stopPropagation();

    // TODO: add here Ajax filter request
    //
    // alert($(this).prop('checked'))
    // alert($(this).prop('id'))
  })
});
