package vidada.views.mediabrowsers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class MediaBrowserContainer extends JLayeredPane {

	private boolean enablehiddenSettingsBar = false;

	private HiddenSettingsBar hiddenSettingsBar;
	private VisualSettingsToolBox visualSettingsToolBox;
	private JPanel defaultPanel;

	public MediaBrowserContainer(MediaBrowserBasePanel defaultPanel){

		setBackground(Color.PINK);

		this.defaultPanel = defaultPanel;

		add(defaultPanel, JLayeredPane.DEFAULT_LAYER);

		visualSettingsToolBox = new VisualSettingsToolBox();
		visualSettingsToolBox.setDataContext(defaultPanel.getMediaViewer());

		if(enablehiddenSettingsBar){
			hiddenSettingsBar = new HiddenSettingsBar();
			add(hiddenSettingsBar, JLayeredPane.PALETTE_LAYER);
		}
		add(visualSettingsToolBox, JLayeredPane.POPUP_LAYER);

		registerEventHandlers();

		// init toolbox
		visualSettingsToolBox.setVisible(true);
		visualSettingsToolBox.setVisible(false);
	}

	private void registerEventHandlers() {


		visualSettingsToolBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent me) {

				Component c = SwingUtilities.getDeepestComponentAt(me.getComponent(), me.getX(), me.getY());
				// doesn't work if you move your mouse into the combobox popup

				if(c != null && SwingUtilities.isDescendingFrom(c, visualSettingsToolBox)){
				}else{
					hideSettings();
				}
			}
		});

		if(hiddenSettingsBar != null){
			hiddenSettingsBar.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent me) {
					showSettings();
				}
			});
		}
	}

	private void hideSettings(){
		visualSettingsToolBox.setVisible(false);
		hiddenSettingsBar.repaint();
	}

	private void showSettings(){

		Dimension size = visualSettingsToolBox.getPreferredSize();

		visualSettingsToolBox.setBounds(5, getHeight()/2 - size.height/2, 
				size.width,
				size.height);

		visualSettingsToolBox.setVisible(true);

		visualSettingsToolBox.invalidate();
		visualSettingsToolBox.repaint();

		if(hiddenSettingsBar != null)
			hiddenSettingsBar.repaint();
	}

	@Override
	public Dimension getPreferredSize(){
		return defaultPanel.getPreferredSize();
	}

	@Override
	public void doLayout(){
		defaultPanel.setBounds(0, 0, this.getWidth(), this.getHeight());

		if(hiddenSettingsBar != null){
			int hiddenBarHeight = 100;

			hiddenSettingsBar.setBounds(
					0,
					getHeight()/2 - hiddenBarHeight/2, 
					20,
					hiddenBarHeight);
		}
	}


}
