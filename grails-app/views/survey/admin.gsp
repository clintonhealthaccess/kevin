<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<title><g:message code="surveyAdminPage.view.label"
		default="District Health System Portal" />
</title>
</head>
<body>
	<div id="survey-admin">
		<div id="top-container">
		User who is logged in:
			<div class="clear"></div>
		</div>
		<div id="bottom-container">
			<div id="survey-admin-left-container" class="box">
				<div class="survey-admin-menu">
					Menu List:
				</div>
			</div>
			<div id="survey-admin-right-container" class="box">
				<div class="clear"></div>
			</div>
			<!-- ADMIN SECTION -->
			<g:if test="${true || user.admin}">
				<div class="hidden flow-container"></div>
			</g:if>
			<!-- ADMIN SECTION END -->
			<script type="text/javascript">
				$(document).ready(function() {
					$('#survey-admin-right-container').flow({
						onSuccess : function(data) {
							if (data.result == 'success') {
								location.reload();
							}
						}
					});
				});
			</script>
			<div class="clear"></div>
		</div>
		<div class="clear"></div>
	</div>
</body>
</html>