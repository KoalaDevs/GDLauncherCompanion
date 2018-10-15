package zabi.minecraft.gdlauncher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import zabi.minecraft.gdlauncher.services.Log;
import zabi.minecraft.gdlauncher.services.ModConfig;
import zabi.minecraft.gdlauncher.utils.GameStatusListener;
import zabi.minecraft.gdlauncher.utils.IPCSimulator;
import zabi.minecraft.gdlauncher.utils.IPCSimulator.GameType;

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
	public static final String MOD_VERSION = "1.1";
	public static final String MC_VERSION = "[1.8.9,1.13)";

	public static int port = 0;
	
	private static GDLauncherCompanion instance;

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		instance = this;
		ModConfig.init(evt.getSuggestedConfigurationFile());

		String pathPortFile = evt.getSuggestedConfigurationFile().getParentFile().getParentFile().toString();
		File portFile = new File(pathPortFile + File.separatorChar + "GDLPortComms.cfg");

		if (!portFile.exists() || portFile.isDirectory()) {
			Log.e("Cannot find rendez-vous port. Falling back to default (2002)");
			port = 2002;
		} else {
			try {
				BufferedReader br = new BufferedReader(new FileReader(portFile));
				port = Integer.parseInt(br.readLine());
				br.close();
			} catch (Exception e) {
				Log.e("Invalid rendez-vous port configuration. Falling back to default (2002)", e);
				port = 2002;
			}
		}

		if (port<1024) {
			Log.e("Invalid rendez-vous port found. Port needs to be > 1024");
			return;
		}
		Log.i("GDLauncherCompanion is using port "+port);
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(LaunchListener.INSTANCE);
		GameStatusListener.listen();
	}

	@SubscribeEvent
	public void clientDisconnect(ClientDisconnectionFromServerEvent evt) {
		if (ModConfig.sharePlayingInfo) {
			IPCSimulator.setGamePlaying(GameType.NONE, null, null, null);
			GameStatusListener.listen();
		}
	}
	
	private static final Field player = ReflectionHelper.findField(Minecraft.class, "player", "thePlayer", "field_71439_g", "h");
	private static final Field world = ReflectionHelper.findField(Minecraft.class, "world", "theWorld", "field_71441_e", "f");
	
	
	@SubscribeEvent
	public void playerTick(ClientTickEvent evt) {
		try {
			World worldOb = (World) world.get(Minecraft.getMinecraft());
			if (worldOb!=null && worldOb.getTotalWorldTime()%200 == 0) {
				EntityPlayer p = (EntityPlayer) player.get(Minecraft.getMinecraft());
				IPCSimulator.updatePlayerStats(p.getHealth(), p.experienceLevel, p.getTotalArmorValue(), p.getFoodStats().getFoodLevel(), worldOb.provider.getDimensionType().getName().toLowerCase());
			}
		} catch (Exception e) {
			e.printStackTrace();
			crashed();
		}
	}
	
	public static void crashed() {
		MinecraftForge.EVENT_BUS.unregister(instance);
		MinecraftForge.EVENT_BUS.unregister(LaunchListener.INSTANCE);
		GameStatusListener.stopListening();
		IPCSimulator.crashed();
	}

	static class LaunchListener {

		private static final Field guiField = ReflectionHelper.findField(GuiScreenEvent.class, "gui"); //1.8 version doesn't have the getGui() method

		public static final LaunchListener INSTANCE = new LaunchListener();
		
		private LaunchListener() {
		}
		
		@SubscribeEvent
		public void initGui(InitGuiEvent.Post evt) {
			try {
				if (ModConfig.sharePlayingInfo && guiField.get(evt) instanceof GuiMainMenu) {
					IPCSimulator.setGamePlaying(GameType.NONE, null, null, null);
					GameStatusListener.listen();
					MinecraftForge.EVENT_BUS.unregister(this);
				}
			} catch (Exception e) {
				e.printStackTrace();
				crashed();
			}
		}

	}
}
