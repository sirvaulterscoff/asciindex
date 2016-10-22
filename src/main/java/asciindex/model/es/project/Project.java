package asciindex.model.es.project;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author Alex
 * @since 11.10.2016
 */
@Document(indexName = "#{configuration.indexName}", type = Project.TYPE, shards = 1)
public class Project {
	public static final String TYPE = "project";
	@Id
	private String id;
	@Field(index = FieldIndex.not_analyzed, type = FieldType.String)
	private String name;
	@Field(index = FieldIndex.not_analyzed, type = FieldType.String)
	private String version;

	public Project() {
	}

	public Project(String name, String version) {
		this.name = name;
		this.version = version;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
