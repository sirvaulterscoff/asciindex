package asciindex.service.indexing;

import org.apache.commons.io.IOUtils;
import org.apache.lucene.analysis.charfilter.HTMLStripCharFilter;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @author Alex
 * @since 21.09.2016
 */
public class TextProcessor {
	private static final Logger log = LoggerFactory.getLogger(TextProcessor.class);


	public Stream<String> processText(Stream<String> reader) {
		Predicate<String> notEmpty = (String s) -> !s.isEmpty();

		return (Stream<String>) reader
				.map(StringReader::new)
//				.map(HTMLStripCharFilter::new)
				.map(r -> readerToString(r))
				.flatMap(list -> list.stream())
				.map(String::trim)
				.filter(notEmpty)
				.map(text -> Jsoup.clean(text, Whitelist.relaxed()));
	}


	public List<String> readerToString(Reader r) {
		try {
			return IOUtils.readLines(r);
		} catch (IOException e) {
			log.error("Failed to collect text linex", e);
			return Collections.emptyList();
		}
	}
}
