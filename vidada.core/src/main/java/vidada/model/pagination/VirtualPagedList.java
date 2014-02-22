package vidada.model.pagination;

import java.util.HashMap;
import java.util.Map;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import archimedesJ.data.events.CollectionEventArg;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.EventListenerEx;
import archimedesJ.events.IEvent;

/**
 * Represents a List-Datasource which supports asynchronous loading of data.
 * The data is loaded in page-chunks depending on the required items.
 * 
 * 
 * @author IsNull
 *
 * @param <T>
 */
public class VirtualPagedList<T> implements IDataProvider<IDeferLoaded<T>>{

	private final Object pageCacheLock = new Object();
	private final PageLoadTask[] pageCache;
	private final IPageLoader<T> pageLoader;

	private final int maxPageSize;
	private final int totalListSize;

	@Override
	public IEvent<CollectionEventArg<IDeferLoaded<T>>> getItemsChangedEvent() { return null; }


	/***************************************************************************
	 *                                                                         *
	 * Public API                                                              *
	 *                                                                         *
	 **************************************************************************/

	/**
	 * Creates a new VirtualPagedList
	 * @param pageLoader The pageloader used to deferred load required pages
	 */
	public VirtualPagedList(IPageLoader<T> pageLoader, ListPage<T> firstPage){
		this.pageLoader = pageLoader;
		maxPageSize = firstPage.getMaxPageSize();
		totalListSize = (int)firstPage.getTotalListSize();
		pageCache = new PageLoadTask[getPageCount()];
	}

	@Override
	public IDeferLoaded<T> get(int index) {
		DeferLoadedItem<T> item;

		int pageNumber = getPageNumberForIndex(index);
		PageLoadTask<T> pageLoadTask = getPageLoadTask(pageNumber);

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

		throw new NotImplementedException();
		// TODO Start loading of this page / move to top
	}

	@SuppressWarnings("unchecked")
	private PageLoadTask<T> getPageLoadTask(int page){
		synchronized (pageCacheLock) {
			PageLoadTask<T> task = pageCache[page];
			if(task == null){
				task = new PageLoadTask<T>(pageLoader, page);
				pageCache[page] = task;
			}
			return task;
		}
	}

	private int getPageNumberForIndex(int index){
		int pageBase = Math.floorDiv(index, maxPageSize);
		if((index % maxPageSize) > 0){
			pageBase++;
		}
		return pageBase;
	}

	private int getPageCount(){
		return (int)(totalListSize / maxPageSize);
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
				ListPage<T> page = pageLoader.getLoadedPage();
				loadedItem = page.getByRealIndex(index);
				loadedEvent.fireEvent(this, EventArgs.Empty);
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

		public boolean isLoaded() {
			return loadedPage != null;
		}

		@Override
		public void run() {
			synchronized (loadLock) {
				if(loadedPage == null){
					loadedPage = pageLoader.load(page);
				}
			}
			pageLoadedEvent.fireEvent(this, EventArgs.Empty);
		}

		public ListPage<T> getLoadedPage(){
			return loadedPage;
		}
	}

}
