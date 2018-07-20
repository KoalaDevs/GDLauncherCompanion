package zabi.minecraft.gdlauncher;

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
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		ModConfig.init(evt.getSuggestedConfigurationFile());
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
