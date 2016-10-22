package asciindex;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author Alex
 * @since 11.10.2016
 */
@ConfigurationProperties(prefix = "asciindex")
@Component
public class Configuration {
	private String indexName;

	@PostConstruct
	public void init () {
		if(indexName == null || indexName.isEmpty()) {
			indexName = "documentation";
		}
	}

	public String getIndexName() {
		return indexName;
	}
}
