package asciindex.service.indexing;

import asciindex.model.indexing.ChapterInfo;
import org.junit.Test;
import reactor.core.publisher.Flux;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author Alex
 * @since 18.09.2016
 */
public class TextSplittingServiceTest {
	private TextSplittingService textSplittingService = new TextSplittingService();
	public static final String INDEXED_TEXT = new BufferedReader(new InputStreamReader(TextSplittingServiceTest.class.getResourceAsStream("index.html"))).lines().collect(Collectors.joining("\n"));

	@Test
	public void indexingWillSplitUpTextToChapters() {
		Flux<ChapterInfo> chapterInfo = textSplittingService.splitToChapters(INDEXED_TEXT, SplitLevel.H2, LevelMatch.STRICT);
		List<ChapterInfo> chapters = chapterInfo.toStream().collect(Collectors.toList());

		assertEquals(2, chapters.size());
		assertThat(chapters.get(0).title(), is("About"));
		assertThat(chapters.get(1).title(), is("Using asciindex"));
	}

	@Test
	public void eachChapterHasContentLines() {
		Flux<ChapterInfo> chapterInfo = textSplittingService.splitToChapters(INDEXED_TEXT, SplitLevel.H2, LevelMatch.STRICT);

		List<ChapterInfo> chapters = chapterInfo.toStream().collect(Collectors.toList());

		assertThat(
				chapters.get(0).text().collect(Collectors.toList()),
				is(Arrays.asList(
						"This project is intented to be use together with excelent documenting tool asciidoc",
						"It’s intended to fill the missing features of project documentation, such as:",
						"fulltext search",
						"version diff")));

		assertThat(
				chapters.get(1).text().collect(Collectors.toList()),
				is(Arrays.asList(
						"Asciindex provides simple REST API to interact with.",
						"Start by invoking",
						"#> http put :8080/index/spring-boot/1.4.0 url:http://docs.spring.io/spring-boot/docs/1.4.0.RELEASE/reference/htmlsingle/",
						"Note",
						"this example uses brilliant httpie command line tool, which can be found here",
						"Last updated 2016-09-18 15:32:51 +03:00"
				))
		);
	}
}
