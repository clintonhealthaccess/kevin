<%@page import="org.chai.kevin.util.Utils"%>

<div id="add-normalized-data-element" class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">Expression</h3>
		<g:locales/>
		<div class="clear"></div>
	</div>
	<div class="forms-container">
		<div class="data-field-column">
			<g:form url="[controller:'normalizedDataElement', action:'save', params: [targetURI: targetURI]]" useToken="true">
				<g:i18nInput name="names" bean="${normalizedDataElement}" value="${normalizedDataElement.names}" label="Name" field="names"/>
				<g:i18nTextarea name="descriptions" bean="${normalizedDataElement}" value="${normalizedDataElement.descriptions}" label="Description" field="descriptions"/>
				<g:input name="code" label="Code" bean="${normalizedDataElement}" field="code"/>
				
				<g:textarea name="type.jsonValue" label="Type" bean="${normalizedDataElement}" field="type" value="${normalizedDataElement.type.jsonValue}"/>
				
				<div class="row ${hasErrors(bean:normalizedDataElement, field:'expressionMap', 'errors')}">
					<label>Expression map:</label>
					<div>
						<g:each in="${periods}" var="period" status="i">
							<div>
								<a class="${i==0?'no-link':''} expression-period-link" href="#" 
									onclick="$('.expression-period').hide();$('#expression-period-${period.id}').show();$('.expression-period-link').removeClass('no-link');$(this).addClass('no-link');return false;">
									${Utils.formatDate(period.startDate)} to ${Utils.formatDate(period.endDate)}
								</a>
							</div>
						</g:each>
					</div>
					<g:each in="${periods}" var="period" status="i">
						<div class="expression-period ${i!=0?'hidden':''}" id="expression-period-${period.id}">
							<g:each in="${groups}" var="group">
								<label for="expressionMap[${period.id}][${group.code}]">${group.code}</label> 
								<textarea name="expressionMap[${period.id}][${group.code}]" rows="4">${normalizedDataElement.getExpression(period, group.code)}</textarea>
							</g:each>
						</div>
					</g:each>
					<div class="error-list"><g:renderErrors bean="${normalizedDataElement}" field="expressionMap" /></div>
				</div>
				
				<g:if test="${normalizedDataElement.id != null}">
					<input type="hidden" name="id" value="${normalizedDataElement.id}"></input>
				</g:if>
				
				<div class="row">
					<button type="submit"><g:message code="default.button.save.label" default="Save"/></button>
					<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label" default="Cancel"/></a>
				</div>
			</g:form>
		</div>
		<div class="data-search-column">
			<g:form name="search-data-form" class="search-form" url="[controller:'data', action:'getData', params:[class:'RawDataElement']]">
				<div class="row">
					<label for="searchText">Search: </label>
			    	<input name="searchText" class="idle-field"></input>
			    </div>
				<div class="row">
					<button type="submit">Search</button>
					<div class="clear"></div>
				</div>
			</g:form>
			
		    <ul class="filtered idle-field" id="data"></ul>
		</div>
	</div>
</div>
<script type="text/javascript">
	$(document).ready(function() {
		getDataElement(function(event){
			if ($('.in-edition').size() == 1) {
				var edition = $('.in-edition')[0]
				$(edition).replaceSelection('$'+$(this).data('code'));
			}
		});
		$('#add-normalized-data-element textarea')
		.bind('click keypress focus',
			function(){
				$(this).addClass('in-edition');
			}
		)
		.bind('blur',
			function(){
				$(this).removeClass('in-edition');
			}
		);
	});
</script>