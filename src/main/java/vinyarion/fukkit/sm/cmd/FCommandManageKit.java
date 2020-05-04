package vinyarion.fukkit.sm.cmd;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import vinyarion.fukkit.main.cmd.FCommand;
import vinyarion.fukkit.main.data.FKits;
import vinyarion.fukkit.main.data.FRecipes;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FCommandManageKit extends FCommand {
	
	public FCommandManageKit(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <add|set|remove> <kitName> [<cooldown(hh:mm:ss)>]";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		Assert(sender instanceof EntityPlayerMP, "Must be a player!");
		EntityPlayerMP player = (EntityPlayerMP)sender;
		Assert(args.length >= 2, "Not enough arguments!");
		String op = args[0];
		String op2 = args[1];;
		if(op.equals("add") || op.equals("set")) {
			String op3 = args.length >= 3 ? args[2] : "00:01:00";
			if(FKits.instance().add(op2, player, op3)) {
				Info(sender, "Overwrote kit " + op2);
			} else {
				Info(sender, "Added kit " + op2);
			}
		} else if(op.equals("remove")) {
			if(FKits.instance().remove(op2)) {
				Info(sender, "Removed kit " + op2);
			} else {
				Info(sender, "Kit " + op2 + " does not exist!");
			}
		} else Invalid(op);
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return args.length == 1 ? new String[]{"add", "set", "remove"} : 
			args.length == 2 ? FKits.instance().kits() : null;
	}
	
}
