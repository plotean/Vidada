package vidada.commands;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;

import vidada.IVidadaServer;
import vidada.model.media.MediaLibrary;
import vidada.model.settings.VidadaClientSettings;
import vidada.views.ImageResources;
import archimedes.core.expressions.Predicate;
import archimedes.core.io.locations.DirectoryLocation;
import archimedes.core.swing.components.ChooseFileDialog;



/**
 * Adds new media library wizard
 * @author IsNull
 *
 */
@SuppressWarnings("serial")
public class AddNewMediaLibraryAction extends AbstractAction{

	private final IVidadaServer localServer = vidada.Application.getLocalServer();

	private final Window owner;


	public AddNewMediaLibraryAction(Window owner){
		super("", ImageResources.ADD_ICON_32);
		this.owner = owner;
		this.putValue(Action.SHORT_DESCRIPTION, "Add a new Library");
	}

	@Override
	public void actionPerformed(ActionEvent evt) {

		Predicate<File> fileChooseStrategy = new Predicate<File>() {
			@Override
			public boolean where(File value) {
				return value.isDirectory();
			}
		};

		ChooseFileDialog dialog = new ChooseFileDialog(owner, fileChooseStrategy);
		dialog.setLocationRelativeTo(owner);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setTitle("Choose your root folder");
		dialog.setDescription("Choose the base folder of your media library. All contents of this folder will be scanned and imported.\n\nYou can define as many media libraries as you want.");
		dialog.requestFocus();
		dialog.setVisible(true);

		if(dialog.isOk())
		{
			MediaLibrary library = new MediaLibrary();
			VidadaClientSettings settings = VidadaClientSettings.instance();
			library.setIgnoreImages(settings.isIgnoreImages());
			library.setIgnoreMovies(settings.isIgnoreMovies());

			DirectoryLocation location = DirectoryLocation.Factory.create(dialog.getFile().toURI());
			library.setLibraryRoot(location);

			localServer.getLibraryService().addLibrary(library);
			System.out.println("AddNewMediaLibraryAction: Added media libarary @" + library.getMediaDirectory());

		}else {
			System.out.println("AddNewMediaLibraryAction: User aborted adding a new library");
		}
	}

}
