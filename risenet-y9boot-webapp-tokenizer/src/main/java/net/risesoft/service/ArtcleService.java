package net.risesoft.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import net.risesoft.nosql.elastic.entity.Article;
import net.risesoft.nosql.elastic.entity.Word;

public interface ArtcleService {
	
	Map<String, Object> getTitle(String type,String mark,Integer startIndex,Integer endIndex,Integer page, Integer rows);
	
	Map<String, Integer> getArtcleType();
	
	List<Word> getRecord(String id);
	
	List<String> getKeyword(String id);
	
	Article getArtcle(String id);
	
	/**
	 * 临时使用,之后会改
	 * @return
	 */
	String wenzhangneirong(String id);
	
	void saveKeyword(String id, String keyword);
	
	void deleteKeyword(String id, String keyword);
	
	void label(String id);
	
	void upload(MultipartFile[] multipartFiles, String type) throws IOException ;
	
	/**
	 * 导出
	 * @return
	 */
	void exportData(HttpServletResponse response);
	
	
	void del(String sy) throws IOException;
	
	void delTweKeyword(ServletResponse response) throws IOException;

}
