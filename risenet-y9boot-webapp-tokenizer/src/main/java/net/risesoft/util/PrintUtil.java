package net.risesoft.util;

import java.io.PrintWriter;

public class PrintUtil {
	
	public static void black(PrintWriter out,String str) {
		System.out.println(str);
		if(out != null) {
			out.println("<script>");
	    	out.println("document.write('<div><p>" + str + "</p></div>');");
	    	out.println("</script>");
	    	out.flush();
		}
	}
	public static void blue(PrintWriter out,String str) {
		System.out.println(str);
		if(out != null) {
			out.println("<script>");
	    	out.println("document.write('<div><p style="+'"'+ "color:blue;"+'"'+ ">" + str + "</p></div>');");
	    	out.println("</script>");
	    	out.flush();
		}
	}
	public static void red(PrintWriter out,String str) {
		System.out.println(str);
		if(out != null) {
			out.println("<script>");
	    	out.println("document.write('<div><p style="+'"'+ "color:red;"+'"'+ ">" + str + "</p></div>');");
	    	out.println("</script>");
	    	out.flush();
		}
	}
	
}
