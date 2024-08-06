package net.risesoft.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry){
	    // 设置允许跨域的路径
	    registry.addMapping("/**")
	            // 设置允许跨域请求的域名
	            .allowedOrigins("*")
	            // 是否允许证书
	            .allowCredentials(true)
	            // 设置允许的方法
	            .allowedMethods("GET", "POST", "DELETE", "PUT")
	            // 设置允许的header属性
	            .allowedHeaders("*")
	            // 跨域允许时间
	            .maxAge(3600);
	}

}
