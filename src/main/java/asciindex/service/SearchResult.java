package asciindex.service;

import asciindex.model.indexing.ChapterInfo;
import asciindex.model.indexing.Documentation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.stream.Stream;

/**
 * @author Alex
 * @since 07.11.2016
 */
@Document(indexName = "#{configuration.indexName}", type = Documentation.TYPE)
public class SearchResult {
	private String title;
	private final Stream<String> text;

	public SearchResult(String title, Stream<String> text) {
		this.title = title;
		this.text = text;
	}

	public String title() {
		return title;
	}

	public static SearchResult fromChapter(ChapterInfo chapterInfo) {
		return new SearchResult(chapterInfo.getChapterTitle().getText(), chapterInfo.getChapterBody().text());
	}
}
