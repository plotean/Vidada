package vidada.views.mediabrowsers.imageviewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;

import javax.swing.JDialog;

import archimedesJ.swing.components.imageviewer.IImageProvider;
import archimedesJ.swing.components.imageviewer.SmartImageViewer;
import archimedesJ.swing.components.thumbpresenter.JThumbInteractionController;

@SuppressWarnings("serial")
public class ImageViewerDialog extends JDialog {

	private SmartImageViewer smartImageViewer;

	public ImageViewerDialog(JThumbInteractionController thumbInteractionController){
		this(new VidadaImageProvider(thumbInteractionController));
	}

	public ImageViewerDialog(IImageProvider imageProvider){
		setBackground(Color.DARK_GRAY);

		this.setModalityType(ModalityType.APPLICATION_MODAL);

		smartImageViewer = new SmartImageViewer();
		smartImageViewer.setDataContext(imageProvider);
		smartImageViewer.setBackground(Color.DARK_GRAY);

		this.add(smartImageViewer);
		this.pack();

		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setMinimumSize(new Dimension(300, 300));
		this.setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());
	}

	@Override
	public void dispose(){
		smartImageViewer.dispose();
		super.dispose();
	}



}
