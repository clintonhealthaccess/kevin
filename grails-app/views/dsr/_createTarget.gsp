<div id="add-dsr-target" class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">D.S.Rs Target</h3>
		<g:locales/>
		<div class="clear"></div>
	</div>
	<g:form url="[controller:'dsrTarget', action:'save']" useToken="true">
	    <g:if test="${target != null}">
		<input type="hidden" name="id" value="${target.id}"/>
		</g:if>
		<g:i18nInput name="names" bean="${target}" value="${target.names}" label="Name" field="names"/>
		<g:i18nTextarea name="descriptions" bean="${target}" value="${target.descriptions}" label="Description" field="descriptions"/>
		<g:input name="code" label="Code" bean="${target}" field="code"/>
		<g:input name="format" label="Format" bean="${target}" field="format"/>
	    <div class="row">
			<div class="clear"></div>
			<div id="orgunitgroup-block">
					<div class="group-list ${hasErrors(bean:target, field:'groupUuidString', 'errors')}">
						<label for="groups">Organisation Unit Group:</label>
						<select class="group-list" name="groupUuids" multiple="multiple" size="5" >
							<g:each in="${groups}" var="group">
								<option value="${group.uuid}" ${groupUuids.contains(group.uuid)?'selected="selected"':''}>
						           ${group.name}
					            </option>
							</g:each>
						</select>
						<div class="error-list"><g:renderErrors bean="${target}" field="groupUuidString" /></div>
					</div>
			</div>
		</div>
		<div class="row">
			<div>
				<a id="new-dsr-objective-link" class="float-right"  href="${createLink(controller:'dsrObjective', action:'create')}">New DSR Objective</a>
			</div>
			<div class="clear"></div>
			<div id="objective-block">
					<div class="group-list ${hasErrors(bean:target, field:'objective', 'errors')}">
						<label for="objective">Objective:</label>
						<select class="objective-list" name="objective.id">
							<option value="null">-- Select an Objective --</option>
							<g:each in="${objectives}" var="objective">
								<option value="${objective.id}" ${objective.id==target.objective?.id?'selected="selected"':''}>
									<g:i18n field="${objective.names}"/>
								</option>
							</g:each>
						</select>
						<div class="error-list"><g:renderErrors bean="${target}" field="objective" /></div>
					</div>
			</div>
		</div>
		<div class="row">
			<div>
				<a id="new-dsr-category-link" class="float-right"  href="${createLink(controller:'dsrTargetCategory', action:'create')}">New DSR Category</a>
			</div>
			<div class="clear"></div>
			<div id="categories-block">
					<div class="group-list ${hasErrors(bean:target, field:'category', 'errors')}">
						<label for="category.id">Category:</label>
						<select class="category-list" name="category.id">
							<option value="null">-- Select a Category --</option>
							<g:each in="${categories}" var="category">
								<option value="${category.id}" ${category.id==target.category?.id?'selected="selected"':''}>
									<g:i18n field="${category.names}"/>
								</option>
							</g:each>
						</select>
						<div class="error-list"><g:renderErrors bean="${target}" field="category" /></div>
					</div>
			</div>
		</div>
		<div class="row">
			<div>
				<a id="new-dsr-expression-link" class="float-right"  href="${createLink(controller:'expression', action:'create')}">New Expression</a>
			</div>
			<div class="clear"></div>
			<div id="expressions-block">
					<div class="group-list ${hasErrors(bean:target, field:'expression', 'errors')}">
						<label for="expression.id">Expression:</label>
						<select class="expression-list" name="expression.id">
							<option value="null">-- Select an Expression --</option>
							<g:each in="${expressions}" var="expression">
								<option value="${expression.id}"  ${expression.id==target.expression?.id?'selected="selected"':''}>
									<g:i18n field="${expression.names}"/>
								</option>
							</g:each>
						</select>
						<div class="error-list"><g:renderErrors bean="${target}" field="expression" /></div>
					</div>
			</div>
		</div>
		<g:input name="order" label="Order" bean="${target}" field="order"/>
		<div class="row">
			<button type="submit">Save Target</button>&nbsp;&nbsp;&nbsp;
			<button id="cancel-button">Cancel</button>
		</div>
    </g:form>
	<div class="clear"></div>
</div>
<div class="hidden flow-container"></div>

<script type="text/javascript">
	$(document).ready(function() {
		
		$('#add-dsr-target').flow({
			addLinks: '#new-dsr-objective-link',
			onSuccess: function(data) {
				if (data.result == 'success') {
					var dsrObjective = data.newEntity;
					$('.objective-list').append('<option value="'+dsrObjective.id+'">'+dsrObjective.names[data.locale]+'</option>');
					$.sexyCombo.changeOptions('.objective-list');
				}
			}
		});
		
			
		$('#add-dsr-target').flow({
			addLinks: '#new-dsr-expression-link',
			onSuccess: function(data) {
				if (data.result == 'success') {
					var expression = data.newEntity
					$('.expression-list').append('<option value="'+expression.id+'">'+expression.names[data.language]+'</option>');
					$.sexyCombo.changeOptions('.expression-list');
				}
			}
		});
		

		
		$('#add-dsr-target').flow({
			addLinks: '#new-dsr-category-link',
			onSuccess: function(data) {
				if (data.result == 'success') {
					var dsrCategory = data.newEntity;
					$('.category-list').append('<option value="'+dsrCategory.id+'">'+dsrCategory.names[data.locale]+'</option>');
				}
			}
		});
					
	});
</script>

