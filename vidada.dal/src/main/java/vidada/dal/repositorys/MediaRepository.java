package vidada.dal.repositorys;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import vidada.aop.IUnitOfWorkService;
import vidada.dal.JPARepository;
import vidada.model.media.MediaItem;
import vidada.model.media.MediaLibrary;
import vidada.model.media.OrderProperty;
import vidada.model.media.source.MediaSource;
import vidada.model.pagination.ListPage;
import vidada.model.tags.Tag;
import vidada.server.dal.repositories.IMediaRepository;
import vidada.server.queries.MediaExpressionQuery;
import archimedesJ.exceptions.NotImplementedException;
import archimedesJ.io.locations.ResourceLocation;

public class MediaRepository extends JPARepository implements IMediaRepository{

	public MediaRepository(
			IUnitOfWorkService<EntityManager> unitOfWorkService) {
		super(unitOfWorkService);
	}


	@Override
	public ListPage<MediaItem> query(MediaExpressionQuery qry, int pageIndex, int maxPageSize) {
		long totalCount = queryCount(qry);

		TypedQuery<MediaItem> query = buildQuery(qry);		
		query.setMaxResults(maxPageSize);
		query.setFirstResult(pageIndex * maxPageSize);
		List<MediaItem> pageItems = query.getResultList();

		return new ListPage<MediaItem>(pageItems, totalCount, maxPageSize, pageIndex);
	}

	public long queryCount(MediaExpressionQuery qry){
		Query cQuery = getEntityManager().createQuery("SELECT count(m) from MediaItem m WHERE " + buildMediaWhereQuery(qry));
		setQueryParams(cQuery, qry);
		Number result = (Number) cQuery.getSingleResult();
		return result.longValue();
	}

	private TypedQuery<MediaItem> buildQuery(MediaExpressionQuery qry){

		String sQry = "SELECT m from MediaItem m WHERE " + buildMediaWhereQuery(qry) + " " + buildMediaOrderByQuery(qry);

		System.out.println(sQry);

		TypedQuery<MediaItem> q = getEntityManager()
				.createQuery(sQry, MediaItem.class);

		setQueryParams(q, qry);

		return q;
	}

	private void setQueryParams(Query q, MediaExpressionQuery qry){
		if(qry.hasKeyword()) q.setParameter("keywords", "%" + qry.getKeywords() + "%");
		if(qry.hasMediaType()) q.setParameter("type", qry.getMediaType());
	}

	private String buildMediaOrderByQuery(MediaExpressionQuery qry){
		String orderBy = ""; 

		OrderProperty order = qry.getOrder();
		if(!order.equals(OrderProperty.NONE)){

			String direction = "DESC";
			String reversedirection = "ASC";

			if(qry.isReverseOrder()){
				// Swap
				String tmp = direction;
				direction = reversedirection;
				reversedirection = tmp;
			}

			orderBy += "ORDER BY ";

			switch (order) {
			case FILENAME:
				// Ignore
				break;
			default:
				orderBy += "m." + order.getProperty() + " " + direction + ","; // By default desc order
			}

			orderBy += " m.filename " + reversedirection;  // file name is asc order by default
		}

		return orderBy;
	}

	private String buildMediaWhereQuery(MediaExpressionQuery qry){

		String where = ""; 

		if(qry.hasKeyword()){
			where += "(m.filename LIKE :keywords) AND ";
		}

		if(qry.hasMediaType()){
			where += "(m.type = :type) AND ";
		}

		String tagExpr =  qry.getTagsExpression().code();
		if(!tagExpr.trim().isEmpty()){
			where += tagExpr + " AND ";
		}

		where += "1=1";

		return where;
	}


	@Override
	public List<MediaItem> query(Collection<MediaLibrary> libraries) {
		// TODO 
		throw new NotImplementedException();
	}

	@Override
	public Collection<MediaItem> query(Tag tag) {
		String sQry = "SELECT m from vidada.model.media.MediaItem m WHERE '" + tag + "' MEMBER OF m.tags";
		TypedQuery<MediaItem> q = getEntityManager()
				.createQuery(sQry, MediaItem.class);
		return q.getResultList();
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

		MediaItem managedMedia = getEntityManager().find(MediaItem.class, media.getFilehash());

		media = media.clone();

		for (Tag t : new ArrayList<Tag>(media.getTags())) {
			media.getTags().remove(t);
			Tag tm = getEntityManager().merge(t);
			media.getTags().add(tm);
		}

		for (MediaSource s : media.getSources()) {
			media.removeSource(s);
			MediaSource sm = getEntityManager().merge(s);
			media.addSource(sm);
		}

		managedMedia.prototype(media);
	}

	@Override
	public void update(Iterable<MediaItem> mediadatas) {
		for (MediaItem media : mediadatas) {
			update(media);
		}
	}

	@Override
	public void removeAll() {
		// TODO
		throw new NotImplementedException();
	}

	@Override
	public MediaItem queryByHash(String hash) {
		return getEntityManager().find(MediaItem.class, hash);
	}

	@Override
	public MediaItem queryByPath(ResourceLocation file, MediaLibrary library) {
		// TODO
		throw new NotImplementedException();
	}


	@Override
	public int countAll() {
		Query cQuery = getEntityManager()
				.createQuery("SELECT count(m) from MediaItem m");
		Number result = (Number) cQuery.getSingleResult();
		return (int)result.longValue();
	}


	@Override
	public List<MediaItem> queryByLibrary(MediaLibrary library) {
		TypedQuery<MediaItem> query = getEntityManager()
				.createQuery("SELECT distinct m from MediaItem m inner join m.sources s where s.parentLibrary = :library", MediaItem.class);
		query.setParameter("library", library);

		return query.getResultList();
	}

}
