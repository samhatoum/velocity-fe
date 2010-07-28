package com.hatoum.velocity;

public class Utilities {

	public static String getLog(Exception e) {
		String build = "\r\n" + e.toString() + "\r\n";
		if ("CONSOLE".equals(Resources.getProperty("logTo"))) {
			e.printStackTrace();
		} else {
			StackTraceElement[] stackTraceElements = e.getStackTrace();
			for (StackTraceElement eachStackTraceElement : stackTraceElements) {
				build += "\t" + eachStackTraceElement + "\r\n";
			}
		}
		return build;
	}
}
