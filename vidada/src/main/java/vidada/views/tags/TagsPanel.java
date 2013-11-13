package vidada.views.tags;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import vidada.model.tags.Tag;
import archimedesJ.events.EventArgs;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.swing.components.JMultiStateCheckBox;
import archimedesJ.swing.components.JMultiStateCheckBox.MultiCheckState;
import archimedesJ.swing.layouts.WrapLayout;

/**
 * Represents a Tags Panel
 * 
 * 
 * @author IsNull
 *
 */
@SuppressWarnings("serial")
public class TagsPanel extends JPanel { //  implements ITagsView

	/**
	 * Event fired when the selected Tags have been changed
	 */
	public EventHandlerEx<EventArgs> TagStateChangedEvent = new EventHandlerEx<EventArgs>();

	private Map<Tag, TagCheckBox> tagToViewMap = new HashMap<Tag, TagCheckBox>();


	/**
	 * A simple checkbox wrapper for Tags 
	 */
	private class TagCheckBox extends JMultiStateCheckBox{

		private final Tag tag;

		public TagCheckBox(Tag tag){
			super();
			this.tag = tag;

			updateTagInfo();
		}

		public Tag getTag(){
			return this.tag;
		}

		protected void updateTagInfo(){
			this.setText(tag.getName());
		}

		@Override
		public String toString(){
			return getTag().toString();
		}
	}

	/**
	 * Creates a new Tag Panel
	 */
	public TagsPanel(){ 
		WrapLayout wrapLayout = new WrapLayout();
		wrapLayout.setAlignment(FlowLayout.LEFT);
		wrapLayout.setAlignOnBaseline(true);
		this.setLayout(wrapLayout);
	}


	public void addTags(Iterable<Tag> tags)
	{
		for (Tag tag : tags) {
			addTagInternal(tag, false);
		}
		update();
	}

	public void clearTags(){
		List<Tag> tags = getAllTags();
		for (Tag tag : tags) {
			removeTagInternal(tag, false);
		}
	}

	/**
	 * Adds a new Tag to this panel
	 * @param tag
	 */
	public void addTag(Tag tag){
		addTagInternal(tag, true);	
	}


	protected void addTagInternal(Tag tag, boolean invalidate) {
		TagCheckBox cBox = new TagCheckBox(tag);
		this.add(cBox);
		cBox.addActionListener(tagActionListener);

		tagToViewMap.put(tag, cBox);

		if(invalidate)
			update();
	}

	/**
	 * Removes the given Tag from the panel
	 * @param tag
	 */
	public void removeTag(Tag tag){
		removeTagInternal(tag, true);
	}


	protected void removeTagInternal(Tag tag, boolean invalidate) {

		for (java.awt.Component comp : this.getComponents()) {
			if(comp instanceof TagCheckBox)
			{
				if(((TagCheckBox)comp).getTag().equals(tag))
				{
					this.remove(comp);
					tagToViewMap.remove(tag);
				}
			}
		}

		if(invalidate)
			update();
	}

	/**
	 * Occurs when a TagCheckBox has been Checked/UnChecked
	 */
	private ActionListener tagActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			TagCheckBox checkBox = (TagCheckBox)e.getSource();
			System.out.println("user changed tag " + checkBox);
			TagStateChangedEvent.fireEvent(checkBox, EventArgs.Empty);
		}
	};




	/**
	 * Get the state of this Tag
	 * @param tag
	 * @param state
	 */
	public MultiCheckState getTagState(Tag tag){
		return getCheckBox(tag).getState();
	}


	/**
	 * Set the state of this Tag
	 * @param tag
	 * @param state
	 */
	public void setTagState(Tag tag, MultiCheckState state){
		getCheckBox(tag).setState(state);
	}

	/**
	 * Set the given state to all Tags
	 * @param state
	 */
	public void setAllTagsTo(MultiCheckState state){

		for (java.awt.Component comp : this.getComponents()) {
			if(comp instanceof TagCheckBox)
			{
				TagCheckBox checkBox = ((TagCheckBox)comp);
				checkBox.setState(state);
			}
		}
	}

	/**
	 * Set the given state to all Tags which match the tagsFilter
	 * @param state
	 * @param tagsFilter
	 */
	public void setTagsState(MultiCheckState state, Collection<Tag> tagsFilter){

		for (java.awt.Component comp : this.getComponents()) {
			if(comp instanceof TagCheckBox)
			{
				TagCheckBox checkBox = ((TagCheckBox)comp);

				if(tagsFilter.contains(checkBox.getTag()))
				{
					checkBox.setState(state);
				}
			}
		}
	}

	/**
	 * Returns all Tags which have been checked
	 * @return
	 */
	public List<Tag> getTagsWithState(MultiCheckState filterState){

		List<Tag> tags = new ArrayList<Tag>();

		for (TagCheckBox chk : tagToViewMap.values()) {
			if(chk.getState() == filterState)
			{
				tags.add(chk.getTag());
			}
		}
		return tags;
	}

	/**
	 * Gets all Tags which are currently in this TagPanel
	 * @return
	 */
	public List<Tag> getAllTags(){

		List<Tag> tags = new ArrayList<Tag>();

		for (TagCheckBox chk : tagToViewMap.values()) {
			tags.add(chk.getTag());
		}
		return tags;
	}

	@Override
	public void setEnabled(boolean enabled) {
		for (java.awt.Component comp : this.getComponents()) {
			comp.setEnabled(enabled);
		}
		super.setEnabled(enabled);
	}


	private void update(){
		this.invalidate();
		this.validate();
		this.repaint();
	}

	private TagCheckBox getCheckBox(Tag tag){
		if(tagToViewMap.containsKey(tag))
			return tagToViewMap.get(tag);

		return null;
	}

}
