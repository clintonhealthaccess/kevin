<%@ page import="java.text.SimpleDateFormat" %>
<% SimpleDateFormat simpleDateformat=new SimpleDateFormat("yyyy"); %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <title><g:message code="dashboard.progress.label" default="Dashboard Progress" /></title>
    </head>
    <body>
		<div id="progress">
			<div id="corner" class="box">
				<div id="progressbar"></div>
			
				<div>
					<a href="${createLink(controller: 'dashboard', action: 'cancel', params:[period: period, organisation: organisation, objective: objective])}">
						cancel
					</a>
				</div>
			</div>
		</div>
		<script type="text/javascript">
			$(document).ready(function() {
				$('#progressbar').progressbar({value:0})
				
				$.PeriodicalUpdater('${createLink(controller:'dashboard', action:'progressInc', params:[period: period, organisation: organisation, objective: objective])}', {
				    method: 'get',          
				    minTimeout: 5000,
				    maxTimeout: 100000,       
				    multiplier: 2,     
				    type: 'json',
				    maxCalls: 0,
				    autoStop: 0 
				}, function(remoteData, success, xhr, handle) {
				    // Process the new data (only called when there was a change)
				    if (remoteData.job == 'found') {
				    	$( "#progressbar" ).progressbar( "option", "value", (remoteData.current / remoteData.total) * 100 );
				    }
				    else {
				    	$( "#progressbar" ).progressbar( "option", "value", 100 );
				    	window.location = "${createLink(controller: 'dashboard', action: 'view', params:[period: period, organisation: organisation, objective: objective])}"
				    }
				});
				
			});
		</script>
    </body>
</html>