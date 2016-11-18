package asciindex.model.indexing;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Alex
 * @since 18.09.2016
 */
public class ChapterBody {
	private Stream<String> text;

	public ChapterBody() {
	}

	public ChapterBody(Stream<String> text) {
		this.text = text;
	}

	public Stream<String> text() {
		return text;
	}

	public List<String> getText() {
		return text.collect(Collectors.toList());
	}

	public void setText(List<String> text) {
		this.text = text.stream();
	}

	public List<String> textAsList() {
		return text.collect(Collectors.toList());
	}
}
