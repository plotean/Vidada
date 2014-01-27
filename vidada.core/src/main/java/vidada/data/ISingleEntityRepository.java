package vidada.data;


public interface ISingleEntityRepository<T> extends IRepository<T> {

	/**
	 * Get the single entity. The entity will be cached in memory after first call.
	 * @return
	 */
	public abstract T get();

	/**
	 * Update the entity
	 * @param item
	 */
	public abstract void update(T item);

}
