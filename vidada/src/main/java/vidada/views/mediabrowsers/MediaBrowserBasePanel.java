package vidada.views.mediabrowsers;

import javax.swing.JPanel;

import archimedesJ.swing.components.thumbpresenter.JThumbViewPortRenderer;

@SuppressWarnings("serial")
public abstract class MediaBrowserBasePanel extends JPanel {

	/**
	 * Get the ThumbPresenter
	 * @return
	 */
	public abstract JThumbViewPortRenderer getMediaViewer();

}
