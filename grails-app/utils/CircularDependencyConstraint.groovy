import org.chai.kevin.data.DataElement;
import org.hibernate.FlushMode;


class CircularDependencyConstraint {

	def expressionService
	
	def validate = { val, obj ->
		DataElement.withSession { session ->
			def flushMode = session.getFlushMode()
			session.setFlushMode(FlushMode.MANUAL);
			def result = !expressionService.hasCircularDependency(obj)
			session.setFlushMode(flushMode);
			return result
		}
	}
	
}
