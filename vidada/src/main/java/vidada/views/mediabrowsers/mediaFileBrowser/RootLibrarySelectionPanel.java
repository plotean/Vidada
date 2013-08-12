package vidada.views.mediabrowsers.mediaFileBrowser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import vidada.model.ServiceProvider;
import vidada.model.libraries.IMediaLibraryService;
import vidada.model.libraries.MediaLibrary;
import archimedesJ.events.EventArgsG;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.EventListenerEx;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

@SuppressWarnings("serial")
public class RootLibrarySelectionPanel extends JPanel {


	public final EventHandlerEx<EventArgsG<MediaLibrary>> LibraryChangedEvent = new EventHandlerEx<EventArgsG<MediaLibrary>>();

	private final IMediaLibraryService mediaLibraryService =  ServiceProvider.Resolve(IMediaLibraryService.class);

	/**
	 * Create the panel.
	 */
	public RootLibrarySelectionPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
				new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));

		JLabel lblMediaLibrary = new JLabel("Media Library");
		add(lblMediaLibrary, "2, 2, right, default");

		final JComboBox cmbMediaLibrary = new JComboBox();
		add(cmbMediaLibrary, "4, 2, 2, 1, fill, default");

		JLabel label = new JLabel(" ");
		add(label, "2, 6");


		for (MediaLibrary lib : mediaLibraryService.getAllLibraries()) {
			cmbMediaLibrary.addItem(lib);
		}

		mediaLibraryService.getLibraryAddedEvent().add(new EventListenerEx<EventArgsG<MediaLibrary>>() {
			@Override
			public void eventOccured(Object sender, EventArgsG<MediaLibrary> eventArgs) {
				cmbMediaLibrary.addItem(eventArgs.getValue());
			}
		});

		mediaLibraryService.getLibraryRemovedEvent().add(new EventListenerEx<EventArgsG<MediaLibrary>>() {
			@Override
			public void eventOccured(Object sender, EventArgsG<MediaLibrary> eventArgs) {
				cmbMediaLibrary.removeItem(eventArgs.getValue());
			}
		});


		cmbMediaLibrary.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				LibraryChangedEvent.fireEvent(this, 
						EventArgsG.build((MediaLibrary)cmbMediaLibrary.getSelectedItem()));
			}
		});
	}

}
