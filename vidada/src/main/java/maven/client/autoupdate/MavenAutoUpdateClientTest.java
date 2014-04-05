package maven.client.autoupdate;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by IsNull on 05.04.14.
 */
public class MavenAutoUpdateClientTest {

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
            System.out.println("fetching latest version...");
            MavenVersion version = client.fetchLatestVersion();

            if(version.isValid()) {
                System.out.println("version: " + version);
            }else{
                System.out.println("version could not be fetched. (either a network/server issue or you specified wrong group/artifact id)");
            }

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

}
