package vidada.model.pagination;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import archimedesJ.data.events.CollectionEventArg;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.EventListenerEx;
import archimedesJ.events.IEvent;

public class VirtualPagedList<T> implements IDataProvider<IDeferLoaded<T>>{

	private final Object pageCacheLock = new Object();
	private final List<PageLoadTask<T>> pageCache;

	private final IPageLoader<T> pageLoader;

	@Override
	public IEvent<CollectionEventArg<IDeferLoaded<T>>> getItemsChangedEvent() { return null; }


	/***************************************************************************
	 *                                                                         *
	 * Public API                                                              *
	 *                                                                         *
	 **************************************************************************/


	public VirtualPagedList(IPageLoader<T> pageLoader){
		this.pageLoader = pageLoader;
		pageCache = new ArrayList<PageLoadTask<T>>(getPageCount());
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
		return pageLoader.getDataSize();
	}

	@Override
	public boolean isEmpty() {
		return pageLoader.getDataSize() == 0;
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

		// TODO Start loading / move to tip
	}

	private PageLoadTask<T> getPageLoadTask(int page){
		synchronized (pageCacheLock) {
			PageLoadTask<T> task = pageCache.get(page);
			if(task == null){
				task = new PageLoadTask<T>(pageLoader, page);
				pageCache.set(page, task);
			}
			return task;
		}
	}

	private int getPageNumberForIndex(int index){
		int pageBase = Math.floorDiv(index, pageLoader.getMaxPageSize());
		if((index % pageLoader.getMaxPageSize()) > 0){
			pageBase++;
		}
		return pageBase;
	}

	private int getPageCount(){
		return pageLoader.getDataSize() / pageLoader.getMaxPageSize();
	}

	/***************************************************************************
	 *                                                                         *
	 * Inner classes                                                           *
	 *                                                                         *
	 **************************************************************************/

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
