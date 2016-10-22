package asciindex.dao;

import asciindex.model.es.project.Project;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author Alex
 * @since 11.10.2016
 */
public interface ProjectRepository extends ElasticsearchRepository<Project, String>{
}
