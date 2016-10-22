package asciindex.rest;

import asciindex.Configuration;
import asciindex.dao.ProjectRepository;
import asciindex.model.es.project.Project;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.search.aggregations.AggregationBuilders.terms;

/**
 * @author Alex
 * @since 11.10.2016
 */
@RestController
@RequestMapping(path = "/project")
public class ProjectController {
	private Configuration configuration;
	private ProjectRepository projectRepository;
	private ElasticsearchTemplate elasticsearchTemplate;

	public ProjectController(Configuration configuration, ProjectRepository projectRepository, ElasticsearchTemplate elasticsearchTemplate) {
		this.configuration = configuration;
		this.projectRepository = projectRepository;
		this.elasticsearchTemplate = elasticsearchTemplate;
	}

	@RequestMapping(method = RequestMethod.GET)
	public List<String> list() {
		String aggrName = "projects";
		NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder()
				.withQuery(matchAllQuery())
				.withIndices(configuration.getIndexName())
				.withTypes(Project.TYPE)
				.addAggregation(terms(aggrName).field("name"));

		return elasticsearchTemplate.query(builder.build(), response -> collectBuckets(response, aggrName)).collect(Collectors.toList());
	}

	@RequestMapping(method = RequestMethod.GET, path = "/{id}")
	public List<String> listVersions() {
		final String aggrName = "versions";
		NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder()
				.withQuery(matchAllQuery())
				.withIndices(configuration.getIndexName())
				.withTypes(Project.TYPE)
				.addAggregation(terms(aggrName).field("version"));

		return elasticsearchTemplate.query(builder.build(), response -> collectBuckets(response, aggrName).collect(Collectors.toList()));
	}

	private Stream<String> collectBuckets(SearchResponse response, String aggrName) {
		return Mono
				.just(response)
				.map(SearchResponse::getAggregations)
				.map(aggr -> (StringTerms) aggr.get(aggrName))
				.flatMap(aggr -> Flux
						.fromIterable(aggr.getBuckets())
						.map(bucket -> bucket.getKeyAsString()))
				.toStream();
	}
}
