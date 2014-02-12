package vidada.viewsFX.bindings;

import java.util.Collection;

import archimedesJ.data.observable.binding.IBindingContext;

class BindingListenerFX<T> implements javafx.collections.ListChangeListener<T>{

	private final Collection<T> syncTarget;
	private final IBindingContext context;

	public BindingListenerFX(IBindingContext context, Collection<T> syncTarget){
		this.syncTarget = syncTarget;
		this.context = context;
	}

	@Override
	public void onChanged(
			javafx.collections.ListChangeListener.Change<? extends T> eventArgs) {

		if(!context.inTransaction()){
			context.startTransaction();
			boolean next = eventArgs.next();

			if(next){
				if(eventArgs.wasPermutated() || eventArgs.wasReplaced()){
					// Completely refresh list
					syncTarget.clear();
					syncTarget.addAll(eventArgs.getList());
				}else{
					if(eventArgs.wasAdded()){
						syncTarget.addAll(eventArgs.getAddedSubList());
					}
					if(eventArgs.wasRemoved()){
						syncTarget.removeAll(eventArgs.getRemoved());
					}
				}
			}

			context.endTransaction();
		}
	}

}
