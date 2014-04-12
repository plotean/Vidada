package maven.client.autoupdate;

import archimedes.core.util.OSValidator;
import maven.client.autoupdate.MavenVersion.VersionFormatException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MavenAutoUpdateClient {

	private static String MetaDataFile = "maven-metadata.xml";
	private static String Api = "RepositoryClient.php";

	private static String Action_Version_Latest = "VERSION_LATEST";
	private static String Action_Repository_Latest = "URI_REPOSITORY_LATEST";
	private static String Action_Binary = "URI_BINARY";
	private static String Action_Download = "DOWNLOAD";


	private final URI mavenRepository;
	private final String groupId;
	private final String artifactId;
	private final File mavenCache;

	private URI projectRepository;


	/**
	 * Creates a new MavenAutoUpdateClient
	 * 
	 * @param mavenRepository
	 * @param groupId
	 * @param artifactId
	 * @param mavenCache
	 */
	public MavenAutoUpdateClient(URI mavenRepository, String groupId, String artifactId, File mavenCache){
		this.mavenRepository = mavenRepository;
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.mavenCache = mavenCache;
        this.projectRepository = getProjectRepository(mavenRepository, groupId, artifactId);
	}

	/**
	 * Fetches the latest available version information.
	 * This method call blocks!
	 * 
	 * @return
	 */
	public synchronized MavenVersion fetchLatestVersion(){
		MavenVersion version = MavenVersion.INVLAID;
		URI path = getURI(Action_Version_Latest);
		String versionString = uriDownloadString(path);
		if(versionString != null){
            try {
                version = MavenVersion.parse(versionString);
            } catch (VersionFormatException e) {
                e.printStackTrace();
            }
        }
		return version;
	}

	/**
	 * Fetches the given version.
	 * This method call blocks!
	 * 
	 * @param version
	 * @return
	 */
	public synchronized File fetchUpdate(MavenVersion version){
		File updateFile = getUpdateFile(version);

		if(updateFile.exists()){
			return updateFile;
		}else {
			URI dlLinkGenerator = getURI(Action_Binary, version);
			String updateLocation = uriDownloadString(dlLinkGenerator);
			URI updateUri;
			try {
				updateUri = new URI(updateLocation);

				File tmpDownloadFile = getTempDownloadUpdateFile(version);
                System.out.println("Downloading " + updateUri + " to " + tmpDownloadFile);
                if(uriDownloadToFile(updateUri, tmpDownloadFile)){
					tmpDownloadFile.renameTo(updateFile);
				}
				return updateFile;
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public synchronized File fetchCachedUpdate(MavenVersion version){
		File updateFile = getUpdateFile(version);
		if(updateFile.exists())
			return updateFile;
		return null;
	}

	/**
	 * Returns all current cached update versions
	 * @return
	 */
	public List<MavenVersion> listCachedUpdates(){
		List<MavenVersion> cached = new ArrayList<MavenVersion>();

		File updatesCache = getUpdateCache();
		if(updatesCache.isDirectory()){
			for (File update : updatesCache.listFiles()) {
				String ext = FilenameUtils.getExtension(update.toString());
				if(ext.contains("update") ){
					try {
						cached.add(MavenVersion.parse(update.getName()));
					} catch (VersionFormatException e) {
						e.printStackTrace();
					}
				}
			}
		}

		return cached;
	}

	/**
	 * Returns the latest locally cached update.
	 * @return
	 */
	public MavenVersion getLatestCachedUpdate(){
		List<MavenVersion> cachedUpdates = listCachedUpdates();
		Collections.sort(cachedUpdates);
		Collections.reverse(cachedUpdates);
		return !cachedUpdates.isEmpty() ? cachedUpdates.get(0) : null;
	}




    private URI getProjectRepository(URI mavenRepository, String groupId, String artifactId){
        URI repository = null;
        try {
            repository = new URI(mavenRepository.toString() + "/" + groupId.replace(".","/") + "/" + artifactId);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return repository;
    }

	private File getUpdateFile(MavenVersion version){
		File updatesCache = getUpdateCache();
		updatesCache.mkdirs();
		return new File(updatesCache, version.toString() + ".update");
	}

	private File getUpdateCache(){
		return new File(mavenCache, "updates");
	}

	private File getTempDownloadUpdateFile(MavenVersion version){
		File updatesCache = getUpdateCache();
		updatesCache.mkdirs();
		return new File(updatesCache, version.toString() + ".tmp");
	}

	private boolean uriDownloadToFile(URI uri, File destination){
		try {
			org.apache.commons.io.FileUtils.copyURLToFile(uri.toURL(), destination);
			return true;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}


	/**
	 * Downloads the content of the given URI as string
	 * @param path
	 * @return
	 */
	private String uriDownloadString(URI path){
		String string = null;
		InputStream in = null;
		try {
            System.out.println("Maven Client: fetching " + path.toString());
            in = path.toURL().openStream();
			string = IOUtils.toString( in );
		} catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
			IOUtils.closeQuietly(in);
		}
		return string;
	}


	private URI getURI(String action){
		return getURI(action, null);
	}

	private URI getURI(String action, MavenVersion version){

		String osname = OSValidator.getPlatformName();

		UriBuilder builder = UriBuilder.fromUri(getMavenRepository())
				.path(Api)
				.queryParam("group", groupId)
				.queryParam("artifact", artifactId)
				.queryParam("action", action)
				.queryParam("platform", osname);

		if(version != null) builder.queryParam("version", version.toString());

		return builder.build();
	}

	private URI getMavenRepository(){
		return mavenRepository;
	}

	private URI getProjectRepository(){
		return projectRepository;
	}

}
