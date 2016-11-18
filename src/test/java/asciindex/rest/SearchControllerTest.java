package asciindex.rest;

import asciindex.service.SearchResult;
import asciindex.service.TextIndexLookupTest;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.springframework.http.HttpStatus.OK;

/**
 * @author Alex
 * @since 18.11.2016
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = "classpath:elasticsearch-template-test.xml")
public class SearchControllerTest extends TextIndexLookupTest {
	@Autowired
	SearchController searchController;

	@Test
	public void returns_list_of_hits() throws Exception {
		//@formatter:off
		TestSearchResult[] result =
		given()
				.standaloneSetup(searchController)
		.when()
				.get("/search?q=Lorem ipsum dolor sit amet")
		.then()
				.statusCode(OK.value())
				.extract()
				.as(TestSearchResult[].class);
		//@formatter:on
		assertThat(result.length, is(3));
		assertThat(result[0].getTitle(), is("Chapter #1"));
		assertThat(result[1].getTitle(), is("Chapter #5"));
		assertThat(result[2].getTitle(), is("Chapter #3"));
		assertThat(result[0].getText().length, is(3));
		assertThat(result[1].getText().length, is(8));
		assertThat(result[2].getText().length, is(1));
		assertThat(result[0].getText(),
				is(new String[]{
						"Lorem ipsum dolor sit amet",
						"consectetur adipiscing elit",
						"sed do eiusmod tempor incididunt ut labore et dolore magna aliqua"}));
	}

	public static class TestSearchResult {
		private String[] text;
		private String title;

		public String[] getText() {
			return text;
		}

		public void setText(String[] text) {
			this.text = text;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}
	}
}