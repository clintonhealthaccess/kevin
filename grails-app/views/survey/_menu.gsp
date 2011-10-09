<ul id="survey-objective-list">
	<g:each in="${surveyPage.survey.getObjectives(surveyPage.organisation.organisationUnitGroup)}" var="objective">
		<g:set var="enteredObjective" value="${surveyPage.objectives[objective]}"/>
		
		<li id="objective-${objective.id}" class="${surveyPage.section?.objective?.id == objective.id?'current':''}">
			<a class="item" href="${createLink(controller:'editSurvey', action:'objectivePage', params:[organisation: surveyPage.organisation.id, objective:objective.id])}">
				<span><g:i18n field="${objective.names}" /></span>
				<span class="item-status">
					<span class="objective-status-complete objective-status ${enteredObjective.displayedStatus!='complete'?'hidden':''}"></span>
					<span class="objective-status-invalid  objective-status ${enteredObjective.displayedStatus!='invalid'?'hidden':''}"></span>
					<span class="objective-status-incomplete objective-status ${enteredObjective.displayedStatus!='incomplete'?'hidden':''}"></span>
					<span class="objective-status-closed objective-status ${enteredObjective.displayedStatus!='closed'?'hidden':''}"></span>
				</span>
			</a>
			<g:if test="${surveyPage.objective.equals(objective)}">
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
			</g:if>
		</li>
	</g:each>
</ul>

<r:script>
	function initializeSurvey(callback) {
		$('#survey').delegate('#survey-form input, #survey-form select, #survey-form textarea', 'change', function(){
			var element = $(this).parents('.element');
			surveyValueChanged(element, callback);
		});
		$('#survey').delegate('#survey-form a.outlier-validation', 'click', function(){
			$(this).next().val($(this).data('rule'));						
			var element = $(this).parents('.element');
			surveyValueChanged(element, callback);						
			return false;
		});
		$('#survey').delegate('#survey-form .element-list-add', 'click', function(){
			listAddClick(this, callback);
			return false;
		});
		$('#survey').delegate('#survey-form .element-list-remove', 'click', function(){
			listRemoveClick(this);
			return false;
		});
		
		$.manageAjax.create('surveyQueue', {
			url : "${createLink(controller:'editSurvey', action:'saveValue', params: [organisation: surveyPage.organisation.id, section: surveyPage.section?.id, objective: surveyPage.objective?.id])}",
			type : 'POST',
			dataType: 'json',
			// ajax queue options
			queue: true,
			cacheResponse: true
		});
		
		$('button[type=submit]').bind('click', function(){
			$.manageAjax.clear('surveyQueue', true);
			// wait until last ajax request finishes
			return true;
		});
		
	}

	function surveyValueChanged(element, callback) {
		var elementId = $(element).data('element');
		var questionId = $(element).parents('.question').data('question'); 
		
		var data = $('#survey-form').serialize();
		data += '&element='+elementId+'&question='+questionId;
		
		$(element).removeClass('ajax-error');
		$(element).addClass('ajax-in-process');
		
		// we add the request to the queue
		$.manageAjax.add('surveyQueue', {
			data : data,
			success : function(data, textStatus) {
				$(element).removeClass('ajax-in-process');

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
					alert('Please reload the page, the objective has been closed.');
				}
			},
			error: function() {
				$(element).removeClass('ajax-in-process');
				$(element).addClass('ajax-error');
			}
		});
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
	
	function listRemoveClick(toRemove) {
		var element = $(toRemove).parents('.element');
		
		$(toRemove).parents('div').first().remove();
		surveyValueChanged(element, function(data, element) {location.reload()});
	}
	
	function listAddClick(element, callback) {
		var suffix = $(element).parents('.element').data('suffix');

		var clone = $(element).prev().clone(true);
		var index = $(element).prev().prev().data('index');
		if (index == null) index = "0";
		else index = parseInt(index)+1;
		var copyHtml = clone.html().replace(RegExp(suffix+'\\[_\\]', 'g'), suffix+'['+index+']')
	
		$(element).prev().before(copyHtml);
		$(element).prev().prev().data('index', index);
		
		surveyValueChanged($(element).parents('.element'), callback);
	}
</r:script>
