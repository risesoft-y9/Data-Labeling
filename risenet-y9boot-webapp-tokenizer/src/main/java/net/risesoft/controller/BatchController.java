package net.risesoft.controller;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletResponse;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import net.risesoft.nosql.elastic.entity.Article;
import net.risesoft.nosql.elastic.repository.ArticleRepository;
import net.risesoft.y9.json.Y9JacksonUtil;
@Controller
@RequestMapping(value = "/batch")
public class BatchController {
	
	@Autowired
	private RestHighLevelClient elasticsearchClient;
	
	@Autowired
	private ArticleRepository articlesRepository;
	
	@RequestMapping(value = "/dell")
    @ResponseBody
    public void dell(String type, ServletResponse response) throws IOException {
		List<Article> al = articlesRepository.findByType(type);
		for(Article a : al) {
			articlesRepository.delete(a);
		}
    }
	
    @RequestMapping(value = "/del")
    @ResponseBody
    public void del(String type, Integer startIndex,Integer endIndex, ServletResponse response) throws IOException {
    	BoolQueryBuilder query = new BoolQueryBuilder();
    	query.must(QueryBuilders.termQuery("type", type));
    	query.must(QueryBuilders.rangeQuery("index").from(startIndex).to(endIndex).includeLower(true).includeUpper(true));
		SearchHit[] shits = queryBuilder(new String[]{"y9_articles"}, query,null,null,1, endIndex-startIndex);
		for (int i = 0; i < shits.length; i++) {
			SearchHit shit = shits[i];
			String json = shit.getSourceAsString();
			Article info = Y9JacksonUtil.readValue(json, Article.class);
			articlesRepository.delete(info);
		}
    }
    
    private SearchHit[] queryBuilder(String[] type, BoolQueryBuilder query , String sort, SortOrder sortOrder, Integer page, Integer limit) throws IOException {
		SearchRequest searchRequest = new SearchRequest(type).searchType(SearchType.DFS_QUERY_THEN_FETCH);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(query).trackTotalHits(true);
		searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		if(sort != null && sortOrder != null) {
			searchSourceBuilder.sort(sort, sortOrder);	
		}
		if(page != null && limit != null) {
			searchSourceBuilder.from((page - 1) * limit);
			searchSourceBuilder.size(limit);	
		}
		searchRequest.source(searchSourceBuilder);
		SearchResponse response = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
		SearchHits searchHits = response.getHits();
		SearchHit[] hits = searchHits.getHits();
		return hits;
	}

}
