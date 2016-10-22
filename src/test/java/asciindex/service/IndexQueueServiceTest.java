package asciindex.service;

import asciindex.model.es.IndexTask;
import asciindex.model.es.IndexTaskStatus;
import asciindex.model.indexing.Documentation;
import asciindex.service.indexing.TextSplittingServiceTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.GetQuery;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * @author Alex
 * @since 17.09.2016
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = "classpath:elasticsearch-template-test.xml")
@Import(IndexQueueServiceTest.Config.class)
public class IndexQueueServiceTest{

	private static final String PROJECT = "asciindex";
	private static final String VERSION = "1.0.0";
	public static final String CONTENT = "Lorem Ipsum";
	public static final String INDEXED_TEXT = new BufferedReader(new InputStreamReader(TextSplittingServiceTest.class.getResourceAsStream("index.html"))).lines().collect(Collectors.joining("\n"));

	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;
	@Autowired
	private IndexQueueService indexQueueService;


	@Before
	public void setUp() throws Exception {
		elasticsearchTemplate.createIndex(IndexTask.class);
		elasticsearchTemplate.putMapping(IndexTask.class);
	}

	@Test
	public void testTaskWillBeAddedToQueue() throws Exception {
		String id = indexQueueService.addToQueue(PROJECT, VERSION, CONTENT);

		GetQuery getQuery = new GetQuery();
		getQuery.setId(id);
		IndexTask task = elasticsearchTemplate.queryForObject(getQuery, IndexTask.class);

		assertNotNull(id);
		assertThat(task.getId(), is(id));
		assertThat(task.getProject(), is(PROJECT));
		assertThat(task.getVersion(), is(VERSION));
		assertThat(task.getContent(), is(CONTENT));
	}

	@Test
	public void testPutToQueueAllowsToStoreMoreThan32KText() {
		Random r = new Random();
		byte[] arr = new byte[65000];
		r.nextBytes(arr);

		String id = indexQueueService.addToQueue(PROJECT, VERSION, new String(arr));

		assertNotNull(id);
	}

	@Test(timeout = 120 * 1000L)
	@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
	public void eventWillBeFiredUponAddingToQueue() {
		Flux<String> stream = indexQueueService.getPublishEventStream();

		String id = indexQueueService.addToQueue(PROJECT, VERSION, CONTENT);

		assertThat(stream.blockFirst(), is(id));
	}

	@Test
	public void taskStatusWillBeReturned() {
		IndexTask task = new IndexTask(PROJECT, VERSION, CONTENT);
		task.setStatus(IndexTaskStatus.INDEXING);
		IndexQuery indexQuery = new IndexQuery();
		indexQuery.setObject(task);
		String taskId = elasticsearchTemplate.index(indexQuery);

		IndexTaskStatus status = indexQueueService.status(taskId);

		assertThat(status, is(IndexTaskStatus.INDEXING));
	}

	@Test
	public void indexTaskIsLoadedFromQueueAndIndexed() {
		String id = indexQueueService.addToQueue(PROJECT, VERSION, INDEXED_TEXT);

		String indexedId = indexQueueService.index(id);

		GetQuery getQuery = new GetQuery();
		getQuery.setId(indexedId);
		Documentation documentation = elasticsearchTemplate.queryForObject(getQuery, Documentation.class);

		assertThat(documentation.getProject(), is(PROJECT));
		assertThat(documentation.getVersion(), is(VERSION));
		assertThat(documentation.getChapters().size(), is(2));
		assertThat(documentation.chapter(0).title(), is("About"));
		assertThat(documentation.chapter(0).text().collect(Collectors.toList()),
				is(Arrays.asList(
						"This project is intented to be use together with excelent documenting tool asciidoc",
						"It’s intended to fill the missing features of project documentation, such as:",
						"fulltext search",
						"version diff")));

		assertThat(documentation.chapter(1).title(), is("Using asciindex"));
		assertThat(documentation.chapter(1).text().collect(Collectors.toList()),
				is(Arrays.asList(
						"Asciindex provides simple REST API to interact with.",
						"Start by invoking",
						"#> http put :8080/index/spring-boot/1.4.0 url:http://docs.spring.io/spring-boot/docs/1.4.0.RELEASE/reference/htmlsingle/",
						"Note",
						"this example uses brilliant httpie command line tool, which can be found here",
						"Last updated 2016-09-18 15:32:51 +03:00"
				)));
	}

	@Test
	public void removeContent() throws Exception {
		String id = indexQueueService.addToQueue(PROJECT, VERSION, INDEXED_TEXT);

		indexQueueService.removeContent(id);


		GetQuery getQuery = new GetQuery();
		getQuery.setId(id);
		IndexTask indexTask = elasticsearchTemplate.queryForObject(getQuery, IndexTask.class);

		assertTrue(StringUtils.isEmpty(indexTask.getContent()));
	}

	@Configuration
	public static class Config {
		@Bean
		@Primary
		public IndexQueueSubscriber indexQueueSubscriber() {
			return mock(IndexQueueSubscriber.class);
		}
	}
}