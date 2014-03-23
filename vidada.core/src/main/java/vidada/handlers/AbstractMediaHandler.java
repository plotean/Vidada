package vidada.handlers;

import archimedesJ.io.locations.ResourceLocation;
import vidada.model.media.MediaItem;

/**
 * Base class of all media handlers
 */
public abstract class AbstractMediaHandler implements  IMediaHandler {

    private final String name;

    protected AbstractMediaHandler(String name){
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public abstract boolean handle(MediaItem media, ResourceLocation mediaResource);


    @Override
    public String toString(){
        return getName();
    }
}
