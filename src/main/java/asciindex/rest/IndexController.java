package asciindex.rest;

import asciindex.model.rest.ResourceInfo;
import asciindex.service.IndexQueueService;
import asciindex.service.ProjectService;
import asciindex.service.SourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author Alex
 * @since 14.09.2016
 */
@RestController
@RequestMapping("/index")
public class IndexController {
	private static final Logger log = LoggerFactory.getLogger(IndexController.class);

	private final RestTemplate restTemplate;
	private final IndexQueueService indexQueueService;
	private final SourceService sourceService;
	private final ProjectService projectService;

	public IndexController(RestTemplate restTemplate,
	                       IndexQueueService indexQueueService,
	                       SourceService sourceService, ProjectService projectService) {
		this.restTemplate = restTemplate;
		this.indexQueueService = indexQueueService;
		this.sourceService = sourceService;
		this.projectService = projectService;
	}

	@RequestMapping(
			path = "/{project}/{version:.+}",
			method = RequestMethod.PUT)
	public ResponseEntity<Object> index(@PathVariable("project") String project,
	                                    @PathVariable("version") String version,
	                                    @RequestHeader(value = "url", required = false) String url,
	                                    @RequestBody(required = false) String content) {

		String indexedContent = content;
		if (content == null || content.trim().isEmpty()) {
			if (url == null || url.isEmpty()) {
				return new ResponseEntity<>("No content or url header specified in request", HttpStatus.BAD_REQUEST);
			}
			log.info("Downloading content for {}:{}", project, version);
			ResponseEntity<String> result = restTemplate.getForEntity(url, String.class);
			if (result.getStatusCode().is2xxSuccessful()) {
				indexedContent = result.getBody();
			} else {
				return new ResponseEntity<>(result.getBody(), result.getStatusCode());
			}
		}
		storeProjectAndVersion(project, version);
		return putToQueue(project, version, indexedContent);
	}

	private void storeProjectAndVersion(String project, String version) {
		projectService.saveOrUpdate(project, version);
	}

	private ResponseEntity<Object> putToQueue(String project, String version, String content) {
		log.debug("Adding task to scan project {} v {}", project, version);
		String queueId = indexQueueService.addToQueue(project, version, content);
		log.debug("Task added to queue with id {}", queueId);
		sourceService.storeSource(project, version, content);
		log.debug("Saved content for {}:{}", project, version);
		MultiValueMap<String, String> headers = new HttpHeaders();
		headers.set(HttpHeaders.LOCATION, "/queue/index/" + String.valueOf(queueId));
		return new ResponseEntity<>(new ResourceInfo("/queue/index", queueId), headers, HttpStatus.ACCEPTED);
	}
}
