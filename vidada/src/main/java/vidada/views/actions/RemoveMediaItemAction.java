package vidada.views.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import vidada.model.ServiceProvider;
import vidada.model.compatibility.IHaveMediaData;
import vidada.model.media.IMediaService;
import vidada.model.media.MediaItem;
import vidada.views.ImageResources;
import archimedesJ.swing.components.thumbpresenter.JThumbViewPortRenderer;
import archimedesJ.swing.components.thumbpresenter.items.IBaseThumb;

/**
 * Remove Media Item Action 
 * 
 * @author IsNull
 *
 */
@SuppressWarnings("serial")
public class RemoveMediaItemAction extends AbstractAction{

	private final IMediaService mediaService = ServiceProvider.Resolve(IMediaService.class);

	private final JThumbViewPortRenderer viewPortRenderer;


	public RemoveMediaItemAction(JThumbViewPortRenderer viewPortRenderer){
		super("Remove", ImageResources.DELETE_ICON_32);
		putValue(Action.SHORT_DESCRIPTION, "Remove this Item from the Database");

		this.viewPortRenderer = viewPortRenderer;
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		IBaseThumb selectedThumb = viewPortRenderer.getInteractionController().getFirstSelected();
		if(selectedThumb instanceof IHaveMediaData){
			MediaItem data = ((IHaveMediaData)selectedThumb).getMediaData();	
			mediaService.removeMediaData(data);
		}
	}

}
