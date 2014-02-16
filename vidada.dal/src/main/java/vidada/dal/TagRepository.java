package vidada.dal;

import java.util.Collection;
import java.util.List;

import javax.persistence.TypedQuery;

import vidada.model.media.store.libraries.MediaLibrary;
import vidada.model.tags.Tag;
import vidada.repositories.ITagRepository;

public class TagRepository extends JPARepository implements ITagRepository {

	@Override
	public void store(Tag newTag) {
		getEntityManager().persist(newTag);
	}

	@Override
	public void store(Collection<Tag> newTags) {
		for (Tag tag : newTags) {
			getEntityManager().persist(tag);
		}
	}

	@Override
	public void delete(Tag tag) {
		getEntityManager().remove(tag);
	}

	@Override
	public Collection<Tag> getAllUsedTags(Collection<MediaLibrary> libraries) {
		// TODO Auto-generated method stub
		return getAllTags();
	}

	@Override
	public List<Tag> getAllTags() {
		TypedQuery<Tag> allTagsQuery = getEntityManager().createQuery("SELECT t from Tag t", Tag.class);
		return allTagsQuery.getResultList();
	}

	@Override
	public void update(Tag tag) {
		getEntityManager().persist(tag);
	}

}
