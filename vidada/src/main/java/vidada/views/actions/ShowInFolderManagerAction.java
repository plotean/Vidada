package vidada.views.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import vidada.model.ServiceProvider;
import vidada.model.compatibility.IHaveMediaData;
import vidada.model.media.MediaItem;
import vidada.model.media.source.MediaSource;
import vidada.model.system.ISystemService;
import vidada.views.ImageResources;
import archimedesJ.swing.components.thumbpresenter.JThumbViewPortRenderer;
import archimedesJ.swing.components.thumbpresenter.items.IBaseThumb;
import archimedesJ.util.OSValidator;


/**
 * Show in Explorer/Finder / Browser Action 
 * @author IsNull
 *
 */
@SuppressWarnings("serial")
public class ShowInFolderManagerAction extends AbstractAction{

	private static String mngrOSName = (OSValidator.isWindows() ? "Explorer" : OSValidator.isOSX() ? "Finder" : "Browser");

	private final JThumbViewPortRenderer viewPortRenderer;


	public ShowInFolderManagerAction(JThumbViewPortRenderer viewPortRenderer){
		super("Show in " +  mngrOSName, ImageResources.ADD_ICON_32);
		putValue(Action.SHORT_DESCRIPTION, "Open a " + mngrOSName + " to show this file.");

		this.viewPortRenderer = viewPortRenderer;
	}

	@Override
	public void actionPerformed(ActionEvent evt) {

		IBaseThumb selectedThumb = viewPortRenderer.getInteractionController().getFirstSelected();

		if(selectedThumb instanceof IHaveMediaData)
		{
			MediaItem data = ((IHaveMediaData)selectedThumb).getMediaData();
			if(data != null){
				MediaSource source = data.getSource();
				ISystemService systemService = ServiceProvider.Resolve(ISystemService.class);
				systemService.showResourceHome(source.getResourceLocation());
			}
			else
				System.err.println("can not retrive media data for thumb " + selectedThumb);
		}
	}

}
