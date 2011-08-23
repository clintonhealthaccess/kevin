<%@ page import="org.chai.kevin.data.ValueType" %>
<div id="add-data-element" class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">Create Data Element</h3>
		<g:locales/>
		<div class="clear"></div>
	</div>
	<g:form url="[controller:'dataElement', action:'save']" useToken="true">
	<g:i18nInput name="names" bean="${dataElement}" value="${dataElement.names}" label="Name" field="names" />
	<g:i18nRichTextarea name="descriptions" bean="${dataElement}" value="${dataElement.descriptions}" label="Descriptions" field="descriptions" height="150"  width="300" maxHeight="150" />
	<g:input name="code" label="Code" bean="${dataElement}" field="code"/>
	<div id="data-element-type">
	  <g:selectFromEnum name="type" bean="${dataElement}" values="${ValueType.values()}" field="type" label="Type"/>
	</div>
	<div id="enume-list-container">
	<div class="group-list ${hasErrors(bean:dataElement, field:'enume', 'errors')}">
		<label for="enume.id">Enume: </label>
		<select class="enume-list" name="enume.id">
			<option value="null">-- Select Enume --</option>
			<g:each in="${enumes}" var="enume">
				<option value="${enume.id}" ${(dataElement?.enume?.id!=null && dataElement?.enume?.id==enume?.id)?'selected="selected"':''}}>
					<g:i18n field="${enume.names}"/>
				</option>
			</g:each>
		</select>
		<div class="error-list"><g:renderErrors bean="${dataElement}" field="enume" /></div>
	</div>
	</div>
	
	<g:input name="info" label="Info" bean="${dataElement}" field="info"/>
		<g:if test="${dataElement.id != null}">
			<input type="hidden" name="id" value="${dataElement.id}"/>
		</g:if>
		<div class="row">
			<button type="submit" class="rich-textarea-form">Save Date Element</button>
			<button id="cancel-button">Cancel</button>
		</div>
    </g:form>
	<div class="clear"></div>
</div>
<script type="text/javascript">
	$(document).ready(function() {
		getRichTextContent();
		var enume = $('div#data-element-type select');
		if ($(enume).children('option:selected').text().trim() != "ENUM")
			$('#enume-list-container').hide()

		$(enume).change(function() {
			if ($(enume).children('option:selected').text().trim() == "ENUM")
				$('#enume-list-container').show('slow')
			else
				$('#enume-list-container').hide('slow')
		});
	});
</script>