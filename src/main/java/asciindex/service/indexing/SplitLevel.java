package asciindex.service.indexing;

/**
 * @author Alex
 * @since 18.09.2016
 */
public enum SplitLevel {
	H1("h1"), H2("h2"), H3("h3"), H4("h4"), H5("h5"), H6("h6");

	private String tagName;

	SplitLevel(String tagName) {
		this.tagName = tagName;
	}

	public String openTag() {
		return "<" + tagName;
	}

	public String closeTag() {
		return "</" + tagName + ">";
	}

	public static SplitLevel fromTag(String tag) {
		return valueOf(tag.trim().toUpperCase());
	}

	public boolean inHierarchyOf(SplitLevel splitLevel, LevelMatch match) {
		switch (match) {
			case STRICT:
				return this.equals(splitLevel);
			case UP:
				return this.ordinal() <= splitLevel.ordinal();
			case DOWN:
				return this.ordinal() >= splitLevel.ordinal();
		}
		return false;
	}

	public SplitLevel getLowestTag(LevelMatch match) {
		switch (match) {
			case STRICT:
			case UP:
				return this;
			case DOWN:
				return H4;
		}
		return this;
	}

	public SplitLevel upper(LevelMatch match) {
		switch (match) {
			case STRICT:
			case UP:
				return this;
			case DOWN:
				if (this.equals(H1)) {
					return H1;
				}
				return values()[ordinal() - 1];
		}
		return this;
	}
}
