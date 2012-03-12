<div class="filter">

	<g:render template="/templates/linkParamFilter" model="[linkParams:linkParams, filter:'locationTypes']" />
		
	<span class="js_dropdown dropdown">
		<a class='facility selected' data-period='3' data-type='period' href='#'>
			<g:message code="filter.facility.type.label"/>
		</a>
		<g:if test="${locationTypes != null && !locationTypes.isEmpty()}">
			<div class="hidden js_dropdown-list dropdown-list" id="js_facility_filter">
				<ul>
					<li><a id="js_checkall" class="dropdown-check" href="#">Check all</a>
					<a id="js_uncheckall" class="dropdown-check" href="#" >Uncheck all</a>
					<g:each in="${locationTypes}" var="type">
							<li class="check_filter">								
								<input class="locationType-checkbox" name="locationTypes" type="checkbox" value="${type.id}" 
								${currentLocationTypes != null && !currentLocationTypes.empty && currentLocationTypes.contains(type)?'checked="checked"':''}/>
								<label for="${type.id}"><g:i18n field="${type.names}"/></label>								
							</li>
						</g:each>	        
					<li><button id="locationType-submit" type="submit">Filter</button></li>
				</ul>
			</div>
		</g:if>
	    <g:else>
			<span class="italic"><g:message code="filter.facility.type.empty.label"/></span>
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
    			alert('A minimum of 1 facility type is required.');
    			return false;
    		}		    	
		});
    });    
    </r:script>
</div>