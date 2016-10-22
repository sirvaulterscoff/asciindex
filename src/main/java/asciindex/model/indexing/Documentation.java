package asciindex.model.indexing;

import asciindex.model.es.IndexTask;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.CompletionField;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

/**
 * @author Alex
 * @since 22.09.2016
 */
@Document(indexName = "#{configuration.indexName}", type = "documentation")
public class Documentation {
	@Id
	private String id;
	@CompletionField
	private String project;
	@CompletionField
	private String version;
	@Field(type = FieldType.Nested, index = FieldIndex.analyzed)
	private List<ChapterInfo> chapters;

	public Documentation() {
	}

	public Documentation(String project, String version) {
		this.project = project;
		this.version = version;
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

	public List<ChapterInfo> getChapters() {
		return chapters;
	}

	public ChapterInfo chapter(int i) {
		return chapters.get(i);
	}

	public void setChapters(List<ChapterInfo> chapters) {
		this.chapters = chapters;
	}
}
