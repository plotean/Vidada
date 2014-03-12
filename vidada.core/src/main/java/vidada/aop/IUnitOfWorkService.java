package vidada.aop;

import vidada.aop.UnitOfWorkService.IUnitOfWorkIntercepter;

/**
 * Manages a unit of work branch
 * @author IsNull
 *
 * @param <T>
 */
public interface IUnitOfWorkService<T> extends IUnitOfWorkRunner {

	/**
	 * Set the Unit of work intercepter.
	 * There can only be one intercepter, thus this method will override an existing one. 
	 * @param interceptor
	 */
	public abstract void setIntercepter(IUnitOfWorkIntercepter<T> interceptor);

	/**
	 * Returns the current Unit-Of-Work
	 * @return
	 */
	public T getCurrentUnitContext();
}