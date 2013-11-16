package vidada.viewmodel;

public interface IViewModel<T> {
	T getModel();
	void setModel(T model);
}
