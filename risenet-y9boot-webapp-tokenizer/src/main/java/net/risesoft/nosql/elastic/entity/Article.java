package net.risesoft.nosql.elastic.entity;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文章信息
 *
 */
@Document(indexName = "y9_articles")
@NoArgsConstructor
@Data
public class Article implements Serializable {
	
	private static final long serialVersionUID = 1713211648783962106L;
	
	public static final String UNLABELED = "未标注";
	
	public static final String LABELLING = "标注中";
	
	public static final String LABELED = "已标注";

	/**
     * 主键
     */
    @Id
    @Field(type = FieldType.Keyword)
    private String id;
    
	/**
     * 序号,之后显示根据序号显示
     */
    @Field(type = FieldType.Integer)
    private Integer index;
    
    /**
     * 类型
     */
    @Field(type = FieldType.Keyword, index = true, store = true)
    private String type;
    
    /**
	 * 类型字段信息
	 */
	@Field(type = FieldType.Nested)
	private List<TypeField> typefield;
	
	/**
	 * 创建时间
	 */
	@Field(type = FieldType.Keyword, index = true, store = true)
	private String createTime;
	
	/**
	 * 本文档分词
	 */
	@Field(type = FieldType.Nested)
	private List<Word> words;
	
	/**
	 * 修改记录
	 */
	@Field(type = FieldType.Nested)
	private List<Word> amendmentRecord;
	
	/**
     * 是否标注(未标注/标注中/已标注)
     */
	@Field(type = FieldType.Keyword, index = true, store = true)
    private String mark = Article.UNLABELED;
    
    /**
	 * 标注时间
	 */
	@Field(type = FieldType.Keyword, index = true, store = true)
	private String markTime;
	
	private List<String> dic;
	
}
