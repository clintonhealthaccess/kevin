<table id="data-element-table">
	<thead>
		<tr>
		    <g:sortableColumn property="id" title="${message(code: 'dataelement.id.label', default: 'Id')}" />
			<th>Name</th>
			<th>Type</th>
			<g:sortableColumn property="code" title="${message(code: 'dataelement.code.label', default: 'Code')}" />
			<th>Manage</th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="dataElement"> 
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>${dataElement.id}</td> 
				<td>
					<a class="data-element-explainer" onclick="return false;"  href="${createLink(controller:'dataElement', action:'getExplainer', params:[dataElement: dataElement.id])}">
						<g:i18n field="${dataElement.names}" />
					</a>
				</td>
				<td><g:toHtml value="${dataElement.type.getDisplayedValue(2)}"/></td>
				<td>${dataElement.code}</td>
				<td>
					<div class="dropdown white-dropdown"> 
						<a class="selected" href="#" data-type="section">Manage</a>
						<div class="hidden dropdown-list">
							<ul>
								<li>
									<a href="${createLinkWithTargetURI(controller:'dataElement', action:'edit', params:[id: dataElement.id])}">
										<g:message code="general.text.edit" default="Edit" />
									</a>
								</li>
								<li>
									<a href="${createLinkWithTargetURI(controller:'dataElement', action:'delete', params:[id: dataElement.id])}" onclick="return confirm('\${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');">
										<g:message code="general.text.delete" default="Delete" />
									</a>
								</li>
							</ul>
						</div>
					</div> 		
				</td>
			</tr>
		</g:each>
	</tbody>
</table>
<script type="text/javascript">
	
	$(document).ready(function() {
		$('a.data-element-explainer').bind('click', function() {
			var clickedElement = $(this);
			var currentOpen = $('table#data-element-table tr.current-row');
			if (currentOpen.html() == null) {
				getAddRow(clickedElement);
				$('tr.current-row').slideDown('slow', function() {
					getDateElementExplainer(clickedElement);
				});

			} else {
				$('tr.current-row').slideUp('slow', function() {
					$('tr.current-row').remove();
					getAddRow(clickedElement);
					$('tr.current-row').slideDown('slow', function() {
						getDateElementExplainer(clickedElement);
					});
				});
			}

		})
	});
	
	function getAddRow(element) {
		$(element)
				.parents('tr')
				.after(
						'<tr class="current-row"><td colspan="6" class="box"><div class="white-box"></div></td></tr>');
	}
	
	function getDateElementExplainer(element) {
		var htmlData = "";
		$.ajax({
			type : 'GET',
			url : $(element).attr('href'),
			success : function(data) {
				if (data.result == 'success')
					$('tr.current-row .white-box').html(data.html);
			}
		});
		return false;
	}
</script>