<div class="entity-form-container togglable">
	<div class="entity-form-header">
		<h3 class="title">Create Survey</h3>
		<g:locales/>
		<div class="clear"></div>
	</div>
	<g:form url="[controller:'survey', action:'save', params:[targetURI: targetURI]]" useToken="true">
		<g:i18nInput name="names" bean="${survey}" value="${survey?.names}" label="Name" field="names"/>
		<g:i18nRichTextarea name="descriptions" bean="${survey}" value="${survey?.descriptions}" label="Descriptions" field="descriptions" height="100"  width="300" maxHeight="100" />
		
		<div class="row">
			<div id="iteration-block">
				<div class="${hasErrors(bean:survey, field:'period', 'errors')}">
					<label for="period.id">Period:</label>
					<select class="iteration-list" name="period.id">
						<option value="null">-- Select an Iteration --</option>
						<g:each in="${periods}" var="period">
							<option value="${period.id}" ${period.id==survey.period?.id?'selected="selected"':''}>
								${period.startDate} &harr; ${period.endDate}
							</option>
						</g:each>
					</select>
					<div class="error-list"><g:renderErrors bean="${survey}" field="period" /></div>
				</div>
			</div>
		</div>
		
		<div class="row">
			<div id="iteration-block">
				<div class="${hasErrors(bean:survey, field:'period', 'errors')}">
					<label for="lastPeriod.id">Last period (for reference to old values):</label>
					<select class="iteration-list" name="lastPeriod.id">
						<option value="null">-- Select an Iteration --</option>
						<g:each in="${periods}" var="period">
							<option value="${period.id}" ${period.id==survey.lastPeriod?.id?'selected="selected"':''}>
								${period.startDate} &harr; ${period.endDate}
							</option>
						</g:each>
					</select>
					<div class="error-list"><g:renderErrors bean="${survey}" field="lastPeriod" /></div>
				</div>
			</div>
		</div>
		
		<div class="row">
			<label>Active</label>
			<g:checkBox name="active" value="${survey.active}" />
		</div>
		
		<div class="clear"></div>
		<g:if test="${survey?.id != null}">
			<input type="hidden" name="id" value="${survey?.id}"></input>
		</g:if>
		<div class="row">
			<button type="submit" class="rich-textarea-form">Save Survey</button>&nbsp;&nbsp;
			<a href="${createLink(uri: targetURI)}">cancel</a>
		</div>
    </g:form>
	<div class="clear"></div>
</div>
<script type="text/javascript">
	$(document).ready(function() {
		getRichTextContent();
	})
</script>