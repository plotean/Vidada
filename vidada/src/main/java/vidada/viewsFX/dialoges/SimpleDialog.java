package vidada.viewsFX.dialoges;

import javafx.scene.Node;
import org.controlsfx.dialog.Dialog;

public final class SimpleDialog extends Dialog {

	public SimpleDialog(Object owner, String title, Node content) {
		super(owner, title);
        setContent(content);
        setResizable(false);
	}
}
