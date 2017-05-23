package asciindex.model.es;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author Alex
 * @since 23.05.2017
 */
@Document(indexName = "#{configuration.indexName}", shards = 2, type = Source.TYPE_NAME)
public class Source {
	public static final String TYPE_NAME = "Source";
	private final String project;
	private final String version;
	@Field(index = FieldIndex.no, type = FieldType.String)
	private final String content;

	public Source(String project, String version, String content) {
		this.project = project;
		this.version = version;
		this.content = content;
	}

	public String getProject() {
		return project;
	}

	public String getVersion() {
		return version;
	}

	public String getContent() {
		return content;
	}
}
