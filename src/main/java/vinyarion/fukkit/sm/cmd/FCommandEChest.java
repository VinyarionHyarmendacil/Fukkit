package vinyarion.fukkit.sm.cmd;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import vinyarion.fukkit.main.cmd.FCommand;

public class FCommandEChest extends FCommand {

	public FCommandEChest(String phiName) {
		super(phiName);
	}

	public String getCommandUsage(ICommandSender sender) {
		return "Usage: /" + this.getCommandName() + " [<player>]";
	}

	public void processCommand(ICommandSender sender, String[] args) {
		Assert(sender instanceof EntityPlayerMP, "Must be a player!");
		EntityPlayerMP player = (EntityPlayerMP)sender;
		EntityPlayerMP target = args.length == 0 ? player : getPlayer(sender, args[0]);
		Assert(target != null, "That is not a player");
		player.displayGUIChest(target.getInventoryEnderChest());
	}

	public Object tabComplete(ICommandSender sender, String[] args) {
		return players();
	}

}
