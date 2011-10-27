function hideQuestionHelp(el) {
	el.parents('.question-help').slideUp(500, function () {
		el.parents('.question-help-container').prev().css({'display': 'inline-block'});
	})
}

function showQuestionHelp(el) {
	el.next().find('.question-help').slideDown(500, function () {
		el.hide();
	})
}

$(document).delegate('.hide-question-help', 'click', function(){
	hideQuestionHelp($(this));
	return false;
});

$(document).delegate('.show-question-help', 'click', function(){
	showQuestionHelp($(this));
    return false;
});
