package vidada.views.mediabrowsers.mediaFileBrowser;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JPanel;

import vidada.model.ServiceProvider;
import vidada.model.libraries.IMediaLibraryService;
import vidada.model.libraries.MediaLibrary;
import vidada.views.IContentPresenter;
import vidada.views.mediabrowsers.MediaBrowserContainer;
import archimedesJ.events.EventArgsG;
import archimedesJ.events.EventListenerEx;
import archimedesJ.io.locations.DirectoiryLocation;
import archimedesJ.swing.components.thumbexplorer.IBaseTreeItem;
import archimedesJ.swing.components.thumbexplorer.JThumbExplorerRenderer;
import archimedesJ.swing.components.thumbexplorer.NavigationDecoratorPanel;
import archimedesJ.swing.components.thumbexplorer.model.IThumbNodeFactory;
import archimedesJ.swing.components.thumbexplorer.model.locations.RootLocationTreeNode;

@SuppressWarnings("serial")
public class FileSystemBrowserPanel extends JPanel implements IContentPresenter{

	private final IMediaLibraryService mediaLibraryService =  ServiceProvider.Resolve(IMediaLibraryService.class);

	private MediaExplorerPanel mediaExplorer;


	public FileSystemBrowserPanel(){

		this.setLayout(new BorderLayout(0, 0));

		RootLibrarySelectionPanel rootSelectionPanel = new RootLibrarySelectionPanel();

		mediaExplorer = new MediaExplorerPanel();

		JPanel browserWithNavigation = new NavigationDecoratorPanel(
				new MediaBrowserContainer(mediaExplorer),
				(JThumbExplorerRenderer)mediaExplorer.getMediaViewer());

		this.add(rootSelectionPanel, BorderLayout.NORTH);
		this.add(browserWithNavigation, BorderLayout.CENTER);


		List<MediaLibrary> allLibs = mediaLibraryService.getAllLibraries();
		if(!allLibs.isEmpty()){
			setLibrary(mediaLibraryService.getAllLibraries().get(0));
		}


		// register events

		rootSelectionPanel.LibraryChangedEvent.add(new EventListenerEx<EventArgsG<MediaLibrary>>() {

			@Override
			public void eventOccured(Object sender, EventArgsG<MediaLibrary> eventArgs) {
				setLibrary(eventArgs.getValue());
			}
		});
	}

	private final IThumbNodeFactory thumbFactory = new VidadaThumbNodeFactory();

	private void setLibrary(MediaLibrary lib){
		IBaseTreeItem root = null;
		if(lib != null)
		{
			DirectoiryLocation libPath = lib.getLibraryRoot();
			if(libPath != null)
				root = new RootLocationTreeNode(libPath, thumbFactory);
			else {
				System.err.println("FileSystemBrowserPanel.setLibrary: Library root is null!");
			}
		}
		((JThumbExplorerRenderer)mediaExplorer.getMediaViewer()).setDataContext(root);
	}


	@Override
	public void refreshContent() {
		IBaseTreeItem root =  (IBaseTreeItem)mediaExplorer.getMediaViewer().getDataContext();
		((JThumbExplorerRenderer)mediaExplorer.getMediaViewer()).setDataContext(root);
	}


}
