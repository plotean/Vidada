package vidada.viewsFX.util;

public interface IDataContext<T> {
	void setDataContext(T context);
	T getDataContext();
}
