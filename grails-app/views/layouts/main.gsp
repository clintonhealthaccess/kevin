<%@ page import="org.chai.kevin.util.LanguageUtils" %>

<!DOCTYPE html>
<html>
<head>
	<title><g:layoutTitle default="Grails" /></title>
	<link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />

	<g:layoutHead />	
	<r:require module="core"/>
	<r:layoutResources/>

	<ga:trackPageview />
</head>
<body>
  
	<div id="spinner" class="spinner" style="display: none;">
		<img src="${resource(dir:'images',file:'ajax-loader.gif')}" alt="${message(code:'spinner.alt',default:'Loading...')}" />
	</div>

	<div id="header">
	  <div class="wrapper">
	    <h1 id="logo">DHSST</h1>
			<ul class="locales" id="switcher">
				<g:each in="${LanguageUtils.availableLanguages}" var="language" status="i">
					<% params['lang'] = language %>
					<li><a class="${LanguageUtils.currentLanguage==language?'no-link':''}" href="${createLink(controller: controllerName, action: actionName, params: params)}">${language}</a></li>
				</g:each>
			</ul>
			<h2><span>Rwanda Ministry Of Health</span>District Health Systems Strenghtening Tool</h2>
			
			<ul id="logout">
				<shiro:hasPermission permission="admin">
					<li>
						<a class="redmine follow" target="_blank" href="http://districthealth.moh.gov.rw/redmine"><g:message code="header.labels.redmine" default="Found a bug? Go to REDMINE"/></a>
    				</li>
				</shiro:hasPermission>
				<li>
					<a class="redmine follow" href="${createLink(uri:'/helpdesk')}"><g:message code="header.labels.helpdesk" default="Questions? Call the Helpdesk 114"/></a>
				</li>
				<shiro:user>
				<li>
					<a class="follow" href="${createLink(controller: 'auth', action: 'signOut')}"><g:message code="header.labels.logout" default="Logout"/></a>
				</li>
				</shiro:user>
			</div>
			
			<div class="clear"></div>
		</div>			
			<!--<h1>Welcome to Kevin</h1>-->
			<div id="navigation">
				<div class="wrapper">
			  		<ul id="main-menu" class="menu">
			  		    <shiro:hasPermission permission="menu:survey">
			  				<li><a class="${controllerName=='editSurvey'?'active':''}" href="${createLink(controller: 'editSurvey', action:'view')}"><g:message code="header.navigation.survey" default="Survey"/></a></li>
			  			</shiro:hasPermission>
			  			<shiro:hasPermission permission="menu:reports">
			  				<li><a class="${controllerName in ['dashboard','cost','dsr','maps']?'active':''}" href="#" onclick="return false;"><g:message code="header.navigation.reports" default="Reports"/></a>
			  					<ul class="submenu">
			  						<li><a class="${controllerName=='dashboard'?'active':''}" href="${createLink(controller: 'dashboard', action:'view')}"><g:message code="header.navigation.dashboard" default="Dashboard"/></a></li>
			  						<li><a class="${controllerName=='cost'?'active':''}" href="${createLink(controller: 'cost', action:'view')}"><g:message code="header.navigation.costing" default="Costing"/></a></li>
			  						<li><a class="${controllerName=='dsr'?'active':''}" href="${createLink(controller: 'dsr', action:'view')}"><g:message code="header.navigation.dsr" default="District Summary Reports"/></a></li>
			  						<li><a class="${controllerName=='maps'?'active':''}" href="${createLink(controller: 'maps', action:'view')}"><g:message code="header.navigation.maps" default="Maps"/></a></li>
			  						<li><a class="${controllerName=='fct'?'active':''}" href="${createLink(controller: 'fct', action:'view')}"><g:message code="header.navigation.fct" default="Facility Count Tables"/></a></li>
			  					</ul>
			  				</li>
			  			</shiro:hasPermission>
			  			<shiro:hasPermission permission="menu:admin">
			  				<li><a class="${controllerName!=null && org.chai.kevin.AbstractEntityController.class.isAssignableFrom(grailsApplication.getArtefactByLogicalPropertyName('Controller', controllerName).getClazz())?'active':''}" href="#"  onclick="return false;"><g:message code="header.navigation.administration" default="Administration"/></a>
			  					<ul class="submenu">
			  						<li><a class="${controllerName=='expression'?'active':''}" href="${createLink(controller: 'expression', action:'list')}"><g:message code="expression.label" default="Expressions"/></a></li>
			  <!-- 								<li><a href="${createLink(controller: 'constant', action:'list')}"><g:message code="header.navigation.constants" default="Constants"/></a></li> -->
			  						<li><a class="${controllerName=='dataElement'?'active':''}" href="${createLink(controller: 'dataElement', action:'list')}"><g:message code="dataelement.label" default="Data Element"/></a></li>
			  						<li><a class="${controllerName=='enum'?'active':''}" href="${createLink(controller: 'enum', action:'list')}"><g:message code="enum.label" default="Enum"/></a></li>
			  						<li><a class="${controllerName=='iteration'?'active':''}" href="${createLink(controller: 'iteration', action:'list')}"><g:message code="period.label" default="Iterations"/></a></li>
			  						<li><a class="${controllerName=='survey'?'active':''}" href="${createLink(controller: 'survey', action:'list')}"><g:message code="survey.label" default="Survey"/></a></li>
			  					</ul>
			  				</li>
			  			</shiro:hasPermission>
			  		</ul>
			  	</div>
			</div>
		</div>	
	</div>
			
	<div id="content">
	  <div class="wrapper">
			<g:layoutBody />
			<div class=clear></div>
		</div>
	</div>

	<div id="footer">
	  <div class="wrapper">
		  &copy; <g:message code="footer.labels.chai" default="Clinton Health Access Initiative"/> <br /><a href="${createLink(uri: '/about')}"><g:message code="footer.labels.about" default="About"/></a> | <a href="${createLink(uri:'/contact')}"><g:message code="footer.labels.contact" default="Contact"/></a> | <a href="${createLink(uri:'/helpdesk')}"><g:message code="footer.labels.helpdesk" default="Helpdesk"/></a>
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
