<r:require module="foldable"/>

<ul>
	<g:each in="${surveyPage.survey.getObjectives(surveyPage.organisation.organisationUnitGroup)}" var="objective">
		<g:set var="enteredObjective" value="${surveyPage.objectives[objective]}"/>
		
		<li id="objective-${objective.id}" class="foldable ${surveyPage.objective?.id == objective.id?'current':''}">
			<a class="foldable-toggle" href="#">(toggle)</a>
		
			<a class="item ${surveyPage.objective?.id == objective.id?'opened':''}" href="${createLink(controller:'editSurvey', action:'objectivePage', params:[organisation: surveyPage.organisation.id, objective:objective.id])}">
				<span><g:i18n field="${objective.names}" /></span>
				<span class="item-status">
					<span class="objective-status-complete objective-status ${enteredObjective.displayedStatus!='complete'?'hidden':''}"></span>
					<span class="objective-status-invalid  objective-status ${enteredObjective.displayedStatus!='invalid'?'hidden':''}"></span>
					<span class="objective-status-incomplete objective-status ${enteredObjective.displayedStatus!='incomplete'?'hidden':''}"></span>
					<span class="objective-status-closed objective-status ${enteredObjective.displayedStatus!='closed'?'hidden':''}"></span>
				</span>
			</a>
			<ul class="survey-section">
				<g:each in="${objective.getSections(surveyPage.organisation.organisationUnitGroup)}" var="section">
					<g:set var="enteredSection" value="${surveyPage.sections[section]}"/>

					<li id="section-${section.id}">
						<a class="item ${surveyPage.section?.id == section.id?'opened':''}" href="${createLink(controller:'editSurvey', action:'sectionPage', params:[organisation: surveyPage.organisation.id, section:section.id])}">
							<span><g:i18n field="${section.names}" /></span>
							<span class="item-status">
								<span class="section-status-complete section-status ${enteredSection.displayedStatus!='complete'?'hidden':''}"></span>
								<span class="section-status-invalid section-status ${enteredSection.displayedStatus!='invalid'?'hidden':''}"></span>
								<span class="section-status-incomplete section-status ${enteredSection.displayedStatus!='incomplete'?'hidden':''}"></span>
							</span>
						</a>
					</li>
				</g:each>
			</ul>
		</li>
	</g:each>
</ul>

