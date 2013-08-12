package vidada.model.tags;

import vidada.model.entities.BaseEntity;

/**
 * Represents a single keyword from a Tag
 * @author IsNull
 *
 */
public class TagKeyoword extends BaseEntity {

	private String keyword;


	public TagKeyoword(){}

	public TagKeyoword(String keyword){this.keyword = keyword;}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	/**
	 * Checks if the given text matches this keyword
	 * @param text
	 * @return
	 */
	public boolean isMatch(String text){
		return keyword.toLowerCase().equals(text.toLowerCase());
	}

	@Override
	public String toString(){
		return this.getKeyword();
	}
}
