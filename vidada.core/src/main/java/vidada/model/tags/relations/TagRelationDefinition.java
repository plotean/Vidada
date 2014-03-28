package vidada.model.tags.relations;

import vidada.model.tags.Tag;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Represents all tag-relations in memory
 * 
 * @author IsNull
 *
 */
public class TagRelationDefinition {


	/***************************************************************************
	 *                                                                         *
	 * Private fields                                                          *
	 *                                                                         *
	 **************************************************************************/


	private Set<TagRelation> relations = new HashSet<TagRelation>();
	private Map<TagRelationOperator, Set<TagRelation>> operatorCluster = new HashMap<TagRelationOperator, Set<TagRelation>>();
	private Map<String, Set<Tag>> namedGroups = new HashMap<String, Set<Tag>>();


	transient private TagRelationIndex relationIndex = null;

	/***************************************************************************
	 *                                                                         *
	 * Constructor                                                             *
	 *                                                                         *
	 **************************************************************************/

	/**
	 * Creates an empty relation definition
	 */
	public TagRelationDefinition(){

	}


	/***************************************************************************
	 *                                                                         *
	 * Public API                                                              *
	 *                                                                         *
	 **************************************************************************/

	/**
	 * 
	 * @param name If name is left null, the ANONYMOUS group will be used.
	 * @param tags
	 */
	public void addNamedGroupTags(String name, Set<Tag> tags){
		if(name == null) name = "ANONYMOUS";
		Set<Tag> group = namedGroups.get(name);
		if(group == null){
			group = new HashSet<Tag>();
			namedGroups.put(name, group);
		}
		group.addAll(tags);
	}

	public void addRelation(TagRelation relation){
		relations.add(relation);
		getOperatorRelations(relation.getOperator()).add(relation);

		if(relationIndex != null){
			relationIndex.addRelation(relation);
		}
	}

	public void removeRelation(TagRelation relation){
		relations.remove(relation);
		getOperatorRelations(relation.getOperator()).remove(relation);

		relationIndex = null; // relation index is dirty
	}


	public Set<Tag> getAllRelatedTags(Tag tag){
		return getIndex().getAllRelatedTags(tag);
	}

	public TagRelationIndex getIndex(){
		if(relationIndex == null){
			relationIndex = buildTagRelationIndex();
		}
		return relationIndex;
	}

	public void merge(TagRelationDefinition relationDef) {
		relationIndex = null;
		for (TagRelation relation : relationDef.relations) {
			this.addRelation(relation);
		} 
		for (Entry<String, Set<Tag>> namedGroup : relationDef.namedGroups.entrySet()) {
			this.addNamedGroupTags(namedGroup.getKey(), namedGroup.getValue());
		}
	}

	/**
	 * Returns all tags in this definition
	 * @return
	 */
	public Set<Tag> getAllTags() {
		Set<Tag> allTags = new HashSet<Tag>();

		for (TagRelation relation : relations) {
			allTags.add(relation.getLeft());
			allTags.add(relation.getRight());
		}
		for (Set<Tag> tagGroup : namedGroups.values()) {
			allTags.addAll(tagGroup);
		}
		return allTags;
	}

	public void print(){
		getIndex().print();
	}

	@Override
	public String toString(){
		String str = "";
		for (Tag t : getAllTags()) {
			str += t + ", ";
		}
		return str;
	}


	/***************************************************************************
	 *                                                                         *
	 * Private methods                                                         *
	 *                                                                         *
	 **************************************************************************/

	private Set<TagRelation> getOperatorRelations(TagRelationOperator operator){
		Set<TagRelation> oprels = operatorCluster.get(operator);
		if(oprels == null){
			oprels = new HashSet<TagRelation>();
			operatorCluster.put(operator, oprels);
		}
		return oprels;
	}

	private TagRelationIndex buildTagRelationIndex(){
		System.out.println("building tag relation index...");
		TagRelationIndex index = new TagRelationIndex();

		Set<TagRelation> equalRelations = getOperatorRelations(TagRelationOperator.Equal);
		for (TagRelation tagRelation : equalRelations) {
			index.addRelation(tagRelation);
		}

		Set<TagRelation> hirarchyRelations = getOperatorRelations(TagRelationOperator.IsParentOf);
		for (TagRelation tagRelation : hirarchyRelations) {
			index.addRelation(tagRelation);
		}

		return index;
	}

}
