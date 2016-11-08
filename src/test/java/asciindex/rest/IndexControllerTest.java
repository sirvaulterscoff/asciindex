package asciindex.rest;

import asciindex.dao.ProjectRepository;
import asciindex.model.es.project.Project;
import asciindex.service.IndexQueueService;
import asciindex.service.ProjectService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.response.MockMvcResponse;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static java.security.SecureRandom.*;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

/**
 * @author Alex
 * @since 17.09.2016
 */
public class IndexControllerTest {
	private static final String PROJECT = "Prj";
	public static final String V1 = "1.0.0";
	private static final String INDEXED_CONTENT = "Lorem ipsum";
	public static final String INDEX_PATH = "/index/{project}/{version}";
	@Mock
	IndexQueueService indexQueueService;
	@Mock
	ProjectService projectService;

	IndexController indexController;

	@Rule
	public MockServerRule mockServerRule = new MockServerRule(this);

	private MockServerClient mockServerClient;


	@Before
	public void setUp() throws Exception {
		RestAssuredMockMvc.standaloneSetup(indexController);
		MockitoAnnotations.initMocks(this);

		indexController =  new IndexController(new RestTemplate(), indexQueueService, projectService);
	}

	@Test
	public void indexResourceAddsRequestBodyToQueue() {
		String queueid = String.valueOf(new SecureRandom().nextLong());
		when(indexQueueService.addToQueue(eq(PROJECT), eq(V1), eq(INDEXED_CONTENT))).thenReturn(queueid);

		MockMvcResponse response =
		given()
				.standaloneSetup(indexController)
				.body(INDEXED_CONTENT)
		.when()
				.put(INDEX_PATH, PROJECT, V1);
		verify(indexQueueService).addToQueue(eq(PROJECT), eq(V1), eq(INDEXED_CONTENT));
		response
			.then()
				.statusCode(HttpStatus.ACCEPTED.value())
				.body("link", is("/queue/index/" + queueid))
				.header(HttpHeaders.LOCATION, is("/queue/index/" + queueid));
	}

	@Test
	public void url_passed_in_header_downloaded_and_put_to_queue() throws NoSuchAlgorithmException {
		String path = "/" + String.valueOf(getInstanceStrong().nextLong());
		mockServerClient
				.when(
						request()
								.withMethod("GET")
								.withPath(path))
				.respond(response()
						.withStatusCode(HttpStatus.OK.value())
						.withBody(INDEXED_CONTENT));

		given()
				.standaloneSetup(indexController)
				.header("url", "http://localhost:" + mockServerRule.getPort() +path)
		.when()
			.put(INDEX_PATH, PROJECT, V1)
		.then()
			.statusCode(is(HttpStatus.ACCEPTED.value()));

		verify(indexQueueService).addToQueue(PROJECT, V1, INDEXED_CONTENT);
	}

	@Test
	public void registers_new_project_on_ading_to_queue() {
		given()
				.standaloneSetup(indexController)
				.body(INDEXED_CONTENT)
		.when()
				.put(INDEX_PATH, PROJECT, V1);
		verify(projectService).saveOrUpdate(eq(PROJECT), eq(V1));
		verifyNoMoreInteractions(projectService);
	}
}