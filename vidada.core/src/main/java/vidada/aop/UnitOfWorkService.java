package vidada.aop;

import java.util.concurrent.Callable;


/**
 * Implements the Unit-Of-Work AOP pattern
 * 
 * @author IsNull
 *
 */
public class UnitOfWorkService<T> implements IUnitOfWorkService<T> {

	/***************************************************************************
	 *                                                                         *
	 * Private Fields                                                          *
	 *                                                                         *
	 **************************************************************************/

	private final ThreadLocal<UnitOfWork<T>> currentUnitOfWork = new ThreadLocal<UnitOfWork<T>>();

	private IUnitOfWorkIntercepter<T> interceptor;


	/***************************************************************************
	 *                                                                         *
	 * Public API                                                              *
	 *                                                                         *
	 **************************************************************************/

	/**{@inheritDoc} */
	@Override
	public void runUnit(Runnable code){

		startUnitOfWork();

		try{
			code.run();
		}catch(Exception e){
			e.printStackTrace();
			// TODO rollback??
		}finally{
			endUnitOfWork();
		}
	}

	/**{@inheritDoc} */
	@Override
	public <V> V runUnit(Callable<V> code) {

		V ret = null;

		startUnitOfWork();

		try{
			ret = code.call();
		}catch(Exception e){
			e.printStackTrace();
			// TODO rollback??
		}finally{
			endUnitOfWork();
		}

		return ret;
	}

	/**{@inheritDoc} */
	@Override
	public void setIntercepter(IUnitOfWorkIntercepter<T> interceptor){
		this.interceptor = interceptor;
	}

	/**{@inheritDoc} */
	@Override
	public T getCurrentContext() {
		UnitOfWork<T> unit = currentUnitOfWork.get();
		if(unit == null)
			throw new IllegalStateException("Illegal access of Unit context outside Unit-Of-Work.");
		return unit.getContext();
	}


	/***************************************************************************
	 *                                                                         *
	 * Private Methods                                                         *
	 *                                                                         *
	 **************************************************************************/

	private void startUnitOfWork() {
		UnitOfWork<T> unit = currentUnitOfWork.get();
		if(unit == null){
			unit = initUnitOfWork();
			currentUnitOfWork.set(unit);
		}else {
			// We have a nested unit of work
			// inform the unit that it is nested
			unit.incrementNested();
		}
	}

	private void endUnitOfWork() {
		UnitOfWork<T> unit = currentUnitOfWork.get();
		if(unit == null)
			throw new IllegalStateException("No unit of work is running, can not end it!");

		if(unit.isNested()){
			unit.decrementNested();
		}else {
			// this is the root level
			finalizeUnitOfWork(unit);
			currentUnitOfWork.remove();
		}
	}


	/**
	 * Initialize a new Unit.
	 * This method is called only ONCE for each unit at the start of its life cycle.
	 * @param newUnit
	 */
	private UnitOfWork<T> initUnitOfWork(){
		T context = interceptor.initUnitOfWork();
		return new UnitOfWork<T>(context);
	}

	/**
	 * Finalize / dispose the given Unit.
	 * This method is called only ONCE for each unit at the end of its life cycle.
	 * @param newUnit
	 */
	private void finalizeUnitOfWork(UnitOfWork<T> oldUnit){
		interceptor.finalizeUnitOfWork(oldUnit.getContext());
	}


	/***************************************************************************
	 *                                                                         *
	 * Inner classes / interfaces                                              *
	 *                                                                         *
	 **************************************************************************/

	public interface IUnitOfWorkIntercepter<T> {

		/**
		 * Init the context for this unit of work
		 * @return
		 */
		T initUnitOfWork();

		/**
		 * Finalize the context of this unit of work
		 * @param object
		 */
		void finalizeUnitOfWork(T object);

	}



}
