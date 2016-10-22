package asciindex.service.indexing;

/**
 * @author Alex
 * @since 18.09.2016
 */
public enum LevelMatch {
	/**
	 * match current tag and all "greater" tags, such as if h3 is matched, then h2 and h1 will also be matched
	 */
	UP, STRICT, DOWN
}
