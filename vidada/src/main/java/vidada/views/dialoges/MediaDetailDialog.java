package vidada.views.dialoges;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import vidada.model.media.MediaItem;
import vidada.model.media.source.IMediaSource;
import archimedesJ.util.FileSupport;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

@SuppressWarnings("serial")
public class MediaDetailDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JLabel lblPassword;
	private JTextArea details;


	/**
	 * Create the dialog.
	 */
	public MediaDetailDialog(Window owner, MediaItem media) {
		super(owner, "Authenticate", ModalityType.APPLICATION_MODAL);
		setTitle("Media Details of " + media.getFilename());

		setBounds(100, 100, 449, 314);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
				new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		{
			JLabel lblPleaseEnterYour = new JLabel("Details of media\n");
			contentPanel.add(lblPleaseEnterYour, "2, 4, 3, 1");
		}
		{
			lblPassword = new JLabel("Data");
			contentPanel.add(lblPassword, "2, 12, right, default");
		}
		{
			details = new JTextArea();
			details.setText(getDetailString(media));
			lblPassword.setLabelFor(details);
			contentPanel.add(details, "4, 6, 1, 7, fill, default");
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
						setOk(true);
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

		setLocationRelativeTo(owner);
	}

	private String getDetailString(MediaItem media){
		StringBuilder sb = new StringBuilder();

		sb.append("----------" + FileSupport.NEWLINE);

		for (IMediaSource source : media.getSources()) {
			sb.append("source: " + source.toString() + " - avaiable: " + source.isAvailable() + FileSupport.NEWLINE);
		}

		sb.append("----------" + FileSupport.NEWLINE);


		return sb.toString();
	}


	private boolean isOk;

	public boolean isOk() {
		return isOk;
	}

	private void setOk(boolean isOk) {
		this.isOk = isOk;
	}

}
