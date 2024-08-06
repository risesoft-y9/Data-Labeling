package net.risesoft.nosql.elastic.entity;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 词
 * 
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Word {
	
	public static final String SAVE = "新增";
	
	public static final String DELETE = "删除";

	/**
	 * 更新时间
	 */
	@Field(type = FieldType.Keyword, index = true, store = true)
	private String time;
	
	/**
	 * 操作 (新增/删除)
	 */
	@Field(type = FieldType.Keyword, index = true, store = true)
	private String operation;
	
	/**
	 * 关键词
	 */
	@Field(type = FieldType.Text, index = true, store = true, searchAnalyzer = "ik_smart", analyzer = "ik_smart")
	private String word;
	
}
