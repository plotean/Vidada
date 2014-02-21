package vidada.aop;

import java.util.concurrent.Callable;

/**
 * Provides an AOP Unit-of-Work service, which runs given code in a unit-of-work.
 * @author IsNull
 *
 */
public interface IUnitOfWorkRunner {
	/**
	 * Run the given code in a Unit of work.
	 * 
	 * Nested calls are supported as long as the same Unit-Of-Work service is used.
	 * (Which should always be the case.)
	 * @param code
	 */
	void runUnit(Runnable code);

	/**
	 * Run the given code in a Unit of work and return the result.
	 * 
	 * @param code
	 * @return Generic return value
	 */
	<V> V runUnit(Callable<V> code);
}
