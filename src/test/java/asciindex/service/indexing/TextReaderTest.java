package asciindex.service.indexing;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * @author Alex
 * @since 18.09.2016
 */
public class TextReaderTest {
	public static final String INDEXED_TEXT = "<h1>TextReader</h1> will search\n" +
	                                          "for the first <h2>Tag</h2>\n" +
	                                          "After the h2 text comes chapter text\n" +
	                                          "then <H2>Another chapter</h2>\n" +
	                                          "And text for chapter2, with <h3>Some subHeading</h3>" +
	                                          "<h2>Last chapter</h2>" +
	                                          "We don't expect any tags beyond this point";
	public static final String HTEXT = "<h1>Title1</h1>" +
	                                   "Title1 text" +
	                                   "<h2>Title2</h2>" +
	                                   "Title2 text" +
	                                   "<h3>Title3</h3>" +
	                                   "Title3 text" +
	                                   "<h2>Title2_1</h2>" +
	                                   "End";

	@Test
	public void advanceToFirstChangedThePositionInTextReaderToFirstMatchingH2Tag() throws Exception {
		TextReader textReader = new TextReader(INDEXED_TEXT, SplitLevel.H2, LevelMatch.STRICT);
		assertTrue(textReader.advanceToFirst());

		assertThat(textReader.title(), is("Tag"));
	}


	@Test
	public void returnsTextForChapter() {
		TextReader textReader = new TextReader(INDEXED_TEXT, SplitLevel.H2, LevelMatch.STRICT);

		assertTrue(textReader.advanceToFirst());
		assertThat(textReader.text().collect(Collectors.toList()), is(asList("After the h2 text comes chapter text", "then ")));
	}

	@Test
	public void returnsTextWithNestedTags() {
		final TextReader textReader = new TextReader(INDEXED_TEXT, SplitLevel.H2, LevelMatch.STRICT);
		textReader.advanceToFirst();
		boolean hasNext = textReader.advanceToNext();

		assertThat(textReader.title(), is("Another chapter"));
		List<String> nestedText = textReader.text().collect(Collectors.toList());
		assertThat(nestedText, is(singletonList("And text for chapter2, with <h3>Some subHeading</h3>")));
	}

	@Test
	public void advanceToNextWillIterateThroughTags()  {
		TextReader reader = new TextReader(INDEXED_TEXT, SplitLevel.H2, LevelMatch.STRICT);

		assertTrue(reader.advanceToFirst());
		assertThat(reader.title(), is("Tag"));
		assertTrue(reader.advanceToNext());
		assertThat(reader.title(), is("Another chapter"));
		assertTrue(reader.advanceToNext());
		assertThat(reader.title(), is("Last chapter"));
		assertFalse(reader.advanceToNext());
	}


	@Test
	public void testReadingUpWillIncludeTextFromH3IntoH2() {
		TextReader textReader = new TextReader(HTEXT, SplitLevel.H2, LevelMatch.UP);

		assertTrue(textReader.advanceToFirst());
		assertThat(textReader.title(), is("Title1"));
		assertThat(textReader.text().collect(Collectors.toList()), is(singletonList("Title1 text")));

		textReader.advanceToNext();
		assertThat(textReader.title(), is("Title2"));
		assertThat(textReader.text().collect(Collectors.toList()), is(singletonList("Title2 text<h3>Title3</h3>Title3 text")));

		textReader.advanceToNext();
		assertThat(textReader.title(), is("Title2_1"));
		assertThat(textReader.text().collect(Collectors.toList()), is(singletonList("End")));

		assertFalse(textReader.advanceToNext());
	}

	@Test
	public void testReadingDownWillNotIncludeH1TextIntoH2() {
		TextReader textReader = new TextReader(HTEXT, SplitLevel.H2, LevelMatch.DOWN);

		textReader.advanceToFirst();
		assertThat(textReader.title(), is("Title2"));
		assertThat(textReader.text().collect(Collectors.toList()), is(Collections.singletonList("Title2 text")));

		textReader.advanceToNext();
		assertThat(textReader.title(), is("Title3"));
		assertThat(textReader.text().collect(Collectors.toList()), is(Collections.singletonList("Title3 text")));

		textReader.advanceToNext();
		assertThat(textReader.title(), is("Title2_1"));
		assertThat(textReader.text().collect(Collectors.toList()), is(singletonList("End")));
	}
}