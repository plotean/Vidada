package vidada.views.mediabrowsers.decorators.overlays;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;

import vidada.views.mediabrowsers.decorators.JThumbOverlayDecorator;
import archimedesJ.events.EventArgsG;
import archimedesJ.events.EventListenerEx;
import archimedesJ.swing.components.imageviewer.ImageViewerContent;
import archimedesJ.swing.components.thumbexplorer.model.files.ThumbFolderNode;
import archimedesJ.swing.components.thumbpresenter.items.IBaseThumb;
import archimedesJ.util.Objects;

public class FolderImagePreviewOverlay extends AbstractThumbOverlay {

	private final ImageViewerContent overlayPanel;
	private FolderNodeImageProvider folderNodeImageProvider;
	private IBaseThumb lastClicked;

	public FolderImagePreviewOverlay() {
		overlayPanel = new ImageViewerContent();
		setUpOverlay();
	}

	@Override
	public void onOverlayRegistered(JThumbOverlayDecorator thumbOverlayDecorator) {
		super.onOverlayRegistered(thumbOverlayDecorator);

		selectionService.getItemClickedEvent().add(new EventListenerEx<EventArgsG<IBaseThumb>>() {
			@Override
			public void eventOccured(Object sender, EventArgsG<IBaseThumb> eventArgs) {

				IBaseThumb item = eventArgs.getValue();
				if (item != null && item.isSelected()) {
					if (Objects.equals(item, lastClicked)) {
						IBaseThumb thumbitem = selectionService.getFirstSelected();
						if (thumbitem instanceof ThumbFolderNode) {
							// System.out.println("FolderImagePreviewOverlay: FolderPreview has been started");
							startFolderPreview((ThumbFolderNode) thumbitem);
						}
					}
					lastClicked = item;
				}
			}
		});
	}

	private void setUpOverlay() {

		overlayPanel.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
				endPreview();
			}
		});

		overlayPanel.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e) {
				
				if(folderNodeImageProvider != null)
				{
					float xpos = e.getX();
					float width = e.getComponent().getSize().width;
					float relativeXPos = 1 / width * xpos;
	
					folderNodeImageProvider.setCurrentImageRelative(relativeXPos);
				}else
					System.err.println("overlayPanel.addMouseMotionListener: folderNodeImageProvider is NULL!");
			}

			@Override
			public void mouseDragged(MouseEvent arg0) {
				// ignore
			}
		});
	}

	private void startFolderPreview(ThumbFolderNode folderNode) {
		// System.out.println("startFolderPreview");

		overlayPanel.setVisible(true);

		if (folderNode != null) {
			updateThumbOverlayLocation();
		}

		folderNodeImageProvider = new FolderNodeImageProvider(folderNode, viewPortRenderer.getItemSize());
		overlayPanel.setDataContext(folderNodeImageProvider);

	}

	private void endPreview() {
		overlayPanel.setVisible(false);
	}

	@Override
	public JComponent getOverlay() {
		return overlayPanel;
	}

}
