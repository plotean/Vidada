package vidada.client.viewmodel;

import vidada.model.media.MediaLibrary;

public class MediaLibraryVM implements IViewModel<MediaLibrary>{

    private MediaLibrary model;


    public MediaLibraryVM(MediaLibrary model){
        setModel(model);
    }


    @Override
    public MediaLibrary getModel() {
        return model;
    }

    @Override
    public final void setModel(MediaLibrary model) {
        this.model = model;
    }

    public String toString(){
        return getModel().getMediaDirectory().getDirectory().getPath();
    }


    @Override
    public int hashCode(){
        return model.hashCode();
    }

    @Override
    public boolean equals(Object other){
        if(!(other instanceof  MediaLibraryVM)) return false;
        MediaLibraryVM otherVM = (MediaLibraryVM)other;
        return otherVM.getModel() != null && otherVM.getModel().equals(this.getModel());
    }
}
