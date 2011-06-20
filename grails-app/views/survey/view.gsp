<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<title><g:message code="surveyPage.view.label"
		default="District Health System Portal" /></title>
</head>
<body>
	<div id="survey">
		<div id="top-container">
			<div id="survey-iteration-box" class="box">
				<h5>Iteration:</h5>
				<g:dateFormat format="yyyy" date="${surveyPage.period.startDate}" />
				</a>
			</div>
			<div id="survey-objective-box" class="box">
				<g:if test="${surveyPage.section != null}">
					<h5>Strategic Objective:</h5>
					<g:i18n field="${surveyPage.section.section.names}" />
             </g:if>
			</div>
			<div class="clear"></div>
		</div>
		<div id="bottom-container">
			<div id="survey-left-objective-container" class="box">
				<g:if test="${sections != null}">
					<div id="survey-section" class="section">
						<ul id="survey-section-list" class="sections expandfirst collapsible">
							<g:each in="${sections}" var="section">
								<li><a class="section-link" href="#"> <g:i18n
											field="${section.names}" /></a>
									<ul id="survey-subsection-list" class="survey-subsection">
										<g:each in="${section.subSections}" var="subsection">
											<li>
											<a id="subsection-${subsection.id}" class="flow-add" 
											href="${createLink(controller:'Survey', action:'view',params:[section: subsection.section.id, subsection:subsection.id])}">
											<g:i18n field="${subsection.names}" /></a></li>
										</g:each>
									</ul>
								</li>
							</g:each>
						</ul>

					</div>
				</g:if>
				<g:else>
				Couldn't load Sections.
				</g:else>
			</div>
			<div id="survey-right-question-container" class="box">
			ii
				<div id="subsection-questions">
				kk
				</div>
			</div>
			<!-- ADMIN SECTION -->
			<g:if test="${true || user.admin}">
				<div class="hidden flow-container"></div>
			</g:if>
			<!-- ADMIN SECTION END -->
			<script type="text/javascript">
				$(document)
						.ready(
								function() {
									$('#survey-right-question-container').flow({
										onSuccess : function(data) {
											if (data.result == 'success') {
												location.reload();
											}
										}
									});

									//Menu Accordion functions
									initSectionMenus();
								});			
				
				 function initSectionMenus() {
					$('ul.sections ul').hide();
					$.each($('ul.sections'), function(){
						$('#' + this.id + '.expandfirst ul:first').show();
					});
					$('ul.sections li a').click(
						function() {
							var checkElement = $(this).next();
							var parent = this.parentNode.parentNode.id;
							if((checkElement.is('ul')) && (checkElement.is(':visible'))) {
								if($('#' + parent).hasClass('collapsible')) {
									$('#' + parent + ' ul:visible').slideUp('slow');
								}
								return false;
							}
							if((checkElement.is('ul')) && (!checkElement.is(':visible'))) {
								$('#' + parent + ' ul:visible').slideUp('slow');
								checkElement.slideDown('slow');
								return false;
							}
						}
					);
				} 
				
				
			</script>
			<div class="clear"></div>
		</div>
		<div class="clear"></div>
	</div>
</body>
</html>