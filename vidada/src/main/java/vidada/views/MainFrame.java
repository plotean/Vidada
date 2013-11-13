package vidada.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import vidada.model.ServiceProvider;
import vidada.model.media.MediaItem;
import vidada.views.media.ExpandableMediaDetail;
import vidada.views.mediabrowsers.mediaBrowser.PrimaryMediaBrowserPanel;
import vidada.views.mediabrowsers.mediaFileBrowser.FileSystemBrowserPanel;
import archimedesJ.services.ISelectionService;


@SuppressWarnings("serial")
public class MainFrame extends JFrame{

	private final ISelectionService selectionService = ServiceProvider.Resolve(ISelectionService.class);


	private final ToolPane toolPane;


	public MainFrame(){

		this.setTitle("Vidada - Your Video Manager");


		//this.setIconImage(null /*Images.DB_ICON_32.getImage()*/);
		getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		panel.setBackground(Color.DARK_GRAY);

		toolPane = new ToolPane();
		toolPane.setFloatable(false);
		toolPane.setBackground(Color.DARK_GRAY);
		toolPane.setForeground(Color.DARK_GRAY);

		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(toolPane);


		getContentPane().add(panel, BorderLayout.NORTH);

		JPanel mediaBrowserPanel = new PrimaryMediaBrowserPanel();
		JPanel filesystemBrowser = new FileSystemBrowserPanel();

		JTabbedPane presentationTab = new JTabbedPane(JTabbedPane.TOP);

		presentationTab.addTab("Media Library", mediaBrowserPanel);
		presentationTab.addTab("Browser", filesystemBrowser);


		getContentPane().add(presentationTab, BorderLayout.CENTER);


		ExpandableMediaDetail mediaDetailPanel = new ExpandableMediaDetail(selectionService.getSelectionManager(MediaItem.class));
		mediaDetailPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		getContentPane().add(mediaDetailPanel, BorderLayout.SOUTH);
		//TODO DEBUG!!


		presentationTab.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ce) {

				JTabbedPane sourceTabbedPane = (JTabbedPane) ce.getSource();
				int index = sourceTabbedPane.getSelectedIndex();
				Component activeComponent = sourceTabbedPane.getComponentAt(index);

				if(activeComponent instanceof IContentPresenter)
				{
					((IContentPresenter)activeComponent).refreshContent();
				}
			}
		});


		this.setSize(800, 800);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);

	}

}
