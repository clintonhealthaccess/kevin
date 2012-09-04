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

		<g:render template="/entity/data/explanation/explain${dataElement.class.simpleName}" model="${model}"/>
		
	</body>
</html>

