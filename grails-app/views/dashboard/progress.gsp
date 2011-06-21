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
				$("#progressbar").progressBar({
					stepDuration: 2000,
					boxImage: "${resource(dir:'js/jquery/progressbar/images',file:'progressbar.gif')}",
					barImage: {
						0:  "${resource(dir:'js/jquery/progressbar/images',file:'progressbg_red.gif')}",
						30: "${resource(dir:'js/jquery/progressbar/images',file:'progressbg_yellow.gif')}",
						70: "${resource(dir:'js/jquery/progressbar/images',file:'progressbg_green.gif')}"
					},
					callback: function(data) {
						$.ajax({
							type:'GET',
							url: "${createLink(controller:'dashboard', action:'progressInc', params:[period: period, organisation: organisation, objective: objective])}",
							success: function(remoteData) {
								// Process the new data (only called when there was a change)
							    if (remoteData.job == 'found') {
							    	$( "#progressbar" ).progressBar((remoteData.current / remoteData.total) * 100);
							    }
							    else {
							    	$( "#progressbar" ).progressBar(100);
							    	window.location = "${createLink(controller: 'dashboard', action: 'view', params:[period: period, organisation: organisation, objective: objective])}"
							    }
							}
						})
					}
				});
				
			});
		</script>
    </body>
</html>