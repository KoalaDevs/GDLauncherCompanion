package zabi.minecraft.gdlauncher;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ModConfig {
	
	public static Configuration config;
	
	public static boolean sharePlayingInfo = true;

	//@Config.Comment()
	public static boolean shareWorldName = false;
	
	//@Config.Comment()
	public static boolean shareServer = false;
	
	public static void init(File file) {
		config = new Configuration(file);
		config.load();
		sharePlayingInfo = config.getBoolean("sharePlayingInfo", "General", true, "If set to false disables sharing your status with friends (eg \"In the main menu\", \"In a server\"...)");
		shareWorldName = config.getBoolean("shareWorldName", "General", false, "If set to true your GDL friends will know the name of the single player world you're playing");
		shareServer = config.getBoolean("shareServer", "General", false, "If set to true your GDL friends will know the name and ip of the server you're playing in");
		
		if (config.hasChanged()) {
			config.save();
		}
	}
	
}
