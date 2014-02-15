package vidada.repositories.db4o;

import java.util.List;

import vidada.data.db4o.SessionManagerDB4O;
import vidada.repositories.ISingleEntityRepository;

import com.db4o.ObjectContainer;
import com.db4o.query.Query;

public abstract class SingleEntityRepositoryDb4o<T> implements ISingleEntityRepository<T> {

	private final Class<T> type;

	transient private T cachedInstance = null;

	public SingleEntityRepositoryDb4o(Class<T> type){
		this.type = type;
	}


	@Override
	public T get() {

		if(cachedInstance == null){
			T item = null;
			ObjectContainer db = SessionManagerDB4O.getObjectContainer();

			Query query = db.query();
			query.constrain(type);
			List<T> resultSet = query.execute();

			if(resultSet.isEmpty())
			{
				System.out.println("defalt settings created.");
				item = createDefault();
				db.store(item);
				db.commit();
			}else{
				item = resultSet.get(0);
			}
			cachedInstance = item;
		}

		return cachedInstance;
	}

	@Override
	public void update(T item) {
		ObjectContainer db = SessionManagerDB4O.getObjectContainer();
		db.store(item);
		db.commit();
	}

	/**
	 * Create the default instance of this single entity
	 * @return
	 */
	protected abstract T createDefault();

}
