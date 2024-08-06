package net.risesoft.controller.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import net.risesoft.service.ArtcleService;
import net.risesoft.y9.json.Y9JacksonUtil;
import net.risesoft.y9.util.Y9Util;
import net.risesoft.y9public.model.Message;



@RestController
@RequestMapping("/rest/articles")
@CrossOrigin
public class RestArtcleController {
	
	@Resource(name = "artclesService")
	private ArtcleService artclesService;
	
	/**
	 * 获取文章类型
	 * @return
	 */
	@RequestMapping(value = "/artcleType")
    public void getArtcleType(HttpServletResponse response) {
        Message message = new Message();
        message.setCode(Message.STATUS_FAIL);
        message.setMsg("检索失败");
        try {
        	Map<String,Integer> map = artclesService.getArtcleType();
            message.setData(map);
        	//List<String> list2 = new ArrayList<String>();
        	//list2.add("国务院");
        	//list2.add("国防部");
        	//list2.add("政务解读");
        	//message.setData(list2);
            message.setCode(Message.STATUS_SUCCESS);
            message.setMsg("检索成功");
        } catch (Exception e) {
            message.setMsg("检索异常");
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JacksonUtil.writeValueAsString(message));
    }
	
	/**
	 * 获取标题
	 * @return
	 */
	@RequestMapping(value = "/title")
    public void getTitle(String type, String mark ,Integer startIndex,Integer endIndex,Integer page, Integer rows, HttpServletResponse response) {
        Message message = new Message();
        message.setCode(Message.STATUS_FAIL);
        message.setMsg("检索失败");
        try {
            Map<String, Object> map = artclesService.getTitle(type,mark,startIndex,endIndex,page, rows);
            message.setData(map);
            message.setCode(Message.STATUS_SUCCESS);
            message.setMsg("检索成功");
        } catch (Exception e) {
            message.setMsg("检索异常");
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JacksonUtil.writeValueAsString(message));
    }
	
	/**
	 * 获取文种内容
	 * @return
	 */
	@RequestMapping(value = "/artcle")
    public void getContent(String id, HttpServletResponse response) {
        Message message = new Message();
        message.setCode(Message.STATUS_FAIL);
        message.setMsg("检索失败");
        try {
            message.setData(artclesService.getArtcle(id));
            message.setCode(Message.STATUS_SUCCESS);
            message.setMsg("检索成功");
        } catch (Exception e) {
            message.setMsg("检索异常");
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JacksonUtil.writeValueAsString(message));
    }
	
	/**
	 * 获取记录
	 * @return
	 */
	@RequestMapping(value = "/record")
    public void record(String id, HttpServletResponse response) {
        Message message = new Message();
        message.setCode(Message.STATUS_FAIL);
        message.setMsg("获取失败");
        try {
            message.setData(artclesService.getRecord(id));
            message.setCode(Message.STATUS_SUCCESS);
            message.setMsg("获取成功");
        } catch (Exception e) {
            message.setMsg("获取异常");
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JacksonUtil.writeValueAsString(message));
    }
	
	/**
	 * 获取本文关键词
	 * @return
	 */
	@RequestMapping(value = "/keyword")
    public void keyWord(String id, HttpServletResponse response) {
        Message message = new Message();
        message.setCode(Message.STATUS_FAIL);
        message.setMsg("获取失败");
        try {
            message.setData(artclesService.getKeyword(id));
            message.setCode(Message.STATUS_SUCCESS);
            message.setMsg("获取成功");
        } catch (Exception e) {
            message.setMsg("获取异常");
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JacksonUtil.writeValueAsString(message));
    }
	
	/**
	 * 获取文章内容
	 * @return
	 */
	@RequestMapping(value = "/artcleContent")
    public void artcleContent(String id, HttpServletResponse response) {
        Message message = new Message();
        message.setCode(Message.STATUS_FAIL);
        message.setMsg("获取失败");
        try {
            message.setData(artclesService.wenzhangneirong(id));
            message.setCode(Message.STATUS_SUCCESS);
            message.setMsg("获取成功");
        } catch (Exception e) {
            message.setMsg("获取异常");
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JacksonUtil.writeValueAsString(message));
    }
	
