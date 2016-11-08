package asciindex.model.indexing;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.stream.Stream;

/**
 * @author Alex
 * @since 18.09.2016
 */
public class ChapterInfo {
	//	@Id
	private String id;
	private String projectRef;
	@Field(type = FieldType.Object)
	private ChapterTitle chapterTitle;
	@Field(type = FieldType.Object)
	private ChapterBody chapterBody;

	public ChapterInfo() {
	}

	public ChapterInfo(ChapterTitle chapterTitle, ChapterBody chapterBody) {

		this.chapterTitle = chapterTitle;
		this.chapterBody = chapterBody;
	}

	public String title() {
		return chapterTitle.text();
	}

	public Stream<String> text() {
		return chapterBody.text();
	}

	public ChapterTitle getChapterTitle() {
		return chapterTitle;
	}

	public ChapterBody getChapterBody() {
		return chapterBody;
	}

	public void setChapterTitle(ChapterTitle chapterTitle) {
		this.chapterTitle = chapterTitle;
	}

	public void setChapterBody(ChapterBody chapterBody) {
		this.chapterBody = chapterBody;
	}
}
