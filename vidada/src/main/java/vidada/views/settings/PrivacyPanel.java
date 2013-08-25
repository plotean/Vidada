package vidada.views.settings;

import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import vidada.model.ServiceProvider;
import vidada.model.security.AuthenticationException;
import vidada.model.security.ICredentialManager;
import vidada.model.security.IPrivacyService;
import archimedesJ.security.CredentialType;
import archimedesJ.security.Credentials;
import archimedesJ.util.Debug;
import archimedesJ.util.FileSupport;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

@SuppressWarnings("serial")
public class PrivacyPanel extends JPanel {

	private Action protectAction;
	private Action unprotectAction;


	private IPrivacyService privacyService = ServiceProvider.Resolve(IPrivacyService.class);
	private ICredentialManager credentialManager = ServiceProvider.Resolve(ICredentialManager.class);

	private void defineActions(){

		//
		// Protect the database
		//
		protectAction = new AbstractAction("Protect Database") {

			@Override
			public void actionPerformed(ActionEvent evt) {

				if(privacyService.isProtected()){
					System.err.println("Already protected!");
					return;
				}

				Window parent = (Window)PrivacyPanel.this.getTopLevelAncestor();

				Credentials newCredentials = credentialManager
						.requestNewCredentials("Please enter a new password:", CredentialType.PasswordOnly);

				if(newCredentials != null){
					// now we are ready to protect the database:
					privacyService.protect(newCredentials);
					updateModelToView();

					JOptionPane.showMessageDialog(parent, "Successful protected", "Database and cache successful protected.", JOptionPane.INFORMATION_MESSAGE);
				}else {
					System.err.println("user aborted creditals input.");
				}


			}

		};
		protectAction.putValue(Action.SHORT_DESCRIPTION, "Protect your media database by password");


		//
		// Protect the database
		//
		unprotectAction = new AbstractAction("Remove protection") {

			@Override
			public void actionPerformed(ActionEvent evt) {

				if(!privacyService.isProtected()){
					System.err.println("Not protected!");
					return;
				}

				Window parent = (Window)PrivacyPanel.this.getTopLevelAncestor();

				Credentials oldCredentials = credentialManager.requestCredentials(
						"Please enter your old password in order to remove it.",
						CredentialType.PasswordOnly);


				if(oldCredentials != null){
					try {
						privacyService.removeProtection(oldCredentials);
						JOptionPane.showMessageDialog(
								parent,
								"Protection removed",
								"Database and cache protection has been removed.",
								JOptionPane.INFORMATION_MESSAGE);
					} catch (AuthenticationException e) {
						JOptionPane.showMessageDialog(
								parent,
								"Your password was not correct. You have to supply yor password in order to remove the protection.",
								"Removing protection failed",
								JOptionPane.WARNING_MESSAGE);
					}catch (Exception e) {
						JOptionPane.showMessageDialog(
								parent,
								"There was an error while removing the protection:" + FileSupport.NEWLINE +
								Debug.buildExceptionStackTrace(e),
								"Removing protection failed",
								JOptionPane.WARNING_MESSAGE);
					}
				}
			}

		};
		unprotectAction.putValue(Action.SHORT_DESCRIPTION, "Remove the password protection from this database");

	}

	private void updateModelToView(){

		protectAction.setEnabled(!privacyService.isProtected());
		unprotectAction.setEnabled(privacyService.isProtected());
		btnProtect.setText(!privacyService.isProtected() ? "Protect" : "(protected)");
	}

	JButton btnProtect;

	public PrivacyPanel(){

		defineActions();

		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
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

		JLabel lblLocal = new JLabel("Local");
		lblLocal.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		add(lblLocal, "2, 2");

		JLabel lblPasswordProtection = new JLabel("Library Password");
		add(lblPasswordProtection, "4, 4");

		btnProtect = new JButton(protectAction);
		add(btnProtect, "6, 4");

		JButton btnUnprotect = new JButton(unprotectAction);
		add(btnUnprotect, "8, 4");

		JCheckBox chckbxEncryptCache = new JCheckBox("Encrypt Cache");
		chckbxEncryptCache.setEnabled(false);
		chckbxEncryptCache.setSelected(true);
		add(chckbxEncryptCache, "6, 6, 5, 1");

		JLabel lblNetwork = new JLabel("Network");
		lblNetwork.setEnabled(false);
		lblNetwork.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		add(lblNetwork, "2, 20");

		JCheckBox chckbxShareTagInformation = new JCheckBox("Share Tag informations");
		chckbxShareTagInformation.setEnabled(false);
		add(chckbxShareTagInformation, "4, 22, 7, 1");


		updateModelToView();
	}

}
