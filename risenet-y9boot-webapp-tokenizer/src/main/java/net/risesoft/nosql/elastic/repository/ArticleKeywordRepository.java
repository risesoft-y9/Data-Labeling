package net.risesoft.nosql.elastic.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import net.risesoft.nosql.elastic.entity.ArticleKeyword;

public interface ArticleKeywordRepository extends ElasticsearchRepository<ArticleKeyword, String>{

	
	
}
