<%@page import="org.chai.kevin.util.DataUtils"%>

<ul class="push-20 tab-navigation horizontal">
	<g:each in="${periods}" var="period" status="periodIndex">
		<li>
			<a href="${data==null?'#':createLink(controller:'data', action:'dataValueList', params:[data:data.id, period:period.id])}" class="listing-link ${!period.equals(selectedPeriod)?'':'selected'}">
				${DataUtils.formatDate(period.startDate)} to ${DataUtils.formatDate(period.endDate)}
			</a>
		</li>
	</g:each>
</ul>