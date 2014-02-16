package vidada.dal;

import java.util.Collection;
import java.util.List;

import javax.persistence.TypedQuery;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaQuery;
import vidada.model.media.store.libraries.MediaLibrary;
import vidada.model.tags.Tag;
import vidada.repositories.IMediaRepository;
import archimedesJ.io.locations.ResourceLocation;

public class MediaRepository extends JPARepository implements IMediaRepository{

	@Override
	public List<MediaItem> query(MediaQuery qry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MediaItem> query(Collection<MediaLibrary> libraries) {
		// TODO 
		throw new NotImplementedException();
	}

	@Override
	public Collection<MediaItem> query(Tag tag) {
		// TODO
		throw new NotImplementedException();
	}

	@Override
	public void store(MediaItem mediadata) {
		getEntityManager().persist(mediadata);
	}

	@Override
	public void store(Iterable<MediaItem> mediadatas) {
		for (MediaItem mediaItem : mediadatas) {
			store(mediaItem);
		}
	}

	@Override
	public void delete(MediaItem mediadata) {
		getEntityManager().remove(mediadata);
	}

	@Override
	public void delete(Iterable<MediaItem> mediadatas) {
		for (MediaItem mediaItem : mediadatas) {
			delete(mediaItem);
		}
	}

	@Override
	public List<MediaItem> getAllMedias() {
		TypedQuery<MediaItem> query = getEntityManager().createQuery("SELECT l from MediaItem l", MediaItem.class);
		return query.getResultList();
	}


	@Override
	public void update(MediaItem media) {
		getEntityManager().merge(media);
	}

	@Override
	public void update(Iterable<MediaItem> mediadatas) {
		for (MediaItem media : mediadatas) {
			getEntityManager().merge(media);
		}
	}

	@Override
	public void removeAll() {
		// TODO
		throw new NotImplementedException();
	}

	@Override
	public MediaItem queryByHash(String hash) {
		// TODO
		throw new NotImplementedException();
	}

	@Override
	public MediaItem queryByPath(ResourceLocation file, MediaLibrary library) {
		// TODO
		throw new NotImplementedException();
	}

}
