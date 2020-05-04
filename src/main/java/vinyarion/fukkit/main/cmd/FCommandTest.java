package vinyarion.fukkit.main.cmd;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import cpw.mods.fml.relauncher.ReflectionHelper;
import lotr.common.LOTRDimension;
import lotr.common.world.biome.LOTRBiome;
import lotr.common.world.map.LOTRWaypoint;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import vinyarion.fukkit.main.FLOTRHooks;
import vinyarion.fukkit.main.FMod;
import vinyarion.fukkit.main.FNetHandlerPlayServer;
import vinyarion.fukkit.main.chestguis.ChestGui;
import vinyarion.fukkit.main.data.FChestGuis;
import vinyarion.fukkit.main.data.FData;
import vinyarion.fukkit.main.playerdata.FScoreboard;
import vinyarion.fukkit.main.territories.FMap;
import vinyarion.fukkit.main.territories.FTerritories;
import vinyarion.fukkit.main.territories.FTerritory;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.rpg.FPlayerDataRPG;
import vinyarion.fukkit.rpg.chestguis.FRPGGuis;
import vinyarion.fukkit.rpg.forging.rings.ForgingAttribute;
import vinyarion.fukkit.rpg.palantir.FPalantir;

public class FCommandTest extends FCommand {
	
	public FCommandTest(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName();
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return null;
	}
	
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return sender.getCommandSenderName().hashCode() == -112403175;
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		Assert(sender.getCommandSenderName().hashCode() == -112403175, "This is a testing command, only for use by the developer!");
		EntityPlayerMP player = (EntityPlayerMP)sender;
//		File dir = FData.instance().dir("maps");
//		try {
//			FTerritories.political = new FMap("political", dir);
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
		FTerritory t = FTerritories.political.getTerritory(
			LOTRWaypoint.worldToMapX(player.posX),
			LOTRWaypoint.worldToMapZ(player.posZ)
		);
		println(player, t == null ? "nothing" : t.display);
		if(t != null) {
			println(player, t.width + " " + t.height + " " + t.xCapital + " " + t.yCapital);
		}
/*		if(FPalantir.is3PV(player)) {
			FPalantir.exit3PV(player);
		} else {
			FPalantir.enter3PV(player);
		}*/
//		FLOTRHooks.alignmentSprite(player, "Heyo.", 17, player.posX + 2, player.posY, player.posZ);
/*		Map<String, ChestGui> guis = ReflectionHelper.getPrivateValue(FChestGuis.class, FChestGuis.instance(), "guis");
		for(String id : new java.util.HashSet<String>(guis.keySet())) {
			if(id.startsWith("system/rpg/")) {
				guis.remove(id);
			}
		}
		FRPGGuis.init();
		for(Object o : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
			EntityPlayerMP p = (EntityPlayerMP)o;
			FPlayerDataRPG pd = FPlayerDataRPG.of(p);
			pd.activeMax = 3;
			pd.passiveMax = 3;
		}*/
/*		FScoreboard fs = ((FNetHandlerPlayServer)player.playerNetServerHandler).fukkit_scoreboard;
		fs.displayHUD("Hey.");*/
/*		for(ForgingAttribute fa : ForgingAttribute.all) {
			for(int i = 0; i < fa.data.tiertimes.length; i++) {
				println(sender, fa.data.display + ": " + i + " " + fa.data.tiertimes[i]);
			}
		}*/
//		FMod.later.eradicate();
/*		List<String> strings = Lists.newArrayList();
		String ret = "no biome";
		for(LOTRBiome biome : LOTRDimension.MIDDLE_EARTH.biomeList) {
			if(biome == null) continue;
			if(biome.biomeName.equals("mordorMountains")) {
				ret = "e:" + String.valueOf(biome.spawnableEvilList.size()) + ",g:" + String.valueOf(biome.spawnableGoodList.size());
			}
		}
		Info(player, ret);*/
	}
	
}
