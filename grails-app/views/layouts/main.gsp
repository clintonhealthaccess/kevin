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
			
			<ul class="locales" id="top_nav">
			  <shiro:user>
					<li><a class="${controllerName=='auth'?'active':''}" href="${createLinkWithTargetURI(controller: 'auth', action:'newPassword')}"><g:message code="header.navigation.password" default="My Account"/></a></li>
  				</li>
  			</shiro:user>
				<shiro:user>
					<li>
						<a class="no-link" href="${createLink(controller: 'auth', action: 'signOut')}"><g:message code="header.labels.logout" default="Logout"/></a>
					</li>
				</shiro:user>
				<shiro:notUser>
					<g:if test="${controllerName != 'auth' || actionName != 'login'}">
						<li>
							<a class="no-link" href="${createLink(controller: 'auth', action: 'login')}"><g:message code="header.labels.login" default="Login"/></a>
						</li>
					</g:if>
					<g:if test="${controllerName != 'auth' || actionName != 'register'}">
						<li>
							<a class="no-link" href="${createLink(controller: 'auth', action: 'register')}"><g:message code="header.labels.register" default="Request access"/></a>
						</li>
					</g:if>
				</shiro:notUser>
			</ul>
			
			<h2>
				<span class="right"><img src="${resource(dir:'images',file:'rwanda.png')}" alt='Rwanda coat of arms' width='33' /></span>
				<span><g:message code="header.labels.moh"/></span>
				<g:message code="header.labels.dhsst"/>
			</h2>
			
			<ul id="logout">
				<shiro:hasPermission permission="admin">
					<li>
						<a class="redmine follow" target="_blank" href="http://districthealth.moh.gov.rw/redmine"><g:message code="header.labels.redmine" default="Report a bug"/></a>
	   				</li>
				</shiro:hasPermission>
				<li>
					<a class="redmine follow" href="${createLink(uri:'/helpdesk')}"><g:message code="header.labels.helpdesk" default="Helpdesk 114"/></a>
				</li>
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
	  				<li><a class="${controllerName=='dashboard'?'active':''}" href="${createLink(controller: 'dashboard', action:'view')}"><g:message code="header.navigation.reports" default="Reports"/></a></li>
	  			</shiro:hasPermission>
	  			<shiro:hasPermission permission="menu:admin">
	  				<li><a class="${controllerName!=null && org.chai.kevin.AbstractEntityController.class.isAssignableFrom(grailsApplication.getArtefactByLogicalPropertyName('Controller', controllerName).getClazz())?'active':''}" href="#"  onclick="return false;"><g:message code="header.navigation.administration" default="Administration"/></a>
	  					<ul class="submenu">
	  <!-- 								<li><a href="${createLink(controller: 'constant', action:'list')}"><g:message code="header.navigation.constants" default="Constants"/></a></li> -->
	  						<li><a class="${controllerName=='rawDataElement'?'active':''}" href="${createLink(controller: 'rawDataElement', action:'list')}"><g:message code="rawdataelement.label" default="Raw data Element"/></a></li>
	  						<li><a class="${controllerName=='normalizedDataElement'?'active':''}" href="${createLink(controller: 'normalizedDataElement', action:'list')}"><g:message code="normalizedDataElement.label" default="Normalized data element"/></a></li>
	  						<li><a class="${controllerName=='calculation'?'active':''}" href="${createLink(controller: 'calculation', action:'list')}"><g:message code="calculation.label" default="Calculation"/></a></li>
	  						<li><a class="${controllerName=='enum'?'active':''}" href="${createLink(controller: 'enum', action:'list')}"><g:message code="enum.label" default="Enum"/></a></li>
	  						<li><a class="${controllerName=='iteration'?'active':''}" href="${createLink(controller: 'iteration', action:'list')}"><g:message code="period.label" default="Iterations"/></a></li>
	  						<li><a class="${controllerName=='dashboardObjective'?'active':''}" href="${createLink(controller: 'dashboardObjective', action:'list')}"><g:message code="dashboard.objective.label" default="Dashboard Objective"/></a></li>
	  						<li><a class="${controllerName=='dashboardTarget'?'active':''}" href="${createLink(controller: 'dashboardTarget', action:'list')}"><g:message code="dashboard.target.label" default="Dashboard Target"/></a></li>
	  						<li><a class="${controllerName=='dsrTarget'?'active':''}" href="${createLink(controller: 'dsrTarget', action:'list')}"><g:message code="dsr.target.label" default="DSR Target"/></a></li>
	  						<li><a class="${controllerName=='dsrTargetCategory'?'active':''}" href="${createLink(controller: 'dsrTargetCategory', action:'list')}"><g:message code="dsr.targetcategory.label" default="DSR Target Category"/></a></li>
	  						
	  						<li><a class="${controllerName=='survey'?'active':''}" href="${createLink(controller: 'survey', action:'list')}"><g:message code="survey.label" default="Survey"/></a></li>
	  						<li><a class="${controllerName=='location'?'active':''}" href="${createLink(controller: 'location', action:'list')}"><g:message code="location.label" default="Location"/></a></li>
	  						<li><a class="${controllerName=='locationLevel'?'active':''}" href="${createLink(controller: 'locationLevel', action:'list')}"><g:message code="locationLevel.label" default="Location Level"/></a></li>
	  						<li><a class="${controllerName=='dataLocation'?'active':''}" href="${createLink(controller: 'dataLocation', action:'list')}"><g:message code="dataLocation.label" default="Data Location"/></a></li>
	  						<li><a class="${controllerName=='dataEntityType'?'active':''}" href="${createLink(controller: 'dataEntityType', action:'list')}"><g:message code="dataEntityType.label" default="Data Entity Type"/></a></li>
	  						<li><a class="${controllerName=='user'?'active':''}" href="${createLink(controller: 'user', action:'list')}"><g:message code="user.label" default="User"/></a></li>
							<li><a class="${controllerName=='importerEntity'?'active':''}" href="${createLink(controller: 'importerEntity', action:'importer')}"><g:message code="import.label" default="Import"/></a></li>
	  				
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
