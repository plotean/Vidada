package vidada.views.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import vidada.model.compatibility.IHaveMediaData;
import vidada.model.media.MediaItem;
import vidada.views.ImageResources;
import vidada.views.dialoges.MediaDetailDialog;
import archimedesJ.swing.components.thumbpresenter.JThumbViewPortRenderer;
import archimedesJ.swing.components.thumbpresenter.items.IBaseThumb;

public class ShowMediaDetailsAction extends AbstractAction {

	private final JThumbViewPortRenderer viewPortRenderer;


	public ShowMediaDetailsAction(JThumbViewPortRenderer viewPortRenderer){
		super("Show Details", ImageResources.ADD_ICON_32);
		putValue(Action.SHORT_DESCRIPTION, "Opens a Detail- View for this media item.");

		this.viewPortRenderer = viewPortRenderer;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		IBaseThumb selectedThumb = viewPortRenderer.getInteractionController().getFirstSelected();

		if(selectedThumb instanceof IHaveMediaData)
		{
			MediaItem data = ((IHaveMediaData)selectedThumb).getMediaData();

			MediaDetailDialog mediaDetailDlg = new MediaDetailDialog(null, data);
			mediaDetailDlg.setVisible(true);
		}
	}

}
