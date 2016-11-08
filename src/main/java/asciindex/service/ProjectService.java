package asciindex.service;

import asciindex.dao.ProjectRepository;
import asciindex.model.es.project.Project;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.StreamSupport;

/**
 * @author Alex
 * @since 07.11.2016
 */
@Service
public class ProjectService {
	private ProjectRepository projectRepository;

	public ProjectService(ProjectRepository projectRepository) {
		this.projectRepository = projectRepository;
	}

	/**
	 * saves new project or updating existing project with new version
	 * @param project
	 * @param version
	 */
	public Project saveOrUpdate(String project, String version) {
		Iterable<Project> savedProjects = projectRepository.search(QueryBuilders.termQuery("name", project));
		return projectRepository.index(
				StreamSupport
						.stream(savedProjects.spliterator(), false)
						.findFirst()
						.map(prj -> this.updateProject(prj, version))
						.orElse(new Project(project, Collections.singletonList(version), version)));
	}

	private Project updateProject(Project oldProject, String version) {
		Set<String> containedVersions = new HashSet<>(oldProject.getVersion());
		if (!containedVersions.add(version)) {
			return oldProject;
		} else {
			return projectRepository.index(new Project(oldProject.getName(), new ArrayList<String>(containedVersions), version));
		}
	}
}
