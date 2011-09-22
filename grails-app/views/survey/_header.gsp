<r:require module="dropdown"/>

<div id="survey-header" class="grey-rounded-box-top">
	<div>
		<div class="filter">
			<h5>Strategic Objective:</h5>
			<div class="dropdown">
				<a class="selected" href="#">
					<g:if test="${objective != null}">
						<g:i18n field="${objective.names}" />
					</g:if>
					<g:else>
						select an objective
					</g:else>
				</a>
				<div id="survey-menu" class="grey-rounded-box-bottom hidden dropdown-list">
					<g:render template="/survey/menu" model="[surveyPage: surveyPage]"/>
				</div>
			</div>
		</div>
		<div class="filter">
			<h5>Facility Name:</h5>
			<span>${organisation.name}</span>
		</div>
		<div class="filter">
			<h5>Year:</h5>
			<span> 
				<g:dateFormat format="yyyy" date="${period.startDate}" />
			</span>
		</div>
	</div>
	<div class="clear"></div>
</div>
