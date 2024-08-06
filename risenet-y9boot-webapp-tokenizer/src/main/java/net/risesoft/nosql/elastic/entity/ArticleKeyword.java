package net.risesoft.nosql.elastic.entity;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document(indexName = "articles_keyword")
@NoArgsConstructor
@Data
public class ArticleKeyword implements Serializable{
	
	private static final long serialVersionUID = 2864514534274258947L;
	
	/**
	 * 主键
	 */
	@Id
	@Field(type = FieldType.Keyword)
	private String id;
	
	/**
	 * 关键字
	 */
	@Field(type = FieldType.Text, index = true, store = true, searchAnalyzer = "ik_smart", analyzer = "ik_smart")
	private String keyword;
	
	/**
	 * 最后修改时间(yyyy-MM-dd HH:mm:ss)
	 */
	@Field(type = FieldType.Date , pattern = "yyyy-MM-dd HH:mm:ss", format = DateFormat.custom, store = true)
	private Date endTime;

}
