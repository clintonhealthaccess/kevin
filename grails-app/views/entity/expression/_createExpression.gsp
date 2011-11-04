<div id="add-expression" class="entity-form-container togglable">
	
	<div class="entity-form-header">
		<h3 class="title">Expression</h3>
		<g:locales/>
		<div class="clear"></div>
	</div>
	<div class="forms-container">
	<div class="data-field-column">
		<g:form url="[controller:'expression', action:'save', params: [targetURI: targetURI]]" useToken="true">
			<g:i18nInput name="names" bean="${expression}" value="${expression.names}" label="Name" field="names"/>
			<g:i18nTextarea name="descriptions" bean="${expression}" value="${expression.descriptions}" label="Description" field="descriptions"/>
			<g:input name="code" label="Code" bean="${expression}" field="code"/>
			
			<g:textarea name="type.jsonValue" label="Type" bean="${expression}" field="type"/>
			<g:textarea name="expression" label="Expression" bean="${expression}" field="expression" rows="5"/>
			
			<g:if test="${expression.id != null}">
				<input type="hidden" name="id" value="${expression.id}"></input>
			</g:if>
			
			<div class="row">
				<button type="submit"><g:message code="default.button.save.label" default="Save"/></button>
				<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label" default="Cancel"/></a>
			</div>
		</g:form>
	</div>
	<div class="data-search-column">
		<g:form name="search-data-form" class="search-form" url="[controller:'dataElement', action:'getData']">
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
	<div class="clear"></div>
	</div>
	<div class="clear"></div>
</div>
<script type="text/javascript">
$(document).ready(function() {
	getDataElement(function(event){
		if ($('.in-edition').size() == 1) {
			var edition = $('.in-edition')[0]
			$(edition).replaceSelection('$'+$(this).data('code'));
		}
	});
	$('#add-expression textarea')
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