package asciindex.service.indexing;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * @author Alex
 * @since 18.09.2016
 */
public class SplitLevelTest {
	@Test
	public void upper() throws Exception {
		assertThat(SplitLevel.H4.upper(LevelMatch.STRICT), is(SplitLevel.H4));
		assertThat(SplitLevel.H2.upper(LevelMatch.UP), is(SplitLevel.H2));
		assertThat(SplitLevel.H1.upper(LevelMatch.DOWN), is(SplitLevel.H1));
		assertThat(SplitLevel.H4.upper(LevelMatch.DOWN), is(SplitLevel.H3));
		assertThat(SplitLevel.H3.upper(LevelMatch.DOWN), is(SplitLevel.H2));
		assertThat(SplitLevel.H2.upper(LevelMatch.DOWN), is(SplitLevel.H1));
	}

	@Test
	public void getLowestTag() throws Exception {

		assertThat(SplitLevel.H2.getLowestTag(LevelMatch.STRICT), is(SplitLevel.H2));
		assertThat(SplitLevel.H2.getLowestTag(LevelMatch.UP), is(SplitLevel.H2));
		assertThat(SplitLevel.H2.getLowestTag(LevelMatch.DOWN), is(SplitLevel.H4));

	}

	@Test
	public void openTagReturnsPartOfTag() throws Exception {
		assertThat(SplitLevel.H1.openTag(), is("<h1"));
		assertThat(SplitLevel.H2.openTag(), is("<h2"));
		assertThat(SplitLevel.H3.openTag(), is("<h3"));
		assertThat(SplitLevel.H4.openTag(), is("<h4"));

	}

	@Test
	public void h2PriorToH3IfNonStrictMatch() throws Exception {
		SplitLevel slh2 = SplitLevel.H2;

		assertTrue(slh2.inHierarchyOf(SplitLevel.H1, LevelMatch.DOWN));
		assertTrue(slh2.inHierarchyOf(SplitLevel.H2, LevelMatch.DOWN));
		assertFalse(slh2.inHierarchyOf(SplitLevel.H3, LevelMatch.DOWN));
	}

	@Test
	public void onlySelfTagIsPriorToSelf() {
		SplitLevel slh3 = SplitLevel.H3;

		assertTrue(slh3.inHierarchyOf(SplitLevel.H3, LevelMatch.STRICT));
		assertFalse(slh3.inHierarchyOf(SplitLevel.H1, LevelMatch.STRICT));
		assertFalse(slh3.inHierarchyOf(SplitLevel.H2, LevelMatch.STRICT));
	}

	@Test
	public void fromTagReturnsCorrectTag() {
		SplitLevel slh1 = SplitLevel.fromTag("h1");
		SplitLevel slh2 = SplitLevel.fromTag("H2");
		SplitLevel slh3 = SplitLevel.fromTag("h3 ");

		assertThat(slh1, is(SplitLevel.H1));
		assertThat(slh2, is(SplitLevel.H2));
		assertThat(slh3, is(SplitLevel.H3));
	}

	@Test
	public void selfAndDescentsTagsArePriorToSelf() {
		SplitLevel slh2 = SplitLevel.H2;

		assertTrue(slh2.inHierarchyOf(SplitLevel.H3, LevelMatch.UP));
		assertTrue(slh2.inHierarchyOf(SplitLevel.H2, LevelMatch.UP));
		assertFalse(slh2.inHierarchyOf(SplitLevel.H1, LevelMatch.UP));
	}

}