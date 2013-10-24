package vidada.views.mediabrowsers.mediaBrowser;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

import vidada.mock.MediaDataCloner;
import vidada.model.ServiceProvider;
import vidada.model.media.IMediaService;
import vidada.model.media.MediaItem;
import vidada.views.IContentPresenter;
import vidada.views.mediabrowsers.MediaBrowserContainer;
import vidada.views.mediabrowsers.mediaBrowser.filter.AsyncFetchData;
import vidada.views.mediabrowsers.mediaBrowser.filter.AsyncFetchData.CancelTokenEventArgs;
import vidada.views.mediabrowsers.mediaBrowser.filter.IFilterProvider;
import vidada.views.mediabrowsers.mediaBrowser.filter.MediaFilterExpandabel;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventListenerEx;
import archimedesJ.swing.components.thumbpresenter.JThumbViewPortRenderer;
import archimedesJ.swing.components.thumbpresenter.model.ThumbListModel;
import archimedesJ.threading.CancellationTokenSource;

import com.db4o.query.Query;

/**
 * 
 * @author IsNull
 *
 */
@SuppressWarnings("serial")
public class PrimaryMediaBrowserPanel extends JPanel implements IContentPresenter{

	private final IMediaService mediaService = ServiceProvider.Resolve(IMediaService.class);

	private final MediaBrowserPanel browser = new MediaBrowserPanel();
	private final MediaFilterExpandabel filterPanel = new MediaFilterExpandabel();

	private final ThumbListModel browserModel;
	private final IFilterProvider filterProvider;


	private AsyncFetchData<MediaItem> datafetcher;
	private Object datafetcherLock = new Object();


	public PrimaryMediaBrowserPanel(){

		this.setLayout(new BorderLayout(0, 0));

		this.add(filterPanel, BorderLayout.NORTH);

		MediaBrowserContainer browserContainer = new MediaBrowserContainer(browser);
		this.add(browserContainer, BorderLayout.CENTER);

		browserModel = (ThumbListModel)browser.getMediaViewer().getDataContext(); // java generics suck! - nuff said.

		filterProvider = filterPanel;


		filterProvider.getFilterChangedEvent().add(new EventListenerEx<EventArgs>() {
			@Override
			public void eventOccured(Object sender, EventArgs eventArgs) {
				updateBrowserModel();
			}
		});

		wireUpMouseHandler();

		init();
	}

	private void wireUpMouseHandler()
	{
		Toolkit.getDefaultToolkit().addAWTEventListener(
				new TargetedMouseHandler(browser.getMediaViewer()), 
				AWTEvent.MOUSE_MOTION_EVENT_MASK);
	}

	public class TargetedMouseHandler implements AWTEventListener
	{
		@SuppressWarnings("unused")
		private final JComponent parent;

		public TargetedMouseHandler(JComponent p)
		{
			parent = p;
		}

		@Override
		public void eventDispatched(AWTEvent e)
		{
			if (e instanceof MouseEvent)
			{
				if (!filterPanel.isCollapsed() && 
						(e.getSource() instanceof JThumbViewPortRenderer)) {

					MouseEvent m = (MouseEvent) e;
					if (m.getID() == MouseEvent.MOUSE_MOVED)
					{
						filterPanel.setCollapsed(true);
					}
				}
			}
		}
	}




	/**
	 * Init the primary browser panel, hook up requred events
	 */
	private void init(){

		if(browserModel != null && mediaService != null)
		{
			mediaService.getMediaDatasChangedEvent().add(new EventListenerEx<EventArgs>() {

				@Override
				public void eventOccured(Object sender, EventArgs eventArgs) {

					updateBrowserModel();

				}
			});

			updateBrowserModel();
		}
	}

	CancellationTokenSource ctx;


	/**
	 * Updates the view with the current model data
	 */
	private void updateBrowserModel(){

		System.out.println("PrimaryMediaBrowserPanel: updateBrowserModel");

		browserModel.removeAllElements();

		Query query = filterProvider.getCriteria();

		synchronized (datafetcherLock) {
			if(datafetcher != null && !datafetcher.isDone() && ctx != null && !ctx.isCancellationRequested())
			{
				ctx.cancel();
			}

			if(datafetcher != null)
			{
				datafetcher.getFetchingCompleteEvent().remove(dataFetchedListener);
			}

			ctx = new CancellationTokenSource();

			datafetcher = new AsyncFetchData<MediaItem>(query, ctx.getToken());
			datafetcher.setPostFilter(filterPanel.getPostFilter());
			datafetcher.getFetchingCompleteEvent().add(dataFetchedListener);
			datafetcher.execute();
		}

	}



	private boolean sampleItemPerformance = false;

	transient private final EventListenerEx<CancelTokenEventArgs<List<MediaItem>>> dataFetchedListener = new EventListenerEx<CancelTokenEventArgs<List<MediaItem>>>(){
		//
		// As this event is fired by an async running thread, this method will therefore be async too.
		//
		@Override
		public void eventOccured(Object sender,final CancelTokenEventArgs<List<MediaItem>> eventArgs) {

			List<MediaItem> medias = eventArgs.getValue();

			filterPanel.setCurrentResultSet(medias);

			// TODO: Refactor this debug hack
			// for debug purposes make much more items
			if(sampleItemPerformance)
				medias = MediaDataCloner.crowdRandom(medias, 30);

			browserModel.setData(medias);
		}
	};


	@Override
	public void refreshContent() {
		updateBrowserModel();
	}



}