<r:script>
	var surveyQueue;

	function initializeSurvey(callback) {
		$('#survey').delegate('#survey-form .input', 'change', function(){
			var element = $(this).parents('.element').first();
			surveyValueChanged(element, $(element).find('.input'), callback);
		});
		$('#survey').delegate('#survey-form .ajax-error .input', 'focus', function(){
			var element = $(this).parents('.element').first();
			surveyValueChanged(element, $(element).find('.input'), callback);
		});
		$('#survey').delegate('#survey-form a.outlier-validation', 'click', function(){
			$(this).next().val($(this).data('rule'));						
			var element = $(this).parents('.element').first();
			surveyValueChanged(element, $(element).find('.input'), callback);						
			return false;
		});
		$('#survey').delegate('#survey-form .element-list-add', 'click', function(){
			if (!$(this).hasClass('ajax-disabled')) {
				listAddClick(this, callback);
			}
			return false;
		});
		$('#survey').delegate('#survey-form .element-list-remove', 'click', function(){
			if (!$(this).hasClass('ajax-disabled')) {
				var remove = confirm("${message(code: 'default.link.delete.confirm.message', default: 'Are you sure?')}");
				if (remove) listRemoveClick(this);
			}
			return false;
		});
		$('#survey').delegate('#survey-form .element-list-minimize', 'click', function(){
			minimizeRows($(this).parents('.element-list-row'));
			return false;
		});
		$('#survey').delegate('#survey-form .element-list-maximize', 'click', function(){
			maximizeRow($(this).parents('.element-list-row').first());
			return false;
		});
		
		// stop all calls to external links while values are saving
		$(document).delegate('a', 'click', function(){
			if (ajaxCallsInProgress() || $('.element.ajax-error').length > 0) {
				return confirm("${message(code: 'survey.exit.saving.confirm.text', default:"Are you sure you want to exit this page? Some values you entered may be lost.")}");
			}
		});
		
		surveyQueue = $.manageAjax.create('surveyQueue', {
			url : "${createLink(controller:'editSurvey', action:'saveValue', params: [organisation: surveyPage.organisation.id, section: surveyPage.section?.id, objective: surveyPage.objective?.id])}",
			type : 'POST',
			dataType: 'json',
			// ajax queue options
			queue: true,
			cacheResponse: false
		});
		
		$('button[type=submit]').bind('click', function(){
			if (ajaxCallsInProgress()) {
				alert("${message(code:'survey.exit.saving.alert.text',default:'Some values are being saved, please wait before submitting.')}");
				return false;
			}
			else if ($('.question-container.incomplete').length > 0) {
				return confirm("${message(code:'survey.exit.incomplete.confirm.text',default:'Some questions are incomplete, are you sure you want to go to the next section?')}");
			}
		});
		
		$('button[type=cancel]').bind('click', function(){
			$.manageAjax.clear('surveyQueue', true);
			$('.ajax-in-process').find('.input').removeAttr('disabled');
			$('.ajax-in-process').addClass('ajax-error');
			$('.ajax-in-process').removeClass('.ajax-in-process')
			return false;
		});

		minimizeRows($('#survey').find('.element-list-row'));		
		enableAfterLoading();
	}

	function enableAfterLoading() {
		$('.loading-disabled').each(function(index, disabled) {
			if (!$(disabled).parents('.element').first().hasClass('skipped')) {
				$(disabled).removeClass('loading-disabled').removeAttr('disabled');
			}
		})
	}

	function surveyValueChanged(element, inputs, callback) {
		var elementId = $(element).data('element');
		var suffix = $(element).data('suffix');
		
		var data = '';
		data += 'element='+elementId;
		data += '&suffix='+suffix;
		
		// we send all the modified inputs
		$.each(inputs, function(i, input) {
			data += '&'+$(input).serialize();
		})
		// we send the list indexes
		$(element).parents('.element-list').find('.list-input-indexes').each(function(i, input) {
			data += '&'+$(input).serialize();
		});
		
		$(element).removeClass('ajax-error');
		$(element).addClass('ajax-in-process');
		$(element).find('.input').attr('disabled', 'disabled');
		
		// we add the request to the queue
		$.manageAjax.add('surveyQueue', {
			data : data,
			success : function(data, textStatus) {
				$(element).removeClass('ajax-in-process');
				$(element).find('.input').removeAttr('disabled');
				
				toggleControls($.queue(document, 'surveyQueue').length > 0);
				
				if (data.status == 'success') {
					// we go through all the sections
					$.each(data.sections, function(index, section) {
						$('#section-'+section.id).find('.section-status').addClass('hidden');
						$('#section-'+section.id).find('.section-status-'+section.status).removeClass('hidden');
					});
					
					// we go through the objectives
					$.each(data.objectives, function(index, objective) {
						$('#objective-'+objective.id).find('.objective-status').addClass('hidden');
						$('#objective-'+objective.id).find('.objective-status-'+objective.status).removeClass('hidden');
					});
					
					callback(data, element);
				}
				else {
					alert("${message(code:'survey.saving.objective.closed.text',default:'Please reload the page, the objective has been closed.')}");
				}
			},
			error: function() {
				$(element).removeClass('ajax-in-process');
				$(element).find('.input').removeAttr('disabled');
				
				$(element).addClass('ajax-error');
				toggleControls($.queue(document, 'surveyQueue').length > 0);
			}
		});
		
		toggleControls(ajaxCallsInProgress());
	}
	
	function ajaxCallsInProgress() {
		return $.queue(document, 'surveyQueue').length > 0 || surveyQueue.inProgress > 0;
	}
	
	function toggleControls(hide) {
		if (hide) {
			$('.element-list-add, .element-list-remove, button[type=submit]').addClass('ajax-disabled').attr('disabled', 'disabled');
			$('button[type=cancel]').show();
		}
		else {
			$('.element-list-add, .element-list-remove, button[type=submit]').removeClass('ajax-disabled').removeAttr('disabled');
			$('button[type=cancel]').hide();
		}
	}
	
	function getId(array, id) {
		var result = null;
		$.each(array, function(index, value){
			if (value.id == id) result = value;
		});
		return result;
	}
	
	function escape(myid) { 
		return myid.replace(/(:|\.|\[|\])/g,'\\$1');
	}
	
	function minimizeRows(rows, keepOpen) {
		$.each(rows, function(i, element) {
			if (!$(element).hasClass('minimized') && $(element).data('index') != $(keepOpen).data('index')) {
				$(element).find('.input').each(function(i, input) {
					var elementInRow = $(input).parents('.element').first();
					if (!$(elementInRow).hasClass('skipped')) {
						var value = $(input).attr('value');
						if ($(input).prop('nodeName') == 'SELECT' && $(input).attr('value')) {
							value = $(input).find("option[selected]").html();
						}
						$(input).after('<span class="minimized-input" onclick="maximizeRow($(this).parents(\'.element-list-row\')); return false;">'+value+'</span>');
					}
				});
				
				$(element).find('.input').hide();
				$(element).find('label, h5, h6').hide();
				$(element).find('.adv-form-section').hide();
				$(element).find('.adv-form-title').hide();
				$(element).find('.element-map').addClass('adv-form-mini');
				$(element).find('.element-list-minimize').hide();
				$(element).find('.element-list-maximize').show();
				$(element).addClass('minimized');
			}
		});
	}
	
	function maximizeRow(row) {
		$(row).find('.minimized-input').remove();
		$(row).find('.input').show();
		$(row).find('label, h5, h6').show();
		$(row).find('.adv-form-section').show();
		$(row).find('.adv-form-title').show();
		$(row).find('.element-map').removeClass('adv-form-mini');
		$(row).find('.element-list-minimize').show();
		$(row).find('.element-list-maximize').hide();
		$(row).removeClass('minimized');
		
		minimizeRows($(row).parents('.element-list').first().find('.element-list-row'), row);
	}
	
	function listRemoveClick(toRemove) {
		var element = $(toRemove).parents('.element').first();
		var index = $(toRemove).parents('.element-list-row').first().data('index');
		
		$(toRemove).parents('.element-list-row').find('.list-input').remove();

		surveyValueChanged(element, $(element).find('.list-input'), function(data, element) {
			$(toRemove).parents('.element-list-row').first().remove();	
		});
	}
	
	function listAddClick(list, callback) {
		var suffix = $(list).parents('.element').first().data('suffix');

		var clone = $(list).prev().clone(true);
		var index = $(list).prev().prev().data('index');
		if (index == null) index = "0";
		else index = parseInt(index)+1;
		var copyHtml = clone.html().replace(RegExp(suffix+'\\[_\\]', 'g'), suffix+'['+index+']')
	
		$(list).prev().before(copyHtml);
		$(list).prev().prev().data('index', index);
		
		surveyValueChanged($(list).parents('.element').first(), $(list).parents('.element').first().find('.list-input'), function(data, element) {
			$(list).prev().prev().show();

			maximizeRow($(list).prev().prev());
			callback(data, element);
		});
	}
</r:script>
