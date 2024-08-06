package net.risesoft.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarUtil {

	/**
	 * 近一个月日期List
	 * 
	 * @return
	 */
	public static List<String> getNearlyMonth() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar begin = Calendar.getInstance();// 得到一个Calendar的实例
		begin.setTime(new Date()); // 设置时间为当前时间
		begin.add(Calendar.MONTH, -1);// 月份减1
		begin.add(Calendar.DATE, +1);// 日期加1
		Calendar end = Calendar.getInstance();
		Long startTime = begin.getTimeInMillis();
		Long endTime = end.getTimeInMillis();
		Long oneDay = 1000 * 60 * 60 * 24l;// 一天的时间转化为ms
		List<String> dates = new ArrayList<String>();
		Long time = startTime;
		int i = 0;
		while (time <= endTime) {
			Date d = new Date(time);
			dates.add(i, df.format(d));
			i++;
			time += oneDay;
		}
		return dates;
	}

	/**
	 * 近一周日期List
	 * 
	 * @return
	 */
	public static List<String> getNearlyWeek() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar begin = Calendar.getInstance();// 得到一个Calendar的实例
		begin.setTime(new Date()); // 设置时间为当前时间
		begin.add(Calendar.DATE, -6);
		Calendar end = Calendar.getInstance();
		Long startTime = begin.getTimeInMillis();
		Long endTime = end.getTimeInMillis();
		Long oneDay = 1000 * 60 * 60 * 24l;// 一天的时间转化为ms
		List<String> dates = new ArrayList<String>();
		Long time = startTime;
		int i = 0;
		while (time <= endTime) {
			Date d = new Date(time);
			dates.add(i, df.format(d));
			i++;
			time += oneDay;
		}
		return dates;
	}
	
	/**
	 * 获取时间段
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static List<String> getDateList(String startDate, String endDate){
		List<String> list = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(sdf.parse(startDate));
			list.add(startDate);
			if(!startDate.equals(endDate)){
				while (true){
					calendar.add(Calendar.MONTH, +1);
					String preDate = sdf.format(calendar.getTime());
					list.add(preDate);
					if(endDate.equals(preDate)){
						break;
					}
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return list;
	}
	
}
