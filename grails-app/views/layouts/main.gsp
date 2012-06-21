<%@ page import="org.apache.shiro.SecurityUtils" %>
<%@ page import="org.chai.kevin.security.User" %>

<!DOCTYPE html>
<html>
<head>
	<title><g:layoutTitle /></title>
	<link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />

	<g:layoutHead />	
	<r:require module="core"/>
	<r:layoutResources/>

	<ga:trackPageview />
</head>
<body>
  
	<div id="spinner" class="spinner" style="display: none;">
		<img src="${resource(dir:'images',file:'ajax-loader.gif')}" alt="${message(code:'spinner.alt')}" />
	</div>

	<div id="header">
		<div class="wrapper">
		    <h1 id="logo">
		    	<a href="${createLink(controller:'home', action:'index')}"><g:message code="title.dhsst"/></a>
		    </h1>

			<ul class="locales" id="switcher">
				<% def languageService = grailsApplication.mainContext.getBean('languageService') %>
				<g:each in="${languageService.availableLanguages}" var="language" status="i">
					<% params['lang'] = language %>
					<li><a class="${languageService.currentLanguage==language?'no-link':''}" href="${createLink(controller: controllerName, action: actionName, params: params)}">${language}</a></li>
				</g:each>
			</ul>
			
			<ul class="locales" id="top_nav">
				<shiro:user>
					<%
						def user = User.findByUuid(SecurityUtils.subject.principal, [cache: true])
					%>
					<li>
						<a class="${controllerName=='auth'?'active':''}" href="${createLinkWithTargetURI(controller: 'account', action:'editAccount')}">
							<g:message code="header.navigation.myaccount"/> : ${user.firstname} ${user.lastname}
						</a>
					</li>
				</shiro:user>
				<shiro:user>
					<li>
						<a class="no-link" href="${createLink(controller: 'auth', action: 'signOut')}"><g:message code="header.labels.logout"/></a>
					</li>
				</shiro:user>
				<shiro:notUser>
					<g:if test="${controllerName != 'auth' || actionName != 'login'}">
						<li>
							<a class="no-link" href="${createLink(controller: 'auth', action: 'login')}"><g:message code="header.labels.login"/></a>
						</li>
					</g:if>
					<g:if test="${controllerName != 'auth' || actionName != 'register'}">
						<li>
							<a class="no-link" href="${createLink(controller: 'auth', action: 'register')}"><g:message code="header.labels.register"/></a>
						</li>
					</g:if>
				</shiro:notUser>
			</ul>
			
			<h2>
				<span class="right"><img src="${resource(dir:'images',file:'rwanda.png')}" alt="Rwanda coat of arms" width="33" /></span>
				<span><g:message code="header.labels.moh"/></span>
				<g:message code="header.labels.dhsst"/>
			</h2>
			
			<ul id="logout">
				<shiro:hasPermission permission="admin">
					<li>
						<a class="redmine follow" target="_blank" href="http://districthealth.moh.gov.rw/redmine"><g:message code="header.labels.redmine"/></a>
	   				</li>
				</shiro:hasPermission>
				<li>
					<a class="redmine follow" href="${createLink(uri:'/helpdesk')}"><g:message code="header.labels.helpdesk"/></a>
				</li>
			</ul>
		</div>
			
		<div class="clear"></div>
		</div>			
			<!--<h1>Welcome to Kevin</h1>-->
		<div id="navigation">
		<div class="wrapper">
			<ul id="main-menu" class="menu">
				<!-- Home -->
				<shiro:hasPermission permission="menu:home">
					<li><a class="home" href="/kevin/"><img src="${resource(dir:'images',file:'/icons/home_dhsst.png')}" alt="Home"/></a></li>
				</shiro:hasPermission>
				<g:set var="controllerClass" value="${grailsApplication.getArtefactByLogicalPropertyName('Controller', controllerName).getClazz()}" />	
				<!-- Reports -->
				<shiro:hasPermission permission="menu:reports">
					<g:set var="isDashboard" value="${org.chai.kevin.dashboard.DashboardController.class.isAssignableFrom(controllerClass)}" />
					<g:set var="isDsr" value="${org.chai.kevin.dsr.DsrController.class.isAssignableFrom(controllerClass)}" />
					<g:set var="isFct" value="${org.chai.kevin.fct.FctController.class.isAssignableFrom(controllerClass)}" />
					<g:set var="isReports" value="${isDashboard || isDsr || isReports}" />				
					<li><a class="${controllerName!=null && isReports ?'active':''}" href="#"  onclick="return false;">
									<g:message code="header.navigation.reports"/></a>
						<ul class="submenu">
							<li><a class="${controllerName=='dashboard'?'active':''}" href="${createLink(controller: 'dashboard', action:'view')}"><g:message code="header.navigation.reports.dashboard"/></a></li>
							<li><a class="${controllerName=='dsr'?'active':''}" href="${createLink(controller: 'dsr', action:'view')}"><g:message code="header.navigation.reports.dsr"/></a></li>
							<li><a class="${controllerName=='fct'?'active':''}" href="${createLink(controller: 'fct', action:'view')}"><g:message code="header.navigation.reports.fct"/></a></li>
						</ul>
					</li>
				</shiro:hasPermission>
				<!-- Survey -->
				<shiro:hasPermission permission="menu:survey">
					<li><a class="${controllerName=='editSurvey'||controllerName=='surveySummary'?'active':''}" href="${createLink(controller: 'editSurvey', action:'view')}"><g:message code="header.navigation.survey"/></a></li>
				</shiro:hasPermission>
				<!-- Planning -->
				<shiro:hasPermission permission="menu:planning">
					<li><a class="${controllerName=='editPlanning'?'active':''}" href="${createLink(controller: 'editPlanning', action:'view')}"><g:message code="header.navigation.planning"/></a></li>
				</shiro:hasPermission>
				<!-- Admin -->
				<shiro:hasPermission permission="menu:admin">
					<g:set var="isAdmin" value="${org.chai.kevin.AbstractEntityController.class.isAssignableFrom(controllerClass)}" />					
	  				<li><a class="${controllerName!=null && isAdmin?'active':''}" href="#"  onclick="return false;">
	  								<g:message code="header.navigation.administration"/></a>
	  					<ul class="submenu">
	  						<li><a class="${controllerName=='rawDataElement'?'active':''}" href="${createLink(controller: 'rawDataElement', action:'list')}"><g:message code="rawdataelement.label"/></a></li>
	  						<li><a class="${controllerName=='normalizedDataElement'?'active':''}" href="${createLink(controller: 'normalizedDataElement', action:'list')}"><g:message code="normalizeddataelement.label"/></a></li>
	  						<li><a class="${controllerName=='calculation'?'active':''}" href="${createLink(controller: 'calculation', action:'list')}"><g:message code="calculation.label"/></a></li>
	  						<li><a class="${controllerName=='enum'?'active':''}" href="${createLink(controller: 'enum', action:'list')}"><g:message code="enum.label"/></a></li>
	  						<li><a class="${controllerName=='period'?'active':''}" href="${createLink(controller: 'period', action:'list')}"><g:message code="period.label"/></a></li>
	  						<li><a class="${controllerName=='reportProgram'?'active':''}" href="${createLink(controller: 'reportProgram', action:'list')}"><g:message code="reports.program.label"/></a></li>
	  						<li><a class="${controllerName=='dashboardProgram'?'active':''}" href="${createLink(controller: 'dashboardProgram', action:'list')}"><g:message code="dashboard.program.label"/></a></li>
	  						<li><a class="${controllerName=='dashboardTarget'?'active':''}" href="${createLink(controller: 'dashboardTarget', action:'list')}"><g:message code="dashboard.target.label"/></a></li>
	  						<li><a class="${controllerName=='dsrTarget'?'active':''}" href="${createLink(controller: 'dsrTarget', action:'list')}"><g:message code="dsr.target.label"/></a></li>
	  						<li><a class="${controllerName=='dsrTargetCategory'?'active':''}" href="${createLink(controller: 'dsrTargetCategory', action:'list')}"><g:message code="dsr.category.label"/></a></li>
	  						<li><a class="${controllerName=='fctTarget'?'active':''}" href="${createLink(controller: 'fctTarget', action:'list')}"><g:message code="fct.target.label"/></a></li>
	  						<li><a class="${controllerName=='fctTargetOption'?'active':''}" href="${createLink(controller: 'fctTargetOption', action:'list')}"><g:message code="fct.targetoption.label"/></a></li>
	  						<li><a class="${controllerName=='survey'?'active':''}" href="${createLink(controller: 'survey', action:'list')}"><g:message code="survey.label"/></a></li>
	  						<li><a class="${controllerName=='planning'?'active':''}" href="${createLink(controller: 'planning', action:'list')}"><g:message code="planning.label"/></a></li>
	  						<li><a class="${controllerName=='location'?'active':''}" href="${createLink(controller: 'location', action:'list')}"><g:message code="location.label"/></a></li>
	  						<li><a class="${controllerName=='locationLevel'?'active':''}" href="${createLink(controller: 'locationLevel', action:'list')}"><g:message code="locationlevel.label"/></a></li>
	  						<li><a class="${controllerName=='dataLocation'?'active':''}" href="${createLink(controller: 'dataLocation', action:'list')}"><g:message code="datalocation.label"/></a></li>
	  						<li><a class="${controllerName=='dataLocationType'?'active':''}" href="${createLink(controller: 'dataLocationType', action:'list')}"><g:message code="datalocationtype.label"/></a></li>
	  						<li><a class="${controllerName=='user'?'active':''}" href="${createLink(controller: 'user', action:'list')}"><g:message code="user.label"/></a></li>
							<li><a class="${controllerName=='expression'?'active':''}" href="${createLink(controller: 'expression', action:'test')}"><g:message code="expression.test.label"/></a></li>
							<li><a class="${controllerName=='generalImporter'?'active':''}" href="${createLink(controller: 'generalImporter', action:'importer')}"><g:message code="import.general.data.label" /></a></li>
							<li><a class="${controllerName=='nominativeImporter'?'active':''}" href="${createLink(controller: 'nominativeImporter', action:'importer')}"><g:message code="import.nominative.data.label" /></a></li>
							<li><a class="${controllerName=='dataExport'?'active':''}" href="${createLink(controller: 'dataExport', action:'list')}"><g:message code="dataexport.label" /></a></li>
	  					
	  					</ul>
	  				</li>
	  			</shiro:hasPermission>
	  		</ul>
	  	</div>
	</div>

	<div id="flash">
		<g:if test="${flash.message}">
			<!-- TODO add error class if it's an error -->
			<div class="message js_help">${flash.message} <a href="#" class="delete-link js_hide-help">Turn off</a></div>
		</g:if>	
	</div>
		
	<div id="content">
		<div class="wrapper">
			<g:layoutBody />
		</div>
	</div>

	<div id="footer">
		<div class="wrapper push-20">
			&copy; <g:message code="footer.labels.chai"/> <br /><a href="${createLink(controller:'home', action:'about')}"><g:message code="footer.labels.about"/></a> | <a href="${createLink(controller:'home', action:'contact')}"><g:message code="footer.labels.contact"/></a> | <a href="${createLink(controller:'home', action:'helpdesk')}"><g:message code="footer.labels.helpdesk"/></a>
		</div>
		<div style="opacity: 0.6">
			<build:buildInfo/>
		</div>
	</div>

	<r:script>
		//Styling the main menu
		$('#main-menu > li').hover(
			function () {
				//show its submenu
				if (!$('ul', this).hasClass('open')) {
					$('ul', this).addClass('open');
					$('ul', this).show();
				}
			}, 
			function () {
				var self = this;
				//hide its submenu
				$('ul', this).slideUp(10, function(){
					$('ul', self).removeClass('open');
				});	
			}
		);
	</r:script>
	
	<r:layoutResources/>
</body>
</html>
