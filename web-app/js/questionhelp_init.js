function hideQuestionHelp(el) {
  el.parents('.question-help').slideUp(500, function () {
    el.parents('.question-help-container').find('.show_question_help').show();
  })
}

function showQuestionHelp(el) {
  el.parents('.question-help-container').find('.question-help').slideDown(500, function () {
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
