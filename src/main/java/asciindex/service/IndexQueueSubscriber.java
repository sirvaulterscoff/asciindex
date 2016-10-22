package asciindex.service;

import asciindex.model.es.IndexTaskStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author Alex
 * @since 25.09.2016
 */
@Service
public class IndexQueueSubscriber {
	private IndexQueueService indexQueueService;

	@Autowired
	public IndexQueueSubscriber(IndexQueueService indexQueueService) {
		this.indexQueueService = indexQueueService;
	}

	@PostConstruct
	public void init() {
		indexQueueService
				.getPublishEventStream()
				.doOnNext(indexQueueService::index)
				.doOnNext(indexQueueService::removeContent)
				.subscribe(x -> indexQueueService.updateStatus(x, IndexTaskStatus.DONE));
	}
}
