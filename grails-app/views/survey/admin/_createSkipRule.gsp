<div id="add-skip-rule" class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">
			<g:message code="default.new.label" args="[message(code:'survey.skiprule.label',default:'Skip Rule')]"/>
		</h3>
		<g:locales />
		<div class="clear"></div>
	</div>
	<div class="forms-container">
		<div class="data-field-column">
		<g:form url="[controller:'surveySkipRule', action:'save', params:[targetURI:targetURI]]" useToken="true">
			<input type="hidden" name="survey.id" value="${skip.survey.id}" />
			<div class="row">
				<label><g:message code="survey.label" default="Survey"/>:</label>
			 	<input type="text" name="survey.id" value="${i18n(field: skip.survey.names)}" class="idle-field" disabled />
		 	</div>
		 	<g:i18nTextarea name="descriptions" bean="${skip}" value="${skip?.descriptions}" label="Descriptions" field="descriptions" />
		 
			<div class="row ${hasErrors(bean:skip, field:'skippedSurveyElements', 'errors')}">
				<label><g:message code="survey.skiprule.skippedsurveyelement.label" default="Skipped Survey Elements"/>: </label>
				
				<!-- START SKIPPED SURVEY ELEMENTS -->
				<g:each in="${skip.skippedSurveyElements}" var="entry">
					<div class="white-box">
						<g:set var="surveyElement" value="${entry.key}"/>
						<g:set var="prefixes" value="${entry.value}"/>
						
						<label for="skipped.element"><g:message code="survey.surveyelement.label" default="Survey Element"/>:</label> 
						<select name="skipped.element" class="ajax-search-field skipped-survey-elements-list">
							<option value="${surveyElement.id}" selected>
								<g:i18n field="${surveyElement.dataElement.names}" />[${surveyElement.id}]
							</option>
						</select>
						<label for="skipped.prefix"><g:message code="survey.skiprule.skippedsurveyelement.prefixes.label" default="Prefixes (comma-separated)"/>:</label>
						<input type="text" value="${prefixes}" name="skipped.prefix"/> 
						<a href="#" onclick="$(this).parent().remove();return false;"><g:message code="default.link.delete.label" default="Delete"/></a>
					</div>
				</g:each>
				<div class="white-box hidden">
					<label for=""><g:message code="survey.surveyelement.label" default="Survey Element"/>:</label> 
					<select name="skipped.element" class="ajax-search-field skipped-survey-elements-list">
						<option value="" selected></option>
					</select>
					<label for="skipped.prefix"><g:message code="survey.skiprule.skippedsurveyelement.prefixes.label" default="Prefixes (comma-separated)"/>:</label>
					<input type="text" value="${prefixes}" name="skipped.prefix"/> 
					<a href="#" onclick="$(this).parent().remove();return false;"><g:message code="default.link.delete.label" default="Delete"/></a>
				</div>
				<a href="#" onclick="$(this).before($(this).prev().clone()); $(this).prev().prev().show(); return false;">
					<g:message code="survey.skiprule.skippedsurveyelement.add.label" default="Add"/>
				</a>
				<!-- END SKIPPED SURVEY ELEMENTS -->
				
				<div class="error-list"><g:renderErrors bean="${skip}" field="skippedSurveyElements" /></div>
			</div>

			<div class="row ${hasErrors(bean:skip, field:'skippedSurveyQuestions', 'errors')}">
				<label><g:message code="survey.skiprule.skippedquestions.label" default="Questions to Skip"/>: </label>
				<select id="questions-list" name="skippedSurveyQuestions" multiple="true" class="ajax-search-field">
					<g:if test="${!skip.skippedSurveyQuestions.isEmpty()}">
						<g:each in="${skip.skippedSurveyQuestions}" var="question">
							<option value="${question.id}" selected>
								<g:stripHtml field="${question.names}" chars="35"/> - <g:i18n field="${question.section.names}"/>
							</option>
						</g:each>
					</g:if>
				</select>
				<div class="error-list"><g:renderErrors bean="${skip}" field="skippedSurveyQuestions" /></div>
			</div>
			
		 	<g:textarea name="expression" label="Expression" bean="${skip}" field="expression" rows="5"/>
		 
			<g:if test="${skip.id != null}">
				<input type="hidden" name="id" value="${skip.id}" />
			</g:if>
			<div class="row">
				<button type="submit" class="rich-textarea-form"><g:message code="default.button.save.label" default="Save"/></button>
				<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label" default="Cancel"/></a>
			</div>
		</g:form>
		</div>
		<div class="data-search-column">
			<g:form name="search-data-form" class="search-form" url="[controller:'surveyElement', action:'getHtmlData']">
				<div class="row">
					<label for="searchText"><g:message code="entity.search.label" default="Search"/>: </label>
			    	<input name="searchText" class="idle-field"/>
			    	<button type="submit"><g:message code="default.button.search.label" default="Search"/></button>
					<div class="clear"></div>
				</div>
			</g:form>
		    <ul class="filtered idle-field" id="data" ></ul>
		</div>
		<div class="clear"></div>
	</div>
	<div class="clear"></div>
</div>
<script type="text/javascript">
	$(document).ready(function() {		
		$(".skipped-survey-elements-list").ajaxChosen({
			type : 'GET',
			dataType: 'json',
			url : "${createLink(controller:'surveyElement', action:'getAjaxData', params:[surveyId: skip.survey.id])}"
		}, function (data) {
			var terms = {};
			$.each(data.elements, function (i, val) {
				terms[val.id] = val.surveyElement;
			});
			return terms;
		});
		
		$("#questions-list").ajaxChosen({
			type : 'GET',
			dataType: 'json',
			url : "${createLink(controller:'question', action:'getAjaxData', params:[surveyId: skip.survey.id])}"
		}, function (data) {
			var terms = {};
			$.each(data.questions, function (i, val) {
				terms[val.id] = val.question;
			});
			return terms;
		});
		
		getDataElement(function(event){
			if ($('.in-edition').size() == 1) {
				var edition = $('.in-edition')[0]
				$(edition).replaceSelection('$'+$(this).data('code'));
			}
		});
		$('#add-skip-rule textarea')
		.bind('click keypress focus',
			function(){
				$(this).addClass('in-edition');
			}
		)
		.bind('blur',
			function(){
				$(this).removeClass('in-edition');
			}
		);
			 
	});					
</script>