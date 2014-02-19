package vidada.viewsFX.mediabrowsers;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import vidada.client.viewmodel.browser.BrowserItemVM;

public class SimpleCellItemView extends BrowserCellView {

	private final Label description = new Label("<no description>");

	public SimpleCellItemView(){
		description.setId("description"); // style id
		description.setAlignment(Pos.CENTER_LEFT);
		description.setPadding(new Insets(10));

		GlyphFont font = GlyphFontRegistry.font("FontAwesome");
		Glyph folderViewNode = font.fontSize(100).create(FontAwesome.Glyph.FILE_ALT.name());

		this.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseOpenHandler);

		this.setCenter(folderViewNode);
		this.setBottom(description);
	}


	@Override
	protected ContextMenu createContextMenu() {
		return null;
	}

	@Override
	public void setDataContext(BrowserItemVM viewmodel) {
		super.setDataContext(viewmodel);

		if(viewmodel != null){
			description.setText(viewmodel.getName());
		}else{
			description.setText("null");
		}

	}

	/**
	 * Occurs when the user clicks on the media
	 */
	transient private final EventHandler<MouseEvent> mouseOpenHandler = new EventHandler<MouseEvent>(){
		@Override
		public void handle(MouseEvent me) {
			if(me.getButton().equals(MouseButton.PRIMARY)){
				if(getDataContext() != null){
					getDataContext().open();
				}
			}
		}
	};


}
