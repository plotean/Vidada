package vidada.server.dal.repositories;

import java.util.List;

import vidada.model.media.MediaLibrary;
import archimedesJ.io.locations.ResourceLocation;


public interface IMediaLibraryRepository extends IRepository {

	public List<MediaLibrary> getAllLibraries();

	public void store(MediaLibrary library);

	public void delete(MediaLibrary library);

	public void update(MediaLibrary library);

	public MediaLibrary queryById(long id);

	public MediaLibrary queryByLocation(ResourceLocation file);

}
