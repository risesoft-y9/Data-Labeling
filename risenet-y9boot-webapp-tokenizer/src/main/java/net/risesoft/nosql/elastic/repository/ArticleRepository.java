package net.risesoft.nosql.elastic.repository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import net.risesoft.nosql.elastic.entity.Article;

public interface ArticleRepository extends ElasticsearchRepository<Article, String> {

	List<Article> findByType(String type);
	
}
