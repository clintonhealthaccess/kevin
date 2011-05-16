<!DOCTYPE html>
<html>
<head>
	<title><g:layoutTitle default="Grails" /></title>
	<link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
	<!-- link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" / -->
	
	<link href="${resource(dir:'css',file:'screen.css')}" media="screen, projection" rel="stylesheet" type="text/css" />
	<link href="${resource(dir:'css',file:'print.css')}" media="print" rel="stylesheet" type="text/css" />
	<!--[if lt IE 8]>
		<link href="${resource(dir:'css',file:'ie.css')}" media="screen, projection" rel="stylesheet" type="text/css" />
	<![endif]-->
	
	<link rel="stylesheet" type="text/css" href="${resource(dir:'js/jquery/sexy-combo/css',file:'sexy-combo.css')}" />
	<link rel="stylesheet" type="text/css" href="${resource(dir:'js/jquery/sexy-combo/css',file:'sexy/sexy.css')}"/ >
	<link rel="stylesheet" type="text/css" href="${resource(dir:'js/jquery/cluetip',file:'jquery.cluetip.css')}"/ >
	
	<g:layoutHead />
	<g:javascript library="jquery" plugin="jquery" />
	<jqui:resources />
	
	<!-- jq:resource components="searchabledropdown" bundle="jquery"/ -->
	<!-- TODO replace with sexy combo -->
	<!-- g:javascript src="jquery/searchabledropdown/jquery.searchabledropdown-1.0.7.src.js" / -->
	<!-- g:javascript src="jquery/blockui/jquery.blockui.js" / -->
	<g:javascript src="jquery/sexy-combo/jquery.sexy-combo.js" />
	<g:javascript src="jquery/form/jquery.form.js" />
	<g:javascript src="jquery/fieldselection/jquery.fieldselection.js" />
	<g:javascript src="jquery/cluetip/jquery.cluetip.js" />
	<g:javascript src="jquery/periodicalupdater/jquery.periodicalupdater.js" />

	<g:javascript library="application" />
	
