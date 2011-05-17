<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <title><g:message code="maps.view.label" default="Maps" /></title>
        
        <script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>
    </head>
    <body>
    	<div id="maps">
			<div id="top" class="box">
				<div class="filter">
					<h5>Iteration</h5>
					<div class="dropdown dropdown-period">
						<a class="selected" href="#" data-period="${currentPeriod.id}" data-type="period"><g:dateFormat format="yyyy" date="${currentPeriod.startDate}"/></a>
						<div class="hidden dropdown-list">
							<ul>
								<g:each in="${periods}" var="period">
									<li>
										<a class="parameter" href="#" data-period="${period.id}" data-type="period">
											<g:dateFormat format="yyyy" date="${period.startDate}"/>
										</a>
									</li>
								</g:each>
							</ul>
						</div> 
					</div>
				</div>
				<div class="filter">
					<h5>Organisation:</h5>
					<div class="dropdown dropdown-organisation">
						<g:if test="${currentOrganisation != null}">
							<a class="selected" href="#" data-organisation="${currentOrganisation.id}" data-type="organisation">${currentOrganisation.name}</a>
						</g:if>
						<g:else>
							<a class="selected" href="#" data-type="organisation">no organisation selected</a>
						</g:else>
						<div class="hidden dropdown-list">
							<ul>
								<g:render template="/templates/organisationTree" model="[controller: 'maps', action: 'view', organisation: organisationTree, params:[period: currentPeriod.id, objective: currentTarget?.id], displayLinkUntil: displayLinkUntil]"/>
							</ul>
						</div>
					</div>
				</div>
				<div class="filter">
					<h5>Target:</h5>
					<div class="dropdown dropdown-target">
						<g:if test="${currentTarget != null}">
							<a class="selected" href="#" data-target="${currentTarget.id}" data-type="target">${currentTarget.name}</a>
						</g:if>
						<g:else>
							<a class="selected" href="#" data-type="target">no target selected</a>
						</g:else>
						<div class="hidden dropdown-list">
							<g:if test="${!targets.empty}">
								<ul>
									<g:each in="${targets}" var="target">
										<li>
											<a class="parameter" href="#" data-target="${target.id}" data-type="target">
												${target.name}
											</a>
											<span><g:link class="flow-edit" controller="mapsTarget" action="edit" id="${target.id}" class="flow-edit">(edit)</g:link></span>
										</li>
									</g:each>
								</ul>
							</g:if>
							<g:else>
								<span>no targets found</span>
							</g:else>
						</div>
					</div>
				</div>
				<g:if test="${true || user.admin}">
					<div>
						<a id="add-maps-target-link" class="flow-add" href="${createLink(controller:'mapsTarget', action:'create')}" class="flow-add">add target</a>
					</div>
				</g:if>
				<div class="clear"></div>
			</div>
    		<div id="center" class="box">
    			<div id="map_canvas"></div>
    			
    			<!-- ADMIN SECTION -->
		    	<g:if test="${true || user.admin}">
	    			<div class="hidden flow-container"></div>

					<script type="text/javascript">
						$(document).ready(function() {
							$('#map_canvas').flow({
								onSuccess: function(data) {
									if (data.result == 'success') {
										location.reload();
									}
								}
							});
						});
					</script>
		    	</g:if>
		    	<div class="clear"></div>
    		</div>
    	</div>
    	
    	<script type="text/javascript">
    		var opacity = 0.6;
    		var opacitySelected = 0.9;
    		var centerZoom = 9;
    		var centerLatLng = new google.maps.LatLng(-1.93,29.84);
    		
    		// holds state for the level, 
    		// HTML holds the state for the other parameters
    		var levelControl;
    		
    		var map;
    		var polygons = {};
    		
    		function LevelControl(controlDiv) {
    			// Set CSS styles for the DIV containing the control
				// Setting padding to 5 px will offset the control
				// from the edge of the map
				controlDiv.style.padding = '5px';
				
				// Set CSS for the control border
				this.controlUI = document.createElement('DIV');
				this.controlUI.style.backgroundColor = 'white';
				this.controlUI.style.cursor = 'pointer';
				this.controlUI.style.borderStyle = 'solid';
				this.controlUI.style.borderTopWidth = '1px';
				this.controlUI.style.borderLeftWidth = '1px';
				this.controlUI.style.borderRightWidth = '1px';
				this.controlUI.style.marginTop = '40px';
				// this.controlUI.style.width = '100px';
				this.controlUI.title = '';
				controlDiv.appendChild(this.controlUI);
				
				this.setLevels = function(levels, selectedLevel) {
					var self = this;
					self.selectedLevel = selectedLevel
				
					this.controlUI.innerHTML = '';
					$.each(levels, function(key, element){
						var controlText = document.createElement('DIV');
						controlText.style.fontFamily = 'Arial,sans-serif';
						controlText.style.fontSize = '12px';
						controlText.style.paddingLeft = '4px';
						controlText.style.paddingRight = '4px';
						controlText.style.paddingTop = '2px';
						controlText.style.paddingBottom = '2px';
						controlText.style.borderStyle = 'solid';
						controlText.style.borderBottomWidth = '1px';
						if (element.level == selectedLevel) {
							controlText.selected = true;
							controlText.style.backgroundColor = 'lightBlue';
						}
						controlText.level = element.level;
						controlText.innerHTML = element.name;

						google.maps.event.addDomListener(controlText, 'click', function() {
							load({level: controlText.level});
						});
						google.maps.event.addDomListener(controlText, 'mouseover', function() {
							controlText.style.backgroundColor = 'lightBlue';
						});
						google.maps.event.addDomListener(controlText, 'mouseout', function() {
							if (!controlText.selected) controlText.style.backgroundColor = 'white';
						});
						self.controlUI.appendChild(controlText);
					});
				}
				
    		}
    	
			function initialize() {
				var latlng = new google.maps.LatLng(-1.93,29.84);
				var myOptions = {
					zoom: centerZoom,
					center: centerLatLng,
					mapTypeId: google.maps.MapTypeId.ROADMAP,
					disableDoubleClickZoom: true,
					streetViewControl: false
				};
				
				map = new google.maps.Map(document.getElementById("map_canvas"),myOptions);
				google.maps.event.addListener(map, 'dblclick', function(){
					load({organisation: getParent('organisation', $('.dropdown-organisation .selected').data('organisation')), level: null});
				});
				
				// Create the DIV to hold the control and call the HomeControl() constructor
				// passing in this DIV.
				var levelControlDiv = document.createElement('DIV');
				levelControlDiv.index = 1;
				map.controls[google.maps.ControlPosition.RIGHT_TOP].push(levelControlDiv);
				
				levelControl = new LevelControl(levelControlDiv, map);				
  			}
    
    		function recenter() {
    			map.setZoom(9);
    			map.setCenter(centerLatLng);
    		}
    	
    		function drawMap(parameters) {
    			$.ajax({
    				type: 'GET',
    				url: "${createLink(controller: 'maps', action: 'map')}",
    				data: {period: parameters.period, target: parameters.target, organisation: parameters.organisation, level: parameters.level},
    				success: function(data) {
    					if (data.result == 'success') {
    						clearMap();

							window.location.hash = 'period='+data.map.selectedPeriod+'&organisation='+data.map.selectedOrganisation+'&level='+data.map.selectedLevel+'&target='+data.map.selectedTarget;
	
    						levelControl.setLevels(data.map.levels, data.map.selectedLevel);
    						select('organisation', data.map.selectedOrganisation)
    						select('target', data.map.selectedTarget)
    						select('period', data.map.selectedPeriod)
    					
    						if (data.map.selectedCoordinates != null) {
	    						var bounds = getPolygonBounds(data.map.selectedCoordinates[0][0]);
	    						map.fitBounds(bounds);
    						}
    						else {
    							recenter();
    						}
    						
    						$.each(data.map.polygons, function(key, element){
	    						var polygon = [];
		    					if (element.organisation.coordinates != null) {
		    						$.each(element.organisation.coordinates[0][0], function(key, element){
										var point = new google.maps.LatLng(element[1], element[0])
										polygon.push(point)
									});
		    						var polygonBounds = getPolygonBounds(element.organisation.coordinates[0][0])
		    						
		    						var polygon = new google.maps.Polygon({
										paths: polygon,
										strokeColor: element.color,
										strokeOpacity: opacitySelected,
										strokeWeight: 2,
										fillColor: element.color,
										fillOpacity: opacity
									});
									polygon.organisation = element.organisation;
									polygon.organisation.bounds = polygonBounds;
									polygons[element.organisation.id] = polygon;
										
									polygon.setMap(map);
									google.maps.event.addListener(polygon, 'dblclick', function() {
										load({organisation: polygon.organisation.id, level: null});
									});
									google.maps.event.addListener(polygon, 'click', function() {
										$.each(polygons, function(key, element) {
											overrideOptions(element, {fillOpacity: opacity});
											polygon.selected = false
										});
										polygon.selected = true
										overrideOptions(polygon, {fillOpacity: opacitySelected})
									});
									google.maps.event.addListener(polygon, 'mouseover', function() {
										$.each(polygons, function(key, element) {
											if (!element.selected) overrideOptions(element, {fillOpacity: opacity});
										});
										overrideOptions(polygon, {fillOpacity: opacitySelected})
									});
									google.maps.event.addListener(polygon, 'mouseout', function() {
										$.each(polygons, function(key, element) {
											if (!element.selected) overrideOptions(element, {fillOpacity: opacity});
										});
										if (!polygon.selected) overrideOptions(polygon, {fillOpacity: opacity});
									});
	    						}
							})
    					}
    				},
    				error: function(data, textStatus, error) {alert(error)}
    			});
    		}
    		
    		function overrideOptions(polygon, options) {
    			polygon.setOptions($.extend({},polygon.options,options))
    		}
    		
    		function getPolygonBounds(coordinates) {
    			var polygonBounds = new google.maps.LatLngBounds()
				$.each(coordinates, function(key, element){
					var point = new google.maps.LatLng(element[1], element[0])
					polygonBounds.extend(point)
				});
				return polygonBounds;
    		}
    		
    		function clearMap() {
    			$.each(polygons, function(key, element){
    				element.setMap(null);
    			});
    			// polygons = [];
    		}
    		
    		function getParent(type, id) {
    			var result = null;
    			$('.dropdown-'+type+' .parameter').each(function(key, element){
    				if ($(element).data(type) == id) {
    					result = $(element).closest('li').closest('ul').closest('li').children('.parameter').data(type);
    				}
    			});
    			return result;
    		}
    		
    		function select(type, id) {
    			$('.dropdown-'+type+' .parameter').each(function(key, element){
    				if ($(element).data(type) == id) {
    					var newLink = $(element).clone().removeClass('parameter').addClass('selected');
						$(element).parents('.dropdown').find('.selected').replaceWith(newLink);
    				}
    			});
    		}
    	
    		function load(options) {
    			var current = {
    				period: $('.dropdown-period .selected').data('period'), 
    				target: $('.dropdown-target .selected').data('target'), 
    				organisation: $('.dropdown-organisation .selected').data('organisation'), 
    				level: levelControl.selectedLevel
    			}
    			drawMap($.extend({},current,options));
    		}
    	
			jQuery(document).ready(function() {
				initialize();
				load({period: $.url().fparam('period'), organisation: $.url().fparam('organisation'), level: $.url().fparam('level'), target: $.url().fparam('target')});
				
				$('.parameter').bind('click', function() {
					var options = {level: null};
					var type = $(this).data('type');
					options[type] = $(this).data(type);
					load(options);
					
					return false;
				});
			});
    	</script>
    </body>
</html>