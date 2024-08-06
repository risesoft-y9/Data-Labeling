package net.risesoft.scheduled.runner;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.ArrayUtils;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import net.risesoft.nosql.elastic.entity.ArticleKeyword;
import net.risesoft.service.ArticleKeywordService;
import net.risesoft.y9.json.Y9JacksonUtil;

@Component
public class CommandLineRunnerImpl implements CommandLineRunner {
	
	@Resource(name = "articleKeywordService")
	private ArticleKeywordService articleKeywordService;
	
	@Autowired
	private RestHighLevelClient elasticsearchClient;
	
	private static final long SCROLL_TIMEOUT = 200000;
	
	private static int SIZE = 1000;
	
	/**
	 * 用于初始化date文档
	 */
    @Override
    public void run(String... args) throws Exception {
    	System.out.println("执行了CommandLineRunnerImpl");
    	//滚动查询的Scroll
        Scroll scroll = new Scroll(TimeValue.timeValueMillis(SCROLL_TIMEOUT));
        //构建searchRequest
        SearchRequest request = new SearchRequest("articles_keyword");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //sourceBuilder.fetchSource(includes, null);
        //每次滚动的长度
        sourceBuilder.size(SIZE);
        sourceBuilder.sort("endTime",SortOrder.ASC);
        //加入scroll和构造器
        request.scroll(scroll);
        request.source(sourceBuilder);
        //存储scroll的list
        List<String> scrollIdList = new ArrayList<>();
        //返回结果
        SearchResponse searchResponse = elasticsearchClient.search(request, RequestOptions.DEFAULT);
        //拿到第一个ScrollId（游标）
        String scrollId = searchResponse.getScrollId();
        //拿到hits结果
        SearchHit[] hits = searchResponse.getHits().getHits();
        //保存返回结果List
        //List<ArticleKeyword> result = new ArrayList<ArticleKeyword>();
        scrollIdList.add(scrollId);
        String result = "";
        try {
            //滚动查询将SearchHit封装到result中
            while (ArrayUtils.isNotEmpty(hits)) {
                for (SearchHit hit : hits) {
                    //Function<SearchHit, T>, 输入SearchHit，经过操作后，返回T结果
                	String json = hit.getSourceAsString();
                	ArticleKeyword info = Y9JacksonUtil.readValue(json, ArticleKeyword.class);
                	result = result + info.getKeyword() + "\r\n";
                }
                //说明滚动完了，返回结果即可
                if (hits.length < SIZE) {
                    break;
                }
                //继续滚动，根据上一个游标，得到这次开始查询位置
                SearchScrollRequest searchScrollRequest = new SearchScrollRequest(scrollId);
                searchScrollRequest.scroll(scroll);
                //得到结果
                SearchResponse searchScrollResponse = elasticsearchClient.scroll(searchScrollRequest, RequestOptions.DEFAULT);
                //定位游标
                scrollId = searchScrollResponse.getScrollId();
                hits = searchScrollResponse.getHits().getHits();
                scrollIdList.add(scrollId);
            }
        } finally {
            //清理scroll,释放资源
            ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
            clearScrollRequest.setScrollIds(scrollIdList);
            elasticsearchClient.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
        }
        // 创建目录
        String directoryPath = "/data/tokenizer/ik";
        File directory = new File(directoryPath);
        if (directory.mkdirs()) {
            System.out.println("目录创建成功：" + directory.getAbsolutePath());
        } else {
            System.out.println("目录已存在：" + directory.getAbsolutePath());
        }
        //创建文件
        File file = new File("/data/tokenizer/ik/hot_ext.dic");
        if (!file.exists()) {
        	System.out.println("创建hot_ext.dic文件");
        	file.createNewFile();
        }
        Files.write(Paths.get("/data/tokenizer/ik/hot_ext.dic"), result.getBytes(StandardCharsets.UTF_8));
    }
}