</head>
<body class="bp two-columns">

	<div id="spinner" class="spinner" style="display: none;">
		<img src="${resource(dir:'images',file:'spinner.gif')}" alt="${message(code:'spinner.alt',default:'Loading...')}" />
	</div>

	<div id="container" class="">
		<div id="header">
			<h1>Welcome to Kevin</h1>

			<div id="navigation">
				<ul class="menu">
					<li><a href="${createLink(controller: 'dashboard', action:'view')}">Dashboard</a></li>
					<li><a href="${createLink(controller: 'cost', action:'view')}">Costing</a></li>
					<li><a href="${createLink(controller: 'maps', action:'view')}">Maps</a></li>
				</ul>
				<ul class="menu">
					<li><a href="${createLink(controller: 'expression', action:'list')}">Expressions</a></li>
					<li><a href="${createLink(controller: 'constant', action:'list')}">Constants</a></li>
				</ul>
			</div>
		</div>

		<div id="content">
			<g:layoutBody />

			<div class=clear></div>
		</div>

		<div id="footer">Ministry of Health - About | Contact | Helpdesk</div>
	</div>

	<!-- utilities -->
	<script type="text/javascript">
		/**
		 * edition pane functionality
		 */
		(function($) {
			var defaults = {
				container: '.flow-container',
				addLinks: '.flow-edit,.flow-add',
				deleteLinks: '.flow-delete',
				onSuccess: function(){}
			}
			
			$.fn.flow = function(options) {
			    this.config = $.extend({}, defaults, options || {}); 

				var values = this;
				var container = this.next(this.config.container).first();
				 
				var self = this;
				$(document).find(this.config.addLinks).each(function(index, element){
					$(element).bind('click', function() {
						$.ajax({
							type : 'GET',
							dataType: 'json',
							url : $(this).attr('href'),
							success : function(data) {
								if (data.result == 'success') {
									$(values).slideUp(function() {
										$(container).html(data.html);
										$(container).slideDown('slow', function(){
											bindEvents(container, values, self.config.onSuccess);
										});
									});
								}
							}
						});
						return false;
					});
				});
				
				this.find(this.config.deleteLinks).each(function(index, element) {
					$(element).bind('click', function() {
						$.ajax({
							type : 'POST',
							dataType: 'json',
							url : $(this).attr('href'),
							success : function() {
								location.reload();
							}
						});
						return false;
					});
				});
			}
			
			function bindEvents(container, values, onSuccess) {
				var cancel = $(container).find("#cancel-button").first();
				var form = $(container).find("form").first();
				
				$(cancel).bind('click', function() {
					// $('#add-expression-cancel').click();
					clearForm(form);
					$(container).slideUp('slow', function() {
						$(values).slideDown();
					});
					return false;	
				});

				$(form).find("button[type=submit]").bind('click', function() {$(this).submit(); return false;})				
				$(form).bind('submit', function() {
					$.ajax({
						type : 'POST',
						dataType: 'json',
						data : $(this).serialize(),
						url : $(this).attr('action'),
						success : function(data, textStatus) {
							onSuccess(data);
							if (data.result == 'error') {
								$(container).html(data.html);
								bindEvents(container, values, onSuccess);
							} 
							else {
								$(container).slideUp('slow', function() {
									$(values).slideDown();
								});
								clearForm(form);
							}
						}
					});
					return false;
				});
			}

			function clearForm(form) {
				$(form).clearForm();
				$(form).find('.errors').removeClass('errors');
				$(form).find('.error-list ul').remove();
			}
			
		})(jQuery);
		
		
		var cluetipOptions = {
			ajaxProcess : function(data) {
				return data.html;
			},
			ajaxSettings : {
				dataType : 'json'
			},
			cluetipClass : 'jtip',
			hoverIntent : false,
			dropShadow : false,
			width : '400px'
		};
		// end of edition pane functionality

		/**
		 * nice table functionality
		 */
 		var selected = false;
		function addClass(column, row, className) {
			if (column != null) {
				addClassByType('col', column, className);
			}
			if (row != null) {
				addClassByType('row', row, className);
			}
		}
		
		function addClassByType(type, id, className) {
				$('.'+type+'-'+id).addClass(className);
		}    
		// END of nice table functionality
		
		/**
		 * explanation functionality
		 */
		function explanationClick(element, prefix, onSuccess){
			if ($(element).find('a').size() == 0) return;
			
			if ($(element).hasClass('me-open')) {
				toClose = true
			}
			else {
				toClose = false
			}
			$('.me-open').removeClass('me-open');
			
			if (toClose) {
				slideUp(function(){});
			}
			else {
				if ($('#explanation-'+prefix).hasClass('loaded')) {
					slideUpExplanation(function(){slideDown(prefix);});
				}
				else {
					$.ajax(
					{
						type:'GET', url: $(element).find('a').attr('href'),
						success: function(data) {
							slideUpExplanation(function() {
								addData(prefix, data);
								onSuccess(prefix);
							});
						}
					});
				}
				$(element).addClass('me-open');
			}
   		}
   		
   		function slideUpExplanation(callback) {
   			if ($('.explanation-cell.visible').length == 0) {
				callback();
			}
			else {
				slideUp(callback);
			}
   		}
   		
   		function slideUp(callback) {
   			$('.explanation-cell.visible').slideUp("slow", function() {callback();});
   			$('.explanation-cell.visible').removeClass('visible');
      		}
   		
   		function slideDown(prefix) {
			$('#explanation-'+prefix).addClass('visible');
   			$('#explanation-'+prefix).slideDown('slow');
   		}
   		
   		function addData(prefix, data) {
   			$('#explanation-'+prefix).html(data);
			$('#explanation-'+prefix).addClass('loaded');
			
			slideDown(prefix);
   		}
		// END of explanation functionality
		
		$(document).ready(function() {
			/**
			 * date drop-down
			 **/
			$(".dropdown .selected").live('click', function() {
				$(this).parent(".dropdown").find("div.dropdown-list").toggle();
				return false;
			});

			$(document).bind('click', function(e) {
				var clicked = e.target;
				$(".dropdown a").each(function(){
					if (clicked != this) {
						$(this).parent(".dropdown").find("div.dropdown-list").hide();	
					}
				});
			});
			
			/**
			 * foldable lists
			 */
			$(".foldable").click(function() {
				
			})
			
			/**
			 * nice tables
			 */
			$('.nice-table .cell').bind({
				mouseenter: function(){
					$('.cell').removeClass('highlighted');
					addClass($(this).data('col'), $(this).data('row'), 'highlighted');
				},
				mouseleave: function(){
					if (selected) {
						$('.cell').removeClass('highlighted');
					}
					else {
						$('.cell.value').addClass('highlighted');
						$('.cell.label').removeClass('highlighted');
					}
				},
				click: function(){
					var row = $(this).data('row');
					var col = $(this).data('col');
				
					if ($(this).hasClass('me-selected')) {
						deselect = true
						selected = false;
					}
					else {
						deselect = false;
						selected = true;
					}
					$('.cell').removeClass('me-selected');
					$('.cell').removeClass('selected');
				
					if (!deselect) {
						$(this).addClass('me-selected');
						addClass(col, row, 'selected');
					}
				}
			});
			
		});
	</script>
</body>
</html>
