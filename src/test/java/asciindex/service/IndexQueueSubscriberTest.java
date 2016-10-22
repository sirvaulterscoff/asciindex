package asciindex.service;

import asciindex.model.es.IndexTaskStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.WorkQueueProcessor;

import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Alex
 * @since 25.09.2016
 */
public class IndexQueueSubscriberTest {
	@Mock
	IndexQueueService indexQueueService;
	private Subscription subscription;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void subscribesToStream() throws Exception {
		when(indexQueueService.getPublishEventStream()).thenReturn(Flux.just(UUID.randomUUID().toString()).doOnSubscribe(new Consumer<Subscription>() {
			@Override
			public void accept(Subscription subscription) {
				IndexQueueSubscriberTest.this.subscription = subscription;

			}
		}));
		IndexQueueSubscriber indexQueueSubscriber = new IndexQueueSubscriber(indexQueueService);

		indexQueueSubscriber.init();

		assertNotNull(subscription);
	}

	@Test
	public void afterMessageInTheStreamIndexWillBeInvokedAndNewStatusSet() {
		WorkQueueProcessor<String> flux = WorkQueueProcessor.create();
		when(indexQueueService.getPublishEventStream()).thenReturn(flux);
		IndexQueueSubscriber indexQueueSubscriber = new IndexQueueSubscriber(indexQueueService);
		indexQueueSubscriber.init();


		final String id = UUID.randomUUID().toString();
		flux.onNext(id);
		flux.onComplete();
		flux.blockFirst();

		verify(indexQueueService).index(eq(id));
		verify(indexQueueService).updateStatus(eq(id), eq(IndexTaskStatus.DONE));
	}
}