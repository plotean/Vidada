package vidada.views.dialoges;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URI;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import vidada.commands.AddNewMediaLibraryAction;
import vidada.commands.UpdateMediaLibraryAction;
import vidada.model.ServiceProvider;
import vidada.model.libraries.IMediaLibraryService;
import vidada.model.libraries.MediaLibrary;
import vidada.views.ImageResources;
import archimedesJ.events.EventArgsG;
import archimedesJ.events.EventListenerEx;
import archimedesJ.expressions.Predicate;
import archimedesJ.io.locations.DirectoryLocation;
import archimedesJ.swing.ImageListCellRenderer;
import archimedesJ.swing.JImageButton;
import archimedesJ.swing.components.FileChooserPanel;

@SuppressWarnings("serial")
public class ManageLibraryFoldersDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JList listAllLibraries;
	private FileChooserPanel txtCurrentPath;

	private IMediaLibraryService libService = ServiceProvider.Resolve(IMediaLibraryService.class);

	private DefaultListModel allLibrariesModel = new DefaultListModel();

	private Action removeLibraryAction;
	private Action addLibraryAction;
	private Action saveCurrentLibAction;


	private void init(){

		List<MediaLibrary> allibraries = libService.getAllLibraries();
		for (MediaLibrary mediaLibrary : allibraries) {
			allLibrariesModel.addElement(mediaLibrary);
		}

		//
		// Remove selected Library 
		//
		removeLibraryAction = new AbstractAction("", ImageResources.DELETE_ICON_32) {

			@Override
			public void actionPerformed(ActionEvent evt) {

				libService.removeLibrary((MediaLibrary)listAllLibraries.getSelectedValue());
			}
		};
		removeLibraryAction.putValue(Action.SHORT_DESCRIPTION, "Remove the selected library ");

		//
		// Add a new MediaLibrary 
		//
		addLibraryAction =  new AddNewMediaLibraryAction(ManageLibraryFoldersDialog.this);


		saveCurrentLibAction = new AbstractAction("save") { //Images.ADD_ICON_16
			@Override
			public void actionPerformed(ActionEvent evt) {
				MediaLibrary library = (MediaLibrary)listAllLibraries.getSelectedValue();
				if(library != null)
				{
					URI location = txtCurrentPath.getFile().toURI();
					library.setLibraryRoot(DirectoryLocation.Factory.create(location, null));
				}
			}
		};


		//
		// Register event listeners
		//

		libService.getLibraryAddedEvent().add(new EventListenerEx<EventArgsG<MediaLibrary>>() {
			@Override
			public void eventOccured(Object sender, EventArgsG<MediaLibrary> eventArgs) {
				allLibrariesModel.addElement(eventArgs.getValue());
				librariesHasChanged = true;
			}
		});

		libService.getLibraryRemovedEvent().add(new EventListenerEx<EventArgsG<MediaLibrary>>() {
			@Override
			public void eventOccured(Object sender, EventArgsG<MediaLibrary> eventArgs) {
				allLibrariesModel.removeElement(eventArgs.getValue());
				librariesHasChanged = true;
			}
		});

	}

	private void postInit(){

		listAllLibraries.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {

				MediaLibrary library = (MediaLibrary)listAllLibraries.getSelectedValue();
				if(library != null){
					DirectoryLocation location = library.getMediaDirectory().getDirectory();
					txtCurrentPath.setFilePath(location.toString());
				}

				updateState();
			}
		});

	}

	private void updateState(){
		removeLibraryAction.setEnabled(listAllLibraries.getSelectedValue() != null);
	}



	/**
	 * Create the dialog.
	 */
	public ManageLibraryFoldersDialog(JFrame owner) {
		super(owner, true);


		init();

		setTitle("Manage your Libraries");

		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{0, 0, 0, 0, 0};
		gbl_contentPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{	
			GridBagConstraints gbc_txtNewLibraryPath = new GridBagConstraints();
			gbc_txtNewLibraryPath.gridwidth = 3;
			gbc_txtNewLibraryPath.insets = new Insets(0, 0, 5, 5);
			gbc_txtNewLibraryPath.fill = GridBagConstraints.HORIZONTAL;
			gbc_txtNewLibraryPath.gridx = 0;
			gbc_txtNewLibraryPath.gridy = 1;
		}
		{
			JButton btnNewButton = new JImageButton(addLibraryAction);


			GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
			gbc_btnNewButton.insets = new Insets(0, 0, 5, 5);
			gbc_btnNewButton.gridx = 0;
			gbc_btnNewButton.gridy = 2;
			contentPanel.add(btnNewButton, gbc_btnNewButton);
		}
		{
			JButton btnRemoveLib = new JImageButton(removeLibraryAction);
			GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
			gbc_btnNewButton_1.insets = new Insets(0, 0, 5, 5);
			gbc_btnNewButton_1.gridx = 1;
			gbc_btnNewButton_1.gridy = 2;
			contentPanel.add(btnRemoveLib, gbc_btnNewButton_1);
		}
		{
			listAllLibraries = new JList(allLibrariesModel);
			listAllLibraries.setCellRenderer(new ImageListCellRenderer<MediaLibrary>(){

				@Override
				protected Component buildItem(MediaLibrary item){
					JLabel lblJLabel = new JLabel(item != null ? item.toString() : "null",ImageResources.FOLDER_ICON_32, SwingConstants.LEFT);
					lblJLabel.setOpaque(true);

					Border border =BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.WHITE,1),new EmptyBorder(10,10,10,10));
					lblJLabel.setBorder(border);

					return lblJLabel;
				}
			});
			//listAllLibraries.setBorder(UIManager.getBorder("List.focusCellHighlightBorder"));
			GridBagConstraints gbc_listAllLibraries = new GridBagConstraints();
			gbc_listAllLibraries.gridwidth = 3;
			gbc_listAllLibraries.insets = new Insets(0, 0, 5, 5);
			gbc_listAllLibraries.fill = GridBagConstraints.BOTH;
			gbc_listAllLibraries.gridx = 0;
			gbc_listAllLibraries.gridy = 3;

			contentPanel.add(new JScrollPane(listAllLibraries), gbc_listAllLibraries);
		}
		{

			txtCurrentPath = new FileChooserPanel(new Predicate<File>() {
				@Override
				public boolean where(File value) {
					// TODO proper checking
					return true; //value.isDirectory();
				}
			}, false, true);

			GridBagConstraints gbc_txtCurrentPath = new GridBagConstraints();
			gbc_txtCurrentPath.gridwidth = 3;
			gbc_txtCurrentPath.insets = new Insets(0, 0, 0, 5);
			gbc_txtCurrentPath.fill = GridBagConstraints.HORIZONTAL;
			gbc_txtCurrentPath.gridx = 0;
			gbc_txtCurrentPath.gridy = 4;
			contentPanel.add(txtCurrentPath, gbc_txtCurrentPath);
		}
		{
			JButton btnSave = new JButton(saveCurrentLibAction);
			GridBagConstraints gbc_btnSave = new GridBagConstraints();
			gbc_btnSave.gridx = 3;
			gbc_btnSave.gridy = 4;
			contentPanel.add(btnSave, gbc_btnSave);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						setVisible(false);
						dispose();
						onDialogClosing();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}


		updateState();

		postInit();
	}

	boolean librariesHasChanged = false;

	private void onDialogClosing(){
		if(librariesHasChanged){
			Action updateMediaLibraryAction = new UpdateMediaLibraryAction(getOwner()); 
			updateMediaLibraryAction.actionPerformed(null);
		}
	}


}
