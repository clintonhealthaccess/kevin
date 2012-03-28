<%@page import="org.chai.kevin.util.Utils"%>

<ul class="push-20 tab-navigation horizontal">
	<g:each in="${periods}" var="period" status="periodIndex">
		<li>
			<a href="#" class="listing-link ${periodIndex!=0?'':'selected'}"
				onclick="$('.listing').hide(); $('#listing-${period.id}').show(); 
				$('.listing-link').removeClass('selected'); $(this).addClass('selected'); 
				return false;">
				${Utils.formatDate(period.startDate)} to ${Utils.formatDate(period.endDate)}
			</a>
		</li>
	</g:each>
</ul>