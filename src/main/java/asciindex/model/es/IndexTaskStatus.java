package asciindex.model.es;

import org.springframework.http.HttpStatus;

/**
 * @author Alex
 * @since 17.09.2016
 */
public enum IndexTaskStatus {
	INDEXING, DONE, CREATED;

	public static <R> HttpStatus toHttpCode(IndexTaskStatus indexTaskStatus) {
		switch (indexTaskStatus) {
			case CREATED:
			case INDEXING:
				return HttpStatus.OK;
			case DONE:
				return HttpStatus.SEE_OTHER;
			default:
				return HttpStatus.INTERNAL_SERVER_ERROR;
		}
	}
}
