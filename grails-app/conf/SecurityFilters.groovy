import org.apache.shiro.SecurityUtils;

/**
 * Generated by the Shiro plugin. This filters class protects all URLs
 * via access control by convention.
 */
class SecurityFilters {
    def filters = {
		all(uri: "/**") {
			before = {
				log.debug("filtering params: "+params)
				
				// Ignore direct views (e.g. the default main index page).
				if (!controllerName) return true

				// deny access to survey not corresponding to user
				// leave access to /survey/view open
				if (controllerName == 'editSurvey') {
					if (SecurityUtils.subject.isPermitted("editSurvey:"+actionName+":"+params.organisation)) return true;
				}
				
				accessControl()
			}
		}
    }
}