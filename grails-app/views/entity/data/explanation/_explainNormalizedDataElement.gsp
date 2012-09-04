<%@page import="org.chai.kevin.util.Utils"%>

<ul class="tab-subnav horizontal">
	<li>
		<a class="js_tab-selector selected" href="#" data-type="reports" data-id="${dataElement.id}">Used in reports</a>
	</li>
	<!-- li>
		<a class="js_tab-selector" href="#" data-type="planning" data-id="${dataElement.id}">Used in planning</a>
	</li -->
	<li>
		<a class="js_tab-selector" href="#" data-type="data" data-id="${dataElement.id}">Used in data</a>
	</li>
</ul>

<div class="js_tab-${dataElement.id}" id="js_tab-reports-${dataElement.id}">
	<g:render template="/entity/data/explanation/referencingReportTargets" model="[referencingTargets: referencingTargets]"/>
</div>

<div class="js_tab-${dataElement.id} hidden" id="js_tab-data-${dataElement.id}">
	<g:render template="/entity/data/explanation/referencingData" model="[referencingData: referencingData]"/>
</div>