package vidada.model.pagination;

import archimedes.core.data.events.CollectionEventArg;
import archimedes.core.events.EventArgs;
import archimedes.core.events.EventHandlerEx;
import archimedes.core.events.EventListenerEx;
import archimedes.core.events.IEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a List-Datasource which supports asynchronous loading of data in chunks (pages).
 *
 * 
 * @author IsNull
 *
 * @param <T>
 */
public class VirtualPagedList<T> implements IDataProvider<IDeferLoaded<T>>{

	private AsyncPriorityLoader asyncPriorityLoader = new AsyncPriorityLoader();


	private final Object pageCacheLock = new Object();
	private final PageLoadTask[] pageCache;
	private final IPageLoader<T> pageLoader;

	private final int maxPageSize;
	private final int totalListSize;

	// TODO fire events...
	private final EventHandlerEx<CollectionEventArg<IDeferLoaded<T>>> itemsChangedEvent = new EventHandlerEx<CollectionEventArg<IDeferLoaded<T>>>();
	@Override
	public IEvent<CollectionEventArg<IDeferLoaded<T>>> getItemsChangedEvent() { return itemsChangedEvent; }


	/***************************************************************************
	 *                                                                         *
	 * Public API                                                              *
	 *                                                                         *
	 **************************************************************************/

	/**
	 * Creates a new VirtualPagedList
	 * @param pageLoader The page-loader used to deferred load required pages
	 */
	public VirtualPagedList(IPageLoader<T> pageLoader, ListPage<T> firstPage){
		this.pageLoader = pageLoader;

		if(pageLoader == null)
			throw new IllegalArgumentException("The pageLoader must not be null");

		if(firstPage == null)
			throw new IllegalArgumentException("The firstPage must not be null");

		if(firstPage.getPage() != 0)
			throw new IllegalArgumentException("The firstPage must have the page index 0!");

		maxPageSize = firstPage.getMaxPageSize();
		totalListSize = (int)firstPage.getTotalListSize();
		pageCache = new PageLoadTask[getPageCount()];


		pageCache[0] = new PageLoadTask<T>(firstPage);

		System.out.println("VirtualPagedList");
	}

	@Override
	public IDeferLoaded<T> get(int index) {
		DeferLoadedItem<T> item;

		int pageIndex = getPageIndexForAbsoluteIndex(index);
		PageLoadTask<T> pageLoadTask = getPageLoadTask(pageIndex);

		item = getItemWrapper(index, pageLoadTask);

		loadHighPriority(pageLoadTask);

		return item;
	}

	@Override
	public int size() {
		return totalListSize;
	}

	@Override
	public boolean isEmpty() {
		return totalListSize == 0;
	}

	/***************************************************************************
	 *                                                                         *
	 * Private methods                                                         *
	 *                                                                         *
	 **************************************************************************/

	private Map<Integer, DeferLoadedItem<T>> deferItemCache = new HashMap<Integer, DeferLoadedItem<T>>();

	private DeferLoadedItem<T> getItemWrapper(int index, PageLoadTask<T> pageLoadTask){
		DeferLoadedItem<T> deferLoadedItem = deferItemCache.get(index);
		if(deferLoadedItem == null){
			deferLoadedItem = new DeferLoadedItem<T>(pageLoadTask, index);
			deferItemCache.put(index, deferLoadedItem);
		}

		return deferLoadedItem;
	}

	private void loadHighPriority(PageLoadTask<T> page){
		if(page.isLoaded()) return;
		asyncPriorityLoader.loadHighPriority(page);
	}

	@SuppressWarnings("unchecked")
	private PageLoadTask<T> getPageLoadTask(int pageIndex){
		synchronized (pageCacheLock) {
			PageLoadTask<T> task = pageCache[pageIndex];
			if(task == null){
				task = new PageLoadTask<T>(pageLoader, pageIndex);
				pageCache[pageIndex] = task;
			}
			return task;
		}
	}

	private int getPageIndexForAbsoluteIndex(int index){
		return (int)Math.floor((float)index / (float)maxPageSize);
	}

	private int getPageCount(){
		return (int)Math.floor((float)totalListSize / (float)maxPageSize) + 1;
	}

	/***************************************************************************
	 *                                                                         *
	 * Inner classes                                                           *
	 *                                                                         *
	 **************************************************************************/

	/**
	 * Wrapper around a single item which may not yet be loaded.
	 * @author IsNull
	 *
	 * @param <T>
	 */
	private static class DeferLoadedItem<T> implements IDeferLoaded<T> {

		private final EventHandlerEx<EventArgs> loadedEvent = new EventHandlerEx<EventArgs>();
		@Override
		public IEvent<EventArgs> getLoadedEvent() { return loadedEvent; }

		private final PageLoadTask<T> pageLoader;
		private final int index;

		private T loadedItem;

		/**
		 * 
		 * @param page
		 * @param index
		 */
		public DeferLoadedItem(final PageLoadTask<T> page, int index){
			this.pageLoader = page;
			this.index = index;

			page.getPageLoadedEvent().add(new EventListenerEx<EventArgs>() {
				@Override
				public void eventOccured(Object sender, EventArgs eventArgs) {
					tryLoad();
				}
			});

			tryLoad();
		}

		private void tryLoad(){
			if(pageLoader.isLoaded())
			{
				try{
					ListPage<T> page = pageLoader.getLoadedPage();
					loadedItem = page.getByRealIndex(index);
					loadedEvent.fireEvent(this, EventArgs.Empty);
				}catch(IllegalArgumentException e){
					e.printStackTrace();
				}
			}
		}

		@Override
		public T getLoadedItem(){
			return loadedItem;
		}

		@Override
		public boolean isLoaded(){
			return loadedItem != null;
		}
	}

	/**
	 * A task responsible of loading and holding a single page
	 * @author IsNull
	 *
	 * @param <T>
	 */
	private static class PageLoadTask<T> implements Runnable {

		private final Object loadLock = new Object();
		private final int page;
		private final IPageLoader<T> pageLoader;

		private volatile ListPage<T> loadedPage;

		private final EventHandlerEx<EventArgs> pageLoadedEvent = new EventHandlerEx<EventArgs>();
		/**
		 * Event fired when this page is completely loaded
		 * @return
		 */
		public IEvent<EventArgs> getPageLoadedEvent() { return pageLoadedEvent; }

		public PageLoadTask(IPageLoader<T> pageLoader, int page){
			this.page = page;
			this.pageLoader = pageLoader;
		}

		public PageLoadTask(ListPage<T> loadedPage){
			this.loadedPage = loadedPage;
			this.page = loadedPage.getPage();
			this.pageLoader = null;
		}

		public boolean isLoaded() {
			return loadedPage != null;
		}

		@Override
		public void run() {
			synchronized (loadLock) {
				if(loadedPage == null){
					if(pageLoader != null)
						loadedPage = pageLoader.load(page);
					else {
						System.err.println("VirtualPagedList::PageLoadTask:: pageLoader = NULL");
					}
				}
			}
			pageLoadedEvent.fireEvent(this, EventArgs.Empty);
		}

		public ListPage<T> getLoadedPage(){
			return loadedPage;
		}
	}

}
