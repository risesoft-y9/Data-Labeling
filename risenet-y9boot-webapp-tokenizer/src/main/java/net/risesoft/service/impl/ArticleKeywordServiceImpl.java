package net.risesoft.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.risesoft.nosql.elastic.entity.ArticleKeyword;
import net.risesoft.nosql.elastic.repository.ArticleKeywordRepository;
import net.risesoft.service.ArticleKeywordService;

@Service(value = "articleKeywordService")
public class ArticleKeywordServiceImpl implements ArticleKeywordService{
	
	@Autowired
	private ArticleKeywordRepository articleKeywordRepository;

	@Override
	public void save(String keyword) {
		ArticleKeyword ak = articleKeywordRepository.findById(keyword).orElse(new ArticleKeyword());
		ak.setId(keyword);
		ak.setKeyword(keyword);
		ak.setEndTime(new Date());
		articleKeywordRepository.save(ak);
	}

	@Override
	public void del(String keyword) {
		ArticleKeyword ak = articleKeywordRepository.findById(keyword).orElse(null);
		if(ak != null) {
			articleKeywordRepository.delete(ak);	
		}
	}
	
	

}
