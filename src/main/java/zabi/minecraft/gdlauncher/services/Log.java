package zabi.minecraft.gdlauncher.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import zabi.minecraft.gdlauncher.GDLauncherCompanion;

public class Log {
	
	private static final Logger logger = LogManager.getLogger(GDLauncherCompanion.MOD_ID);
	
	public static void i(String s) {
		logger.info(s);
	}
	
	public static void w(String s) {
		logger.warn(s);
	}
	
	public static void e(String s) {
		logger.error(s);
	}
	
	public static void f(String s) {
		logger.fatal(s);
	}
	
	public static void e(String s, Throwable e) {
		e(s);
		logger.error(e);
	}
	
	public static void f(String s, Throwable e) {
		f(s);
		logger.fatal(e);
	}
	
	public static void d(String s) {
		logger.debug(s);
	}
}
