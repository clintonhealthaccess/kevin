
class CircularDependencyConstraint {

	def expressionService
	
	def validate = { val, obj ->
		return !expressionService.hasCircularDependency(obj)
	}
	
}
