package vidada.model.tags;

import java.beans.Transient;
import java.util.HashSet;
import java.util.Set;

import vidada.model.ServiceProvider;
import vidada.model.entities.BaseEntity;
import archimedesJ.events.EventArgsG;
import archimedesJ.events.EventHandlerEx;
import archimedesJ.events.IEvent;

/**
 * Represents a single Tag
 * @author IsNull
 *
 */
public class Tag extends BaseEntity implements Comparable<Tag>{

	private String name;
	private Set<TagKeyoword> keyWords = new HashSet<TagKeyoword>();


	private transient EventHandlerEx<EventArgsG<TagKeyoword>> keywordAddedEvent = new EventHandlerEx<EventArgsG<TagKeyoword>>();
	private transient EventHandlerEx<EventArgsG<TagKeyoword>> keywordRemovedEvent = new EventHandlerEx<EventArgsG<TagKeyoword>>();


	public IEvent<EventArgsG<TagKeyoword>> getKeywordAddedEvent() { return keywordAddedEvent; }
	public IEvent<EventArgsG<TagKeyoword>> getKeywordRemovedEvent() { return keywordRemovedEvent; }


	public Tag(){ }

	public Tag(String name){
		setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<TagKeyoword> getKeyWords() {
		return keyWords;
	}

	protected void setKeyWords(Set<TagKeyoword> keyWords) {
		this.keyWords = keyWords;
	}

	/**
	 * Add the given string as new keyword to this Tag
	 * @param keywordName 
	 * @return returns the added keyword
	 */
	@Transient
	public TagKeyoword addKeyword(String keywordName){

		ITagService tagService = ServiceProvider.Resolve(ITagService.class);
		TagKeyoword keyoword = tagService.getTagKeyoword(keywordName);

		addKeyword(keyoword);

		return keyoword;
	}

	/**
	 * Add the given keyoword to this Tag
	 * @param keyoword
	 * @return
	 */
	@Transient
	protected void addKeyword(TagKeyoword keyoword){
		this.keyWords.add(keyoword);
		keywordAddedEvent.fireEvent(this, EventArgsG.build(keyoword));
	}


	/**
	 * Remove the given Keyword from this Tag
	 * @param keyoword
	 */
	@Transient
	public void removeKeyword(TagKeyoword keyoword){
		keyWords.remove(keyoword);
		keywordRemovedEvent.fireEvent(this, EventArgsG.build(keyoword));
	}


	@Override
	public String toString(){
		return getName();
	}


	@Override
	public int compareTo(Tag o) {
		if(o == null) return -1;
		return this.getName().compareTo(o.getName());
	}



}
