package asciindex.service;

import asciindex.dao.ProjectRepository;
import asciindex.model.es.IndexTask;
import asciindex.model.es.project.Project;
import org.elasticsearch.index.query.QueryBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Alex
 * @since 07.11.2016
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = "classpath:elasticsearch-template-test.xml")
public class ProjectServiceTest {
	private static final String P1 = "project1";
	private static final String P2 = "project2";
	private static final String P3 = "project3";
	private static final String V1 = "1.0.0";
	private static final String V2 = "2.0.0";

	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private ProjectRepository projectRepository;

	public void setUp() throws Exception {
		elasticsearchTemplate.createIndex(Project.class);
	}

	@Test
	public void saves_new_project() {
		final Project expectedProject = projectRepository.index(new Project(P1, Arrays.asList(V1, V2), V1));

		Project savedProject = projectService.saveOrUpdate(P1, V1);

		assertThat(savedProject, is(expectedProject));
	}

	@Test
	public void saving_existing_project_adds_version_to_list() {
		final Project initial = projectRepository.index(new Project(P2, Collections.singletonList(V1), V1));
		final Project exptectedProject = new Project(P1, Collections.singletonList(V1));

		Project savedProject = projectService.saveOrUpdate(P1, V2);

		assertThat(savedProject.getName(), is(exptectedProject.getName()));
		assertThat(savedProject.getVersion(), is(Arrays.asList(V1, V2)));
	}

	@Test
	public void saving_existing_project_updates_current_version() {
		Project initial = projectRepository.index(new Project(P3, Collections.singletonList(V1), V1));
		assertThat(initial.getActiveVersion(), is(V1));

		final Project savedProject = projectService.saveOrUpdate(P3, V2);

		assertThat(savedProject.getName(), is(P3));
		assertThat(savedProject.getActiveVersion(), is(V2));
	}
}