package vidada.views;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import vidada.IMainUIContext;
import archimedesJ.util.OSValidator;

public class SwingMainContext implements IMainUIContext {

	private VidadaSplash customSplash;
	private JFrame MainFrame;

	@Override
	public void showSplash(){

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {

					// create splash
					customSplash = new VidadaSplash();
					customSplash.setVisible(true);
					customSplash.setAlwaysOnTop(true);
					customSplash.toFront();
					customSplash.setAlwaysOnTop(false);

					// define visual appearance
					setTheme();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void hideSplash() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				if(customSplash!= null && customSplash.isVisible())
					customSplash.setVisible(false);
			}
		});
	}


	@Override
	public void showMainUI() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				System.out.println("init application frame...");
				MainFrame = new MainFrame();
				MainFrame.setLocationRelativeTo(null);
				MainFrame.setAlwaysOnTop(true);
				MainFrame.toFront();
				MainFrame.requestFocus();
				MainFrame.setAlwaysOnTop(false);
				//MainFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
			}
		});
	}


	static boolean useTheme = true;
	private static void setTheme(){

		if(useTheme && !OSValidator.isOSX()){
			try {
				// Default
				//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

				//Nimbus
				for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
					if ("Nimbus".equals(info.getName())) {
						UIManager.setLookAndFeel(info.getClassName());


						UIDefaults defaults = UIManager.getLookAndFeelDefaults();
						defaults.put("Panel.background", Color.WHITE); // we want a white background as an overall design rule
						defaults.put("OptionPane.background", Color.WHITE); // dito
						defaults.put("ToolPane.background", Color.DARK_GRAY);

						break;
					}
				}
			} catch (Exception e) {
				// If Nimbus is not available, you can set the GUI to another look and feel.
			}
		}
	}




}
