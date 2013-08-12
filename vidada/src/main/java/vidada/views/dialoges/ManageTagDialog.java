package vidada.views.dialoges;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import vidada.model.tags.Tag;
import vidada.views.tags.TagEditPanel;

@SuppressWarnings({ "serial" }) // 1.6 compatibility, no generic in Swing allowed
public class ManageTagDialog extends JDialog {




	private ManageTagDialog(){
		this(null, null);
	}

	/**
	 * Create the dialog.
	 */
	public ManageTagDialog(Window owner, Tag mytag) {
		super(owner, "", ModalityType.APPLICATION_MODAL);

		setTitle("Manage your Tag");

		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());


		TagEditPanel tagEditPanel = new TagEditPanel();
		tagEditPanel.setDataContext(mytag);
		getContentPane().add(tagEditPanel, BorderLayout.CENTER);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		{
			JButton okButton = new JButton("OK");
			okButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
				}
			});
			okButton.setActionCommand("OK");
			buttonPane.add(okButton);
			getRootPane().setDefaultButton(okButton);
		}
		{
			JButton cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
				}
			});
			cancelButton.setActionCommand("Cancel");
			buttonPane.add(cancelButton);
		}

	}


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ManageTagDialog dialog = new ManageTagDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
