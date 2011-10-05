function hideQuestionHelp(el) {
  el.parents('.question-help').slideUp(500, function () {
    el.parents('.question-help-container').prev().css({'display': 'inline'});
  })
}

function showQuestionHelp(el) {
  el.next().find('.question-help').slideDown(500, function () {
    el.hide();
  })
}

$(document).ready(function(){
  $('.hide_question_help').click(function () {
    hideQuestionHelp($(this));
  });

  $('.show_question_help').click(function () {
    showQuestionHelp($(this));
  });
});
