package vidada.model.tags.relations;

import vidada.model.tags.Tag;
import vidada.model.tags.TagFactory;

public class RelationTest {

	public static void main(String[] args){

		TagRelationDefinition rootDef = new TagRelationDefinition();

		Tag object = TagFactory.instance().createTag("object");

		Tag category = TagFactory.instance().createTag("category");

		Tag action = TagFactory.instance().createTag("action");
		Tag kaboom = TagFactory.instance().createTag("kaboom");

		Tag comedy = TagFactory.instance().createTag("comedy");

		Tag fruit = TagFactory.instance().createTag("fruit");
		Tag fegetable = TagFactory.instance().createTag("fegetable");

		Tag feggi = TagFactory.instance().createTag("feggi");

		Tag apple = TagFactory.instance().createTag("apple");

		TagRelationDefinition definition = new TagRelationDefinition();

		/*
		 */
		definition.addRelation(new TagRelation(action, TagRelationOperator.Equal, kaboom));
		definition.addRelation(new TagRelation(object, TagRelationOperator.IsParentOf, category));

		definition.addRelation(new TagRelation(category, TagRelationOperator.IsParentOf, action));
		definition.addRelation(new TagRelation(category, TagRelationOperator.IsParentOf, comedy));

		definition.addRelation(new TagRelation(object, TagRelationOperator.IsParentOf, fruit));
		definition.addRelation(new TagRelation(fruit, TagRelationOperator.IsParentOf, apple));
		definition.addRelation(new TagRelation(object, TagRelationOperator.IsParentOf, action));

		definition.addRelation(new TagRelation(fruit, TagRelationOperator.Equal, fegetable));

		definition.addRelation(new TagRelation(fegetable, TagRelationOperator.IsParentOf, feggi));

		//definition.addRelation(new TagRelation(Tag.create("object"), TagRelationOperator.IsParentOf, Tag.create("category")));

		//definition.addRelation(new TagRelation(action, TagRelationOperator.Equal, kaboom));
		//definition.addRelation(new TagRelation(object, TagRelationOperator.IsParentOf, action));



		definition.print();

		/* */
		System.out.println();
		System.out.println("Tests:");
		System.out.println(definition.getAllRelatedTags(Tag.create("fruit")));
		rootDef.merge(definition);
		System.out.println(rootDef.getAllRelatedTags(Tag.create("fruit")));

	}
}
