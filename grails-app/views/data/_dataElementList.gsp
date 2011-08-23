<div class="data-element-list">
	<div class="float-left">
			<h5>Data Element List</h5>
		</div>
		<div class="float-right">
			<a id="add-data-element-link" class="flow-add" href="${createLink(controller:'dataElement', action:'create')}">
			New Data Element</a>
		</div>
		<div class="admin-table-list">
			<table id="data-element-table">
			 <g:if test="${!dataElements.isEmpty()}">
					<tr class="admin-table-header">
					    <g:sortableColumn property="id" title="${message(code: 'dataelement.id.label', default: 'Id')}" />
						<th>Name</th>
						<th>Type</th>
						<g:sortableColumn property="code" title="${message(code: 'dataelement.code.label', default: 'Code')}" />
						<th class="hidden">Manage</th>
					</tr>
					<g:each in="${dataElements}" status="i" var="dataElement"> 
						<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
						   <td>${dataElement.id}</td> 
						   <td>
							   <a class="data-element-explainer display-in-block" onclick="return false;"  href="${createLink(controller:'dataElement', action:'getExplainer', params:[dataElement: dataElement.id])}">
						          <g:i18n field="${dataElement.names}" />
							   </a>
						   </td>
							<td>${dataElement.type.getName()}</td>
							<td>${dataElement.code}</td>
						  <td class="hidden">
							<div class="dropdown"> 
							     <a class="selected" href="#" data-type="section">Manage</a>
								<div class="hidden dropdown-list">
									<ul>
										<li class="edit-data-element-link">
								        <g:link controller="dataElement" action="edit" id="${dataElement.id}" class="flow-edit">
											<g:message code="general.text.edit" default="Edit" />
										</g:link>
										</li>
										<li class="delete-data-element-link">
								      	<g:link controller="dataElement" action="delete" id="${dataElement.id}" class="flow-delete">
											<g:message code="general.text.delete" default="Delete" />
										</g:link>
										</li>
									</ul>
								</div>
							</div> 		
							</td>
						</tr>
					</g:each>
					</g:if>
					<g:else>
						<tr>
							<td colspan="5">No Data Element available 
							<a id="new-data-element-link" class="flow-add" href="${createLink(controller:'dataElement', action:'create')}">
							New Section</a>
							</td>
						</tr>
					</g:else>
			</table>
		</div>
		<div class="paginateButtons">
			<g:paginate total="${dataElementCount}" />
		</div>
	<div class="clear"></div>
</div>
<div class="hidden flow-container"></div>
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