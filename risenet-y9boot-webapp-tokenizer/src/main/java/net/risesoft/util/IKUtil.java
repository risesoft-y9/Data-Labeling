package net.risesoft.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class IKUtil {

	// 用来存放停用词的集合
	private static Set<String> stopWordSet = new HashSet<String>();

	@SuppressWarnings("resource")
	public static Set<String> getStopWords() throws IOException {
		if (stopWordSet.isEmpty()) {
			// 读入停用词文件
			BufferedReader StopWordFileBr = new BufferedReader(new InputStreamReader(new FileInputStream(new File("F:\\project\\git\\weibo\\stopwords\\中文停用词库.txt")), "utf-8"));
			// 初如化停用词集
			String stopWord = null;
			for (; (stopWord = StopWordFileBr.readLine()) != null;) {
				stopWordSet.add(stopWord);
			}
		}
		return stopWordSet;
	}
}
