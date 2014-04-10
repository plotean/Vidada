package vidada.viewsFX.update;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import vidada.model.update.SelfUpdateState;
import vidada.services.ISelfUpdateService;
import vidada.services.ServiceProvider;

/**
 * Visualizes updates
 */
public class UpdateBar extends BorderPane{

    private final ISelfUpdateService updateService = ServiceProvider.Resolve(ISelfUpdateService.class);
    private final GlyphFont font = GlyphFontRegistry.font("FontAwesome");

    public UpdateBar(){
        updateService.getUpdateAvailableEvent().add((s,e)-> updateView(updateService.getState()));
        updateService.getUpdateInstallAvailableEvent().add((s,e)-> updateView(updateService.getState()));

        updateView(SelfUpdateState.UpdateAvailableForInstall);
    }

    private void updateView(SelfUpdateState state){

        Node icon = null;

        switch (state){

            case UpdateAvailableForDownload:
                icon = createIcon(FontAwesome.Glyph.CLOUD_DOWNLOAD, "Download new Update!", () -> updateService.downloadUpdateAsync());
                break;

            case UpdateAvailableForInstall:
                icon = createIcon(FontAwesome.Glyph.REFRESH, "Install new Update!", () -> updateService.installAndRestart());
                break;

            default:

                break;
        }

        this.setCenter(icon);
    }


    private Node createIcon(FontAwesome.Glyph glyph, String tooltip, Runnable action){
        Glyph icon = font
                .fontColor(Color.WHITE)
                .fontSize(40)
                .create(glyph.name());

        icon.setPadding(new Insets(0,20,0,20));
        icon.setTooltip(new Tooltip(tooltip));
        icon.setOnMouseClicked(x -> action.run());

        return icon;
    }

}
