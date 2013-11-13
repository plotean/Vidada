package vidada.views.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingUtilities;

import vidada.model.compatibility.IHaveMediaData;
import vidada.model.media.MediaItem;
import vidada.model.media.movies.MovieMediaItem;
import vidada.views.ImageResources;
import archimedesJ.swing.components.thumbpresenter.JThumbViewPortRenderer;
import archimedesJ.swing.components.thumbpresenter.items.IBaseThumb;

/**
 * Regenerate Thumb Action 
 * @author IsNull
 *
 */
@SuppressWarnings("serial")
public class RegenerateThumbnailAction extends AbstractAction {

	private final JThumbViewPortRenderer viewPortRenderer;

	public RegenerateThumbnailAction(JThumbViewPortRenderer viewPortRenderer){
		super("Regenerate Thumb", ImageResources.ADD_ICON_32);
		putValue(Action.SHORT_DESCRIPTION, "Regenerate this thumbnail from the source file");

		this.viewPortRenderer = viewPortRenderer;
	}


	@Override
	public void actionPerformed(ActionEvent evt) {

		IBaseThumb selectedThumb = viewPortRenderer.getInteractionController().getFirstSelected();

		if(selectedThumb instanceof IHaveMediaData){
			final MediaItem data = ((IHaveMediaData)selectedThumb).getMediaData();

			if(data instanceof MovieMediaItem)
			{
				final MovieMediaItem movieData = (MovieMediaItem)data;
				Thread thumbCreator = new Thread() {
					@Override
					public void run() {

						movieData.createNewRandomThumb();

						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								//viewPortRenderer.onItemChanged(data);  //TODO invoke changed data on mediaservice
							}
						});
					}
				};
				thumbCreator.start();
			}
		}
	}

}
