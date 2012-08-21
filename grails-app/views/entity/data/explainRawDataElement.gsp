<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="ajax" />
        <g:set var="entityName" value="${message(code: 'dashboard.explanation.label')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    
    <body>
    	<script type="text/javascript">
    		$(document).delegate('.js_tab-selector', 'click', function() {
    			var type = $(this).data('type');
    			var dataId = $(this).data('id');
    			
    			$('.js_tab-'+dataId).hide();
    			$('#js_tab-'+type+'-'+dataId).show();
    			
    			$('.js_tab-selector').removeClass('selected');
    			$(this).addClass('selected');
    			
    			return false;
    		});
    	</script>

		<div class="main">
			<ul class="tab-navigation horizontal">
				<li>
					<a class="js_tab-selector selected" href="#" data-type="reports" data-id="${rawDataElement.id}">Used in reports</a>
				</li>
				<li>
					<a class="js_tab-selector" href="#" data-type="survey" data-id="${rawDataElement.id}">Used in survey</a>
				</li>
				<li>
					<a class="js_tab-selector" href="#" data-type="planning" data-id="${rawDataElement.id}">Used in planning</a>
				</li>
				<li>
					<a class="js_tab-selector" href="#" data-type="data" data-id="${rawDataElement.id}">Used in data</a>
				</li>
			</ul>
		</div>
		
		<div class="js_tab-${rawDataElement.id}" id="js_tab-reports-${rawDataElement.id}">
		
		</div>
		
		<div class="js_tab-${rawDataElement.id} hidden" id="js_tab-survey-${rawDataElement.id}">
			<g:if test="${surveyElements.size()!=0}">
				<table class="listing">
					<thead>
						<tr>
							<th><g:message code="period.label"/></th>
							<th><g:message code="survey.label"/></th>
							<th><g:message code="survey.question.label"/></th>
							<th><g:message code="rawdataelement.surveyelement.location.applicable.label"/></th>
						</tr>
					</thead>
					<tbody>
						<g:each in="${surveyElements}" status="i" var="surveyElement"> 
							<g:set var="question" value="${surveyElement.key.surveyQuestion}" /> 
							<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
								<td>${question.section.program.survey.period.startDate} &harr; ${question.section.program.survey.period.endDate}</td>
								<td>${i18n(field:question.section.program.survey.names)}</td>
								<td><g:stripHtml field="${i18n(field: question.names)}" chars="100"/></a></td>
								<td>${surveyElement.value}</td>
							</tr>
						</g:each>
					</tbody>
				</table>
			</g:if>
			<g:else>
				<div class="explanation-empty">
					<g:message code="rawdataelement.surveyelement.notassociated"/>
				</div>
			</g:else>
		</div>
		
		<div class="js_tab-${rawDataElement.id} hidden" id="js_tab-planning-${rawDataElement.id}">
		
		</div>
		
		<div class="js_tab-${rawDataElement.id} hidden" id="js_tab-data-${rawDataElement.id}">
			<g:if test="${!referencingData.isEmpty()}">
				<g:render template="/entity/data/referencingDataList" model="[referencingData: referencingData]"/>
			</g:if>
		</div>
		
	</body>
</html>

