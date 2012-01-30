<%@ page import="java.text.SimpleDateFormat" %>
<% SimpleDateFormat simpleDateformat=new SimpleDateFormat("yyyy"); %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <title><g:message code="dashboard.view.label" default="Dashboard" /></title>
        
        <!-- for admin forms -->
        <shiro:hasPermission permission="admin:dashboard">
        	<r:require modules="form"/>
        </shiro:hasPermission>
        
        <r:require module="dashboard"/>
        <script type="text/javascript" src="https://www.google.com/jsapi"></script>
    </head>
    <body>
		<div id="report">
			<div class="subnav">			
				<g:render template="/templates/topLevelReportFilters" model="[linkParams:params]"/>
			</div>
			<div class="main">
				<g:render template="/templates/topLevelReportTabs" model="[tab:'dashboard', linkParams:params]"/>
				<g:render template="/templates/reportTabHelp"/>
				<ul class='clearfix' id='questions'>
  					<li class='question push-20'>
						<g:render template="/templates/reportTableHeader" model="[tab:'dashboard', table:'program']"/>						
						<g:if test="programDashboard != null">
							<g:render template="/templates/dashboard/reportTableCompareFilter" model="[table:'program', dashboard:programDashboard, params:params]"/>
							<div class='horizontal-graph-wrap'>
								<g:render template="/templates/dashboard/reportProgramTable" model="[dashboard:programDashboard, params:params]"/>
			                </div>
		                </g:if>
	                </li>
	                <li class='question push-10'>
		                <g:render template="/templates/reportTableHeader" model="[tab:'dashboard', table:'location']"/>						
		                <g:if test="locationDashboard != null">
		                <g:render template="/templates/dashboard/reportTableCompareFilter" model="[table:'location', dashboard:locationDashboard, params:params]"/>
							<div class='horizontal-graph-wrap'>
								<g:render template="/templates/dashboard/reportLocationTable" model="[dashboard:locationDashboard, params:params]"/>			                  
							</div>
		                </g:if>
		            </li>
		        </ul>    
			</div>    	
	    </div>
	    
		<r:script>
		
			$(document).ready(function() {
				$('.horizontal-bar-avg.tooltip').hide();
				$('.horizontal-graph-average').hide();
				
			 	$('#program-compare').bind('change', function() {
					var locationId = $(this).val();
					if(locationId > 0){
						compareFilter('program', locationId);
					}
					else {
						$('.horizontal-bar-avg.tooltip').hide();
					}
					return;
				});
			 	
				$('#location-compare').bind('change', function() {
					var locationId = $(this).val();
					if(locationId > 0){
						compareFilter('location', locationId);
					}
					else {
						$('.horizontal-graph-average').hide();
					}
					return;
				});
					
			});	
		
			function compareFilter(table, locationId) {
				var data = 'location='+locationId;		
		
				$.ajax({
					type: 'GET',
					data: data,
					url: "${createLink(controller:'dashboard', action:'compare', params:[period:currentPeriod.id, objective:currentObjective.id])}",
					success: function(data) {
						if(table == 'program'){
							$.each(data.compareValues, function(index, compareValue) {
								var dashboardEntityId = compareValue.id;
								var percentageValue = compareValue.value;
								var compareDiv = $('#compare-dashboard-entity-'+dashboardEntityId);
								if(percentageValue == null){
									$(compareDiv).css('width', '0%');			
									$(compareDiv).attr('title', 'N/A');
									$(compareDiv).attr('data-percentage', 'N/A');							
								}
								else{							
									$(compareDiv).css('width', percentageValue + '%');						
									$(compareDiv).attr('title', percentageValue + '%');
									$(compareDiv).attr('data-percentage', percentageValue);
									if(percentageValue > 100){ 
										$(compareDiv).addClass('expand-bar');
										$(compareDiv).css('width', '100%');
									}
								}
								$(compareDiv).show();
							})
						}
						if(table == 'location'){				
							var dashboardEntityId = $('.horizontal-graph-average').data('entity');
							var percentageValue = data.compareValues[dashboardEntityId].value;
							var compareDiv = $('.horizontal-graph-average');
							var tooltip = $(compareDiv).children('.tooltip');
							var tickmark = $(compareDiv).children('.horizontal-graph-marker');
							if(percentageValue == null){
								$(tooltip).css('left', '0%');
								$(tickmark).css('width', '0%');
								$(tooltip).attr('title', 'N/A');
								$(tooltip).attr('data-percentage', 'N/A');
								
							}
							else{						
								$(tooltip).css('left', percentageValue + '%')
								$(tickmark).css('width', percentageValue + '%');
								$(tooltip).attr('title', percentageValue + '%')
								$(tooltip).attr('data-percentage', percentageValue);
								if(percentageValue > 100){
									$(tooltip).addClass('.expand-tick');
									$(tooltip).css('left', '100%');
									$(tickmark).css('width', '100%');
								}
							}					
							$(compareDiv).show();
						}
					},
					error: function() {
					}
				});
			}
		</r:script>      
    </body>
    
</html>