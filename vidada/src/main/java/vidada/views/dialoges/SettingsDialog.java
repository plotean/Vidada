package vidada.views.dialoges;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import vidada.views.settings.AboutPanel;
import vidada.views.settings.GeneralSettingsPanel;
import vidada.views.settings.PrivacyPanel;

@SuppressWarnings("serial")
public class SettingsDialog extends JDialog {

	/**
	 * Create the dialog.s
	 */
	public SettingsDialog(JFrame owner) {
		super(owner, true);

		setTitle("Advanced Settings");

		setBounds(100, 100, 586, 556);
		getContentPane().setLayout(new BorderLayout());


		JTabbedPane contentTab = new JTabbedPane(JTabbedPane.TOP);
		contentTab.addTab("Settings", new GeneralSettingsPanel());
		contentTab.addTab("Privacy", new PrivacyPanel());
		contentTab.addTab("About", new AboutPanel());


		getContentPane().add(contentTab, BorderLayout.CENTER);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}

}
