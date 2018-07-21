package zabi.minecraft.gdlauncher;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Objects;

import com.google.gson.JsonObject;

public class IPCDispatcher {
	
	public static enum GameType {
		NONE, SINGLEPLAYER, MULTIPLAYER, LAN 
	}
	
	
	public static void setGamePlaying(GameType type, String name, String address, String motd, String iconBase64) {
		Objects.requireNonNull(type);
		JsonObject json = new JsonObject();
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
		if (iconBase64!=null) {
			json.addProperty("icon", iconBase64);
		}
		try {
			Socket connection = new Socket("localhost", GDLauncherCompanion.port);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
			bw.write(json.toString());
			bw.close();
			connection.close();
		} catch (UnknownHostException e) {
			Log.e("Cannot connect to GDLauncher service", e);
		} catch (IOException e) {
			Log.e("Cannot connect to GDLauncher service", e);
		}
	}
}
