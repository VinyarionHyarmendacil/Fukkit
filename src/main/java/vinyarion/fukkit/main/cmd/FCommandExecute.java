package vinyarion.fukkit.main.cmd;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import vinyarion.fukkit.main.data.FPermissions;

public class FCommandExecute extends FCommand {
	
	public FCommandExecute(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <selector> <command>";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		Assert(args.length >= 2, "Not enough args!");
		FPermissions.isOverride.set(true);
		if(args[0].startsWith("@e")) {
			String cmd = "/" + args[1];
			for(int i = 2; i < args.length; i++) {
				cmd += " " + args[i];
			}
			for(Entity entity : FEntitySelector.getEntitiesFor(sender, args[0])) {
				MinecraftServer.getServer().getCommandManager().executeCommand(new EntityCommandSender(sender, entity), cmd);
			}
		} else {
			MinecraftServer.getServer().getCommandManager().executeCommand(getPlayer(sender, args[0]), rest(args, 1));
		}
		FPermissions.isOverride.remove();
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return args.length == 1 ? players() : null;
	}
	
}
