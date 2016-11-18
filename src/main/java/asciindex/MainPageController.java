package asciindex;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Alex
 * @since 18.11.2016
 */
@Controller
@RequestMapping(path = "/")
public class MainPageController {

	@RequestMapping(method = RequestMethod.GET)
	public String mainPage() {
		return "index";
	}
}
