package vidada.dal.repositorys;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import vidada.aop.IUnitOfWorkService;
import vidada.dal.JPARepository;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaLibrary;
import vidada.model.media.source.MediaSource;
import vidada.model.media.source.MediaSourceLocal;
import vidada.server.dal.repositories.IMediaLibraryRepository;
import vidada.server.dal.repositories.IMediaRepository;
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
		getEntityManager().persist(library);
	}

	@Override
	public void update(MediaLibrary library) {
		getEntityManager().persist(library);
	}

	@Override
	public void delete(MediaLibrary library) {

		// Remove all Sources which used this library

		// Find all Sources which depend on this library
		TypedQuery<MediaSourceLocal> query = getEntityManager()
				.createQuery("SELECT s from MediaSourceLocal s WHERE s.parentLibrary = :library", MediaSourceLocal.class);
		query.setParameter("library", library);
		List<MediaSourceLocal> localSources = query.getResultList();

		//System.out.println("found " + localSources.size() + " local sources for library: " + library);

		// find all medias which have this library
		IMediaRepository mediaRepository = new MediaRepository(getUnitOfWorkService());
		List<MediaItem> mediaItems = mediaRepository.queryByLibrary(library);

		//System.out.println("found " + mediaItems.size() + " in library " + library);

		for (MediaItem mediaItem : mediaItems) {
			Set<MediaSource> sources = mediaItem.getSources();
			for (MediaSource source : sources) {
				if(source instanceof MediaSourceLocal){
					if(((MediaSourceLocal) source).getParentLibrary().equals(library)){
						mediaItem.removeSource(source);
					}
				}
			}

			// check if the media has any sources left
			if(mediaItem.getSources().isEmpty()){
				// The media has no sources left
				// TODO We can delete it now, but then we loose all general info
				// is this intended?
				mediaRepository.delete(mediaItem);
			}
		}

		// remove them from the medias

		for (MediaSourceLocal source : localSources) {
			getEntityManager().remove(source);
		}

		// Remove the library

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
