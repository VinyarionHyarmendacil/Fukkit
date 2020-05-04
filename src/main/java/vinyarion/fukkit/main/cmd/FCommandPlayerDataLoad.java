package vinyarion.fukkit.main.cmd;

import java.util.Map;
import java.util.UUID;

import cpw.mods.fml.relauncher.ReflectionHelper;
import lotr.common.LOTRLevelData;
import lotr.common.LOTRPlayerData;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import vinyarion.fukkit.main.playerdata.FPlayerData;

public class FCommandPlayerDataLoad extends FCommand {
	
	public FCommandPlayerDataLoad(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <fukkit|lotr|vanilla> <name|uuid>";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		Assert(args.length >= 2, "Not enough args!");
		String type = args[0];
		String player = args[1];
		EntityPlayerMP emp = playerFor(sender, player);
		Assert(emp != null, "Must specify a player!");
		if("fukkit".equals(type)) {
			FPlayerData pd = FPlayerData.forPlayer(emp);
			Assert(pd != null, "Data not found!");
			pd.load();
			Info(sender, "Player data for "+player+" loaded!");
		} else if("lotr".equals(type)) {
			Map<UUID, LOTRPlayerData> lotrmap = ReflectionHelper.getPrivateValue(LOTRLevelData.class, null, "playerDataMap");
			LOTRPlayerData lotrpd = lotrmap.remove(emp.getGameProfile().getId());
			LOTRLevelData.getData(emp);
			Info(sender, "Player data for "+player+" loaded!");
		} else if("vanilla".equals(type)) {
			Error("Unimplemented!"); // TODO : implement.
		} else Invalid(type);
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return args.length == 1 ? new String[]{"vanilla", "lotr", "fukkit"} : 
			args.length == 2 ? players() : 
			null;
	}
	
}
