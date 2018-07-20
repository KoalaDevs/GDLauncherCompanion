package zabi.minecraft.gdlauncher;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import zabi.minecraft.gdlauncher.IPCDispatcher.GameType;

public class GameStatusListener {
	
	public static final GameStatusListener INSTANCE = new GameStatusListener();
	
	private static boolean listening = false;
	
	private GameStatusListener() {}
	
	@SubscribeEvent
	public void clientConnect(PlayerTickEvent evt) {
		if (Minecraft.getMinecraft().world!=null) {
			notifyGameChange();
			stopListening();
		}
	}

	private static void notifyGameChange() {
		if (Minecraft.getMinecraft().isSingleplayer()) {
			IPCDispatcher.setGamePlaying(GameType.SINGLEPLAYER, ModConfig.shareWorldName?Minecraft.getMinecraft().getIntegratedServer().getWorldName():null, null, null, null);
		} else {
			ServerData data = Minecraft.getMinecraft().getCurrentServerData();
			if (data.isOnLAN()) {
				if (ModConfig.shareServer) {
					IPCDispatcher.setGamePlaying(GameType.LAN, data.serverName, data.serverIP, null, null);
				} else {
					IPCDispatcher.setGamePlaying(GameType.LAN, null, null, null, null);
				}
			} else {
				if (ModConfig.shareServer) {
					IPCDispatcher.setGamePlaying(GameType.MULTIPLAYER, data.serverName, data.serverIP, data.serverMOTD, data.getBase64EncodedIconData());
				} else {
					IPCDispatcher.setGamePlaying(GameType.MULTIPLAYER, null, null, null, null);
				}
				
			}
		}
	}
	
	public static void listen() {
		if (!listening && ModConfig.sharePlayingInfo) {
			listening=true;
			MinecraftForge.EVENT_BUS.register(INSTANCE);
		}
	}
	
	public static void stopListening() {
		if (listening) {
			listening=false;
			MinecraftForge.EVENT_BUS.unregister(INSTANCE);
		}
	}
}
