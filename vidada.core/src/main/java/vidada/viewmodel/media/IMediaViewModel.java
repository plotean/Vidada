package vidada.viewmodel.media;

import java.util.List;

import vidada.model.tags.Tag;
import archimedesJ.data.observable.IObservableList;


/**
 * ViewModel for a media item.
 * This class provides an easy to use facade for views to show and manipulate a media item
 * @author IsNull
 *
 */
public interface IMediaViewModel {


	/***************************************************************************
	 *                                                                         *
	 * General media data                                                      *
	 *                                                                         *
	 **************************************************************************/

	public abstract void setRating(int selection);

	public abstract int getRating();

	public abstract String getTitle();

	public abstract String getFilename();

	public abstract void setFileName(String text);

	public abstract int getOpened();

	public abstract String getResolution();

	public abstract String getAddedDate();


	/***************************************************************************
	 *                                                                         *
	 * Tag Management                                                          *
	 *                                                                         *
	 **************************************************************************/

	/**
	 * Gets the observable tags list. Changes to this list are reflected back to the model
	 * @return
	 */
	public abstract IObservableList<Tag> getTags();

	/**
	 * Creates the given tag
	 * @param name
	 * @return
	 */
	public abstract Tag createTag(String name);

	/**
	 * Gets all tags which are available for this media
	 * @return
	 */
	public abstract List<Tag> getAvailableTags();


	/***************************************************************************
	 *                                                                         *
	 * Persistence                                                             *
	 *                                                                         *
	 **************************************************************************/

	/**
	 * Persist the changes made to this media

	public abstract void persist();
	 */
}