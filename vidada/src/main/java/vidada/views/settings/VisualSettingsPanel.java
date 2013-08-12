package vidada.views.settings;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import vidada.model.settings.GlobalSettings;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventListenerEx;
import archimedesJ.swing.components.thumbpresenter.JThumbViewPortRenderer;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;



/**
 * 
 * @author IsNull
 *
 */
@SuppressWarnings("serial")
public class VisualSettingsPanel extends JPanel {

	private final JSlider itemSizeSlider;

	private JThumbViewPortRenderer thumbpresenter;



	private void init(){

		// register slider event 
		itemSizeSlider.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(thumbpresenter != null)
				{
					int value = itemSizeSlider.getValue();
					thumbpresenter.setItemSizeWidth(value);
				}
			}
		});


	}

	/**
	 * Set the DataContext
	 * @param datacontext
	 */
	public void setDataContext(JThumbViewPortRenderer datacontext){
		thumbpresenter = datacontext;
		itemSizeSlider.setValue(datacontext.getItemSize().width);

		thumbpresenter.getThumbSettingsChangedEvent().add(new EventListenerEx<EventArgs>() {

			@Override
			public void eventOccured(Object sender, EventArgs eventArgs) {
				itemSizeSlider.setValue(thumbpresenter.getItemSize().width);
			}
		});
	}


	public VisualSettingsPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
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
				FormFactory.DEFAULT_ROWSPEC,}));

		JLabel lblThumbSize = new JLabel("Thumb size");
		add(lblThumbSize, "2, 6");

		itemSizeSlider = new JSlider();
		itemSizeSlider.setPaintTicks(true);
		itemSizeSlider.setMajorTickSpacing(GlobalSettings.THUMBNAIL_SIZE_GAP);
		itemSizeSlider.setSnapToTicks(true);
		itemSizeSlider.setValue(0);
		itemSizeSlider.setMaximum(GlobalSettings.THUMBNAIL_SIZE_MAX);
		itemSizeSlider.setMinimum(GlobalSettings.THUMBNAIL_SIZE_MIN);
		add(itemSizeSlider, "4, 6");

		JLabel lblShowRating = new JLabel("Show Rating");
		add(lblShowRating, "2, 8");

		JCheckBox chckbxNewCheckBox = new JCheckBox("");
		chckbxNewCheckBox.setEnabled(false);
		chckbxNewCheckBox.setSelected(true);
		add(chckbxNewCheckBox, "4, 8");

		init();
	}

}
