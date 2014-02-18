package vidada.dal.repositorys;

import java.util.Collection;
import java.util.List;

import javax.persistence.TypedQuery;

import vidada.dal.JPARepository;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaQuery;
import vidada.model.media.store.libraries.MediaLibrary;
import vidada.model.tags.Tag;
import vidada.repositories.IMediaRepository;
import archimedesJ.exceptions.NotImplementedException;
import archimedesJ.io.locations.ResourceLocation;

public class MediaRepository extends JPARepository implements IMediaRepository{

	@Override
	public List<MediaItem> query(MediaQuery qry) {

		String sQry = "SELECT m from vidada.model.media.MediaItem m WHERE ";


		if(qry.hasKeyword()){
			sQry += "(m.filename LIKE :keywords) AND ";
		}

		if(qry.hasMediaType()){
			sQry += "(m.type = :type) AND ";
		}


		sQry += "1=1";
		TypedQuery<MediaItem> q = getEntityManager()
				.createQuery(sQry, MediaItem.class);


		if(qry.hasKeyword()) q.setParameter("keywords", "%" + qry.getKeywords() + "%");
		if(qry.hasMediaType()) q.setParameter("type", qry.getSelectedtype());


		return q.getResultList();
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
		TypedQuery<MediaItem> query = getEntityManager().createQuery("SELECT m from MediaItem m", MediaItem.class);
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
		TypedQuery<MediaItem> query = getEntityManager().createQuery("SELECT m from MediaItem m WHERE m.fileHash = '" + hash + "'", MediaItem.class);
		return firstOrDefault(query);
	}

	@Override
	public MediaItem queryByPath(ResourceLocation file, MediaLibrary library) {
		// TODO
		throw new NotImplementedException();
	}

}
