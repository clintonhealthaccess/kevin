<div id="add-skip-rule" class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">
			<g:message code="default.new.label" args="[message(code:'skiprule.label',default:'Skip Rule')]"/>
		</h3>
		<g:locales />
		<div class="clear"></div>
	</div>
	<div class="forms-container">
		<div class="data-field-column">
		<g:form url="[controller:'planningSkipRule', action:'save', params:[targetURI:targetURI]]" useToken="true">
			<input type="hidden" name="planning.id" value="${skip.planning.id}" />
			
		 	<g:i18nTextarea name="descriptions" bean="${skip}" value="${skip?.descriptions}" label="Descriptions" field="descriptions" />
			<g:render template="/templates/skippedFormElements" model="[skip: skip]"/>
		 	<g:textarea name="expression" label="Expression" bean="${skip}" field="expression" value="${skip.expression}" rows="5"/>
		 
			<g:if test="${skip.id != null}">
				<input type="hidden" name="id" value="${skip.id}" />
			</g:if>
			<div class="row">
				<button type="submit" class="rich-textarea-form"><g:message code="default.button.save.label" default="Save"/></button>
				<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label" default="Cancel"/></a>
			</div>
		</g:form>
		</div>
		<div class="data-search-column">
			<g:form name="search-data-form" class="search-form" url="[controller:'formElement', action:'getHtmlData']">
				<div class="row">
					<label for="searchText"><g:message code="entity.search.label" default="Search"/>: </label>
			    	<input name="searchText" class="idle-field"/>
			    	<button type="submit"><g:message code="default.button.search.label" default="Search"/></button>
					<div class="clear"></div>
				</div>
			</g:form>
		    <ul class="filtered idle-field" id="data" ></ul>
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
		$('#add-skip-rule textarea')
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