<div id="add-validation-rule" class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">Create a Validation Rule</h3>
		<g:locales />
		<div class="clear"></div>
	</div>
	<div class="forms-container"">
	<div class="data-field-column">
	<g:form url="[controller:'surveyValidationRule', action:'save']" useToken="true">
	 <input type="hidden" name="surveyElement.id" value="${validation.surveyElement.id}" />
	 <label class="display-in-block">Survey Element</label>
	 <input type="text" name="surveyElement.id" value="${i18n(field: validation.surveyElement.dataElement.names)}[${validation.surveyElement.id}]" class="idle-field" disabled />
	 
	 <div class="${hasErrors(bean:validation, field:'dependencies', 'errors')}">
	 <label class="display-in-block">Dependencies: </label>
     <select id="dependencies-list" name="dependencies" multiple="true" class="ajax-search-field">
		<g:if test="${validation.dependencies.size() != 0}">
			<g:each in="${validation.dependencies}" var="dependency">
				<option value="${dependency.id}" selected>
					<g:i18n field="${dependency.dataElement.names}" />
					[${dependency.id}]
				</option>
			</g:each>
		</g:if>
	</select>
	<div class="error-list"><g:renderErrors bean="${validation}" field="dependencies" /></div>
	</div>
	
	<label class="display-in-block">Allow Outlier</label>
	<g:checkBox name="allowOutlier" value="${validation.allowOutlier}" />
	<g:textarea name="expression" label="Expression" bean="${validation}" field="expression" rows="5"/>
	
	<div class="${hasErrors(bean:validation, field:'validationMessage', 'errors')}">
	<label class="display-in-block">Messages Test: </label>
	<select id="messages-list" name="validationMessage.id" class="ajax-search-field">
		<g:if test="${validation.validationMessage}">
				<option value="${validation.validationMessage.id}" selected>
					<g:i18n field="${validation.validationMessage.messages}" />
				</option>
		</g:if>
	</select>
	<div class="error-list"><g:renderErrors bean="${validation}" field="validationMessage" /></div>
	</div>
		<g:if test="${validation.id != null}">
			<input type="hidden" name="id" value="${validation.id}" />
		</g:if>
		<div class="row">
			<button type="submit" class="rich-textarea-form">Save Rule</button>
			&nbsp;&nbsp;
			<button id="cancel-button">Cancel</button>
		</div>
	</g:form>
	</div>
	<div class="data-search-column">
		<g:form name="search-data-form" class="search-form" url="[controller:'surveyElement', action:'getData']">
			<div class="row">
				<label for="searchText">Search: </label>
		    	<input name="searchText" class="idle-field"/>
		    	<input type="hidden" name="surveyId" value=""/>
		    	<button type="submit">Search</button>
				<div class="clear"></div>
			</div>
		</g:form>
	    <ul class="filtered idle-field" id="data" ></ul>
	</div>
	</div>
	<div class="clear"></div>
</div>
<div class="hidden flow-container"></div>
<script type="text/javascript">
	$(document).ready(function() {		
		$("#messages-list").ajaxChosen({
			type : 'GET',
			dataType: 'json',
			url : "${createLink(controller:'validationMessage', action:'getAjaxData')}"
		}, function (data) {
			var terms = {};
			$.each(data.messages, function (i, val) {
				terms[val.id] = val.message;
			});
			return terms;
		});
		
		$("#dependencies-list").ajaxChosen({
			type : 'GET',
			dataType: 'json',
			url : "${createLink(controller:'surveyElement', action:'getAjaxData')}"
		}, function (data) {
			var terms = {};
			$.each(data.elements, function (i, val) {
				terms[val.id] = val.surveyElement;
			});
			return terms;
		});
		
		getDataElement(function(event){
			if ($('.in-edition').size() == 1) {
				var edition = $('.in-edition')[0]
				$(edition).replaceSelection('['+$(this).data('code')+']');
			}
		});
		$('#add-validation-rule textarea')
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