	/**
	 * 添加关键词
	 * @return
	 */
	@RequestMapping(value = "/saveKeyword")
    public void saveKeyword(String id, String keywords, HttpServletResponse response) {
		long startTime = 0;
		long endTime = 0;
		startTime = System.currentTimeMillis();
        Message message = new Message();
        message.setCode(Message.STATUS_FAIL);
        message.setMsg("添加失败");
        try {
        	String[] kws = keywords.split(" ");
        	for(String kw : kws) {
        		artclesService.saveKeyword(id,kw);
        	}
        	endTime = System.currentTimeMillis();
        	System.out.println("saveKeyword用时"+(endTime - startTime) + "毫秒");
        	Map<String, Object> map = new HashMap<String, Object>();
        	
        	startTime = System.currentTimeMillis();
        	map.put("keyword", artclesService.getKeyword(id));
        	endTime = System.currentTimeMillis();
        	System.out.println("获取keyword用时"+(endTime - startTime) + "毫秒");
        	
        	startTime = System.currentTimeMillis();
        	map.put("record", artclesService.getRecord(id));
        	endTime = System.currentTimeMillis();
        	System.out.println("获取record用时"+(endTime - startTime) + "毫秒");
        	
        	message.setData(map);
            message.setCode(Message.STATUS_SUCCESS);
            message.setMsg("添加成功");
        } catch (Exception e) {
            message.setMsg("添加异常");
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JacksonUtil.writeValueAsString(message));
    }
	
	/**
	 * 删除关键词
	 * @return
	 */
	@RequestMapping(value = "/deleteKeyword")
    public void deleteKeyword(String id, String keyword, HttpServletResponse response) {
        Message message = new Message();
        message.setCode(Message.STATUS_FAIL);
        message.setMsg("删除失败");
        try {
        	artclesService.deleteKeyword(id,keyword);
        	Map<String, Object> map = new HashMap<String, Object>();
        	map.put("keyword", artclesService.getKeyword(id));
        	map.put("record", artclesService.getRecord(id));
        	message.setData(map);
            message.setCode(Message.STATUS_SUCCESS);
            message.setMsg("删除成功");
        } catch (Exception e) {
            message.setMsg("删除异常");
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JacksonUtil.writeValueAsString(message));
    }
    
    /**
	 * 标注完成
	 * @return
	 */
	@RequestMapping(value = "/label")
    public void label(String id, HttpServletResponse response) {
        Message message = new Message();
        message.setCode(Message.STATUS_FAIL);
        message.setMsg("标注失败");
        try {
        	artclesService.label(id);
            message.setCode(Message.STATUS_SUCCESS);
            message.setMsg("标注完成");
        } catch (Exception e) {
            message.setMsg("标注异常");
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JacksonUtil.writeValueAsString(message));
    }

	/**
	 * 上传
	 * @return
	 */
	//@ResponseBody
	@RequestMapping(value = "/upload")
	public void upload(
			MultipartFile[] files,
			String type,
			HttpServletResponse response) 
	{
		Message message = new Message();
		message.setCode(Message.STATUS_FAIL);
		
		for(MultipartFile file : files) {
			//对前端传递的文件进行校验
	        if (file == null || file.getSize() == 0) {
	        	message.setMsg("文件上传错误，重新上传");
	        	Y9Util.renderJson(response, Y9JacksonUtil.writeValueAsString(message));
	        	return;
	        }
	        //获取文件名称 判断文件是否为 Excel
	        String filename = file.getOriginalFilename();
	        if (!(filename.endsWith(".xls") || filename.endsWith(".xlsx"))) {
	        	message.setMsg("文件上传格式有误，请重新上传");
	        	Y9Util.renderJson(response, Y9JacksonUtil.writeValueAsString(message));
	        	return;
	        }	
		}
        try {
        	artclesService.upload(files, type);
        	message.setMsg("上传成功");
    		message.setCode(Message.STATUS_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            message.setMsg("上传异常");
        }
		Y9Util.renderJson(response, Y9JacksonUtil.writeValueAsString(message));
		return;
	}
	
	/**
	 * 导出
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/exportData")
	public void exportData(HttpServletResponse response) {
		artclesService.exportData(response);
	}
	
	/**
	 * 删除2字关键词
	 * @return
	 * @throws IOException 
	 */
	@ResponseBody
	@RequestMapping(value = "/delTweKeyword")
	public void delTweKeyword(ServletResponse response) throws IOException {
		artclesService.delTweKeyword(response);
	}
	
}
