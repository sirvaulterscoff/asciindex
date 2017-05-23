package asciindex.service;

import asciindex.Configuration;
import asciindex.model.es.IndexTask;
import asciindex.model.es.IndexTaskStatus;
import asciindex.model.indexing.ChapterInfo;
import asciindex.model.indexing.Documentation;
import asciindex.service.indexing.LevelMatch;
import asciindex.service.indexing.SplitLevel;
import asciindex.service.indexing.TextSplittingService;
import com.google.common.base.Preconditions;
import org.elasticsearch.action.update.UpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.GetQuery;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.WorkQueueProcessor;

import static asciindex.model.es.IndexTaskStatus.CREATED;

/**
 * @author Alex
 * @since 17.09.2016
 */
@Service
public class IndexQueueService {
	private static final Logger log = LoggerFactory.getLogger(IndexQueueService.class);

	private WorkQueueProcessor<String> queueProcessor = WorkQueueProcessor.create();
	private ElasticsearchTemplate elasticsearchTemplate;
	private TextSplittingService textSplittingService;
	private Configuration configuration;

	@Autowired
	public IndexQueueService(ElasticsearchTemplate elasticsearchTemplate, TextSplittingService textSplittingService, Configuration configuration) {
		this.elasticsearchTemplate = elasticsearchTemplate;
		this.textSplittingService = textSplittingService;
		this.configuration = configuration;
		elasticsearchTemplate.createIndex(IndexTask.class);
		elasticsearchTemplate.putMapping(IndexTask.class);
		elasticsearchTemplate.refresh(IndexTask.class);
		elasticsearchTemplate.createIndex(Documentation.class);
		elasticsearchTemplate.putMapping(Documentation.class);
	}

	public String addToQueue(String project, String version, String content) {
		final IndexQuery query = new IndexQuery();
		final IndexTask task = new IndexTask(project, version, content);
		query.setObject(task);
		String id = elasticsearchTemplate.index(query);
		log.debug("Notifying task {} created", id);
		queueProcessor.onNext(id);
		return id;
	}

	public IndexTaskStatus status(String id) {
		return Mono
				.just(id)
				.map(_id -> {
					GetQuery q = new GetQuery();
					q.setId(_id);
					return q;
				})
				.map(query -> elasticsearchTemplate.queryForObject(query, IndexTask.class))
				.map(result -> result.getStatus())
				.block();
	}

	public String index(String id) {
		log.debug("Starting indexing of task {}", id);
		try {
			GetQuery getQuery = new GetQuery();
			getQuery.setId(id);
			IndexTask indexTask = elasticsearchTemplate.queryForObject(getQuery, IndexTask.class);
			Preconditions.checkArgument(CREATED.equals(indexTask.getStatus()), "Task is already being indexed");

			updateStatus(indexTask, IndexTaskStatus.INDEXING);
			Flux<ChapterInfo> chapters = textSplittingService.splitToChapters(new String(indexTask.getContent()), SplitLevel.H2, LevelMatch.UP);
			Documentation doc = new Documentation(indexTask.getProject(), indexTask.getVersion());
			doc.setChapters(chapters.collectList().block());

			IndexQuery indexQuery = new IndexQuery();
			indexQuery.setObject(doc);
			indexQuery.setId(doc.getId());
			return elasticsearchTemplate.index(indexQuery);
		} catch (RuntimeException e) {
			log.error("Failed to index task with id {}", id, e);
			throw e;
		}
	}

	public void updateStatus(String id, IndexTaskStatus status) {
		GetQuery getQuery = new GetQuery();
		getQuery.setId(id);
		log.debug("Changing state for task {} to {}", id, status);
		IndexTask indexTask = elasticsearchTemplate.queryForObject(getQuery, IndexTask.class);
		updateStatus(indexTask, status);
	}

	private void updateStatus(IndexTask indexTask, IndexTaskStatus status) {
		indexTask.setStatus(status);
		final IndexQuery updateQuery = new IndexQuery();
		updateQuery.setObject(indexTask);
		elasticsearchTemplate.index(updateQuery);
	}

	public Flux<String> getPublishEventStream() {
		return queueProcessor;
	}

	public void removeContent(String id) {
		UpdateQuery updateQuery = new UpdateQuery();
		updateQuery.setId(id);
		updateQuery.setClazz(IndexTask.class);
		updateQuery.setUpdateRequest(new UpdateRequest().doc("content", null));
		elasticsearchTemplate.update(updateQuery);
	}
}
