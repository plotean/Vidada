package vidada.views.mediabrowsers;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import vidada.views.settings.VisualSettingsPanel;
import archimedesJ.swing.components.thumbpresenter.JThumbViewPortRenderer;

@SuppressWarnings("serial")
public class VisualSettingsToolBox extends JPanel {


	private final VisualSettingsPanel visualSettings;

	public VisualSettingsToolBox(){

		setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.DARK_GRAY),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));

		this.setLayout(new BorderLayout(0,0));
		visualSettings = new VisualSettingsPanel();
		this.add(visualSettings, BorderLayout.CENTER);
		visualSettings.setBackground(this.getBackground());
	}

	/**
	 * Set the DataContext
	 * @param datacontext
	 */
	public void setDataContext(JThumbViewPortRenderer datacontext){
		visualSettings.setDataContext(datacontext);
	}



}
