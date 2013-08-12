package vidada.views.mediabrowsers.mediaFileBrowser;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import vidada.model.ServiceProvider;
import vidada.model.compatibility.IHaveMediaData;
import vidada.model.media.MediaItem;
import vidada.model.media.images.ImageMediaItem;
import vidada.views.ImageResources;
import vidada.views.actions.RegenerateThumbnailAction;
import vidada.views.actions.RemoveMediaItemAction;
import vidada.views.actions.ShowInFolderManagerAction;
import vidada.views.mediabrowsers.MediaBrowserBasePanel;
import vidada.views.mediabrowsers.decorators.JThumbOverlayDecorator;
import vidada.views.mediabrowsers.decorators.overlays.DirectPlayOverlayComponent;
import vidada.views.mediabrowsers.decorators.overlays.FolderImagePreviewOverlay;
import vidada.views.mediabrowsers.imageviewer.ImageViewerDialog;
import vidada.views.mediabrowsers.mediaBrowser.thumbviewer.renderer.VidadaThumbRenderer;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventArgsG;
import archimedesJ.events.EventListenerEx;
import archimedesJ.exceptions.NotSupportedException;
import archimedesJ.services.ISelectionManager;
import archimedesJ.services.ISelectionService;
import archimedesJ.swing.components.thumbexplorer.JThumbExplorerRenderer;
import archimedesJ.swing.components.thumbexplorer.model.files.ThumbFolderNode;
import archimedesJ.swing.components.thumbexplorer.renderer.ExplorerItemRenderer;
import archimedesJ.swing.components.thumbexplorer.renderer.FolderRenderer;
import archimedesJ.swing.components.thumbpresenter.JThumbViewPortRenderer;
import archimedesJ.swing.components.thumbpresenter.hotspots.HotSpotLoaderManager;
import archimedesJ.swing.components.thumbpresenter.items.IBaseThumb;
import archimedesJ.swing.components.thumbpresenter.renderer.IThumbItemRenderer;
import archimedesJ.swing.components.thumbpresenter.renderer.IThumbSizeProvider;
import archimedesJ.util.FileSupport;
import archimedesJ.util.OSValidator;

/**
 * Basic File Explorer Panel
 * 
 * @author IsNull
 * 
 */
@SuppressWarnings("serial")
public class MediaExplorerPanel extends MediaBrowserBasePanel {

	private final JThumbExplorerRenderer viewPortRenderer;

	private final ISelectionManager<MediaItem> mediaDataSelectionManager;
	private final ISelectionService selectionService = ServiceProvider.Resolve(ISelectionService.class);

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public MediaExplorerPanel() {

		if (selectionService != null) // hack so it works in the designer view
										// ;)
			mediaDataSelectionManager = selectionService.getSelectionManager(MediaItem.class);
		else {
			mediaDataSelectionManager = null;
		}

		setLayout(new GridLayout(0, 1, 0, 0));

		JScrollPane mediaViewerScrollPane = new JScrollPane();

		boolean hdip = OSValidator.isHDPI();
		;

		ExplorerItemRenderer explorerRenderer = new ExplorerItemRenderer(new VidadaThumbRenderer(hdip), new FolderRenderer((ImageIcon) ImageResources.FOLDER_ICON_DARK, hdip), hdip);

		viewPortRenderer = new JThumbExplorerRenderer((IThumbItemRenderer) explorerRenderer, 200);

		// activate hotspot loader
		new HotSpotLoaderManager(viewPortRenderer, (IThumbSizeProvider) viewPortRenderer.getItemRenderer());

		viewPortRenderer.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

		JThumbOverlayDecorator overlayDecorator = new JThumbOverlayDecorator(viewPortRenderer);
		registerOverlays(overlayDecorator);

		mediaViewerScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		mediaViewerScrollPane.setViewportView(overlayDecorator);

		add(mediaViewerScrollPane);

		final JFrame frame = (JFrame) getTopLevelAncestor();

		viewPortRenderer.registerChildPopupMenu(createMediaMenu(), VidadaFileTreeNodeWrapper.class);
		viewPortRenderer.registerChildPopupMenu(createFolderMenu(), ThumbFolderNode.class);

		viewPortRenderer.getInteractionController().getComponentActionEvent().add(new EventListenerEx<EventArgsG<IBaseThumb>>() {
			@Override
			public void eventOccured(Object sender, EventArgsG<IBaseThumb> eventArgs) {
				if (eventArgs.getValue() instanceof IHaveMediaData) {
					MediaItem media = ((IHaveMediaData) eventArgs.getValue()).getMediaData();

					if (media instanceof ImageMediaItem) {

						JDialog viewerDialog = new ImageViewerDialog(viewPortRenderer.getInteractionController());
						viewerDialog.setVisible(true);

					} else {
						// default OS action
						if (!media.open()) {
							JOptionPane.showMessageDialog(frame, "The file " + media.getTitle() + " could not be opened!" + FileSupport.NEWLINE + media.getSource(), "File not found!", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}
		});

		viewPortRenderer.getInteractionController().getSelectionChangedEvent().add(new EventListenerEx<EventArgs>() {
			@Override
			public void eventOccured(Object sender, EventArgs eventArgs) {
				List<IBaseThumb> items = viewPortRenderer.getInteractionController().getSelectedItems();

				System.out.println("selection: " + items.size());

				List<MediaItem> mediaDatas = new ArrayList<MediaItem>();

				for (IBaseThumb iBaseThumb : items) {
					if (iBaseThumb instanceof IHaveMediaData) {
						mediaDatas.add(((IHaveMediaData) iBaseThumb).getMediaData());
					}
				}

				mediaDataSelectionManager.trySelect(mediaDatas);
			}
		});
	}

	private void registerOverlays(JThumbOverlayDecorator overlayManager) {
		
		try {
			overlayManager.registerOverlay(new DirectPlayOverlayComponent());
		}catch(NotSupportedException e){
			System.err.println( e.getMessage());
		}
		overlayManager.registerOverlay(new FolderImagePreviewOverlay());
	}

	private JPopupMenu createFolderMenu() {

		JPopupMenu childPopupMenu = new JPopupMenu("Folder");

		childPopupMenu.add(new JMenuItem("Folder action"));

		return childPopupMenu;
	}

	/**
	 * Creates the Popupmenu for the image browser items
	 * 
	 * @return
	 */
	private JPopupMenu createMediaMenu() {

		JPopupMenu childPopupMenu = new JPopupMenu("Media Item");

		// Regenerate Thumb Action
		childPopupMenu.add(new JMenuItem(new RegenerateThumbnailAction(viewPortRenderer)));

		// Open in Explorer / Browser Action
		childPopupMenu.add(new JMenuItem(new ShowInFolderManagerAction(viewPortRenderer)));

		// Remove Media Item Action
		childPopupMenu.add(new JMenuItem(new RemoveMediaItemAction(viewPortRenderer)));

		return childPopupMenu;
	}

	@Override
	public JThumbViewPortRenderer getMediaViewer() {
		return viewPortRenderer;
	}

}