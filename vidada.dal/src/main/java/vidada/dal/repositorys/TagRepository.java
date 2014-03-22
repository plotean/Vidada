package vidada.dal.repositorys;

import archimedesJ.aop.IUnitOfWorkService;
import vidada.dal.JPARepository;
import vidada.model.tags.Tag;
import vidada.server.dal.repositories.ITagRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.List;

public class TagRepository extends JPARepository implements ITagRepository {

	public TagRepository(IUnitOfWorkService<EntityManager> unitOfWorkService) {
		super(unitOfWorkService);
	}

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
	public List<Tag> getAllTags() {
		TypedQuery<Tag> allTagsQuery = getEntityManager().createQuery("SELECT t from Tag t", Tag.class);
		return allTagsQuery.getResultList();
	}

	@Override
	public void update(Tag tag) {
		getEntityManager().persist(tag);
	}

	@Override
	public Tag queryById(String tagName) {
		return getEntityManager().find(Tag.class, tagName);
	}

}
