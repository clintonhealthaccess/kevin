<div class="filter">
	<div class="dropdown subnav-dropdown">
		<a class="selected" href="#" data-type="question">New Question</a>
		<div class="hidden dropdown-list">
			<ul>
				<li>
					<a href="${createLinkWithTargetURI(controller:'simpleQuestion', action:'create', params:[sectionId: section.id])}">
						New Simple Question
					</a>
				</li>
				<li>	
					<a href="${createLinkWithTargetURI(controller:'checkboxQuestion', action:'create', params:[sectionId: section.id])}">
						New Checkbox Question
					</a>
				</li>
				<li>
					<a href="${createLinkWithTargetURI(controller:'tableQuestion', action:'create', params:[sectionId: section.id])}">
						New Table Question
					</a>
				</li>
			</ul>
		</div>
	</div>
</div>