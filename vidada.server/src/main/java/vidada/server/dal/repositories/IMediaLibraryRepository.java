package vidada.server.dal.repositories;

import archimedes.core.io.locations.ResourceLocation;
import vidada.model.media.MediaLibrary;

import java.util.List;


public interface IMediaLibraryRepository extends IRepository {

	public List<MediaLibrary> getAllLibraries();

	public void store(MediaLibrary library);

	public void delete(MediaLibrary library);

	public void update(MediaLibrary library);

	public MediaLibrary queryById(long id);

	public MediaLibrary queryByLocation(ResourceLocation file);

}
