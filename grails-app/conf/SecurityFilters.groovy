import org.apache.shiro.SecurityUtils;

/**
 * Generated by the Shiro plugin. This filters class protects all URLs
 * via access control by convention.
 */
class SecurityFilters {
    def filters = {
		all(uri: "/**") {
			before = {
				log.debug("filtering params: "+params+", controller: "+controllerName+", action: "+actionName)

				// Ignore direct views (e.g. the default main index page).
				if (!controllerName) return true
				// Ignore refresh and export
				if (controllerName == 'editSurvey' && actionName == 'export') return true;
				if (controllerName == 'refresh') return true;
				
				// Ignore home controller, except when the user is logged in
				if (controllerName == 'home' && (actionName != 'index' || SecurityUtils.subject.principal != null)) return true
				
				// deny access to survey not corresponding to user
				// leave access to /survey/view open
				if (controllerName == 'editSurvey') {
					if (SecurityUtils.subject.isPermitted("editSurvey:"+actionName+":"+params.location)) return true;
				}
				
				accessControl()
			}
		}
    }
}
