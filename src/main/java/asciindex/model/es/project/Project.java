package asciindex.model.es.project;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

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
	private List<String> version;
	@Field(index = FieldIndex.not_analyzed, type = FieldType.String)
	private String activeVersion;

	public Project() {
	}

	public Project(String name, List<String> version) {
		this.name = name;
		this.version = version;
	}

	public Project(String name, List<String> version, String activeVersion) {
		this.name = name;
		this.version = version;
		this.activeVersion = activeVersion;
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

	public List<String> getVersion() {
		return version;
	}

	public void setVersion(List<String> version) {
		this.version = version;
	}

	public void setActiveVersion(String activeVersion) {
		this.activeVersion = activeVersion;
	}

	public String getActiveVersion() {
		return activeVersion;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Project project = (Project) o;

		if (name != null ? !name.equals(project.name) : project.name != null) {
			return false;
		}
		if (version != null ? !version.equals(project.version) : project.version != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = name != null ? name.hashCode() : 0;
		result = 31 * result + (version != null ? version.hashCode() : 0);
		return result;
	}
}
