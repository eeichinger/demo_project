import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Dont use File path optimizations in ClassPathResource for vfszip:// resources
 */
public class VfszipResource extends ClassPathResource {

	public VfszipResource(ClassPathResource resource) {
		super(resource.getPath(), resource.getClassLoader());
	}

	@Override
	public boolean isReadable() {
		return true;
	}

	@Override
	public long contentLength() throws IOException {
		URL url = getURL();
		// Try a URL connection content-length header...
		URLConnection con = url.openConnection();
		con.setUseCaches(false);
		if (con instanceof HttpURLConnection) {
			((HttpURLConnection) con).setRequestMethod("HEAD");
		}
		return con.getContentLength();
	}

	@Override
	public long lastModified() throws IOException {
		URL url = getURL();
		// Try a URL connection last-modified header...
		URLConnection con = url.openConnection();
		con.setUseCaches(false);
		if (con instanceof HttpURLConnection) {
			((HttpURLConnection) con).setRequestMethod("HEAD");
		}
		return con.getLastModified();
	}

	@Override
	public Resource createRelative(String relativePath) {
		return new VfszipResource((ClassPathResource) super.createRelative(relativePath));
	}
}
