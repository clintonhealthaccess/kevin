import org.springframework.web.context.request.RequestContextHolder;


class IE6Filters {

	def filters = {
		allURIs(uri:'/upgrade', invert:true) {
			before = {
				if (actionName != null && controllerName != null) {
					
					def userAgent = RequestContextHolder.currentRequestAttributes().currentRequest.getHeader('user-agent')
					
					if (userAgent.contains('MSIE 6.0')) {
						redirect(uri:'/upgrade')
						return false
					}
					
				}
			}
		}
	}
	
}
