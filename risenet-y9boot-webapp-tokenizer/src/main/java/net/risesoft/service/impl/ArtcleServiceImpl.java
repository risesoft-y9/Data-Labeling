package net.risesoft.service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import net.risesoft.nosql.elastic.entity.Article;
import net.risesoft.nosql.elastic.entity.ArticleKeyword;
import net.risesoft.nosql.elastic.entity.TypeField;
import net.risesoft.nosql.elastic.entity.Word;
import net.risesoft.nosql.elastic.repository.ArticleKeywordRepository;
import net.risesoft.nosql.elastic.repository.ArticleRepository;
import net.risesoft.service.ArtcleService;
import net.risesoft.service.ArticleKeywordService;
import net.risesoft.util.PrintUtil;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.json.Y9JacksonUtil;

@Service(value = "artclesService")
@Transactional(readOnly = true)
public class ArtcleServiceImpl implements ArtcleService {
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Autowired
	private RestHighLevelClient elasticsearchClient;
	
	@Autowired
	private ArticleRepository articlesRepository;
	
	@Autowired
	private ArticleKeywordRepository articleKeywordRepository;
	
	@Resource(name = "articleKeywordService")
	private ArticleKeywordService articleKeywordService;
	
private static final long SCROLL_TIMEOUT = 200000;
	
	private static int SIZE = 1000;
	
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
	
	@Override
    public Map<String, Object> getTitle(String type, String mark,Integer startIndex,Integer endIndex,Integer page, Integer rows) {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        List<Map<String, String>> listMap = new ArrayList<Map<String, String>>();
        dataMap.put("success", true);
        try {
        	BoolQueryBuilder query = new BoolQueryBuilder();
        	if( StringUtils.isNotBlank(type) ) query.must(QueryBuilders.termQuery("type", type));
        	if( StringUtils.isNotBlank(mark) ) query.must(QueryBuilders.termQuery("mark", mark));
        	if( startIndex != null && endIndex != null ) {
        		query.must(QueryBuilders.rangeQuery("index").from(startIndex).to(endIndex).includeLower(true).includeUpper(true));
        	}
        	String sort = "index";
			SearchHit[] shits = queryBuilder(new String[]{"y9_articles"}, query,sort,SortOrder.ASC,page, rows);
			for (int i = 0; i < shits.length; i++) {
				SearchHit shit = shits[i];
				String json = shit.getSourceAsString();
				Article info = Y9JacksonUtil.readValue(json, Article.class);
				Map<String, String> map = new HashMap<String, String>();
				List<TypeField> typeFields = info.getTypefield();
				map.put("id", info.getId());
				map.put("index", info.getIndex().toString());
				map.put("mark", info.getMark());
				map.put("title", "");
				for(TypeField typeField : typeFields) {
					String str = typeField.getFieldName().replaceAll(" ","");
					if(str.equals("标题")) {
						map.put("title", typeField.getFieldContent());
						break;
					}
				}
				if( StringUtils.isBlank(map.get("title")) ) map.put("title", "无标题-" + info.getId());
				listMap.add(map);
			}
			//获取查询总数量
			SearchRequest request = new SearchRequest("y9_articles").source(new SearchSourceBuilder().query(query).trackTotalHits(true));
			long count = elasticsearchClient.search(request, RequestOptions.DEFAULT).getHits().getTotalHits().value;
			long countAll = elasticsearchClient.search(new SearchRequest("y9_articles").source(new SearchSourceBuilder().trackTotalHits(true)), RequestOptions.DEFAULT).getHits().getTotalHits().value;
			dataMap.put("countAll", countAll);
			dataMap.put("pages", (count + rows - 1) / rows);
			dataMap.put("total", count);
			
			dataMap.put("dataList", listMap);
        } catch (Exception e) {
            dataMap.put("success", false);
            e.printStackTrace();
        }
        return dataMap;
    }
	
