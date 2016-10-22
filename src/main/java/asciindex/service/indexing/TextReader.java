package asciindex.service.indexing;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @author Alex
 * @since 18.09.2016
 */
public class TextReader {
	public static final Pattern tagNameMatcher = Pattern.compile("\\<h\\d[\\s|\\>]");
	public static final int SKIP_MARGIN = 3;
	private String text;
	private String itext;
	private final SplitLevel splitLevel;
	private final LevelMatch match;
	private int pos = -1;
	private String scurrentTag = "";
	private SplitLevel currentTag;
	private int textLength;

	public TextReader(String text, SplitLevel splitLevel, LevelMatch match) {
		this.text = text;
		this.textLength = text.length();
		this.itext = text.toLowerCase();
		this.splitLevel = splitLevel;
		this.match = match;
	}

	public boolean advanceToFirst() {
		boolean atFirstTag = advanceToTag();
		Preconditions.checkState(atFirstTag, "Indexing text does not cotain tag %s", splitLevel.name());
		while (!acceptedTag()) {
			skipCurrentTag();
			if (!advanceToTag()) {
				throw new IllegalArgumentException("Indexing text does not cotain tag " + splitLevel.name());
			}
		}
		return true;
	}


	public boolean advanceToNext() {
		Preconditions.checkState(!scurrentTag.isEmpty(), "Not at first tag");
		skipCurrentTag();
		boolean tagFound = advanceToTag();
		if (!tagFound) {
			return false;
		}
		while (!acceptedTag()) {
			if (this.pos < this.textLength - SKIP_MARGIN * 2) {
				skipCurrentTag();
				if (!advanceToTag()) {
					return false;
				}
			} else {
				return false;
			}
		}

		return true;
	}

	private void skipCurrentTag() {
		this.pos += SKIP_MARGIN;
	}

	private boolean acceptedTag() {
		return SplitLevel
				.fromTag(scurrentTag)
				.inHierarchyOf(splitLevel, match);
	}

	private boolean advanceToTag() {
		while (true) {
			int possibleChapterPos = stringPart().indexOf("<h");
			if (possibleChapterPos == -1) {
				return false;
			}
			final String tag = stringPart(possibleChapterPos).isubstring(0, 4);
			if (tagNameMatcher.matcher(tag).matches()) {
				scurrentTag = tag.substring(1, 3);
				currentTag = SplitLevel.fromTag(scurrentTag);
				this.pos = possibleChapterPos;
				return true;
			} else {
				skipCurrentTag();
			}
		}
	}

	private StringPart stringPart(int pos) {
		return new StringPart(this.text, this.itext, pos);
	}

	public String title() {
		Preconditions.checkState(!scurrentTag.isEmpty(), "Tag not selected");

		int endOfCurrentTag = this.text.indexOf(">", pos + 2);
		Preconditions.checkState(endOfCurrentTag != -1, "End of current tag %s not found after position %s", scurrentTag, pos);
		final int endIndex = this.text.indexOf("</" + scurrentTag, endOfCurrentTag + 1);
		Preconditions.checkState(endIndex != -1, "Reading beyond the end of content?");
		return this.text.substring(endOfCurrentTag + 1, endIndex);
	}

	public <R> Stream<String> text() {
		Preconditions.checkState(pos > -1, "Should first locate chapter tag");
		int nextChapterStart = stringPart().indexOf(currentTag.openTag());
		return Stream
				.of(textToNextTagOrEndOfString())
				.filter(string -> string != null)
				.map(string -> string.split("\\n"))
				.flatMap(
						strings ->
								Stream
										.of(strings)
										.filter(string -> StringUtils.isNotEmpty(string)));
	}

	@Nullable
	private String textToNextTagOrEndOfString() {
		String toNextTag;
		SplitLevel _tag = splitLevel.getLowestTag(match);
		while (true) {
			toNextTag = stringPart().substringBetween(currentTag.closeTag(), _tag.openTag());
			if (splitLevel.equals(_tag) || toNextTag != null) {
				break;
			}
			_tag = _tag.upper(match);
		}
		if (toNextTag == null) {
			return stringPart().substringBetween(currentTag.closeTag());
		} else {
			return toNextTag;
		}
	}

	private StringPart stringPart() {
		return new StringPart(this.text, this.itext, this.pos);
	}

	public int pos() {
		return this.pos;
	}


	private class StringPart {
		private final String text;
		private String itext;
		private final int pos;

		public StringPart(String text, String itext, int pos) {
			this.text = text;
			this.itext = itext;
			this.pos = pos;
		}

		public int indexOf(String what) {
			return itext.indexOf(what, this.pos);
		}

		@Nullable
		public String substringBetween(String from, String to) {
			int startAt = itext.indexOf(from, this.pos);
			if (startAt == -1) {
				return null;
			}
			startAt += from.length();
			int endAt = itext.indexOf(to, startAt + 1);
			if (endAt == -1) {
				return null;
			}
			if (checkRange(startAt) || checkRange(endAt)) {
				return null;
			}
			return text.substring(startAt, endAt);
		}


		public String substringBetween(String from) {
			int startAt = itext.indexOf(from, this.pos);
			if (startAt == -1) {
				return null;
			}
			startAt += from.length();
			return text.substring(startAt);
		}

		public String substring(int fromInclusive, int toExclusive) {
			return text.substring(pos + fromInclusive, pos + toExclusive);
		}

		private boolean checkRange(int idx) {
			return idx >= text.length() - 1;
		}

		public String isubstring(int fromInclusive, int toExclusive) {
			return itext.substring(pos + fromInclusive, pos + toExclusive);
		}

	}
}
