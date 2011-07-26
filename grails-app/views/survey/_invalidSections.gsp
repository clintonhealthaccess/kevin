<g:each in="${invalidSectionMap}" var="entry" status="i">
	<g:set value="${entry.key}" var="section"/>
	<g:set value="${entry.value}" var="questions"/>
	
	<g:each in="${questions}" var="question" status="j">
		<div class="invalid-question ${i!=0||j!=0?'hidden':''}">
			<div class="rounded-box-top">
				<h5>
					<g:i18n field="${section.names}" />
				</h5>
			</div>
			<div class="rounded-box-bottom">
				<div class="question errors">
					<!-- separation -->
					<g:render template="/survey/question/${question.getTemplate()}" model="[question: question, surveyElementValues: surveyPage.surveyElements]" />
				</div> 
				<g:if test="${i!=0||j!=0}">
					<a href="#" onclick="$(this).parents('.invalid-question').hide();$(this).parents('.invalid-question').prev().show();">previous</a>
				</g:if>
				<g:if test="${i!=invalidSectionMap.size()-1 || j!=questions.size()-1}">
					<a href="#" onclick="$(this).parents('.invalid-question').hide();$(this).parents('.invalid-question').next().show();">next</a>
				</g:if>
			</div>
		</div>
	</g:each>
</g:each>