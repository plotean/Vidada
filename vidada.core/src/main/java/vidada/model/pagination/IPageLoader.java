package vidada.model.pagination;

public interface IPageLoader<T> {
	/**
	 * Load the page for the given pageIndex
	 * @param pageIndex
	 * @return
	 */
	ListPage<T> load(int pageIndex);
}
