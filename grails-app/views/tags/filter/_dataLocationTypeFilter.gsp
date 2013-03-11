<div class="left">
	<%
		dataLocationTypeLinkParams = [:]
		dataLocationTypeLinkParams.putAll linkParams
		dataLocationTypeLinkParams.remove 'dataLocationTypes'
	%>
	<g:form name="report-filters" method="get" url="${[controller:controllerName, action:actionName, params: dataLocationTypeLinkParams]}">			
		<span class="js_dropdown dropdown">
			<a class="datalocation js_dropdown-link nice-button with-highlight" href="#">
				<g:if test="${!currentLocationTypes.empty}">
					<g:each in="${currentLocationTypes}" var="currentLocationType" status="i">					
						<g:if test="${i < currentLocationTypes.size() && i < 3}">
							<g:i18n field="${currentLocationType.names}"/><g:if test="${i < currentLocationTypes.size()-1 && i < 2}">, </g:if>
						</g:if>
					</g:each><g:if test="${currentLocationTypes.size() > 3}">...</g:if>
				</g:if>
				<g:else>
					<span class="italic"><g:message code="filter.datalocationtype.noselection.label"/></span>
				</g:else>
			</a>
			<g:if test="${!dataLocationTypes.empty}">
				<div class="js_dropdown-list dropdown-list push-top-10" id="js_location-type-filter">
					<ul>
						<li>
							<a id="js_checkall" class="nice-button2" href="#"><g:message code="filter.datalocationtype.checkall"/></a>
							<a id="js_uncheckall" class="nice-button2" href="#" ><g:message code="filter.datalocationtype.uncheckall"/></a>
							<g:each in="${dataLocationTypes}" var="type">
								<li>								
									<input class="js_data-location-type-checkbox js_dropdown-ignore" name="dataLocationTypes" type="checkbox" value="${type.id}" 
									${currentLocationTypes != null && !currentLocationTypes.empty && currentLocationTypes.contains(type)?'checked="checked"':''}/>
									<label for="${type.id}"><g:i18n field="${type.names}"/></label>								
								</li>
							</g:each>
						<li>
						<button id="js_data-location-type-submit" type="submit"><g:message code="filter.datalocationtype.filter"/></button></li>
					</ul>
				</div>
			</g:if>
		    <g:else>
				<span class="italic"><g:message code="filter.datalocationtype.no.locationtypes"/></span>
			</g:else>	
	    </span>
	    <r:script>
	    $(document).ready(function() {
	    	$('#js_data-location-type-submit').bind('click', function() {
	    		var minLocationTypes = 0;
	    		$('.js_data-location-type-checkbox').each(function() { 
	    			if(this.checked)
	    				minLocationTypes++;
	    		});
	    		if(minLocationTypes < 1){
	    			alert('${message(code:'filter.datalocationtype.minimum')}');
	    			return false;
	    		}		    	
			});
	    });    
	    </r:script>
	</g:form>
</div>
