package vidada.viewsFX.update;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import vidada.model.update.SelfUpdateState;
import vidada.services.ISelfUpdateService;
import vidada.services.ServiceProvider;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Visualizes version / updates and the current update status
 */
public class UpdateBar extends BorderPane{

    private final ISelfUpdateService updateService = ServiceProvider.Resolve(ISelfUpdateService.class);
    private final GlyphFont font = GlyphFontRegistry.font("FontAwesome");
    private Timer statusTimer = new java.util.Timer();

    public UpdateBar(){
        startPollStatus();
        updateService.checkForUpdateAsync();
    }

    private void startPollStatus(){
        statusTimer.schedule(new TimerTask() {
            public void run() {
                Platform.runLater(() -> {
                    updateView();
                });
            }
        }, 2 * 1000, 5 * 1000);
    }

    private void updateView(){
        updateView(updateService.getState());
    }

    private void updateView(SelfUpdateState state){

        Node icon;

        switch (state){

            case UpdateAvailableForDownload:
                icon = createIcon(FontAwesome.Glyph.CLOUD_DOWNLOAD, "Download the new Update!", () -> {
                    updateService.downloadUpdateAsync();
                    updateView(SelfUpdateState.UpdateDownloading);
                });
                break;

            case UpdateAvailableForInstall:
                icon = createIcon(FontAwesome.Glyph.REFRESH, "Install the new Update!", () -> updateService.installAndRestart());
                break;

            case UpToDate:
                icon = createIcon(FontAwesome.Glyph.INFO, "Great, you use the latest available version!", null);
                break;

            case UpdateDownloading:
                icon = createIcon(FontAwesome.Glyph.SPINNER, "Update is being downloaded...", null);
            break;

            default:
                icon = createIcon(FontAwesome.Glyph.EXCLAMATION_SIGN, "No update information available.", null);
                break;
        }

        this.setCenter(icon);
    }


    private Node createIcon(FontAwesome.Glyph glyph, String tooltip, Runnable action){
        Glyph icon = font
                .fontColor(Color.WHITE)
                .fontSize(40)
                .create(glyph.name());

        if(action != null) {
            icon.getStyleClass().addAll(Glyph.STYLE_HOVER_EFFECT);
        }

        icon.setPadding(new Insets(0,20,0,20));
        Tooltip tip = new Tooltip(tooltip);
        tip.setFont(new Font(tip.getFont().getName(), 18));

        icon.setTooltip(tip);
        icon.setOnMouseClicked(x -> action.run());

        return icon;
    }

}
