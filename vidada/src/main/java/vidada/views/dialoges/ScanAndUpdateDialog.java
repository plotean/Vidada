package vidada.views.dialoges;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import archimedesJ.events.EventArgs;
import archimedesJ.events.EventListenerEx;
import archimedesJ.threading.IProgressListener;
import archimedesJ.threading.ProgressEventArgs;
import archimedesJ.util.FileSupport;

@SuppressWarnings("serial")
public class ScanAndUpdateDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private final JProgressBar progressBar;
	private final JLabel lblCurrentTask;
	private final JTextArea txtTaskLog;
	private final JButton okButton;

	private boolean closeDialogOnSuccess = true;



	/**
	 * Create the dialog.
	 */
	public ScanAndUpdateDialog(Window owner) {
		super(owner);
		setModalityType(ModalityType.APPLICATION_MODAL);

		setTitle("Scan and Update");

		setBounds(100, 100, 931, 570);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{0, 0};
		gbl_contentPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			progressBar = new JProgressBar();
			GridBagConstraints gbc_progressBar = new GridBagConstraints();
			gbc_progressBar.fill = GridBagConstraints.HORIZONTAL;
			gbc_progressBar.insets = new Insets(0, 0, 5, 0);
			gbc_progressBar.gridx = 0;
			gbc_progressBar.gridy = 1;
			contentPanel.add(progressBar, gbc_progressBar);
		}
		{
			lblCurrentTask = new JLabel("Current task...");
			GridBagConstraints gbc_lblCurrentTask = new GridBagConstraints();
			gbc_lblCurrentTask.anchor = GridBagConstraints.SOUTHWEST;
			gbc_lblCurrentTask.insets = new Insets(0, 0, 5, 0);
			gbc_lblCurrentTask.gridx = 0;
			gbc_lblCurrentTask.gridy = 2;
			contentPanel.add(lblCurrentTask, gbc_lblCurrentTask);
		}
		{
			Box verticalBox = Box.createVerticalBox();
			GridBagConstraints gbc_verticalBox = new GridBagConstraints();
			gbc_verticalBox.fill = GridBagConstraints.VERTICAL;
			gbc_verticalBox.insets = new Insets(0, 0, 5, 0);
			gbc_verticalBox.gridx = 0;
			gbc_verticalBox.gridy = 3;
			contentPanel.add(verticalBox, gbc_verticalBox);
		}
		{
			txtTaskLog = new JTextArea();
			txtTaskLog.setEditable(false);
			txtTaskLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
			GridBagConstraints gbc_txtTaskLog = new GridBagConstraints();
			gbc_txtTaskLog.fill = GridBagConstraints.BOTH;
			gbc_txtTaskLog.gridx = 0;
			gbc_txtTaskLog.gridy = 4;



			contentPanel.add(new JScrollPane(txtTaskLog), gbc_txtTaskLog);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("OK");
				okButton.setEnabled(false);
				okButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						closeDialog();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setEnabled(false);
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}


		final ScanDBWorker updateDbWorker = new ScanDBWorker(new IProgressListener(){
			@Override
			public void currentProgress(final ProgressEventArgs progressInfo) {


				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						if(progressInfo.isIndeterminant())
						{
							progressBar.setIndeterminate(progressInfo.isIndeterminant());
						}else{
							progressBar.setIndeterminate(false);
							progressBar.setValue((int)progressInfo.getProgressInPercent());
						}
						lblCurrentTask.setText(progressInfo.getCurrentTask());
						txtTaskLog.append(progressInfo.getCurrentTask() + FileSupport.NEWLINE);
					}
				});
			}
		});

		updateDbWorker.DoneEvent.add(new EventListenerEx<EventArgs>() {

			@Override
			public void eventOccured(Object sender, EventArgs eventArgs) {
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {

						progressBar.setIndeterminate(false);
						progressBar.setValue(100);
						okButton.setEnabled(true);

						if(closeDialogOnSuccess && updateDbWorker.success()){
							//
							// close the update dialog automatically
							// when there were no errors
							//
							closeDialog();
						}else{
							txtTaskLog.append(updateDbWorker.getExceptionDetail().getMessage() + FileSupport.NEWLINE);
						}
					}
				});

			}
		});
		updateDbWorker.execute();
	}

	/**
	 * Close this dialog
	 */
	private void closeDialog(){
		setVisible(false);
		dispose();
	}


}
