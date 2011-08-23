<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<title><g:message code="listDateElement.view.label"
		default="District Health System Portal" /></title>
</head>
<body>
	<div id="data-element-admin">
			<div class="top-container box">
				<ul class="top-menu-list">
					<li><a href="${createLink(controller:'dataElement', action:'list')}" class="">Data Elements</a></li>
					<li>| <a href="${createLink(controller:'enum', action:'list')}" class="">Enums</a></li>
				</ul>
				<div class="clear"></div>
			</div>
		<div id="bottom-container" class="box">
			<div id="list-container">
				<!-- Template goes here -->
				<g:render template="/data/${template}" model="[dataElements: dataElements, dataElementCount: dataElementCount,enums: enums, enumCount: enumCount,options: options, optionCount: optionCount]" />
				<!-- End of template -->
				<div class="clear"></div>
			</div>
			<div class="hidden flow-container"></div>
			<div class="clear"></div>
		</div>
		<div class="clear"></div>
	</div>
	<script type="text/javascript">
		$(document).ready(function() {
			$('#list-container').flow({
				onSuccess : function(data) {
					if (data.result == 'success') {
						location.reload();
					}
				}
			});
		});
	</script>
</body>
</html>