package vidada.views.mediabrowsers.mediaBrowser;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;

import vidada.model.ServiceProvider;
import vidada.model.media.IMediaService;
import vidada.model.media.MediaItem;
import vidada.model.media.images.ImageMediaItem;
import vidada.views.actions.RegenerateThumbnailAction;
import vidada.views.actions.RemoveMediaItemAction;
import vidada.views.actions.ShowInFolderManagerAction;
import vidada.views.actions.ShowMediaDetailsAction;
import vidada.views.mediabrowsers.MediaBrowserBasePanel;
import vidada.views.mediabrowsers.decorators.JThumbOverlayDecorator;
import vidada.views.mediabrowsers.decorators.overlays.DirectPlayOverlayComponent;
import vidada.views.mediabrowsers.imageviewer.ImageViewerDialog;
import vidada.views.mediabrowsers.mediaBrowser.thumbviewer.VidadaThumbViewPortRenderer;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventArgsG;
import archimedesJ.events.EventListenerEx;
import archimedesJ.exceptions.NotSupportedException;
import archimedesJ.services.ISelectionManager;
import archimedesJ.services.ISelectionService;
import archimedesJ.swing.components.thumbpresenter.JThumbViewPortRenderer;
import archimedesJ.swing.components.thumbpresenter.hotspots.HotSpotLoaderManager;
import archimedesJ.swing.components.thumbpresenter.items.IBaseThumb;
import archimedesJ.swing.components.thumbpresenter.model.ThumbListModel;
import archimedesJ.swing.components.thumbpresenter.renderer.IThumbSizeProvider;
import archimedesJ.util.FileSupport;


@SuppressWarnings("serial")
public class MediaBrowserPanel extends MediaBrowserBasePanel {

	JThumbOverlayDecorator overlayDecorator;
	private final JThumbViewPortRenderer  viewPortRenderer;
	private final ThumbListModel dataModel;

	private final ISelectionManager<MediaItem> mediaDataSelectionManager;
	private JPopupMenu childPopupMenu;
	private final IMediaService mediaService = ServiceProvider.Resolve(IMediaService.class);

	private final ISelectionService selectionService = ServiceProvider.Resolve(ISelectionService.class);



	@SuppressWarnings({ "unchecked", "rawtypes" })
	public MediaBrowserPanel() {


		if(selectionService != null) // hack so it works in the designer view ;)
			mediaDataSelectionManager = selectionService.getSelectionManager(MediaItem.class);
		else {
			mediaDataSelectionManager = null;
		}

		setLayout(new GridLayout(0, 1, 0, 0));



		dataModel = new ThumbListModel();

		viewPortRenderer = new VidadaThumbViewPortRenderer(dataModel);

		mediaService.getMediaDataChangedEvent().add(new EventListenerEx<EventArgsG<MediaItem>>() {
			@Override
			public void eventOccured(Object sender, EventArgsG<MediaItem> eventArgs) {
				viewPortRenderer.onItemChanged(eventArgs.getValue());
			}
		});


		// activate HotSpot loader 
		new HotSpotLoaderManager(viewPortRenderer, (IThumbSizeProvider)viewPortRenderer.getItemRenderer());

		overlayDecorator = new JThumbOverlayDecorator(viewPortRenderer);
		registerOverlays(overlayDecorator);
		JScrollPane mediaViewerScrollPane = new JScrollPane();	
		mediaViewerScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		mediaViewerScrollPane.setViewportView(overlayDecorator);
		mediaViewerScrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);

		add(mediaViewerScrollPane);

		final JFrame frame = (JFrame)getTopLevelAncestor();

		viewPortRenderer.setChildPopupMenu(createMenu());


		viewPortRenderer.getInteractionController().getComponentActionEvent().add(new EventListenerEx<EventArgsG<IBaseThumb>>() {
			@Override
			public void eventOccured(Object sender,
					EventArgsG<IBaseThumb> eventArgs) {
				MediaItem media = (MediaItem)eventArgs.getValue();

				if(media instanceof ImageMediaItem){

					JDialog viewerDialog = new ImageViewerDialog(viewPortRenderer.getInteractionController());
					viewerDialog.setVisible(true);

				}else{
					// default OS action
					if(!media.open()){
						JOptionPane.showMessageDialog(frame, "The file " + media.getTitle() + 
								" could not be opened!" + FileSupport.NEWLINE + media.getSource() ,
								"File not found!",JOptionPane.ERROR_MESSAGE );
					}
				}
			}
		});


		viewPortRenderer.getInteractionController().getSelectionChangedEvent().add(new EventListenerEx<EventArgs>() {
			@Override
			public void eventOccured(Object sender, EventArgs eventArgs) {
				List<IBaseThumb> items = viewPortRenderer.getInteractionController().getSelectedItems();

				List<MediaItem> mediaDatas = new ArrayList<MediaItem>();
				for (IBaseThumb iBaseThumb : items) {
					if(iBaseThumb instanceof MediaItem)
						mediaDatas.add((MediaItem)iBaseThumb);
				}

				mediaDataSelectionManager.trySelect(mediaDatas);
			}
		});
	}

	private void registerOverlays(JThumbOverlayDecorator overlayDecorator){
		try {
			System.out.println("registering DirectPlayOverlayComponent...");
			overlayDecorator.registerOverlay(new DirectPlayOverlayComponent());
		}catch(NotSupportedException e){
			System.err.println( e.getMessage());
		}

		//overlayDecorator.registerOverlay(new TestOverlay());
	}

	/**
	 * Creates the Popupmenu for the image browser items
	 * @return
	 */
	private JPopupMenu createMenu(){

		childPopupMenu = new JPopupMenu("Edit");

		// Regenerate Thumb Action 
		childPopupMenu.add(new JMenuItem(new RegenerateThumbnailAction(viewPortRenderer)));

		// Open in Explorer / Browser Action 
		childPopupMenu.add(new JMenuItem(new ShowInFolderManagerAction(viewPortRenderer)));

		// Show Details Action 
		childPopupMenu.add(new JMenuItem(new ShowMediaDetailsAction(viewPortRenderer)));

		// Remove Media Item Action 
		childPopupMenu.add(new JMenuItem(new RemoveMediaItemAction(viewPortRenderer)));

		return childPopupMenu;
	}


	@Override
	public JThumbViewPortRenderer getMediaViewer(){
		return viewPortRenderer;
	}
}