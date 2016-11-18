package asciindex.service;

import asciindex.model.indexing.Documentation;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.print.Doc;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * @author Alex
 * @since 07.11.2016
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = "classpath:elasticsearch-template-test.xml")
public class SearchServiceTest extends TextIndexLookupTest {
	@Autowired
	SearchService searchService;


	@Test
	public void searching_for_a_phrase_returns_only_matching_chapter_parts() {
		List<SearchResult> searchResults = searchService.findByQuery("Lorem ipsum dolor sit amet");

		assertThat(searchResults.size(), is(3));
		assertThat(searchResults.get(0).title(), is("Chapter #1"));
		assertThat(searchResults.get(1).title(), is("Chapter #5"));
		assertThat(searchResults.get(2).title(), is("Chapter #3"));
	}
}