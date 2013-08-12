package vidada.views.mediabrowsers.mediaBrowser.thumbviewer;

import java.awt.Graphics;

import vidada.views.mediabrowsers.mediaBrowser.thumbviewer.renderer.VidadaThumbRenderer;
import archimedesJ.swing.components.thumbpresenter.JThumbViewPortRenderer;
import archimedesJ.swing.components.thumbpresenter.items.IBaseThumb;
import archimedesJ.swing.components.thumbpresenter.model.ThumbListModel;
import archimedesJ.swing.components.thumbpresenter.renderer.IThumbItemRenderer;
import archimedesJ.swing.components.thumbpresenter.renderer.MediaItemThumbRenderer;
import archimedesJ.util.OSValidator;


/**
 * VidadaThumbViewPortRenderer
 * 
 * @author IsNull
 *
 * @param <T>
 */
@SuppressWarnings("serial")
public class VidadaThumbViewPortRenderer extends JThumbViewPortRenderer {

	/**
	 * Create a new ThumbPresenter backed by the given data model
	 * @param dataModel
	 */
	@SuppressWarnings("unchecked")
	public VidadaThumbViewPortRenderer(ThumbListModel  dataModel){
		this(dataModel, buildThumbItemRenderer(), 200);
	}

	/**
	 * 
	 * @param dataModel
	 * @param renderer
	 * @param itemSize
	 */
	protected VidadaThumbViewPortRenderer(ThumbListModel dataModel, IThumbItemRenderer<IBaseThumb> renderer, int itemSize) {
		super(dataModel, renderer, itemSize);
	}

	@SuppressWarnings("rawtypes")
	private static IThumbItemRenderer buildThumbItemRenderer(){
		MediaItemThumbRenderer renderer;

		boolean mdpiMode = OSValidator.isHDPI();;
		renderer = new VidadaThumbRenderer(mdpiMode);

		if(mdpiMode) System.out.println("using HDPI renderer");

		return renderer;
	}

	@Override
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
	}

}