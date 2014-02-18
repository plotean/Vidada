package vidada.client.viewmodel;

public interface IVMFactory<T,TVM> {
	/**
	 * Create a viewmodel for this model
	 * @param model
	 * @return
	 */
	TVM create(T model);
}