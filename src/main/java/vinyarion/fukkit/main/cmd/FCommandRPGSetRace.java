package vinyarion.fukkit.main.cmd;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import vinyarion.fukkit.main.data.FServer;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.rpg.FPlayerDataRPG;
import vinyarion.fukkit.rpg.archetypes.Race;

public class FCommandRPGSetRace extends FCommand {
	
	public FCommandRPGSetRace(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + this.getCommandName() + " <player> <race>";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		Assert(args.length >= 2, "Not enough args!");
		EntityPlayerMP target = playerFor(sender, args[0]);
		Assert(target != null, "Must specify a player!");
		FPlayerDataRPG ps = FPlayerDataRPG.of(target);
		Race r = Race.of(args[1]);
		ps.stats.race = r;
		sender.addChatMessage(Colors.make(target.getCommandSenderName() + " is now race " + r.name + "."));
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return args.length == 1 ? players() : 
			args.length == 2 ? Race.names : 
			null;
	}
	
}
