package vidada.viewsFX.images;

import archimedes.core.images.IRawImageFactory;
import archimedes.core.images.viewer.IImageViewerService;
import archimedes.core.images.viewer.ISmartImage;
import archimedes.core.images.viewer.SmartImageLazy;
import archimedes.core.io.locations.ResourceLocation;
import vidada.handlers.IMediaHandler;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaType;
import vidada.services.ServiceProvider;


public class ImageOpenHandler implements IMediaHandler {

    transient private final IImageViewerService imageViewer = ServiceProvider.Resolve(IImageViewerService.class);
    transient private final IRawImageFactory imageFactory = ServiceProvider.Resolve(IRawImageFactory.class);

    @Override
    public String getName() {
        return "Internal Image Viewer";
    }

    @Override
    public boolean handle(MediaItem media, ResourceLocation mediaResource) {

        if(media.getType().equals(MediaType.IMAGE)){
            //
            // In case it is an image, show it in internal preview
            //
            ISmartImage smartImage;
            smartImage = new SmartImageLazy(imageFactory, mediaResource.getUri());
            imageViewer.showImage(smartImage);
            return true;
        }else {
            return false;
        }
    }
}
