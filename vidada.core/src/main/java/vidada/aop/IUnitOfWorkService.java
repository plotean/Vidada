package vidada.aop;

import vidada.aop.UnitOfWorkService.IUnitOfWorkIntercepter;

public interface IUnitOfWorkService<T> extends IUnitOfWorkRunner {


	/**
	 * Set the Unit of work intercepter.
	 * There can only be one intercepter, thus this method will override an existing one. 
	 * @param interceptor
	 */
	public abstract void setIntercepter(IUnitOfWorkIntercepter<T> interceptor);

	/**
	 * Returns the currents Unit-Of-Work context
	 * @return
	 */
	public T getCurrentContext();
}