package vinyarion.fukkit.main.cmd;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import vinyarion.fukkit.main.data.FRecipes;
import vinyarion.fukkit.main.data.FRegions;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FCommandRegionCyl extends FCommand {
	
	public FCommandRegionCyl(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <add|remove|display> <regionName> [<x> <z> <radius> <dimension> <perms>]";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		Assert(args.length >= 2, "Not enough arguments!");
		String op = args[0];
		String op2 = args[1];
		if(op.equals("add")) {
			Assert(args.length >= 7, "Not enough arguments!");
			NBTTagCompound region = new NBTTagCompound();
			int x = parseInt(sender, args[2]);
			int z = parseInt(sender, args[3]);
			int r = parseInt(sender, args[4]);
			int dim = parseInt(sender, args[5]);
			int pp = parseInt(sender, args[6]);
			region.setString("type", "cyl");
			region.setString("name", op2);
			region.setInteger("x", x);
			region.setInteger("z", z);
			region.setInteger("r", r);
			region.setInteger("dim", dim);
			region.setInteger("perms", pp);
			if(region != null) {
				FRegions.instance().submit(region);
				Info(sender, "Added region '" + op2 + "'.");
			}
		} else if(op.equals("remove")) {
			if(FRegions.instance().remove(op2)) {
				Info(sender, "Removed region '" + op2 + "'.");
			} else {
				Info(sender, "Region '" + op2 + "' does not exist!");
			}
		} else if(op.equals("display")) {
			NBTTagCompound region = FRegions.instance().retreive(op2);
			Info(sender, region.toString());
			Info(sender, Long.toBinaryString(region.getInteger("perms")));
		} else Invalid(op);
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return args.length == 1 ? new String[]{"add", "remove", "display"} : 
			args.length == 2 ? FRegions.instance().regions() : 
			args.length == 3 ? new String[]{String.valueOf(getLookPos(sender)[0])} : 
			args.length == 4 ? new String[]{String.valueOf(getLookPos(sender)[2])} : 
			args.length == 5 ? null : 
			sender.getEntityWorld() != null ? new String[]{String.valueOf(sender.getEntityWorld().provider.dimensionId)} : 
			null;
	}
	
}
