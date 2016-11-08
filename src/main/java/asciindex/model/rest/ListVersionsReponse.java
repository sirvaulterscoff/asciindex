package asciindex.model.rest;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Alex
 * @since 24.10.2016
 */
public class ListVersionsReponse {
	private static final ListVersionsReponse NOT_FOUND = new ListVersionsReponse();
	private List<String> versions;
	private String activeVersion;

	private ListVersionsReponse(List<String> versions, String activeVersion) {
		this.versions = versions;
		this.activeVersion = activeVersion;
	}

	private ListVersionsReponse() {

	}

	public List<String> getVersions() {
		return versions;
	}

	public String getActiveVersion() {
		return activeVersion;
	}

	public static ListVersionsReponse from(List<Object> version, String activeVersion) {
		return new ListVersionsReponse(version.stream().map(s -> (String)s).collect(Collectors.toList()), activeVersion);
	}

	public static ListVersionsReponse notFound(String id) {
		return NOT_FOUND;
	}
}
