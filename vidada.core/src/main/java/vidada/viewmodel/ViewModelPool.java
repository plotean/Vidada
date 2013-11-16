package vidada.viewmodel;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages view models for a given set of models.
 * The view models are cached
 * @author IsNull
 *
 * @param <T>
 * @param <TVM>
 */
public class ViewModelPool<T, TVM extends IViewModel<T>> {
	private final Map<T, TVM> viewmodelMap = new HashMap<T, TVM>();
	private final IVMFactory<T,TVM> factory;


	public ViewModelPool(IVMFactory<T,TVM> factory){
		this.factory = factory;
	}

	public boolean isEmpty(){return viewmodelMap.isEmpty();}

	/**
	 * Removes the given model / view-model from this pool
	 * @param model
	 * @return Returns the removed viewmodel or <code>null</code>
	 */
	public TVM remove(T model){
		return viewmodelMap.remove(model);
	}


	public void clear(){
		viewmodelMap.clear();
	}

	/**
	 * Re
	 * @return
	 */
	public Iterable<TVM> getViewModels() {
		return viewmodelMap.values();
	}

	/**
	 * Gets a viewmodel for the given model
	 * @param tag
	 * @return
	 */
	public TVM viewModel(T model) {
		TVM vm = viewmodelMap.get(model);
		if(vm == null){
			vm = factory.create(model);
			viewmodelMap.put(model, vm);
		}else{
			if(vm.getModel() != model){
				// references are not the same
				// update model
				vm.setModel(model);
			}
		}
		return vm;
	}

}
