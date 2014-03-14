package vidada.model.queries;

public abstract class Expression<T> {

	/**
	 * Generates source code
	 * @return
	 */
	public abstract String code();


	@Override
	public String toString(){
		return code();
	}
}
