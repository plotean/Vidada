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
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import archimedesJ.exceptions.NotSupportedException;
import archimedesJ.security.CredentialType;
import archimedesJ.security.Credentials;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

@SuppressWarnings("serial")
public class AuthenticateDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JLabel lblPassword;

	private boolean isOk;
	private JTextField txtDomain;
	private JTextField txtUsername;
	private JPasswordField passwordField;

	private boolean sDomain;
	private boolean sUser;
	private boolean sPass;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			AuthenticateDialog dialog = new AuthenticateDialog(
					null,
					CredentialType.PasswordOnly,
					"Please enter your password below:");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void configDialog(CredentialType type){
		sDomain = false;
		sUser = false;
		sPass = false;

		switch (type) {
		case PasswordOnly:
			sPass = true;
			break;

		case UserAndPass:
			sUser = true;
			sPass = true;
			break;

		case DomainUserPass:
			sDomain = true;
			sUser = true;
			sPass = true;
			break;

		default:
			throw new NotSupportedException("CredentialType: " + type.toString());
		}
	}


	/**
	 * Create the dialog.
	 */
	public AuthenticateDialog(Window owner, CredentialType type, String description) {
		super(owner, "Authenticate", ModalityType.APPLICATION_MODAL);

		configDialog(type);

		setBounds(100, 100, 452, 316);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("right:default"),
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
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		{
			JLabel lblPleaseEnterYour = new JLabel(description);
			contentPanel.add(lblPleaseEnterYour, "2, 4, 3, 1");
		}
		{
			if(sDomain){
				JLabel lblDomain = new JLabel("Domain");
				contentPanel.add(lblDomain, "2, 12, right, default");
			}
		}
		{
			if(sDomain){
				txtDomain = new JTextField();
				txtDomain.setText("textDomain");
				contentPanel.add(txtDomain, "4, 12, fill, default");
				txtDomain.setColumns(10);
			}
		}
		{
			if(sUser){
				JLabel lblUsername = new JLabel("Username");
				contentPanel.add(lblUsername, "2, 14, right, default");
			}
		}
		{
			if(sUser){
				txtUsername = new JTextField();
				txtUsername.setText("Username");
				contentPanel.add(txtUsername, "4, 14, fill, default");
				txtUsername.setColumns(10);
			}
		}
		{
			if(sPass){
				lblPassword = new JLabel("Password");
				contentPanel.add(lblPassword, "2, 16, right, default");
			}
		}
		{
			if(sPass){
				passwordField = new JPasswordField();
				lblPassword.setLabelFor(passwordField);
				contentPanel.add(passwordField, "4, 16, fill, default");
			}
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

	public Credentials getCredentials(){

		return new Credentials(
				(txtDomain != null) ? txtDomain.getText() : null,
						(txtUsername != null) ? txtUsername.getText() : null,
								(passwordField != null) ? new String(passwordField.getPassword()) : null);
	}



	public boolean isOk() {
		return isOk;
	}

	private void setOk(boolean isOk) {
		this.isOk = isOk;
	}

}
