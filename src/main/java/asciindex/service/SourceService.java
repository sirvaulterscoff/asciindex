package asciindex.service;

import asciindex.model.es.Source;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Service;

/**
 * @author Alex
 * @since 23.05.2017
 */
@Service
public class SourceService {
	private static final Log log = LogFactory.getLog(SourceService.class);
	private ElasticsearchTemplate elasticsearchTemplate;

	public SourceService(ElasticsearchTemplate elasticsearchTemplate) {
		this.elasticsearchTemplate = elasticsearchTemplate;
	}

	public void storeSource(String project, String version, String content) {
		Source src = new Source(project, version, content);
		final IndexQuery indexQuery = new IndexQuery();
		indexQuery.setObject(src);
		elasticsearchTemplate.index(indexQuery);
	}
}
