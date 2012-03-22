<g:form name="report-filters" method="get" url="[controller:controllerName, action:actionName]">
	<div class="filter">
	
		<g:linkParamFilter linkParams="${linkParams}" exclude="${['locationTypes']}" />
			
		<span class="js_dropdown dropdown">
			<a class="datalocation selected" data-type="period" href="#">
				<g:message code="filter.locationtype.label"/>
			</a>
			<g:if test="${locationTypes != null && !locationTypes.isEmpty()}">
				<div class="hidden js_dropdown-list dropdown-list" id="js_location-type-filter">
					<ul>
						<li><a id="js_checkall" class="dropdown-check" href="#"><g:message code="filter.locationtype.checkall"/></a>
						<a id="js_uncheckall" class="dropdown-check" href="#" ><g:message code="filter.locationtype.uncheckall"/></a>
						<g:each in="${locationTypes}" var="type">
								<li class="check_filter">								
									<input class="locationType-checkbox" name="locationTypes" type="checkbox" value="${type.id}" 
									${currentLocationTypes != null && !currentLocationTypes.empty && currentLocationTypes.contains(type)?'checked="checked"':''}/>
									<label for="${type.id}"><g:i18n field="${type.names}"/></label>								
								</li>
							</g:each>	        
						<li><button id="locationType-submit" type="submit"><g:message code="filter.locationtype.filter"/></button></li>
					</ul>
				</div>
			</g:if>
		    <g:else>
				<span class="italic"><g:message code="filter.locationtype.empty.label"/></span>
			</g:else>		
	    </span>
	    <r:script>
	    $(document).ready(function() {
	    	$('#locationType-submit').bind('click', function() {
	    		var minLocationTypes = 0;
	    		$('.locationType-checkbox').each(function() { 
	    			if(this.checked)
	    				minLocationTypes++;
	    		});
	    		if(minLocationTypes < 1){
	    			alert('${message(code:'filter.locationtype.minimum')}');
	    			return false;
	    		}		    	
			});
	    });    
	    </r:script>
	</div>
</g:form>