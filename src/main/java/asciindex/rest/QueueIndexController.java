package asciindex.rest;

import asciindex.model.es.IndexTaskStatus;
import asciindex.service.IndexQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Optional;

/**
 * @author Alex
 * @since 17.09.2016
 */
@Controller
@RequestMapping("/queue/index")
public class QueueIndexController {
	private IndexQueueService indexQueueService;

	@Autowired
	public QueueIndexController(IndexQueueService indexQueueService) {
		this.indexQueueService = indexQueueService;
	}

	@RequestMapping("/{id}")
	public ResponseEntity getStatus(@PathVariable("id") String id) {
		IndexTaskStatus status = indexQueueService.status(id);
		return Mono
				.just(Optional.ofNullable(status))
				.flatMap(st -> statusToEntity(st, id))
				.blockFirst();
	}

	private Flux<ResponseEntity> statusToEntity(Optional<IndexTaskStatus> indexTaskStatus, String id) {
		if(indexTaskStatus.isPresent()) {
			switch (indexTaskStatus.get()) {
				case INDEXING:
					return Flux.just(ResponseEntity.ok(Collections.singletonMap("status", IndexTaskStatus.INDEXING.name())));
				case CREATED:
					return Flux.just(ResponseEntity.ok(Collections.singletonMap("status", IndexTaskStatus.INDEXING.name())));
				case DONE:
					return Flux.just(ResponseEntity.status(HttpStatus.SEE_OTHER).header(HttpHeaders.LOCATION, "/index/" + id).build());
				default:
					return Flux.just(ResponseEntity.badRequest().build());
			}

		} else {
			return Flux.just(ResponseEntity.notFound().build());
		}
	}
}
