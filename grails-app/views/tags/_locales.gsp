<div class="locales">
	<g:each in="${locales}" var="locale" status="i">
		<a href="#" class="toggle-link ${i==0?'no-link':''}" data-toggle="${locale}">${locale}</a>
	</g:each>
</div>