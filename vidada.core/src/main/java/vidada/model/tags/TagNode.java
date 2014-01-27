package vidada.model.tags;

import java.util.HashSet;
import java.util.Set;

public class TagNode {
	private final Tag tag;
	private final Set<TagNode> children = new HashSet<TagNode>();

	public TagNode(Tag tag){
		this.tag = tag;
	}

	public Tag getTag(){
		return tag;
	}

	public Set<TagNode> getChildren() {
		return children;
	}
}
