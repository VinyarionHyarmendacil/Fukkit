package vinyarion.fukkit.main;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import com.google.common.collect.Maps;

import lotr.common.fac.LOTRAlignmentBonusMap;
import lotr.common.fac.LOTRAlignmentValues;
import lotr.common.LOTRDimension;
import lotr.common.fac.LOTRFaction;
import lotr.common.LOTRLevelData;
import lotr.common.LOTRMod;
import lotr.common.LOTRPlayerData;
import lotr.common.entity.item.LOTREntityPortal;
import lotr.common.network.LOTRPacketAlignmentBonus;
import lotr.common.network.LOTRPacketHandler;
import lotr.common.world.LOTRTeleporter;
import lotr.common.world.map.LOTRAbstractWaypoint;
import lotr.common.world.map.LOTRCustomWaypoint;
import lotr.common.world.map.LOTRWaypoint;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import vinyarion.fukkit.main.util.FieldHandle;
import vinyarion.fukkit.main.util.memory.RTC;

public class FLOTRHooks {
	
	private static boolean lotr = false;
	private static Class lotrctc;
	private static Field tableBlock;
	private static Field tableFaction;
	private static Method codeName;
	private static Method ft;
	private static RTC.MW<Void> ftm = RTC.MW.ofTypes(LOTRPlayerData.class, new String[]{"fastTravelTo"}, LOTRAbstractWaypoint.class);
	
	static {
		try {
			lotrctc = Class.forName("lotr.common.inventory.LOTRContainerCraftingTable");
			tableBlock = lotrctc.getDeclaredField("tableBlock");
			tableFaction = Class.forName("lotr.common.block.LOTRBlockCraftingTable").getDeclaredField("tableFaction");
			codeName = Class.forName("lotr.common.fac.LOTRFaction").getDeclaredMethod("codeName", new Class[0]);
			ft = Class.forName("lotr.common.LOTRPlayerData").getDeclaredMethod("fastTravelTo", LOTRAbstractWaypoint.class);
			ft.setAccessible(true);
			lotr = true;
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getNameFromBench(Container container) {
		if(!(container instanceof ContainerWorkbench)) {
			if(container instanceof ContainerPlayer) {
				return "player";
			} else {
				return "none";
			}
		}
		ContainerWorkbench bench = (ContainerWorkbench)container;
		if(lotr) {
			try {
				if(lotrctc.isAssignableFrom(bench.getClass())) {
					String fn = (String)codeName.invoke(tableFaction.get(tableBlock.get(bench)), new Object[0]);
					return fn.toLowerCase().replace("_", "");
				} else {
					return "vanilla";
				}
			} catch(Exception e) {
				return "vanilla";
			}
		} else {
			return "vanilla";
		}
	}
	
	public static EntityPlayerMP playerFromData(LOTRPlayerData data) {
		for(Object o : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
			EntityPlayerMP p = (EntityPlayerMP)o;
			if(p.getGameProfile().getId().equals(data.getPlayerUUID())) {
				return p;
			}
		}
		return null;
	}

	public static void warpViaFT(EntityPlayerMP player, String name, double x, double y, double z, int dim) {
		warpViaFT(player, name, MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z), dim);
	}

	public static void warpViaFT(EntityPlayerMP player, String name, int x, int y, int z, int dim) {
		warpViaFT(player, name, x, y, z, dim, false);
	}
	private static void warpViaFT(EntityPlayerMP player, String name, int x, int y, int z, int dim, boolean override) {
		if(override || dim != player.dimension) {
			Teleporter tp = new LOTRTeleporter(DimensionManager.getWorld(dim), false);
			MinecraftServer.getServer().getConfigurationManager().transferPlayerToDimension(player, dim, tp);
			player.playerNetServerHandler.setPlayerLocation(x, y, z, 0, 0);
		} else if(dim == LOTRDimension.MIDDLE_EARTH.dimensionID) {
			LOTRCustomWaypoint wp = new LOTRCustomWaypoint(name, LOTRWaypoint.worldToMapX(x), LOTRWaypoint.worldToMapZ(z), x, y, z, -17);
			LOTRPlayerData pd = LOTRLevelData.getData(player);
			wp.setSharingPlayerID(pd.getPlayerUUID());
			pd.addOrUpdateSharedCustomWaypoint(wp);
			try {
				if(lotr) {
					ft.invoke(pd, wp);
				} else {
					warpViaFT(player, name, x, y, z, dim, true);
				}
//				TODO
//				ftm.with(pd).call(wp);
			} catch (Throwable e) {
				e.printStackTrace();
			}
			pd.removeSharedCustomWaypoint(wp);
		}
	}
	
	private static final Map<LOTRFaction, Integer> theMap = Maps.newHashMap();
	static {
		for(LOTRFaction fac : LOTRFaction.getPlayableAlignmentFactions()) {
			theMap.put(fac, Integer.valueOf(0));
		}
	}
	
	public static void alignmentSprite(EntityPlayerMP player, String display, float val, double posX, double posY, double posZ) {
		LOTRAlignmentValues.AlignmentBonus bonus = new LOTRAlignmentValues.AlignmentBonus(val, display);
		bonus.needsTranslation = false;
		LOTRPlayerData pd = LOTRLevelData.getData(player);
		LOTRFaction viewing = pd.getViewingFaction();
		LOTRAlignmentBonusMap map = new LOTRAlignmentBonusMap();
		for(LOTRFaction fac : LOTRFaction.getPlayableAlignmentFactions()) {
			map.put(fac, Float.valueOf(val));
		}
		if(viewing == null) {
			viewing = LOTRFaction.UNALIGNED;
		}
		alignmentSprite(player, bonus, viewing, pd.getAlignment(viewing), 0.0f, map, posX, posY, posZ);
	}
	
	public static void alignmentSprite(EntityPlayerMP player, LOTRAlignmentValues.AlignmentBonus source, LOTRFaction faction, float prevMainAlignment, float conq, LOTRAlignmentBonusMap factionMap, double posX, double posY, double posZ) {
		LOTRPacketHandler.networkWrapper.sendTo(new LOTRPacketAlignmentBonus(faction, prevMainAlignment, factionMap, conq, posX, posY, posZ, source), player);
	}
	
	public static class FactionNames {
		public static final String HOBBIT = "hobbit";
		public static final String RANGER_NORTH = "rangernorth";
		public static final String HIGH_ELF = "highelf";
		public static final String GUNDABAD = "gundabad";
		public static final String ANGMAR = "angmar";
		public static final String WOOD_ELF = "woodelf";
		public static final String DOL_GULDUR = "dolgoldur";
		public static final String DALE = "dale";
		public static final String DWARF = "dwarf";
		public static final String GALADHRIM = "galadrhim";
		public static final String DUNLAND = "dunland";
		public static final String URUK_HAI = "urukhai";
		public static final String FANGORN = "fangorn";
		public static final String ROHAN = "rohan";
		public static final String GONDOR = "gondor";
		public static final String MORDOR = "mordor";
		public static final String DORWINION = "dorwinion";
		public static final String NEAR_HARAD = "nearharad";
		public static final String MOREDAIN = "moredain";
		public static final String TAUREDAIN = "tauredain";
	}
	
}
