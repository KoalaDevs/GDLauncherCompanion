package zabi.minecraft.gdlauncher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import zabi.minecraft.gdlauncher.IPCDispatcher.GameType;

@Mod(
		modid = GDLauncherCompanion.MOD_ID, 
		name = GDLauncherCompanion.MOD_NAME, 
		version = GDLauncherCompanion.MOD_VERSION, 
		clientSideOnly=true,
		acceptedMinecraftVersions = GDLauncherCompanion.MC_VERSION
		)

public class GDLauncherCompanion {
	
	public static final String MOD_ID = "gdlaunchercompanion";
	public static final String MOD_NAME = "GDLauncher Companion Mod";
	public static final String MOD_VERSION = "0.0.1a";
	public static final String MC_VERSION = "[1.8,1.14)";
	
	public static int port = 0;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		ModConfig.init(evt.getSuggestedConfigurationFile());
		
		String pathPortFile = evt.getSuggestedConfigurationFile().getParentFile().getParentFile().toString();
		File portFile = new File(pathPortFile + File.separatorChar + "GDLPortComms.cfg");
		
		if (!portFile.exists() || portFile.isDirectory()) {
			Log.e("Cannot find rendez-vous port. Use latest GDLauncher and GDLauncherCompanion mod");
			return;
		}
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(portFile));
			port = Integer.parseInt(br.readLine());
			br.close();
		} catch (Exception e) {
			Log.e("Invalid rendez-vous port configuration. Use latest GDLauncher and GDLauncherCompanion mod", e);
			return;
		}
		
		if (port<1000) {
			Log.e("Invalid rendez-vous port found. Use latest GDLauncher and GDLauncherCompanion mod");
			return;
		}
		Log.i("GDLauncherCompanion is using port "+port);
		MinecraftForge.EVENT_BUS.register(this);
		GameStatusListener.listen();
	}

	@SubscribeEvent
	public void clientDisconnect(ClientDisconnectionFromServerEvent evt) {
		if (ModConfig.sharePlayingInfo) {
			IPCDispatcher.setGamePlaying(GameType.NONE, null, null, null, null);
			GameStatusListener.listen();
		}
	}
	
	@SubscribeEvent
	public void initGui(InitGuiEvent.Post evt) {
		if (ModConfig.sharePlayingInfo && evt.getGui() instanceof GuiMainMenu) {
			IPCDispatcher.setGamePlaying(GameType.NONE, null, null, null, null);
			GameStatusListener.listen();
		}
	}
}
