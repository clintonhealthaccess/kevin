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
		    <h1 id="logo"><a href="${createLink(controller:'home', action:'index')}">DHSST</a></h1>
		    
			<g:if test="${flash.message}">
				<!-- TODO add error class if it's an error -->
				<div class="message">${flash.message}</div>
        	</g:if>
		    
			<ul class="locales" id="switcher">
				<% def languageService = grailsApplication.mainContext.getBean('languageService') %>
				<g:each in="${languageService.availableLanguages}" var="language" status="i">
					<% params['lang'] = language %>
					<li><a class="${languageService.currentLanguage==language?'no-link':''}" href="${createLink(controller: controllerName, action: actionName, params: params)}">${language}</a></li>
				</g:each>
			</ul>
			<h2>
				<span class="right"><img src="${resource(dir:'images',file:'rwanda.png')}" alt='Rwanda coat of arms' width='33' /></span>
				<span><g:message code="header.labels.moh"/></span>
				<g:message code="header.labels.dhsst"/>
			</h2>
			
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
				<shiro:notUser>
					<g:if test="${controllerName != 'auth' || actionName != 'login'}">
						<li>
							<a class="follow" href="${createLink(controller: 'auth', action: 'login')}"><g:message code="header.labels.login" default="Login"/></a>
						</li>
					</g:if>
					<g:if test="${controllerName != 'auth' || actionName != 'register'}">
						<li>
							<a class="follow" href="${createLink(controller: 'auth', action: 'register')}"><g:message code="header.labels.register" default="Request access"/></a>
						</li>
					</g:if>
				</shiro:notUser>
			</ul>
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
			  						<li><a class="${controllerName=='user'?'active':''}" href="${createLink(controller: 'user', action:'list')}"><g:message code="user.label" default="User"/></a></li>
			  					</ul>
			  				</li>
			  			</shiro:hasPermission>
			  			<shiro:user>
			  				<li><a class="${controllerName in ['auth']?'active':''}" href="#" onclick="return false;"><g:message code="header.navigation.account" default="My Account"/></a>
			  					<ul class="submenu">
			  						<li><a class="${controllerName=='auth'?'active':''}" href="${createLinkWithTargetURI(controller: 'auth', action:'newPassword')}"><g:message code="header.navigation.password" default="Change password"/></a></li>
			  					</ul>
			  				</li>
			  			</shiro:user>
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
		  &copy; <g:message code="footer.labels.chai" default="Clinton Health Access Initiative"/> <br /><a href="${createLink(controller:'home', action:'about')}"><g:message code="footer.labels.about" default="About"/></a> | <a href="${createLink(controller:'home', action:'contact')}"><g:message code="footer.labels.contact" default="Contact"/></a> | <a href="${createLink(controller:'home', action:'helpdesk')}"><g:message code="footer.labels.helpdesk" default="Helpdesk"/></a>
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
