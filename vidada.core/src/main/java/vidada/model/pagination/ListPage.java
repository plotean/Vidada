package vidada.model.pagination;

import java.util.List;

/**
 * Represents a single page of a paged list.
 * @author IsNull
 *
 * @param <T>
 */
public class ListPage<T> {

	private List<T> pageItems;
	private int page;

	private long totalListSize;
	private int maxPageSize;

	/**
	 * Creates a new ListPage
	 * @param pageItems
	 * @param listSize
	 * @param maxPageSize
	 * @param page
	 */
	public ListPage(List<T> pageItems, long totalListSize, int maxPageSize, int page) {
		super();
		this.pageItems = pageItems;
		this.totalListSize = totalListSize;
		this.maxPageSize = maxPageSize;
		this.page = page;
	}

	protected ListPage() { }

	/**
	 * Get all items of this page
	 * @return
	 */
	public List<T> getPageItems() {
		return pageItems;
	}

	/**
	 * Get the size of the full result set / list
	 * @return
	 */
	public long getTotalListSize() {
		return totalListSize;
	}

	/**
	 * Get the max item count per page
	 * @return
	 */
	public int getMaxPageSize() {
		return maxPageSize;
	}

	/**
	 * Get the page number (zero based index)
	 * @return
	 */
	public int getPage() {
		return page;
	}

	/**
	 * Gets the element at the real index position. 
	 * That means you can use the index u would have used to access the real list.
	 * @param index
	 * @return
	 */
	public T getByRealIndex(int index) {
		int pageStartIndex = getPage() * getMaxPageSize();
		int localIndex = index - pageStartIndex;
		if(localIndex < 0) throw new IllegalArgumentException("The given real index "+ index +" is too small for this page (" + getPage() + ").");
		if(localIndex >= getPageItems().size()) throw new IllegalArgumentException("The given real index "+ index +" is too high for this page (" + getPage() + ").");

		return getPageItems().get(localIndex);
	}


	protected void setPageItems(List<T> pageItems) {
		this.pageItems = pageItems;
	}
	protected void setTotalListSize(long listSize) {
		this.totalListSize = listSize;
	}
	protected void setPageSize(int pageSize) {
		this.maxPageSize = pageSize;
	}
	protected void setPage(int page) {
		this.page = page;
	}

	@Override
	public String toString(){
		return "[Items: " +  getPageItems().size() + ", Page: " + getPage() + ", Total Count: " + getTotalListSize() + ", MaxPageSize: " +maxPageSize+  "]";
	}

}