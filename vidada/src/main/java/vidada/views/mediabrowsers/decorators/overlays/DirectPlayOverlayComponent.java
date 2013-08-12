package vidada.views.mediabrowsers.decorators.overlays;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import vidada.model.compatibility.IHaveMediaData;
import vidada.model.media.MediaItem;
import vidada.model.media.movies.MovieMediaItem;
import vidada.model.media.source.FileMediaSource;
import vidada.model.media.source.MediaSource;
import vidada.views.directplay.DirectPlayBaseComponent;
import vidada.views.directplay.IDecoupledRenderer;
import vidada.views.mediabrowsers.decorators.JThumbOverlayDecorator;
import vlcj.VlcjUtil;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventArgsG;
import archimedesJ.events.EventListenerEx;
import archimedesJ.exceptions.NotSupportedException;
import archimedesJ.swing.components.thumbpresenter.items.IBaseThumb;
import archimedesJ.swing.components.thumbpresenter.renderer.IThumbItemRenderer;
import archimedesJ.swing.components.thumbpresenter.renderer.MediaItemThumbRenderer;
import archimedesJ.swing.redirectionEvents.EventRedirectionSupport;
import archimedesJ.util.Objects;

/**
 * Overlay Component for thumbs which supports DirectPlay
 * 
 * @author IsNull
 * 
 */
public class DirectPlayOverlayComponent extends AbstractThumbOverlay {

	private MediaItemThumbRenderer itemRenderer;
	private DirectPlayBaseComponent directplayOverlay = null;
	private boolean directPlaySetup = false;

	private IBaseThumb lastClicked;
	static final Color colorStaticBkgrd = new Color(0x48, 0x48, 0x48, 0xBF);

	public DirectPlayOverlayComponent() {
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void onOverlayRegistered(JThumbOverlayDecorator thumbOverlayManager) {
		super.onOverlayRegistered(thumbOverlayManager);

		System.out.println("DirectPlayOverlayComponent: registering " + thumbOverlayManager);

		itemRenderer = (MediaItemThumbRenderer) (IThumbItemRenderer) viewPortRenderer.getItemRenderer();

		if (!directPlaySetup) {
			VlcjUtil.ensureVLCLib();
			directPlaySetup = true;
			if (VlcjUtil.isVlcjAvaiable()) {
				setupDirectPlay();
				canUseDirectPlay = true;
			} else {
				throw new NotSupportedException("Vlcj jni-lib could not be loaded. (VLC is either not installed or a missmatch of 32bit and 64bit assemblies is causing truble.) DirectPlayOverlayComponent");
			}
		}

		selectionService.getItemClickedEvent().add(new EventListenerEx<EventArgsG<IBaseThumb>>() {
			@Override
			public void eventOccured(Object sender, EventArgsG<IBaseThumb> eventArgs) {

				IBaseThumb item = eventArgs.getValue();
				if (item != null && item.isSelected()) {
					if (Objects.equals(item, lastClicked)) {
						IBaseThumb thumbitem = selectionService.getFirstSelected();
						if (thumbitem instanceof IHaveMediaData) {
							startDirectPlay(((IHaveMediaData) thumbitem).getMediaData());
						}
					}
					lastClicked = item;
				}
			}
		});

	}

	@Override
	public JComponent getOverlay() {
		return directplayOverlay;
	}

	private void setupDirectPlay() {

		System.out.println("setting up DirectPlay...");

		directplayOverlay = DirectPlayBaseComponent.build();

		directplayOverlay.setVideoOverlayRenderer(new IDecoupledRenderer() {
			@Override
			public void render(Graphics2D g) {
				IBaseThumb thumbitem = selectionService.getFirstSelected();

				if (thumbitem instanceof IHaveMediaData) {
					MediaItem media = ((IHaveMediaData) thumbitem).getMediaData();
					itemRenderer.drawVideoInformation(g, g.getClipBounds(), media, colorStaticBkgrd);
				}
			}
		});

		directplayOverlay.setVisible(false);

		EventRedirectionSupport.redirectAllMouseEvents(directplayOverlay, viewPortRenderer);
		EventRedirectionSupport.redirectKeyEvents(directplayOverlay, viewPortRenderer);

		viewPortRenderer.getThumbSettingsChangedEvent().add(new EventListenerEx<EventArgs>() {
			@Override
			public void eventOccured(Object sender, EventArgs eventArgs) {
				updateThumbOverlayLocation();
			}
		});

		directplayOverlay.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				endDirectPlay();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				endDirectPlay();
			}
		});

		@SuppressWarnings("serial")
		Action suggestThumbnailNowAction = new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (directplayOverlay != null && directplayOverlay.isVisible()) {
					final float currentPos = directplayOverlay.getPosition();

					IBaseThumb thumbitem = selectionService.getFirstSelected();
					if (thumbitem instanceof IHaveMediaData) {
						final MovieMediaItem currentPlayedMedia = (MovieMediaItem) ((IHaveMediaData) thumbitem).getMediaData();

						Thread thumbCreator = new Thread() {
							@Override
							public void run() {
								endDirectPlay();
								currentPlayedMedia.createNewCachedThumb(currentPos);

								SwingUtilities.invokeLater(new Runnable() {
									@Override
									public void run() {
										viewPortRenderer.onItemChanged(currentPlayedMedia); // TODO Refactor
										// invoke
										// changed
										// data
										// on
										// mediaservice
									}
								});
							}
						};
						thumbCreator.start();

					}
				}
			}
		};

		KeyStroke key = KeyStroke.getKeyStroke("A"); // TODO Define key-bindings on central location
		directplayOverlay.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, "suggestThumbnailNowAction"); 
		directplayOverlay.getActionMap().put("suggestThumbnailNowAction", suggestThumbnailNowAction);

	}

	private void endDirectPlay() {
		directplayOverlay.stop();
		directplayOverlay.setVisible(false);
	}



	/**
	 * startDirectPlay
	 * 
	 * Start playing
	 * 
	 * @param mediaData
	 */
	private synchronized void startDirectPlay(MediaItem mediaData) {
		if (directplayOverlay.getParent() == null)
			throw new NotSupportedException("startDirectPlay: " + getClass().getName() + " has no parent!");

		System.out.println("starting direct play...");

		if (!canUseDirectPlay()) {
			System.err.println("DirectPlay Feature not supported");
			return;
		}

		System.out.println("requested media to play: " + mediaData);

		endDirectPlay();

		if (mediaData != null) {
			updateThumbOverlayLocation();

			// set the overlay
			if (mediaData instanceof MovieMediaItem) {
				MovieMediaItem movie = (MovieMediaItem) mediaData;
				MediaSource source = movie.getSource();
				if(source instanceof FileMediaSource && source.isAvailable()) 
				{
					directplayOverlay.setVisible(true);

					// System.out.println(getAspectRatio());
					// directplayOverlay.setCropGeometry("16:10");
					directplayOverlay.playMedia(source.getPath());
					directplayOverlay.setCropGeometry(getAspectRatio());
					directplayOverlay.setPosition(0.2f);

					directplayOverlay.pause();
					directplayOverlay.pause();
					// directplayOverlay.setScale(0.01f);
				}
			}
			directplayOverlay.requestFocusInWindow();
		}
	}

	boolean canUseDirectPlay = false;

	public boolean canUseDirectPlay() {
		return canUseDirectPlay;
	}

	private String getAspectRatio() {
		double relation = viewPortRenderer.getItemSideRelation();

		int rleft = 100;
		int rright = (int) (rleft * relation);

		return rleft + ":" + rright;
	}

}
