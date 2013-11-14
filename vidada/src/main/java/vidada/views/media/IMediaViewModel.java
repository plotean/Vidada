package vidada.views.media;

import vidada.model.tags.Tag;
import archimedesJ.events.EventArgs;
import archimedesJ.events.IEvent;
import archimedesJ.swing.components.JMultiStateCheckBox.MultiCheckState;

/**
 * ViewModel for a media item
 * @author IsNull
 *
 */
public interface IMediaViewModel {

	/**
	 * Event occurs when the tags have been changed
	 * @return
	 */
	public abstract IEvent<EventArgs> getTagsChanged();

	public abstract MultiCheckState getState(Tag tag);

	public abstract void setState(Tag tag, MultiCheckState newState);

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

}