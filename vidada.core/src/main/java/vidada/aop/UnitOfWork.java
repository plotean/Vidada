package vidada.aop;

import java.util.concurrent.atomic.AtomicInteger;

public class UnitOfWork<T> {

	private final AtomicInteger nestedLevel = new AtomicInteger();
	private final T context;

	public UnitOfWork(T context){
		this.context = context;
	}

	public void incrementNested(){
		nestedLevel.incrementAndGet();
	}

	public void decrementNested(){
		if(isNested()){
			nestedLevel.decrementAndGet();
		}else {
			throw new IllegalStateException("Can not decrement since this unit is already at root level.");
		}
	}

	public boolean isNested(){
		return nestedLevel.get() > 0;
	}

	public T getContext() {
		return context;
	}


}
