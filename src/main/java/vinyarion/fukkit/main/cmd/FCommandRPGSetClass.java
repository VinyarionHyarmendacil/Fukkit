package vinyarion.fukkit.main.cmd;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import vinyarion.fukkit.main.data.FServer;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.rpg.FPlayerDataRPG;
import vinyarion.fukkit.rpg.archetypes.FClass;
import vinyarion.fukkit.rpg.archetypes.FClasses;
import vinyarion.fukkit.rpg.archetypes.Race;

public class FCommandRPGSetClass extends FCommand {
	
	public FCommandRPGSetClass(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + this.getCommandName() + " <player> <class>";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		Assert(args.length >= 2, "Not enough args!");
		EntityPlayerMP target = playerFor(sender, args[0]);
		Assert(target != null, "Must specify a player!");
		FPlayerDataRPG ps = FPlayerDataRPG.of(target);
		FClass c = FClasses.get(args[1]);
		ps.stats.fclass = c;
		sender.addChatMessage(Colors.make(target.getCommandSenderName() + " is now class " + c.name + "."));
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return args.length == 1 ? players() : 
			args.length == 2 ? FClasses.names : 
			null;
	}
	
}
