package maven.client.autoupdate;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class MavenAutoUpdateClient {

	private static String MetaDataFile = "maven-metadata.xml";


	private URI projectRepository;


	public MavenAutoUpdateClient(URI mavenRepository, String groupId, String artifactId){
		try {
			projectRepository = new URI(mavenRepository.toString() + "/" + groupId + "/" + artifactId);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}


	public MavenVersion fetchLatestVersion(){
		MavenVersion version = MavenVersion.INVLAID;
		InputStream in = null;
		try {
			URI path = subPath(getProjectRepository(), MetaDataFile);
			in = path.toURL().openStream();
			String metaDataString = IOUtils.toString( in );

			// parse version info
			DocumentBuilderFactory domFactory = 
					DocumentBuilderFactory.newInstance();
			domFactory.setNamespaceAware(true); 
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			Document doc = builder.parse(metaDataString);
			XPath xpath = XPathFactory.newInstance().newXPath();
			// XPath Query for showing all nodes value
			XPathExpression expr = xpath.compile("//metadata/versioning/release/text()");

			String result = (String)expr.evaluate(doc, XPathConstants.STRING);
			version = MavenVersion.parse(result);

		} catch (IOException | ParserConfigurationException e1) { 
			e1.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
		}

		return version;
	}

	private static URI subPath(URI base, String sub) {
		try {
			return new URI(base.toString() + "/" + sub);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	private URI getProjectRepository(){
		return projectRepository;
	}

}
