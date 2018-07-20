package zabi.minecraft.gdlauncher;

import java.util.Objects;

import com.google.gson.JsonObject;

public class IPCDispatcher {
	
	public static enum GameType {
		MULTIPLAYER, LAN, SINGLEPLAYER, NONE
	}
	
	
	public static void setGamePlaying(GameType type, String name, String address, String motd, String iconBase64) {
		Objects.requireNonNull(type);
		JsonObject json = new JsonObject();
		json.addProperty("type", type.name().toLowerCase());
		if (name!=null) {
			json.addProperty("name", name);
		}
		if (address!=null) {
			json.addProperty("address", address);
		}
		if (motd!=null) {
			json.addProperty("motd", motd);
		}
		if (iconBase64!=null) {
			json.addProperty("icon", iconBase64);
		}
		Log.i(json.toString());
	}
}
