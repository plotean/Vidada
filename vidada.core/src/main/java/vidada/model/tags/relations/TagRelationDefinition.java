package vidada.model.tags.relations;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import vidada.model.tags.Tag;


public class TagRelationDefinition {

	private Set<TagRelation> relations = new HashSet<TagRelation>();
	private Map<TagRelationOperator, Set<TagRelation>> operatorCluster = new HashMap<TagRelationOperator, Set<TagRelation>>();


	transient private TagRelationIndex relationIndex = null;

	public TagRelationDefinition(){

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

	private Set<TagRelation> getOperatorRelations(TagRelationOperator operator){
		Set<TagRelation> oprels = operatorCluster.get(operator);
		if(oprels == null){
			oprels = new HashSet<TagRelation>();
			operatorCluster.put(operator, oprels);
		}
		return oprels;
	}

	private TagRelationIndex buildTagRelationIndex(){
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
