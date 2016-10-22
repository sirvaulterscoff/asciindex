package asciindex.rest;

import asciindex.model.es.IndexTaskStatus;
import asciindex.service.IndexQueueService;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Alex
 * @since 17.09.2016
 */
public class QueueIndexControllerTest {
	public static final String QUEUE_CONTROLLER = "/queue/index/{id}";
	private static final String ID = UUID.randomUUID().toString();
	private static final String PROJECT = "asciindex";
	public static final String VERSION = UUID.randomUUID().toString();
	@Mock
	IndexQueueService indexQueueService;
	@InjectMocks
	QueueIndexController queueIndexController;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void getWillReturn404ForNonExistingId() throws Exception {
		when(indexQueueService.status(eq(ID))).thenReturn(null);
		given()
				.standaloneSetup(queueIndexController)
				.get(QUEUE_CONTROLLER, ID)
				.then()
				.statusCode(HttpStatus.NOT_FOUND.value());

		verify(indexQueueService).status(eq(ID));
	}

	@Test
	public void willReturn200AndStatusIfStillBeingIndexed() {
		when(indexQueueService.status(eq(ID))).thenReturn(IndexTaskStatus.INDEXING);

		given()
				.standaloneSetup(queueIndexController)
				.get(QUEUE_CONTROLLER, ID)
				.then()
				.statusCode(HttpStatus.OK.value())
				.body("status", is(IndexTaskStatus.INDEXING.name()));

		verify(indexQueueService).status(eq(ID));
		verifyNoMoreInteractions(indexQueueService);
	}

	@Test
	public void willReturn303AndIndexUrlForProcessedId() {
		when(indexQueueService.status(eq(ID))).thenReturn(IndexTaskStatus.DONE);

		given()
				.standaloneSetup(queueIndexController)
				.get(QUEUE_CONTROLLER, ID)
				.then()
				.statusCode(HttpStatus.SEE_OTHER.value())
				.header(HttpHeaders.LOCATION, is("/index/" + ID));
	}
}
