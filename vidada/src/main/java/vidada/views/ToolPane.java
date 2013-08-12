package vidada.views;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.Timer;

import vidada.commands.UpdateMediaLibraryAction;
import vidada.model.settings.GlobalSettings;
import vidada.views.dialoges.ManageLibraryFoldersDialog;
import vidada.views.dialoges.ManageTagsDialog;
import vidada.views.dialoges.SettingsDialog;
import archimedesJ.swing.JImageButton;
import archimedesJ.util.Debug;

/**
 * Primary tool pane
 * @author IsNull
 *
 */
@SuppressWarnings("serial")
public class ToolPane extends JToolBar{


	public ToolPane() {

		// Create the actions

		//
		// Library Manager
		//

		//	final JFrame parentFrame = (JFrame)this.getTopLevelAncestor();

		Action showLibManagerAction = new AbstractAction("", ImageResources.FOLDER_ICON_32) {
			@Override
			public void actionPerformed(ActionEvent evt) {
				JFrame parentFrame = (JFrame)getTopLevelAncestor();

				ManageLibraryFoldersDialog libDialog = new ManageLibraryFoldersDialog(parentFrame);
				libDialog.setLocationRelativeTo(null);
				libDialog.setVisible(true);
			}
		};
		showLibManagerAction.putValue(Action.SHORT_DESCRIPTION, "Open Library Manager");
		registerAction(showLibManagerAction);

		//
		// Update Library
		//
		Action updateLibraryAction = new UpdateMediaLibraryAction((Window)getTopLevelAncestor()); 
		registerAction(updateLibraryAction);



		//
		// Edit Tags
		//
		Action manageTagsAction = new AbstractAction("", ImageResources.TAG_ICON_32) {
			@Override
			public void actionPerformed(ActionEvent evt) {
				ManageTagsDialog manageTagsDialog = new ManageTagsDialog();
				manageTagsDialog.setLocationRelativeTo(null);
				manageTagsDialog.setVisible(true);
			}
		};
		manageTagsAction.putValue(Action.SHORT_DESCRIPTION, "Manage Tags");
		registerAction(manageTagsAction);


		//
		// Show settings
		//
		Action showSettingsAction = new AbstractAction("", ImageResources.SETTINGS_ICON_32) {
			@Override
			public void actionPerformed(ActionEvent evt) {
				JFrame parentFrame = (JFrame)getTopLevelAncestor();

				SettingsDialog settingsDialog = new SettingsDialog(parentFrame);
				settingsDialog.setLocationRelativeTo(null);
				settingsDialog.setVisible(true);
			}
		};
		showSettingsAction.putValue(Action.SHORT_DESCRIPTION, "Settings");
		registerAction(showSettingsAction);

		
		if(GlobalSettings.getInstance().isDebug())
			registerDebugTools();
	}

	private JButton debugInfo;
	
	private void registerDebugTools(){
		debugInfo = new JButton("...");
		Timer dbgTimer = new Timer(500, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				long freeMem = Runtime.getRuntime().freeMemory();
				long maxMem = 	Runtime.getRuntime().maxMemory();
				long totalMem = Runtime.getRuntime().totalMemory();
				
				debugInfo.setText(
								"[f:"+ Debug.humanReadableByteCount(freeMem) + "] " +
								"[t:"+ Debug.humanReadableByteCount(totalMem) + "] "  + 
								"[m:"+ Debug.humanReadableByteCount(maxMem) + "]");
			}
		});
		dbgTimer.start();
		this.add(debugInfo);
	}
	
	
	
	private void registerAction(Action action){

		//JButton toolButton = new JButton(action);
		JButton toolButton = new JImageButton(action);		
		toolButton.setContentAreaFilled(false);
		toolButton.setBorderPainted(false);

		this.add(toolButton);
	}

}

