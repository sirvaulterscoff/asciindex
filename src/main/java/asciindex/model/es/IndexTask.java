package asciindex.model.es;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author Alex
 * @since 17.09.2016
 */
@Document(indexName = "#{configuration.indexName}", shards = 1, type = IndexTask.TYPE_NAME)
public class IndexTask {
	public static final String INDEX_NAME = "queue_index";
	public static final String TYPE_NAME = "queue";
	@Id
	private String id;
	@Field(index = FieldIndex.no, type = FieldType.String)
	private String content;
	private String project;
	private String version;
	private IndexTaskStatus status = IndexTaskStatus.CREATED;

	public IndexTask() {
	}

	public IndexTask(String project, String version, String content) {
		this.project = project;
		this.version = version;
		this.content = content;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public IndexTaskStatus getStatus() {
		return status;
	}

	public void setStatus(IndexTaskStatus status) {
		this.status = status;
	}
}
