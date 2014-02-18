package vidada.client.viewmodel;

public interface IViewModel<T> {
	T getModel();
	void setModel(T model);
}
