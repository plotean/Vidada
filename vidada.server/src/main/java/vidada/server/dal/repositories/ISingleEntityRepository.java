package vidada.server.dal.repositories;


/**
 * A repository which only allows a single instance
 * @author IsNull
 *
 * @param <T>
 */
public interface ISingleEntityRepository<T> extends IRepository {

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
