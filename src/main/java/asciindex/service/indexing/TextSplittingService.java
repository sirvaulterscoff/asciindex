package asciindex.service.indexing;

import asciindex.model.indexing.ChapterBody;
import asciindex.model.indexing.ChapterInfo;
import asciindex.model.indexing.ChapterTitle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.function.Consumer;

/**
 * @author Alex
 * @since 18.09.2016
 */
@Service
public class TextSplittingService {
	private static final Logger log = LoggerFactory.getLogger(TextSplittingService.class);
	private TextProcessor textProcessor = new TextProcessor();

	public Flux<ChapterInfo> splitToChapters(String indexedText, SplitLevel splitLevel, LevelMatch strict) {
		return Flux.create(new Consumer<FluxSink<ChapterInfo>>() {
			@Override
			public void accept(FluxSink<ChapterInfo> sink) {
				TextReader reader = new TextReader(indexedText, SplitLevel.H2, LevelMatch.STRICT);
				reader.advanceToFirst();

				chapterFromReader(sink, reader);

				while (reader.advanceToNext() && !sink.isCancelled()) {
					chapterFromReader(sink, reader);
				}
				sink.complete();
			}

			private void chapterFromReader(FluxSink<ChapterInfo> chapters, TextReader reader) {
				chapters.next(
						new ChapterInfo(
								new ChapterTitle(reader.pos(), reader.title()),
								new ChapterBody(textProcessor.processText(reader.text()))
						)
				);
			}
		});
	}

}
