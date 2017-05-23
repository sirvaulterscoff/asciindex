package asciindex.dao;

import asciindex.model.es.project.Project;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Alex
 * @since 11.10.2016
 */
@Repository
public interface ProjectRepository extends ElasticsearchRepository<Project, String>{
}
