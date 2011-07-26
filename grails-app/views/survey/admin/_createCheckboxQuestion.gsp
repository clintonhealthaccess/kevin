<div id="add-question" class="entity-form-container togglable">

	<div class="entity-form-header">
		<h3 class="title">Create a Checkbox Question</h3>
		<g:locales />
		<div class="clear"></div>
	</div>
	<div id="add-question-col">
		<g:form url="[controller:'checkboxQuestion', action:'save']" useToken="true">
			<g:i18nRichTextarea name="names" bean="${question}" value="${question.names}" label="Question" field="names" height="250" width="400" maxHeight="250" />
			<g:input name="order" label="Order" bean="${question}" field="order"/>
		    <div id="data-element-block">
		       <table id="question-option">
			    <g:each in="${question.options}" status="i" var="option">
			     <tr class="question-option ${(i % 2) == 0 ? 'odd' : 'even'}"> 
			      <td id="question-option-${option.id}"><g:render template="/templates/checkboxOption" model="[option: option]" /></td>
			     </tr>
			    </g:each>
			   </table>
			    <g:if test="${question.id != null}">
					<div>
						<a id="add-option-link" class="flow-add float-right" href="${createLink(controller:'checkboxOption', action:'create',params:[questionId: question.id])}">Add Option</a>
					</div>
				</g:if>
			    <div class="clear"></div>
		   	</div>
			<div class="row">
				<div id="subobjective-block">
					<div class="group-list ${hasErrors(bean:question, field:'subObjective', 'errors')}">
						<label for="subObjective.id">Objective:</label>
						<select class="subobjective-list" name="subObjective.id">
							<option value="null">-- Select an Sub-Objective --</option>
							<g:each in="${subobjectives}" var="subobjective">
								<option value="${subobjective.id}" ${subobjective.id+''==fieldValue(bean: question, field: 'subObjective.id')+''?'selected="selected"':''}>
									<g:i18n field="${subobjective.names}"/>
								</option>
							</g:each>
						</select>
						<div class="error-list"><g:renderErrors bean="${question}" field="subObjective" /></div>
					</div>
				</div>
			</div>
			<div id="orgunitgroup-block">
				<div class="group-list ${hasErrors(bean:question, field:'groupUuidString', 'errors')}">
					<label for="groups" class="display-in-block">Organisation Unit Group:</label>
						<select class="group-list" name="groupUuids" multiple="multiple" size="5" >
							<g:each in="${groups}" var="group">
								<option value="${group.uuid}" ${groupUuids.contains(group.uuid)?'selected="selected"':''}>
						           ${group.name}
					            </option>
							</g:each>
						</select>
					<div class="error-list">
						<g:renderErrors bean="${question}" field="groupUuidString" />
					</div>
				</div>
			</div>
			<g:if test="${question.id != null}">
				<input type="hidden" name="id" value="${question.id}"></input>
			</g:if>
			<div class="row">
				<button type="submit" class="question-form">Save Question</button>
				&nbsp;&nbsp;
				<button id="cancel-button">Cancel</button>
			</div>
		</g:form>
	</div>
	<div class="clear"></div>
</div>

<div class="hidden flow-container"></div>

<script type="text/javascript">
	$(document).ready(function() {
		getRichTextContent();	
		$('#add-question').flow({
			addLinks : ['.flow-edit-option'],
			onSuccess : function(data) {
				if (data.result == 'success') {
					var checkboxOptionHtml = data.html;
					var checkboxOptionId =data.newEntity;
					var selector = '#question-option-'+checkboxOptionId.id;
					$(selector).replaceWith('<td id="question-option-'+checkboxOptionId.id+'">'+checkboxOptionHtml+'</td>');
				}
			}
		});
		
		$('#add-question').flow({
			addLinks : [ '#add-option-link' ],
			onSuccess : function(data) {
				if (data.result == 'success') {
					//var rr= $("#add-option-link").prev('table tr:last');
					var checkboxOptionHtml = data.html;
					var checkboxOptionId =data.newEntity;
					$('#question-option').append('<tr class="question-option"><td id="question-option-'+checkboxOptionId.id+'">'+checkboxOptionHtml+'</td></tr>');
				}
			}
				});

	})
</script>