package vidada.model.tags.relations;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import vidada.model.tags.Tag;
import archimedesJ.exceptions.NotSupportedException;
import archimedesJ.util.Lists;

/**
 * Implementation of a Tag Relation Index.
 * 
 * Tags can have a hierarchical relation towards each other, therefore
 * this implementation uses a Tree Structure for the Tag-Nodes.
 * 
 * 
 * @author IsNull
 *
 */
public class TagRelationIndex {

	transient private final Map<Tag, TagNode> rootNodes = new HashMap<Tag, TagNode>(5000);
	//transient private final Map<TagNode, Set<Tag>> equalTags = new HashMap<TagNode, Set<Tag>>(5000);


	/***************************************************************************
	 *                                                                         *
	 * Public API                                                              *
	 *                                                                         *
	 **************************************************************************/



	/**
	 * Add a relation between two tags. 
	 * This will update this index accordingly.
	 * 
	 * @param left
	 * @param relation
	 * @param right
	 */
	public void addRelation(TagRelation relation){
		switch (relation.getOperator()) {
		case Equal:
			setEqualityRelation(relation.getLeft(), relation.getRight());
			break;

		case IsParentOf:
			setParentRelation(relation.getLeft(), relation.getRight());
			break;

		default:
			throw new NotSupportedException("Unknown relation: " + relation);
		}
	}

	/**
	 * Returns all tags which are mutually related to the given tag.
	 * I.e. it will tags with synonymous meaning or tags
	 * which are specalisations of the given tag.
	 * 
	 * @param tag
	 * @return
	 */
	public Set<Tag> getAllRelatedTags(Tag tag){
		Set<Tag> relatedTags = new HashSet<Tag>();

		relatedTags.addAll(getAllEqualTags(tag));

		relatedTags.addAll(getSpecialisations(tag));

		relatedTags.add(tag);

		return relatedTags;
	}

	/**
	 * Returns all tags which as synonyms to the given tag
	 * @param tag
	 * @return
	 */
	private Set<Tag> getAllEqualTags(Tag tag){
		TagNode node = findNode(tag);
		return node != null ? node.getSynonyms() : new HashSet<Tag>();
	}

	/**
	 * Returns all tags which are hierarchical synonyms (specalisations)
	 * @param tag
	 * @return
	 */
	private Set<Tag> getSpecialisations(Tag tag){

		Set<Tag> hirarchical = new HashSet<Tag>();

		TagNode node = findNode(tag);

		if(node != null){
			for (TagNode child : node.getChildren()) {
				hirarchical.add(child.getTag());
				hirarchical.addAll(getSpecialisations(child.getTag()));
			}
		}

		return hirarchical;
	}



	public Tag getMasterTag(Tag tag){
		TagNode node = findNode(tag);
		return node != null ? node.getTag() : tag;
	}

	/**
	 * Checks if the given tag is a slave tag
	 * @param tag
	 * @return
	 */
	public boolean isSlaveTag(Tag tag){
		TagNode node = findNode(tag);
		return node != null ? !node.getTag().equals(tag) : false;
	}


	public void print(){
		Set<TagNode> addedNodes = new HashSet<TagNode>();

		System.out.println("-------------- "+ rootNodes.size() +" ------------- ");
		for (Tag tag : rootNodes.keySet()) {
			TagNode node = findNode(tag);
			if(addedNodes.add(node)){ // ensure we print each node max once
				node.print();
			}
		} 
		System.out.println("------------------------------");
	}

	/***************************************************************************
	 *                                                                         *
	 * Private methods                                                         *
	 *                                                                         *
	 **************************************************************************/

	/**
	 * Introduce an equality relation between two tags
	 * This will in fact merge all the references of the two tags into the left one
	 * @param left
	 * @param right
	 */
	private void setEqualityRelation(Tag master, Tag slave){
		TagNode masterNode = getNode(master);
		TagNode slaveNode = findNode(slave);

		if(slaveNode != null && !slaveNode.getChildren().isEmpty()){
			mergeInto(masterNode, slaveNode);
		}
		rootNodes.put(slave, masterNode);
		masterNode.addSynonym(slave);

		if(slaveNode != null){
			// Update the master-node with the now obsolete slave-node equality references
			Set<Tag> rightEquals = slaveNode.getSynonyms();
			if(rightEquals != null){
				masterNode.addSynonyms(rightEquals);
			}
		}
	}

	/**
	 * Merge the slave node into the master node
	 * @param master
	 * @param slave
	 */
	private void mergeInto(TagNode master, TagNode slave){
		Set<TagNode> masterChildren = master.getChildren();
		masterChildren.addAll(slave.getChildren());
	}

	private void setParentRelation(Tag parent, Tag child){
		TagNode parentNode = getNode(parent);
		TagNode childNode = getNode(child);
		parentNode.getChildren().add(childNode);
	}

	/**
	 * Gets a TagNode for the given tag.
	 * This will create a new Node if no matching node is available
	 * @param tag
	 * @return
	 */
	private TagNode getNode(Tag tag){
		TagNode node = findNode(tag);
		if(node == null){
			node = new TagNode(tag);
			rootNodes.put(tag, node);
		}
		return node;
	}

	/**
	 * Finds a matching Node for the given Tag, but does not 
	 * create a new Node.
	 * @param tag
	 * @return May return <code>null</code> if no matching {@link TagNode} exists
	 */
	private TagNode findNode(Tag tag){
		return rootNodes.get(tag);
	}


	/***************************************************************************
	 *                                                                         *
	 * Inner classes                                                           *
	 *                                                                         *
	 **************************************************************************/

	/**
	 * Tree-Node wrapper for a Tag
	 * @author IsNull
	 *
	 */
	private class TagNode {
		private final Tag tag;
		private final Set<TagNode> children = new HashSet<TagNode>();
		private final Set<Tag> synonyms = new HashSet<Tag>();

		public TagNode(Tag tag){
			this.tag = tag;
			this.addSynonym(tag);
		}

		public Set<Tag> getSynonyms() {
			return synonyms;
		}

		public Tag getTag(){
			return tag;
		}

		public Set<TagNode> getChildren() {
			return children;
		}

		public void addSynonym(Tag tag){
			synonyms.add(tag);
		}

		public void addSynonyms(Collection<Tag> synonyms) {
			synonyms.addAll(synonyms);
		}

		public void print() {
			print("", true);
		}

		@Override
		public String toString(){
			return tag.getName();
		}

		private void print(String prefix, boolean isTail) {
			System.out.println(prefix + (isTail ? "└── " : "├── ") + toString() + equalTags(tag));

			List<TagNode> chr = Lists.toList(children);

			for (int i = 0; i < children.size() - 1; i++) {
				chr.get(i).print(prefix + (isTail ? "    " : "│   "), false);
			}

			if (chr.size() >= 1) {
				chr.get(children.size() - 1).print(prefix + (isTail ?"    " : "│   "), true);
			}
		}

		private String equalTags(Tag tag){
			String str = "";
			Set<Tag> equals = getAllEqualTags(tag);
			if(equals != null){
				for (Tag t : equals) {
					if(!t.equals(tag))
						str += " = " + t;
				};
			}
			return str;
		}
	}
}
