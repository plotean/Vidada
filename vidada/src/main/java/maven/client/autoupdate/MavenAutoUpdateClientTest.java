package maven.client.autoupdate;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by IsNull on 05.04.14.
 */
public class MavenAutoUpdateClientTest {

    private static final Logger logger = LogManager.getLogger(MavenAutoUpdateClientTest.class.getName());


    public static void main(String[] args) {

        final String repository = "http://dl.securityvision.ch/maven";
        final String groupId = "ch.securityvision.vidada";
        final String artifactId = "Vidada";

        try {
            MavenAutoUpdateClient client = new MavenAutoUpdateClient(
                    new URI(repository),
                    groupId,
                    artifactId,
                    new File("./mavenCache"));
            logger.info("fetching latest version...");
            MavenVersion version = client.fetchLatestVersion();

            if(version.isValid()) {
                logger.info("version: " + version);
            }else{
                logger.info("version could not be fetched. (either a network/server issue or you specified wrong group/artifact id)");
            }

        } catch (URISyntaxException e) {
            logger.error(e);
        }
    }

}
