package vidada.model.pagination;

/**
 * Abstraction of the logic to load pages
 * @param <T>
 */
public interface IPageLoader<T> {
	/**
	 * Load the page for the given pageIndex
	 * @param pageIndex
	 * @return
	 */
	ListPage<T> load(int pageIndex);
}
