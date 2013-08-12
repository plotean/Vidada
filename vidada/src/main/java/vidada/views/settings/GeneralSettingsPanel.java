package vidada.views.settings;

import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import vidada.model.ServiceProvider;
import vidada.model.connectivity.IConnectivityService;
import vidada.model.media.IMediaService;
import vidada.model.settings.DatabaseSettings;
import vidada.model.settings.GlobalSettings;
import vidada.views.tools.DuplicateManagerDialoge;
import vlcj.VlcjUtil;
import archimedesJ.util.FileSupport;
import archimedesJ.util.OSValidator;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import ffmpeg.Interop.FFmpegInterop;

@SuppressWarnings("serial")
public class GeneralSettingsPanel extends JPanel {
	private JTextField txtVLCPath;
	private JTextField txtFFMpegPath;

	private final DatabaseSettings applicationSettings = DatabaseSettings.getSettings();

	private final JCheckBox chckbxEnableDirectplaySound;


	public GeneralSettingsPanel(){
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.GROWING_BUTTON_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,},
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
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));

		JLabel lblSettings = new JLabel("General");
		lblSettings.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		add(lblSettings, "2, 2");

		JCheckBox chckbxDrawHdpi = new JCheckBox("Draw HDPI");
		chckbxDrawHdpi.setEnabled(false);
		chckbxDrawHdpi.setSelected(OSValidator.isHDPI());
		add(chckbxDrawHdpi, "4, 4, 3, 1");

		JCheckBox chckbxEnableDirectplay = new JCheckBox("Enable DirectPlay");
		chckbxEnableDirectplay.setEnabled(false);
		chckbxEnableDirectplay.setSelected(true);
		add(chckbxEnableDirectplay, "4, 6, 3, 1");

		chckbxEnableDirectplaySound = new JCheckBox("Enable DirectPlay Sound");
		chckbxEnableDirectplaySound.setSelected(applicationSettings.isPlaySoundDirectPlay());
		add(chckbxEnableDirectplaySound, "4, 8, 5, 1");

		chckbxEnableDirectplaySound.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				applicationSettings.setPlaySoundDirectPlay(chckbxEnableDirectplaySound.isSelected());
				applicationSettings.persist();
			}
		});

		JLabel lblEnvironment = new JLabel("Environment");
		lblEnvironment.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		add(lblEnvironment, "2, 12");

		JLabel lblVlc = new JLabel("VLC Library");
		add(lblVlc, "4, 14, right, default");

		txtVLCPath = new JTextField(VlcjUtil.getVLCLibPath());
		txtVLCPath.setEditable(false);
		txtVLCPath.setEnabled(false);
		add(txtVLCPath, "6, 14, 3, 1, fill, default");
		txtVLCPath.setColumns(10);

		JLabel lblFfmpeg = new JLabel("ffmpeg");
		add(lblFfmpeg, "4, 16, right, default");

		txtFFMpegPath = new JTextField();
		txtFFMpegPath.setEnabled(false);
		txtFFMpegPath.setEditable(false);
		add(txtFFMpegPath, "6, 16, 3, 1, fill, default");
		txtFFMpegPath.setColumns(10);
		txtFFMpegPath.setText(FFmpegInterop.instance().getFFmpegBinaryFile().toString());


		JLabel lblTools = new JLabel("Tools");
		lblTools.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		add(lblTools, "2, 22");

		JLabel lblNewLabel = new JLabel("                  ");
		add(lblNewLabel, "6, 22");

		JButton btnClearMediaDb = new JButton("Clear Media DB");
		btnClearMediaDb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ServiceProvider.Resolve(IMediaService.class).removeAll();
			}
		});
		add(btnClearMediaDb, "4, 24, 3, 1, fill, default");

		JLabel lblDeleteAllMedias = new JLabel("Delete all medias in your library");
		add(lblDeleteAllMedias, "8, 24, 3, 1");

		JButton btnNewButton = new JButton("Export Media Infos");
		btnNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				IConnectivityService connectivityService = ServiceProvider.Resolve(IConnectivityService.class);
				File export = new File(GlobalSettings.getInstance().getAbsoluteCachePath(), "mediadata.json");
				connectivityService.exportMediaInfo(export);

				JOptionPane.showMessageDialog((Window)GeneralSettingsPanel.this.getTopLevelAncestor(),
						"Exported all media info to:" + FileSupport.NEWLINE + export.getAbsolutePath(), "Export successful", JOptionPane.INFORMATION_MESSAGE);


			}
		});
		add(btnNewButton, "4, 26, 3, 1, fill, default");

		JButton btnNewButton_1 = new JButton("Import Media Info");
		btnNewButton_1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				IConnectivityService connectivityService = ServiceProvider.Resolve(IConnectivityService.class);

				//Create a file chooser
				final JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(GlobalSettings.getInstance().getAbsoluteCachePath());

				//In response to a button click:
				int returnVal = fc.showOpenDialog((Window)GeneralSettingsPanel.this.getTopLevelAncestor());

				System.out.println(returnVal + " .--" + JFileChooser.APPROVE_OPTION);

				if(JFileChooser.APPROVE_OPTION == returnVal)
				{
					connectivityService.updateMediaDatas(fc.getSelectedFile());

					JOptionPane.showMessageDialog((Window)GeneralSettingsPanel.this.getTopLevelAncestor(),
							"Updated all media files with the imported information", "Update successful", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		add(btnNewButton_1, "4, 28, 3, 1");

		JButton btnDuplicatetool = new JButton("DuplicateTool");
		add(btnDuplicatetool, "4, 30, 3, 1");
		btnDuplicatetool.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Window owner = (Window) SwingUtilities.getRoot(GeneralSettingsPanel.this);
				DuplicateManagerDialoge duplicateDlg= new DuplicateManagerDialoge(owner);
				duplicateDlg.setVisible(true);
			}
		});

	}

}
