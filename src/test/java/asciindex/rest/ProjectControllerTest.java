package asciindex.rest;

import asciindex.dao.ProjectRepository;
import asciindex.model.es.project.Project;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;


import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.when;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Alex
 * @since 11.10.2016
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = "classpath:elasticsearch-template-test.xml")
public class ProjectControllerTest {
	public static final String P1 = "asciindex";
	public static final String P2 = "spring-boot";
	public static final String P3 = "spring-security";
	public static final String[] PROJECTS = {P1, P2, P3};
	public static final String[][] VERSIONS = {{"1.0.0", "1.0.1"}, {"1.4.0", "1.4.1"}, {"n/a"}};
	public static final String[] ACTIVE_VERSIONS = {"1.0.1", "1.4.1", "n/a"};
	public static final String PROJECTS_URL = "/project";
	private static final String PROJECT_URL = "/project/{id}";
	private String[] ids = new String[PROJECTS.length];
	@Autowired
	private ProjectController projectController;
	@Autowired
	private ProjectRepository projectService;

	@Before
	public void setUp() throws Exception {
		for (int i = 0; i < PROJECTS.length; i++) {
			String project = PROJECTS[i];
			String[] versions = VERSIONS[i];
			Project entity = new Project(project, Arrays.asList(versions));
			entity.setActiveVersion(ACTIVE_VERSIONS[i]);
			entity = projectService.save(entity);
			ids[i] = entity.getId();
		}
	}

	@Test
	public void willReturnListOfProjects() {
		//@formatter:off
		given()
				.standaloneSetup(projectController)
				.when()
					.get(PROJECTS_URL)
				.then()
					.statusCode(HttpStatus.OK.value())
					.body("sort()", is(Arrays.asList(P1, P2, P3)));
		//@formatter:on
	}

	@Test
	public void will_return_list_of_versions_for_project() {
		//@formatter:off
		String projectId;
		for (int i = 0; i < PROJECTS.length; i++) {
			String project = PROJECTS[i];
			given()
					.standaloneSetup(projectController)
					.when()
						.get(PROJECT_URL, project)
					.then()
						.statusCode(HttpStatus.OK.value())
						.body("versions.sort()", is(Arrays.asList(VERSIONS[i])));
		}
		//@formatter:on
	}

	@Test
	public void active_version_is_set() {
		//@formatter:off
		for (int i = 0; i < PROJECTS.length; i++) {
			String project = PROJECTS[i];
			given()
					.standaloneSetup(projectController)
					.when()
						.get(PROJECT_URL, project)
					.then()
						.body("activeVersion", is(ACTIVE_VERSIONS[i]));
		}
		//@formatter:on
	}

}