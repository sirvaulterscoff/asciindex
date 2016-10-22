package asciindex.service.indexing;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Alex
 * @since 21.09.2016
 */
public class TextProcessorTest {
	public static final String TEXT = "Each <b>HTML</b> tag should be\n" +
	                                  "removed <a href=\"each\">from</a> each line";
	public static final String EXPECTED_TEXT = TEXT
			.replaceAll("<b>", "")
			.replaceAll("</b>", "")
			.replaceAll("<a href=\"each\">", "")
			.replaceAll("</a>", "");

	@Test
	public void removesHtmlTagsFromSourceText() {
		TextProcessor textProcessor = new TextProcessor();

		Stream<String> result = textProcessor.processText(Stream.of(TEXT.split("\\n")));
		Assert.assertThat(result.collect(Collectors.toList()).toArray(), CoreMatchers.is(EXPECTED_TEXT.split("\\n")));
	}

}