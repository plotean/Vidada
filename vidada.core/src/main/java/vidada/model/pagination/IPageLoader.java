package vidada.model.pagination;

public interface IPageLoader<T> {

	ListPage<T> load(int page);

	/**
	 * Gets the total size of the request
	 * @return
	 */
	int getDataSize();

	/**
	 * Gets the max site of one page of this request
	 * @return
	 */
	int getMaxPageSize();

}
