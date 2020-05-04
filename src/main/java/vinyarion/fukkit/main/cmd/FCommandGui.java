package vinyarion.fukkit.main.cmd;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import vinyarion.fukkit.main.FNetHandlerPlayServer;
import vinyarion.fukkit.main.chestguis.ChestGui;
import vinyarion.fukkit.main.chestguis.InventoryChestHijacked;
import vinyarion.fukkit.main.data.FChestGuis;
import vinyarion.fukkit.main.playerdata.FPlayerData;

public class FCommandGui extends FCommand {
	
	public FCommandGui(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <display|reload> <name> [<player>]";
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return args.length == 1 ? new String[]{"display", "reload"} : 
			args.length == 2 ? FChestGuis.instance().names() : null;
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		Assert(args.length >= 2, "Not enough args!");
		String arg = args[0];
		String name = args[1];
		if(arg.equals("reload")) {
			FChestGuis.instance().reload(name);
		} else if(arg.equals("display")) {
			ChestGui cg =  FChestGuis.instance().get(name);
			EntityPlayerMP target;
			if(args.length >= 3) {
				target = playerFor(sender, args[2]);
			} else {
				Assert(sender instanceof EntityPlayerMP, "Must be a player! Else, specify someone!");
				target = (EntityPlayerMP)sender;
			}
			if(cg != null) {
				if(target != null) {
					Info(sender, "Displaying " + cg.name);
					FPlayerData.forPlayer(target).tagVolatile().setString(InventoryChestHijacked.NextFukkitGui, name);
				} else Info(sender, "No such player '" + args[2] + "'!");
			} else Info(sender, "No such gui '" + name + "'!");
		} else Invalid(arg);
	}
	
}
