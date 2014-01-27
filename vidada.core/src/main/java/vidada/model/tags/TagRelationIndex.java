package vidada.model.tags;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import archimedesJ.exceptions.NotSupportedException;

public class TagRelationIndex {

	transient private final Map<Tag, TagNode> rootNodes = new HashMap<Tag, TagNode>(5000);
	transient private final Map<TagNode, Set<Tag>> equalTags = new HashMap<TagNode, Set<Tag>>(5000);

	/**
	 * Add a relation between two tags
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
	 * Introduce an equality relation between two tags
	 * This will infact merge all the references of the two tags into the left one
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

		Set<Tag> masterEquals = equalTags.get(masterNode);
		masterEquals.add(slave);

		// Update the master-node with the now obsolete slave-node equality references
		Set<Tag> rightEquals = equalTags.get(slaveNode);
		if(rightEquals != null){
			masterEquals.addAll(rightEquals);

			// we can now remove the obsolete slave node
			equalTags.remove(slaveNode);
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

			Set<Tag> equals = new HashSet<Tag>();
			equals.add(tag);
			equalTags.put(node, equals);
		}
		return node;
	}

	/**
	 * Finds a matching Node for the given Tag
	 * @param tag
	 * @return May return <code>null</code> if no matching node exists
	 */
	private TagNode findNode(Tag tag){
		return rootNodes.get(tag);
	}


	/**
	 * Returns all tags which as synonyms to the given tag
	 * @param tag
	 * @return
	 */
	public Set<Tag> getAllEqualTags(Tag tag){
		TagNode node = getNode(tag);
		return equalTags.get(node);
	}

	/**
	 * Returns all tags which are hierarchical synonyms (specalisations)
	 * @param tag
	 * @return
	 */
	public Set<Tag> getSpecialisations(Tag tag){

		Set<Tag> hirarchical = new HashSet<Tag>();

		TagNode node = getNode(tag);

		for (TagNode child : node.getChildren()) {
			hirarchical.add(child.getTag());
			hirarchical.addAll(getSpecialisations(child.getTag()));
		}
		return hirarchical;
	}

	/**
	 * Returns all tags which are mutually related to the given tag
	 * @param tag
	 * @return
	 */
	public Set<Tag> getAllRelatedTags(Tag tag){
		Set<Tag> relatedTags = new HashSet<Tag>();

		relatedTags.addAll(getAllEqualTags(tag));

		relatedTags.addAll(getSpecialisations(tag));
		//relatedTags.add(tag);

		return relatedTags;
	}

	public Tag getMasterTag(Tag tag){
		return getNode(tag).getTag();
	}
}
