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
public class SearchServiceTest {
	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	SearchService searchService;


	@Before
	public void setUp() throws Exception {
		elasticsearchTemplate.createIndex(Documentation.class);
		elasticsearchTemplate.putMapping(Documentation.class);

		List<Documentation> data = objectMapper.readValue(getClass().getResourceAsStream("SearchServiceTest-data.json"), new TypeReference<List<Documentation>>() {});

		data.stream().map(this::toIndexQuery).forEach(elasticsearchTemplate::index);
		elasticsearchTemplate.refresh(Documentation.class);
	}

	@Test
	public void searching_for_a_phrase_returns_only_matching_chapter_parts() {
		List<SearchResult> searchResults = searchService.findByQuery("Lorem ipsum dolor sit amet");

		assertThat(searchResults.size(), is(3));
		assertThat(searchResults.get(0).title(), is("Chapter #1"));
		assertThat(searchResults.get(1).title(), is("Chapter #5"));
		assertThat(searchResults.get(2).title(), is("Chapter #3"));
	}

	private <R> IndexQuery toIndexQuery(Documentation documentation) {
		IndexQuery iq = new IndexQuery();
		iq.setIndexName("documentation");
		iq.setType(Documentation.TYPE);
		iq.setObject(documentation);
		return iq;
	}
}