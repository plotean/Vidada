package vidada.viewmodel.media;

import vidada.viewmodel.ITagStatesVMProvider;

/**
 * ViewModel for a media item
 * @author IsNull
 *
 */
public interface IMediaViewModel {


	// media general info

	public abstract void setRating(int selection);

	public abstract int getRating();

	public abstract String getTitle();

	public abstract String getFilename();

	public abstract void setFileName(String text);

	public abstract int getOpened();

	public abstract String getResolution();

	public abstract String getAddedDate();

	/**
	 * Persist the changes
	 */
	public abstract void persist();

	public abstract ITagStatesVMProvider getTagsVM();

}