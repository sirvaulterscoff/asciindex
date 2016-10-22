package asciindex.model.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.MultiValueMap;

/**
 * @author Alex
 * @since 17.09.2016
 */
public class ResourceInfo  {
	private String link;

	public ResourceInfo(String link, String id) {
		this.link = String.format("%s/%s", link, id);
	}

	public String getLink() {
		return link;
	}
}
