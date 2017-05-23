	package asciindex.service;

import asciindex.model.indexing.ChapterInfo;
import asciindex.model.indexing.ChapterTitle;
import asciindex.model.indexing.Documentation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.search.SearchAction;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.support.QueryInnerHitBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.DefaultEntityMapper;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.EntityMapper;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Alex
 * @since 07.11.2016
 */
@Service
public class SearchService {
	private static final Log log = LogFactory.getLog(SearchService.class);
	private ElasticsearchTemplate elasticsearchTemplate;
	private EntityMapper entityMapper = new DefaultEntityMapper();

	public SearchService(ElasticsearchTemplate elasticsearchTemplate) {
		this.elasticsearchTemplate = elasticsearchTemplate;
	}

	public List<SearchResult> findByQuery(String query) {
		final MatchQueryBuilder textMatch = QueryBuilders.matchQuery("chapters.chapterBody.text", query);
		final NestedQueryBuilder chaptersQuery = QueryBuilders.nestedQuery("chapters", textMatch).innerHit(new QueryInnerHitBuilder());
		NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
		final NativeSearchQuery searchQuery = new NativeSearchQuery(chaptersQuery);
		searchQuery.addSourceFilter(new FetchSourceFilter(new String[0], new String[]{"*"}));
		Page<SearchResult> results = elasticsearchTemplate.queryForPage(searchQuery, SearchResult.class, new SearchResultMapper() {

			@Override
			public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
				List<SearchResult> hits = Arrays.stream(response.getHits().hits())
						.flatMap(hit -> hit.getInnerHits().values().stream())
						.flatMap(h -> Arrays.stream(h.hits()))
						.map(SearchHit::sourceAsString)
						.map(SearchService.this::readChpaterInfo)
						.map(SearchResult::fromChapter)
						.collect(Collectors.toList());
				return  new AggregatedPageImpl<T>((List<T>) hits);
			}
		});
		return results.getContent();
	}

	private <R> ChapterInfo readChpaterInfo(String content) {
		try {
			return entityMapper.mapToObject(content, ChapterInfo.class);
		} catch (IOException e) {
			log.error("Failed to collect chapters", e);
			throw new RuntimeException(e);
		}
	}
}
