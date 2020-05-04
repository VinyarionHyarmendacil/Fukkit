package vinyarion.fukkit.sm.cmd;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import vinyarion.fukkit.main.cmd.FCommand;

public class FCommandInvsee extends FCommand {

	public FCommandInvsee(String phiName) {
		super(phiName);
	}

	public String getCommandUsage(ICommandSender sender) {
		return "Usage: /" + this.getCommandName() + " <player>";
	}

	public void processCommand(ICommandSender sender, String[] args) {
		Assert(sender instanceof EntityPlayerMP, "Must be a player!");
		Assert(args.length > 0, "Not enough args!");
		EntityPlayerMP player = (EntityPlayerMP)sender;
		EntityPlayerMP target = getPlayer(sender, args[0]);
		Assert(target != null, "That is not a player");
		player.displayGUIChest(target.inventory);
	}

	public Object tabComplete(ICommandSender sender, String[] args) {
		return players();
	}

}
