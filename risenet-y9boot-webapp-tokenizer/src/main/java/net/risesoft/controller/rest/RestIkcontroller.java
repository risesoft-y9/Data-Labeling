package net.risesoft.controller.rest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import net.risesoft.service.ArtcleService;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.json.Y9JacksonUtil;
import net.risesoft.y9.util.Y9Util;
import net.risesoft.y9public.model.Message;
@Controller
@RequestMapping(value = "/rest/Ik")
@CrossOrigin
public class RestIkcontroller {

	@Resource(name = "artclesService")
	private ArtcleService artclesService;

    @RequestMapping(value = "/analyse")
    public void analyse(String id, HttpServletResponse response) {
        Message message = new Message();
        message.setCode(Message.STATUS_FAIL);
        message.setMsg("检索失败");
        try {
            message.setData(getText(artclesService.wenzhangneirong(id)));
            message.setCode(Message.STATUS_SUCCESS);
            message.setMsg("检索成功");
        } catch (Exception e) {
            message.setMsg("检索异常");
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JacksonUtil.writeValueAsString(message));
        return;
    }

    public String getText(String text) {
        RestTemplate restTemplate = new RestTemplate();
        String username = Y9Context.getProperty("spring.elasticsearch.rest.username");
        String password = Y9Context.getProperty("spring.elasticsearch.rest.password");
        if(StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(username,password));
        String url = "http://" + Y9Context.getProperty("spring.elasticsearch.rest.uris") + "/_analyze?pretty=true";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> map = new HashMap<>();
        map.put("analyzer", "ik_smart");
        map.put("text", text);
        HttpEntity<String> httpEntity = new HttpEntity<>(JSON.toJSONString(map), headers);
        ResponseEntity<String> result = restTemplate.postForEntity(url, httpEntity, String.class);
        JSONObject jsonObject = JSONObject.parseObject(result.getBody());
        return jsonObject.toString();
    }
    
//    @RequestMapping(value = "/del")
//    public void del(String sy, HttpServletResponse response) throws IOException {
//    	artclesService.del(sy);
//    }

}
