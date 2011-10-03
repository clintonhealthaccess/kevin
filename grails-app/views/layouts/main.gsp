<!DOCTYPE html>
<html>
<head>
	<title><g:layoutTitle default="Grails" /></title>
	<link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
	<link href="${resource(dir:'css',file:'screen.css')}" type="text/css" rel="stylesheet" media="screen, projection"/>

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
			<% def localeService = application.getAttribute("org.codehaus.groovy.grails.APPLICATION_CONTEXT").getBean("localeService") %>
			<ul class="locales" id="switcher">
				<g:each in="${localeService.availableLanguages}" var="language" status="i">
					<% params['lang'] = language %>
					<li><a class="${localeService.currentLanguage==language?'no-link':''}" href="${createLink(controller: controller, action: action, params:params)}">${language}</a></li>
				</g:each>
			</ul>
			<h2><span>Rwanda Ministry Of Health</span>District Health Systems Strenghtening Tool</h2>
			<shiro:user>
				<ul id="logout">
				  <li>
					  <shiro:hasPermission permission="admin">
        			<a class="redmine" target="_blank" href="http://districthealth.moh.gov.rw/redmine">Found a bug? Go to REDMINE</a>
        		</shiro:hasPermission>
					</li>
					<li><a href="${createLink(controller: 'auth', action: 'signOut')}">Logout</a></li>
					
				</div>
			</shiro:user>
		</div>
	</div>	

	<div id="navigation">
	  <div class="wrapper">
  		<ul id="main-menu" class="menu">
  		    <shiro:hasPermission permission="menu:survey">
  				<li><a href="${createLink(controller: 'editSurvey', action:'view')}"><g:message code="header.navigation.survey" default="Survey"/></a></li>
  			</shiro:hasPermission>
  			<shiro:hasPermission permission="menu:reports">
  				<li><a href="#"><g:message code="header.navigation.reports" default="Reports"/></a>
  					<ul class="submenu">
  						<li><a href="${createLink(controller: 'dashboard', action:'view')}"><g:message code="header.navigation.dashboard" default="Dashboard"/></a></li>
  						<li><a href="${createLink(controller: 'cost', action:'view')}"><g:message code="header.navigation.costing" default="Costing"/></a></li>
  						<li><a href="${createLink(controller: 'dsr', action:'view')}"><g:message code="header.navigation.dsr" default="District Summary Reports"/></a></li>
  						<li><a href="${createLink(controller: 'maps', action:'view')}"><g:message code="header.navigation.maps" default="Maps"/></a></li>
  					</ul>
  				</li>
  			</shiro:hasPermission>
  			<shiro:hasPermission permission="menu:admin">
  				<li><a href="#"><g:message code="header.navigation.administration" default="Administration"/></a>
  					<ul class="submenu">
  						<li><a href="${createLink(controller: 'expression', action:'list')}"><g:message code="header.navigation.expressions" default="Expressions"/></a></li>
  <!-- 								<li><a href="${createLink(controller: 'constant', action:'list')}"><g:message code="header.navigation.constants" default="Constants"/></a></li> -->
  						<li><a href="${createLink(controller: 'dataElement', action:'list')}"><g:message code="header.navigation.dataelement" default="Data Element"/></a></li>
  						<li><a href="${createLink(controller: 'enum', action:'list')}"><g:message code="header.navigation.enum" default="Enum"/></a></li>
  						<li><a href="${createLink(controller: 'iteration', action:'list')}"><g:message code="header.navigation.iteration" default="Iterations"/></a></li>
  						<li><a href="${createLink(controller: 'survey', action:'list')}"><g:message code="header.navigation.survey" default="Survey"/></a></li>
  					</ul>
  				</li>
  			</shiro:hasPermission>
  		</ul>
  		
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
		  &copy; Clinton Health Access Initiative <br /><a href="#"><g:message code="footer.labels.about" default="About"/></a> | <a href="#"><g:message code="footer.labels.contact" default="Contact"/></a> | <a href="#"><g:message code="footer.labels.helpdesk" default="Helpdesk"/></a>
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
