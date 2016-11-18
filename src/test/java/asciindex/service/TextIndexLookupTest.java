package asciindex.service;

import asciindex.model.indexing.Documentation;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

/**
 * @author Alex
 * @since 18.11.2016
 */
public class TextIndexLookupTest {
	private static final Log log = LogFactory.getLog(TextIndexLookupTest.class);
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;

	@Before
	public void setUp() throws Exception {
		elasticsearchTemplate.createIndex(Documentation.class);
		elasticsearchTemplate.putMapping(Documentation.class);

		List<Documentation> data = objectMapper.readValue(getClass().getResourceAsStream("/asciindex/service/SearchServiceTest-data.json"), new TypeReference<List<Documentation>>() {});

		data.stream().map(this::toIndexQuery).forEach(elasticsearchTemplate::index);
		elasticsearchTemplate.refresh(Documentation.class);
	}

	private <R> IndexQuery toIndexQuery(Documentation documentation) {
		IndexQuery iq = new IndexQuery();
		iq.setId(documentation.id());
		iq.setIndexName("documentation");
		iq.setType(Documentation.TYPE);
		iq.setObject(documentation);
		return iq;
	}
}
