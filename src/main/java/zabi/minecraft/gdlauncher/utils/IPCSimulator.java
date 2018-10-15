package zabi.minecraft.gdlauncher.utils;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Objects;

import com.google.gson.JsonObject;

import zabi.minecraft.gdlauncher.GDLauncherCompanion;
import zabi.minecraft.gdlauncher.services.Log;

public class IPCSimulator {
	
	public static enum GameType {
		NONE, SINGLEPLAYER, MULTIPLAYER 
	}
	
	public static void updatePlayerStats(float health, int xp, float armor, float hunger, String dimensionName) {
		JsonObject json = new JsonObject();
		json.addProperty("messageType", "stats");
		json.addProperty("health", health);
		json.addProperty("xp", xp);
		json.addProperty("armor", armor);
		json.addProperty("dimension", dimensionName);
		json.addProperty("hunger", hunger);
		sendPayloadToServer(json);
	}
	
	public static void crashed() {
		JsonObject json = new JsonObject();
		json.addProperty("messageType", "modCrashedSilently");
		sendPayloadToServer(json);
	}
	
	public static void setGamePlaying(GameType type, String name, String address, String motd) {
		Objects.requireNonNull(type);
		JsonObject json = new JsonObject();
		json.addProperty("messageType", "world");
		json.addProperty("type", type.ordinal());
		if (name!=null) {
			json.addProperty("name", name);
		}
		if (address!=null) {
			json.addProperty("address", address);
		}
		if (motd!=null) {
			json.addProperty("motd", motd);
		}
		sendPayloadToServer(json);
	}
	
	public static void sendPayloadToServer(JsonObject json) {
		String data = json.toString();
		if (data != null) {
			try {
				Socket connection = new Socket("localhost", GDLauncherCompanion.port);
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
				bw.write(data+"\n");
				bw.close();
				connection.close();
			} catch (Exception e) {
				Log.e("Cannot connect to GDLauncher service\n" + e);
			}
		}
	}
}
