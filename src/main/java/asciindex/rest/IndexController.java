package asciindex.rest;

import asciindex.dao.ProjectRepository;
import asciindex.model.es.project.Project;
import asciindex.model.rest.ResourceInfo;
import asciindex.service.IndexQueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

	private RestTemplate restTemplate;
	private IndexQueueService indexQueueService;
	private ProjectRepository projectRepository;

	public IndexController(RestTemplate restTemplate, IndexQueueService indexQueueService, ProjectRepository projectRepository) {
		this.restTemplate = restTemplate;
		this.indexQueueService = indexQueueService;
		this.projectRepository = projectRepository;
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
		projectRepository.save(new Project(project, version));
	}

	private ResponseEntity<Object> putToQueue(String project, String version, String content) {
		log.debug("Adding task to scan project {} v {}", project, version);
		String queueId = indexQueueService.addToQueue(project, version, content);
		log.debug("Task added to queue with id {}", queueId);
		MultiValueMap<String, String> headers = new HttpHeaders();
		headers.set(HttpHeaders.LOCATION, "/queue/index/" + String.valueOf(queueId));
		return new ResponseEntity<>(new ResourceInfo("/queue/index", queueId), headers, HttpStatus.ACCEPTED);
	}
}