	@Override
    public Map<String,Integer> getArtcleType() {
		Map<String,Integer> map = new HashMap<String,Integer>();
		SearchRequest searchRequest = new SearchRequest("y9_articles").searchType(SearchType.DFS_QUERY_THEN_FETCH);
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		try {
			//聚合
			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			searchSourceBuilder.aggregation(AggregationBuilders.terms("count").field("type").order(BucketOrder.count(false)).size(10000));
			searchSourceBuilder.query(boolQueryBuilder).trackTotalHits(true);
			searchRequest.source(searchSourceBuilder);
			
			SearchResponse searchResponse = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
			Aggregations aggregations = searchResponse.getAggregations();
			org.elasticsearch.search.aggregations.bucket.terms.Terms ts = aggregations.get("count");
			for (Terms.Bucket bucket : ts.getBuckets()) {
				map.put(bucket.getKeyAsString(), (int) bucket.getDocCount());
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	@Override
    public List<Word> getRecord(String id) {
		Article art = articlesRepository.findById(id).orElse(null);
		if(art.getAmendmentRecord() == null) return new ArrayList<Word>();
		return art.getAmendmentRecord();
	}
	
	@Override
    public List<String> getKeyword(String id) {
		Article art = articlesRepository.findById(id).orElse(null);
		List<String> list = new ArrayList<String>();
		if(art.getWords() == null) return list;
		for(Word word : art.getWords()) {
			list.add(word.getWord());
		}
		return list;
	}
	
	@Override
    public String wenzhangneirong(String id) {
		Article art = articlesRepository.findById(id).orElse(null);
		String str = "";
		for(TypeField tf : art.getTypefield()) {
			if(tf.getFieldName().equals("文章内容") || tf.getFieldName().equals("内容")) {
				str = tf.getFieldContent();
				break;
			}
		}
		return str;
	}
	
	@Override
    public Article getArtcle(String id) {
		Article art = articlesRepository.findById(id).orElse(new Article());
		if(art.getWords() == null) art.setWords(new ArrayList<Word>());
		if(art.getAmendmentRecord() == null) art.setAmendmentRecord(new ArrayList<Word>());
		art.setDic(getDic());
        return art;
    }
	
	private List<String> getDic(){
		List<String> dic = new ArrayList<String>();
        try {
        	InputStream in  = new FileInputStream(new File(Y9Context.getProperty("y9.common.dicPath")));
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while ((line = reader.readLine()) != null) {
            	if(StringUtils.isNotBlank(line)) dic.add(line);
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("读取文件出错");
        }
		return dic;
	}
	
	@Override
    public void label(String id) {
		Article art = articlesRepository.findById(id).orElse(null);
		if(!art.getMark().equals(Article.LABELED)) {
			art.setMark(Article.LABELED);
			art.setMarkTime(sdf.format(new Date()));
			articlesRepository.save(art);
		}
    }
	
	@Override
	public void saveKeyword(String id, String keyword) {
		long startTime = 0;
		long endTime = 0;
		startTime = System.currentTimeMillis();
		if(StringUtils.isBlank(keyword)) return;
		Article art = articlesRepository.findById(id).orElse(null);
		Word word = new Word();
		word.setWord(keyword);
		List<Word> words = art.getWords();
		if(words == null) words = new ArrayList<Word>();
		words.add(word);
		word.setTime(sdf.format(new Date()));
		art.setWords(words);
		word = new Word();
		word.setWord(keyword);
		word.setTime(sdf.format(new Date()));
		word.setOperation(Word.SAVE);
		List<Word> ar = art.getAmendmentRecord();
		if(ar == null) ar = new ArrayList<Word>();
		List<Word> ard = new ArrayList<Word>();
		ard.add(word);
		ard.addAll(ar);
		art.setAmendmentRecord(ard);
		if(art.getMark().equals(Article.UNLABELED)) {
			art.setMark(Article.LABELLING);
			art.setMarkTime(sdf.format(new Date()));
		}
		articlesRepository.save(art);
		endTime = System.currentTimeMillis();
    	System.out.println("记录操作用时"+(endTime - startTime) + "毫秒");
    	
    	startTime = System.currentTimeMillis();
		articleKeywordService.save(keyword);
		endTime = System.currentTimeMillis();
    	System.out.println("articleKeywordService.save用时"+(endTime - startTime) + "毫秒");
    	
    	startTime = System.currentTimeMillis();
		keyWord4File(keyword,Word.SAVE);
		endTime = System.currentTimeMillis();
    	System.out.println("keyWord4File用时"+(endTime - startTime) + "毫秒");
	}
	
	@Override
	public void deleteKeyword(String id, String keyword) {
		try {
        	BoolQueryBuilder query = new BoolQueryBuilder();
        	query.must(QueryBuilders.boolQuery()
      			.should(QueryBuilders.termsQuery("id", id))
        		.should(QueryBuilders.nestedQuery("words",QueryBuilders.matchPhrasePrefixQuery("words.word", keyword), ScoreMode.None))
        	);
			SearchHit[] shits = queryBuilder(new String[]{"y9_articles"}, query,null,null,1, 1000);
			for (int i = 0; i < shits.length; i++) {
				SearchHit shit = shits[i];
				String json = shit.getSourceAsString();
				Article info = Y9JacksonUtil.readValue(json, Article.class);
				List<Word> words = info.getWords();
				Boolean boo = true;
				for(Word word : words) {
					if(word.getWord().equals(keyword)) {
						words.remove(word);
						boo = false;
						break;
					}
				}
				if(boo) continue;
				info.setWords(words);
				Word word = new Word(sdf.format(new Date()),"",keyword);
				if(info.getId().equals(id)) {
					word.setOperation(Word.DELETE);
				}else {
					word.setOperation("其他文章中删除");
				}
				List<Word> ar = info.getAmendmentRecord();
				ar.add(0, word);
				info.setAmendmentRecord(ar);
				articlesRepository.save(info);
				articleKeywordService.del(keyword);
				keyWord4File(keyword,Word.DELETE);
			}
		} catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	private synchronized void keyWord4File(String keyword,String state) {
		if(Word.SAVE.equals(state)) saveKeyWord4File(keyword);
		else if(Word.DELETE.equals(state)) deleteKeyWord4File(keyword);
	}
	
	private void saveKeyWord4File(String keyword) {
		String dicPath = Y9Context.getProperty("y9.common.dicPath");
		File f = new File(dicPath);
		if(!f.exists()){
			f.getParentFile().mkdirs();
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String context = readDictionary(f,null);
		if(!context.contains("\r\n" + keyword + "\r\n")) {
			writeDictionary(dicPath,context + keyword);	
		}
	}
	
	private void deleteKeyWord4File(String keyword) {
		String dicPath = Y9Context.getProperty("y9.common.dicPath");
		File f = new File(dicPath);
		String context = readDictionary(f,keyword);
		writeDictionary(dicPath,context);
	}
	
	/**
     * @Description 读取远程词典
     * @param filePath 词典路径
     * @return String 词典内容*/
    private String readDictionary(File f,String delKey) {
        StringBuffer document = new StringBuffer();
        try {
        	InputStream in  = new FileInputStream(f);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while ((line = reader.readLine()) != null) {
            	String str = line + System.getProperty("line.separator");
            	//System.out.println(str);
            	if(StringUtils.isNotBlank(delKey) && delKey.equals(str.replace("\r", "").replace("\n", ""))) {
            		System.out.println("删除关键字");
            		delKey = null;
            		continue;
            	}
            	if(StringUtils.isNotBlank(str)) document.append(str);
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("读取文件出错");
        }
        return document.toString();
    }

    /**
     * @Description 保存到词典
     * @param filePath 词典路径
     * @param content 修改的内容
     * @return boolean 是否上传成功*/
    private boolean writeDictionary(String dicPath, String content) {
        try {
            File f = new File(dicPath);
            if (!f.exists()) {
                return false;
            }
            OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(f), "UTF-8");
            BufferedWriter writer = new BufferedWriter(write);
            writer.write(content);
            writer.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

	@Override
	public void upload(MultipartFile[] multipartFiles, String type) throws IOException {
		for(MultipartFile multipartFile : multipartFiles) {
			String filename = multipartFile.getOriginalFilename();
	        InputStream inputStream = multipartFile.getInputStream();
	        if (filename.endsWith(".xlsx")) {
	        	readXlsx(inputStream,type);
	        } else {
	        	readXls(inputStream,type);
	        }	
		}
	}
	
	private void readXls(InputStream inputStream, String type) throws IOException {
		@SuppressWarnings("resource")
        HSSFWorkbook sheets = new HSSFWorkbook(inputStream);
        HSSFSheet sheetAt = sheets.getSheetAt(0);
        HSSFRow hr1 = sheetAt.getRow(0);
        
        for (int rowNum = 1; rowNum < sheetAt.getLastRowNum(); rowNum++) {
        	List<TypeField> tfL = new ArrayList<>();
            HSSFRow row = sheetAt.getRow(rowNum);
            if (row != null) {
            	//默认15列,少了再改
            	for(int i=0;i<15;i++) {
                	TypeField tf = new TypeField();
            		row.getCell(i).setCellType(CellType.STRING);
            		String cell = row.getCell(i).getStringCellValue();
            		if(row.getCell(i) == null) continue;
            		if( StringUtils.isNotBlank(cell) ) {
            			hr1.getCell(i).setCellType(CellType.STRING);
            			tf.setFieldName(hr1.getCell(i).getStringCellValue());
            			tf.setFieldContent(cell);
            			tfL.add(tf);
            		}
            	}
            }
            if(tfL.size() > 0) {
            	Article art = new Article();
            	art.setId(UUID.randomUUID().toString());
            	art.setType(type);
            	art.setTypefield(tfL);
            	art.setCreateTime(sdf.format(new Date()));
            	articlesRepository.save(art);
            }
        }
    }
	
    private void readXlsx(InputStream inputStream, String type) throws IOException {
    	@SuppressWarnings("resource")
        XSSFWorkbook sheets = new XSSFWorkbook(inputStream);
        XSSFSheet sheetAt = sheets.getSheetAt(0);
        XSSFRow xr1 = sheetAt.getRow(0);
        int index = getIndex(type);
        for (int rowNum = 1; rowNum <= sheetAt.getLastRowNum(); rowNum++) {
        	List<TypeField> tfL = new ArrayList<>();
        	XSSFRow row = sheetAt.getRow(rowNum);
        	if (row != null) {
            	for(int i=0;i<15;i++) {
            		TypeField tf = new TypeField();
            		if(row.getCell(i) == null) continue;
            		row.getCell(i).setCellType(CellType.STRING);
            		String cell = row.getCell(i).getStringCellValue();
            		if( StringUtils.isNotBlank(cell) ) {
            			xr1.getCell(i).setCellType(CellType.STRING);
            			tf.setFieldName(xr1.getCell(i).getStringCellValue());
            			tf.setFieldContent(cell);
            			tfL.add(tf);
            		}
            	}
            }
        	if(tfL.size() > 0) {
            	Article art = new Article();
            	art.setId(UUID.randomUUID().toString());
            	art.setIndex(index++);
            	art.setType(type);
            	art.setTypefield(tfL);
            	art.setCreateTime(sdf.format(new Date()));
            	articlesRepository.save(art);
            }
        }
    }
    
    private Integer getIndex(String type) throws IOException {
    	Integer index = 0;
    	BoolQueryBuilder query = new BoolQueryBuilder();
    	query.must(QueryBuilders.termQuery("type", type));
    	String sort = "index";
		SearchHit[] shits = queryBuilder(new String[]{"y9_articles"}, query,sort,SortOrder.DESC,1, 1);
		for (int i = 0; i < shits.length; i++) {
			SearchHit shit = shits[i];
			String json = shit.getSourceAsString();
			Article info = Y9JacksonUtil.readValue(json, Article.class);
			index = info.getIndex();
		}
		return ++index;
    }

    @Override
	public void exportData(HttpServletResponse response) {
    	String dicPath = Y9Context.getProperty("y9.common.dicPath");
    	File file = new File(dicPath);
        if (file.exists()) {
            response.addHeader("Content-Disposition", "attachment;fileName=分词");// 设置文件名
            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally { // 做关闭操作
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
	}

	@Override
	public void del(String sy) throws IOException {
		DeleteIndexRequest request = new DeleteIndexRequest("y9_articles");
		elasticsearchClient.indices().delete(request, RequestOptions.DEFAULT);
	}

	@Override
	public void delTweKeyword(ServletResponse response) throws IOException {
		PrintWriter out = null;
		if(response != null) {
			response.setContentType("text/html; charset=utf-8");
			out = response.getWriter();
		}
		
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
        try {
            //滚动查询将SearchHit封装到result中
            while (ArrayUtils.isNotEmpty(hits)) {
                for (SearchHit hit : hits) {
                    //Function<SearchHit, T>, 输入SearchHit，经过操作后，返回T结果
                	String json = hit.getSourceAsString();
                	ArticleKeyword info = Y9JacksonUtil.readValue(json, ArticleKeyword.class);
                	if(StringUtils.isNotBlank(info.getId()) && info.getId().length() <= 2) {
                		articleKeywordRepository.delete(info);
                		deleteKeyWord4File(info.getId());
                		if(out != null) PrintUtil.blue(out, info.getId());
                	}
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
	}

}
