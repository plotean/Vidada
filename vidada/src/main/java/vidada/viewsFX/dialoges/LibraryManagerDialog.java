package vidada.viewsFX.dialoges;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import org.controlsfx.dialog.Dialog;

import java.io.IOException;

public final class LibraryManagerDialog extends Dialog {

	public LibraryManagerDialog(Object owner) {
		super(owner, "Vidada Library Manager");
		setMasthead("Here you can add or remove media libraries.");

        try {
            Node libManager = FXMLLoader.load(getClass().getResource("../../../../resources/vidada/viewsFX/libraries/LibraryManagerView.fxml"));
            setContent(libManager);
        } catch (IOException e) {
            e.printStackTrace();
        }

		setResizable(true);
	}
}
