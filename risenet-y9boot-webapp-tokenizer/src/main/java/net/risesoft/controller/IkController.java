package net.risesoft.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import net.risesoft.y9.Y9Context;

@Controller
@RequestMapping(value = "/ik")
public class IkController {
	
	/**
     * @Description 加载远程词典内容
     * @param request http请求
     * @return String 词典内容*/
    @RequestMapping(value = "/loadDictionary", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String loadDictionary(HttpServletRequest request) {
        return readDictionary(getFilePath());
    }
    
    /**
     * @Description 获取词典路径
     * @param request HTTP请求
     * @return String 词典路径*/
    private String getFilePath() {
        return Y9Context.getProperty("y9.common.tomcatURL") + "hot_ext.dic";
    }

    /**
     * @Description 读取远程词典
     * @param filePath 词典路径
     * @return String 词典内容*/
    private String readDictionary(String filePath) {
        StringBuffer document = new StringBuffer();
        try {
            URL url = new URL(filePath);
            URLConnection conn = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                document.append(line + System.getProperty("line.separator"));
            }
            reader.close();
        } catch (MalformedURLException e) {
            System.out.println("Unable to connect to URL: " + filePath);
        } catch (IOException e) {
            System.out.println("IOException when connecting to URL: " + filePath);
        }
        return document.toString();
    }
    
    /**
     * @Description 保存词典
     * @param context 词典内容
     * @param request HTTP请求
     * @return boolean 是否成果能够*/
    @RequestMapping(value = "/saveDictionary", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String saveDictionary(String text, HttpServletRequest request) {
        String dicPath = Y9Context.getProperty("y9.common.dicPath");
        String context = readDictionary(getFilePath());
        if(StringUtils.isBlank(text)) return "无内容";
        if(context.contains(" "+text.trim()+" ")) return "词典中已存在";
        String content = context + " " + text.trim() + " ";
        boolean result = writeDictionary(dicPath,content);
        if(result) return "成功";
        return "失败";
    }



    /**
     * @Description 上传词典
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
        	System.out.println("失败");
            return false;
        }
    }
    
    /**
     * @Description 删除词典中内容
     * @param context 词典内容
     * @param request HTTP请求
     * @return boolean 是否成果能够*/
    @RequestMapping(value = "/delDictionary", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String delDictionary(String text, HttpServletRequest request) {
        String dicPath = Y9Context.getProperty("y9.common.dicPath");
        String context = readDictionary(getFilePath());
        if(StringUtils.isBlank(text)) return "无内容";
        if(context.contains(" "+text.trim()+" ")) return "词典中不存在";
        String content = context.replace(" " + text.trim() + " ","");
        boolean result = writeDictionary(dicPath,content);
        if(result) return "成功";
        return "失败";
    }

    
}
