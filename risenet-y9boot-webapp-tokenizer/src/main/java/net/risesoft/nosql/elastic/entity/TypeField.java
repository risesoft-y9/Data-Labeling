package net.risesoft.nosql.elastic.entity;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 字段信息
 * 
 */
@NoArgsConstructor
@Data
public class TypeField {

	/**
	 * 字段名称
	 */
	@Field(type = FieldType.Keyword, index = true, store = true)
	private String fieldName;
	
	/**
	 * 字段内容
	 */
	@Field(type = FieldType.Text, index = true, store = true, searchAnalyzer = "ik_smart", analyzer = "ik_smart")
	private String fieldContent;
	
}
