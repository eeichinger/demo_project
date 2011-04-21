import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.context.support.XmlWebApplicationContext;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Replace standard classpath resources with JBoss' VFSZIP protocol capable Resource implementation
 */
public class JBossXmlWebApplicationContext extends XmlWebApplicationContext {

	@Override
	public Resource getResource(String location) {
		Resource res = super.getResource(location);
		res = convertClassPathResourceIfNecessary(res);
		return res;
	}

	private Resource convertClassPathResourceIfNecessary(Resource res) {
		try {
			if (!res.exists()) return res;

			final URL url = res.getURL();
			if (url != null &&
					res instanceof ClassPathResource && url.getProtocol().equalsIgnoreCase("vfszip")) {
				logger.debug("found VFSZIP entry, creating VfsZipResource: " + url);
				res = new VfszipResource((ClassPathResource) res);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return res;
	}

	@Override
	public Resource[] getResources(String locationPattern) throws IOException {
		Resource[] resources = super.getResources(locationPattern);
		if (resources != null) {
			ArrayList<Resource> ress = new ArrayList<Resource>();
			for(Resource res : resources) {
				ress.add(convertClassPathResourceIfNecessary(res));
			}
			resources = ress.toArray(new Resource[0]);
		}
		return resources;
	}
}
