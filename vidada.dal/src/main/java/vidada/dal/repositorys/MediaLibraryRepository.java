package vidada.dal.repositorys;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import vidada.aop.IUnitOfWorkService;
import vidada.dal.JPARepository;
import vidada.model.media.MediaLibrary;
import vidada.server.dal.repositories.IMediaLibraryRepository;
import archimedesJ.io.locations.ResourceLocation;

public class MediaLibraryRepository extends JPARepository implements IMediaLibraryRepository {

	public MediaLibraryRepository(
			IUnitOfWorkService<EntityManager> unitOfWorkService) {
		super(unitOfWorkService);
	}

	@Override
	public List<MediaLibrary> getAllLibraries() {
		TypedQuery<MediaLibrary> query = getEntityManager().createQuery("SELECT l from MediaLibrary l", MediaLibrary.class);
		return query.getResultList();
	}

	@Override
	public void store(MediaLibrary library) {
		getEntityManager().getTransaction().begin();
		getEntityManager().persist(library);
		getEntityManager().getTransaction().commit();
	}

	@Override
	public void update(MediaLibrary library) {
		getEntityManager().getTransaction().begin();
		getEntityManager().persist(library);
		getEntityManager().getTransaction().commit();
	}

	@Override
	public void delete(MediaLibrary library) {
		getEntityManager().remove(library);
	}


	@Override
	public MediaLibrary queryById(long id) {
		TypedQuery<MediaLibrary> query = getEntityManager().createQuery("SELECT l from MediaLibrary l WHERE l.id =" + id, MediaLibrary.class);
		return firstOrDefault(query);
	}

	@Override
	public MediaLibrary queryByLocation(ResourceLocation file) {
		// TODO
		throw new NotImplementedException();
	}

}
