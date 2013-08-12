package vidada.views.tags;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

@SuppressWarnings({"serial"})
public class TagSettingsPanel extends JPanel {

	public TagSettingsPanel(){
		setLayout(new BorderLayout(0, 0));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);

		tabbedPane.addTab("Edit Tags", new TagManagerPanel());

		tabbedPane.addTab("AutoTag", new AutoTagPanel());


		add(tabbedPane, BorderLayout.CENTER);


	}


}
