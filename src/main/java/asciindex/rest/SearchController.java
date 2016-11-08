package asciindex.rest;

import asciindex.service.SearchResult;
import asciindex.service.SearchService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author Alex
 * @since 07.11.2016
 */
@Controller
@RequestMapping(path = "/search")
public class SearchController {
	private static final Log log = LogFactory.getLog(SearchController.class);
	SearchService searchService;

	@ResponseBody
	@RequestMapping(method = RequestMethod.GET)
	public List<SearchResult> doSearch(@RequestParam(name = "q", required = true)String query) {
		return searchService.findByQuery(query);
	}
}
